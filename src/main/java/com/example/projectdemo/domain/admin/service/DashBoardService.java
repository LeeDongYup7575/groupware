package com.example.projectdemo.domain.admin.service;

import com.example.projectdemo.domain.admin.dao.DashBoardDAO;
import com.example.projectdemo.domain.admin.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DashBoardService {

    @Autowired
    private DashBoardDAO dashBoardDAO;

    public List<DepartmentListDTO> getDepartmentDistribution() {
        return dashBoardDAO.getDeplist();
    }

    public List<AttendanceRankingDTO> getAttendanceRanking() {
        return dashBoardDAO.getAttendanceRanking();
    }

    public List<EmployeeStatusDTO> getEmployeeStatus() {
        return dashBoardDAO.getEmployeeStatus();
    }

    public List<TodayAbsencesDTO> getTodayAbsences() {
        return dashBoardDAO.getTodayAbsences();
    }

    public DashBoardDTO getDashBoard() {
        DashBoardDTO dto = new DashBoardDTO(
                dashBoardDAO.getTotalEmployees(),
                dashBoardDAO.getTodayMeetingRoomBookings(),
                dashBoardDAO.getTodaySuppliesBookings(),
                dashBoardDAO.getEmployees());
        return dto;
    }
}
