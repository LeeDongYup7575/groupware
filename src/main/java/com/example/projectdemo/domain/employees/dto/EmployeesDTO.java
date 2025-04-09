package com.example.projectdemo.domain.employees.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeesDTO {
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
    private String departmentName; // 부서명 (매핑용)
    private Integer posId;
    private String positionTitle; // 직급명 (매핑용)
    private LocalDate hireDate;
    private Integer retirementId;
    private String attendStatus;
    private BigDecimal salary;
    private boolean enabled;
    private String role;
    private LocalDateTime lastLogin;
    private boolean registered;
    private Integer temp_password;
    private BigDecimal totalLeave;
    private BigDecimal usedLeave;

    // 추가 보안 관련 필드
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
}