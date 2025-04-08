package com.example.projectdemo.domain.contact.mapper;

import com.example.projectdemo.domain.contact.dto.EmployeeContactDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContactMapper {
    // 모든 사원의 연락처 정보 조회
    List<EmployeeContactDTO> findAllEmpContacts();

    // 부서별 공유주소록(사원연락처) 조회
    List<EmployeeContactDTO> findEmpContactsByDepartment(@Param("depName") String depName);
}
