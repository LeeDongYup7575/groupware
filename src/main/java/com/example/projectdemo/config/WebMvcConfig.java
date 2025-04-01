package com.example.projectdemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 MVC 설정
 * 인터셉터 등록 및 경로 설정
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthorizationInterceptor authorizationInterceptor;
    private final TempPasswordInterceptor tempPasswordInterceptor;
    private final LoginTrackerInterceptor loginTrackerInterceptor;

    @Autowired
    public WebMvcConfig(
            AuthorizationInterceptor authorizationInterceptor,
            TempPasswordInterceptor tempPasswordInterceptor,
            LoginTrackerInterceptor loginTrackerInterceptor) {
        this.authorizationInterceptor = authorizationInterceptor;
        this.tempPasswordInterceptor = tempPasswordInterceptor;
        this.loginTrackerInterceptor = loginTrackerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //요청은 나중에 개발 후 수정 예정 ..

        // 로그인 추적 인터셉터 등록 (모든 API 요청)
        registry.addInterceptor(loginTrackerInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/register", "/api/auth/reset-password");

        // 임시 비밀번호 상태 체크 인터셉터 등록
        registry.addInterceptor(tempPasswordInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/change-password",
                        "/api/auth/reset-password", "/api/auth/refresh-token");

        // 권한 체크 인터셉터 등록 (관리자 권한 필요한 API)
        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/api/admin/**");
    }
}