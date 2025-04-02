package com.example.projectdemo.domain.booking.controller;
import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.booking.dto.MeetingRoomBookingDTO;
import com.example.projectdemo.domain.booking.dto.SuppliesBookingDTO;
import com.example.projectdemo.domain.booking.service.MeetingRoomService;
import com.example.projectdemo.domain.booking.service.SuppliesService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private EmployeesService employeeService;

    @Autowired
    private MeetingRoomService meetingRoomService;

    @Autowired
    private SuppliesService suppliesService;

    // 예약 메인 페이지 - 전체 예약 현황
    @GetMapping("/main")
    public String main(Model model, HttpServletRequest request) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String)request.getAttribute("empNum");

        if (empNum == null) { //예외처리
            return "redirect:/auth/login";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { //예외처리
            return "redirect:/auth/login";
        }

        // 오늘 날짜의 예약 정보 조회
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 오늘의 회의실 예약 목록
        List<MeetingRoomBookingDTO> meetingRoomBookings =
                meetingRoomService.getBookingsByDateRange(startOfDay, endOfDay);

        // 오늘의 비품 예약 목록
        List<SuppliesBookingDTO> suppliesBookings =
                suppliesService.getBookingsByDateRange(startOfDay, endOfDay);

        // 내 회의실 예약 목록
        List<MeetingRoomBookingDTO> myMeetingRoomBookings =
                meetingRoomService.getBookingsByEmpNum(empNum);

        // 내 비품 예약 목록
        List<SuppliesBookingDTO> mySuppliesBookings =
                suppliesService.getBookingsByEmpNum(empNum);

        model.addAttribute("employee", employee);
        model.addAttribute("meetingRoomBookings", meetingRoomBookings);
        model.addAttribute("suppliesBookings", suppliesBookings);
        model.addAttribute("myMeetingRoomBookings", myMeetingRoomBookings);
        model.addAttribute("mySuppliesBookings", mySuppliesBookings);
        model.addAttribute("today", LocalDate.now());

        return "booking/booking-main";
    }

    // 회의실 예약 페이지
    @GetMapping("/meeting-room")
    public String meetingRoom(Model model, HttpServletRequest request) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String)request.getAttribute("empNum");

        if (empNum == null) { //예외처리
            return "redirect:/auth/login";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { //예외처리
            return "redirect:/auth/login";
        }

        // 모든 회의실 목록
        model.addAttribute("employee", employee);
        model.addAttribute("meetingRooms", meetingRoomService.getAllMeetingRooms());
        model.addAttribute("myBookings", meetingRoomService.getBookingsByEmpNum(empNum));

        return "booking/booking-meeting-room";
    }

    // 비품 예약 페이지
    @GetMapping("/supplies")
    public String supplies(Model model, HttpServletRequest request) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String)request.getAttribute("empNum");

        if (empNum == null) { //예외처리
            return "redirect:/auth/login";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { //예외처리
            return "redirect:/auth/login";
        }

        // 모든 비품 목록
        model.addAttribute("employee", employee);
        model.addAttribute("supplies", suppliesService.getAllSupplies());
        model.addAttribute("myBookings", suppliesService.getBookingsByEmpNum(empNum));

        return "booking/booking-supplies";
    }
}
