package uz.codebyz.notify;

public interface EmailService {
    void sendVerificationCode(String toEmail, String subject, String code, String purpose);
}
