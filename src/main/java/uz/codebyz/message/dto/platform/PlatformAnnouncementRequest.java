package uz.codebyz.message.dto.platform;

import jakarta.validation.constraints.NotBlank;
import uz.codebyz.message.entity.enums.PlatformAudience;

public class PlatformAnnouncementRequest {
    @NotBlank
    private String content;

    private PlatformAudience audience = PlatformAudience.ALL_USERS;

    public PlatformAnnouncementRequest() {}

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public PlatformAudience getAudience() { return audience; }
    public void setAudience(PlatformAudience audience) { this.audience = audience; }
}
