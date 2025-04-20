package com.example.projectdemo.domain.board.controller;

import com.example.projectdemo.domain.board.dto.CommentsDTO;
import com.example.projectdemo.domain.board.entity.Comments;
import com.example.projectdemo.domain.board.service.CommentsService;
import com.example.projectdemo.domain.board.service.PostsService;
import com.example.projectdemo.domain.notification.service.NotificationEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 댓글 관련 API를 처리하는 컨트롤러
@RestController
@RequestMapping("/api/comments")
public class CommentsApiController {

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private PostsService postsService;

    @Autowired
    private NotificationEventHandler notificationEventHandler;

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

        // 알림 발생 처리
        if (savedComments.getParentId() == null) {
            // 일반 댓글인 경우: 게시글 작성자에게 알림
            Integer postAuthorId = postsService.getPostById(savedComments.getPostId()).getEmpId();
            notificationEventHandler.handleCommentNotification(savedComments, postAuthorId);
        } else {
            // 대댓글인 경우: 원 댓글 작성자에게 알림
            notificationEventHandler.handleReplyNotification(savedComments);
        }

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

    // 게시글의 모든 댓글 조회 (계층 구조로)
    @GetMapping("/post/{postId}/hierarchical")
    public ResponseEntity<List<Comments>> getHierarchicalCommentsByPostId(@PathVariable int postId) {
        List<Comments> hierarchicalComments = commentsService.getHierarchicalCommentsByPostId(postId);
        return ResponseEntity.ok(hierarchicalComments);
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

    /**
     * 댓글 다중 삭제
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteComments(@RequestBody List<Integer> commentIds) {
        try{
            commentsService.deleteCommentsByIds(commentIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}