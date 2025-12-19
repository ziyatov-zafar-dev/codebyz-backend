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
}
