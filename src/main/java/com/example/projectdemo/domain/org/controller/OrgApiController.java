package com.example.projectdemo.domain.org.controller;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.DepartmentsMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/org")
public class OrgApiController {

    @Autowired
    private EmployeesService employeesService;

    @Autowired
    private DepartmentsMapper departmentsMapper;

    /**
     * 활성화된 모든 직원 정보 조회
     */
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeesDTO>> getAllEmployees() {
        List<EmployeesDTO> employees = employeesService.getAllActiveEmployees();
        return ResponseEntity.ok(employees);
    }

    /**
     * 부서별 직원 정보 조회
     */
    @GetMapping("/department/{deptId}")
    public ResponseEntity<Map<String, Object>> getDepartmentEmployees(@PathVariable Integer deptId) {
        // 모든 직원 목록 가져오기
        List<EmployeesDTO> allEmployees = employeesService.getAllActiveEmployees();

        // 부서별 필터링
        List<EmployeesDTO> departmentEmployees = allEmployees.stream()
                .filter(emp -> emp.getDepId().equals(deptId))
                .collect(Collectors.toList());

        // 부서 이름 가져오기
        String departmentName = departmentsMapper.findById(deptId).getName();

        Map<String, Object> result = new HashMap<>();
        result.put("departmentName", departmentName);
        result.put("employees", departmentEmployees);

        return ResponseEntity.ok(result);
    }

    /**
     * 직원 상세 정보 조회
     */
    @GetMapping("/employee/{empId}")
    public ResponseEntity<EmployeesDTO> getEmployeeDetail(@PathVariable Integer empId) {
        EmployeesDTO employee = employeesService.findById(empId);
        return ResponseEntity.ok(employee);
    }


    /**
     * 직원 검색
     */
    @GetMapping("/search")
    public ResponseEntity<List<EmployeesDTO>> searchEmployees(@RequestParam String query) {
        // 모든 직원 가져오기
        List<EmployeesDTO> allEmployees = employeesService.getAllActiveEmployees();

        // 검색어 필터링 (이름, 부서명, 직급명으로 검색)
        String queryLower = query.toLowerCase();
        List<EmployeesDTO> searchResults = allEmployees.stream()
                .filter(emp ->
                        (emp.getName() != null && emp.getName().toLowerCase().contains(queryLower)) ||
                                (emp.getDepartmentName() != null && emp.getDepartmentName().toLowerCase().contains(queryLower)) ||
                                (emp.getPositionTitle() != null && emp.getPositionTitle().toLowerCase().contains(queryLower)) ||
                                (emp.getEmail() != null && emp.getEmail().toLowerCase().contains(queryLower)) ||
                                (emp.getPhone() != null && emp.getPhone().contains(query))
                )
                .collect(Collectors.toList());

        return ResponseEntity.ok(searchResults);
    }
}
