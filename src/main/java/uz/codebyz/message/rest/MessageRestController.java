package uz.codebyz.message.rest;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.message.EditMessageRequest;
import uz.codebyz.message.dto.message.MessageResponse;
import uz.codebyz.message.dto.message.SendMessageRequest;
import uz.codebyz.message.entity.enums.MessageType;
import uz.codebyz.message.service.MessageService;
import uz.codebyz.security.JwtUser;

import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageRestController {

    private final MessageService messageService;

    public MessageRestController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<ResponseDto<MessageResponse>> send(
            @AuthenticationPrincipal JwtUser jwtUser,
            @Valid @RequestBody SendMessageRequest req
    ) {
        return ResponseEntity.ok(messageService.sendMessage(jwtUser.getUserId(), req));
    }

@PostMapping(value = "/send-file", consumes = {"multipart/form-data"})
public ResponseEntity<ResponseDto<MessageResponse>> sendFile(
        @AuthenticationPrincipal JwtUser jwtUser,
        @RequestParam UUID chatId,
        @RequestParam MessageType type,
        @RequestPart MultipartFile file,
        @RequestParam(required = false) String caption
) {
    return ResponseEntity.ok(messageService.sendFileMessage(jwtUser.getUserId(), chatId, type, file, caption));
}

    @PutMapping("/{messageId}")
    public ResponseEntity<ResponseDto<MessageResponse>> edit(
            @AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable UUID messageId,
            @Valid @RequestBody EditMessageRequest req
    ) {
        return ResponseEntity.ok(messageService.editMessage(jwtUser.getUserId(), messageId, req));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<ResponseDto<Void>> delete(
            @AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable UUID messageId
    ) {
        return ResponseEntity.ok(messageService.deleteMessage(jwtUser.getUserId(), messageId));
    }

    @GetMapping("/chats/{chatId}")
    public ResponseEntity<ResponseDto<Page<MessageResponse>>> chatMessages(
            @AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable UUID chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        return ResponseEntity.ok(messageService.getChatMessages(jwtUser.getUserId(), chatId, page, size));
    }
}
