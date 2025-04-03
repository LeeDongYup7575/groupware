package com.example.projectdemo.domain.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
public class BoardController {

//        @GetMapping("")
//        public String board(Model model) {
//            model.addAttribute("pageTitle", "게시판");
//            return "board/list";
//        }

        @GetMapping("/write")
        public String writePage() {
            return "board/write"; // 글쓰기 페이지
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


