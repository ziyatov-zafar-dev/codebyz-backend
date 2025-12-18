package uz.codebyz.security;

import java.util.UUID;

public class JwtUser {
    private UUID userId;
    private String email;
    private String type;

    public JwtUser() {}

    public JwtUser(UUID userId, String email, String type) {
        this.userId = userId;
        this.email = email;
        this.type = type;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
