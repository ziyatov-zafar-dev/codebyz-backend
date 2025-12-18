package uz.codebyz.user.dto;

import jakarta.validation.constraints.NotBlank;
import uz.codebyz.user.entity.SocialLinks;

import java.time.LocalDate;

public class UpdateProfileRequest {
    @NotBlank
    private String fullName;

    private LocalDate birthDate;

    private SocialLinks socialLinks;

    public UpdateProfileRequest() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public SocialLinks getSocialLinks() { return socialLinks; }
    public void setSocialLinks(SocialLinks socialLinks) { this.socialLinks = socialLinks; }
}
