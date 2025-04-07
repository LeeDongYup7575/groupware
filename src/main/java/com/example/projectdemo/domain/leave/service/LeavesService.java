package com.example.projectdemo.domain.leave.service;


import com.example.projectdemo.domain.edsm.dto.EdsmBusinessContactDTO;
import com.example.projectdemo.domain.leave.dao.LeavesDAO;
import com.example.projectdemo.domain.leave.dto.LeavesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


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

    public int updateByLeaves(){
        return leavesDAO.updateByLeaves();
    }
}
