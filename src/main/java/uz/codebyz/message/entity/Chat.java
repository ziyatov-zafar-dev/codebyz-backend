package uz.codebyz.message.entity;

import jakarta.persistence.*;
import uz.codebyz.message.entity.enums.ChatStatus;
import uz.codebyz.user.entity.ProfileImage;
import uz.codebyz.user.entity.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "chats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chat_user1_user2",
                        columnNames = {"user1_id", "user2_id"}
                )
        },
        indexes = {
                @Index(name = "idx_chat_user1", columnList = "user1_id"),
                @Index(name = "idx_chat_user2", columnList = "user2_id"),
                @Index(name = "idx_chat_last_message_time", columnList = "last_message_time")
        }
)
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * Private chat participant #1
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    /**
     * Private chat participant #2
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    /**
     * Chat status (ACTIVE / ARCHIVED / DELETED)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatStatus status;

    /**
     * Last message ID (FK qilinmadi – performance uchun)
     */
    @Column(name = "last_message_id", columnDefinition = "uuid")
    private UUID lastMessageId;

    /**
     * Last message time (chat list sorting uchun)
     */
    @Column(name = "last_message_time")
    private Instant lastMessageTime;

    /**
     * Chat creation time
     */
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Chat update time
     */
    @Column(nullable = false)
    private Instant updatedAt;
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();


    // ================= LIFECYCLE =================

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = ChatStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    // ================= GETTERS / SETTERS =================

    public UUID getId() {
        return id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public ChatStatus getStatus() {
        return status;
    }

    public void setStatus(ChatStatus status) {
        this.status = status;
    }

    public UUID getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(UUID lastMessageId) {
        this.lastMessageId = lastMessageId;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
