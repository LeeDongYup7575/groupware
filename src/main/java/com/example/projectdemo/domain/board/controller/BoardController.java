package com.example.projectdemo.domain.board.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.board.dto.BoardsDTO;
import com.example.projectdemo.domain.board.dto.PostsDTO;
import com.example.projectdemo.domain.board.service.BoardsService;
import com.example.projectdemo.domain.board.service.PostsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private PostsService postsService;

    @Autowired
    private BoardsService boardsService;


    // 통합 게시판 - 모든 권한 있는 게시판의 게시글 보기
    @GetMapping("")
    public String integratedBoard(HttpServletRequest request, Model model) {
        // 현재 로그인한 사용자 정보 가져오기
        int empId = (int)request.getAttribute("id");

        // 접근 가능한 모든 게시글 조회
        List<PostsDTO> posts = postsService.getAccessiblePosts(empId);

        model.addAttribute("posts", posts);
        model.addAttribute("boardName", "통합 게시판");

        return "board/integrated-list";
    }

    // 특정 게시판의 게시글 목록 보기
    @GetMapping("/{boardId}")
    public String boardPosts(@PathVariable Integer boardId, HttpServletRequest request, Model model) {
        int empId = (int)request.getAttribute("id");

        // 게시판 접근 권한 확인
        if (!boardsService.hasAccess(empId, boardId)) {
            return "board/access-denied";
        }

        BoardsDTO board = boardsService.getBoardById(boardId);
        List<PostsDTO> posts = postsService.getPostsByBoardId(boardId);

        model.addAttribute("board", board);
        model.addAttribute("posts", posts);
        model.addAttribute("boardName", board.getName());

        return "board/list";
    }

    // 게시글 상세 보기
    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable Integer id, HttpServletRequest request, Model model) {
        int empId = (int)request.getAttribute("id");

        PostsDTO post = postsService.getPostById(id);
        BoardsDTO board = boardsService.getBoardById(post.getBoardId());

        // 게시판 접근 권한 확인
        if (!boardsService.hasAccess(empId, board.getId())) {
            return "board/access-denied";
        }

        model.addAttribute("post", post);
        model.addAttribute("board", board);

        return "board/view";
    }

    @GetMapping("/write")
    public String showWriteForm(Model model) {
        model.addAttribute("PostsDTO", new PostsDTO());
        return "board/write";  // 글쓰기 페이지
    }

    @GetMapping("/important")
    public String importantPosts() {
        return "board/important"; // 중요 게시물 페이지
    }

    @GetMapping("/create")
    public String createBoard() {
        return "board/create"; // 게시판 만들기
    }

    @GetMapping("/manage")
    public String manageBoard() {
        return "board/manage"; // 게시판 관리
    }

    // 게시글 수정 폼 표시
    @GetMapping("/edit/{id}")
    public String editPostForm(@PathVariable int id, HttpServletRequest request, Model model) {
        int empId = (int)request.getAttribute("id");

        // 게시글 정보 조회
        PostsDTO post = postsService.getPostById(id);

        // 게시글이 없거나 수정 권한이 없는 경우
        if (post == null || !postsService.canModifyPost(empId, id)) {
            return "redirect:/board/post/" + id + "?error=unauthorized";
        }

        // 게시글 정보를 모델에 추가
        model.addAttribute("post", post);
        model.addAttribute("board", boardsService.getBoardById(post.getBoardId()));

        return "board/edit";
    }

    // 게시글 수정 처리
    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable int id,
                             @ModelAttribute PostsDTO post,
                             HttpServletRequest request) {
        int empId = (int)request.getAttribute("id");

        // 기존 게시글 확인
        PostsDTO existingPost = postsService.getPostById(id);

        // 게시글이 없거나 수정 권한이 없는 경우
        if (existingPost == null || !postsService.canModifyPost(empId, id)) {
            return "redirect:/board/post/" + id + "?error=unauthorized";
        }

        // 변경할 수 없는 필드는 기존 값으로 설정
        post.setId(id);
        post.setEmpId(empId);
        post.setBoardId(existingPost.getBoardId());

        // 게시글 수정
        boolean updated = postsService.updatePost(post);

        if (updated) {
            return "redirect:/board/post/" + id + "?success=true";
        } else {
            return "redirect:/board/edit/" + id + "?error=failed";
        }
    }

    //게시글 삭제
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            boolean  result = postsService.deletePost(id);
            if (result) {
                redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("error", "게시글을 찾을 수 없습니다.");
            }
            return "redirect:/board";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시글 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/board/post/" + id;
        }
    }

}


