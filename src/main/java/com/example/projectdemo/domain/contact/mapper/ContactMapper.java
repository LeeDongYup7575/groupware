package com.example.projectdemo.domain.contact.mapper;

import com.example.projectdemo.domain.contact.dto.EmployeeContactDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContactMapper {
    // 모든 사원의 연락처 정보 조회
    List<EmployeeContactDTO> findAllEmpContacts();
}
