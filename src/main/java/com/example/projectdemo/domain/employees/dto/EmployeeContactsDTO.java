package com.example.projectdemo.domain.employees.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeContactsDTO {
    private String empNum;
    private String name;
    private String internalEmail;
    private String phone;
    private Integer depId;
    private String depName;
}
