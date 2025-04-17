package com.example.projectdemo.domain.videoconf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoRoomParticipantDTO {
    private Long id;
    private String roomId;
    private String empNum;
    private String name;        // 이름 필드
    private String deptName;    // 부서명 필드
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private LocalDateTime lastHeartbeat; // 하트비트 필드 추가
    private boolean isActive;
}