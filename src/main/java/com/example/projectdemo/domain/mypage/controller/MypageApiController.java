package com.example.projectdemo.domain.mypage.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.auth.service.ProfileUploadService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
public class MypageApiController {
    private static final Logger logger = LoggerFactory.getLogger(MypageApiController.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final EmployeesService employeesService;
    private final ProfileUploadService profileUploadService;

    @Autowired
    public MypageApiController(JwtTokenUtil jwtTokenUtil,
                               EmployeesService employeesService,
                               ProfileUploadService profileUploadService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.employeesService = employeesService;
        this.profileUploadService = profileUploadService;
    }

    @GetMapping("/info")
    public ResponseEntity<EmployeesDTO> info(HttpServletRequest request) {
        String empNum = (String)request.getAttribute("empNum");

        EmployeesDTO employee = employeesService.findByEmpNum(empNum);

        return ResponseEntity.ok(employee);
    }

    @GetMapping("/security")
    public ResponseEntity<Map<String, String>> security(HttpServletRequest request) {
        String empNum = (String)request.getAttribute("empNum");

        Map<String, String> response = employeesService.selectLastLogin(empNum);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update")
    public ResponseEntity<?> update(HttpServletRequest request,
                                    @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                    @RequestParam("phone") String phone,
                                    @RequestParam("email") String email) {
        String empNum = (String)request.getAttribute("empNum");

        // 프로필 이미지 처리 - 로컬 파일 시스템에 저장
        String profileImgUrl = "/assets/images/default-profile.png";  // 기본 이미지

        if (profileImage != null && !profileImage.isEmpty()) {
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

//    @GetMapping("/activities/{menu}")
}
