package uz.codebyz.message.ws;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import uz.codebyz.message.dto.message.MessageResponse;
import uz.codebyz.message.ws.dto.ChatEventResponse;

import java.util.UUID;

@Component
public class ChatEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publish(UUID chatId, ChatEventResponse event) {
        if (chatId == null) return;
        messagingTemplate.convertAndSend("/topic/chats/" + chatId, event);
    }

    public void messageSent(UUID chatId, MessageResponse msg) {
        publish(chatId, ChatEventResponse.of("MESSAGE_SENT", chatId, msg != null ? msg.getId() : null, msg));
    }

    public void messageEdited(UUID chatId, MessageResponse msg) {
        publish(chatId, ChatEventResponse.of("MESSAGE_EDITED", chatId, msg != null ? msg.getId() : null, msg));
    }

    public void messageDeleted(UUID chatId, UUID messageId) {
        publish(chatId, ChatEventResponse.of("MESSAGE_DELETED", chatId, messageId, null));
    }

    public void chatRead(UUID chatId) {
        publish(chatId, ChatEventResponse.of("CHAT_READ", chatId, null, null));
    }

    public void pinned(UUID chatId, UUID messageId) {
        publish(chatId, ChatEventResponse.of("PINNED", chatId, messageId, null));
    }

    public void unpinned(UUID chatId) {
        publish(chatId, ChatEventResponse.of("UNPINNED", chatId, null, null));
    }

    public void reactionAdded(UUID chatId, UUID messageId, String emoji) {
        ChatEventResponse e = ChatEventResponse.of("REACTION_ADDED", chatId, messageId, null);
        e.setEmoji(emoji);
        publish(chatId, e);
    }

    public void reactionRemoved(UUID chatId, UUID messageId, String emoji) {
        ChatEventResponse e = ChatEventResponse.of("REACTION_REMOVED", chatId, messageId, null);
        e.setEmoji(emoji);
        publish(chatId, e);
    }

    public void chatDeleted(UUID chatId) {
        publish(chatId, ChatEventResponse.of("CHAT_DELETED", chatId, null, null));
    }

    public void system(UUID chatId, MessageResponse msg) {
        publish(chatId, ChatEventResponse.of("SYSTEM", chatId, msg != null ? msg.getId() : null, msg));
    }
}
