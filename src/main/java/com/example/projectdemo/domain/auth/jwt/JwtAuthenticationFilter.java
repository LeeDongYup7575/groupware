package com.example.projectdemo.domain.auth.jwt;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
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
            "/images/**","/ws/**","/ws"
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
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            // CORS 프리플라이트 요청에 필요한 헤더 설정
            response.setHeader("Access-Control-Allow-Origin", "http://10.10.55.57:3000"); // 필요에 따라 도메인 제한 가능
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            response.setHeader("Access-Control-Max-Age", "3600"); // 프리플라이트 캐싱 시간(초)

            // OPTIONS 요청은 여기서 종료 (200 OK 응답)
            response.setStatus(HttpServletResponse.SC_OK);
            return; // 더 이상 필터 체인을 진행하지 않음
        }

        String requestURI = request.getRequestURI();

        // 인증이 필요 없는 경로는 통과
        if (isAllowedPath(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 추출 시도
        String token = null;

        // 1. 헤더에서 토큰 확인
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization 헤더: " + (authHeader != null ? "존재함" : "없음"));

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("헤더에서 토큰 추출: " + (token != null ? token.substring(0, Math.min(10, token.length())) + "..." : "없음"));
        }

        // 2. 토큰이 없으면 쿠키에서 확인
        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        token = cookie.getValue();
                        System.out.println("쿠키에서 토큰 추출: " + (token != null ? token.substring(0, Math.min(10, token.length())) + "..." : "없음"));
                        break;
                    }
                }
            } else {
                System.out.println("쿠키가 없음");
            }
        }

        if (token == null) {
            sendErrorResponse(response, 401, "인증 토큰이 필요합니다.");
            return;
        }

        try {
            // 토큰 유효성 검증
            System.out.println("토큰 유효성 검증 시작");
            boolean isValid = jwtTokenUtil.validateToken(token);
            System.out.println("토큰 유효성 검증 결과: " + isValid);

            if (!isValid) {
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
            request.setAttribute("id", id);
            request.setAttribute("empNum", empNum);
            request.setAttribute("role", role);
            request.setAttribute("tempPassword", tempPassword);

            // 요청 진행
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            System.out.println("인증 처리 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
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