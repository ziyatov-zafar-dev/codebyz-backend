package uz.codebyz.message.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.chat.ChatResponse;
import uz.codebyz.message.dto.chat.UnreadSummaryResponse;
import uz.codebyz.message.entity.Chat;
import uz.codebyz.message.entity.ChatMember;
import uz.codebyz.message.entity.Message;
import uz.codebyz.message.entity.enums.ChatStatus;
import uz.codebyz.message.entity.enums.ChatType;
import uz.codebyz.message.entity.enums.MessageType;
import uz.codebyz.message.repo.ChatMemberRepository;
import uz.codebyz.message.repo.ChatRepository;
import uz.codebyz.message.repo.MessageRepository;
import uz.codebyz.message.repo.UserLiteRepository;
import uz.codebyz.message.ws.ChatEventPublisher;
import uz.codebyz.user.entity.User;

import java.time.Instant;
import java.util.*;

@Service
public class ChatService {

    public static final String PLATFORM_MAIN_KEY = "PLATFORM_MAIN";

    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final MessageRepository messageRepository;
    private final UserLiteRepository userLiteRepository;
    private final ChatEventPublisher eventPublisher;

    public ChatService(ChatRepository chatRepository,
                       ChatMemberRepository chatMemberRepository,
                       MessageRepository messageRepository,
                       UserLiteRepository userLiteRepository,
                       ChatEventPublisher eventPublisher) {
        this.chatRepository = chatRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.messageRepository = messageRepository;
        this.userLiteRepository = userLiteRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ResponseDto<ChatResponse> createOrGetPrivateChat(UUID myUserId, UUID otherUserId) {
        if (myUserId == null || otherUserId == null) {
            return ResponseDto.fail(400, null, "Kullanıcı bilgisi eksik.");
        }
        if (myUserId.equals(otherUserId)) {
            return ResponseDto.fail(400, null, "Kendi kendinle sohbet oluşturamazsın.");
        }

        UUID u1 = myUserId.compareTo(otherUserId) < 0 ? myUserId : otherUserId;
        UUID u2 = myUserId.compareTo(otherUserId) < 0 ? otherUserId : myUserId;

        Chat chat = chatRepository
                .findByTypeAndUser1IdAndUser2IdAndStatus(ChatType.PRIVATE, u1, u2, ChatStatus.ACTIVE)
                .orElse(null);

        if (chat == null) {
            chat = new Chat();
            chat.setType(ChatType.PRIVATE);
            chat.setStatus(ChatStatus.ACTIVE);
            chat.setUser1Id(u1);
            chat.setUser2Id(u2);
            chat = chatRepository.save(chat);

            User me = userLiteRepository.findById(myUserId).orElseThrow(() -> new MessageException("Kullanıcı bulunamadı."));
            User other = userLiteRepository.findById(otherUserId).orElseThrow(() -> new MessageException("Kullanıcı bulunamadı."));

            ChatMember cm1 = new ChatMember();
            cm1.setChat(chat);
            cm1.setUser(me);
            chatMemberRepository.save(cm1);

            ChatMember cm2 = new ChatMember();
            cm2.setChat(chat);
            cm2.setUser(other);
            chatMemberRepository.save(cm2);

            Message sys = createSystemMessage(chat, "Sohbet oluşturuldu.");
            eventPublisher.system(chat.getId(), MessageMapper.toResponse(sys));
        }

        List<UUID> members = chatMemberRepository.findMemberIds(chat.getId());
        long unread = getUnreadCountForChat(myUserId, chat.getId());
        ChatResponse res = new ChatResponse(chat.getId(), chat.getType(), chat.getStatus(), members, chat.getLastMessageTime(), unread, chat.getPinnedMessageId());
        return ResponseDto.ok("Sohbet hazır.", res);
    }

    @Transactional
    public Chat ensurePlatformChat() {
        Chat c = chatRepository.findByTypeAndPlatformKey(ChatType.PLATFORM, PLATFORM_MAIN_KEY).orElse(null);
        if (c != null && c.getStatus() == ChatStatus.ACTIVE) return c;

        Chat chat = new Chat();
        chat.setType(ChatType.PLATFORM);
        chat.setStatus(ChatStatus.ACTIVE);
        chat.setPlatformKey(PLATFORM_MAIN_KEY);
        chat = chatRepository.save(chat);

        List<User> users = userLiteRepository.findAllActiveVerified();
        for (User u : users) {
            ChatMember cm = new ChatMember();
            cm.setChat(chat);
            cm.setUser(u);
            chatMemberRepository.save(cm);
        }

        Message sys = createSystemMessage(chat, "Platform sohbeti oluşturuldu.");
        eventPublisher.system(chat.getId(), MessageMapper.toResponse(sys));
        return chat;
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<ChatResponse>> getMyChats(UUID myUserId) {
        List<Chat> chats = chatRepository.findMyActiveChats(myUserId);
        List<ChatResponse> out = new ArrayList<>();
        for (Chat c : chats) {
            List<UUID> members = chatMemberRepository.findMemberIds(c.getId());
            long unread = getUnreadCountForChat(myUserId, c.getId());
            out.add(new ChatResponse(c.getId(), c.getType(), c.getStatus(), members, c.getLastMessageTime(), unread, c.getPinnedMessageId()));
        }
        return ResponseDto.ok("Sohbetler getirildi.", out);
    }

    @Transactional(readOnly = true)
    public ResponseDto<UnreadSummaryResponse> getUnreadSummary(UUID myUserId) {
        List<Chat> chats = chatRepository.findMyActiveChats(myUserId);
        long unreadChats = 0;
        long unreadMessages = 0;
        for (Chat c : chats) {
            long unread = getUnreadCountForChat(myUserId, c.getId());
            if (unread > 0) unreadChats++;
            unreadMessages += unread;
        }
        return ResponseDto.ok("Okunmamış özet getirildi.", new UnreadSummaryResponse(unreadChats, unreadMessages));
    }

    @Transactional
    public ResponseDto<Void> deleteChat(UUID myUserId, UUID chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new MessageException("Sohbet bulunamadı."));
        boolean member = chatMemberRepository.findByChatIdAndUserId(chatId, myUserId).isPresent();
        if (!member) {
            return ResponseDto.fail(403, null, "Bu sohbeti silme yetkin yok.");
        }
        chat.setStatus(ChatStatus.DELETED);
        chatRepository.save(chat);

        Message sys = createSystemMessage(chat, "Sohbet silindi.");
        eventPublisher.system(chat.getId(), MessageMapper.toResponse(sys));
        eventPublisher.chatDeleted(chat.getId());

        return ResponseDto.ok("Sohbet silindi.");
    }

    @Transactional
    public ResponseDto<Void> markChatRead(UUID myUserId, UUID chatId) {
        ChatMember cm = chatMemberRepository.findByChatIdAndUserId(chatId, myUserId)
                .orElseThrow(() -> new MessageException("Bu sohbete erişimin yok."));
        cm.setLastReadAt(Instant.now());
        chatMemberRepository.save(cm);

        eventPublisher.chatRead(chatId);
        return ResponseDto.ok("Sohbet okundu olarak işaretlendi.");
    }

    @Transactional
    public ResponseDto<Void> pinMessage(UUID myUserId, UUID chatId, UUID messageId) {
        // üye kontrol
        chatMemberRepository.findByChatIdAndUserId(chatId, myUserId)
                .orElseThrow(() -> new MessageException("Bu sohbete erişimin yok."));
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new MessageException("Sohbet bulunamadı."));
        chat.setPinnedMessageId(messageId);
        chatRepository.save(chat);

        eventPublisher.pinned(chatId, messageId);
        return ResponseDto.ok("Mesaj sabitlendi.");
    }

    @Transactional
    public ResponseDto<Void> unpinMessage(UUID myUserId, UUID chatId) {
        chatMemberRepository.findByChatIdAndUserId(chatId, myUserId)
                .orElseThrow(() -> new MessageException("Bu sohbete erişimin yok."));
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new MessageException("Sohbet bulunamadı."));
        chat.setPinnedMessageId(null);
        chatRepository.save(chat);

        eventPublisher.unpinned(chatId);
        return ResponseDto.ok("Sabit kaldırıldı.");
    }

    @Transactional
    public Message createSystemMessage(Chat chat, String text) {
        Message m = new Message();
        m.setChat(chat);
        m.setSender(null);
        m.setType(MessageType.SYSTEM);
        m.setContent(text);
        m = messageRepository.save(m);

        chat.setLastMessageTime(Instant.now());
        chatRepository.save(chat);
        return m;
    }

    private long getUnreadCountForChat(UUID myUserId, UUID chatId) {
        ChatMember cm = chatMemberRepository.findByChatIdAndUserId(chatId, myUserId).orElse(null);
        Instant lastReadAt = (cm == null || cm.getLastReadAt() == null) ? Instant.EPOCH : cm.getLastReadAt();
        return messageRepository.countUnread(chatId, lastReadAt, myUserId);
    }
}
