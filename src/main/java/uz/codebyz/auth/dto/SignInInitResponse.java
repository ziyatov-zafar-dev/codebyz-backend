package uz.codebyz.auth.dto;

public class SignInInitResponse {
    private String email;
    private boolean verificationRequired;

    public SignInInitResponse() {}

    public SignInInitResponse(String email, boolean verificationRequired) {
        this.email = email;
        this.verificationRequired = verificationRequired;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isVerificationRequired() { return verificationRequired; }
    public void setVerificationRequired(boolean verificationRequired) { this.verificationRequired = verificationRequired; }
}
