//package com.example.projectdemo.domain.attendance.controller;
//
//import com.example.projectdemo.domain.attendance.entity.Attendance;
//import com.example.projectdemo.domain.attendance.service.AttendanceService;
//import com.example.projectdemo.domain.attendance.util.QRTokenUtil;
//import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
//import com.example.projectdemo.domain.employees.service.EmployeesService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
////app과의 token 검증/qr scan
//@RestController
//@RequestMapping("/api/attendance")
//public class QRApiController {
//
//    @Autowired
//    private QRTokenUtil qrTokenUtil;
//
//    @Autowired
//    private EmployeesService employeeService;
//
//    @Autowired
//    private AttendanceService attendanceService;
//
////scan 시 출석 -> attendance와 employees table 모두 반영해야 함.
//    @PostMapping("/scan")
//    public ResponseEntity<?> processQRScan(@RequestBody Map<String, String> request) {
//        String token = request.get("token");
//
//        if (token == null) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", "Token is required");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        try {
//            //qr token 체크.../검증~
//            if (!qrTokenUtil.validateQRToken(token)) {
//                Map<String, Object> response = new HashMap<>();
//                response.put("success", false);
//                response.put("message", "Invalid or expired QR code");
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//            }
//
//            String empNum = qrTokenUtil.extractEmpNum(token);
//            String attendanceType = qrTokenUtil.getAttendanceTypeFromToken(token);
//
//            EmployeesDTO employee = employeeService.findByEmpNum(empNum);
//            if (employee == null) {
//                Map<String, Object> response = new HashMap<>();
//                response.put("success", false);
//                response.put("message", "Employee not found");
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            }
//
//            Attendance attendance = attendanceService.processQRAttendance(empNum, attendanceType);
//
//            if (attendance == null) {
//                // 적절한 오류 응답 반환
//                Map<String, Object> errorResponse = new HashMap<>();
//                errorResponse.put("success", false);
//                errorResponse.put("message", "Unable to process attendance");
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//            }
//
//            Map<String, Object> attendanceData = new HashMap<>();
//            attendanceData.put("empNum", empNum);
//            attendanceData.put("name", employee.getName());
//            attendanceData.put("attendanceType", attendanceType);
//            attendanceData.put("status", attendance.getStatus());
//
//            if (attendanceType.equals("CHECKOUT") || attendanceType.equals("EARLY_LEAVE")) {
//                attendanceData.put("checkoutTime", attendance.getCheckOut().toString());
//            } else {
//                String checkInTime = (attendance.getCheckIn() != null) ?
//                        attendance.getCheckIn().toString() :
//                        "미체크인";
//                attendanceData.put("checkinTime", checkInTime);
//            }
//
//            attendanceData.put("workDate", attendance.getWorkDate().toString());
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "Attendance recorded successfully");
//            response.put("data", attendanceData);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", "Error processing QR code: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    @GetMapping("/status/{empNum}")
//    public ResponseEntity<?> getAttendanceStatus(@PathVariable String empNum) {
//        try {
//            EmployeesDTO employee = employeeService.findByEmpNum(empNum);
//            if (employee == null) {
//                Map<String, Object> response = new HashMap<>();
//                response.put("success", false);
//                response.put("message", "Employee not found");
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            }
//
//            Attendance attendance = attendanceService.getAttendanceByEmployeeAndDate(employee.getId(), java.time.LocalDate.now());
//
//            Map<String, Object> attendanceData = new HashMap<>();
//            attendanceData.put("empNum", empNum);
//            attendanceData.put("name", employee.getName());
//            attendanceData.put("attendStatus", employee.getAttendStatus());
//
//            if (attendance != null) {
//                attendanceData.put("workDate", attendance.getWorkDate().toString());
//                attendanceData.put("checkIn", attendance.getCheckIn() != null ? attendance.getCheckIn().toString() : null);
//                attendanceData.put("checkOut", attendance.getCheckOut() != null ? attendance.getCheckOut().toString() : null);
//                attendanceData.put("status", attendance.getStatus());
//                attendanceData.put("workHours", attendance.getWorkHours());
//            }
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("data", attendanceData);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", "Error getting attendance status: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * Get employee's attendance history for a specific month
//     */
//    @GetMapping("/history/{empNum}")
//    public ResponseEntity<?> getAttendanceHistory(
//            @PathVariable String empNum,
//            @RequestParam(required = false) String month) {
//        try {
//            EmployeesDTO employee = employeeService.findByEmpNum(empNum);
//            if (employee == null) {
//                Map<String, Object> response = new HashMap<>();
//                response.put("success", false);
//                response.put("message", "Employee not found");
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            }
//
//            java.time.LocalDate startDate;
//            java.time.LocalDate endDate;
//
//            if (month != null && !month.isEmpty()) {
//                java.time.YearMonth yearMonth = java.time.YearMonth.parse(month);
//                startDate = yearMonth.atDay(1);
//                endDate = yearMonth.atEndOfMonth();
//            } else {
//                java.time.YearMonth currentMonth = java.time.YearMonth.now();
//                startDate = currentMonth.atDay(1);
//                endDate = currentMonth.atEndOfMonth();
//            }
//
//            // 대체 구현: 개별 날짜마다 출퇴근 기록 조회
//            List<Map<String, Object>> attendanceData = new ArrayList<>();
//            java.time.LocalDate currentDate = startDate;
//
//            while (!currentDate.isAfter(endDate)) {
//                // 해당 날짜의 출퇴근 기록 조회
//                Attendance attendance = attendanceService.getAttendanceByEmployeeAndDate(employee.getId(), currentDate);
//
//                if (attendance != null) {
//                    Map<String, Object> record = new HashMap<>();
//                    record.put("workDate", attendance.getWorkDate().toString());
//                    record.put("checkIn", attendance.getCheckIn() != null ? attendance.getCheckIn().toString() : null);
//                    record.put("checkOut", attendance.getCheckOut() != null ? attendance.getCheckOut().toString() : null);
//                    record.put("status", attendance.getStatus());
//                    record.put("workHours", attendance.getWorkHours());
//                    attendanceData.add(record);
//                }
//
//                // 다음 날짜로 이동
//                currentDate = currentDate.plusDays(1);
//            }
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("data", attendanceData);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", "Error getting attendance history: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//}

package com.example.projectdemo.domain.attendance.controller;

import com.example.projectdemo.domain.attendance.entity.Attendance;
import com.example.projectdemo.domain.attendance.service.AttendanceService;
import com.example.projectdemo.domain.attendance.util.QRTokenUtil;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//app과의 token 검증/qr scan
@RestController
@RequestMapping("/api/attendance")
public class QRApiController {

    @Autowired
    private QRTokenUtil qrTokenUtil;

    @Autowired
    private EmployeesService employeeService;

    @Autowired
    private AttendanceService attendanceService;

    /**
     * Process QR code scan for attendance
     */
    @PostMapping("/scan")
    public ResponseEntity<?> processQRScan(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (token == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Token is required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            //qr token 체크.../검증~
            if (!qrTokenUtil.validateQRToken(token)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid or expired QR code");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String empNum = qrTokenUtil.extractEmpNum(token);
            String attendanceType = qrTokenUtil.getAttendanceTypeFromToken(token);

            EmployeesDTO employee = employeeService.findByEmpNum(empNum);
            if (employee == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Employee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // 출퇴근 처리 - processQRAttendance 메서드가 employees 테이블도 함께 업데이트함
            Attendance attendance = attendanceService.processQRAttendance(empNum, attendanceType);

            if (attendance == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Unable to process attendance");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }

            // 처리 결과 확인을 위해 업데이트된 직원 정보 다시 조회
            employee = employeeService.findByEmpNum(empNum);

            Map<String, Object> attendanceData = new HashMap<>();
            attendanceData.put("empNum", empNum);
            attendanceData.put("name", employee.getName());
            attendanceData.put("attendanceType", attendanceType);
            attendanceData.put("status", attendance.getStatus());
            attendanceData.put("employeeStatus", employee.getAttendStatus()); // 직원 테이블의 상태도 함께 제공

            if (attendanceType.equals("CHECKOUT") || attendanceType.equals("EARLY_LEAVE") ||
                    attendanceType.equals("퇴근") || attendanceType.equals("조퇴")) {
                attendanceData.put("checkoutTime", attendance.getCheckOut().toString());
            } else {
                String checkInTime = (attendance.getCheckIn() != null) ?
                        attendance.getCheckIn().toString() :
                        "미체크인";
                attendanceData.put("checkinTime", checkInTime);
            }

            attendanceData.put("workDate", attendance.getWorkDate().toString());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Attendance recorded successfully");
            response.put("data", attendanceData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error processing QR code: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/status/{empNum}")
    public ResponseEntity<?> getAttendanceStatus(@PathVariable String empNum) {
        try {
            EmployeesDTO employee = employeeService.findByEmpNum(empNum);
            if (employee == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Employee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Attendance attendance = attendanceService.getAttendanceByEmployeeAndDate(employee.getId(), java.time.LocalDate.now());

            Map<String, Object> attendanceData = new HashMap<>();
            attendanceData.put("empNum", empNum);
            attendanceData.put("name", employee.getName());
            attendanceData.put("attendStatus", employee.getAttendStatus());

            if (attendance != null) {
                attendanceData.put("workDate", attendance.getWorkDate().toString());
                attendanceData.put("checkIn", attendance.getCheckIn() != null ? attendance.getCheckIn().toString() : null);
                attendanceData.put("checkOut", attendance.getCheckOut() != null ? attendance.getCheckOut().toString() : null);
                attendanceData.put("status", attendance.getStatus());
                attendanceData.put("workHours", attendance.getWorkHours());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", attendanceData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error getting attendance status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get employee's attendance history for a specific month
     */
    @GetMapping("/history/{empNum}")
    public ResponseEntity<?> getAttendanceHistory(
            @PathVariable String empNum,
            @RequestParam(required = false) String month) {
        try {
            EmployeesDTO employee = employeeService.findByEmpNum(empNum);
            if (employee == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Employee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            java.time.LocalDate startDate;
            java.time.LocalDate endDate;

            if (month != null && !month.isEmpty()) {
                java.time.YearMonth yearMonth = java.time.YearMonth.parse(month);
                startDate = yearMonth.atDay(1);
                endDate = yearMonth.atEndOfMonth();
            } else {
                java.time.YearMonth currentMonth = java.time.YearMonth.now();
                startDate = currentMonth.atDay(1);
                endDate = currentMonth.atEndOfMonth();
            }

            // 해당 기간의 출퇴근 기록 조회
            List<Attendance> attendanceHistory = attendanceService.getAttendanceHistoryByEmployeeIdAndDateRange(
                    employee.getId(), startDate, endDate);

            List<Map<String, Object>> attendanceData = new ArrayList<>();
            for (Attendance attendance : attendanceHistory) {
                Map<String, Object> record = new HashMap<>();
                record.put("workDate", attendance.getWorkDate().toString());
                record.put("checkIn", attendance.getCheckIn() != null ? attendance.getCheckIn().toString() : null);
                record.put("checkOut", attendance.getCheckOut() != null ? attendance.getCheckOut().toString() : null);
                record.put("status", attendance.getStatus());
                record.put("workHours", attendance.getWorkHours());
                attendanceData.add(record);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", attendanceData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error getting attendance history: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}