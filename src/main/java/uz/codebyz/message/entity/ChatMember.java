package uz.codebyz.message.entity;

import jakarta.persistence.*;
import uz.codebyz.user.entity.User;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "chat_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_chat_member", columnNames = {"chat_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_chat_member_user", columnList = "user_id"),
                @Index(name = "idx_chat_member_chat", columnList = "chat_id")
        }
)
public class ChatMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, name = "joined_at")
    private Instant joinedAt;

    @Column(nullable = false, name = "active")
    private boolean active;

    @Column(nullable = false, name = "last_read_at")
    private Instant lastReadAt;

    public ChatMember() {}

    @PrePersist
    public void prePersist() {
        this.joinedAt = Instant.now();
        this.active = true;
        this.lastReadAt = Instant.EPOCH; // başlangıçta hiç okumadı
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Chat getChat() { return chat; }
    public void setChat(Chat chat) { this.chat = chat; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Instant getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Instant joinedAt) { this.joinedAt = joinedAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getLastReadAt() { return lastReadAt; }
    public void setLastReadAt(Instant lastReadAt) { this.lastReadAt = lastReadAt; }
}
