package com.example.projectdemo.domain.leave.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class LeavesDTO {
    private int id;
    private String empId;
    private int edsmDocId;
    private String leaveType;
    private String startDate;
    private String endDate;
    private String status;
    private String reason;
    private Timestamp createAt;

            
}
