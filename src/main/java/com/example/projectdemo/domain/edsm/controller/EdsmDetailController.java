package com.example.projectdemo.domain.edsm.controller;

import com.example.projectdemo.domain.edsm.dao.EdsmDAO;
import com.example.projectdemo.domain.edsm.dto.*;
import com.example.projectdemo.domain.edsm.services.EdsmDetailService;
import com.example.projectdemo.domain.edsm.services.EdsmFilesService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.leave.dto.LeavesDTO;
import com.example.projectdemo.domain.leave.service.LeavesService;
import com.example.projectdemo.domain.work.dto.OverTimeDTO;
import com.example.projectdemo.domain.work.service.WorkService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/edsmDetail")
public class EdsmDetailController {

    @Autowired
    private EdsmDAO edao;

    @Autowired
    private EmployeesService employeeService;

    @Autowired
    private EdsmDetailService edsmDetailService;

    @Autowired
    private EdsmFilesService edsmFilesService;

    @Autowired
    private WorkService workService;

    @Autowired
    private LeavesService leavesService;

    /**
     * 유효한 직원 정보를 확인하고 모델에 추가하는 공통 메서드
     * @param model 모델 객체
     * @param request HTTP 요청 객체
     * @return 유효하지 않은 경우 리다이렉트 경로, 유효한 경우 null
     */
    private String validateEmployeeAndAddToModel(Model model, HttpServletRequest request) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) {
            return "redirect:/edsm/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) {
            return "redirect:/edsm/main";
        }

        model.addAttribute("employee", employee);
        return null;
    }

    /**
     * 공통 문서 정보를 모델에 추가하는 메서드
     * @param model 모델 객체
     * @param id 문서 ID
     */
    private void addCommonDocumentData(Model model, int id) {
        List<EdsmDocumentDTO> edsmDocumentList = edsmDetailService.getEdsmDocumentListFromDocId(id);
        List<ApprovalLineDTO> approvalLineList = edsmDetailService.getEdsmApprovalLineListFromDocId(id);
        List<EdsmFilesDTO> edsmFilesDTOList = edsmFilesService.getFilesSelectFromDocId(id);

        model.addAttribute("edsmDocumentList", edsmDocumentList);
        model.addAttribute("approvalLineList", approvalLineList);

        if (!edsmFilesDTOList.isEmpty()) {
            model.addAttribute("edsmFilesDTOList", edsmFilesDTOList);
        }
    }

    //업무연락 상세페이지
    @GetMapping("/businessContact/{id}")
    public String businessContactDetail(@PathVariable("id") int id, Model model, HttpServletRequest request) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        addCommonDocumentData(model, id);

        List<EdsmBusinessContactDTO> edsmBusinessContactDTOList = edsmDetailService.getEdsmBusinessContactListFromDocId(id);
        model.addAttribute("edsmBusinessContactDTOList", edsmBusinessContactDTOList);

        return "edsm/edsmDetail/businessContactDetail";
    }

    //지출결의서 상세 페이지
    @GetMapping("/cashDisbuVoucher/{id}")
    public String cashDisbuVoucherDetail(@PathVariable("id") int id, Model model, HttpServletRequest request) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        addCommonDocumentData(model, id);

        List<EdsmCashDisbuVoucherDTO> edsmCashDisbuVoucherDTOList = edsmDetailService.getEdsmCashDisbuVoucherListFromDocId(id);
        model.addAttribute("edsmCashDisbuVoucherDTOList", edsmCashDisbuVoucherDTOList);

        return "edsm/edsmDetail/cashDisbuVoucherDetail";
    }

    //품의서 상세 페이지
    @GetMapping("/letterOfApproval/{id}")
    public String letterOfApprovalDetail(@PathVariable("id") int id, Model model, HttpServletRequest request) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        addCommonDocumentData(model, id);

        List<EdsmLetterOfApprovalDTO> edsmLetterOfApprovalDTOList = edsmDetailService.getEdsmLetterOfApprovalListFromDocId(id);
        model.addAttribute("edsmLetterOfApprovalDTOList", edsmLetterOfApprovalDTOList);

        return "edsm/edsmDetail/letterOfApprovalDetail";
    }

    //휴가신청서 Detail
    @GetMapping("/leavesDetail/{id}")
    public String leavesDetail(@PathVariable("id") int id, Model model, HttpServletRequest request) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        addCommonDocumentData(model, id);

        List<LeavesDTO> leavesList = leavesService.getLeavesDTOListByDocId(id);
        model.addAttribute("leavesList", leavesList);

        return "edsm/edsmDetail/leavesDetail";
    }

    //연장근무신청서 Detail
    @GetMapping("/overtimesDetail/{id}")
    public String overtimesDetail(@PathVariable("id") int id, Model model, HttpServletRequest request) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        addCommonDocumentData(model, id);

        List<OverTimeDTO> overtimesList = workService.getOvertimeDTOListByDocId(id);
        model.addAttribute("overtimesList", overtimesList);

        return "edsm/edsmDetail/overtimeDetail";
    }

    // 결재권자의 결재 상태 업데이트
    @ResponseBody
    @PostMapping(value = "updateApprovalStatus", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String updateApprovalStatus(ApprovalLineDTO approvalLineDTO, HttpServletRequest request, Model model) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return "fail";
        }

        EmployeesDTO employee = employeeService.findByEmpNum(empNum);
        if (employee == null) {
            return "fail";
        }
        model.addAttribute("employee", employee);

        boolean result = edsmDetailService.updateApprovalStatus(approvalLineDTO);
        return result ? "success" : "false";
    }

    @ResponseBody
    @GetMapping("/getRejectionReason")
    public String getRejectionReason(ApprovalLineDTO approvalLineDTO, HttpServletRequest request) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return "edsm/main";
        }

        EmployeesDTO employee = employeeService.findByEmpNum(empNum);
        if (employee == null) {
            return "edsm/main";
        }

        return edsmDetailService.getRejectionReason(approvalLineDTO);
    }
}