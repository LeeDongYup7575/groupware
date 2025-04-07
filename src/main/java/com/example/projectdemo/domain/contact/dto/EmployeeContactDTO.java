package com.example.projectdemo.domain.contact.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeContactDTO {
    private String empNum;
    private String name;
    private String internalEmail;
    private String phone;
    private Integer depId;
    private String depName;
}
