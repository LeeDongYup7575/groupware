package com.example.projectdemo.domain.attend.controller;

import com.example.projectdemo.domain.attend.dto.AttendDTO;
import com.example.projectdemo.domain.attend.service.AttendService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/attend")
public class AttendController {
    @Autowired
    private AttendService attendService;

    @Autowired
    private EmployeesMapper employeeMapper;

    @RequestMapping("/main")
    public String list(Model model) {
        int empId = 6;
        List<AttendDTO>list = attendService.selectByEmpIdAndDate(empId);
        List<Map<String, Object>> statisticsByYear = attendService.getAttendanceStatisticsThisYear(empId);
        EmployeesDTO employeesDTO = employeeMapper.findById(empId);
        int CanUseLeaves = employeesDTO.getTotalLeave()-employeesDTO.getUsedLeave();

        BigDecimal totalWorkHours = attendService.getTotalWorkHoursThisYear(empId);
        int workDays = attendService.getWorkDaysThisYear(empId);
        BigDecimal correctionAverage = workDays > 0 ? totalWorkHours.divide(new BigDecimal(workDays), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

        model.addAttribute("list", list);
        model.addAttribute("statisticsByYear", statisticsByYear);
        model.addAttribute("CanUseLeaves", CanUseLeaves);
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

//    @GetMapping("/calendar-events")
//    @ResponseBody
//    public List<Map<String, Object>> getCalendarEvents() {
//        int empId = 6; // 예제 ID (추후 로그인 연동 필요)
//        List<Map<String, Object>> events = new ArrayList<>();
//
//        // 근무 일정 조회
//        List<Map<String, Object>> workSchedules = attendService.getWorkSchedules(empId);
//        for (Map<String, Object> schedule : workSchedules) {
//            Map<String, Object> event = new HashMap<>();
//            event.put("title", schedule.get("workType"));
//            event.put("start", schedule.get("workDate") + "T" + schedule.get("startTime"));
//            event.put("end", schedule.get("workDate") + "T" + schedule.get("endTime"));
//            event.put("color", "#28a745"); // 초록색으로 표시
//            events.add(event);
//        }
//
//        return events;
//    }

    @RequestMapping("/leavesHistory")
    public String leavesHistory(Model model) {
        return "/attend/attendLeavesHistory";
    }

    @RequestMapping("/annualStatistics")
    public String annualStatistics(Model model) {
        return "/attend/attendAnnualStatistics";
    }

    @RequestMapping("/workDetails")
    public String workDetails(Model model) {
        return "/attend/attendWorkDetails";
    }
}
