package com.example.projectdemo.domain.chat.dto;

import java.sql.Date;

public class ChatRoomDTO {
    private int id;
    private String name;
    private Date createdAt;

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
