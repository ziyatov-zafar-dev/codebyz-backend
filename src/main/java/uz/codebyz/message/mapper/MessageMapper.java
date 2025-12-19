package uz.codebyz.message.mapper;

import org.springframework.stereotype.Component;
import uz.codebyz.common.mapper.UserMapper;
import uz.codebyz.message.dto.message.MessageResponse;
import uz.codebyz.message.entity.Message;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageMapper {
    private final UserMapper userMapper;

    public MessageMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public MessageResponse toDto(Message message) {
        MessageResponse dto = new MessageResponse();
        dto.setId(message.getId());
        dto.setSender(userMapper.toDto(message.getSender()));
        dto.setChatId(message.getChat().getId());
        dto.setType(message.getType());
        dto.setContent(message.getContent());
        dto.setFileUrl(message.getFileUrl());
        dto.setFileName(message.getFileName());
        dto.setFileSize(message.getFileSize());
        dto.setStatus(message.getStatus());
        dto.setReplyTo(message.getReplyTo() == null ? null : (toDto(message.getReplyTo())));
        dto.setCreatedAt(message.getCreatedAt());
        dto.setUpdatedAt(message.getUpdatedAt());
        dto.setEdited(message.getEdited());
        return dto;
    }
    public List<MessageResponse> toDto(List<Message> messages) {
        messages.sort(Comparator.comparing(Message::getCreatedAt));
        messages = messages.stream().filter(message -> !message.isDeleted()).collect(Collectors.toList());
        return messages.stream().map(this::toDto).collect(Collectors.toList());
    }
}
