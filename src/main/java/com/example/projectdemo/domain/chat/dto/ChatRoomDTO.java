package com.example.projectdemo.domain.chat.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
@Data
public class ChatRoomDTO {
    private int id;
    private String name;
    private Date createdAt;
    private String lastMessage;
    private Timestamp lastMessageTime;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public ChatRoomDTO() {
        super();
    }

    public ChatRoomDTO(int id, String name, Date createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;

    }
}
