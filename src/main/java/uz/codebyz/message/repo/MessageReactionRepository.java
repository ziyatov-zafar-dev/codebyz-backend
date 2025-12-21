package uz.codebyz.message.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.codebyz.message.entity.MessageReaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageReactionRepository extends JpaRepository<MessageReaction, UUID> {

    Optional<MessageReaction> findByMessageIdAndUserIdAndEmoji(UUID messageId, UUID userId, String emoji);

    @Query("select mr.emoji as emoji, count(mr) as cnt from MessageReaction mr where mr.message.id = :messageId group by mr.emoji")
    List<Object[]> countByEmoji(UUID messageId);

    @Query("select mr from MessageReaction mr where mr.message.id = :messageId")
    List<MessageReaction> findAllByMessageId(UUID messageId);
}
