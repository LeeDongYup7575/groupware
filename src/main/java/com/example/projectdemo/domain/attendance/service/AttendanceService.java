package com.example.projectdemo.domain.attendance.service;

import com.example.projectdemo.domain.attendance.entity.Attendance;
import com.example.projectdemo.domain.attendance.enums.AttendanceStatus;
import com.example.projectdemo.domain.attendance.mapper.AttendanceMapper;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceMapper attendanceMapper;
    private final EmployeesMapper employeeMapper;

    private static final LocalTime STANDARD_START_TIME = LocalTime.of(9, 0); // 9:00 AM
    private static final LocalTime LATE_TIME = LocalTime.of(9, 30); // 9:30 AM
    private static final LocalTime STANDARD_END_TIME = LocalTime.of(18, 0); // 6:00 PM


    @Transactional
    public Attendance recordCheckIn(Integer empId) {
        EmployeesDTO employee = employeeMapper.findById(empId);
        if (employee == null) {
            throw new RuntimeException("Employee not found with id: " + empId);
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        Attendance attendance = attendanceMapper.getAttendanceByEmployeeAndDate(empId, today);


        String status;
        if (now.isBefore(LATE_TIME)) {
            status = AttendanceStatus.NORMAL.getStatus();
        } else {
            status = AttendanceStatus.LATE.getStatus();
        }

        if (attendance == null) {
            attendance = Attendance.builder()
                    .empId(empId)
                    .workDate(today)
                    .checkIn(now)
                    .status(status)
                    .workHours(BigDecimal.ZERO) // Initialize with zero
                    .build();

            attendanceMapper.insertAttendance(attendance);

            employee.setAttendStatus(status);
            employeeMapper.updateAttendStatus(employee.getId(), status);
        } else {
            if (attendance.getCheckIn() == null) {
                attendance.setCheckIn(now);
                attendance.setStatus(status);
                attendanceMapper.updateAttendance(attendance);

                employee.setAttendStatus(status);
                employeeMapper.updateAttendStatus(employee.getId(), status);
            }
        }

        return attendance;
    }


    @Transactional
    public Attendance recordCheckOut(Integer empId) {
        EmployeesDTO employee = employeeMapper.findById(empId);
        if (employee == null) {
            throw new RuntimeException("Employee not found with id: " + empId);
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        Attendance attendance = attendanceMapper.getAttendanceByEmployeeAndDate(empId, today);

        if (attendance == null) {
            throw new RuntimeException("No check-in record found for today");
        }

        String status = AttendanceStatus.CHECKOUT.getStatus();
        if (now.isBefore(STANDARD_END_TIME)) {
            status = AttendanceStatus.EARLY_LEAVE.getStatus();
        }

        BigDecimal workHours = BigDecimal.ZERO;
        if (attendance.getCheckIn() != null) {
            long minutes = attendance.getCheckIn().until(now, ChronoUnit.MINUTES);
            double hours = minutes / 60.0;
            workHours = BigDecimal.valueOf(hours);
        }

        attendance.setCheckOut(now);
        attendance.setStatus(status);
        attendance.setWorkHours(workHours);
        attendanceMapper.updateAttendance(attendance);

        employee.setAttendStatus(status);
        employeeMapper.updateAttendStatus(employee.getId(), status);

        return attendance;
    }


    public Attendance getAttendanceByEmployeeAndDate(Integer empId, LocalDate date) {
        return attendanceMapper.getAttendanceByEmployeeAndDate(empId, date);
    }


    public List<Attendance> getAttendanceHistoryByEmployeeId(Integer empId) {
        return attendanceMapper.getAttendanceHistoryByEmployeeId(empId);
    }


    public List<Attendance> getAttendanceHistoryByEmployeeIdAndDateRange(
            Integer empId, LocalDate startDate, LocalDate endDate) {
        return attendanceMapper.getAttendanceHistoryByEmployeeIdAndDateRange(empId, startDate, endDate);
    }


//    @Transactional
//    public Attendance processAttendanceById(Integer empId, String attendanceType) {
//        switch (attendanceType) {
//            case "출근":
//            case "지각":
//                return recordCheckIn(empId);
//            case "퇴근":
//            case "조퇴":
//                return recordCheckOut(empId);
//            default:
//                throw new IllegalArgumentException("Invalid attendance type: " + attendanceType);
//        }
//    }
    @Transactional
    public Attendance processAttendanceById(Integer empId, String attendanceType) {
        // enum 이름인지 먼저 확인
        try {
            AttendanceStatus status = AttendanceStatus.valueOf(attendanceType);
            attendanceType = status.getStatus(); // enum 이름이면 한글 상태값으로 변환
        } catch (IllegalArgumentException e) {
            // 이미 한글 상태값이라면 패스
        }

        // 이제 한글 상태값으로 처리
        switch (attendanceType) {
            case "출근":
            case "지각":
                return recordCheckIn(empId);
            case "퇴근":
            case "조퇴":
                return recordCheckOut(empId);
            case "미출근":
                // 미출근 상태 처리 로직...
                EmployeesDTO employee = employeeMapper.findById(empId);
                if (employee != null) {
                    employee.setAttendStatus("미출근");
                    employeeMapper.updateAttendStatus(employee.getId(), "미출근");
                }

                LocalDate today = LocalDate.now();
                Attendance attendance = attendanceMapper.getAttendanceByEmployeeAndDate(empId, today);

                if (attendance == null) {
                    attendance = Attendance.builder()
                            .empId(empId)
                            .workDate(today)
                            .status("미출근")
                            .workHours(BigDecimal.ZERO)
                            .build();

                    attendanceMapper.insertAttendance(attendance);
                }

                return attendance;
            default:
                throw new IllegalArgumentException("Invalid attendance type: " + attendanceType);
        }
    }


    @Transactional
    public Attendance processAttendanceByEmpNum(String empNum, String attendanceType) {
        EmployeesDTO employeeDTO = employeeMapper.findByEmpNum(empNum);
        if (employeeDTO == null) {
            throw new RuntimeException("Employee not found with employee number: " + empNum);
        }

        return processAttendanceById(employeeDTO.getId(), attendanceType);
    }

    @Transactional
    public Attendance processQRAttendance(String empNum, String attendanceType) {
        return processAttendanceByEmpNum(empNum, attendanceType);
    }
}