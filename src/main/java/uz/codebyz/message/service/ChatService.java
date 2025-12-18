package uz.codebyz.message.service;

import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.chat.ChatResponse;
import uz.codebyz.security.JwtUser;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    ResponseDto<ChatResponse> existsChat(UUID user1Id, UUID user2Id);

    ResponseDto<ChatResponse> getChatByUser1IdAndUser2Id(UUID user1Id, UUID user2Id);

    ResponseDto<ChatResponse> getChat(UUID chatId);

    ResponseDto<ChatResponse> createChat(UUID user1Id, UUID user2Id) throws Exception;

    ResponseDto<Void> deleteChat(UUID chatId);

    ResponseDto<List<ChatResponse>> geyMyChats(UUID userid);
}
