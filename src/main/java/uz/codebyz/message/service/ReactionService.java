package uz.codebyz.message.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.message.ReactionSummaryResponse;
import uz.codebyz.message.entity.Message;
import uz.codebyz.message.entity.MessageReaction;
import uz.codebyz.message.entity.enums.MessageStatus;
import uz.codebyz.message.repo.ChatMemberRepository;
import uz.codebyz.message.repo.MessageReactionRepository;
import uz.codebyz.message.repo.UserLiteRepository;
import uz.codebyz.message.ws.ChatEventPublisher;
import uz.codebyz.user.entity.User;

import java.util.Map;
import java.util.UUID;

@Service
public class ReactionService {

    private final MessageService messageService;
    private final MessageReactionRepository reactionRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final UserLiteRepository userLiteRepository;
    private final ChatEventPublisher eventPublisher;

    public ReactionService(MessageService messageService,
                           MessageReactionRepository reactionRepository,
                           ChatMemberRepository chatMemberRepository,
                           UserLiteRepository userLiteRepository,
                           ChatEventPublisher eventPublisher) {
        this.messageService = messageService;
        this.reactionRepository = reactionRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.userLiteRepository = userLiteRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ResponseDto<ReactionSummaryResponse> addReaction(UUID myUserId, UUID messageId, String emoji) {
        if (emoji == null || emoji.trim().isEmpty()) {
            return ResponseDto.fail(400, null, "Emoji boş olamaz.");
        }

        Message m = messageService.getMessageOrThrow(messageId);
        if (m.getStatus() == MessageStatus.DELETED) {
            return ResponseDto.fail(400, null, "Silinmiş mesaja reaksiyon eklenemez.");
        }

        UUID chatId = m.getChat().getId();

        chatMemberRepository.findByChatIdAndUserId(chatId, myUserId)
                .orElseThrow(() -> new MessageException("Bu sohbete erişimin yok."));

        if (reactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, myUserId, emoji).isPresent()) {
            // idempotent
            return ResponseDto.ok("Reaksiyon zaten mevcut.", getSummary(messageId));
        }

        User u = userLiteRepository.findById(myUserId).orElseThrow(() -> new MessageException("Kullanıcı bulunamadı."));

        MessageReaction r = new MessageReaction();
        r.setMessage(m);
        r.setUser(u);
        r.setEmoji(emoji);
        reactionRepository.save(r);

        eventPublisher.reactionAdded(chatId, messageId, emoji);
        return ResponseDto.ok("Reaksiyon eklendi.", getSummary(messageId));
    }

    @Transactional
    public ResponseDto<ReactionSummaryResponse> removeReaction(UUID myUserId, UUID messageId, String emoji) {
        Message m = messageService.getMessageOrThrow(messageId);
        UUID chatId = m.getChat().getId();

        chatMemberRepository.findByChatIdAndUserId(chatId, myUserId)
                .orElseThrow(() -> new MessageException("Bu sohbete erişimin yok."));

        MessageReaction r = reactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, myUserId, emoji).orElse(null);
        if (r != null) {
            reactionRepository.delete(r);
            eventPublisher.reactionRemoved(chatId, messageId, emoji);
        }
        return ResponseDto.ok("Reaksiyon kaldırıldı.", getSummary(messageId));
    }

    @Transactional(readOnly = true)
    public ResponseDto<ReactionSummaryResponse> getReactionSummary(UUID myUserId, UUID messageId) {
        Message m = messageService.getMessageOrThrow(messageId);
        UUID chatId = m.getChat().getId();
        chatMemberRepository.findByChatIdAndUserId(chatId, myUserId)
                .orElseThrow(() -> new MessageException("Bu sohbete erişimin yok."));
        return ResponseDto.ok("Reaksiyonlar getirildi.", getSummary(messageId));
    }

    private ReactionSummaryResponse getSummary(UUID messageId) {
        Map<String, Long> counts = ReactionHelper.toCounts(reactionRepository.countByEmoji(messageId));
        return new ReactionSummaryResponse(messageId, counts);
    }
}
