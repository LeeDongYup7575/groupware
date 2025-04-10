package com.example.projectdemo.domain.attend.service;

import com.example.projectdemo.domain.attend.dao.AttendDAO;
import com.example.projectdemo.domain.attend.dto.AttendDTO;
import com.example.projectdemo.domain.attendance.entity.Attendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service
public class AttendService {
    @Autowired
    private AttendDAO dao;

    public List<AttendDTO> selectByEmpId(int empId) {
        return dao.selectByEmpId(empId);
    }

    public List<AttendDTO> selectByEmpIdAndDate(int empId) {
        return dao.selectByEmpIdAndDate(empId);
    }

    public List<Map<String, Object>> getAttendanceStatisticsThisYear(int empId) {
        List<Map<String, Object>> allStatistics = dao.getAttendanceStatisticsByYear(empId);

        // 올해의 데이터만 필터링
        List<Map<String, Object>> thisYearStatistics = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);  // 현재 연도 가져오기

        for (Map<String, Object> stats : allStatistics) {
            if (Integer.parseInt(stats.get("year").toString()) == currentYear) {
                thisYearStatistics.add(stats);
            }
        }

        return thisYearStatistics;
    }

    public List<Map<String, Object>> getMonthlyAttendanceStatisticsThisMonth(int empId) {
        return dao.getMonthlyAttendanceStatisticsThisMonth(empId);
    }

    public List<Map<String, Object>> getWeeklyAttendanceStatisticsThisWeek(int empId) {
        return dao.getWeeklyAttendanceStatisticsThisWeek(empId);
    }

    public List<Map<String, Object>> getMonthlyAttendanceStatistics(int empId,int year) {
        return dao.getMonthlyAttendanceStatistics(empId, year);
    }

    // 총 근무시간 조회
    public BigDecimal getTotalWorkHoursThisYear(int empId) {
        return dao.getTotalWorkHoursThisYear(empId);
    }

    // 근무일수 조회
    public int getWorkDaysThisYear(int empId) {
        return dao.getWorkDaysThisYear(empId);
    }



}
