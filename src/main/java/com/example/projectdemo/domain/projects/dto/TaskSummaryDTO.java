// TaskSummaryDTO.java - 업무 통계용 DTO
package com.example.projectdemo.domain.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSummaryDTO {
    private Integer totalTasks;
    private Integer notStartedTasks;
    private Integer inProgressTasks;
    private Integer completedTasks;
    private Integer delayedTasks;
    private Double completionRate; // 완료율 (%)

    // 우선순위별 개수
    private Integer highPriorityTasks;
    private Integer mediumPriorityTasks;
    private Integer lowPriorityTasks;
}