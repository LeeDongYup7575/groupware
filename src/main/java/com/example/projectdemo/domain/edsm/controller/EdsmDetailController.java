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
    EdsmDAO edao;

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


    //업무연락 상세페이지
    @GetMapping("/businessContact/{id}")
    public String businessContactDetail(@PathVariable("id") int id, Model model, HttpServletRequest request) {

        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        model.addAttribute("employee", employee);

        List<EdsmDocumentDTO> edsmDocumentList = edsmDetailService.getEdsmDocumentListFromDocId(id);
        List<EdsmBusinessContactDTO> edsmBusinessContactDTOList = edsmDetailService.getEdsmBusinessContactListFromDocId(id);
        List<ApprovalLineDTO> approvalLineList = edsmDetailService.getEdsmApprovalLineListFromDocId(id);
        List<EdsmFilesDTO> edsmFilesDTOList = edsmFilesService.getFilesSelectFromDocId(id);
        model.addAttribute("edsmDocumentList", edsmDocumentList);
        model.addAttribute("approvalLineList", approvalLineList);
        model.addAttribute("edsmBusinessContactDTOList", edsmBusinessContactDTOList);
        if(!edsmFilesDTOList.isEmpty()){
            model.addAttribute("edsmFilesDTOList", edsmFilesDTOList);
        }

        return "edsm/edsmDetail/businessContactDetail"; // 상세 페이지 템플릿 이름
    }


    //지출결의서 상세 페이지
    @GetMapping("/cashDisbuVoucher/{id}")
    public String cashDisbuVoucherDetail(@PathVariable("id") int id, Model model, HttpServletRequest request) {

        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        model.addAttribute("employee", employee);

        List<EdsmDocumentDTO> edsmDocumentList = edsmDetailService.getEdsmDocumentListFromDocId(id);
        List<EdsmCashDisbuVoucherDTO> edsmCashDisbuVoucherDTOList = edsmDetailService.getEdsmCashDisbuVoucherListFromDocId(id);
        List<ApprovalLineDTO> approvalLineList = edsmDetailService.getEdsmApprovalLineListFromDocId(id);
        List<EdsmFilesDTO> edsmFilesDTOList = edsmFilesService.getFilesSelectFromDocId(id);
        model.addAttribute("edsmDocumentList", edsmDocumentList);
        model.addAttribute("approvalLineList", approvalLineList);
        model.addAttribute("edsmCashDisbuVoucherDTOList", edsmCashDisbuVoucherDTOList);
        if(!edsmFilesDTOList.isEmpty()){
            model.addAttribute("edsmFilesDTOList", edsmFilesDTOList);
        }
        return "edsm/edsmDetail/cashDisbuVoucherDetail"; // 상세 페이지 템플릿 이름
    }


    //품의서 상세 페이지
    @GetMapping("/letterOfApproval/{id}")
    public String letterOfApprovalDetail(@PathVariable("id") int id, Model model, HttpServletRequest request) {

        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        model.addAttribute("employee", employee);

        List<EdsmLetterOfApprovalDTO> edsmLetterOfApprovalDTOList = edsmDetailService.getEdsmLetterOfApprovalListFromDocId(id);


        List<EdsmDocumentDTO> edsmDocumentList = edsmDetailService.getEdsmDocumentListFromDocId(id);
        List<ApprovalLineDTO> approvalLineList = edsmDetailService.getEdsmApprovalLineListFromDocId(id);
        List<EdsmFilesDTO> edsmFilesDTOList = edsmFilesService.getFilesSelectFromDocId(id);
        model.addAttribute("edsmDocumentList", edsmDocumentList);
        model.addAttribute("approvalLineList", approvalLineList);
        model.addAttribute("edsmLetterOfApprovalDTOList", edsmLetterOfApprovalDTOList);
        if(!edsmFilesDTOList.isEmpty()){
            model.addAttribute("edsmFilesDTOList", edsmFilesDTOList);
        }


        return "edsm/edsmDetail/letterOfApprovalDetail"; // 상세 페이지 템플릿 이름
    }


    //휴가신청서 Detail
    @GetMapping("/leavesDetail/{id}")
    public String leavesDetail(@PathVariable("id") int id, Model model, HttpServletRequest request) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        model.addAttribute("employee", employee);



        List<LeavesDTO> leavesList = leavesService.getLeavesDTOListByDocId(id);
        List<EdsmDocumentDTO> edsmDocumentList = edsmDetailService.getEdsmDocumentListFromDocId(id);
        List<ApprovalLineDTO> approvalLineList = edsmDetailService.getEdsmApprovalLineListFromDocId(id);
        List<EdsmFilesDTO> edsmFilesDTOList = edsmFilesService.getFilesSelectFromDocId(id);
        model.addAttribute("edsmDocumentList", edsmDocumentList);
        model.addAttribute("approvalLineList", approvalLineList);
        model.addAttribute("leavesList", leavesList);
        if(!edsmFilesDTOList.isEmpty()){
            model.addAttribute("edsmFilesDTOList", edsmFilesDTOList);
        }


        return "edsm/edsmDetail/leavesDetail"; // 상세 페이지 템플릿 위치 이름
    }

    //연장근무신청서 Detail
    @GetMapping("/overtimesDetail/{id}")
    public String overtimesDetail(@PathVariable("id") int id, Model model, HttpServletRequest request) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        model.addAttribute("employee", employee);


        List<OverTimeDTO> overtimesList = workService.getOvertimeDTOListByDocId(id);
        List<EdsmDocumentDTO> edsmDocumentList = edsmDetailService.getEdsmDocumentListFromDocId(id);
        List<ApprovalLineDTO> approvalLineList = edsmDetailService.getEdsmApprovalLineListFromDocId(id);
        List<EdsmFilesDTO> edsmFilesDTOList = edsmFilesService.getFilesSelectFromDocId(id);
        model.addAttribute("edsmDocumentList", edsmDocumentList);
        model.addAttribute("approvalLineList", approvalLineList);
        model.addAttribute("overtimesList", overtimesList);

        if(!edsmFilesDTOList.isEmpty()){
            model.addAttribute("edsmFilesDTOList", edsmFilesDTOList);
        }


        return "edsm/edsmDetail/overtimeDetail"; // 상세 페이지 템플릿 위치 이름
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

       if(result) {
           return "success";
       }
        return "false";
    }

}
