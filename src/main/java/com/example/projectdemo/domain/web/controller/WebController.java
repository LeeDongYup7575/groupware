package com.example.projectdemo.domain.web.controller;

import com.example.projectdemo.domain.attend.service.AttendService;
import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.auth.service.LogoutService;
import com.example.projectdemo.domain.board.service.PostsService;
import com.example.projectdemo.domain.booking.service.MeetingRoomService;
import com.example.projectdemo.domain.booking.service.SuppliesService;
import com.example.projectdemo.domain.booking.util.BookingTimeUtils;
import com.example.projectdemo.domain.edsm.services.EdsmService;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.notification_scraping.crawler.NoticeCrawler;
import com.example.projectdemo.domain.notification_scraping.model.Notice;
import com.example.projectdemo.main.MainPageData;
import com.example.projectdemo.main.MainPageFacade;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    private final AttendService attendService;
    private final SuppliesService suppliesService;
    private final EdsmService edsmService;
    private final PostsService postsService;

    // 메모리 캐시
    private static final ConcurrentHashMap<String, CacheEntry<List<Notice>>> CACHE = new ConcurrentHashMap<>();
    // 캐시 유효 시간 (1시간 = 3600000ms)
    private static final long CACHE_EXPIRY_TIME_MS = 3600000;

    private final MainPageFacade mainPageFacade;

    @Autowired
    public WebController(EmployeesService employeesService, JwtTokenUtil jwtTokenUtil, NoticeCrawler noticeCrawler,
                         LogoutService logoutService, MeetingRoomService meetingRoomService,
                         AttendService attendService, SuppliesService suppliesService, EdsmService edsmService, PostsService postsService,
                         MainPageFacade mainPageFacade) {
        this.employeesService = employeesService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.noticeCrawler = noticeCrawler;
        this.logoutService = logoutService;
        this.meetingRoomService = meetingRoomService;
        this.attendService = attendService;
        this.suppliesService = suppliesService;
        this.edsmService = edsmService;
        this.postsService = postsService;
        this.mainPageFacade = mainPageFacade;
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


    /**
     * 로그인 후 페이지
     * Facade 패턴을 적용하여 리팩토링 완료
     */
    @GetMapping("/main")
    public String mainPage(HttpServletRequest request,
                           @RequestParam(required = false) String token,
                           Model model) {

        MainPageData mainPageData = mainPageFacade.prepareMainPageData(request, token);

        // 인증 실패 또는 사용자 정보를 찾을 수 없는 경우
        if (mainPageData == null) {
            System.out.println("유효한 토큰을 찾을 수 없거나 사용자 정보를 찾을 수 없어 로그인 페이지로 리다이렉트");
            return "redirect:/auth/login";
        }

        // 모델에 데이터 추가
        model.addAttribute("employee", mainPageData.getEmployee());
        model.addAttribute("notices", mainPageData.getNotices());
        model.addAttribute("publicList", mainPageData.getPublicList());
        model.addAttribute("meetingRoomBookings", mainPageData.getMeetingRoomBookings());
        model.addAttribute("myMeetingRoomBookings", mainPageData.getMyMeetingRoomBookings());
        model.addAttribute("myBookingsCount", mainPageData.getMyBookingsCount());
        model.addAttribute("currentDate", mainPageData.getCurrentDate());
        model.addAttribute("attendanceListByDate", mainPageData.getAttendanceListByDate());
        model.addAttribute("edsmCount", mainPageData.getEdsmCount());

        // 회의실 유틸리티 추가
        model.addAttribute("bookingUtils", new BookingTimeUtils());

        return "main";
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
     * 약관동의 페이지 이동
     */
    @GetMapping("/auth/agreement")
    public String agreement() {
        return "auth/privacy-agreement";
    }
}