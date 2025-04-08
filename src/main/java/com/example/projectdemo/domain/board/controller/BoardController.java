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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private PostsService postsService;

    @Autowired
    private BoardsService boardsService;


//  추가 내용 :: s
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
//  추가 내용 :: s



    @GetMapping("/list")
    public String getAllPosts(Model model) {
        List<PostsDTO> posts = postsService.getAllPosts();
        model.addAttribute("posts", posts);
        return "board/list"; //게시글 목록 조회(전체 게시글 보기)
    }

    @GetMapping("/view/{id}")
    public String viewPost(@PathVariable int id, Model model) {
        PostsDTO post = postsService.getPostById(id);
        model.addAttribute("post", post);
        return "board/view"; //게시글 상세 조회(특정 게시글 보기)
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

    @GetMapping("/notice")
    public String notices() {
        return "board/notice"; // 사내공지
    }

    @GetMapping("/free")
    public String freeBoard() {
        return "board/free"; // 자유게시판
    }

    @GetMapping("/football")
    public String footballClub() {
        return "board/football"; // 축구동호회
    }

    @GetMapping("/movies")
    public String movieClub() {
        return "board/movies"; // 영화동호회
    }

    @GetMapping("/create")
    public String createBoard() {
        return "board/create"; // 게시판 만들기
    }

    @GetMapping("/manage")
    public String manageBoard() {
        return "board/manage"; // 게시판 관리
    }
}


