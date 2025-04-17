package com.example.projectdemo.main;

import com.example.projectdemo.domain.attend.dto.AttendDTO;
import com.example.projectdemo.domain.attend.service.AttendService;
import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.board.dto.PostsDTO;
import com.example.projectdemo.domain.board.service.PostsService;
import com.example.projectdemo.domain.booking.dto.MeetingRoomBookingDTO;
import com.example.projectdemo.domain.booking.dto.SuppliesBookingDTO;
import com.example.projectdemo.domain.booking.service.MeetingRoomService;
import com.example.projectdemo.domain.booking.service.SuppliesService;
import com.example.projectdemo.domain.edsm.dto.EdsmDocumentDTO;
import com.example.projectdemo.domain.edsm.services.EdsmService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.notification.model.Notice;
import com.example.projectdemo.domain.notification.service.NoticeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MainPageFacadeImpl implements MainPageFacade {

    private final JwtTokenUtil jwtTokenUtil;
    private final EmployeesService employeesService;
    private final PostsService postsService;
    private final MeetingRoomService meetingRoomService;
    private final SuppliesService suppliesService;
    private final AttendService attendService;
    private final EdsmService edsmService;
    private final NoticeService noticeService;

    @Autowired
    public MainPageFacadeImpl(JwtTokenUtil jwtTokenUtil,
                              EmployeesService employeesService,
                              PostsService postsService,
                              MeetingRoomService meetingRoomService,
                              SuppliesService suppliesService,
                              AttendService attendService,
                              EdsmService edsmService,
                              NoticeService noticeService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.employeesService = employeesService;
        this.postsService = postsService;
        this.meetingRoomService = meetingRoomService;
        this.suppliesService = suppliesService;
        this.attendService = attendService;
        this.edsmService = edsmService;
        this.noticeService = noticeService;
    }

    @Override
    public MainPageData prepareMainPageData(HttpServletRequest request, String tokenParam) {
        MainPageData data = new MainPageData();

        // 1. 토큰 처리 및 사용자 인증
        String empNum = authenticateUser(request, tokenParam, data);
        if (empNum == null) {
            return null; // 인증 실패
        }

        // 2. 사용자 정보 로드
        EmployeesDTO employee = loadEmployeeData(empNum);
        if (employee == null) {
            return null; // 사용자 정보 로드 실패
        }
        data.setEmployee(employee);

        // 3. 공지사항 및 게시글 로드
        loadNoticesAndPosts(data);

        // 4. 회의실 예약 정보 로드
        loadMeetingRoomBookings(data, empNum);

        // 5. 내 비품 예약 및 예약 카운트 계산
        calculateBookingsCount(data, empNum);

        // 6. 현재 날짜 설정
        data.setCurrentDate(LocalDateTime.now());

        // 7. 출퇴근 시간 로드
        loadAttendanceData(request, data);

        // 8. 전자결재 문서 카운트 로드
        loadEdsmData(empNum, data);

        return data;
    }

    private String authenticateUser(HttpServletRequest request, String tokenParam, MainPageData data) {
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
        if (empNum == null && tokenParam != null) {
            if (jwtTokenUtil.validateToken(tokenParam)) {
                accessToken = tokenParam;
                empNum = jwtTokenUtil.getEmpNumFromToken(tokenParam);
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

        if (accessToken != null) {
            data.setAccessToken(accessToken);
        }

        return empNum;
    }

    private EmployeesDTO loadEmployeeData(String empNum) {
        EmployeesDTO employee = employeesService.findByEmpNum(empNum);
        if (employee == null) {
            System.out.println("사용자 정보를 찾을 수 없음: " + empNum);
            return null;
        }
        return employee;
    }

    private void loadNoticesAndPosts(MainPageData data) {
        // 공지사항 데이터 로드
        List<Notice> notices = getCachedNotices();
        data.setNotices(notices);

        // 공개 게시판 게시글 로드
        List<PostsDTO> publicList = postsService.getPostsByBoardId(2);
        data.setPublicList(publicList);
    }

    private List<Notice> getCachedNotices() {
        // 캐싱 로직 구현 (원본에서는 이 부분이 생략되어 있어 임시 구현)
        return new ArrayList<>(); // 실제 구현에서는 캐시된 공지사항을 반환
    }

    private void loadMeetingRoomBookings(MainPageData data, String empNum) {
        // 오늘 날짜의 예약 정보 조회
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 오늘의 회의실 예약 목록
        List<MeetingRoomBookingDTO> meetingRoomBookings =
                meetingRoomService.getBookingsByDateRange(startOfDay, endOfDay);
        data.setMeetingRoomBookings(meetingRoomBookings);

        // 내 회의실 예약 목록
        List<MeetingRoomBookingDTO> myMeetingRoomBookings =
                meetingRoomService.getBookingsByEmpNum(empNum);
        data.setMyMeetingRoomBookings(myMeetingRoomBookings);
    }

    private void calculateBookingsCount(MainPageData data, String empNum) {
        // 내 비품 예약 목록
        List<SuppliesBookingDTO> mySuppliesBookings = suppliesService.getBookingsByEmpNum(empNum);
        int mySuppliesCount = mySuppliesBookings.size();

        // 내 예약 개수 계산
        int myBookingsCount = data.getMyMeetingRoomBookings().size() + mySuppliesCount;
        System.out.println(mySuppliesCount + " : 비품예약 개수" +
                data.getMyMeetingRoomBookings().size() + " : 룸예약현황");

        data.setMyBookingsCount(myBookingsCount);
    }

    private void loadAttendanceData(HttpServletRequest request, MainPageData data) {
        // 근태관리 출퇴근 시간 추가
        int empId = (int) request.getAttribute("id");
        if (empId != 0) {
            List<AttendDTO> attendanceListByDate = attendService.selectByEmpIdAndDate(empId);
            data.setAttendanceListByDate(attendanceListByDate);
        }
    }

    private void loadEdsmData(String empNum, MainPageData data) {
        // 전자결재 문서 카운트
        List<EdsmDocumentDTO> edsmList = edsmService.selectByAllApprovalFromIdWait(empNum);
        int edsmCount = edsmList.size();
        data.setEdsmCount(edsmCount);
    }

    private void loadNoticesAndPosts(MainPageData data) {
        // 공지사항 데이터 로드
        List<Notice> notices = noticeService.getCachedNotices();  // 변경된 부분
        data.setNotices(notices);

        // 공개 게시판 게시글 로드
        List<PostsDTO> publicList = postsService.getPostsByBoardId(2);
        data.setPublicList(publicList);
    }
}
