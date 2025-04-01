package com.example.projectdemo.domain.employees.mapper;

import com.example.projectdemo.domain.employees.dto.PositionsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PositionsMapper {

    // 직급 id(PK)로 직급 조회
    PositionsDTO findById(@Param("posId") Integer posId);
}
