package uz.codebyz.auth.service;

import org.springframework.stereotype.Service;
import uz.codebyz.auth.entity.EmailVerification;
import uz.codebyz.auth.entity.VerificationPurpose;
import uz.codebyz.auth.repo.EmailVerificationRepository;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

@Service
public class VerificationService {

    private final EmailVerificationRepository repo;
    private final VerificationProperties props;
    private final SecureRandom rnd = new SecureRandom();

    public VerificationService(EmailVerificationRepository repo, VerificationProperties props) {
        this.repo = repo;
        this.props = props;
    }

    public EmailVerification create(String email, VerificationPurpose purpose) {
        EmailVerification ev = new EmailVerification();
        ev.setEmail(email);
        ev.setPurpose(purpose);
        ev.setCode(generateCode());
        long minutes = (purpose == VerificationPurpose.SIGN_UP) ? props.getSignUpCodeMinutes() : props.getSignInCodeMinutes();
        ev.setExpiresAt(Instant.now().plus(Duration.ofMinutes(minutes)));
        ev.setUsed(false);
        return repo.save(ev);
    }

    public EmailVerification verify(String email, VerificationPurpose purpose, String code) throws VerifyException {
        EmailVerification ev = repo.findLatestNotUsed(email, purpose).orElse(null);
        if (ev == null) throw new VerifyException(VerifyError.NOT_FOUND);
        if (ev.isUsed()) throw new VerifyException(VerifyError.USED);
        if (ev.getExpiresAt().isBefore(Instant.now())) throw new VerifyException(VerifyError.EXPIRED);
        if (!ev.getCode().equals(code)) throw new VerifyException(VerifyError.INVALID);
        ev.setUsed(true);
        return repo.save(ev);
    }

    private String generateCode() {
        int n = 100000 + rnd.nextInt(900000);
        return String.valueOf(n);
    }

    public enum VerifyError { NOT_FOUND, USED, EXPIRED, INVALID }

    public static class VerifyException extends Exception {
        private final VerifyError error;
        public VerifyException(VerifyError error) { this.error = error; }
        public VerifyError getError() { return error; }
    }
}
