package com.example.projectdemo.domain.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/test")
public class AdminTestController {

    @GetMapping
    public ResponseEntity<Map<String, String>> testAdminAPI() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "그룹웨어 관리자 API가 정상적으로 작동 중입니다.");
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }
}