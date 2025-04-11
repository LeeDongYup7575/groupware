package com.example.projectdemo.domain.projects.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskLogs {
    private Integer id;
    private Integer taskId;             // 업무 ID
    private String empNum;         // 직원
    private String logType;             // 로그 유형(상태변경, 담당자변경, 코멘트, 진행도 변경 등)
    private String oldValue;            // 이전 값
    private String newValue;            // 새 값
    private String comment;             // 코멘트
    private LocalDateTime createdAt;    // 생성일시
}