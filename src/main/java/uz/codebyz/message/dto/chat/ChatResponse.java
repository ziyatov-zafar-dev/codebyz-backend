package uz.codebyz.message.dto.chat;

import uz.codebyz.message.entity.enums.ChatStatus;
import uz.codebyz.message.entity.enums.ChatType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChatResponse {
    private UUID id;
    private ChatType type;
    private ChatStatus status;
    private List<UUID> memberIds;
    private Instant lastMessageTime;

    private long unreadCount;
    private UUID pinnedMessageId;

    public ChatResponse() {}

    public ChatResponse(UUID id, ChatType type, ChatStatus status, List<UUID> memberIds, Instant lastMessageTime, long unreadCount, UUID pinnedMessageId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.memberIds = memberIds;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
        this.pinnedMessageId = pinnedMessageId;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public ChatType getType() { return type; }
    public void setType(ChatType type) { this.type = type; }

    public ChatStatus getStatus() { return status; }
    public void setStatus(ChatStatus status) { this.status = status; }

    public List<UUID> getMemberIds() { return memberIds; }
    public void setMemberIds(List<UUID> memberIds) { this.memberIds = memberIds; }

    public Instant getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(Instant lastMessageTime) { this.lastMessageTime = lastMessageTime; }

    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }

    public UUID getPinnedMessageId() { return pinnedMessageId; }
    public void setPinnedMessageId(UUID pinnedMessageId) { this.pinnedMessageId = pinnedMessageId; }
}
