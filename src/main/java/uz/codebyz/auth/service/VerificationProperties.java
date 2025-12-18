package uz.codebyz.auth.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verification")
public class VerificationProperties {
    private long signUpCodeMinutes;
    private long signInCodeMinutes;

    public VerificationProperties() {}

    public long getSignUpCodeMinutes() { return signUpCodeMinutes; }
    public void setSignUpCodeMinutes(long signUpCodeMinutes) { this.signUpCodeMinutes = signUpCodeMinutes; }

    public long getSignInCodeMinutes() { return signInCodeMinutes; }
    public void setSignInCodeMinutes(long signInCodeMinutes) { this.signInCodeMinutes = signInCodeMinutes; }
}
