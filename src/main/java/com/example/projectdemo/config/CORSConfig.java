package com.example.projectdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://10.10.55.57:3000").allowCredentials(true).allowedHeaders("*").allowedMethods("*");
    System.out.println("CORS mappings added");
    }

}
