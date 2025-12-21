package uz.codebyz.message.rest;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.message.ReactionRequest;
import uz.codebyz.message.dto.message.ReactionSummaryResponse;
import uz.codebyz.message.service.ReactionService;
import uz.codebyz.security.JwtUser;

import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class ReactionRestController {

    private final ReactionService reactionService;

    public ReactionRestController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @GetMapping("/{messageId}/reactions")
    public ResponseEntity<ResponseDto<ReactionSummaryResponse>> getSummary(
            @AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable UUID messageId
    ) {
        return ResponseEntity.ok(reactionService.getReactionSummary(jwtUser.getUserId(), messageId));
    }

    @PostMapping("/{messageId}/reactions")
    public ResponseEntity<ResponseDto<ReactionSummaryResponse>> add(
            @AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable UUID messageId,
            @Valid @RequestBody ReactionRequest req
    ) {
        return ResponseEntity.ok(reactionService.addReaction(jwtUser.getUserId(), messageId, req.getEmoji()));
    }

    @DeleteMapping("/{messageId}/reactions")
    public ResponseEntity<ResponseDto<ReactionSummaryResponse>> remove(
            @AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable UUID messageId,
            @RequestParam String emoji
    ) {
        return ResponseEntity.ok(reactionService.removeReaction(jwtUser.getUserId(), messageId, emoji));
    }
}
