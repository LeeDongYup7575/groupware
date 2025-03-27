package com.example.projectdemo.domain.attendance.mapper;

import com.example.projectdemo.domain.attendance.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
// 클로드에게 부탁한 근태 mapper 함수 클래스 -> 실제 mybatis 로직과 연결됩니다.
@Mapper
public interface AttendanceMapper {

    void insertAttendance(Attendance attendance);

    void updateAttendance(Attendance attendance);

    Attendance getAttendanceById(@Param("id") Integer id);

    Attendance getAttendanceByEmployeeAndDate(@Param("empId") Integer empId, @Param("workDate") LocalDate workDate);

    List<Attendance> getAttendanceHistoryByEmployeeId(@Param("empId") Integer empId);

    List<Attendance> getAttendanceHistoryByEmployeeIdAndDateRange(
            @Param("empId") Integer empId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    void updateAttendanceStatus(
            @Param("id") Integer id,
            @Param("status") String status);

    void updateCheckIn(
            @Param("id") Integer id,
            @Param("checkIn") LocalTime checkIn);

    void updateCheckOut(
            @Param("id") Integer id,
            @Param("checkOut") LocalTime checkOut,
            @Param("workHours") java.math.BigDecimal workHours);

    void calculateWorkHours(@Param("id") Integer id);
}