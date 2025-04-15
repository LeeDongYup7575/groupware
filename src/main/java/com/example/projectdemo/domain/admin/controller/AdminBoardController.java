package com.example.projectdemo.domain.admin.controller;

import com.example.projectdemo.domain.admin.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/boards")
public class AdminBoardController {
    @Autowired
    private BoardService boardService;


    @RequestMapping("/list")
    public ResponseEntity<?> list() {

        return ResponseEntity.ok(boardService.getBoardList());
    }

    @RequestMapping("/updateStatus/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable int id, @RequestBody Map<String,Object> body) {
        boolean isActive = (Boolean) body.get("isActive");
        boardService.updateStatus(id,isActive);
        return ResponseEntity.ok().build();
    }
}
