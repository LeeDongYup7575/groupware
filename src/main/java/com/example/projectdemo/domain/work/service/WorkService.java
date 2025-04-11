package com.example.projectdemo.domain.work.service;

import com.example.projectdemo.domain.edsm.dto.EdsmBusinessContactDTO;
import com.example.projectdemo.domain.work.dao.WorkDAO;
import com.example.projectdemo.domain.work.dto.OverTimeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkService {

    @Autowired
    private WorkDAO workDAO;

    public int insertByEdsm(EdsmBusinessContactDTO bcdto) {
        return workDAO.insertByEdsm(bcdto);
    }

    public String selectByStatus(String drafterId, int edsmDocId) {
        return workDAO.selectByStatus(drafterId, edsmDocId);
    }

    public int insertByOverTime(OverTimeDTO overTimeDTO){
        return workDAO.insertByOverTime(overTimeDTO);
    }

    public List<OverTimeDTO> selectAllOverTimeRequests(int empId){
        return workDAO.selectAllOverTimeRequests(empId);
    }

    public List<OverTimeDTO> getOverTimeRequestsByMonth(int empId, int year, int month){
        return workDAO.getOverTimeRequestsByMonth(empId,year,month);
    }

    public int updateOverTimeRequestsStatus(int id, String status){
        return workDAO.updateOverTimeRequestsStatus(id,status);
    }

    public List<OverTimeDTO> selectOverTimeListByEmpId(int empId){
        return workDAO.selectOverTimeListByEmpId(empId);
    }

    public List<Map<String, Object>> getMonthlyOvertimeHours(int empId, int year){
        return workDAO.getMonthlyOvertimeHours(empId,year);
    }

    public List<OverTimeDTO> getOvertimeDTOListByDocId(int id) {
        return workDAO.getOvertimeDTOListByDocId(id);
    }
}
