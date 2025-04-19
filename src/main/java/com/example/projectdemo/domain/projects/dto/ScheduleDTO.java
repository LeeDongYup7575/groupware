// ScheduleDTO.java
package com.example.projectdemo.domain.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Integer id;
    private String creatorEmpNum;
    private String creatorName;            // 일정 생성자 이름 (매핑용)
    private String title;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isAllDay;              // is_all_day와 매핑 (allDay에서 변경)
    private String repeatType;             // 추가: 반복 유형
    private LocalDate repeatEndDate;       // 추가: 반복 종료일
    private String color;                  // 추가: 표시 색상
    private String notificationType;       // 추가: 알림 유형
    private Integer notificationMinutes;   // 추가: 알림 시간(분 전)
    private Integer projectId;
    private String projectName;            // 매핑용
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 화면 표시용
    private Integer participantCount;
    private String statusColor;
}