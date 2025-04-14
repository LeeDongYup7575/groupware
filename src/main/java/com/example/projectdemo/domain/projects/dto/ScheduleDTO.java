// ScheduleDTO.java
package com.example.projectdemo.domain.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Integer id;
    private String empNum; // 일정 소유자
    private String creatorEmpNum; // 일정 생성자
    private String creatorName;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean allDay;
    private Integer projectId;
    private String projectName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 화면 표시용 추가 필드
    private Integer participantCount;
    private String statusColor; // 일정 상태에 따른 색상
}