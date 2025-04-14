// ScheduleParticipantDTO.java
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
    private Integer scheduleId;
    private String empNum;
    private String empName;
    private String status; // 참석, 불참, 미정
    private LocalDateTime joinedAt;
}
