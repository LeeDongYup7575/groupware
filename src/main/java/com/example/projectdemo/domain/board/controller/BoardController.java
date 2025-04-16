package com.example.projectdemo.domain.board.controller;

import com.example.projectdemo.domain.board.dto.AttachmentsDTO;
import com.example.projectdemo.domain.board.dto.BoardsDTO;
import com.example.projectdemo.domain.board.dto.PostsDTO;
import com.example.projectdemo.domain.board.entity.Comments;
import com.example.projectdemo.domain.board.service.AttachmentsService;
import com.example.projectdemo.domain.board.service.BoardsService;
import com.example.projectdemo.domain.board.service.CommentsService;
import com.example.projectdemo.domain.board.service.PostsService;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private PostsService postsService;

    @Autowired
    private BoardsService boardsService;

    @Autowired
    private AttachmentsService attachmentsService;

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private EmployeesService employeesService;

    // 통합 게시판 - 모든 권한 있는 게시판의 게시글 보기
    @GetMapping("")
    public String integratedBoard(HttpServletRequest request, Model model) {
        // 현재 로그인한 사용자 정보 가져오기
        int empId = (int)request.getAttribute("id");

        // 접근 가능한 모든 게시글 조회
        List<PostsDTO> posts = postsService.getAccessiblePosts(empId);

        // 접근 가능한 게시판 목록 추가
        List<BoardsDTO> accessibleBoards = boardsService.getAccessibleBoards(empId);
        model.addAttribute("accessibleBoards", accessibleBoards);

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
            return "board/integrated-list";
        }

        // 모든 접근 가능한 게시판 목록 추가
        List<BoardsDTO> accessibleBoards = boardsService.getAccessibleBoards(empId);
        model.addAttribute("accessibleBoards", accessibleBoards);

        BoardsDTO board = boardsService.getBoardById(boardId);
        List<PostsDTO> posts = postsService.getPostsByBoardId(boardId);

        model.addAttribute("board", board);
        model.addAttribute("posts", posts);
        model.addAttribute("boardName", board.getName());

        return "board/list";
    }

    // 게시글 상세 보기
    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable Integer id, HttpServletRequest request, Model model, Principal principal) {

        int empId = (int)request.getAttribute("id");

        // 게시글 정보 가져오기
        PostsDTO post = postsService.getPostById(id);
        BoardsDTO board = boardsService.getBoardById(post.getBoardId());

        // 게시판 접근 권한 확인
        if (!boardsService.hasAccess(empId, board.getId())) {
            return "board/integrated-list";
        }

        // 첨부파일 목록 가져오기
        List<AttachmentsDTO> attachments = attachmentsService.selectAttachmentsByPostId(id);

        // 모델에 데이터 추가
        model.addAttribute("post", post);
        model.addAttribute("board", board);
        model.addAttribute("attachments", attachments);

        // 댓글 목록 조회
        List<Comments> comments = commentsService.getCommentsByPostId(id);

        model.addAttribute("comments", comments);

        // 로그인한 사용자 ID를 모델에 추가
        model.addAttribute("empId", empId);

        return "board/view";
    }

    @GetMapping("/write")
    public String showWriteForm(HttpServletRequest request, Model model) {
        // 현재 로그인한 사용자 정보 가져오기
        int empId = (int) request.getAttribute("id");

        // 사용자가 접근 가능한 게시판 목록 조회
        List<BoardsDTO> accessibleBoards = boardsService.getAccessibleBoards(empId);
        model.addAttribute("accessibleBoards", accessibleBoards);

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

    // 게시판 만들기
    @PostMapping("/create")
    public String createBoard(BoardsDTO requestDTO,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        // isActive 값 확인 및 설정
        String isActiveParam = request.getParameter("isActive");

        // 체크박스가 체크되면 "on" 또는 "true"가 전송됨, 체크 안되면 null
        boolean isActive = isActiveParam != null;
        requestDTO.setActive(isActive);

        try {
            // 멤버 권한 정보 디버깅
            String[] memberIds = request.getParameterValues("memberIds");
            String[] permissions = request.getParameterValues("permissions");

            // 권한 정보 직접 설정
            if (memberIds != null && permissions != null) {
                List<Integer> memberIdList = new ArrayList<>();
                List<String> permissionList = new ArrayList<>();

                for (int i = 0; i < memberIds.length; i++) {
                    // 빈 문자열 또는 숫자로 변환할 수 없는 값 건너뛰기
                    if (memberIds[i] == null || memberIds[i].trim().isEmpty()) {
                        continue;
                    }

                    try {
                        int memberId = Integer.parseInt(memberIds[i]);
                        memberIdList.add(memberId);

                        // 권한 정보 추가
                        if (i < permissions.length) {
                            permissionList.add(permissions[i]);
                        } else {
                            // 권한이 없으면 기본값 설정
                            permissionList.add("읽기");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("잘못된 멤버 ID 형식: " + memberIds[i]);
                        // 잘못된 형식은 건너뛰기
                    }
                }

                // 유효한 멤버 ID가 있을 경우에만 설정
                if (!memberIdList.isEmpty()) {
                    requestDTO.setMemberIds(memberIdList);
                    requestDTO.setPermissions(permissionList);
                }
            }

            // request에서 empId 가져오기
            int empId = (int) request.getAttribute("id");

            // isGlobal 값 처리
            String isGlobalParam = request.getParameter("isGlobal");
            if (isGlobalParam != null) {
                // "true" 또는 1이면 true로 설정
                boolean isGlobal = "true".equals(isGlobalParam) || "1".equals(isGlobalParam);
                requestDTO.setGlobal(isGlobal);
            }

            // 수정 후 (문자열 배열을 Integer 리스트로 변환)
            List<Integer> memberIdList = new ArrayList<>();
            if (memberIds != null) {
                for (String id : memberIds) {
                    if (id != null && !id.trim().isEmpty()) {
                        try {
                            memberIdList.add(Integer.parseInt(id.trim()));
                        } catch (NumberFormatException e) {
                            // 변환 오류 무시
                        }
                    }
                }
            }
            // DB에 저장
            boardsService.createBoard(requestDTO, empId, memberIdList);

            redirectAttributes.addFlashAttribute("message", "게시판이 성공적으로 생성되었습니다.");
            redirectAttributes.addFlashAttribute("alertType", "alert-success");

            return "redirect:/board";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "게시판 생성 중 오류가 발생했습니다: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertType", "alert-danger");

            return "redirect:/board/create";
        }
    }

    //게시판 관리
    @GetMapping("/manage")
    public String manageBoards(HttpServletRequest request, Model model) {

        // 모든 게시판 정보 가져오기
        List<BoardsDTO> allBoards = boardsService.getAllBoards();

        // 각 게시판에 대한 추가 정보 설정
        for (BoardsDTO board : allBoards) {
            // 게시글 수 조회
            int totalPosts = postsService.countPostsByBoardId(board.getId());

            // 데이터 세팅
            board.setTotalPosts(totalPosts);

        }

        model.addAttribute("boards", allBoards);

        return "board/manage";
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

    // 게시글 삭제
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


