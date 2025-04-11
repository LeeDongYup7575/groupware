package com.example.projectdemo.domain.projects.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMembers {
    private Integer id;
    private Integer projectId;          // 프로젝트 ID
    private String empNum;         // 직원
    private String role;                // 역할(팀원, 매니저, 관찰자 등)
    private LocalDateTime joinedAt;     // 참여 시작일
    private LocalDateTime leftAt;       // 참여 종료일
}