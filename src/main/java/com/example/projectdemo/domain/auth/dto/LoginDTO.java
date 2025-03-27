package com.example.projectdemo.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = "사원번호는 필수입니다.")
    private String empNum;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}