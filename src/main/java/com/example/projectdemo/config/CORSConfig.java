package com.example.projectdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://10.10.55.7:3000",
                        "https://techx-groupware.web.app",
                        "http://groupware.techx.kro.kr",
                        "https://967a-221-150-27-169.ngrok-free.app/"
                )
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedMethods("*");
        System.out.println("CORS mappings added with localhost");  // 로깅 메시지 변경
    }
}