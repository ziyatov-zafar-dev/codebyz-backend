package uz.codebyz.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ChangeEmailRequest {
    @Email
    @NotBlank
    private String email;

    public ChangeEmailRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
