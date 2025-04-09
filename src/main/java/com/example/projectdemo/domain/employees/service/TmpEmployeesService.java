package com.example.projectdemo.domain.employees.service;

import com.example.projectdemo.config.PasswordEncoder;
import com.example.projectdemo.domain.auth.service.EmailService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.DepartmentsMapper;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.mapper.PositionsMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class TmpEmployeesService {

    @Autowired
    private EmployeesMapper employeeMapper;
    @Autowired
    private DepartmentsMapper departmentsMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL_CHARS = "^$*.[]{}()?-\"!@#%&/\\,><':;|_~`+=";
    private static final String ALL_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL_CHARS;
    //private static final String ALL_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static final SecureRandom random = new SecureRandom();

    @Autowired
    private PositionsMapper positionsMapper;

    public List<EmployeesDTO> getAllEmployees() {
        List<EmployeesDTO> employees = employeeMapper.selectEmpAll();

        // 각 직원 부서명과 직급명 설정
        for (EmployeesDTO employee : employees) {
            if (employee.getDepId() != null) {
                String depName = departmentsMapper.findById(employee.getDepId()).getName();
                employee.setDepartmentName(depName);
            }

            if (employee.getPosId() != null) {
                String posTitle = positionsMapper.findById(employee.getPosId()).getTitle();
                employee.setPositionTitle(posTitle);
            }
        }

        return employees;
    }


    public List<EmployeesDTO> getEmployeesByDepartment(Integer depId) {
        // 부서별 수정 필요~ 현재는 모든 직원을 필터링하는 방식으로 구현
        List<EmployeesDTO> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .filter(emp -> emp.getDepId() != null && emp.getDepId().equals(depId))
                .toList();
    }


    @Transactional
    public EmployeesDTO updateEmployee(EmployeesDTO employeeDTO) {
        // 기존 직원 정보 조회
        EmployeesDTO existingEmployee = employeeMapper.findById(employeeDTO.getId());

        if (existingEmployee == null) {
            throw new RuntimeException("직원을 찾을 수 없습니다: ID=" + employeeDTO.getId());
        }

        // 비밀번호 변경이 있는 경우 인코딩
        if (employeeDTO.getPassword() != null && !employeeDTO.getPassword().isEmpty()) {
            employeeDTO.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        } else {
            employeeDTO.setPassword(existingEmployee.getPassword());
        }

        // 내부 이메일은 변경할 수 없음
        employeeDTO.setInternalEmail(existingEmployee.getInternalEmail());

        // 직원 정보 업데이트 - 아직 구현 필요….
        // employeeMapper.updateEmployee(employeeDTO); 이런 방식으로 해야할듯

        // 업데이트된 직원 정보 반환
        return employeeMapper.findById(employeeDTO.getId());
    }


    @Transactional
    public boolean deleteEmployee(Integer id) {
        // 직원 정보 조회
        EmployeesDTO employee = employeeMapper.findById(id);

        if (employee == null) {
            return false;
        }

        // 실제 삭제 대신 비활성화 처리 로직 생각해보기
        // employeeMapper.deactivateEmployee(id); -> 함수 구현 필요…

        return true;
    }

}
