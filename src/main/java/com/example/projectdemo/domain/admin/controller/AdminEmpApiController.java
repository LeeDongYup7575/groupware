package com.example.projectdemo.domain.admin.controller;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.dto.DepartmentsDTO;
import com.example.projectdemo.domain.employees.dto.PositionsDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.employees.service.DepartmentsService;
import com.example.projectdemo.domain.employees.service.PositionsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<EmployeesDTO> updateEmployee(
            @PathVariable Integer id,
            @RequestBody EmployeesDTO employeeDTO) {

        employeeDTO.setId(id); // Ensure ID is set correctly
        EmployeesDTO updatedEmployee = employeesService.updateEmployee(employeeDTO);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/details/{id}")
    public ResponseEntity<Map<String, Object>> deactivateEmployee(@PathVariable Integer id) {
        boolean result = employeesService.deactivateEmployee(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result);

        return ResponseEntity.ok(response);
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
}