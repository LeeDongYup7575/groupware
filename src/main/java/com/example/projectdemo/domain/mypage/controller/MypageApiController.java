package com.example.projectdemo.domain.mypage.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
public class MypageApiController {
    private final JwtTokenUtil jwtTokenUtil;
    private final EmployeesService employeesService;

    @Autowired
    public MypageApiController(JwtTokenUtil jwtTokenUtil, EmployeesService employeesService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.employeesService = employeesService;
    }

    @GetMapping("/info")
    public ResponseEntity<EmployeesDTO> info(HttpServletRequest request) {
        String empNum = (String)request.getAttribute("empNum");

        EmployeesDTO employee = employeesService.findByEmpNum(empNum);

        return ResponseEntity.ok(employee);
    }

    @GetMapping("/security")
    public ResponseEntity<Map<String, String>> security(HttpServletRequest request) {
        System.out.println("라스트로그인 처리 중...");
        String empNum = (String)request.getAttribute("empNum");

        Map<String, String> response = employeesService.selectLastLogin(empNum);

        return ResponseEntity.ok(response);
    }

//    @GetMapping("/activities/{menu}")
}
