package com.example.projectdemo.domain.leave.service;


import com.example.projectdemo.domain.edsm.dto.EdsmBusinessContactDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.leave.dao.LeavesDAO;
import com.example.projectdemo.domain.leave.dto.LeavesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;


@Service
public class LeavesService {

    @Autowired
    private LeavesDAO leavesDAO;

    public int insertByEdsm(EdsmBusinessContactDTO bcdto) {
        return leavesDAO.insertByEdsm(bcdto);
    }

    public String selectByStatus(String drafterId, int edsmDocId) {
        return leavesDAO.selectByStatus(drafterId, edsmDocId);
    }

    public int insertByLeaves(LeavesDTO leavesdto){
        return leavesDAO.insertByLeaves(leavesdto);
    }

    public List<LeavesDTO> selectAllLeaves(int empId){
        return leavesDAO.selectByLeaves(empId);
    }

    public void updateByLeaves() {
        List<EmployeesDTO> targetEmployees = leavesDAO.selectEmployeesForLeaveGrant();

        for (EmployeesDTO emp : targetEmployees) {
            LocalDate hireDate = emp.getHireDate();
            int leaveCount;
            String grantType;

            long months = ChronoUnit.MONTHS.between(hireDate, LocalDate.now());

            if (months < 12) {
                leaveCount = 1;
                grantType = "monthly";
            } else {
                // 연차 초기화 (이전 기록 제거)
                leavesDAO.deleteLeaveGrantsByEmpId(emp.getId());
                leaveCount = 15;
                grantType = "annual";
            }

            leavesDAO.setEmployeeLeave(emp.getId(), leaveCount);
            leavesDAO.insertLeaveGrant(emp.getId(), grantType, leaveCount);
        }
    }

    public int updateLeaveStatus(int id, String status) {
        return leavesDAO.updateLeaveStatus(id, status);
    }

    public List<LeavesDTO> selectLeavesByEmpId(int empId) {
        return leavesDAO.selectLeavesByEmpId(empId);
    }

    public List<Map<String, Object>> getMonthlyLeaveHours(int empId, int year) {
        return leavesDAO.getMonthlyLeaveHours(empId, year);
    }
}
