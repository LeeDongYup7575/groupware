package com.example.projectdemo.domain.employees.mapper;

import com.example.projectdemo.domain.employees.dto.EmployeeContactsDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesInfoUpdateDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;


@Mapper
public interface EmployeesMapper {

    // 사원 전체 조회
    List<EmployeesDTO> selectEmpAll();

    // 사원번호로 직원 조회
    EmployeesDTO findByEmpNum(@Param("empNum") String empNum);

    // 사원 고유 아이디로 직원 조회
    EmployeesDTO findById(@Param("empId") Integer empId);

    // 이메일로 직원 조회
    EmployeesDTO findByEmail(@Param("email") String email);

    // 사원번호, 이름, 이메일, 주민번호로 직원 조회
    EmployeesDTO findByEmpNumAndNameAndEmailAndSsn(
            @Param("empNum") String empNum,
            @Param("name") String name,
            @Param("email") String email,
            @Param("ssn") String ssn);

    // 회원가입 정보 업데이트
    int updateRegistrationStatus(
            @Param("empNum") String empNum,
            @Param("registered") boolean registered,
            @Param("password") String password,
            @Param("profileImgUrl") String profileImgUrl,
            @Param("phone") String phone,
            @Param("gender") String gender,
            @Param("tempPassword") boolean tempPassword);

    // 비밀번호 업데이트
    int updatePassword(
            @Param("empNum") String empNum,
            @Param("password") String password,
            @Param("tempPassword") boolean tempPassword);

    // 마지막 로그인 시간 업데이트
    int updateLastLogin(
            @Param("empNum") String empNum,
            @Param("lastLogin") LocalDateTime lastLogin);

    // 출결 상태 업데이트 메서드 추가
    int updateAttendStatus(
            @Param("empId") Integer empId,
            @Param("attendStatus") String attendStatus);

    // 마지막 로그인 시간 조회
    LocalDateTime selectLastLogin(@Param("empNum") String empNum);

    // 마이페이지 회원정보(프로필이미지, 전화번호, 개인이메일) 수정
    int updateEmpInfo(@Param("updatedEmp") EmployeesInfoUpdateDTO updatedEmp);

    // 모든 사원의 연락처 정보 조회
    List<EmployeeContactsDTO> findAllEmpContacts();
}