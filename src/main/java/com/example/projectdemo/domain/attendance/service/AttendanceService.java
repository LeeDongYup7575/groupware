package com.example.projectdemo.domain.attendance.service;

import com.example.projectdemo.domain.attendance.entity.Attendance;
import com.example.projectdemo.domain.attendance.enums.AttendanceStatus;
import com.example.projectdemo.domain.attendance.mapper.AttendanceMapper;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeeMapper;

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
    private final EmployeeMapper employeeMapper;

    private static final LocalTime STANDARD_START_TIME = LocalTime.of(9, 0); // 9:00 AM
    private static final LocalTime LATE_TIME = LocalTime.of(9, 30); // 9:30 AM
    private static final LocalTime STANDARD_END_TIME = LocalTime.of(18, 0); // 6:00 PM

    /**
     * Record check-in for an employee
     */
    @Transactional
    public Attendance recordCheckIn(Integer empId) {
        // Get employee by ID
        EmployeesDTO employee = employeeMapper.findById(empId);
        if (employee == null) {
            throw new RuntimeException("Employee not found with id: " + empId);
        }

        // Get current date and time
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Check if attendance record already exists for today
        Attendance attendance = attendanceMapper.getAttendanceByEmployeeAndDate(empId, today);

        // Determine attendance status based on check-in time
        String status;
        if (now.isBefore(LATE_TIME)) {
            status = AttendanceStatus.NORMAL.getStatus();
        } else {
            status = AttendanceStatus.LATE.getStatus();
        }

        if (attendance == null) {
            // Create new attendance record
            attendance = Attendance.builder()
                    .empId(empId)
                    .workDate(today)
                    .checkIn(now)
                    .status(status)
                    .workHours(BigDecimal.ZERO) // Initialize with zero
                    .build();

            attendanceMapper.insertAttendance(attendance);

            // Update employee attendance status
            employee.setAttendStatus(status);
            employeeMapper.updateAttendStatus(employee.getId(), status);
        } else {
            // Update existing attendance record if not already checked in
            if (attendance.getCheckIn() == null) {
                attendance.setCheckIn(now);
                attendance.setStatus(status);
                attendanceMapper.updateAttendance(attendance);

                // Update employee attendance status
                employee.setAttendStatus(status);
                employeeMapper.updateAttendStatus(employee.getId(), status);
            }
        }

        return attendance;
    }

    /**
     * Record check-out for an employee
     */
    @Transactional
    public Attendance recordCheckOut(Integer empId) {
        // Get employee by ID
        EmployeesDTO employee = employeeMapper.findById(empId);
        if (employee == null) {
            throw new RuntimeException("Employee not found with id: " + empId);
        }

        // Get current date and time
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Check if attendance record exists for today
        Attendance attendance = attendanceMapper.getAttendanceByEmployeeAndDate(empId, today);

        if (attendance == null) {
            throw new RuntimeException("No check-in record found for today");
        }

        // Determine status based on check-out time
        String status = AttendanceStatus.CHECKOUT.getStatus();
        if (now.isBefore(STANDARD_END_TIME)) {
            status = AttendanceStatus.EARLY_LEAVE.getStatus();
        }

        // Calculate work hours
        BigDecimal workHours = BigDecimal.ZERO;
        if (attendance.getCheckIn() != null) {
            long minutes = attendance.getCheckIn().until(now, ChronoUnit.MINUTES);
            double hours = minutes / 60.0;
            workHours = BigDecimal.valueOf(hours);
        }

        // Update attendance record
        attendance.setCheckOut(now);
        attendance.setStatus(status);
        attendance.setWorkHours(workHours);
        attendanceMapper.updateAttendance(attendance);

        // Update employee attendance status
        employee.setAttendStatus(status);
        employeeMapper.updateAttendStatus(employee.getId(), status);

        return attendance;
    }

    /**
     * Get attendance record by employee ID and date
     */
    public Attendance getAttendanceByEmployeeAndDate(Integer empId, LocalDate date) {
        return attendanceMapper.getAttendanceByEmployeeAndDate(empId, date);
    }

    /**
     * Get attendance history by employee ID
     */
    public List<Attendance> getAttendanceHistoryByEmployeeId(Integer empId) {
        return attendanceMapper.getAttendanceHistoryByEmployeeId(empId);
    }

    /**
     * Get attendance history by employee ID and date range
     */
    public List<Attendance> getAttendanceHistoryByEmployeeIdAndDateRange(
            Integer empId, LocalDate startDate, LocalDate endDate) {
        return attendanceMapper.getAttendanceHistoryByEmployeeIdAndDateRange(empId, startDate, endDate);
    }

    /**
     * Process attendance by ID
     */
    @Transactional
    public Attendance processAttendanceById(Integer empId, String attendanceType) {
        switch (attendanceType) {
            case "NORMAL":
            case "LATE":
                return recordCheckIn(empId);
            case "CHECKOUT":
            case "EARLY_LEAVE":
                return recordCheckOut(empId);
            default:
                throw new IllegalArgumentException("Invalid attendance type: " + attendanceType);
        }
    }

    /**
     * Process attendance by employee number
     */
    @Transactional
    public Attendance processAttendanceByEmpNum(String empNum, String attendanceType) {
        // Find employee by employee number (DTO 반환)
        EmployeesDTO employeeDTO = employeeMapper.findByEmpNum(empNum);
        if (employeeDTO == null) {
            throw new RuntimeException("Employee not found with employee number: " + empNum);
        }

        // DTO의 ID를 사용하여 처리
        return processAttendanceById(employeeDTO.getId(), attendanceType);
    }

    /**
     * Process attendance from QR code scan
     */
    @Transactional
    public Attendance processQRAttendance(String empNum, String attendanceType) {
        return processAttendanceByEmpNum(empNum, attendanceType);
    }
}