package com.example.projectdemo.domain.mypage.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.board.dto.PostsDTO;
import com.example.projectdemo.domain.board.mapper.PostsMapper;
import com.example.projectdemo.domain.board.service.CommentsService;
import com.example.projectdemo.domain.board.service.PostsService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.mypage.dto.MyCommentDTO;
import com.example.projectdemo.domain.s3.service.ProfileUploadService;
import com.example.projectdemo.domain.s3.service.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/mypage")
public class MypageApiController {
    private static final Logger logger = LoggerFactory.getLogger(MypageApiController.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final EmployeesService employeesService;
    private final ProfileUploadService profileUploadService;
    private final PostsService postsService;
    private final CommentsService commentsService;
    private final EmployeesMapper employeesMapper;
    private final S3Service s3Service;

    @Autowired
    public MypageApiController(JwtTokenUtil jwtTokenUtil,
                               EmployeesService employeesService,
                               ProfileUploadService profileUploadService,
                               PostsService postsService,
                               CommentsService commentsService,
                               EmployeesMapper employeesMapper,
                               S3Service s3Service) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.employeesService = employeesService;
        this.profileUploadService = profileUploadService;
        this.postsService = postsService;
        this.commentsService = commentsService;
        this.employeesMapper = employeesMapper;
        this.s3Service = s3Service;
    }

//    /**
//     * 내 정보 조회
//     */
//    @GetMapping("/info")
//    public ResponseEntity<EmployeesDTO> info(HttpServletRequest request) {
//        try {
//            String empNum = (String)request.getAttribute("empNum");
//            if (empNum == null || empNum.isBlank()) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//            }
//
//            EmployeesDTO employee = employeesService.findByEmpNum(empNum);
//
//            return ResponseEntity.ok(employee);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
    /**
     * 내 정보 조회 (S3 프로필 이미지 URL 처리 포함)
     */
    @GetMapping("/info")
    public ResponseEntity<EmployeesDTO> info(HttpServletRequest request) {
        try {
            String empNum = (String)request.getAttribute("empNum");
            if (empNum == null || empNum.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 사용자 정보 조회
            EmployeesDTO employee = employeesService.findByEmpNum(empNum);

            // 프로필 이미지 URL 처리
            if (employee != null && employee.getProfileImgUrl() != null) {
                // 현재 URL이 로컬 경로이고 S3에도 동일한 이미지가 있는지 확인
                if (!employee.getProfileImgUrl().contains("amazonaws.com") &&
                        !employee.getProfileImgUrl().equals("/assets/images/default-profile.png")) {

                    // URL에서 파일명만 추출
                    String fileName = employee.getProfileImgUrl();
                    if (fileName.contains("/")) {
                        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                    }

                    // S3에서 해당 파일의 URL 조회 시도
                    try {
                        String s3Key = "profiles/" + fileName;
                        if (s3Service.doesObjectExist(s3Key)) {
                            // S3에 파일이 존재하면 S3 URL로 업데이트
                            String s3Url = s3Service.getObjectUrl(s3Key);
                            employee.setProfileImgUrl(s3Url);

                            // 선택적: DB에도 S3 URL로 업데이트
                            employeesMapper.updateProfileImgUrl(empNum, s3Url);
                            logger.info("프로필 이미지 URL을 S3 URL로 업데이트: {}", s3Url);
                        }
                    } catch (Exception e) {
                        logger.warn("S3 프로필 이미지 URL 조회 실패: {}", e.getMessage());
                        // 실패 시 기존 URL 유지
                    }
                }
            }

            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            logger.error("사용자 정보 조회 중 오류 발생: {}", e.getMessage());
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
//    @PatchMapping("/update")
//    public ResponseEntity<?> update(HttpServletRequest request,
//                                    @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
//                                    @RequestParam("phone") String phone,
//                                    @RequestParam("email") String email,
//                                    @RequestParam("isImageDeleted") boolean isImageDeleted) {
//        String empNum = (String)request.getAttribute("empNum");
//
//        // 프로필 이미지 처리 - 로컬 파일 시스템에 저장
//        String profileImgUrl = null;  // 기본 이미지
//
//        if(isImageDeleted){
//            profileImgUrl = "/assets/images/default-profile.png";
//        } else if (profileImage != null && !profileImage.isEmpty()) {
//            try {
//                // 로컬 파일 시스템에 업로드
//                profileImgUrl = profileUploadService.uploadProfileImage(profileImage);
//                logger.info("프로필 이미지가 로컬에 성공적으로 업데이트되었습니다: {}", profileImgUrl);
//            } catch (IOException e) {
//                // 업로드 실패 시 기본 이미지 사용 (로그만 남기고 계속 진행)
//                logger.warn("프로필 이미지 업데이트 실패, 기존 이미지 사용: {}", e.getMessage());
//            }
//        }
//
//        employeesService.updateEmpInfo(empNum, phone, email, profileImgUrl);
//
//        return ResponseEntity.ok(Collections.singletonMap("message", "업데이트 성공"));
//    }
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

        // 요청 ID 생성 (중복 업로드 방지용)
        String requestId = empNum + "_update_" + System.currentTimeMillis();

        // 현재 사용자 정보 조회
        EmployeesDTO employee = employeesMapper.findByEmpNum(empNum);
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "사용자 정보를 찾을 수 없습니다."
            ));
        }

        // 프로필 이미지 처리
        String profileImgUrl = null;  // null은 기존 이미지 유지를 의미

        if (isImageDeleted) {
            // 이미지 삭제를 선택한 경우 기본 이미지로 설정
            profileImgUrl = "/assets/images/default-profile.png";

            // 기존 이미지가 기본 이미지가 아니라면 S3에서 삭제 시도
            if (employee.getProfileImgUrl() != null &&
                    !employee.getProfileImgUrl().equals("/assets/images/default-profile.png") &&
                    employee.getProfileImgUrl().contains("amazonaws.com")) {
                try {
                    s3Service.deleteFile(employee.getProfileImgUrl());
                    logger.info("S3에서 기존 프로필 이미지 삭제 완료: {}", employee.getProfileImgUrl());
                } catch (Exception e) {
                    logger.warn("S3에서 기존 프로필 이미지 삭제 실패: {}", e.getMessage());
                }
            }
        } else if (profileImage != null && !profileImage.isEmpty()) {
            try {
                // 파일 확장자 검증
                String originalFilename = profileImage.getOriginalFilename();
                if (originalFilename != null) {
                    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                    if (!Arrays.asList(".jpg", ".jpeg", ".png", ".gif").contains(fileExtension)) {
                        return ResponseEntity.badRequest().body(Map.of(
                                "message", "지원하지 않는 파일 형식입니다. JPG, PNG, GIF 파일만 업로드 가능합니다."
                        ));
                    }
                }

                // ProfileUploadService는 로컬과 S3 모두에 업로드 처리
                profileImgUrl = profileUploadService.uploadProfileImageWithRequestId(profileImage, requestId);
                logger.info("프로필 이미지가 성공적으로 업데이트되었습니다: {}", profileImgUrl);

                // 기존 이미지가 기본 이미지가 아니고 S3 URL이면 삭제 시도
                if (employee.getProfileImgUrl() != null &&
                        !employee.getProfileImgUrl().equals("/assets/images/default-profile.png") &&
                        employee.getProfileImgUrl().contains("amazonaws.com")) {
                    try {
                        s3Service.deleteFile(employee.getProfileImgUrl());
                        logger.info("S3에서 기존 프로필 이미지 삭제 완료: {}", employee.getProfileImgUrl());
                    } catch (Exception e) {
                        logger.warn("S3에서 기존 프로필 이미지 삭제 실패: {}", e.getMessage());
                    }
                }
            } catch (IOException e) {
                // 업로드 실패 시 기존 이미지 유지 (로그만 남기고 계속 진행)
                logger.warn("프로필 이미지 업데이트 실패, 기존 이미지를 유지합니다: {}", e.getMessage());
                profileImgUrl = null;  // 기존 이미지 유지
            }
        }

        // 사용자 정보 업데이트
        employeesService.updateEmpInfo(empNum, phone, email, profileImgUrl);

        // 응답 메시지에 업데이트된 프로필 이미지 URL 정보 포함
        Map<String, Object> response = new HashMap<>();
        response.put("message", "업데이트 성공");
        if (profileImgUrl != null) {
            response.put("profileImgUrl", profileImgUrl);
        }

        return ResponseEntity.ok(response);
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
