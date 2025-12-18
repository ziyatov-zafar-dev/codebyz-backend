package uz.codebyz.common.dto.user;

import uz.codebyz.user.dto.ProfileImageDto;
import uz.codebyz.user.entity.ApprovalStatus;
import uz.codebyz.user.entity.SocialLinks;
import uz.codebyz.user.entity.UserRole;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class UserResponse {
    private UUID    id;
    private String fullName;
    private String username;
    private String email;
    private LocalDate birthDate;
    private SocialLinks socialLinks;
    private List<ProfileImageDto> profileImages;
    private UserRole role;
    private ApprovalStatus approvalStatus;
    private Instant approvalUpdatedAt;
    private String approvalReason;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public SocialLinks getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(SocialLinks socialLinks) {
        this.socialLinks = socialLinks;
    }

    public List<ProfileImageDto> getProfileImages() {
        return profileImages;
    }

    public void setProfileImages(List<ProfileImageDto> profileImages) {
        this.profileImages = profileImages;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Instant getApprovalUpdatedAt() {
        return approvalUpdatedAt;
    }

    public void setApprovalUpdatedAt(Instant approvalUpdatedAt) {
        this.approvalUpdatedAt = approvalUpdatedAt;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }
}
