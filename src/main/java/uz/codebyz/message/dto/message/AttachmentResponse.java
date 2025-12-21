package uz.codebyz.message.dto.message;

import uz.codebyz.message.entity.enums.MessageType;

import java.util.UUID;

public class AttachmentResponse {
    private UUID id;
    private MessageType type;
    private String fileUrl;
    private String originalName;
    private long fileSize;

    public AttachmentResponse() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
}
