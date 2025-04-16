package com.example.projectdemo.domain.board.controller;

import com.example.projectdemo.domain.board.dto.BoardsDTO;
import com.example.projectdemo.domain.board.service.BoardsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/board")
public class BoardApiController {

    @Autowired
    private BoardsService boardsService;


    /**
     * 게시판 목록 조회 API
     */
    @GetMapping("/list")
    public ResponseEntity<List<BoardsDTO>> getBoardList(HttpServletRequest request) {
        int empId = (int) request.getAttribute("id");
        List<BoardsDTO> boards = boardsService.getAccessibleBoards(empId);
        return ResponseEntity.ok(boards);
    }

    /**
     * 게시판 상세 정보 조회 API
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<?> getBoardDetail(@PathVariable Integer boardId, HttpServletRequest request) {
        try {
            int empId = (int) request.getAttribute("id");

            // 접근 권한 확인
            if (!boardsService.hasAccess(empId, boardId)) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "해당 게시판에 접근할 권한이 없습니다.");
                return ResponseEntity.status(403).body(error);
            }

            BoardsDTO board = boardsService.getBoardById(boardId);
            return ResponseEntity.ok(board);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 게시판 업데이트 API
     */
    @PutMapping("/{boardId}")
    public ResponseEntity<?> updateBoard(@PathVariable Integer boardId,
                                         @RequestBody BoardsDTO requestDTO,
                                         HttpServletRequest request) {
        try {
            BoardsDTO updatedBoard = boardsService.updateBoard(boardId, requestDTO);
            return ResponseEntity.ok(updatedBoard);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 게시판 삭제 API (실제로는 비활성화)
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable Integer boardId, HttpServletRequest request) {
        try {
            boardsService.deleteBoard(boardId);
            Map<String, String> success = new HashMap<>();
            success.put("message", "게시판이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}