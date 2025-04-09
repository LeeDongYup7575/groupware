package com.example.projectdemo.domain.admin.controller;

import com.example.projectdemo.domain.booking.dto.MeetingRoomBookingDTO;
import com.example.projectdemo.domain.booking.dto.SuppliesBookingDTO;
import com.example.projectdemo.domain.booking.service.MeetingRoomService;
import com.example.projectdemo.domain.booking.service.SuppliesService;
import com.example.projectdemo.domain.employees.dto.DepartmentsDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.dto.PositionsDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.DepartmentsService;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.employees.service.PositionsService;
import com.example.projectdemo.domain.employees.service.TmpEmployeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//tmpEmployee로 가져온 부분은 모두 수정 필요
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private EmployeesService employeesService;

    @Autowired
    private TmpEmployeesService tmpEmployeesService;

    @Autowired
    private DepartmentsService departmentsService;

    @Autowired
    private PositionsService positionsService;

    @Autowired
    private MeetingRoomService meetingRoomService;

    @Autowired
    private SuppliesService suppliesService;
    @Autowired
    private EmployeesMapper employeesMapper;

    @GetMapping("/check")
    public ResponseEntity<Object> checkAdminAccess() {
        // 인증 및 권한 검사는 인터셉터에서 처리됨
        // 이 지점에 도달하면 이미 ADMIN 권한이 확인된 상태
        return ResponseEntity.ok().body(Map.of("status", "success", "message", "Admin access granted"));
    }

    // 대시보드 데이터 제공
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        try {
            Map<String, Object> dashboardData = new HashMap<>();

            // 임직원 총원 - 나중에 employeeService로 수정
//            List<EmployeesDTO> employees = employeesService.selectEmpAll();
            List<EmployeesDTO> employees =  tmpEmployeesService.getAllEmployees();
            int totalEmployees = employees.size();

            // 오늘의 회의실 예약 현황
            LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            List<MeetingRoomBookingDTO> todayMeetingBookings =
                    meetingRoomService.getBookingsByDateRange(startOfDay, endOfDay);

            // 오늘의 비품 예약 현황
            List<SuppliesBookingDTO> todaySuppliesBookings =
                    suppliesService.getBookingsByDateRange(startOfDay, endOfDay);

            // 대시보드 데이터 설정
            dashboardData.put("totalEmployees", totalEmployees);
            dashboardData.put("todayMeetingBookings", todayMeetingBookings.size());
            dashboardData.put("todaySuppliesBookings", todaySuppliesBookings.size());

            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "대시보드 데이터를 불러오는 중 오류가 발생했습니다.");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 모든 임직원 목록 조회
    @GetMapping("/employees/list")
    public ResponseEntity<List<EmployeesDTO>> getAllEmployees() {
//        List<EmployeesDTO> employees = employeesService.selectAllEmp();
        List<EmployeesDTO> employees =  tmpEmployeesService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    // 부서별 임직원 목록 조회
    @GetMapping("/employees/list/department/{depId}")
    public ResponseEntity<List<EmployeesDTO>> getEmployeesByDepartment(@PathVariable Integer depId) {
//        List<EmployeesDTO> employees = employeesService.getDepartmentById(depId);
        List<EmployeesDTO> employees =  tmpEmployeesService.getEmployeesByDepartment(depId);
        return ResponseEntity.ok(employees);
    }

    // 부서 목록 조회
    @GetMapping("/departments/list")
    public ResponseEntity<List<DepartmentsDTO>> getAllDepartments() {
        List<DepartmentsDTO> departments = departmentsService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    // 직급 목록 조회
    @GetMapping("/positions/list")
    public ResponseEntity<List<PositionsDTO>> getAllPositions() {
        List<PositionsDTO> positions = positionsService.getAllPositions();
        return ResponseEntity.ok(positions);
    }

    // 임직원 상세 정보 조회
    @GetMapping("/employees/details/{id}")
    public ResponseEntity<EmployeesDTO> getEmployeeDetails(@PathVariable Integer id) {
        try {
//            EmployeesDTO employee = employeesService.findById(id);
            //mapper대신 service에 함수 생성하기
            EmployeesDTO employee =  employeesMapper.findById(id);
            if (employee == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 임직원 정보 수정
    @PutMapping("/employees/update/{id}")
    public ResponseEntity<EmployeesDTO> updateEmployee(
            @PathVariable Integer id,
            @RequestBody EmployeesDTO employeesDTO) {
        try {
            // ID 확인 및 설정
            employeesDTO.setId(id);
            EmployeesDTO updatedEmployee = tmpEmployeesService.updateEmployee(employeesDTO);
            return ResponseEntity.ok(updatedEmployee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 임직원 삭제
    @DeleteMapping("/employees/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteEmployee(@PathVariable Integer id) {
        try {
            boolean result = tmpEmployeesService.deleteEmployee(id);
            if (result) {
                return ResponseEntity.ok(Map.of("status", "success", "message", "직원이 성공적으로 삭제되었습니다."));
            } else {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "직원 삭제에 실패했습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "직원 삭제 중 오류가 발생했습니다."));
        }
    }

    // 회의실 예약 목록 조회
    @GetMapping("/booking/meeting-rooms")
    public ResponseEntity<List<MeetingRoomBookingDTO>> getAllMeetingRoomBookings() {
        try {
            List<MeetingRoomBookingDTO> bookings = meetingRoomService.getAllBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 비품 예약 목록 조회
    @GetMapping("/booking/supplies")
    public ResponseEntity<List<SuppliesBookingDTO>> getAllSuppliesBookings() {
        try {
            List<SuppliesBookingDTO> bookings = suppliesService.getAllBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 회의실 예약 취소
    @DeleteMapping("/booking/meeting-room/{id}")
    public ResponseEntity<Map<String, String>> cancelMeetingRoomBooking(@PathVariable Integer id) {
        try {
            boolean result = meetingRoomService.cancelBooking(id);
            if (result) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "회의실 예약이 성공적으로 취소되었습니다."));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "회의실 예약 취소에 실패했습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "회의실 예약 취소 중 오류가 발생했습니다."));
        }
    }

    // 비품 예약 취소
    @DeleteMapping("/booking/supplies/{id}")
    public ResponseEntity<Map<String, String>> cancelSuppliesBooking(@PathVariable Integer id) {
        try {
            boolean result = suppliesService.cancelBooking(id);
            if (result) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "비품 예약이 성공적으로 취소되었습니다."));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "비품 예약 취소에 실패했습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "비품 예약 취소 중 오류가 발생했습니다."));
        }
    }
}