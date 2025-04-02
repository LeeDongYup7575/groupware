package com.example.projectdemo.domain.booking.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoomBooking {
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

    // 추가 필드: 회의실 정보
    private MeetingRoom meetingRoom;
    // 예약자 정보 (employees 테이블과 조인 시 사용)
    private String empName;
    private String deptName;
}
