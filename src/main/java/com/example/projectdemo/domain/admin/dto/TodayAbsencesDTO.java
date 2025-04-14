package com.example.projectdemo.domain.admin.dto;

import lombok.Data;
import org.checkerframework.checker.units.qual.A;

@Data
@A
public class TodayAbsencesDTO {
    private int count;
    private String status;
    private int empId;
    private String name;
}
