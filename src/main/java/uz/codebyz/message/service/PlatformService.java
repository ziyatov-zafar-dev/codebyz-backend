package uz.codebyz.message.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.message.MessageResponse;
import uz.codebyz.message.dto.platform.PlatformAnnouncementRequest;
import uz.codebyz.message.entity.Chat;
import uz.codebyz.message.entity.Message;
import uz.codebyz.message.entity.enums.MessageStatus;
import uz.codebyz.message.entity.enums.MessageType;
import uz.codebyz.message.repo.MessageReactionRepository;
import uz.codebyz.message.repo.MessageRepository;
import uz.codebyz.message.repo.UserLiteRepository;
import uz.codebyz.message.ws.ChatEventPublisher;
import uz.codebyz.user.entity.User;
import uz.codebyz.user.entity.UserRole;

import java.time.Instant;

@Service
public class PlatformService {

    private final ChatService chatService;
    private final MessageRepository messageRepository;
    private final MessageReactionRepository reactionRepository;
    private final UserLiteRepository userLiteRepository;
    private final ChatEventPublisher eventPublisher;

    public PlatformService(ChatService chatService,
                           MessageRepository messageRepository,
                           MessageReactionRepository reactionRepository,
                           UserLiteRepository userLiteRepository,
                           ChatEventPublisher eventPublisher) {
        this.chatService = chatService;
        this.messageRepository = messageRepository;
        this.reactionRepository = reactionRepository;
        this.userLiteRepository = userLiteRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ResponseDto<MessageResponse> announce(java.util.UUID adminUserId, UserRole adminRole, PlatformAnnouncementRequest req) {
        if (adminRole != UserRole.ADMIN) {
            return ResponseDto.fail(403, null, "Sadece ADMIN platform mesajı gönderebilir.");
        }

        Chat platformChat = chatService.ensurePlatformChat();

        User admin = userLiteRepository.findById(adminUserId).orElseThrow(() -> new MessageException("Kullanıcı bulunamadı."));

        Message m = new Message();
        m.setChat(platformChat);
        m.setSender(admin);
        m.setType(MessageType.TEXT);
        m.setStatus(MessageStatus.ACTIVE);
        m.setContent(req.getContent().trim());
        m = messageRepository.save(m);

        platformChat.setLastMessageTime(Instant.now());

        MessageResponse resp = MessageMapper.toResponse(m);
        resp.setReactions(ReactionHelper.toCounts(reactionRepository.countByEmoji(m.getId())));
        eventPublisher.messageSent(platformChat.getId(), resp);

        return ResponseDto.ok("Platform mesajı gönderildi.", resp);
    }
}
