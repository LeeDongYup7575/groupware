package com.example.projectdemo.domain.web.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.auth.service.LogoutService;
import com.example.projectdemo.domain.booking.dto.MeetingRoomBookingDTO;
import com.example.projectdemo.domain.booking.service.MeetingRoomService;
import com.example.projectdemo.domain.booking.util.BookingTimeUtils;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.notification.crawler.NoticeCrawler;
import com.example.projectdemo.domain.notification.model.Notice;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class WebController {
    private final EmployeesService employeesService;
    private final JwtTokenUtil jwtTokenUtil;
    private final NoticeCrawler noticeCrawler;
    private final LogoutService logoutService;
    private final MeetingRoomService meetingRoomService;

    // 메모리 캐시
    private static final ConcurrentHashMap<String, CacheEntry<List<Notice>>> CACHE = new ConcurrentHashMap<>();
    // 캐시 유효 시간 (1시간 = 3600000ms)
    private static final long CACHE_EXPIRY_TIME_MS = 3600000;

    @Autowired
    public WebController(EmployeesService employeesService, JwtTokenUtil jwtTokenUtil, NoticeCrawler noticeCrawler,
                         LogoutService logoutService,  MeetingRoomService meetingRoomService) {
        this.employeesService = employeesService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.noticeCrawler = noticeCrawler;
        this.logoutService = logoutService;
        this.meetingRoomService = meetingRoomService;
    }

    /**
     * 인트로 페이지 (루트 경로)
     * 유효한 토큰이 있으면 메인 페이지로 리다이렉트
     */
    @GetMapping(value = {"/"})
    public String intro(HttpServletRequest request) {
        // 요청에서 토큰 확인
        String token = getTokenFromRequest(request);

        if (token != null && jwtTokenUtil.validateToken(token)) {
            // 유효한 토큰이 있으면 메인 페이지로 리다이렉트
            System.out.println("유효한 토큰이 있어 메인 페이지로 리다이렉트합니다.");
            return "redirect:/main";
        }

        // 토큰이 없거나 유효하지 않으면 인트로 페이지 표시
        return "intro";
    }

    /**
     * 로그아웃 처리
     */
    @GetMapping("/auth/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // 요청에서 토큰 확인
        String token = getTokenFromRequest(request);

        if (token != null) {
            // 토큰을 블랙리스트에 추가
            logoutService.blacklistToken(token);
            System.out.println("로그아웃: 토큰이 블랙리스트에 추가되었습니다.");
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

        // 인트로 페이지로 리다이렉트
        return "redirect:/";
    }

    /**
     * 요청에서 토큰 추출
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 1. 헤더에서 토큰 확인
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 2. 쿼리 파라미터에서 토큰 확인
        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.isEmpty()) {
            return tokenParam;
        }

        // 3. 쿠키에서 토큰 확인
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

//    /**
//     * 로그인 후 페이지
//     */
//    @GetMapping("/main")
//    public String mainPage(HttpServletRequest request,
//                           @RequestParam(required = false) String token,
//                           Model model) {
//        String empNum = null;
//        String accessToken = null;
//
//        // 1. 헤더에서 토큰 확인
//        String authHeader = request.getHeader("Authorization");
//        System.out.println("Authorization 헤더: " + (authHeader != null ? "존재함" : "없음"));
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            accessToken = authHeader.substring(7);
//            if (jwtTokenUtil.validateToken(accessToken)) {
//                empNum = jwtTokenUtil.getEmpNumFromToken(accessToken);
//                System.out.println("헤더에서 유효한 토큰 확인: " + empNum);
//            } else {
//                System.out.println("헤더에 토큰이 있지만 유효하지 않음");
//            }
//        }
//
//        // 2. 쿼리 파라미터에서 토큰 확인
//        if (empNum == null && token != null) {
//            if (jwtTokenUtil.validateToken(token)) {
//                accessToken = token;
//                empNum = jwtTokenUtil.getEmpNumFromToken(token);
//                System.out.println("쿼리 파라미터에서 유효한 토큰 확인: " + empNum);
//            } else {
//                System.out.println("쿼리 파라미터에 토큰이 있지만 유효하지 않음");
//            }
//        }
//
//        // 3. 쿠키에서 토큰 확인
//        if (empNum == null) {
//            Cookie[] cookies = request.getCookies();
//            if (cookies != null) {
//                for (Cookie cookie : cookies) {
//                    if ("accessToken".equals(cookie.getName())) {
//                        accessToken = cookie.getValue();
//                        if (jwtTokenUtil.validateToken(accessToken)) {
//                            empNum = jwtTokenUtil.getEmpNumFromToken(accessToken);
//                            System.out.println("쿠키에서 유효한 토큰 확인: " + empNum);
//                        } else {
//                            System.out.println("쿠키에 토큰이 있지만 유효하지 않음");
//                        }
//                        break;
//                    }
//                }
//            } else {
//                System.out.println("쿠키가 없음");
//            }
//        }
//
//        // 토큰이 없거나 유효하지 않은 경우
//        if (empNum == null) {
//            System.out.println("유효한 토큰을 찾을 수 없어 로그인 페이지로 리다이렉트");
//            return "redirect:/auth/login";
//        }
//
//        System.out.println("인증된 사용자 번호: " + empNum);
//
//        // 토큰에서 추가 정보 추출 또는 DB에서 사용자 정보 로드
//        EmployeesDTO employee = employeesService.findByEmpNum(empNum);
//        if (employee == null) {
//            System.out.println("사용자 정보를 찾을 수 없음: " + empNum);
//            return "redirect:/auth/login";
//        }
//
//        // 사용자 정보를 모델에 추가
//        model.addAttribute("employee", employee);
//
//        // 공지사항 데이터 로드 및 모델에 추가
//        List<Notice> notices = getCachedNotices();
//        model.addAttribute("notices", notices);
//
//        return "/main";
//    }

    /**
     * 로그인 후 페이지
     */
    @GetMapping("/main")
    public String mainPage(HttpServletRequest request,
                           @RequestParam(required = false) String token,
                           Model model) {
        String empNum = null;
        String accessToken = null;

        // 1. 헤더에서 토큰 확인
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization 헤더: " + (authHeader != null ? "존재함" : "없음"));

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
            if (jwtTokenUtil.validateToken(accessToken)) {
                empNum = jwtTokenUtil.getEmpNumFromToken(accessToken);
                System.out.println("헤더에서 유효한 토큰 확인: " + empNum);
            } else {
                System.out.println("헤더에 토큰이 있지만 유효하지 않음");
            }
        }

        // 2. 쿼리 파라미터에서 토큰 확인
        if (empNum == null && token != null) {
            if (jwtTokenUtil.validateToken(token)) {
                accessToken = token;
                empNum = jwtTokenUtil.getEmpNumFromToken(token);
                System.out.println("쿼리 파라미터에서 유효한 토큰 확인: " + empNum);
            } else {
                System.out.println("쿼리 파라미터에 토큰이 있지만 유효하지 않음");
            }
        }

        // 3. 쿠키에서 토큰 확인
        if (empNum == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        accessToken = cookie.getValue();
                        if (jwtTokenUtil.validateToken(accessToken)) {
                            empNum = jwtTokenUtil.getEmpNumFromToken(accessToken);
                            System.out.println("쿠키에서 유효한 토큰 확인: " + empNum);
                        } else {
                            System.out.println("쿠키에 토큰이 있지만 유효하지 않음");
                        }
                        break;
                    }
                }
            } else {
                System.out.println("쿠키가 없음");
            }
        }

        // 토큰이 없거나 유효하지 않은 경우
        if (empNum == null) {
            System.out.println("유효한 토큰을 찾을 수 없어 로그인 페이지로 리다이렉트");
            return "redirect:/auth/login";
        }

        // 토큰에서 추가 정보 추출 또는 DB에서 사용자 정보 로드
        EmployeesDTO employee = employeesService.findByEmpNum(empNum);
        if (employee == null) {
            System.out.println("사용자 정보를 찾을 수 없음: " + empNum);
            return "redirect:/auth/login";
        }

        // 사용자 정보를 모델에 추가
        model.addAttribute("employee", employee);

        // 공지사항 데이터 로드 및 모델에 추가
        List<Notice> notices = getCachedNotices();
        model.addAttribute("notices", notices);

        // 오늘 날짜의 예약 정보 조회
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 오늘의 회의실 예약 목록
        List<MeetingRoomBookingDTO> meetingRoomBookings =
                meetingRoomService.getBookingsByDateRange(startOfDay, endOfDay);
        model.addAttribute("meetingRoomBookings", meetingRoomBookings);

        // 내 회의실 예약 목록
        List<MeetingRoomBookingDTO> myMeetingRoomBookings =
                meetingRoomService.getBookingsByEmpNum(empNum);
        model.addAttribute("myMeetingRoomBookings", myMeetingRoomBookings);

        // 내 예약 개수 계산
        int myBookingsCount = myMeetingRoomBookings.size();
        model.addAttribute("myBookingsCount", myBookingsCount);

        // 현재 날짜 정보 추가
        LocalDateTime now = LocalDateTime.now();
        model.addAttribute("currentDate", now);

        // 현재 날짜를 포맷팅하여 추가 (화면에 표시할 용도)
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E) HH:mm:ss");
        model.addAttribute("formattedDate", now.format(dateFormatter));

        // 회의실 유틸리티 추가
        model.addAttribute("bookingUtils", new BookingTimeUtils());

        return "/main";
    }

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    /**
     * 로그인 페이지 (auth 경로)
     */
    @GetMapping("/auth/login")
    public String authLogin() {
        return "auth/login";
    }

    /**
     * 회원가입 페이지
     */
    @GetMapping("/auth/signup")
    public String signup() {
        return "auth/signup";
    }

    /**
     * 비밀번호 찾기 페이지
     */
    @GetMapping("/auth/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    /**
     * 비밀번호 변경 페이지
     */
    @GetMapping("/auth/change-password")
    public String changePassword() {
        return "auth/change-password";
    }

    /**
     * 쿠키에서 값을 가져오는 유틸리티 메서드
     */
    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private List<Notice> getCachedNotices() {
        String cacheKey = "notices";

        // 캐시에서 데이터 확인
        CacheEntry<List<Notice>> cachedNotices = CACHE.get(cacheKey);

        // 캐시가 유효한지 확인
        if (isValidCache(cachedNotices)) {
            System.out.println("캐시에서 공지사항 데이터를 로드합니다.");
            return cachedNotices.getData();
        }

        // 캐시가 없거나 만료되었으면 크롤링 실행
        System.out.println("캐시가 없거나 만료되어 크롤링을 시작합니다.");
        List<Notice> freshNotices;

        try {
            // NoticeCrawler를 사용하여 공지사항 데이터 크롤링
            System.out.println("NoticeCrawler.crawlNotices() 호출 시작");
            freshNotices = noticeCrawler.crawlNotices();
            System.out.println("NoticeCrawler.crawlNotices() 호출 완료");
            System.out.println("크롤링된 공지사항 수: " + freshNotices.size());

            // 결과를 캐시에 저장
            CACHE.put(cacheKey, new CacheEntry<>(freshNotices));
            System.out.println("공지사항 데이터가 캐시에 저장되었습니다.");
        } catch (Exception e) {
            System.out.println("공지사항 크롤링 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            // 오류 발생 시 빈 리스트로 초기화하여 계속 진행
            freshNotices = new ArrayList<>();
        }

        return freshNotices;
    }

    /**
     * 캐시가 유효한지 확인하는 메소드
     */
    private boolean isValidCache(CacheEntry<List<Notice>> cacheEntry) {
        // 캐시가 존재하고 만료되지 않았는지 확인
        return cacheEntry != null &&
                (System.currentTimeMillis() - cacheEntry.getCreatedTime() < CACHE_EXPIRY_TIME_MS);
    }

    /**
     * 캐시 데이터와 생성 시간을 저장하는 내부 클래스
     */
    private static class CacheEntry<T> {
        private final T data;
        private final long createdTime;

        public CacheEntry(T data) {
            this.data = data;
            this.createdTime = System.currentTimeMillis();
        }

        public T getData() {
            return data;
        }

        public long getCreatedTime() {
            return createdTime;
        }
    }

    /**
     * 사이드바 프래그먼트 경로
     */
    @GetMapping("/fragments/sidebar/main-sidebar")
    public String getMainSidebar() {
        return "fragments/sidebar/main-sidebar :: sidebar";
    }

    /**
     * 게시판
     */
    @GetMapping("/board")
    public String board(Model model) {
        model.addAttribute("pageTitle", "게시판");
        return "board/list";
    }

    /**
     * 약관동의 페이지 이동
     */
    @GetMapping("/auth/agreement")
    public String agreement() {
        return "auth/privacy-agreement";
    }
}