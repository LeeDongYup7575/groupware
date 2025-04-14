// TaskLogDTO.java
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
public class TaskLogDTO {
    private Integer id;
    private Integer taskId;
    private String taskTitle;
    private Integer projectId;
    private String projectName;
    private String empNum;
    private String empName;
    private String logType; // 업무 생성, 업무 수정, 상태 변경, 진행률 변경 등
    private String oldValue;
    private String newValue;
    private String comment;
    private LocalDateTime createdAt;
}