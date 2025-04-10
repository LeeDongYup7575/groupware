package com.example.projectdemo.domain.attend.dao;

import com.example.projectdemo.domain.attend.dto.AttendDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AttendDAO {

    @Autowired
    private SqlSession mybatis;

    public List<AttendDTO> selectByEmpId(int empId) {
        return mybatis.selectList("com.example.projectdemo.domain.attend.dao.AttendDAO.selectByEmpId", empId);
    }

    public List<AttendDTO> selectByEmpIdAndDate(int empId) {
        return mybatis.selectList("com.example.projectdemo.domain.attend.dao.AttendDAO.selectByEmpIdAndDate", empId);
    }

    public List<Map<String, Object>> getAttendanceStatisticsByYear(int empId) {
        return mybatis.selectList("com.example.projectdemo.domain.attend.dao.AttendDAO.getAttendanceStatisticsThisYear", empId);
    }

    // 총 근무 시간 조회
    public BigDecimal getTotalWorkHoursThisYear(int empId) {
        return mybatis.selectOne("com.example.projectdemo.domain.attend.dao.AttendDAO.selectTotalWorkHoursThisYear", empId);
    }

    // 근무 일수 조회
    public int getWorkDaysThisYear(int empId) {
        return mybatis.selectOne("com.example.projectdemo.domain.attend.dao.AttendDAO.selectWorkDaysThisYear", empId);
    }

    public List<Map<String, Object>> getMonthlyAttendanceStatistics(int empId, int year) {
        Map<String, Object> params = new HashMap<>();
        params.put("empId", empId);
        params.put("year", year);

        return mybatis.selectList("com.example.projectdemo.domain.attend.dao.AttendDAO.getMonthlyAttendanceStatistics", params);
    }

    public List<Map<String, Object>> getMonthlyAttendanceStatisticsThisMonth(int empId) {
        return mybatis.selectList("com.example.projectdemo.domain.attend.dao.AttendDAO.getMonthlyAttendanceStatisticsThisMonth", empId);
    }

    public List<Map<String, Object>> getWeeklyAttendanceStatisticsThisWeek(int empId) {
        return mybatis.selectList("com.example.projectdemo.domain.attend.dao.AttendDAO.getWeeklyAttendanceStatisticsThisWeek", empId);
    }

}
