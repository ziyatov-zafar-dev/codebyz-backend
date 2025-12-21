package uz.codebyz.message.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.chat.ChatResponse;
import uz.codebyz.message.dto.chat.UnreadSummaryResponse;
import uz.codebyz.message.service.ChatService;
import uz.codebyz.security.JwtUser;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats")
public class ChatRestController {

    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/private/{otherUserId}")
    public ResponseEntity<ResponseDto<ChatResponse>> createOrGetPrivate(
            @AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable("otherUserId") UUID otherUserId
    ) {
        return ResponseEntity.ok(chatService.createOrGetPrivateChat(jwtUser.getUserId(), otherUserId));
    }

    @GetMapping("/my")
    public ResponseEntity<ResponseDto<List<ChatResponse>>> myChats(
            @AuthenticationPrincipal JwtUser jwtUser
    ) {
        return ResponseEntity.ok(chatService.getMyChats(jwtUser.getUserId()));
    }

    @GetMapping("/unread-summary")
    public ResponseEntity<ResponseDto<UnreadSummaryResponse>> unreadSummary(
            @AuthenticationPrincipal JwtUser jwtUser
    ) {
        return ResponseEntity.ok(chatService.getUnreadSummary(jwtUser.getUserId()));
    }

    @PostMapping("/{chatId}/read")
    public ResponseEntity<ResponseDto<Void>> markRead(
            @AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable("chatId") UUID chatId
    ) {
        return ResponseEntity.ok(chatService.markChatRead(jwtUser.getUserId(), chatId));
    }

    @PostMapping("/{chatId}/pin/{messageId}")
    public ResponseEntity<ResponseDto<Void>> pin(
            @AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable("chatId") UUID chatId,
            @PathVariable("messageId") UUID messageId
    ) {
        return ResponseEntity.ok(chatService.pinMessage(jwtUser.getUserId(), chatId, messageId));
    }

    @PostMapping("/{chatId}/unpin")
    public ResponseEntity<ResponseDto<Void>> unpin(
            @AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable("chatId") UUID chatId
    ) {
        return ResponseEntity.ok(chatService.unpinMessage(jwtUser.getUserId(), chatId));
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<ResponseDto<Void>> deleteChat(
            @AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable("chatId") UUID chatId
    ) {
        return ResponseEntity.ok(chatService.deleteChat(jwtUser.getUserId(), chatId));
    }
}
