package uz.codebyz.message.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class SendMessageRequest {
    @NotNull
    private UUID chatId;

    @NotBlank
    private String content;

    public SendMessageRequest() {}

    public UUID getChatId() { return chatId; }
    public void setChatId(UUID chatId) { this.chatId = chatId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
