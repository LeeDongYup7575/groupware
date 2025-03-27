package com.example.projectdemo.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupDTO {

    @NotBlank(message = "사원번호는 필수 입력 항목입니다.")
    private String empNum;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "주민등록번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "\\d{6}-\\d{7}", message = "올바른 주민등록번호 형식이 아닙니다. (예: 000000-0000000)")
    private String ssn;

    @NotBlank(message = "성별은 필수 입력 항목입니다.")
    private String gender; // "M", "F", "Other" 값 중 하나

    private String phone; // 선택사항

    private String profileImgUrl; // 선택사항, 기본값은 서버에서 설정

    private Integer depId; // 부서 ID

    @NotNull(message = "약관 동의는 필수입니다.")
    private Boolean agreeTerms;
}