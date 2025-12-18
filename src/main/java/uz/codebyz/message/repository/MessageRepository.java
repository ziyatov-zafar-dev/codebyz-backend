package uz.codebyz.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.message.entity.Message;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Query("select m from Message m where (m.id=:message_id and m.deleted=false)")
    Optional<Message> findByIdAndDeleteIsFalse(@Param("message_id") UUID lastMessageId);
}
