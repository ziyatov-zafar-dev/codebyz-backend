package uz.codebyz.message.dto.chat;

import uz.codebyz.common.dto.user.UserResponse;
import uz.codebyz.message.dto.message.MessageResponse;
import uz.codebyz.message.entity.Chat;
import uz.codebyz.user.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChatResponse {
    private UUID id;
    private UserResponse user1;
    private UserResponse user2;
    private MessageResponse lastMessage;
    private Instant lastMessageTime;
    private Instant createdAt;
    private Instant updatedAt;
    private List<MessageResponse> messages;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserResponse getUser1() {
        return user1;
    }

    public void setUser1(UserResponse user1) {
        this.user1 = user1;
    }

    public UserResponse getUser2() {
        return user2;
    }

    public void setUser2(UserResponse user2) {
        this.user2 = user2;
    }

    public MessageResponse getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageResponse lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Instant getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Instant lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
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

    public List<MessageResponse> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageResponse> messages) {
        this.messages = messages;
    }
}
