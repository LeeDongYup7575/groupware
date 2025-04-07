package com.example.projectdemo.domain.board.controller;

import com.example.projectdemo.domain.board.dto.PostsDTO;
import com.example.projectdemo.domain.board.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private PostsService postsService;

    /**
     * 게시판
     */
    @GetMapping("")
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


