package uz.codebyz.message.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.codebyz.message.entity.MessageAttachment;

import java.util.List;
import java.util.UUID;

public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, UUID> {

    @Query("select a from MessageAttachment a where a.message.id = :messageId")
    List<MessageAttachment> findAllByMessageId(UUID messageId);
}
