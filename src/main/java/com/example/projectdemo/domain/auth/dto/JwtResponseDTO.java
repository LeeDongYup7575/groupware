package com.example.projectdemo.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponseDTO {
    // 기본 인증 정보
    private String accessToken;
    private String refreshToken;

    // 핵심 직원 정보
    private int id;
    private String empNum;
    private String name;
    private String email;
    private String internalEmail;
    private String role;

    // 추가 정보
    private String departmentName;
    private String positionTitle;
    private String phone;
    private String profileImgUrl;

    // 계정 상태 관련
    private boolean tempPassword;
    private boolean enabled;
    private LocalDateTime lastLogin;

    // 성별, 입사일 등 추가 정보
    private String gender;
    private LocalDate hireDate;

    // 권한 및 보안 관련
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    // 출퇴근 및 근무 관련 정보
    private String attendStatus;
}