package com.example.projectdemo.domain.leave.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.edsm.dao.EdsmDAO;
import com.example.projectdemo.domain.edsm.dto.ApprovalLineDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmBusinessContactDTO;
import com.example.projectdemo.domain.edsm.enums.ApprovalStatus;
import com.example.projectdemo.domain.edsm.services.EdsmFilesService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.leave.dto.LeaveGrantsDTO;
import com.example.projectdemo.domain.leave.dto.LeavesDTO;
import com.example.projectdemo.domain.leave.service.LeavesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private EdsmDAO edsmDAO;

    @Autowired
    private EmployeesService employeeService;

    @Autowired
    private EdsmFilesService edsmFilesService;

    @RequestMapping("/leavesForm")
    public String leavesForm(Model model, HttpServletRequest request) {

        String empNum = (String)request.getAttribute("empNum");
        int empId = (int) request.getAttribute("id");

        if (empId == 0) return "redirect:/auth/login";

        if (empNum == null) { //예외처리
            return "redirect:/attend/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { //예외처리
            return "redirect:/attend/main";
        }

        BigDecimal totalLeave = employee.getTotalLeave() != null ? employee.getTotalLeave() : BigDecimal.ZERO;
        BigDecimal usedLeave = employee.getUsedLeave() != null ? employee.getUsedLeave() : BigDecimal.ZERO;

        BigDecimal canUseLeaves = totalLeave.subtract(usedLeave);

        List<LeavesDTO> usedLeaveList = leavesService.getLeavesByEmpId(empId);

        List<EmployeesDTO> empAllList = employeeMapper.selectEmpAll();

        List<EmployeesDTO> empList = new ArrayList<>();
        for(int i = 0; empAllList.size()>i; i++) {
            String empNum1 = empAllList.get(i).getEmpNum();
            EmployeesDTO list_emp = employeeService.findByEmpNum(empNum1);
            empList.add(list_emp);
        }

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        model.addAttribute("empList", empList);
        model.addAttribute("employee", employee);
        model.addAttribute("canUseLeaves", canUseLeaves);
        model.addAttribute("today", today);
        model.addAttribute("usedLeaveList",usedLeaveList);

        return "/leave/leavesForm";
    }


    @RequestMapping("/leavesHistory")
    public String leavesHistory(@RequestParam(value = "year", required = false) Integer year,
                                Model model, HttpServletRequest request) {


        int empId = (int) request.getAttribute("id");
        String drafterId = (String) request.getAttribute("empNum");

        if (empId == 0) return "redirect:/auth/login";

        EmployeesDTO employee = employeeMapper.findById(empId);
        if (employee == null) return "redirect:/auth/login";

        BigDecimal totalLeave = employee.getTotalLeave() != null ? employee.getTotalLeave() : BigDecimal.ZERO;
        BigDecimal usedLeave = employee.getUsedLeave() != null ? employee.getUsedLeave() : BigDecimal.ZERO;
        BigDecimal canUseLeaves = totalLeave.subtract(usedLeave);


        LocalDate now = LocalDate.now();
        int currentYear = (year != null) ? year : now.getYear();

        // 연차 생성 내역 조회
        List<LeaveGrantsDTO> allLeavesGrantList = leavesService.getLeaveGrantsByYear(empId,currentYear);

        // ✅ 연차 내역 전체 조회
        List<LeavesDTO> allLeavesList = leavesService.selectAllLeaves(empId);

        // ✅ 해당 연도 필터링
        List<LeavesDTO> leavesList = allLeavesList.stream()
                .filter(leave -> {
                    LocalDate startDate = LocalDate.parse(leave.getStartDate());
                    return startDate.getYear() == currentYear;
                })
                .collect(Collectors.toList());

        // ✅ 각 연차의 edsm status 값 조회 후 세팅
        for (LeavesDTO leave : leavesList) {
            int edsmDocId = leave.getEdsmDocId();
            String status = leavesService.selectByStatus(drafterId, edsmDocId);
            leave.setStatus(status); // 해당 leave 객체에 상태 설정
            leavesService.updateLeaveStatus(leave.getId(), status);
        }

        model.addAttribute("allLeavesGrantList", allLeavesGrantList);
        model.addAttribute("leavesList", leavesList);
        model.addAttribute("employee", employee);
        model.addAttribute("canUseLeaves", canUseLeaves);
        model.addAttribute("selectedYear", currentYear);

        return "/attend/attendLeavesHistory";
    }





    @PostMapping("/submitLeave")
    public String submitLeave(
            @RequestParam("drafterId") String drafterId,
            @RequestParam("leaveStartDate") String leaveStartDate,
            @RequestParam("leaveEndDate") String leaveEndDate,
            @RequestParam("leaveType") String leaveType,
            @RequestParam("leaveHours") String leaveHours,
            @RequestParam("totalLeaveDays") String totalLeaveDays,
            @RequestParam("reason") String content,
            @RequestParam("approvalLine") String approvalLine,
            @RequestParam(value = "fileAttachment", required = false) MultipartFile[] fileAttachment,
            HttpServletRequest request,
            Model model) throws Exception {

        EdsmBusinessContactDTO bcdto = new EdsmBusinessContactDTO();

        String empNum = (String) request.getAttribute("empNum");
        int empId = (int) request.getAttribute("id");

        if (empId == 0) return "redirect:/auth/login";

        if (empNum == null) { // 예외처리
            return "redirect:/attend/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { // 예외처리
            return "redirect:/attend/main";
        }

        model.addAttribute("employee", employee);
        bcdto.setContent(content); // 기안 문서 사유 내용
        bcdto.setDrafterId(drafterId); // 기안자 사원번호

        // 결재 문서 생성
        int edsmDocId = leavesService.insertByEdsm(bcdto);

        // 첨부 파일 처리
        if (fileAttachment != null && fileAttachment.length > 0 && !fileAttachment[0].isEmpty()) {
            edsmFilesService.getFilesInsert(edsmDocId, 1004, fileAttachment);
        }

        // 휴가 정보 처리 - 단일 휴가 신청
        LeavesDTO leavesDto = new LeavesDTO();
        leavesDto.setEmpId(empId);
        leavesDto.setStartDate(leaveStartDate);
        leavesDto.setEndDate(leaveEndDate);
        leavesDto.setLeaveType(leaveType);
        leavesDto.setReason(content);
        leavesDto.setEdsmDocId(edsmDocId);

        leavesService.insertByLeaves(leavesDto);

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

        return "redirect:/leaves/leavesHistory";
    }

}