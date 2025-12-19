package uz.codebyz.message.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.codebyz.message.entity.Chat;
import uz.codebyz.message.entity.Message;
import uz.codebyz.message.entity.enums.ChatStatus;
import uz.codebyz.message.entity.enums.MessageStatus;
import uz.codebyz.message.entity.enums.MessageType;
import uz.codebyz.message.repository.ChatRepository;
import uz.codebyz.message.repository.MessageRepository;
import uz.codebyz.user.entity.User;
import uz.codebyz.user.repo.UserRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class ChatMessageBootstrap {

    private static final UUID USER_1_ID =
            UUID.fromString("226c1537-2b09-4028-9e2d-c291ef8300c2");

    private static final UUID USER_2_ID =
            UUID.fromString("dc255c87-0116-4f9e-92d5-fe8fa5d72422");

    @Bean
    CommandLineRunner initChatsAndMessages(
            UserRepository userRepository,
            ChatRepository chatRepository,
            MessageRepository messageRepository
    ) {
        return args -> {

            Optional<User> u1Opt = userRepository.findById(USER_1_ID);
            Optional<User> u2Opt = userRepository.findById(USER_2_ID);

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
        };
    }
}
