package uz.codebyz.message.entity;

import jakarta.persistence.*;
import uz.codebyz.user.entity.User;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "message_reactions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_message_reaction", columnNames = {"message_id", "user_id", "emoji"})
        },
        indexes = {
                @Index(name = "idx_reaction_message", columnList = "message_id"),
                @Index(name = "idx_reaction_user", columnList = "user_id")
        }
)
public class MessageReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String emoji;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    public MessageReaction() {}

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
