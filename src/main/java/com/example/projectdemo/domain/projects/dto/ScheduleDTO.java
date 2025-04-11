package com.example.projectdemo.domain.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Integer id;
    private String title;
    private String description;
    private Integer projectId;
    private String projectName; // 매핑용
    private String creatorEmpNum;
    private String creatorName; // 매핑용
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private Boolean isAllDay;
    private String repeatType;
    private LocalDateTime repeatEndDate;
    private String color;
    private String notificationType;
    private Integer notificationMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 추가 정보
    private List<ScheduleParticipantDTO> participants;
}