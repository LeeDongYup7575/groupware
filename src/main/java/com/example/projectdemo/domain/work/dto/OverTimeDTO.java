package com.example.projectdemo.domain.work.dto;

import lombok.Data;


import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class OverTimeDTO {
    private int id;
    private int empId;
    private int edsmDocId;
    private String workDate;
    private String startTime;
    private String endTime;
    private String status;
    private String reason;
    private Timestamp createdAt;
}
