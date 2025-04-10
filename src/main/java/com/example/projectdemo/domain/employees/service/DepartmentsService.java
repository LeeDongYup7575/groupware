package com.example.projectdemo.domain.employees.service;

import com.example.projectdemo.domain.employees.dto.DepartmentsDTO;
import com.example.projectdemo.domain.employees.mapper.DepartmentsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentsService {
    @Autowired
    private DepartmentsMapper departmentsMapper;

    /**
     * 부서 조회
     */
    public List<DepartmentsDTO> getAllDepartments() {
        return departmentsMapper.findAll();
    }

    /**
     * ID로 부서 조회
     */
    public DepartmentsDTO findById(Integer id) {
        return departmentsMapper.findById(id);
    }
}