package uz.codebyz.message.service;

import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.chat.ChatResponse;
import uz.codebyz.message.dto.message.MessageResponse;
import uz.codebyz.message.dto.message.request.SendMessageRequest;
import uz.codebyz.message.entity.Message;

import java.util.UUID;

public interface MessageService {
    ResponseDto<MessageResponse>sendMessage(SendMessageRequest req);
}
