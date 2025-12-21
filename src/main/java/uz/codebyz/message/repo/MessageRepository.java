package uz.codebyz.message.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.codebyz.message.entity.Message;

import java.time.Instant;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Page<Message> findByChatIdOrderByCreatedAtDesc(UUID chatId, Pageable pageable);

    @Query("""
        select count(m) 
        from Message m
        where m.chat.id = :chatId
          and m.status <> uz.codebyz.message.entity.enums.MessageStatus.DELETED
          and m.createdAt > :lastReadAt
          and (m.sender is not null and m.sender.id <> :userId)
    """)
    long countUnread(UUID chatId, Instant lastReadAt, UUID userId);
}
