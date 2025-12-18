package uz.codebyz.message.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.chat.ChatResponse;
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

    @GetMapping("my-chats")
    public ResponseEntity<ResponseDto<List<ChatResponse>>> getAllChats(
            @AuthenticationPrincipal JwtUser jwtUser
    ) {
        return ResponseEntity.ok(chatService.geyMyChats(jwtUser.getUserId()));
    }


    @DeleteMapping("chat/delete/{chatId}")
    public ResponseEntity<ResponseDto<Void>> deleteChat(
            @PathVariable("chatId") UUID chatId
    ) {
        return ResponseEntity.ok(chatService.deleteChat(chatId));
    }

    @PostMapping("create-chat")
    public ResponseEntity<ResponseDto<ChatResponse>> createChat(
            @RequestParam("user1id") UUID user1id,
            @RequestParam("user2id") UUID user2id
    ) throws Exception {
        return ResponseEntity.ok(chatService.createChat(user1id, user2id));
    }

    @GetMapping("chat/{chatId}")
    public ResponseEntity<ResponseDto<ChatResponse>> getChat(
            @PathVariable("chatId") UUID chatid
    ) throws Exception {
        return ResponseEntity.ok(chatService.getChat(chatid));
    }

    @GetMapping("chat-by-user1id-user2id")
    public ResponseEntity<ResponseDto<ChatResponse>> getChatByUser1IdAndUser2Id(
            @RequestParam("user1id") UUID user1id,
            @RequestParam("user2id") UUID user2id
    ) {
        return ResponseEntity.ok(chatService.getChatByUser1IdAndUser2Id(
                        user1id, user2id
                )
        );
    }


    @GetMapping("exists-by-chat")
    public ResponseEntity<ResponseDto<ChatResponse>> existsChatByChat(
            @RequestParam("user1id") UUID user1id,
            @RequestParam("user2id") UUID user2id
    ) {
        return ResponseEntity.ok(chatService.existsChat(
                        user1id, user2id
                )
        );
    }
}
