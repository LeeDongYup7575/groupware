package com.example.projectdemo.domain.videochat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoRoomParticipant {
    private Long id;
    private String roomId;
    private String empNum;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private LocalDateTime lastHeartbeat; // 하트비트 필드 추가
    private boolean isActive;
}