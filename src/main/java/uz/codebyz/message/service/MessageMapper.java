package uz.codebyz.message.service;

import uz.codebyz.message.dto.message.MessageResponse;
import uz.codebyz.message.entity.Message;
import uz.codebyz.user.entity.User;

import java.util.UUID;

public class MessageMapper {

    public static MessageResponse toResponse(Message m) {
        MessageResponse r = new MessageResponse();
        r.setId(m.getId());
        r.setChatId(m.getChat() != null ? m.getChat().getId() : null);

        User s = m.getSender();
        if (s != null) {
            r.setSenderId(s.getId());
            r.setSenderFullName(s.getFullName());
        } else {
            r.setSenderId((UUID) null);
            r.setSenderFullName("Sistem");
        }

        r.setType(m.getType());
        r.setStatus(m.getStatus());
        r.setContent(m.getContent());
        r.setCreatedAt(m.getCreatedAt());
        r.setUpdatedAt(m.getUpdatedAt());
        return r;
    }
}
