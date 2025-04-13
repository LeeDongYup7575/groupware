package com.example.projectdemo.domain.board.controller;

import com.example.projectdemo.domain.board.dto.CommentsDTO;
import com.example.projectdemo.domain.board.entity.Comments;
import com.example.projectdemo.domain.board.service.CommentsService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/board/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private EmployeesService employeesService;

    // 댓글 추가 처리
    @PostMapping("/add")
    public String addComment(@ModelAttribute CommentsDTO commentsDTO, HttpServletRequest request) {
        try {
            int empId = (int)request.getAttribute("id");

            System.out.println("댓글 추가 요청 - 게시글 ID: " + commentsDTO.getPostId());
            System.out.println("댓글 내용: " + commentsDTO.getContent());
            System.out.println("작성자 ID: " + empId);

            Comments comments = Comments.builder()
                    .postId(commentsDTO.getPostId())
                    .empId(empId)
                    .content(commentsDTO.getContent())
                    .build();

            Comments savedComments = commentsService.addComments(comments);
            System.out.println("저장된 댓글 ID: " + savedComments.getId());

            return "redirect:/board/post/" + commentsDTO.getPostId();
        } catch (Exception e) {
            System.err.println("댓글 추가 오류: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/board/post/" + commentsDTO.getPostId(); // 오류 발생해도 리다이렉트
        }
    }

    // 댓글 수정 폼
    @GetMapping("/edit/{id}")
    public String editCommentForm(@PathVariable int id, Model model, HttpServletRequest request) {
        int empId = (int)request.getAttribute("id");
        Comments comments = commentsService.getCommentsById(id);


        // 권한 확인 (자신의 댓글만 수정 가능)
        if (comments.getEmpId() != empId) {
            return "redirect:/board/post/" + comments.getPostId();
        }

        model.addAttribute("comments", comments);
        return "comment-edit";
    }

    // 댓글 수정 처리
    @PostMapping("/update")
    public String updateComment(@ModelAttribute CommentsDTO commentsDTO, HttpServletRequest request) {
        int empId = (int)request.getAttribute("id");
        Comments comments = commentsService.getCommentsById(commentsDTO.getId());

        // 권한 확인 (자신의 댓글만 수정 가능)
        if (comments.getEmpId() != empId) {
            return "redirect:/board/posts/" + comments.getPostId();
        }

        comments.setContent(commentsDTO.getContent());
        commentsService.updateComments(comments);

        return "redirect:/board/posts/" + comments.getPostId();
    }

    // 댓글 삭제 처리
    @GetMapping("/delete/{id}")
    public String deleteComment(@PathVariable int id, HttpServletRequest request) {
        int empId = (int)request.getAttribute("id");
        Comments comments = commentsService.getCommentsById(id);

        // 권한 확인 (자신의 댓글만 삭제 가능)
        if (comments.getEmpId() != empId) {
            return "redirect:/board/posts/" + comments.getPostId();
        }

        commentsService.deleteComment(id);

        return "redirect:/board/posts/" + comments.getPostId();
    }
}