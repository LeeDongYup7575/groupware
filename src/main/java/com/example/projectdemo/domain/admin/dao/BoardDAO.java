package com.example.projectdemo.domain.admin.dao;

import com.example.projectdemo.domain.board.dto.BoardsDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class BoardDAO {
    @Autowired
    private SqlSession mybatis;

    public List<BoardsDTO> getBoardList () {
       return mybatis.selectList("AdminBoardMapper.getBoardList");
    }

    public void updateStatus(Map<String,Object> param) {
        mybatis.update("AdminBoardMapper.updateStatus",param);
    }
}
