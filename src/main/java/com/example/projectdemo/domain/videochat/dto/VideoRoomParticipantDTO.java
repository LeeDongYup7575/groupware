package com.example.projectdemo.domain.videochat.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class VideoRoomParticipantDTO {
    private Long id;
    private String roomId;
    private String empNum;
    private String empName; // 사원 이름 (JOIN으로 가져올 수 있음)
    private String empDept; // 사원 부서 (JOIN으로 가져올 수 있음)
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private boolean isActive;
    private LocalDateTime lastHeartbeat;
}