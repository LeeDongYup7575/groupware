package com.example.projectdemo.domain.admin.dao;

import com.example.projectdemo.domain.admin.dto.*;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashBoardDAO {

    @Autowired private SqlSession mybatis;

    public List<DepartmentListDTO> getDeplist () {
        return mybatis.selectList("com.example.projectdemo.domain.board.mapper.AdminMapper.getDepList");
    }
    public List<AttendanceRankingDTO> getAttendanceRanking() {
        return mybatis.selectList("com.example.projectdemo.domain.board.mapper.AdminMapper.getAttendanceRanking");
    }
    public List<EmployeeStatusDTO> getEmployeeStatus() {
        return mybatis.selectList("com.example.projectdemo.domain.board.mapper.AdminMapper.getEmployeeStatus");
    }
    public List<TodayAbsencesDTO> getTodayAbsences() {
        return mybatis.selectList("com.example.projectdemo.domain.board.mapper.AdminMapper.getTodayAbsences");
    }
    public int getTotalEmployees() {
        return mybatis.selectOne("com.example.projectdemo.domain.board.mapper.AdminMapper.getTotalEmployees");
    }
    public int getTodayMeetingRoomBookings() {
        return mybatis.selectOne("com.example.projectdemo.domain.board.mapper.AdminMapper.getTodayMeetingRoomBookings");
    }
    public int getTodaySuppliesBookings() {
        return mybatis.selectOne("com.example.projectdemo.domain.board.mapper.AdminMapper.getTodaySuppliesBookings");
    }
    public List<RecentLoginDTO> getEmployees() {
        return mybatis.selectList("com.example.projectdemo.domain.board.mapper.AdminMapper.getRecentLogins");
    }
}
