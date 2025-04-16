package com.example.projectdemo.domain.mypage.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.auth.service.ProfileUploadService;
import com.example.projectdemo.domain.board.dto.PostsDTO;
import com.example.projectdemo.domain.board.service.CommentsService;
import com.example.projectdemo.domain.board.service.PostsService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.mypage.dto.MyCommentDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
public class MypageApiController {
    private static final Logger logger = LoggerFactory.getLogger(MypageApiController.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final EmployeesService employeesService;
    private final ProfileUploadService profileUploadService;
    private final PostsService postsService;
    private final CommentsService commentsService;

    @Autowired
    public MypageApiController(JwtTokenUtil jwtTokenUtil,
                               EmployeesService employeesService,
                               ProfileUploadService profileUploadService,
                               PostsService postsService,
                               CommentsService commentsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.employeesService = employeesService;
        this.profileUploadService = profileUploadService;
        this.postsService = postsService;
        this.commentsService = commentsService;
    }

    /**
     * 내 정보 조회
     */
    @GetMapping("/info")
    public ResponseEntity<EmployeesDTO> info(HttpServletRequest request) {
        try {
            String empNum = (String)request.getAttribute("empNum");
            if (empNum == null || empNum.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            EmployeesDTO employee = employeesService.findByEmpNum(empNum);

            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 마지막 활동 시간 조회
     */
    @GetMapping("/security")
    public ResponseEntity<Map<String, String>> security(HttpServletRequest request) {
        try{
            String empNum = (String)request.getAttribute("empNum");
            if (empNum == null || empNum.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, String> response = employeesService.selectLastLogin(empNum);
            if (response == null || response.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    /**
     * 내 정보 수정
     */
    @PatchMapping("/update")
    public ResponseEntity<?> update(HttpServletRequest request,
                                    @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                    @RequestParam("phone") String phone,
                                    @RequestParam("email") String email,
                                    @RequestParam("isImageDeleted") boolean isImageDeleted) {
        String empNum = (String)request.getAttribute("empNum");

        // 프로필 이미지 처리 - 로컬 파일 시스템에 저장
        String profileImgUrl = null;  // 기본 이미지

        if(isImageDeleted){
            profileImgUrl = "/assets/images/default-profile.png";
        } else if (profileImage != null && !profileImage.isEmpty()) {
            try {
                // 로컬 파일 시스템에 업로드
                profileImgUrl = profileUploadService.uploadProfileImage(profileImage);
                logger.info("프로필 이미지가 로컬에 성공적으로 업데이트되었습니다: {}", profileImgUrl);
            } catch (IOException e) {
                // 업로드 실패 시 기본 이미지 사용 (로그만 남기고 계속 진행)
                logger.warn("프로필 이미지 업데이트 실패, 기존 이미지 사용: {}", e.getMessage());
            }
        }

        employeesService.updateEmpInfo(empNum, phone, email, profileImgUrl);

        return ResponseEntity.ok(Collections.singletonMap("message", "업데이트 성공"));
    }

    /**
     * 내 게시글 조회
     */
    @GetMapping("/activities/mypost")
    public ResponseEntity<List<PostsDTO>> getMyPosts(HttpServletRequest request) {
        try{
            Integer empId = (Integer) request.getAttribute("id");

            if (empId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            List<PostsDTO> myPosts = postsService.getMyPosts(empId);
            return ResponseEntity.ok(myPosts);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 내 댓글 조회
     */
    @GetMapping("/activities/mycomment")
    public ResponseEntity<List<MyCommentDTO>> getMyComments(HttpServletRequest request) {
        try{
            Integer empId = (Integer) request.getAttribute("id"); // 로그인 사용자 식별자

            if (empId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            List<MyCommentDTO> comments = commentsService.getCommentsByEmpId(empId);
            return ResponseEntity.ok(comments);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
