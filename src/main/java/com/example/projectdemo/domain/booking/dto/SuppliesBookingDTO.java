package com.example.projectdemo.domain.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuppliesBookingDTO {
    private Integer id;
    private Integer supplyId;
    private String empNum;
    private Integer quantity;
    private LocalDateTime start;
    private LocalDateTime end;
    private String purpose;
    private String bookingStatus;
    private LocalDateTime createdAt;

    // 비품 상세 정보 (조회 시 사용)
    private String supplyName;
    private String category;
    // 예약자 정보
    private String empName;
    private String deptName;
}
