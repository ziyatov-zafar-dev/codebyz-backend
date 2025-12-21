package uz.codebyz.message.ws.dto;

import uz.codebyz.message.dto.message.MessageResponse;

import java.time.Instant;
import java.util.UUID;

public class ChatEventResponse {
    private String event;
    private UUID chatId;
    private UUID messageId;
    private MessageResponse message;
    private String emoji;
    private Instant at;

    public ChatEventResponse() {}

    public static ChatEventResponse of(String event, UUID chatId, UUID messageId, MessageResponse message) {
        ChatEventResponse r = new ChatEventResponse();
        r.setEvent(event);
        r.setChatId(chatId);
        r.setMessageId(messageId);
        r.setMessage(message);
        r.setAt(Instant.now());
        return r;
    }

    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }

    public UUID getChatId() { return chatId; }
    public void setChatId(UUID chatId) { this.chatId = chatId; }

    public UUID getMessageId() { return messageId; }
    public void setMessageId(UUID messageId) { this.messageId = messageId; }

    public MessageResponse getMessage() { return message; }
    public void setMessage(MessageResponse message) { this.message = message; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public Instant getAt() { return at; }
    public void setAt(Instant at) { this.at = at; }
}
