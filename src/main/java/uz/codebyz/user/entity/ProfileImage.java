package uz.codebyz.user.entity;

import jakarta.persistence.*;
import uz.codebyz.common.Helper;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "profile_images")
public class ProfileImage {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private Long fileSize;
    private Boolean active = true;
    @Column(nullable = false)
    private Instant uploadedAt;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }


    public ProfileImage() {
    }

    @PrePersist
    public void prePersist() {
        this.uploadedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = Helper.currentTimeInstant();
    }
}
