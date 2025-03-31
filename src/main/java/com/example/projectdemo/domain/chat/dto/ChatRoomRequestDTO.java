package com.example.projectdemo.domain.chat.dto;

import java.util.List;

public class ChatRoomRequestDTO {
    private String name;
    private List<String> members;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<String> getMembers() {
        return members;
    }
    public void setMembers(List<String> members) {
        this.members = members;
    }

}
