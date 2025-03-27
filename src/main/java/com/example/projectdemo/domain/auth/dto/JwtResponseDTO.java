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
    private String empNum;
    private String name;
    private String email;
    private String role;

    // 추가 정보
    private String departmentName;     // 부서명
    private String positionTitle;      // 직급
    private String phone;              // 전화번호
    private String profileImgUrl;      // 프로필 이미지 URL

    // 계정 상태 관련
    private boolean tempPassword;      // 임시 비밀번호 여부
    private boolean enabled;           // 계정 활성화 상태
    private LocalDateTime lastLogin;   // 마지막 로그인 시간

    // 성별, 입사일 등 추가 정보
    private String gender;
    private LocalDate hireDate;

    // 권한 및 보안 관련
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    // 출퇴근 및 근무 관련 정보
    private String attendStatus;        // 현재 출퇴근 상태
}