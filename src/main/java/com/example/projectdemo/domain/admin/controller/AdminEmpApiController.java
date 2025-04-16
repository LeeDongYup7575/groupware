package com.example.projectdemo.domain.admin.controller;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.dto.DepartmentsDTO;
import com.example.projectdemo.domain.employees.dto.PositionsDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.employees.service.DepartmentsService;
import com.example.projectdemo.domain.employees.service.PositionsService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/employees")
public class AdminEmpApiController {

    private final EmployeesService employeesService;
    private final DepartmentsService departmentsService;
    private final PositionsService positionsService;

    @Autowired
    public AdminEmpApiController(
            EmployeesService employeesService,
            DepartmentsService departmentsService,
            PositionsService positionsService) {
        this.employeesService = employeesService;
        this.departmentsService = departmentsService;
        this.positionsService = positionsService;
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getEmployeeList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Integer deptId,
            @RequestParam(required = false) Integer posId) {

        // Fetch employees with pagination and filters
        List<EmployeesDTO> employees = employeesService.getEmployeesWithFilters(page, size, searchTerm, deptId, posId);

        // Calculate total pages
        int totalEmployees = employeesService.countEmployeesWithFilters(searchTerm, deptId, posId);
        int totalPages = (int) Math.ceil((double) totalEmployees / size);

        Map<String, Object> response = new HashMap<>();
        response.put("employees", employees);
        response.put("currentPage", page);
        response.put("totalItems", totalEmployees);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<EmployeesDTO> getEmployeeDetails(@PathVariable Integer id) {
        EmployeesDTO employee = employeesService.findById(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/details/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable Integer id,
            @RequestBody EmployeesDTO employeeDTO,
            HttpServletRequest request) {

        // 권한 확인 (선택적)
        String role = (String) request.getAttribute("role");
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "관리자 권한이 필요합니다.", "success", false));
        }

        try {
            employeeDTO.setId(id); // ID 설정
            EmployeesDTO updatedEmployee = employeesService.updateEmployee(employeeDTO);
            return ResponseEntity.ok(updatedEmployee);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage(), "success", false));
        }
    }

    @DeleteMapping("/details/{id}")
    public ResponseEntity<?> deactivateEmployee(
            @PathVariable Integer id,
            HttpServletRequest request) {

        // 권한 확인 (선택적)
        String role = (String) request.getAttribute("role");
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "관리자 권한이 필요합니다.", "success", false));
        }

        try {
            boolean result = employeesService.deactivateEmployee(id);
            return ResponseEntity.ok(Map.of("success", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage(), "success", false));
        }
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentsDTO>> getAllDepartments() {
        List<DepartmentsDTO> departments = departmentsService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/positions")
    public ResponseEntity<List<PositionsDTO>> getAllPositions() {
        List<PositionsDTO> positions = positionsService.getAllPositions();
        return ResponseEntity.ok(positions);
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<?> activateEmployee(
            @PathVariable Integer id,
            HttpServletRequest request) {

        // 권한 확인 (선택적)
        String role = (String) request.getAttribute("role");
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "관리자 권한이 필요합니다.", "success", false));
        }

        try {
            boolean result = employeesService.activateEmployee(id);
            return ResponseEntity.ok(Map.of("success", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage(), "success", false));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEmployee(
            @RequestBody EmployeesDTO employeeDTO,
            HttpServletRequest request) {

        // 권한 확인
        String role = (String) request.getAttribute("role");
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "관리자 권한이 필요합니다.", "success", false));
        }

        try {
            // 필수 필드 검증
            if (employeeDTO.getName() == null || employeeDTO.getName().trim().isEmpty() ||
                    employeeDTO.getEmpNum() == null || employeeDTO.getEmpNum().trim().isEmpty() ||
                    employeeDTO.getEmail() == null || employeeDTO.getEmail().trim().isEmpty() ||
                    employeeDTO.getSsn() == null || employeeDTO.getSsn().trim().isEmpty() ||
                    employeeDTO.getDepId() == null || employeeDTO.getPosId() == null ||
                    employeeDTO.getGender() == null || employeeDTO.getGender().trim().isEmpty()) {

                return ResponseEntity.badRequest()
                        .body(Map.of("message", "필수 항목이 누락되었습니다.", "success", false));
            }

            // 사원번호 중복 확인
            EmployeesDTO existingEmployee = employeesService.findByEmpNum(employeeDTO.getEmpNum());
            if (existingEmployee != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "이미 존재하는 사원번호입니다.", "success", false));
            }

            // 기본값 설정
            employeeDTO.setRegistered(false);
            employeeDTO.setEnabled(true);

            // 서비스를 통해 직원 추가 (서비스에 해당 메서드 추가 필요)
            EmployeesDTO addedEmployee = employeesService.addEmployee(employeeDTO);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "직원이 성공적으로 추가되었습니다.",
                    "employee", addedEmployee
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage(), "success", false));
        }
    }


}