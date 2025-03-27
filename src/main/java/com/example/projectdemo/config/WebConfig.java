package com.example.projectdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads/profiles}")
    private String uploadDir;

    @Value("${file.profile-access-path:/uploads/profiles}")
    private String profileAccessPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 외부 경로에 저장된 프로필 이미지를 접근 가능하도록 설정
        Path uploadPath = Paths.get(uploadDir);
        String uploadAbsolutePath = uploadPath.toFile().getAbsolutePath();

        // 프로필 이미지 접근 경로 설정
        registry.addResourceHandler(profileAccessPath + "/**")
                .addResourceLocations("file:" + uploadAbsolutePath + "/");

        // 기본 정적 리소스 경로 설정
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");
    }
}