package com.example.projectdemo.domain.work.controller;

import com.example.projectdemo.domain.attend.dto.AttendDTO;
import com.example.projectdemo.domain.attend.service.AttendService;
import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.edsm.dao.EdsmDAO;
import com.example.projectdemo.domain.edsm.dto.ApprovalLineDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmBusinessContactDTO;
import com.example.projectdemo.domain.edsm.enums.ApprovalStatus;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/works")
public class WorkController {

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private EmployeesMapper employeeMapper;

    @Autowired
    private EdsmDAO edsmDAO;

    @Autowired
    private EmployeesService employeeService;

    @Autowired
    private AttendService attendService;

    @Autowired
    private WorkService workService;

    @Autowired
    private LeavesService leavesService;

    @RequestMapping("/overTimeForm")
    public String overTimeForm(Model model, HttpServletRequest request) {
        int empId = (int) request.getAttribute("id");

        if (empId == 0) { //예외처리
            return "redirect:/attend/main";
        }

        String empNum = (String)request.getAttribute("empNum");

        if (empNum == null) { //예외처리
            return "redirect:/attend/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { //예외처리
            return "redirect:/attend/main";
        }

        List<EmployeesDTO> empAllList = employeeMapper.selectEmpAll();
        List<EmployeesDTO> empList = new ArrayList<>();
        for(int i = 0; empAllList.size()>i; i++) {
            String empNum1 = empAllList.get(i).getEmpNum();
            EmployeesDTO list_emp = employeeService.findByEmpNum(empNum1);
            empList.add(list_emp);
        }
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        int year = Year.now().getValue();
        int currentMonth = LocalDate.now().getMonthValue();

        // 연도 내 각 월별 연장/휴일근무시간 데이터를 가져온다.
        List<Map<String, Object>> overtimeHoursList = workService.getMonthlyOvertimeHours(empId, year);

        List<Map<String, Object>> statisticsByMonth = attendService.getMonthlyAttendanceStatisticsThisMonth(empId);

        // 현재 월의 총합을 계산
        double currentMonthTotalOvertime = 0;
        for (Map<String, Object> data : overtimeHoursList) {
            Integer month = (Integer) data.get("month");
            if (month != null && month == currentMonth) {
                Object totalHoursObj = data.get("totalHours");
                double hours = 0;
                if (totalHoursObj == null || totalHoursObj.toString().trim().isEmpty()) {
                    hours = 0; // null 이거나 빈 문자열인 경우 0으로 처리
                } else if (totalHoursObj instanceof Number) {
                    hours = ((Number) totalHoursObj).doubleValue();
                } else {
                    try {
                        hours = Double.parseDouble(totalHoursObj.toString());
                    } catch (NumberFormatException e) {
                        hours = 0; // 파싱 에러 발생 시 0으로 처리
                    }
                }
                currentMonthTotalOvertime += hours;
            }
        }

        model.addAttribute("empList", empList);
        model.addAttribute("employee", employee);
        model.addAttribute("today", today);
        model.addAttribute("currentMonthTotalOvertime", currentMonthTotalOvertime);
        model.addAttribute("statisticsByMonth", statisticsByMonth);

        return "/work/overTimeForm";
    }

    @PostMapping("/submitOverTime")
    public String submitOvertime(
            @RequestParam("drafterId") String drafterId,
            @RequestParam("empId") String empId,
            @RequestParam("workDate") String workDate,
            @RequestParam("startHour") String startHour,
            @RequestParam("startMinute") String startMinute,
            @RequestParam("endHour") String endHour,
            @RequestParam("endMinute") String endMinute,
            @RequestParam("reason") String content,
            @RequestParam("approvalLine") String approvalLine,
            HttpServletRequest request,
            Model model) {

        String formattedDate = workDate;
        if (workDate.contains(" (")) {
            formattedDate = workDate.substring(0, workDate.indexOf(" ("));
        }

        // 시간 합쳐서 처리
        String startTime = startHour + ":" + startMinute;
        String endTime = endHour + ":" + endMinute;
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

        int edsmDocId = workService.insertByEdsm(bcdto);

        OverTimeDTO overTimeDTO = new OverTimeDTO();

        overTimeDTO.setEmpId(empId);
        overTimeDTO.setStartTime(startTime);
        overTimeDTO.setEndTime(endTime);
        overTimeDTO.setWorkDate(formattedDate);
        overTimeDTO.setReason(content);
        overTimeDTO.setEdsmDocId(edsmDocId);
        overTimeDTO.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        workService.insertByOverTime(overTimeDTO);

        // 결재라인 리스트 준비 :
        // 1) 기안자(작성자)는 결재라인의 첫번째에 추가하며, approvalNo는 1, status는 무조건 "승인"
        List<ApprovalLineDTO> finalApprovalList = new ArrayList<>();
        ApprovalLineDTO drafterApproval = new ApprovalLineDTO();
        drafterApproval.setDocumentId(edsmDocId);
        drafterApproval.setDrafterId(drafterId);
        drafterApproval.setApproverId(drafterId); // 기안자 자신이 첫번째 결재자로 고정됨
        drafterApproval.setApprovalNo(1);
        drafterApproval.setStatus(ApprovalStatus.APPROVED.getLabel());
        finalApprovalList.add(drafterApproval);

        // 2) JSON 문자열을 파싱하여 추가 결재자 목록 처리 (순번 2부터 부여)
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ApprovalLineDTO> additionalApprovals = mapper.readValue(approvalLine,
                    new com.fasterxml.jackson.core.type.TypeReference<List<ApprovalLineDTO>>() {});
            int seq = 2;
            for (ApprovalLineDTO dto : additionalApprovals) {
                dto.setDocumentId(edsmDocId);
                dto.setDrafterId(drafterId);
                dto.setApprovalNo(seq++);
                // 추가 결재자는 기본적으로 "대기" 상태로 둘 수 있으며,
                // 필요에 따라 여기서 status 값을 변경할 수 있습니다.
                dto.setStatus(ApprovalStatus.PENDING.getLabel());
                finalApprovalList.add(dto);
            }
            // 최종 결재라인 리스트 전체를 DB에 저장
            for (ApprovalLineDTO alDto : finalApprovalList) {
                edsmDAO.insertByApprovalLine(alDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/works/workDetails";
    }

    @RequestMapping("/workDetails")
    public String workDetails( @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().year}") int year,
                               @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().monthValue}") int month,
                               Model model, HttpServletRequest request) {

        String drafterId = (String) request.getAttribute("empNum");

        int empId = (int) request.getAttribute("id");

        System.out.println("drafterId: " + drafterId);
        System.out.println("empId: " + empId);
        if (empId == 0) {
            return "redirect:/auth/login";
        }
        EmployeesDTO employee = employeeMapper.findById(empId);
        if (employee == null) {
            return "redirect:/auth/login";
        }

        List<OverTimeDTO> overTimeRequestsList = workService.getOverTimeRequestsByMonth(empId, year, month);
        for(OverTimeDTO overTime : overTimeRequestsList) {
            int edsmDocId = overTime.getEdsmDocId();
            String status = workService.selectByStatus(drafterId, edsmDocId);
            overTime.setStatus(status);
            workService.updateOverTimeRequestsStatus(overTime.getId(), status);
        }

        model.addAttribute("employee", employee);
        model.addAttribute("overTimeRequestsList", overTimeRequestsList);
        model.addAttribute("year", year);
        model.addAttribute("month", month);

        return "/work/workDetails";
    }


    @RequestMapping("/workSchedule")
    public String workSchedule(Model model) {
        return "/work/workSchedule";
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

        //휴가 기록을 DB에서 가져오기
        List<LeavesDTO> leavesList = leavesService.selectLeavesByEmpId(empId);

        // 연장 근무 기록을 DB에서 가져오기
        List<OverTimeDTO> overtimesList = workService.selectOverTimeListByEmpId(empId);

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

        for (LeavesDTO leave : leavesList) {
            Map<String, Object> leaveEvent = new HashMap<>();
            leaveEvent.put("title", leave.getLeaveType() + " 휴가"); // 예: 연차 휴가, 반차 휴가
            leaveEvent.put("start", leave.getStartDate()); // "2025-04-10"


            // 반차는 end를 start와 동일하게, 전일휴가는 end 포함 다음날로 설정
            if ("반차".equals(leave.getLeaveType())) {
                leaveEvent.put("end", leave.getStartDate());
            } else {
                // endDate 다음 날로 설정 (FullCalendar는 end 당일 포함 안 함)
                LocalDate endPlusOne = LocalDate.parse(leave.getEndDate()).plusDays(1);
                leaveEvent.put("end", endPlusOne.toString());
            }

            leaveEvent.put("description", "사유: " + leave.getReason());
            leaveEvent.put("color", "#0078d7");

            events.add(leaveEvent);
        }

        for (OverTimeDTO overtime : overtimesList) {
            Map<String, Object> workEvent = new HashMap<>();

            workEvent.put("title", "연장 근무");
            workEvent.put("type", "overtime");

            // 날짜 및 시간 조합
            String workDate = overtime.getWorkDate(); // 예: "2025-04-09"
            String startTime = overtime.getStartTime(); // 예: "18:00"
            String endTime = overtime.getEndTime(); // 예: "20:30"

            workEvent.put("start", workDate + "T" + startTime);
            workEvent.put("end", workDate + "T" + endTime);

            workEvent.put("description", overtime.getReason());
            workEvent.put("color", "#6f42c1");

            events.add(workEvent);
        }



        return events;  // JSON 형식으로 반환
    }
}