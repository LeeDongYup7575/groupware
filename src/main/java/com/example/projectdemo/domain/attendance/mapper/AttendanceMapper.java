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

    List<Attendance> getAttendanceListByEmployeeAndDate(@Param("empId") Integer empId, @Param("today") LocalDate today);

    List<Attendance> getAttendanceHistoryByEmployeeId(@Param("empId") Integer empId);

    // AttendanceMapper.java
    Attendance getLatestCheckIn(@Param("empId") Integer empId, @Param("date") LocalDate date);


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

    /**
     * 특정 날짜에 출근했지만 퇴근 기록이 없는 직원들의 출근 기록을 조회
     * @param workDate 조회할 날짜
     * @return 출근했지만 퇴근하지 않은 직원들의 출근 기록 목록
     */
    List<Attendance> findCheckedInWithoutCheckout(@Param("workDate") LocalDate workDate);
}