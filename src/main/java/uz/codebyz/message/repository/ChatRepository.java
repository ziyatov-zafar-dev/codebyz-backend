package uz.codebyz.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.message.entity.Chat;
import uz.codebyz.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    @Query("""
                select ch
                from Chat ch
                where
                    (
                        (ch.user1.id = :u1 and ch.user2.id = :u2)
                        or
                        (ch.user1.id = :u2 and ch.user2.id = :u1)
                    )
                    and ch.status = uz.codebyz.message.entity.enums.ChatStatus.ACTIVE
                                order by ch.createdAt asc
            """)
    Optional<Chat> findActiveChatBetweenUsers(
            @Param("u1") UUID user1Id,
            @Param("u2") UUID user2Id
    );

    @Query("""
                select ch
                from Chat ch
                where
                    (
                        (ch.user1.id = :u1 and ch.user2.id = :u2)
                        or
                        (ch.user1.id = :u2 and ch.user2.id = :u1)
                    )
            """)
    Optional<Chat> findByChat(
            @Param("u1") UUID user1Id,
            @Param("u2") UUID user2Id
    );

    @Query("select ch from Chat ch where (ch.id=:id and ch.status=uz.codebyz.message.entity.enums.ChatStatus.ACTIVE)")
    Optional<Chat> findByIdAndStatus(@Param("id") UUID chatId);

    @Query("""
                select ch
                from Chat ch
                where
                    ch.status = uz.codebyz.message.entity.enums.ChatStatus.ACTIVE
                    and (ch.user1.id = :userId or ch.user2.id = :userId)
                order by ch.lastMessageTime desc nulls last
            """)
    List<Chat> findAllActiveChatsByUser(@Param("userId") UUID userId);











    Optional<Chat> findByUser1AndUser2(User user1, User user2);

    Optional<Chat> findByUser2AndUser1(User user2, User user1);

    @Query("select ch from Chat ch where ch.id=:id and ch.status=uz.codebyz.message.entity.enums.ChatStatus.ACTIVE")
    Optional<Chat> findByChatId(@Param("id") UUID chatid);
}
