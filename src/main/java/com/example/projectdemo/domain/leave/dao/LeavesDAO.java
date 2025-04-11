package com.example.projectdemo.domain.leave.dao;

import com.example.projectdemo.domain.edsm.dto.EdsmBusinessContactDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.leave.dto.LeavesDTO;
import com.example.projectdemo.domain.work.dto.OverTimeDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class LeavesDAO {

    @Autowired
    private SqlSession mybatis;

    public int insertByEdsm(EdsmBusinessContactDTO bcdto) {
        mybatis.insert("com.example.projectdemo.domain.leave.dao.LeavesDAO.insertByEdsm", bcdto);
        return bcdto.getId();
    }

    public String selectByStatus(String drafterId, int edsmDocId) {
        Map<String, Object> params = new HashMap<>();
        params.put("drafterId", drafterId);
        params.put("edsmDocId", edsmDocId);
        return mybatis.selectOne("com.example.projectdemo.domain.leave.dao.LeavesDAO.selectByStatus", params);
    }

    public int insertByLeaves(LeavesDTO leavesdto) {
        return mybatis.insert("com.example.projectdemo.domain.leave.dao.LeavesDAO.insertByLeaves", leavesdto);
    }

    public List<LeavesDTO> selectByLeaves(int empId) {
        return mybatis.selectList("com.example.projectdemo.domain.leave.dao.LeavesDAO.selectAllLeaves", empId);
    }

    // ✅ 연차 부여를 위한 직원 조회
    public List<EmployeesDTO> selectEmployeesForLeaveGrant() {
        return mybatis.selectList("com.example.projectdemo.domain.leave.dao.LeavesDAO.selectEmployeesForLeaveGrant");
    }

    // ✅ 직원 연차를 '덮어쓰기'로 설정 (초기화 후 부여)
    public void setEmployeeLeave(int empId, int leaveCount) {
        Map<String, Object> params = new HashMap<>();
        params.put("empId", empId);
        params.put("leaveCount", leaveCount);
        mybatis.update("com.example.projectdemo.domain.leave.dao.LeavesDAO.setEmployeeLeave", params);
    }

    // ✅ 연차 부여 이력 기록
    public void insertLeaveGrant(int empId, String grantType, int leaveCount) {
        Map<String, Object> params = new HashMap<>();
        params.put("empId", empId);
        params.put("grantType", grantType);
        params.put("leaveCount", leaveCount);
        mybatis.insert("com.example.projectdemo.domain.leave.dao.LeavesDAO.insertLeaveGrantWithDate", params);
    }

    // 특정 직원의 leave_grants 기록 전체 삭제
    public int deleteLeaveGrantsByEmpId(int empId){
        return mybatis.delete("com.example.projectdemo.domain.leave.dao.LeavesDAO.deleteLeaveGrantsByEmpId",empId);
    }

    public int updateLeaveStatus(int id, String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("status", status);
        return mybatis.update("com.example.projectdemo.domain.leave.dao.LeavesDAO.updateLeaveStatus",params);
    }

    public List<LeavesDTO> selectLeavesByEmpId(int empId) {
        return mybatis.selectList("com.example.projectdemo.domain.leave.dao.LeavesDAO.selectLeavesByEmpId", empId);
    }

    public List<Map<String, Object>> getMonthlyLeaveHours(int empId, int year) {
        Map<String, Object> params = new HashMap<>();
        params.put("empId", empId);
        params.put("year", year);
        return mybatis.selectList("com.example.projectdemo.domain.leave.dao.LeavesDAO.getMonthlyLeaveHours", params);
    }

    public List<LeavesDTO> getLeavesDTOListByDocId(int id) {
        return mybatis.selectList("com.example.projectdemo.domain.leave.dao.LeavesDAO.selectByLeavesFromDocId",id);
    }

}
