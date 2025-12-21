package uz.codebyz.message.dto.chat;

public class UnreadSummaryResponse {
    private long unreadChats;
    private long unreadMessages;

    public UnreadSummaryResponse() {}

    public UnreadSummaryResponse(long unreadChats, long unreadMessages) {
        this.unreadChats = unreadChats;
        this.unreadMessages = unreadMessages;
    }

    public long getUnreadChats() { return unreadChats; }
    public void setUnreadChats(long unreadChats) { this.unreadChats = unreadChats; }

    public long getUnreadMessages() { return unreadMessages; }
    public void setUnreadMessages(long unreadMessages) { this.unreadMessages = unreadMessages; }
}
