package com.example.projectdemo.domain.chat.dto;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class MemberShipDTO {
    private int id;
    private int empId;
    private int chatroomId;
    private boolean role;
    private Timestamp joinnedAt;
}
