package com.example.projectdemo.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Integer id;
    private Integer empId;
    private String empNum;
    private String employeeName;
    private LocalDate workDate;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private BigDecimal workHours;
    private String status;

    // 추가 필드 (UI 표시용)
    private String departmentName; // 부서명 (매핑용)
    private String positionTitle;  // 직급명 (매핑용)
}