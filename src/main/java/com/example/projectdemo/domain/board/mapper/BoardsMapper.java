package com.example.projectdemo.domain.board.mapper;

import com.example.projectdemo.domain.board.dto.BoardsDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

// BoardMapper.java
@Mapper
public interface BoardsMapper {

    // 모든 게시판 목록 조회
    List<BoardsDTO> getAllBoards();

    // ID로 게시판 조회
    BoardsDTO getBoardById(Integer id);

    // 사용자에게 접근 권한이 있는 게시판 조회 (board_permissions 테이블 활용)
    List<BoardsDTO> getBoardsByEmpId(Integer empId);

    // 공개 게시판 조회 (is_global = 1)
    List<BoardsDTO> getGlobalBoards();

}

