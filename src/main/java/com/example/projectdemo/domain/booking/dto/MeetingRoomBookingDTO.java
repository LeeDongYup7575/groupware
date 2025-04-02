package com.example.projectdemo.domain.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoomBookingDTO {
    private Integer id;
    private Integer roomId;
    private String empNum;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer attendees;
    private String purpose;
    private String bookingStatus;
    private LocalDateTime createdAt;

    // 회의실 상세 정보 (조회 시 사용)
    private String roomName;
    private String roomLocation;
    // 예약자 정보
    private String empName;
    private String deptName;
}