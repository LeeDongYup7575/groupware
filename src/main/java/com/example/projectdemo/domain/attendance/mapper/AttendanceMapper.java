package com.example.projectdemo.domain.attendance.mapper;

import com.example.projectdemo.domain.attendance.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface AttendanceMapper {

    // Insert new attendance record
    void insertAttendance(Attendance attendance);

    // Update existing attendance record
    void updateAttendance(Attendance attendance);

    // Get attendance by ID
    Attendance getAttendanceById(@Param("id") Integer id);

    // Get attendance by employee ID and date
    Attendance getAttendanceByEmployeeAndDate(@Param("empId") Integer empId, @Param("workDate") LocalDate workDate);

    // Get attendance history by employee ID
    List<Attendance> getAttendanceHistoryByEmployeeId(@Param("empId") Integer empId);

    // Get attendance history by employee ID and date range
    List<Attendance> getAttendanceHistoryByEmployeeIdAndDateRange(
            @Param("empId") Integer empId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Update attendance status
    void updateAttendanceStatus(
            @Param("id") Integer id,
            @Param("status") String status);

    // Update check-in time
    void updateCheckIn(
            @Param("id") Integer id,
            @Param("checkIn") LocalTime checkIn);

    // Update check-out time
    void updateCheckOut(
            @Param("id") Integer id,
            @Param("checkOut") LocalTime checkOut,
            @Param("workHours") java.math.BigDecimal workHours);

    // Calculate work hours
    void calculateWorkHours(@Param("id") Integer id);
}