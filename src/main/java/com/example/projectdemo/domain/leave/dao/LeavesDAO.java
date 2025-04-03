package com.example.projectdemo.domain.leave.dao;

import com.example.projectdemo.domain.edsm.dto.EdsmBcDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LeavesDAO {

    @Autowired
    private SqlSession mybatis;

    public int insertByLeaves(EdsmBcDTO bcdto) {

        return mybatis.insert("com.example.projectdemo.domain.leave.dao.LeavesDAO.insertByLeaves", bcdto);

    }
}
