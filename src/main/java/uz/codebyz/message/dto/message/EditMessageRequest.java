package uz.codebyz.message.dto.message;

import jakarta.validation.constraints.NotBlank;

public class EditMessageRequest {
    @NotBlank
    private String content;

    public EditMessageRequest() {}

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
