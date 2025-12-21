package uz.codebyz.message.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.codebyz.message.entity.Chat;
import uz.codebyz.message.entity.enums.ChatStatus;
import uz.codebyz.message.entity.enums.ChatType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {

    Optional<Chat> findByTypeAndPlatformKey(ChatType type, String platformKey);

    Optional<Chat> findByTypeAndUser1IdAndUser2IdAndStatus(ChatType type, UUID user1Id, UUID user2Id, ChatStatus status);

    @Query("select c from Chat c where c.id in (select cm.chat.id from ChatMember cm where cm.user.id = :userId and cm.active = true) and c.status = 'ACTIVE' order by c.lastMessageTime desc nulls last, c.createdAt desc")
    List<Chat> findMyActiveChats(UUID userId);
}
