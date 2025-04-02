package com.example.projectdemo.domain.booking.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuppliesBooking {
    private Integer id;
    private Integer supplyId;
    private String empNum;
    private Integer quantity;
    private LocalDateTime start;
    private LocalDateTime end;
    private String purpose;
    private String bookingStatus;
    private LocalDateTime createdAt;

    // 추가 필드: 비품 정보
    private Supplies supplies;
    // 예약자 정보 (employees 테이블과 조인 시 사용)
    private String empName;
    private String deptName;
}
