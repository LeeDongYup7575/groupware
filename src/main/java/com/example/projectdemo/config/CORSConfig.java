package com.example.projectdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//public class CORSConfig implements WebMvcConfigurer {
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**").allowedOrigins("http://10.10.55.57:3000","http://172.30.1.74:3000","http://10.10.55.7:3000", "http://10.10.55.16:3000", "http://localhost:3000").allowCredentials(true).allowedHeaders("*").allowedMethods("*");
//    System.out.println("CORS mappings added");
//    }
//
//}

@Configuration
public class CORSConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://10.10.55.57:3000",
                        "http://172.30.1.74:3000",
                        "http://10.10.55.7:3000",
                        "http://localhost:3000"
                )
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedMethods("*");
        System.out.println("CORS mappings added with localhost");  // 로깅 메시지 변경
    }
}