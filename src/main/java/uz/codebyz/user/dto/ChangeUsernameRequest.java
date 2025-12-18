package uz.codebyz.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangeUsernameRequest {
    @NotBlank
    @Size(min=3, max=40)
    private String username;

    public ChangeUsernameRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
