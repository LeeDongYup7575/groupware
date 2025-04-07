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
    private String name;        // 이름 필드 추가
    private String deptName;    // 부서명 필드 추가
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private boolean isActive;
}