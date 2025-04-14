package com.example.projectdemo.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentLoginDTO {
    private int empNum;
    private String name;
    private String departmentName;
    private String positionTitle;
    private Timestamp lastLogin;

}
