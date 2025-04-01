package com.example.projectdemo.domain.employees.mapper;

import com.example.projectdemo.domain.employees.dto.DepartmentsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DepartmentsMapper {

    // 부서 id(PK)로 부서 조회
    DepartmentsDTO findById(@Param("depId") Integer depId);
}
