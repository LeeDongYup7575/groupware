package com.example.projectdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
})
@EnableMongoRepositories(basePackages = "com.example.projectdemo.mongodb.repository")
@MapperScan("com.example.projectdemo.domain")
@EnableScheduling
public class ProjectdemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProjectdemoApplication.class, args);
	}
}
