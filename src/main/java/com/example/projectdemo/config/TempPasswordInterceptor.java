package com.example.projectdemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class TempPasswordInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    @Autowired
    public TempPasswordInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 인증 관련 엔드포인트는 제외
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/api/auth/login") ||
                requestURI.contains("/api/auth/change-password") ||
                requestURI.contains("/api/auth/reset-password") ||
                requestURI.contains("/api/auth/refresh-token")) {
            return true;
        }

        // JWT 필터에서 설정한 임시 비밀번호 상태 확인
        Boolean tempPassword = (Boolean) request.getAttribute("tempPassword");

        if (tempPassword != null && tempPassword) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", HttpServletResponse.SC_FORBIDDEN);
            errorResponse.put("error", "Forbidden");
            errorResponse.put("message", "비밀번호 변경이 필요합니다.");
            errorResponse.put("requirePasswordChange", true);

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return false;
        }

        return true;
    }
}