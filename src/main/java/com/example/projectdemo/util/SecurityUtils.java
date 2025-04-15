package com.example.projectdemo.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    /**
     * JWT 토큰에서 추출한 사원번호를 검증하고 반환
     * @param request HTTP 요청
     * @return 사원번호, 없으면 null
     */
    public static String getEmployeeNumber(HttpServletRequest request) {
        return (String) request.getAttribute("empNum");
    }

    /**
     * 인증된 사용자인지 확인
     * @param request HTTP 요청
     * @return 인증된 사용자면 true, 아니면 false
     */
    public static boolean isAuthenticated(HttpServletRequest request) {
        return getEmployeeNumber(request) != null;
    }
}