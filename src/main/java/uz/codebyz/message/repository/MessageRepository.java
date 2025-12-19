package uz.codebyz.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.message.entity.Message;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Query("select m from Message m where (m.id=:message_id and m.deleted=false)")
    Optional<Message> findByIdAndDeleteIsFalse(@Param("message_id") UUID lastMessageId);

    @Modifying
    @Query("""
                update Message m
                   set m.status = uz.codebyz.message.entity.enums.MessageStatus.READ,
                       m.updatedAt = :now
                 where m.chat.id = :chatId
                   and m.status = uz.codebyz.message.entity.enums.MessageStatus.SENT
            """)
    int markChatMessagesAsRead(
            @Param("chatId") UUID chatId,
            @Param("now") Instant now
    );

    @Modifying
    @Query("""
                update Message m
                   set m.status = uz.codebyz.message.entity.enums.MessageStatus.READ,
                       m.updatedAt = :now
                 where m.chat.id in (
                       select c.id from Chat c
                       where (c.user1.id = :userId or c.user2.id = :userId)
                         and c.status = uz.codebyz.message.entity.enums.ChatStatus.ACTIVE
                 )
                   and m.status = uz.codebyz.message.entity.enums.MessageStatus.SENT
                   and m.sender.id <> :userId
            """)
    int markAllChatsMessagesAsRead(
            @Param("userId") UUID userId,
            @Param("now") Instant now
    );


}
