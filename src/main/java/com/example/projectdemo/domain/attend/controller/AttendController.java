package com.example.projectdemo.domain.attend.controller;

import com.example.projectdemo.domain.attend.dto.AttendDTO;
import com.example.projectdemo.domain.attend.service.AttendService;
import com.example.projectdemo.domain.attendance.mapper.AttendanceMapper;
import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.leave.service.LeavesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.*;

@Controller
@RequestMapping("/attend")
public class AttendController {
    @Autowired
    private AttendService attendService;

    @Autowired
    private EmployeesMapper employeeMapper;

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private EmployeesService employeeService;

    @Autowired
    private LeavesService leavesService;

    @RequestMapping("/main")
    public String list(Model model, HttpServletRequest request) {


        int empId = (int)request.getAttribute("id");

        if (empId == 0) { //예외처리
            return "redirect:/auth/login";
        }

        EmployeesDTO employee = employeeMapper.findById(empId);
        if (employee == null) { //예외처리
            return "redirect:/auth/login";
        }

        List<AttendDTO>attendanceListByDate = attendService.selectByEmpIdAndDate(empId);

        List<Map<String, Object>> statisticsByYear = attendService.getAttendanceStatisticsThisYear(empId);

        int canUseLeaves = employee.getTotalLeave()-employee.getUsedLeave();

        BigDecimal totalWorkHours = attendService.getTotalWorkHoursThisYear(empId);
        int workDays = attendService.getWorkDaysThisYear(empId);
        BigDecimal correctionAverage = workDays > 0 ? totalWorkHours.divide(new BigDecimal(workDays), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;


        model.addAttribute("employee", employee);
        model.addAttribute("attendanceListByDate", attendanceListByDate);
        model.addAttribute("statisticsByYear", statisticsByYear);
        model.addAttribute("canUseLeaves", canUseLeaves);
        model.addAttribute("currentDate", new Date());
        model.addAttribute("totalWorkHours", totalWorkHours); // 총 근무시간
        model.addAttribute("workDays", workDays); // 근무일수
        model.addAttribute("correctionAverage", correctionAverage); // 보정평균
        return "/attend/attendMain";
    }

    @RequestMapping("/workSchedule")
    public String workSchedule(Model model) {
        return "/attend/attendWorkSchedule";
    }

    @RequestMapping("/getWorkSchedule")
    @ResponseBody
    public List<Map<String, Object>> getWorkSchedule(HttpServletRequest request) {
        int empId = (int) request.getAttribute("id");

        if (empId == 0) {
            throw new IllegalArgumentException("Invalid Employee ID");
        }

        // 근태 기록을 DB에서 가져오기
        List<AttendDTO> attendList = attendService.selectByEmpId(empId);

        // 근태 기록을 FullCalendar에 맞는 형식으로 변환
        List<Map<String, Object>> events = new ArrayList<>();

        for (AttendDTO attend : attendList) {
            if (attend.getWorkDate() != null) {
                // 출근 정보 이벤트
                if (attend.getCheckIn() != null) {
                    Map<String, Object> eventIn = new HashMap<>();
                    Calendar calendarIn = Calendar.getInstance();
                    calendarIn.setTime(attend.getWorkDate());  // workDate를 설정
                    calendarIn.set(Calendar.HOUR_OF_DAY, attend.getCheckIn().getHours());
                    calendarIn.set(Calendar.MINUTE, attend.getCheckIn().getMinutes());
                    calendarIn.set(Calendar.SECOND, 0);  // 초는 0으로 설정

                    eventIn.put("start", calendarIn.getTime());

                    // 출근시간이 지각이면 지각 표시, 아니면 출근만 표시 (시간 제외)
                    if ("지각".equals(attend.getStatus())) {
                        eventIn.put("title", "오전 지각");
                    } else {
                        eventIn.put("title", "오전 출근");
                    }
                    eventIn.put("description", "직원 ID: " + empId);
                    events.add(eventIn);
                }

                // 퇴근 정보 이벤트
                if (attend.getCheckOut() != null) {
                    Map<String, Object> eventOut = new HashMap<>();
                    Calendar calendarOut = Calendar.getInstance();
                    calendarOut.setTime(attend.getWorkDate());  // workDate를 설정
                    calendarOut.set(Calendar.HOUR_OF_DAY, attend.getCheckOut().getHours());
                    calendarOut.set(Calendar.MINUTE, attend.getCheckOut().getMinutes());
                    calendarOut.set(Calendar.SECOND, 0);  // 초는 0으로 설정

                    // 퇴근 이벤트에 start 속성 추가 (필수)
                    eventOut.put("start", calendarOut.getTime());

                    // 퇴근 시간 표시 (시간 제외)
                    eventOut.put("title", "오후 퇴근");
                    eventOut.put("description", "직원 ID: " + empId);
                    events.add(eventOut);
                }
            }
        }

        return events;  // JSON 형식으로 반환
    }


    @RequestMapping("/annualStatistics")
    public String annualStatistics(Model model, HttpServletRequest request,
                                   @RequestParam(required = false) Integer year) {
        int empId = (int) request.getAttribute("id");

        if (year == null) {
            year = Year.now().getValue();
        }

        if (empId == 0) {
            return "redirect:/auth/login";
        }

        EmployeesDTO employee = employeeMapper.findById(empId);
        if (employee == null) {
            return "redirect:/auth/login";
        }

        List<Map<String, Object>> monthlyStats = attendService.getMonthlyAttendanceStatistics(empId, year);

        List<Map<String, Object>> completeMonthlyStats = new ArrayList<>();
        BigDecimal totalTardy = BigDecimal.ZERO;
        BigDecimal totalEarlyLeave = BigDecimal.ZERO;
        BigDecimal totalAbsenteeism = BigDecimal.ZERO;
        BigDecimal totalVacationCount = BigDecimal.ZERO;
        BigDecimal totalWorkHours = BigDecimal.ZERO;
        long totalWorkDays = 0;

        for (int month = 1; month <= 12; month++) {
            boolean found = false;
            for (Map<String, Object> stats : monthlyStats) {
                if (stats.get("month").equals(Long.valueOf(month))) {
                    completeMonthlyStats.add(stats);

                    // 합계 계산
                    totalTardy = totalTardy.add((BigDecimal) stats.get("tardyCount"));
                    totalEarlyLeave = totalEarlyLeave.add((BigDecimal) stats.get("earlyLeaveCount"));
                    totalAbsenteeism = totalAbsenteeism.add((BigDecimal) stats.get("absenteeismCount"));
                    totalVacationCount = totalVacationCount.add((BigDecimal) stats.get("vacationCount"));
                    totalWorkHours = totalWorkHours.add((BigDecimal) stats.get("workHours"));
                    totalWorkDays += (long) stats.get("workDays"); // long 타입 직접 연산

                    found = true;
                    break;
                }
            }
            if (!found) {
                Map<String, Object> emptyStats = new HashMap<>();
                emptyStats.put("month", month);
                emptyStats.put("tardyCount", BigDecimal.ZERO);
                emptyStats.put("earlyLeaveCount", BigDecimal.ZERO);
                emptyStats.put("absenteeismCount", BigDecimal.ZERO);
                emptyStats.put("vacationCount", BigDecimal.ZERO);
                emptyStats.put("workHours", BigDecimal.ZERO);
                emptyStats.put("workDays", 0L); // long 값으로 초기화
                completeMonthlyStats.add(emptyStats);
            }
        }

        model.addAttribute("year", year);
        model.addAttribute("monthlyStats", completeMonthlyStats);
        model.addAttribute("employee", employee);
        model.addAttribute("totalTardy", totalTardy);
        model.addAttribute("totalEarlyLeave", totalEarlyLeave);
        model.addAttribute("totalAbsenteeism", totalAbsenteeism);
        model.addAttribute("totalVacationCount", totalVacationCount);
        model.addAttribute("totalWorkHours", totalWorkHours);
        model.addAttribute("totalWorkDays", totalWorkDays);

        return "/attend/attendAnnualStatistics";
    }



    @RequestMapping("/workDetails")
    public String workDetails(Model model) {
        return "/attend/attendWorkDetails";
    }
}
