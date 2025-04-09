package com.example.projectdemo.domain.employees.mapper;

import com.example.projectdemo.domain.employees.dto.PositionsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PositionsMapper {

    // 직급 id(PK)로 직급 조회
    PositionsDTO findById(@Param("posId") Integer posId);

    // 모든 직급 목록 조회
    List<PositionsDTO> findAll();
}
