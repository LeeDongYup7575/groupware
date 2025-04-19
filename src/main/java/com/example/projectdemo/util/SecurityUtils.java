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

}