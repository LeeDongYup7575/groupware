package com.example.projectdemo.domain.attend.controller;

import com.example.projectdemo.domain.attend.dto.AttendDTO;
import com.example.projectdemo.domain.attend.service.AttendService;
import com.example.projectdemo.domain.attendance.mapper.AttendanceMapper;
import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.leave.dto.LeavesDTO;
import com.example.projectdemo.domain.leave.service.LeavesService;
import com.example.projectdemo.domain.work.dto.OverTimeDTO;
import com.example.projectdemo.domain.work.service.WorkService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private WorkService workService;

    private int parseTimeToMinutes(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return 0;
        }

        String[] parts = timeStr.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    private String formatMinutesToTime(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
        return String.format("%02d:%02d", h, m);
    }

    @RequestMapping("/main")
    public String list(Model model, HttpServletRequest request) throws Exception{
        int empId = (int) request.getAttribute("id");

        if (empId == 0) {
            return "redirect:/auth/login";
        }

        EmployeesDTO employee = employeeMapper.findById(empId);
        if (employee == null) {
            return "redirect:/auth/login";
        }

        List<AttendDTO> attendanceListByDate = attendService.selectByEmpIdAndDate(empId);
        List<Map<String, Object>> statisticsByYear = attendService.getAttendanceStatisticsThisYear(empId);
        List<Map<String, Object>> statisticsByMonth = attendService.getMonthlyAttendanceStatisticsThisMonth(empId);
        List<Map<String, Object>> statisticsByWeek = attendService.getWeeklyAttendanceStatisticsThisWeek(empId);

        BigDecimal canUseLeaves = employee.getTotalLeave().subtract(employee.getUsedLeave());

        BigDecimal totalWorkHours = attendService.getTotalWorkHoursThisYear(empId);
        int workDays = attendService.getWorkDaysThisYear(empId);
        BigDecimal correctionAverage = workDays > 0 ? totalWorkHours.divide(new BigDecimal(workDays), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

        // 근태 기록을 DB에서 가져오기
        List<AttendDTO> attendList = attendService.selectByEmpId(empId);
        ObjectMapper mapper = new ObjectMapper();
        String attendListJson = mapper.writeValueAsString(attendList);

        //휴가 기록을 DB에서 가져오기
        List<LeavesDTO> leavesList = leavesService.selectLeavesByEmpId(empId);

        // 연장 근무 기록을 DB에서 가져오기
        List<OverTimeDTO> overtimesList = workService.selectOverTimeListByEmpId(empId);

        model.addAttribute("employee", employee);
        model.addAttribute("attendanceListByDate", attendanceListByDate);
        model.addAttribute("statisticsByYear", statisticsByYear);
        model.addAttribute("statisticsByMonth", statisticsByMonth);
        model.addAttribute("statisticsByWeek", statisticsByWeek);
        model.addAttribute("canUseLeaves", canUseLeaves);
        model.addAttribute("currentDate", new Date());
        model.addAttribute("totalWorkHours", totalWorkHours);
        model.addAttribute("workDays", workDays);
        model.addAttribute("correctionAverage", correctionAverage);
        model.addAttribute("leavesList", leavesList);
        model.addAttribute("attendListJson", attendListJson);
        model.addAttribute("overtimesList", overtimesList);

        return "/attend/attendMain";
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
        List<Map<String, Object>> overtimeStats = workService.getMonthlyOvertimeHours(empId, year);
        List<Map<String, Object>> leaveStats = leavesService.getMonthlyLeaveHours(empId, year);

        Map<Integer, String> overtimeMap = new HashMap<>();
        for (Map<String, Object> stat : overtimeStats) {
            int month = ((Number) stat.get("month")).intValue();
            String time = (String) stat.get("total_overtime");
            overtimeMap.put(month, time);
        }

        Map<Integer, String> leaveMap = new HashMap<>();
        for (Map<String, Object> stat : leaveStats) {
            int month = ((Number) stat.get("month")).intValue();
            String time = (String) stat.get("total_leave");
            leaveMap.put(month, time);
        }

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
                if (((Number) stats.get("month")).intValue() == month) {
                    stats.put("totalOvertime", overtimeMap.getOrDefault(month, "00:00"));
                    stats.put("totalLeave", leaveMap.getOrDefault(month, "00:00"));
                    completeMonthlyStats.add(stats);

                    totalTardy = totalTardy.add((BigDecimal) stats.get("tardyCount"));
                    totalEarlyLeave = totalEarlyLeave.add((BigDecimal) stats.get("earlyLeaveCount"));
                    totalAbsenteeism = totalAbsenteeism.add((BigDecimal) stats.get("absenteeismCount"));
                    totalVacationCount = totalVacationCount.add((BigDecimal) stats.get("vacationCount"));
                    totalWorkHours = totalWorkHours.add((BigDecimal) stats.get("workHours"));
                    totalWorkDays += (long) stats.get("workDays");

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
                emptyStats.put("workDays", 0L);
                emptyStats.put("totalOvertime", overtimeMap.getOrDefault(month, "00:00"));
                emptyStats.put("totalLeave", leaveMap.getOrDefault(month, "00:00"));
                completeMonthlyStats.add(emptyStats);
            }
        }

        // ✅ 기준 시간 설정 및 보정 근무 시간 계산
        String[] standardHours = {
                "168:00", "152:00", "160:00", "176:00", "160:00", "160:00",
                "184:00", "160:00", "160:00", "168:00", "168:00", "168:00"
        };

        List<String> correctedWorkTimeList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            int standard = parseTimeToMinutes(standardHours[i]);
            int leave = parseTimeToMinutes(leaveMap.getOrDefault(i + 1, "00:00"));
            int overtime = parseTimeToMinutes(overtimeMap.getOrDefault(i + 1, "00:00"));
            int corrected = standard - leave + overtime;
            correctedWorkTimeList.add(formatMinutesToTime(corrected));
        }

        int totalMinutes = 0;
        for (String timeStr : correctedWorkTimeList) {
            totalMinutes += parseTimeToMinutes(timeStr);
        }

        String totalCorrectedWorkTime = formatMinutesToTime(totalMinutes);

        model.addAttribute("totalCorrectedWorkTime", totalCorrectedWorkTime);
        model.addAttribute("year", year);
        model.addAttribute("monthlyStats", completeMonthlyStats);
        model.addAttribute("employee", employee);
        model.addAttribute("totalTardy", totalTardy);
        model.addAttribute("totalEarlyLeave", totalEarlyLeave);
        model.addAttribute("totalAbsenteeism", totalAbsenteeism);
        model.addAttribute("totalVacationCount", totalVacationCount);
        model.addAttribute("totalWorkHours", totalWorkHours);
        model.addAttribute("totalWorkDays", totalWorkDays);
        model.addAttribute("correctedWorkTimeList", correctedWorkTimeList); // ✅ 추가

        return "/attend/attendAnnualStatistics";
    }
}
