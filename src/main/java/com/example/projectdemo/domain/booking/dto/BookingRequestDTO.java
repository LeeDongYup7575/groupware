package com.example.projectdemo.domain.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    // 회의실 예약 필드
    private Integer roomId;
    private String title;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private Integer attendees;
    private String purpose;

    // 비품 예약 필드
    private Integer supplyId;
    private Integer quantity;
}