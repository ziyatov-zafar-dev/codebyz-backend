package uz.codebyz.message.dto.message;

import java.util.Map;
import java.util.UUID;

public class ReactionSummaryResponse {
    private UUID messageId;
    private Map<String, Long> counts;

    public ReactionSummaryResponse() {}

    public ReactionSummaryResponse(UUID messageId, Map<String, Long> counts) {
        this.messageId = messageId;
        this.counts = counts;
    }

    public UUID getMessageId() { return messageId; }
    public void setMessageId(UUID messageId) { this.messageId = messageId; }

    public Map<String, Long> getCounts() { return counts; }
    public void setCounts(Map<String, Long> counts) { this.counts = counts; }
}
