package com.example.projectdemo.domain.employees.service;

import com.example.projectdemo.domain.employees.dto.PositionsDTO;
import com.example.projectdemo.domain.employees.mapper.PositionsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionsService {

    @Autowired
    private PositionsMapper positionsMapper;

//    모든 직급 목록 조회
    public List<PositionsDTO> getAllPositions() {
        return positionsMapper.findAll();
    }

//    ID로 직급 조회
    public PositionsDTO getPositionById(Integer id) {
        return positionsMapper.findById(id);
    }
}