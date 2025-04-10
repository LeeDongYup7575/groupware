package com.example.projectdemo.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnreadNotificationDTO {
    private int chatroomId;
}
