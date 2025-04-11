package com.example.projectdemo.domain.projects.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedules {
    private Integer id;
    private String title;               // 일정 제목
    private String description;         // 일정 설명
    private Integer projectId;          // 프로젝트 ID (null 가능)
    private Integer creatorId;          // 생성자 ID
    private LocalDateTime startTime;    // 시작 시간
    private LocalDateTime endTime;      // 종료 시간
    private String location;            // 장소
    private Boolean isAllDay;           // 종일 일정 여부
    private String repeatType;          // 반복 유형(없음, 매일, 매주, 매월, 매년 등)
    private String repeatEndDate;       // 반복 종료일
    private String color;               // 표시 색상
    private String notificationType;    // 알림 유형
    private Integer notificationMinutes; // 알림 시간(분 전)
    private LocalDateTime createdAt;    // 생성일시
    private LocalDateTime updatedAt;    // 수정일시
}