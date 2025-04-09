package com.example.projectdemo.domain.chat.dto;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "unread_messages")
public class UnreadMessage {
    @Id
    private String id;
    private String messageId;
    private int userId;
    private int chatroomId;

    public UnreadMessage(String messageId, int userId, int chatroomId) {
        this.messageId = messageId;
        this.userId = userId;
        this.chatroomId = chatroomId;
    }
}
