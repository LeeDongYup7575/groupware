package com.example.projectdemo.domain.board.service;

import com.example.projectdemo.domain.board.dto.BoardsDTO;
import com.example.projectdemo.domain.board.dto.PostsDTO;
import com.example.projectdemo.domain.board.mapper.BoardsMapper;
import com.example.projectdemo.domain.board.mapper.PostsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BoardsService {

    @Autowired
    private PostsMapper postsMapper; // 게시글 전용 매퍼

    @Autowired
    private BoardsMapper boardsMapper;// 게시판 전용 매퍼

    // 모든 게시판 목록 조회
    public List<BoardsDTO> getAllBoards() {
        return boardsMapper.getAllBoards();
    }

    // ID로 게시판 조회
    public BoardsDTO getBoardById(Integer id) {
        return boardsMapper.getBoardById(id);
    }

    // 사용자에게 접근 권한이 있는 게시판 조회
    public List<BoardsDTO> getAccessibleBoards(Integer empId) {
        // 1. 공개 게시판(is_global=1) 조회
        List<BoardsDTO> globalBoards = boardsMapper.getGlobalBoards();

        // 2. 개인 권한이 있는 게시판 조회
        List<BoardsDTO> permissionBoards = boardsMapper.getBoardsByEmpId(empId);

        // 3. 중복 제거 후 합치기
        Map<Integer, BoardsDTO> boardMap = new HashMap<>();

        for (BoardsDTO board : globalBoards) {
            boardMap.put(board.getId(), board);
        }

        for (BoardsDTO board : permissionBoards) {
            if (!boardMap.containsKey(board.getId())) {
                boardMap.put(board.getId(), board);
            }
        }

        return new ArrayList<>(boardMap.values());
    }

    // 게시판 접근 권한 확인
    public boolean hasAccess(Integer empId, Integer boardId) {
        BoardsDTO board = getBoardById(boardId);

        // 공개 게시판은 모두 접근 가능
        if (board.isGlobal()) {
            return true;
        }

        // 개인 권한이 있는 게시판인지 확인
        List<BoardsDTO> permissionBoards = boardsMapper.getBoardsByEmpId(empId);
        return permissionBoards.stream()
                .anyMatch(b -> b.getId().equals(boardId));
    }
}
