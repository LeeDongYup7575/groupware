package com.example.projectdemo.domain.attendance.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {
    private Integer id;
    private Integer empId;
    private LocalDate workDate;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private BigDecimal workHours;
    private String status; // enum('출근','퇴근','지각','조퇴','결근','연차','병가')
}