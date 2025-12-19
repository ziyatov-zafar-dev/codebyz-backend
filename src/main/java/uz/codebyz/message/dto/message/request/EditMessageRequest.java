package uz.codebyz.message.dto.message.request;

import java.util.UUID;

public class EditMessageRequest {

    private UUID messageId;
    private String content;

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
