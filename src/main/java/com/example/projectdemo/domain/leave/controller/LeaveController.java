package com.example.projectdemo.domain.leave.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.edsm.dto.EdsmBusinessContactDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.leave.dto.LeavesDTO;
import com.example.projectdemo.domain.leave.service.LeavesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/leaves")
public class LeaveController {

    @Autowired
    private LeavesService leavesService;

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private EmployeesMapper employeeMapper;

    @Autowired
    private EmployeesService employeeService;

    @RequestMapping("/leavesForm")
    public String leavesForm(Model model, HttpServletRequest request) {

        String empNum = (String)request.getAttribute("empNum");

        if (empNum == null) { //예외처리
            return "redirect:/attend/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { //예외처리
            return "redirect:/attend/main";
        }

        int canUseLeaves = employee.getTotalLeave()-employee.getUsedLeave();

        List<EmployeesDTO> empAllList = employeeMapper.selectEmpAll();
        List<EmployeesDTO> empList = new ArrayList<>();
        for(int i = 0; empAllList.size()>i; i++) {
            String empNum1 = empAllList.get(i).getEmpNum();
            EmployeesDTO list_emp = employeeService.findByEmpNum(empNum1);
            empList.add(list_emp);
        }

        model.addAttribute("empList", empList);
        model.addAttribute("employee", employee);
        model.addAttribute("canUseLeaves", canUseLeaves);

        return "/leave/leavesForm";
    }


    @RequestMapping("/leavesHistory")
    public String leavesHistory(@RequestParam(value = "year", required = false) Integer year,
                                Model model, HttpServletRequest request) {

        int empId = (int)request.getAttribute("id");
        String drafterId = (String)request.getAttribute("empNum");

        if (empId == 0) return "redirect:/auth/login";

        EmployeesDTO employee = employeeMapper.findById(empId);
        if (employee == null) return "redirect:/auth/login";

        int canUseLeaves = employee.getTotalLeave() - employee.getUsedLeave();

        LeavesDTO leavesdto = new LeavesDTO();
        String leavesStatus = leavesService.selectByStatus(drafterId, leavesdto.getEdsmDocId());
        leavesdto.setStatus(leavesStatus);

        // ✅ 현재 연도 지정 (없으면 올해)
        LocalDate now = LocalDate.now();
        int currentYear = (year != null) ? year : now.getYear();

        // ✅ 해당 연도의 휴가 신청 내역만 필터링
        List<LeavesDTO> allLeavesList = leavesService.selectAllLeaves(empId);
        List<LeavesDTO> leavesList = allLeavesList.stream()
                .filter(leave -> {
                    LocalDate startDate = LocalDate.parse(leave.getStartDate());
                    return startDate.getYear() == currentYear;
                })
                .collect(Collectors.toList());

        // ✅ 연차 생성일 계산
        LocalDate hireDate = employee.getHireDate(); // 입사일
        List<LocalDate> allGrantDates = new ArrayList<>();

        if (ChronoUnit.YEARS.between(hireDate, now) >= 1) {
            int years = (int) ChronoUnit.YEARS.between(hireDate.plusYears(1), now);
            for (int i = 0; i <= years; i++) {
                allGrantDates.add(LocalDate.of(hireDate.plusYears(1).getYear() + i, 1, 1));
            }
        } else {
            int months = (int) ChronoUnit.MONTHS.between(hireDate, now);
            for (int i = 1; i <= Math.min(months, 11); i++) {
                allGrantDates.add(hireDate.plusMonths(i));
            }
        }

        // ✅ 해당 연도만 필터링
        List<LocalDate> leaveGrantDates = allGrantDates.stream()
                .filter(date -> date.getYear() == currentYear)
                .collect(Collectors.toList());

        model.addAttribute("leavesList", leavesList);
        model.addAttribute("employee", employee);
        model.addAttribute("canUseLeaves", canUseLeaves);
        model.addAttribute("leaveGrantDates", leaveGrantDates);
        model.addAttribute("selectedYear", currentYear); // 프론트에서 표시용

        return "/attend/attendLeavesHistory";
    }


    @PostMapping("/submitLeave")
    public String submitLeave(
            @RequestParam("drafterId") String drafterId,
            @RequestParam("empId") String empId,
            @RequestParam("leaveStartDate[]") List<String> leaveStartDates,
            @RequestParam("leaveEndDate[]") List<String> leaveEndDates,
            @RequestParam("leaveType[]") List<String> leaveTypes,
            @RequestParam("leaveHours[]") List<String> leaveHours,
            @RequestParam("reason") String content,
            HttpServletRequest request,
            Model model) {

        EdsmBusinessContactDTO bcdto = new EdsmBusinessContactDTO();

        String empNum = (String)request.getAttribute("empNum");

        if (empNum == null) { //예외처리
            return "redirect:/attend/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { //예외처리
            return "redirect:/attend/main";
        }

        model.addAttribute("employee", employee);
        bcdto.setContent(content); // 기안 문서 사유 내용
        bcdto.setDrafterId(drafterId); // 기안자 사원번호

        int edsmDocId = leavesService.insertByEdsm(bcdto);

        for (int i = 0; i < leaveStartDates.size(); i++) {
            LeavesDTO leavesdto = new LeavesDTO();

            leavesdto.setEmpId(drafterId);
            leavesdto.setStartDate(leaveStartDates.get(i));
            leavesdto.setEndDate(leaveEndDates.get(i));
            String leaveTypeValue = leaveTypes.get(i).replace("[", "").replace("]", "");
            leavesdto.setLeaveType(leaveTypeValue);
            leavesdto.setReason(content);
            leavesdto.setEdsmDocId(edsmDocId);

            leavesService.insertByLeaves(leavesdto);
        }

        return "redirect:/leaves/leavesHistory";
    }




}