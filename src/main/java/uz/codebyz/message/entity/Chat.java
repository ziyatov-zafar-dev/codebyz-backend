package uz.codebyz.message.entity;

import jakarta.persistence.*;
import uz.codebyz.message.entity.enums.ChatStatus;
import uz.codebyz.message.entity.enums.ChatType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "chats",
        indexes = {
                @Index(name = "idx_chat_type", columnList = "type"),
                @Index(name = "idx_chat_last_message_time", columnList = "last_message_time")
        }
)
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatStatus status;

    @Column(name = "user1_id", columnDefinition = "uuid")
    private UUID user1Id;

    @Column(name = "user2_id", columnDefinition = "uuid")
    private UUID user2Id;

    @Column(name = "platform_key", length = 80)
    private String platformKey;

    @Column(name = "pinned_message_id", columnDefinition = "uuid")
    private UUID pinnedMessageId;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;

    @Column(name = "last_message_time")
    private Instant lastMessageTime;

    public Chat() {}

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) this.status = ChatStatus.ACTIVE;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public ChatType getType() { return type; }
    public void setType(ChatType type) { this.type = type; }

    public ChatStatus getStatus() { return status; }
    public void setStatus(ChatStatus status) { this.status = status; }

    public UUID getUser1Id() { return user1Id; }
    public void setUser1Id(UUID user1Id) { this.user1Id = user1Id; }

    public UUID getUser2Id() { return user2Id; }
    public void setUser2Id(UUID user2Id) { this.user2Id = user2Id; }

    public String getPlatformKey() { return platformKey; }
    public void setPlatformKey(String platformKey) { this.platformKey = platformKey; }

    public UUID getPinnedMessageId() { return pinnedMessageId; }
    public void setPinnedMessageId(UUID pinnedMessageId) { this.pinnedMessageId = pinnedMessageId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(Instant lastMessageTime) { this.lastMessageTime = lastMessageTime; }
}
