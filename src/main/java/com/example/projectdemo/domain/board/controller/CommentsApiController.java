package com.example.projectdemo.domain.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.projectdemo.domain.board.dto.CommentsDTO;
import com.example.projectdemo.domain.board.entity.Comments;
import com.example.projectdemo.domain.board.service.CommentsService;

import lombok.RequiredArgsConstructor;

// 댓글 관련 API를 처리하는 컨트롤러
@RestController
@RequestMapping("/api/comments")
public class CommentsApiController {

    @Autowired
    private CommentsService commentsService;

    // 댓글 추가
    @PostMapping
    public ResponseEntity<Comments> addComments(@RequestBody CommentsDTO commentDTO) {
        Comments comment = Comments.builder()
                .postId(commentDTO.getPostId())
                .empId(commentDTO.getEmpId())
                .content(commentDTO.getContent())
                .parentId(commentDTO.getParentId())
                .build();

        Comments savedComments = commentsService.addComments(comment);
        return ResponseEntity.ok(savedComments);
    }

    // 게시글의 모든 댓글 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comments>> getCommentssByPostId(@PathVariable int postId) {
        List<Comments> comments = commentsService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // 특정 댓글 조회
    @GetMapping("/{id}")
    public ResponseEntity<Comments> getCommentsById(@PathVariable int id) {
        Comments comment = commentsService.getCommentsById(id);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(comment);
    }

    // 댓글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Comments> updateComments(@PathVariable int id, @RequestBody CommentsDTO commentsDTO) {
        // 전달받은 ID로 기존 댓글 조회
        Comments existingComments = commentsService.getCommentsById(id);
        // 댓글이 존재하지 않으면 404 Not Found 응답
        if (existingComments == null) {
            return ResponseEntity.notFound().build();
        }

        // 댓글 내용 수정
        existingComments.setContent(commentsDTO.getContent());

        // 수정된 댓글을 DB에 반영하고 결과 반환
        Comments updatedComments = commentsService.updateComments(existingComments);

        // 수정된 댓글 데이터를 클라이언트에 응답
        return ResponseEntity.ok(updatedComments);
    }

    // 댓글 삭제 (HTTP DELETE 요청)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteComment(@PathVariable int id) {
        // 댓글 삭제 서비스 호출 (삭제 성공 여부를 boolean으로 반환)
        boolean deleted = commentsService.deleteComment(id);

        // 응답 데이터를 담을 Map 생성
        Map<String, Boolean> response = new HashMap<>();

        // 삭제 결과를 "deleted"라는 키로 Map에 저장 (예: {"deleted": true})
        response.put("deleted", deleted);

        // 응답 본문에 Map을 담아 200 OK 상태로 클라이언트에게 응답
        return ResponseEntity.ok(response);
    }


}