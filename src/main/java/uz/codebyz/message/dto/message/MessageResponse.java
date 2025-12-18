package uz.codebyz.message.dto.message;

import uz.codebyz.common.dto.user.UserResponse;
import uz.codebyz.message.entity.Chat;
import uz.codebyz.message.entity.Message;
import uz.codebyz.message.entity.enums.MessageStatus;
import uz.codebyz.message.entity.enums.MessageType;
import uz.codebyz.user.entity.User;

import java.time.Instant;
import java.util.UUID;

public class MessageResponse {
    private UUID id;
    private UserResponse sender;
    private UUID chatId;
    private MessageType type;
    private String content;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private MessageStatus status;
    private MessageResponse replyTo;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean edited;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserResponse getSender() {
        return sender;
    }

    public void setSender(UserResponse sender) {
        this.sender = sender;
    }

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public MessageResponse getReplyTo() {
        return replyTo;
    }



    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getEdited() {
        return edited;
    }

    public void setReplyTo(MessageResponse replyTo) {
        this.replyTo = replyTo;
    }

    public void setEdited(Boolean edited) {
        this.edited = edited;
    }
}
