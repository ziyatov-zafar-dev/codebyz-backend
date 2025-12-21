package uz.codebyz.message.dto.message;

import jakarta.validation.constraints.NotBlank;

public class ReactionRequest {
    @NotBlank
    private String emoji;

    public ReactionRequest() {}

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
}
