package uz.codebyz.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_login_guard")
public class UserLoginGuard {

    @Id
    private UUID userId;

    @Column(nullable = false)
    private int consecutiveFails;

    @Column(nullable = false)
    private int lockStageHours;

    private Instant lockUntil;

    public UserLoginGuard() {}

    public UserLoginGuard(UUID userId) {
        this.userId = userId;
        this.consecutiveFails = 0;
        this.lockStageHours = 0;
        this.lockUntil = null;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public int getConsecutiveFails() { return consecutiveFails; }
    public void setConsecutiveFails(int consecutiveFails) { this.consecutiveFails = consecutiveFails; }

    public int getLockStageHours() { return lockStageHours; }
    public void setLockStageHours(int lockStageHours) { this.lockStageHours = lockStageHours; }

    public Instant getLockUntil() { return lockUntil; }
    public void setLockUntil(Instant lockUntil) { this.lockUntil = lockUntil; }
}
