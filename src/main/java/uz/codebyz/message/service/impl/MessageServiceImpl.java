package uz.codebyz.message.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.codebyz.common.ErrorCode;
import uz.codebyz.common.Helper;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.message.MessageResponse;
import uz.codebyz.message.dto.message.request.EditMessageRequest;
import uz.codebyz.message.dto.message.request.SendMessageRequest;
import uz.codebyz.message.entity.Chat;
import uz.codebyz.message.entity.Message;
import uz.codebyz.message.entity.enums.ChatStatus;
import uz.codebyz.message.entity.enums.MessageStatus;
import uz.codebyz.message.mapper.MessageMapper;
import uz.codebyz.message.repository.ChatRepository;
import uz.codebyz.message.repository.MessageRepository;
import uz.codebyz.message.service.MessageService;
import uz.codebyz.user.entity.User;
import uz.codebyz.user.repo.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {
    private final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper messageMapper;

    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository, ChatRepository chatRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageMapper = messageMapper;
    }

    @Override
    public ResponseDto<MessageResponse> sendMessage(SendMessageRequest req) {
        Optional<User> uOp = userRepository.findById(req.getSenderUserId());
        if (uOp.isEmpty()) {
            log.error("User not found: {}", req.getSenderUserId());
            return ResponseDto.fail(
                    404,
                    ErrorCode.USER_NOT_FOUND,
                    "Kullanıcı bulunamadı"
            );
        }
        Optional<Chat> chatOp = chatRepository.findById(req.getChatId());
        boolean delete = false;
        if (chatOp.isEmpty()) {
            log.error("Chat not found: {}", req.getSenderUserId());
            return ResponseDto.fail(
                    404,
                    ErrorCode.USER_NOT_FOUND,
                    "Chat bulunamadı"
            );
        } else {
            Chat chat = chatOp.get();
            if (chat.getStatus() == ChatStatus.DELETE) {
                log.error("Chat not found: {}", req.getSenderUserId());
                return ResponseDto.fail(
                        404,
                        ErrorCode.CHAT_NOT_FOUND,
                        ErrorCode.CHAT_NOT_FOUND.getTr()
                );
            }
        }
        Optional<Message> mOp = messageRepository.findById(req.getReplyToMessageId());
        if (mOp.isEmpty()) {
            log.error("Message not found: {}", req.getReplyToMessageId());
            return ResponseDto.fail(
                    404,
                    ErrorCode.MESSAGE_NOT_FOUND,
                    ErrorCode.MESSAGE_NOT_FOUND.getTr()
            );
        } else {
            if (mOp.get().isDeleted()) {
                log.error("Message not found: {}", req.getReplyToMessageId());
                return ResponseDto.fail(
                        404,
                        ErrorCode.MESSAGE_NOT_FOUND,
                        ErrorCode.MESSAGE_NOT_FOUND.getTr()
                );
            }
        }
        Chat chat = chatOp.get();
        Message message = new Message();
        message.setSender(uOp.get());
        message.setChat(chat);
        message.setType(req.getType());
        message.setContent(req.getContent());
        message.setFileUrl(req.getFileUrl());
        message.setFileName(req.getFileName());
        message.setFileSize(req.getFileSize());
        message.setStatus(MessageStatus.SENT);
        message.setReplyTo(mOp.get());
        message.setDeleted(false);
        message.setCreatedAt(Helper.currentTimeInstant());
        message.setUpdatedAt(Helper.currentTimeInstant());
        message.setEdited(false);
        return ResponseDto.ok("Success", messageMapper.toDto(messageRepository.save(message)));
    }

    @Override
    public ResponseDto<MessageResponse> editMessage(EditMessageRequest req) {
        Optional<Message> mOp = messageRepository.findById(req.getMessageId());
        if (mOp.isEmpty()) {
            log.error("Message not found: {}", req.getMessageId());
            return ResponseDto.fail(
                    404,
                    ErrorCode.MESSAGE_NOT_FOUND,
                    ErrorCode.MESSAGE_NOT_FOUND.getTr()
            );
        } else {
            if (mOp.get().isDeleted()) {
                log.error("Message not found: {}", req.getMessageId());
                return ResponseDto.fail(
                        404,
                        ErrorCode.MESSAGE_NOT_FOUND,
                        ErrorCode.MESSAGE_NOT_FOUND.getTr()
                );
            }
        }
        Message message = mOp.get();
        message.setContent(req.getContent());
        message.setEdited(true);
        message.setUpdatedAt(Helper.currentTimeInstant());
        return ResponseDto.ok("Success", messageMapper.toDto(messageRepository.save(message)));
    }

    @Override
    public ResponseDto<Void> deleteMessage(UUID messageId) {
        Optional<Message> mOp = messageRepository.findById(messageId);
        if (mOp.isEmpty()) {
            log.error("Message not found: {}", messageId);
            return ResponseDto.fail(
                    404,
                    ErrorCode.MESSAGE_NOT_FOUND,
                    ErrorCode.MESSAGE_NOT_FOUND.getTr()
            );
        } else {
            if (mOp.get().isDeleted()) {
                log.error("Message not found: {}", messageId);
                return ResponseDto.fail(
                        404,
                        ErrorCode.MESSAGE_NOT_FOUND,
                        ErrorCode.MESSAGE_NOT_FOUND.getTr()
                );
            }
        }
        Message message = mOp.get();
        message.setDeleted(true);
        messageRepository.save(message);
        return ResponseDto.ok("Success", null);
    }
}
