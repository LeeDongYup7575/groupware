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

    // 추가 필드 - 현재 참가자 수를 저장하기 위한 필드
    private boolean isPasswordProtected;

    // 비밀번호 존재 여부 확인
    public boolean getIsPasswordProtected() {
        return password != null && !password.isEmpty();
    }
}