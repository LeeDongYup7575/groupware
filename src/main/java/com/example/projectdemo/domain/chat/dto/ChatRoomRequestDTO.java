package com.example.projectdemo.domain.chat.dto;

import java.util.List;

public class ChatRoomRequestDTO {
    private String name;
    private List<Integer> ids;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getMembers() {
        return ids;
    }

    public void setMembers(List<Integer> ids) {
        this.ids = ids;
    }
}
