package uz.codebyz.user.dto;

import uz.codebyz.user.entity.SocialLinks;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class UserMeResponse {
    private UUID id;
    private String fullName;
    private String username;
    private String email;
    private LocalDate birthDate;
    private SocialLinks socialLinks;
    private List<ProfileImageDto> profileImages;

    public UserMeResponse() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public SocialLinks getSocialLinks() { return socialLinks; }
    public void setSocialLinks(SocialLinks socialLinks) { this.socialLinks = socialLinks; }

    public List<ProfileImageDto> getProfileImages() { return profileImages; }
    public void setProfileImages(List<ProfileImageDto> profileImages) { this.profileImages = profileImages; }
}
