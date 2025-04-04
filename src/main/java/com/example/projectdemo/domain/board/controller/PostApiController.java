package com.example.projectdemo.domain.board.controller;

import com.example.projectdemo.domain.board.dto.PostsDTO;
import com.example.projectdemo.domain.board.service.PostsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;

@RestController
@RequestMapping("/api/posts")
public class PostApiController {

    @Autowired
    private PostsService postsService;

    // === 게시글 관련 API ===

    // 게시글 작성, 저장 API
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostsDTO postsDTO, HttpSession session, HttpServletRequest request) {
        //HTTP 요청의 속성 값(id)을 Integer 타입으로 캐스팅
        Integer empId = (Integer) request.getAttribute("id");

        if (empId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 서비스를 통해 게시글 작성
        PostsDTO savedPost = postsService.createPost(empId, postsDTO);

        // 성공 응답 반환
        return ResponseEntity.ok(savedPost);
    }
}
