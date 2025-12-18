package uz.codebyz.message.mapper;

import org.springframework.stereotype.Component;
import uz.codebyz.common.mapper.UserMapper;
import uz.codebyz.message.dto.chat.ChatResponse;
import uz.codebyz.message.entity.Chat;
import uz.codebyz.message.entity.Message;

import java.util.List;

@Component
public class ChatMapper {
    private final UserMapper userMapper;
    private final MessageMapper messageMapper;

    public ChatMapper(UserMapper userMapper, MessageMapper messageMapper) {
        this.userMapper = userMapper;
        this.messageMapper = messageMapper;
    }

    public ChatResponse toDto(Chat chat, Message lastMessage) {
        ChatResponse dto = new ChatResponse();
        dto.setId(chat.getId());
        dto.setUser1(userMapper.toDto(chat.getUser1()));
        dto.setUser2(userMapper.toDto(chat.getUser2()));
        dto.setLastMessage(messageMapper.toDto(lastMessage));
        dto.setLastMessageTime(chat.getLastMessageTime());
        dto.setCreatedAt(chat.getCreatedAt());
        dto.setUpdatedAt(chat.getUpdatedAt());
        dto.setMessages(messageMapper.toDto(chat.getMessages()));
        return dto;
    }
}
