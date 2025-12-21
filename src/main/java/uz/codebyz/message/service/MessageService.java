package uz.codebyz.message.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.message.*;
import uz.codebyz.message.entity.Chat;
import uz.codebyz.message.entity.Message;
import uz.codebyz.message.entity.MessageAttachment;
import uz.codebyz.message.entity.enums.ChatStatus;
import uz.codebyz.message.entity.enums.MessageStatus;
import uz.codebyz.message.entity.enums.MessageType;
import uz.codebyz.message.repo.*;
import uz.codebyz.message.ws.ChatEventPublisher;
import uz.codebyz.user.entity.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final MessageRepository messageRepository;
    private final MessageReactionRepository reactionRepository;
    private final MessageAttachmentRepository attachmentRepository;
    private final UserLiteRepository userLiteRepository;
    private final ChatEventPublisher eventPublisher;
    private final MessageStorageService storageService;

    public MessageService(ChatRepository chatRepository,
                          ChatMemberRepository chatMemberRepository,
                          MessageRepository messageRepository,
                          MessageReactionRepository reactionRepository,
                          MessageAttachmentRepository attachmentRepository,
                          UserLiteRepository userLiteRepository,
                          ChatEventPublisher eventPublisher,
                          MessageStorageService storageService) {
        this.chatRepository = chatRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.messageRepository = messageRepository;
        this.reactionRepository = reactionRepository;
        this.attachmentRepository = attachmentRepository;
        this.userLiteRepository = userLiteRepository;
        this.eventPublisher = eventPublisher;
        this.storageService = storageService;
    }

    @Transactional
    public ResponseDto<MessageResponse> sendMessage(UUID myUserId, SendMessageRequest req) {
        Chat chat = requireChatAndMembership(myUserId, req.getChatId());

        User sender = userLiteRepository.findById(myUserId)
                .orElseThrow(() -> new MessageException("Kullanıcı bulunamadı."));

        Message m = new Message();
        m.setChat(chat);
        m.setSender(sender);
        m.setType(MessageType.TEXT);
        m.setStatus(MessageStatus.ACTIVE);
        m.setContent(req.getContent().trim());
        m = messageRepository.save(m);

        chat.setLastMessageTime(Instant.now());
        chatRepository.save(chat);

        MessageResponse resp = enrich(MessageMapper.toResponse(m));
        eventPublisher.messageSent(chat.getId(), resp);

        return ResponseDto.ok("Mesaj gönderildi.", resp);
    }

    /**
     * FILE/PHOTO/VIDEO/MP3 gönderimi için.
     * content alanına istersen açıklama yazabilirsin (boş bırakılabilir).
     */
    @Transactional
    public ResponseDto<MessageResponse> sendFileMessage(UUID myUserId, UUID chatId, MessageType type, MultipartFile file, String caption) {
        if (type == null || type == MessageType.TEXT || type == MessageType.SYSTEM) {
            return ResponseDto.fail(400, null, "Dosya mesajı için type FILE/PHOTO/VIDEO/MP3 olmalı.");
        }
        if (file == null || file.isEmpty()) {
            return ResponseDto.fail(400, null, "Dosya boş olamaz.");
        }

        Chat chat = requireChatAndMembership(myUserId, chatId);

        User sender = userLiteRepository.findById(myUserId)
                .orElseThrow(() -> new MessageException("Kullanıcı bulunamadı."));

        Message m = new Message();
        m.setChat(chat);
        m.setSender(sender);
        m.setType(type);
        m.setStatus(MessageStatus.ACTIVE);
        m.setContent(caption != null ? caption.trim() : null);
        m = messageRepository.save(m);

        MessageStorageService.StoredFile stored = storageService.store(file);

        MessageAttachment a = new MessageAttachment();
        a.setMessage(m);
        a.setType(type);
        a.setFileUrl(stored.fileUrl);
        a.setOriginalName(stored.originalName);
        a.setFileSize(stored.size);
        attachmentRepository.save(a);

        chat.setLastMessageTime(Instant.now());
        chatRepository.save(chat);

        MessageResponse resp = enrich(MessageMapper.toResponse(m));
        eventPublisher.messageSent(chat.getId(), resp);

        return ResponseDto.ok("Dosya gönderildi.", resp);
    }

    @Transactional
    public ResponseDto<MessageResponse> editMessage(UUID myUserId, UUID messageId, EditMessageRequest req) {
        Message m = messageRepository.findById(messageId).orElseThrow(() -> new MessageException("Mesaj bulunamadı."));
        if (m.getStatus() == MessageStatus.DELETED) {
            return ResponseDto.fail(400, null, "Silinmiş mesaj düzenlenemez.");
        }
        if (m.getType() == MessageType.SYSTEM) {
            return ResponseDto.fail(403, null, "Sistem mesajı düzenlenemez.");
        }
        if (m.getSender() == null || !m.getSender().getId().equals(myUserId)) {
            return ResponseDto.fail(403, null, "Bu mesajı düzenleme yetkin yok.");
        }

        String newText = req.getContent().trim();
        if (newText.isEmpty()) {
            return ResponseDto.fail(400, null, "Mesaj boş olamaz.");
        }

        m.setContent(newText);
        m.setStatus(MessageStatus.EDITED);
        m = messageRepository.save(m);

        MessageResponse resp = enrich(MessageMapper.toResponse(m));
        eventPublisher.messageEdited(resp.getChatId(), resp);

        return ResponseDto.ok("Mesaj güncellendi.", resp);
    }

    @Transactional
    public ResponseDto<Void> deleteMessage(UUID myUserId, UUID messageId) {
        Message m = messageRepository.findById(messageId).orElseThrow(() -> new MessageException("Mesaj bulunamadı."));
        UUID chatId = m.getChat() != null ? m.getChat().getId() : null;

        if (m.getStatus() == MessageStatus.DELETED) {
            return ResponseDto.ok("Mesaj zaten silinmiş.");
        }
        if (m.getType() == MessageType.SYSTEM) {
            return ResponseDto.fail(403, null, "Sistem mesajı silinemez.");
        }
        if (m.getSender() == null || !m.getSender().getId().equals(myUserId)) {
            return ResponseDto.fail(403, null, "Bu mesajı silme yetkin yok.");
        }

        m.setStatus(MessageStatus.DELETED);
        m.setContent("[Silindi]");
        messageRepository.save(m);

        eventPublisher.messageDeleted(chatId, messageId);
        return ResponseDto.ok("Mesaj silindi.");
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<MessageResponse>> getChatMessages(UUID myUserId, UUID chatId, int page, int size) {
        boolean member = chatMemberRepository.findByChatIdAndUserId(chatId, myUserId).isPresent();
        if (!member) {
            return ResponseDto.fail(403, null, "Bu sohbeti görüntüleme yetkin yok.");
        }

        Page<Message> p = messageRepository.findByChatIdOrderByCreatedAtDesc(chatId, PageRequest.of(page, size));
        Page<MessageResponse> mapped = p.map(m -> enrich(MessageMapper.toResponse(m)));
        return ResponseDto.ok("Mesajlar getirildi.", mapped);
    }

    @Transactional(readOnly = true)
    public Message getMessageOrThrow(UUID messageId) {
        return messageRepository.findById(messageId).orElseThrow(() -> new MessageException("Mesaj bulunamadı."));
    }

    private Chat requireChatAndMembership(UUID myUserId, UUID chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new MessageException("Sohbet bulunamadı."));
        if (chat.getStatus() != ChatStatus.ACTIVE) {
            throw new MessageException("Sohbet aktif değil.");
        }
        boolean member = chatMemberRepository.findByChatIdAndUserId(chat.getId(), myUserId).isPresent();
        if (!member) {
            throw new MessageException("Bu sohbet için yetkin yok.");
        }
        return chat;
    }

    private MessageResponse enrich(MessageResponse r) {
        if (r == null || r.getId() == null) return r;

        r.setReactions(ReactionHelper.toCounts(reactionRepository.countByEmoji(r.getId())));

        List<MessageAttachment> atts = attachmentRepository.findAllByMessageId(r.getId());
        List<AttachmentResponse> list = new ArrayList<>();
        for (MessageAttachment a : atts) {
            AttachmentResponse ar = new AttachmentResponse();
            ar.setId(a.getId());
            ar.setType(a.getType());
            ar.setFileUrl(a.getFileUrl());
            ar.setOriginalName(a.getOriginalName());
            ar.setFileSize(a.getFileSize());
            list.add(ar);
        }
        r.setAttachments(list);
        return r;
    }
}
