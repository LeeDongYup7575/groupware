package com.example.projectdemo.domain.employees.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employees {

    private Integer id;
    private String empNum;
    private String password;
    private String name;
    private String gender;
    private String phone;
    private String email;
    private String internalEmail;
    private String profileImgUrl;
    private String ssn;
    private Integer depId;
    private Integer posId;
    private LocalDate hireDate;
    private Integer retirementId;
    private String attendStatus;
    private BigDecimal salary;
    private boolean enabled = true;
    private String role = "ROLE_USER";
    private LocalDateTime lastLogin;
    private boolean registered = false;
    private Integer temp_password;
    private BigDecimal totalLeave;
    private BigDecimal usedLeave;

    // Transient 대신 제외할 필드는 직접 선언하지 않음
    private transient Departments department;
    private transient Positions position;
}