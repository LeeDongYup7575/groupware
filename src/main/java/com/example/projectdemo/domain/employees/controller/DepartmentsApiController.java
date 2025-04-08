package com.example.projectdemo.domain.employees.controller;

import com.example.projectdemo.domain.employees.dto.DepartmentsDTO;
import com.example.projectdemo.domain.employees.service.DepartmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentsApiController {
    private final DepartmentsService departmentsService;

    @Autowired
    public DepartmentsApiController(DepartmentsService departmentsService) {
        this.departmentsService = departmentsService;
    }

    /**
     * 부서 조회
     */
    @GetMapping
    public ResponseEntity<List<DepartmentsDTO>> getAllDepartments() {
        try {
            List<DepartmentsDTO> departments = departmentsService.getAllDepartments();

            if (departments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(departments);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
