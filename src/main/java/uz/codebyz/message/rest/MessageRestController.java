package uz.codebyz.message.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.message.MessageResponse;
import uz.codebyz.message.dto.message.request.EditMessageRequest;
import uz.codebyz.message.dto.message.request.SendMessageRequest;
import uz.codebyz.message.service.MessageService;

import java.util.UUID;

@RequestMapping("/api/messages")
@RestController
public class MessageRestController {
    private final MessageService messageService;

    public MessageRestController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("send-message")
    public ResponseEntity<ResponseDto<MessageResponse>> sendMessage(@RequestBody SendMessageRequest req) {
        return ResponseEntity.ok(messageService.sendMessage(req));
    }

    @PutMapping("edit-message")
    public ResponseEntity<ResponseDto<MessageResponse>> editMessage(@RequestBody EditMessageRequest req) {
        return ResponseEntity.ok(messageService.editMessage(req));
    }

    @DeleteMapping("delete-message/{messageId}")
    public ResponseEntity<ResponseDto<Void>> deleteMessage(@PathVariable("messageId") UUID messageId) {
        return ResponseEntity.ok(messageService.deleteMessage(messageId));
    }
}
