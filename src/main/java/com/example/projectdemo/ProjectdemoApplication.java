package com.example.projectdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
})
@MapperScan("com.example.projectdemo.domain")
public class ProjectdemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProjectdemoApplication.class, args);
	}
}
