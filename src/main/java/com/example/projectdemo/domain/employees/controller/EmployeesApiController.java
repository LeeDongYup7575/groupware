package com.example.projectdemo.domain.employees.controller;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeesApiController {

    @Autowired
    private EmployeesService employeesService;

    // 모든 활성화된 직원 목록 가져오기
    @GetMapping("/list")
    public ResponseEntity<List<EmployeesDTO>> getEmployeesList(HttpServletRequest request) {
        try {
            // 모든 활성화된 직원 목록 가져오기
            List<EmployeesDTO> employees = employeesService.getAllActiveEmployees();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}