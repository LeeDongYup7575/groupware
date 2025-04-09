package com.example.projectdemo.domain.edsm.controller;


import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.edsm.dao.EdsmDAO;
import com.example.projectdemo.domain.edsm.dto.ApprovalLineDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmDocumentDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/edsmDetail")
public class EdsmDetailController {


    @Autowired
    EdsmDAO edao;

    @Autowired
    private EmployeesService employeeService;

    @Autowired
    private EmployeesMapper employeesMapper;

    public String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return now.format(formatter);
    }



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

        List<EdsmDocumentDTO> edsmDocumentList = edao.selectByDocumentId(id);
        List<ApprovalLineDTO> approvalLineList = edao.selectByDocumentIdFromApprovalLine(id);
        model.addAttribute("edsmDocumentList", edsmDocumentList);
        model.addAttribute("approvalLineList", approvalLineList);
        return "businessContactDetail"; // 상세 페이지 템플릿 이름
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

        List<EdsmDocumentDTO> edsmDocumentList = edao.selectByDocumentId(id);
        List<ApprovalLineDTO> approvalLineList = edao.selectByDocumentIdFromApprovalLine(id);
        model.addAttribute("edsmDocumentList", edsmDocumentList);
        model.addAttribute("approvalLineList", approvalLineList);
        return "cashDisbuVoucherDetail"; // 상세 페이지 템플릿 이름
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

        List<EdsmDocumentDTO> edsmDocumentList = edao.selectByDocumentId(id);
        List<ApprovalLineDTO> approvalLineList = edao.selectByDocumentIdFromApprovalLine(id);
        model.addAttribute("edsmDocumentList", edsmDocumentList);
        model.addAttribute("approvalLineList", approvalLineList);
        return "letterOfApproval"; // 상세 페이지 템플릿 이름
    }


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

        edao.updateApprovalStatus(approvalLineDTO);
        return "success";
    }

}
