package uz.codebyz.message.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.chat.ChatResponse;
import uz.codebyz.message.entity.Chat;
import uz.codebyz.message.entity.Message;
import uz.codebyz.message.entity.enums.ChatStatus;
import uz.codebyz.message.entity.enums.MessageStatus;
import uz.codebyz.message.entity.enums.MessageType;
import uz.codebyz.message.repository.ChatRepository;
import uz.codebyz.message.repository.MessageRepository;
import uz.codebyz.message.service.ChatService;
import uz.codebyz.security.JwtUser;
import uz.codebyz.user.entity.User;
import uz.codebyz.user.repo.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats")
public class ChatRestController {
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    public ChatRestController(ChatService chatService, UserRepository userRepository, ChatRepository chatRepository, MessageRepository messageRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
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
            @AuthenticationPrincipal JwtUser user,
            @RequestParam("userid") UUID userid
    ) throws Exception {
        return ResponseEntity.ok(chatService.createChat(user.getUserId(), userid));
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
            @AuthenticationPrincipal JwtUser user,
            @RequestParam("userid") UUID user2id
    ) {
        return ResponseEntity.ok(chatService.existsChat(
                        user.getUserId(), user2id
                )
        );
    }

    @PostMapping("generate-chats")
    public void genereateChats(@AuthenticationPrincipal JwtUser jwtUser, @RequestParam("uid") UUID userid) {

        Optional<User> u1Opt = userRepository.findById(jwtUser.getUserId());
        Optional<User> u2Opt = userRepository.findById(userid);

        if (u1Opt.isEmpty() || u2Opt.isEmpty()) {
            System.out.println("❌ Users not found, bootstrap skipped");
            return;
        }

        User user1 = u1Opt.get();
        User user2 = u2Opt.get();

        Optional<Chat> existingChat =
                chatRepository.findByUser1AndUser2(user1, user2)
                        .or(() -> chatRepository.findByUser2AndUser1(user1, user2));

        if (existingChat.isPresent()) {
            System.out.println("ℹ️ Chat already exists, bootstrap skipped");
            return;
        }

        // ======================
        // 1️⃣ CHAT CREATE
        // ======================
        Chat chat = new Chat();
        chat.setUser1(user1);
        chat.setUser2(user2);
        chat.setStatus(ChatStatus.ACTIVE);
        chat.setCreatedAt(Instant.now());
        chat.setUpdatedAt(Instant.now());

        chat = chatRepository.save(chat);

        // ======================
        // 2️⃣ MESSAGE 1
        // ======================
        Message m1 = new Message();
        m1.setChat(chat);
        m1.setSender(user1);
        m1.setType(MessageType.TEXT);
        m1.setContent("Salom! Qalaysan?");
        m1.setStatus(MessageStatus.SENT);
        m1.setDeleted(false);
        m1.setCreatedAt(Instant.now());
        m1.setUpdatedAt(Instant.now());

        m1 = messageRepository.save(m1);

        // ======================
        // 3️⃣ MESSAGE 2 (REPLY)
        // ======================
        Message m2 = new Message();
        m2.setChat(chat);
        m2.setSender(user2);
        m2.setType(MessageType.TEXT);
        m2.setContent("Yaxshi rahmat 🙂 O‘zingchi?");
        m2.setStatus(MessageStatus.SENT);
        m2.setReplyTo(m1);
        m2.setDeleted(false);
        m2.setCreatedAt(Instant.now());
        m2.setUpdatedAt(Instant.now());

        m2 = messageRepository.save(m2);

        // ======================
        // 4️⃣ CHAT LAST MESSAGE UPDATE
        // ======================
        chat.setLastMessageId(m2.getId());
        chat.setLastMessageTime(m2.getCreatedAt());
        chatRepository.save(chat);

        System.out.println("✅ Chat and messages bootstrap completed");

    }
}
