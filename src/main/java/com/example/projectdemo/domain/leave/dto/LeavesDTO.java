package com.example.projectdemo.domain.leave.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class LeavesDTO {
    private int id;
    private int empId;
    private int approverId;
    private String leaveType;
    private Date startDate;
    private Date endDate;
    private String status;
    private String reason;
    private int edsmDocId;
    private Timestamp createAt;
    private Timestamp updateAt;
            
}
