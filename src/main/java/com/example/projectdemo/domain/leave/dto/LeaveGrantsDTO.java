package com.example.projectdemo.domain.leave.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class LeaveGrantsDTO {
    private int id;
    private int empId;
    private Date grantDate;
    private String grantType;
    private int leaveCount;
    private Timestamp createdAt;
}
