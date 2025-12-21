package uz.codebyz.message.dto.message;

import uz.codebyz.message.entity.enums.MessageStatus;
import uz.codebyz.message.entity.enums.MessageType;

import java.time.Instant;
import java.util.Map;
import java.util.List;
import java.util.UUID;

public class MessageResponse {
    private UUID id;
    private UUID chatId;
    private UUID senderId;
    private String senderFullName;
    private MessageType type;
    private MessageStatus status;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;

    private List<AttachmentResponse> attachments;

    // emoji -> count
    private Map<String, Long> reactions;

    public MessageResponse() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getChatId() { return chatId; }
    public void setChatId(UUID chatId) { this.chatId = chatId; }

    public UUID getSenderId() { return senderId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }

    public String getSenderFullName() { return senderFullName; }
    public void setSenderFullName(String senderFullName) { this.senderFullName = senderFullName; }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public MessageStatus getStatus() { return status; }
    public void setStatus(MessageStatus status) { this.status = status; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public List<AttachmentResponse> getAttachments() { return attachments; }
    public void setAttachments(List<AttachmentResponse> attachments) { this.attachments = attachments; }

    public Map<String, Long> getReactions() { return reactions; }
    public void setReactions(Map<String, Long> reactions) { this.reactions = reactions; }
}
