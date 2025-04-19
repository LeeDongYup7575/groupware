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
    private Integer projectId;
    private String empNum;
    private String role;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
}