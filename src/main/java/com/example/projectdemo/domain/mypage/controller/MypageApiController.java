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
    public ResponseEntity<EmployeesDTO> mypage(HttpServletRequest request) {
        String empNum = (String)request.getAttribute("empNum");

        EmployeesDTO employee = employeesService.findByEmpNum(empNum);

        return ResponseEntity.ok(employee);
    }

//    @GetMapping("/activities/{menu}")
}
