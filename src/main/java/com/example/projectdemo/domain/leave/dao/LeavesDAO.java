package com.example.projectdemo.domain.leave.dao;


import com.example.projectdemo.domain.edsm.dto.EdsmBusinessContactDTO;
import com.example.projectdemo.domain.leave.dto.LeavesDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
        return mybatis.selectOne("com.example.projectdemo.domain.leave.dao.LeavesDAO.selectByStatus",params);
    }

    public int insertByLeaves(LeavesDTO leavesdto){
        return mybatis.insert("com.example.projectdemo.domain.leave.dao.LeavesDAO.insertByLeaves", leavesdto);
    }

    public List<LeavesDTO> selectByLeaves(int empId){
        return mybatis.selectList("com.example.projectdemo.domain.leave.dao.LeavesDAO.selectAllLeaves", empId);
    }
}
