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
public class ProjectMemberDTO {
    private Integer id;
    private Integer projectId;
    private String empNum;
    private String role;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;

    // 매핑용
    private String name;
    private String depName;
    private String posTitle;
}