package com.example.projectdemo.domain.admin.controller;

import com.example.projectdemo.domain.booking.dto.MeetingRoomBookingDTO;
import com.example.projectdemo.domain.booking.dto.SuppliesBookingDTO;
import com.example.projectdemo.domain.booking.service.MeetingRoomService;
import com.example.projectdemo.domain.booking.service.SuppliesService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 전용 API 컨트롤러
 * 모든 API 요청은 ROLE_ADMIN 권한을 가진 사용자만 접근 가능
 * (AuthorizationInterceptor에서 권한 체크)
 */
@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final EmployeesService employeesService;
    private final MeetingRoomService meetingRoomService;
    private final SuppliesService suppliesService;

    @Autowired
    public AdminApiController(
            EmployeesService employeesService,
            MeetingRoomService meetingRoomService,
            SuppliesService suppliesService) {
        this.employeesService = employeesService;
        this.meetingRoomService = meetingRoomService;
        this.suppliesService = suppliesService;
    }

    /**
     * 관리자 대시보드 메인 데이터
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData(HttpServletRequest request) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "인증 정보가 없습니다."
            ));
        }

        // 사원번호로 직원 정보 조회 (관리자 확인)
        EmployeesDTO admin = employeesService.findByEmpNum(empNum);

        if (admin == null || !"ROLE_ADMIN".equals(admin.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "관리자 권한이 없습니다."
            ));
        }

        // 대시보드 데이터 수집
        Map<String, Object> dashboardData = new HashMap<>();

        // 1. 전체 직원 수
        List<EmployeesDTO> allEmployees = employeesService.getAllEmployees();
        dashboardData.put("totalEmployees", allEmployees.size());

        // 2. 오늘 예약된 회의실 수
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        List<MeetingRoomBookingDTO> todayBookings = meetingRoomService.getBookingsByDateRange(startOfDay, endOfDay);
        dashboardData.put("todayMeetingRoomBookings", todayBookings.size());

        // 3. 오늘 예약된 비품 수
        List<SuppliesBookingDTO> todaySuppliesBookings = suppliesService.getBookingsByDateRange(startOfDay, endOfDay);
        dashboardData.put("todaySuppliesBookings", todaySuppliesBookings.size());

        // 4. 최근 가입 직원 (최대 5명)
        // 실제 구현 시 EmployeesService에 최근 가입 직원 조회 메서드 추가 필요
        dashboardData.put("recentEmployees", allEmployees.stream()
                .filter(EmployeesDTO::isRegistered)
                .sorted((e1, e2) -> e2.getLastLogin().compareTo(e1.getLastLogin()))
                .limit(5)
                .toList());

        return ResponseEntity.ok(dashboardData);
    }

    /**
     * 전체 임직원 목록 조회
     */
    @GetMapping("/employees/list")
    public ResponseEntity<List<EmployeesDTO>> getAllEmployees() {
        List<EmployeesDTO> employees = employeesService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    /**
     * 특정 임직원 상세 정보 조회
     */
    @GetMapping("/employees/details/{id}")
    public ResponseEntity<EmployeesDTO> getEmployeeDetails(@PathVariable Integer id) {
        EmployeesDTO employee = employeesService.findById(id);

        if (employee == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(employee);
    }

    /**
     * 임직원 정보 수정
     */
    @PutMapping("/employees/details/{id}")
    public ResponseEntity<EmployeesDTO> updateEmployee(
            @PathVariable Integer id,
            @RequestBody EmployeesDTO updatedEmployee) {

        // ID 확인
        if (!id.equals(updatedEmployee.getId())) {
            return ResponseEntity.badRequest().build();
        }

        // 직원 존재 확인
        EmployeesDTO existingEmployee = employeesService.findById(id);
        if (existingEmployee == null) {
            return ResponseEntity.notFound().build();
        }

        // 직원 정보 업데이트 (서비스 메서드 구현 필요)
        EmployeesDTO updatedEmployeeResult = employeesService.updateEmployee(updatedEmployee);

        return ResponseEntity.ok(updatedEmployeeResult);
    }

    /**
     * 임직원 비활성화 (삭제 대신 비활성화 처리)
     */
    @DeleteMapping("/employees/details/{id}")
    public ResponseEntity<Map<String, Object>> deactivateEmployee(@PathVariable Integer id) {
        // 직원 존재 확인
        EmployeesDTO existingEmployee = employeesService.findById(id);
        if (existingEmployee == null) {
            return ResponseEntity.notFound().build();
        }

        // 직원 비활성화 (서비스 메서드 구현 필요)
        boolean result = employeesService.deactivateEmployee(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "임직원 비활성화 처리가 완료되었습니다." : "임직원 비활성화 처리 중 오류가 발생했습니다.");

        return ResponseEntity.ok(response);
    }

    /**
     * 모든 회의실/비품 예약 목록 조회
     */
    @GetMapping("/booking/list")
    public ResponseEntity<Map<String, Object>> getAllBookings() {
        Map<String, Object> bookings = new HashMap<>();

        // 모든 회의실 예약 조회
        List<MeetingRoomBookingDTO> meetingRoomBookings = meetingRoomService.getAllBookings();
        bookings.put("meetingRoomBookings", meetingRoomBookings);

        // 모든 비품 예약 조회
        List<SuppliesBookingDTO> suppliesBookings = suppliesService.getAllBookings();
        bookings.put("suppliesBookings", suppliesBookings);

        return ResponseEntity.ok(bookings);
    }

    /**
     * 회의실 예약 삭제
     */
    @DeleteMapping("/booking/meeting-room/{id}")
    public ResponseEntity<Map<String, Object>> deleteMeetingRoomBooking(@PathVariable Integer id) {
        boolean result = meetingRoomService.cancelBooking(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "회의실 예약이 취소되었습니다." : "회의실 예약 취소 중 오류가 발생했습니다.");

        return ResponseEntity.ok(response);
    }

    /**
     * 비품 예약 삭제
     */
    @DeleteMapping("/booking/supplies/{id}")
    public ResponseEntity<Map<String, Object>> deleteSuppliesBooking(@PathVariable Integer id) {
        boolean result = suppliesService.cancelBooking(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "비품 예약이 취소되었습니다." : "비품 예약 취소 중 오류가 발생했습니다.");

        return ResponseEntity.ok(response);
    }
}