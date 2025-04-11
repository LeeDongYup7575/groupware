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
public class ScheduleParticipantDTO {
    private Integer id;
    private Integer scheduleId;
    private String empNum;
    private String empName; // 매핑용
    private String status;
    private LocalDateTime respondedAt;
}