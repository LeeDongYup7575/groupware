package com.example.projectdemo.config;

import com.example.projectdemo.domain.auth.jwt.JwtAuthenticationFilter;
import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {
    private final JwtTokenUtil jwtTokenUtil;
    private final EmployeesMapper employeeMapper;
    private final ObjectMapper objectMapper;

    // PasswordEncoder 생성자에서 제거
    public WebSecurityConfig(
            JwtTokenUtil jwtTokenUtil,
            EmployeesMapper employeeMapper,
            ObjectMapper objectMapper
    ) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.employeeMapper = employeeMapper;
        this.objectMapper = objectMapper;
    }

    // PasswordEncoder를 별도의 빈으로 정의
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenUtil, employeeMapper, objectMapper);
    }
}