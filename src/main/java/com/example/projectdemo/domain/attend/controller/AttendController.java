package com.example.projectdemo.domain.attend.controller;

import com.example.projectdemo.domain.attend.dto.AttendDTO;
import com.example.projectdemo.domain.attend.service.AttendService;
import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
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

        model.addAttribute("employee", employee);

        List<AttendDTO>list = attendService.selectByEmpIdAndDate(empId);

        List<Map<String, Object>> statisticsByYear = attendService.getAttendanceStatisticsThisYear(empId);

        int canUseLeaves = employee.getTotalLeave()-employee.getUsedLeave();

        BigDecimal totalWorkHours = attendService.getTotalWorkHoursThisYear(empId);
        int workDays = attendService.getWorkDaysThisYear(empId);
        BigDecimal correctionAverage = workDays > 0 ? totalWorkHours.divide(new BigDecimal(workDays), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

        model.addAttribute("list", list);
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


    @RequestMapping("/leavesHistory")
    public String leavesHistory(Model model) {
        return "/attend/attendLeavesHistory";
    }

    @RequestMapping("/annualStatistics")
    public String annualStatistics(Model model, HttpServletRequest request,
                                   @RequestParam(required = false) Integer year) {
        int empId = (int)request.getAttribute("id");

        // 연도가 지정되지 않은 경우 현재 연도를 기본값으로 사용
        if (year == null) {
            year = Year.now().getValue();
        }

        if (empId == 0) { //예외처리
            return "redirect:/auth/login";
        }

        EmployeesDTO employee = employeeMapper.findById(empId);
        if (employee == null) { //예외처리
            return "redirect:/auth/login";
        }

        // 지정된 연도의 통계 가져오기
        List<Map<String, Object>> monthlyStats = attendService.getMonthlyAttendanceStatistics(empId, year);

        // 월별 데이터가 비어있는 경우 0으로 채우기
        List<Map<String, Object>> completeMonthlyStats = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            boolean found = false;
            for (Map<String, Object> stats : monthlyStats) {
                if (stats.get("month").equals(Long.valueOf(month))) {
                    completeMonthlyStats.add(stats);
                    found = true;
                    break;
                }
            }
            if (!found) {
                // 해당 월에 값이 없으면 0으로 채움
                Map<String, Object> emptyStats = new HashMap<>();
                emptyStats.put("month", month);
                emptyStats.put("tardyCount", 0);
                emptyStats.put("earlyLeaveCount", 0);
                emptyStats.put("absenteeismCount", 0);
                emptyStats.put("vacationCount", 0);
                emptyStats.put("totalWorkHours", 0);
                emptyStats.put("workDays", 0);
                completeMonthlyStats.add(emptyStats);
            }
        }
        model.addAttribute("year", year);
        model.addAttribute("monthlyStats", completeMonthlyStats);
        model.addAttribute("employee", employee);

        return "/attend/attendAnnualStatistics";
    }

    @RequestMapping("/workDetails")
    public String workDetails(Model model) {
        return "/attend/attendWorkDetails";
    }
}
