package com.example.projectdemo.domain.employees.service;

import com.example.projectdemo.domain.employees.dto.DepartmentsDTO;
import com.example.projectdemo.domain.employees.mapper.DepartmentsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentsService {
    @Autowired
    private DepartmentsMapper departmentsMapper;

    /**
     * 부서 조회
     */
    public List<DepartmentsDTO> getAllDepartments(){
        return departmentsMapper.findAll();
    }
}
