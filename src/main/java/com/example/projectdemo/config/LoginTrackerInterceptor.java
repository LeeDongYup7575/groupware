package com.example.projectdemo.config;

import com.example.projectdemo.domain.employees.mapper.EmployeeMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

/**
 * 로그인 추적 인터셉터
 * 사용자의 마지막 로그인 시간을 업데이트
 */
@Component
public class LoginTrackerInterceptor implements HandlerInterceptor {

    private final EmployeeMapper employeeMapper;

    @Autowired
    public LoginTrackerInterceptor(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // JWT 필터에서 설정한 사원번호 가져오기
        String empNum = (String) request.getAttribute("empNum");

        // 사원번호가 있는 경우 마지막 로그인 시간 업데이트
        if (empNum != null) {
            employeeMapper.updateLastLogin(empNum, LocalDateTime.now());
        }

        return true;
    }
}