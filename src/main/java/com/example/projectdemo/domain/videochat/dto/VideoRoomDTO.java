package com.example.projectdemo.domain.videochat.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class VideoRoomDTO {
    private String id;
    private String name;
    private String password;
    private String createdBy;
    private LocalDateTime createdAt;
    private boolean isActive;
    private int maxParticipants;
    private int currentParticipants;

    private boolean isPasswordProtected;

    // 비밀번호 존재 여부 확인 -> 비밀번호 기능 없애서 삭제해도 되지만.. 일단 냅둠
    public boolean getIsPasswordProtected() {
        return password != null && !password.isEmpty();
    }
}