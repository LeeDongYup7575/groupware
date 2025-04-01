package com.example.projectdemo.domain.auth.jwt;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final EmployeesMapper employeeMapper;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 인증이 필요하지 않은 경로 목록
    private final List<String> allowedPaths = Arrays.asList(
            "/api/auth/login",
            "/api/auth/refresh-token",
            "/api/auth/register",
            "/api/auth/reset-password",
            "/api/auth/verify-employee",
            "/auth/**",
            "/error",
            "/",
            "/resources/**",
            "/static/**",
            "/css/**",
            "/js/**",
            "/assets/**",
            "/images/**"
    );

    //개발용
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        return true;
//    }

    @Autowired
    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, EmployeesMapper employeeMapper, ObjectMapper objectMapper) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.employeeMapper = employeeMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 인증이 필요 없는 경로는 통과
        if (isAllowedPath(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 헤더에서 JWT 토큰 추출
        String authHeader = request.getHeader("Authorization");

        System.out.println("Authorization 헤더: " + (authHeader != null ? "존재함" : "없음"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, 401, "인증 토큰이 필요합니다.");
            return;
        }


        String token = authHeader.substring(7);

        try {
            // 토큰 유효성 검증
            System.out.println("토큰 유효성 검증 시작");
            boolean isValid = jwtTokenUtil.validateToken(token);
            System.out.println("토큰 유효성 검증 결과: " + isValid);
            if (!jwtTokenUtil.validateToken(token)) {
                sendErrorResponse(response, 401, "유효하지 않은 토큰입니다.");
                return;
            }

            // 토큰에서 사용자 정보 추출
            String empNum = jwtTokenUtil.getEmpNumFromToken(token);
            String role = jwtTokenUtil.getRoleFromToken(token);
            boolean tempPassword = jwtTokenUtil.getTempPasswordStatusFromToken(token);

            // 사용자 정보 확인
            EmployeesDTO employee = employeeMapper.findByEmpNum(empNum);

            if (employee == null || !employee.isEnabled()) {
                sendErrorResponse(response, 401, "비활성화된 계정입니다.");
                return;
            }
            int id = jwtTokenUtil.getIdFromToken(token);

            // 임시 비밀번호 상태 확인 (비밀번호 변경 페이지로의 접근은 허용)
            if (tempPassword && !requestURI.contains("/api/auth/change-password")) {
                sendErrorResponse(response, 403, "비밀번호 변경이 필요합니다.", true);
                return;
            }

            // 마지막 로그인 시간 업데이트
            employeeMapper.updateLastLogin(empNum, LocalDateTime.now());

            // 요청 속성에 사용자 정보 설정
            request.setAttribute("id",id);
            request.setAttribute("empNum", empNum);
            request.setAttribute("role", role);
            request.setAttribute("tempPassword", tempPassword);

            // 요청 진행
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            sendErrorResponse(response, 401, "인증 처리 중 오류가 발생했습니다.");
        }
    }

    private boolean isAllowedPath(String requestURI) {
        return allowedPaths.stream()
                .anyMatch(path -> pathMatcher.match(path, requestURI));
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        sendErrorResponse(response, status, message, false);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message, boolean passwordChangeRequired) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status);
        errorResponse.put("error", status == 401 ? "Unauthorized" : "Forbidden");
        errorResponse.put("message", message);

        if (passwordChangeRequired) {
            errorResponse.put("requirePasswordChange", true);
        }

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}