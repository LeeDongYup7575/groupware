package com.example.projectdemo.domain.leave.service;

import com.example.projectdemo.domain.edsm.dto.EdsmBcDTO;
import com.example.projectdemo.domain.leave.dao.LeavesDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeavesService {

    @Autowired
    private LeavesDAO leavesDAO;

    public int insertByLeaves(EdsmBcDTO bcdto) {
        return leavesDAO.insertByLeaves(bcdto);
    }
}
