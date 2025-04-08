package com.example.projectdemo.domain.work.dao;

import com.example.projectdemo.domain.edsm.dto.EdsmBusinessContactDTO;
import com.example.projectdemo.domain.work.dto.OverTimeDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WorkDAO {

    @Autowired
    private SqlSession mybatis;

    public int insertByEdsm(EdsmBusinessContactDTO bcdto) {
        mybatis.insert("com.example.projectdemo.domain.work.dao.WorkDAO.insertByEdsm", bcdto);
        return bcdto.getId();
    }

    public String selectByStatus(String drafterId, int edsmDocId) {
        Map<String, Object> params = new HashMap<>();
        params.put("drafterId", drafterId);
        params.put("edsmDocId", edsmDocId);
        return mybatis.selectOne("com.example.projectdemo.domain.work.dao.WorkDAO.selectByStatus",params);
    }

    public int insertByOverTime(OverTimeDTO overTimeDTO){
        return mybatis.insert("com.example.projectdemo.domain.work.dao.WorkDAO.insertByOverTime", overTimeDTO);
    }

    public List<OverTimeDTO> selectAllOverTimeRequests(int empId){
        return mybatis.selectList("com.example.projectdemo.domain.work.dao.WorkDAO.selectAllOverTimeRequests", empId);
    }

    public List<OverTimeDTO> getOverTimeRequestsByMonth(int empId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("empId", empId);
        paramMap.put("startDate", startDate);
        paramMap.put("endDate", endDate);

        return mybatis.selectList("com.example.projectdemo.domain.work.dao.WorkDAO.selectOverTimeRequestsByMonth", paramMap);
    }
}
