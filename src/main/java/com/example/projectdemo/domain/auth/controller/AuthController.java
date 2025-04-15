package com.example.projectdemo.domain.auth.controller;

import com.example.projectdemo.domain.auth.dto.*;
import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.auth.service.EmailService;
import com.example.projectdemo.domain.auth.service.LogoutService;
import com.example.projectdemo.domain.auth.service.ProfileUploadService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.leave.service.LeavesService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.example.projectdemo.config.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final EmployeesMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmployeesService employeeService;
    private final EmailService emailService;
    private final ProfileUploadService profileUploadService;
    private final LogoutService logoutService;
    private final LeavesService leavesService;

    @Autowired
    public AuthController(JwtTokenUtil jwtTokenUtil,
                          EmployeesMapper employeeMapper,
                          PasswordEncoder passwordEncoder,
                          EmployeesService employeeService,
                          EmailService emailService,
                          ProfileUploadService profileUploadService,
                          LogoutService logoutService,
                          LeavesService leavesService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.employeeMapper = employeeMapper;
        this.passwordEncoder = passwordEncoder;
        this.employeeService = employeeService;
        this.emailService = emailService;
        this.profileUploadService = profileUploadService;
        this.logoutService = logoutService;
        this.leavesService = leavesService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginRequest, Model model) {
        try {
            EmployeesDTO employee = employeeMapper.findByEmpNum(loginRequest.getEmpNum());

            if (employee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of(
                                "status", HttpStatus.UNAUTHORIZED.value(),
                                "error", "Unauthorized",
                                "message", "사용자 정보가 존재하지 않습니다."
                        ));
            }

            // 비밀번호 확인
            if (!passwordEncoder.matches(loginRequest.getPassword(), employee.getPassword())) {
                throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            }

            // 계정 상태 확인
            if (!employee.isEnabled()) {
                throw new RuntimeException("비활성화된 계정입니다.");
            }

            // 마지막 로그인 시간 업데이트
            employeeMapper.updateLastLogin(loginRequest.getEmpNum(), LocalDateTime.now());

            // 임시 비밀번호 상태 확인
            boolean isTempPassword = employee.getTemp_password() != null && employee.getTemp_password() == 1;

            // JWT 토큰 생성
            String accessToken = jwtTokenUtil.generateToken(employee, isTempPassword);
            String refreshToken = jwtTokenUtil.generateRefreshToken(employee);

            // 응답 생성
            JwtResponseDTO jwtResponse = JwtResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .id(employee.getId())
                    .empNum(employee.getEmpNum())
                    .name(employee.getName())
                    .email(employee.getEmail())
                    .internalEmail(employee.getInternalEmail())
                    .role(employee.getRole())
                    .tempPassword(isTempPassword)
                    .departmentName(employee.getDepartmentName())
                    .positionTitle(employee.getPositionTitle())
                    .phone(employee.getPhone())
                    .profileImgUrl(employee.getProfileImgUrl())
                    .enabled(employee.isEnabled())
                    .lastLogin(employee.getLastLogin())
                    .gender(employee.getGender())
                    .hireDate(employee.getHireDate())
                    .accountNonExpired(employee.isAccountNonExpired())
                    .accountNonLocked(employee.isAccountNonLocked())
                    .credentialsNonExpired(employee.isCredentialsNonExpired())
                    .attendStatus(employee.getAttendStatus())
                    .build();

            return ResponseEntity.ok(jwtResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "error", "Unauthorized",
                            "message", "인증 중 오류가 발생했습니다."
                    ));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenDTO request) {
        try {
            // 리프레시 토큰 유효성 검증
            if (!jwtTokenUtil.validateToken(request.getRefreshToken())) {
                throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
            }

            // 토큰에서 사원번호 추출
            String empNum = jwtTokenUtil.getEmpNumFromToken(request.getRefreshToken());

            // 사용자 정보 조회
            EmployeesDTO employee = employeeMapper.findByEmpNum(empNum);
            if (employee == null) {
                throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
            }

            // 계정 상태 확인
            if (!employee.isEnabled()) {
                throw new RuntimeException("비활성화된 계정입니다.");
            }

            // 새 JWT 토큰 생성
            String newAccessToken = jwtTokenUtil.generateToken(employee);
            String newRefreshToken = jwtTokenUtil.generateRefreshToken(employee);

            // 응답 생성
            JwtResponseDTO jwtResponse = JwtResponseDTO.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .empNum(employee.getEmpNum())
                    .name(employee.getName())
                    .email(employee.getEmail())
                    .role(employee.getRole())
                    .tempPassword(false)  // 기본값 사용
                    .build();

            return ResponseEntity.ok(jwtResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "error", HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletResponse response) {

        // 헤더에서 토큰 추출
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 토큰이 유효하면 블랙리스트에 추가
        if (token != null) {
            logoutService.blacklistToken(token);
            System.out.println("토큰 블랙리스트 추가 성공: " + token.substring(0, Math.min(10, token.length())) + "...");
            System.out.println("현재 블랙리스트 크기: " + logoutService.getBlacklistSize());
        } else {
            System.out.println("로그아웃 처리: 토큰이 제공되지 않았습니다.");
        }

        // 쿠키 제거
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(Map.of("message", "로그아웃이 성공적으로 처리되었습니다."));
    }

    @PostMapping("/verify-employee")
    public ResponseEntity<?> verifyEmployee(@Valid @RequestBody Map<String, String> request) {
        try {
            String empNum = request.get("empNum");
            String name = request.get("name");
            String email = request.get("email");
            String ssn = request.get("ssn");

            // 필수 파라미터 검증
            if (empNum == null || name == null || email == null || ssn == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "모든 필드는 필수 입력 항목입니다."
                ));
            }

            // 직원 정보 검증
            EmployeesDTO employee = employeeService.verifyEmployeeForRegistration(empNum, name, email, ssn);

            if (employee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "입력하신 정보와 일치하는 직원 정보를 찾을 수 없습니다."
                ));
            }

            // 이미 회원가입한 경우
            if (employeeService.isAlreadyRegistered(empNum)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "이미 회원가입이 완료된 계정입니다. 로그인해주세요."
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "직원 정보가 확인되었습니다. 회원가입을 진행해주세요."
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping(value = "/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> register(
            @RequestPart("userData") @Valid SignupDTO userData,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        try {
            // 필수 파라미터 검증
            if (userData.getEmpNum() == null || userData.getGender() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "사원번호와 성별은 필수 입력 항목입니다."
                ));
            }

            // 이미 회원가입한 경우
            if (employeeService.isAlreadyRegistered(userData.getEmpNum())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "이미 회원가입이 완료된 계정입니다. 로그인해주세요."
                ));
            }

            // 프로필 이미지 처리 - 로컬 파일 시스템에 저장
            String profileImgUrl = "/assets/images/default-profile.png";  // 기본 이미지

            if (profileImage != null && !profileImage.isEmpty()) {
                try {
                    // 로컬 파일 시스템에 업로드
                    profileImgUrl = profileUploadService.uploadProfileImage(profileImage);
                    logger.info("프로필 이미지가 로컬에 성공적으로 업로드되었습니다: {}", profileImgUrl);
                } catch (IOException e) {
                    // 업로드 실패 시 기본 이미지 사용 (로그만 남기고 계속 진행)
                    logger.warn("프로필 이미지 업로드 실패, 기본 이미지 사용: {}", e.getMessage());
                }
            }

            // 회원가입 처리 및 임시 비밀번호 생성
            String tempPassword = employeeService.register(
                    userData.getEmpNum(),
                    profileImgUrl,
                    userData.getPhone(),
                    userData.getGender()
            );

            leavesService.initializeEmployeeLeave(userData.getEmpNum());

            return ResponseEntity.ok(Map.of(
                    "message", "회원가입이 완료되었습니다. 임시 비밀번호가 이메일로 발송되었습니다."
            ));

        } catch (Exception e) {
            logger.error("회원가입 처리 중 예외 발생", e);
            String errorMessage = e.getMessage() != null ? e.getMessage() : "회원가입 중 알 수 없는 오류가 발생했습니다.";
            return ResponseEntity.badRequest().body(Map.of("message", errorMessage));
        }

    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody PasswordChangeDTO request,
            @RequestAttribute(name = "empNum", required = false) String empNum) {

        // empNum이 null인 경우 권한 없음
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "error", HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                            "message", "권한이 없습니다."
                    ));
        }

        try {
            // 사용자 정보 조회
            EmployeesDTO employee = employeeMapper.findByEmpNum(empNum);
            if (employee == null) {
                throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
            }

            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(request.getCurrentPassword(), employee.getPassword())) {
                throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
            }

            // 비밀번호 변경
            employeeService.updatePassword(empNum, request.getNewPassword());

            // 새 토큰 발급
            String accessToken = jwtTokenUtil.generateToken(employee, false);
            String refreshToken = jwtTokenUtil.generateRefreshToken(employee);

            // 응답 생성
            JwtResponseDTO jwtResponse = JwtResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .empNum(employee.getEmpNum())
                    .name(employee.getName())
                    .email(employee.getEmail())
                    .role(employee.getRole())
                    .tempPassword(false)
                    .build();

            return ResponseEntity.ok(jwtResponse);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "error", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetDTO request) {
        try {
            employeeService.resetPassword(request.getEmail());

            return ResponseEntity.ok(Map.of(
                    "message", "임시 비밀번호가 이메일로 발송되었습니다."
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "error", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * 토큰 유효성 검증 API
     */
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // 헤더에서 토큰 추출
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("토큰 검증 실패: Authorization 헤더가 없거나 잘못된 형식입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "valid", false,
                            "message", "인증 토큰이 없거나 형식이 잘못되었습니다."
                    ));
        }

        String token = authHeader.substring(7);
        System.out.println("토큰 검증 시작: " + token.substring(0, Math.min(10, token.length())) + "...");

        // 토큰 유효성 검증
        if (jwtTokenUtil.validateToken(token)) {
            // 토큰에서 사원번호 추출
            String empNum = jwtTokenUtil.getEmpNumFromToken(token);
            System.out.println("토큰 검증 성공: 사원번호 " + empNum);

            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "empNum", empNum,
                    "message", "유효한 토큰입니다."
            ));
        } else {
            System.out.println("토큰 검증 실패: 만료되었거나 유효하지 않은 토큰입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "valid", false,
                            "message", "만료되었거나 유효하지 않은 토큰입니다."
                    ));
        }
    }

    /**
     * 현재 로그인한 사용자 정보 조회 API
     */
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "error", "Unauthorized",
                            "message", "인증 정보가 없습니다."
                    ));
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeMapper.findByEmpNum(empNum);

        if (employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Not Found",
                            "message", "사용자 정보를 찾을 수 없습니다."
                    ));
        }

        // 기본 응답 생성
        JwtResponseDTO userResponse = JwtResponseDTO.builder()
                .id(employee.getId())
                .empNum(employee.getEmpNum())
                .name(employee.getName())
                .email(employee.getEmail())
                .internalEmail(employee.getInternalEmail())
                .role(employee.getRole())
                .departmentName(employee.getDepartmentName())
                .positionTitle(employee.getPositionTitle())
                .phone(employee.getPhone())
                .profileImgUrl(employee.getProfileImgUrl())
                .enabled(employee.isEnabled())
                .lastLogin(employee.getLastLogin())
                .gender(employee.getGender())
                .hireDate(employee.getHireDate())
                .attendStatus(employee.getAttendStatus())
                .build();

        return ResponseEntity.ok(userResponse);
    }

}