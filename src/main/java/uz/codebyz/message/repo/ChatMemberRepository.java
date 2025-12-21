package uz.codebyz.message.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.codebyz.message.entity.ChatMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatMemberRepository extends JpaRepository<ChatMember, UUID> {

    Optional<ChatMember> findByChatIdAndUserId(UUID chatId, UUID userId);

    @Query("select cm.user.id from ChatMember cm where cm.chat.id = :chatId and cm.active = true")
    List<UUID> findMemberIds(UUID chatId);
}
