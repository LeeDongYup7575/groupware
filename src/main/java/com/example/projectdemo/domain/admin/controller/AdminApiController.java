package com.example.projectdemo.domain.admin.controller;

import com.example.projectdemo.domain.admin.dto.*;
import com.example.projectdemo.domain.admin.service.DashBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    @Autowired
    DashBoardService dashBoardService;

    /**
     * 관리자 대시보드 데이터 API 추가하면 됩니덩
     **/
    @GetMapping("/dashboard")
    public ResponseEntity<DashBoardDTO> dashBoard() {
        return ResponseEntity.ok(dashBoardService.getDashBoard());
    }
    @GetMapping("/department-distribution")
    public ResponseEntity<List<DepartmentListDTO>> departmentDistribution() {
        return ResponseEntity.ok(dashBoardService.getDepartmentDistribution());
    }

    @GetMapping("/employee-status")
    public ResponseEntity<List<EmployeeStatusDTO>> employeeStatus() {
        return ResponseEntity.ok(dashBoardService.getEmployeeStatus());
    }

    @GetMapping("/attendance-ranking")
    public ResponseEntity<List<AttendanceRankingDTO>> attendanceRanking() {
        return ResponseEntity.ok(dashBoardService.getAttendanceRanking());
    }

    @GetMapping("/today-absences")
    public ResponseEntity<List<TodayAbsencesDTO>> todayAbsences() {
        return ResponseEntity.ok(dashBoardService.getTodayAbsences());
    }

}
