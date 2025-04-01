package com.example.projectdemo.config;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 권한 체크 인터셉터
 * JWT 필터에서 설정한 정보를 활용하여 특정 권한이 필요한 API에 대한 접근 제어
 */
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // JWT 필터에서 설정한 사용자 역할 가져오기
        String role = (String) request.getAttribute("role");

        // 요청 URI 확인
        String requestURI = request.getRequestURI();

        // 관리자 전용 API 확인
        if (requestURI.startsWith("/api/admin") && !isAdmin(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"관리자 권한이 필요합니다.\",\"status\":403}");
            return false;
        }

        return true;
    }

    private boolean isAdmin(String role) {
        return role != null && role.equals("ROLE_ADMIN");
    }
}