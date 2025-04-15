package com.example.projectdemo.domain.admin.service;

import com.example.projectdemo.domain.admin.dao.BoardDAO;
import com.example.projectdemo.domain.board.dto.BoardsDTO;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BoardService {

    @Autowired
    private BoardDAO boardDAO;

    public List<BoardsDTO> getBoardList() {
        return boardDAO.getBoardList();
    }
    public void updateStatus(int id,boolean isActive) {
        Map<String,Object> param = new HashMap<>();
        param.put("isActive",isActive);
        param.put("id",id);
        boardDAO.updateStatus(param);
    }
}
