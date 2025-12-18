package uz.codebyz.auth.service;

import org.springframework.stereotype.Service;
import uz.codebyz.auth.entity.UserLoginGuard;
import uz.codebyz.auth.repo.UserLoginGuardRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class LoginGuardService {

    private final UserLoginGuardRepository repo;

    public LoginGuardService(UserLoginGuardRepository repo) {
        this.repo = repo;
    }

    public GuardStatus check(UUID userId) {
        UserLoginGuard g = repo.findById(userId).orElseGet(() -> new UserLoginGuard(userId));
        if (g.getLockUntil() != null && g.getLockUntil().isAfter(Instant.now())) {
            return new GuardStatus(true, g.getLockUntil());
        }
        return new GuardStatus(false, null);
    }

    public void onFailed(UUID userId) {
        Instant now = Instant.now();
        UserLoginGuard g = repo.findById(userId).orElseGet(() -> new UserLoginGuard(userId));

        if (g.getLockUntil() != null && g.getLockUntil().isAfter(now)) {
            if (g.getLockStageHours() >= 2) {
                g.setLockUntil(g.getLockUntil().plus(Duration.ofHours(1)));
                repo.save(g);
            }
            return;
        }

        if (g.getLockStageHours() >= 2) {
            Instant base = (g.getLockUntil() == null || g.getLockUntil().isBefore(now)) ? now : g.getLockUntil();
            g.setLockUntil(base.plus(Duration.ofHours(1)));
            repo.save(g);
            return;
        }

        g.setConsecutiveFails(g.getConsecutiveFails() + 1);
        if (g.getConsecutiveFails() >= 3) {
            if (g.getLockStageHours() == 0) {
                g.setLockStageHours(1);
                g.setLockUntil(now.plus(Duration.ofHours(1)));
            } else if (g.getLockStageHours() == 1) {
                g.setLockStageHours(2);
                g.setLockUntil(now.plus(Duration.ofHours(2)));
            }
            g.setConsecutiveFails(0);
        }
        repo.save(g);
    }

    public void onSuccess(UUID userId) {
        UserLoginGuard g = repo.findById(userId).orElseGet(() -> new UserLoginGuard(userId));
        g.setConsecutiveFails(0);
        repo.save(g);
    }

    public static class GuardStatus {
        private final boolean locked;
        private final Instant lockUntil;

        public GuardStatus(boolean locked, Instant lockUntil) {
            this.locked = locked;
            this.lockUntil = lockUntil;
        }

        public boolean isLocked() { return locked; }
        public Instant getLockUntil() { return lockUntil; }
    }
}
