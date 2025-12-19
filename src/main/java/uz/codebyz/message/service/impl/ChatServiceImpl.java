package uz.codebyz.message.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.codebyz.common.ErrorCode;
import uz.codebyz.common.Helper;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.chat.ChatResponse;
import uz.codebyz.message.entity.Chat;
import uz.codebyz.message.entity.Message;
import uz.codebyz.message.entity.enums.ChatStatus;
import uz.codebyz.message.mapper.ChatMapper;
import uz.codebyz.message.repository.ChatRepository;
import uz.codebyz.message.repository.MessageRepository;
import uz.codebyz.message.service.ChatService;
import uz.codebyz.message.service.MessageService;
import uz.codebyz.user.entity.User;
import uz.codebyz.user.repo.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatServiceImpl implements ChatService {
    private final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);
    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageService messageService;

    public ChatServiceImpl(ChatRepository chatRepository, ChatMapper chatMapper, MessageRepository messageRepository, UserRepository userRepository, MessageService messageService) {
        this.chatRepository = chatRepository;
        this.chatMapper = chatMapper;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageService = messageService;
    }

    private Message getLastMessage(UUID messageId) {
        Message message;
        if (messageId == null) message = null;
        else {
            Optional<Message> mOp = messageRepository.findByIdAndDeleteIsFalse(messageId);
            message = mOp.orElse(null);
        }
        return message;
    }

    @Override
    public ResponseDto<ChatResponse> existsChat(UUID user1Id, UUID user2Id) {
        Optional<Chat> chatOptional = chatRepository.findActiveChatBetweenUsers(user1Id, user2Id);
        if (chatOptional.isEmpty()) {
            log.info(
                    "Chat not found between users | user1Id={}, user2Id={}",
                    user1Id, user2Id
            );
            return ResponseDto.fail(
                    404,
                    ErrorCode.CHAT_NOT_FOUND,
                    "Foydalanuvchilar o‘rtasida chat mavjud emas"
            );
        }

        Chat chat = chatOptional.get();
        return ResponseDto.ok("Success", chatMapper.toDto(chat, getLastMessage(chat.getLastMessageId())));
    }

    @Override
    public ResponseDto<ChatResponse> getChatByUser1IdAndUser2Id(UUID user1Id, UUID user2Id) {
        return existsChat(user1Id, user2Id);
    }

    @Override
    public ResponseDto<ChatResponse> getChat(UUID chatId) {

        Optional<Chat> chatOptional =
                chatRepository.findByIdAndStatus(chatId);

        if (chatOptional.isEmpty()) {
            log.error("Chat not found | chatId={}", chatId);
            return ResponseDto.fail(
                    404,
                    ErrorCode.CHAT_NOT_FOUND,
                    "Chat topilmadi"
            );
        }

        Chat chat = chatOptional.get();
        return ResponseDto.ok(
                "Chat topildi",
                chatMapper.toDto(chat, getLastMessage(chat.getLastMessageId()))
        );
    }

    @Override
    public ResponseDto<ChatResponse> createChat(UUID user1Id, UUID user2Id) throws Exception {

        if (user1Id.equals(user2Id)) {
            return ResponseDto.fail(
                    400,
                    ErrorCode.INVALID_REQUEST,
                    "Kendinle konuşamazsın."
            );
        }

        // 🔹 UUID tartiblash (duplicate chat oldini olish)
        UUID first = user1Id.compareTo(user2Id) < 0 ? user1Id : user2Id;
        UUID second = user1Id.compareTo(user2Id) < 0 ? user2Id : user1Id;

        // 🔹 Chat mavjudligini tekshirish
        Optional<Chat> chatOp = chatRepository.findByChat(first, second);
        if (chatOp.isPresent() && chatOp.get().getStatus() == ChatStatus.ACTIVE) {
            log.warn("Chat already exists | user1Id={}, user2Id={}", user1Id, user2Id);
            return ResponseDto.fail(
                    409,
                    ErrorCode.CHAT_ALREADY_EXISTS,
                    "Bu kullanıcılar arasında zaten bir sohbet mevcut."
            );
        }


        // 🔹 Userlarni tekshirish
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new Exception("User not found: " + user1Id));

        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new Exception("User not found: " + user2Id));

        // 🔹 Chat yaratish
        Chat chat;
        chat = chatOp.orElseGet(Chat::new);
        chat.setUser1(first.equals(user1Id) ? user1 : user2);
        chat.setUser2(first.equals(user1Id) ? user2 : user1);
        chat.setStatus(ChatStatus.ACTIVE);
        chat.setLastMessageId(null);
        chat.setLastMessageTime(null);
        chat.setCreatedAt(Helper.currentTimeInstant());
        chat.setUpdatedAt(Helper.currentTimeInstant());
        chat.setMessages(new ArrayList<>());
        Chat savedChat = chatRepository.save(chat);

        log.info(
                "Chat created successfully | chatId={}, user1Id={}, user2Id={}",
                savedChat.getId(), user1Id, user2Id
        );

        return ResponseDto.ok(
                "Sohbet başarıyla oluşturuldu.",
                chatMapper.toDto(savedChat, null)
        );
    }

    @Override
    public ResponseDto<Void> deleteChat(UUID chatId) {

        Optional<Chat> chatOptional =
                chatRepository.findByIdAndStatus(chatId);
        if (chatOptional.isEmpty()) {
            log.info("Delete chat failed | chat not found | chatId={}", chatId);
            return ResponseDto.fail(
                    404,
                    ErrorCode.CHAT_NOT_FOUND,
                    "Sohbet bulunamadı"
            );
        }

        Chat chat = chatOptional.get();
        for (Message message : chat.getMessages().stream().filter(message -> !message.isDeleted()).toList()) {
            messageService.deleteMessage(message.getId());
        }
        chat.setStatus(ChatStatus.DELETE);
        chatRepository.save(chat);

        log.info("Chat deleted successfully | chatId={}", chatId);

        return ResponseDto.ok("Sohbet başarıyla silindi");
    }

    @Override
    public ResponseDto<List<ChatResponse>> geyMyChats(UUID userid) {

        // 🔹 User mavjudligini tekshirish (ixtiyoriy, lekin tavsiya)
        if (!userRepository.existsById(userid)) {
            return ResponseDto.fail(
                    404,
                    ErrorCode.USER_NOT_FOUND,
                    "Kullanıcı bulunamadı"
            );
        }

        // 🔹 User ishtirok etgan barcha ACTIVE chatlarni olish
        List<Chat> chats = chatRepository
                .findAllActiveChatsByUser(userid);

        if (chats.isEmpty()) {
            return ResponseDto.ok(
                    "Sohbet özelliği kullanılamıyor.", List.of()
            );
        }

        // 🔹 Chatlarni ChatResponse ga map qilish
        List<ChatResponse> responses = chats.stream()
                .map(chat -> {
                    return chatMapper.toDto(chat, getLastMessage(chat.getLastMessageId()));
                })
                .toList();

        return ResponseDto.ok(
                "Sohbetler başarıyla alındı", responses
        );
    }
}
