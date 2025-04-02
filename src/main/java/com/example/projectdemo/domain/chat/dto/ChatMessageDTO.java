package com.example.projectdemo.domain.chat.dto;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "chat_messages")
public class ChatMessageDTO {
    @Id
    private String senderName;
    private String id;
    private String type;
    private String content;
    private Date sentAt;
    private int chatroomId;
    private int senderId;
}
