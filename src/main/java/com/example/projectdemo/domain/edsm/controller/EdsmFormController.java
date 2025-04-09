package com.example.projectdemo.domain.edsm.controller;


import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.edsm.dao.EdsmDAO;
import com.example.projectdemo.domain.edsm.dto.ApprovalLineDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmBusinessContactDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmDocumentDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmLetterOfApprovalDTO;
import com.example.projectdemo.domain.edsm.enums.ApprovalStatus;
import com.example.projectdemo.domain.edsm.enums.EdsmStatus;
import com.example.projectdemo.domain.edsm.services.EdsmFormService;
import com.example.projectdemo.domain.edsm.services.EdsmService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/edsmForm")
public class EdsmFormController {


    @Autowired
    private HttpSession session;

    @Autowired
    private JwtTokenUtil jwt;

    @Autowired
    EdsmDAO edao;

    @Autowired
    private EmployeesService employeeService;

    @Autowired
    private EmployeesMapper employeesMapper;

    @Autowired
    private EdsmFormService edsmFormService;


    // 기안문서 컨트롤러
    //작성하기 버튼 컨트롤러
    @RequestMapping("/input")
    public String create(Model model, HttpServletRequest request) {

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

        return "edsm/input";
    }
    //업무연락
    @RequestMapping("/1001")
    public String inputBc(Model model, HttpServletRequest request,@RequestParam(value="documentType", required=false) String documentType) {
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

        //현재시간 가져오기
        String currentTime = edsmFormService.getCurrentTime();
        model.addAttribute("currentTime", currentTime);

        // documentType 값이 없으면 기본값 설정
        if (documentType == null) {
            documentType = "1001"; // 예를 들어 "업무연락"에 해당하는 값
        }
        model.addAttribute("documentType", documentType);

        //전체 사원 출력(사원 번호를 통한 모든정보)
        List<EmployeesDTO> empList = edsmFormService.allEmployeesList();

        // 사번(empNum)을 기준으로 오름차순 정렬 (Java 8 이상 사용)
        empList.sort(Comparator.comparing(EmployeesDTO::getEmpNum).reversed());
        model.addAttribute("list_emp", empList);


        return "edsm/edsmForm/businessContact";
    }

    //지출결의서
    @RequestMapping("/1002")
    public String inputCdv(Model model, HttpServletRequest request,@RequestParam(value="documentType", required=false) String documentType) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) { // 예외 처리
            return "redirect:/edsm/main";
        }
        //현재시간 가져오기
        String currentTime = edsmFormService.getCurrentTime();
        model.addAttribute("currentTime", currentTime);

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);
        model.addAttribute("employee", employee);
        if (employee == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        // documentType 값이 없으면 기본값 설정
        if (documentType == null) {
            documentType = "1002"; // 예를 들어 "업무연락"에 해당하는 값
        }
        model.addAttribute("documentType", documentType);

        //전체 사원 출력(사원 번호를 통한 모든정보)
        List<EmployeesDTO> empList = edsmFormService.allEmployeesList();

        // 사번(empNum)을 기준으로 오름차순 정렬 (Java 8 이상 사용)
        empList.sort(Comparator.comparing(EmployeesDTO::getEmpNum).reversed());
        model.addAttribute("list_emp", empList);

        return "edsm/edsmForm/cashDisbuVoucher";
    }


    //품의서
    @RequestMapping("/1003")
    public String inputLoa(Model model, HttpServletRequest request,@RequestParam(value="documentType", required=false) String documentType) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) { // 예외 처리
            return "redirect:/edsm/main";
        }
        //현재시간 가져오기
        String currentTime = edsmFormService.getCurrentTime();
        model.addAttribute("currentTime", currentTime);

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);
        model.addAttribute("employee", employee);

        if (employee == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        if (documentType == null) {
            documentType = "1003";
        }
        model.addAttribute("documentType", documentType);

        //전체 사원 출력(사원 번호를 통한 모든정보)
        List<EmployeesDTO> empList = edsmFormService.allEmployeesList();

        // 사번(empNum)을 기준으로 오름차순 정렬 (Java 8 이상 사용)
        empList.sort(Comparator.comparing(EmployeesDTO::getEmpNum).reversed());
        model.addAttribute("list_emp", empList);


        return "edsm/edsmForm/letterOfApproval";
    }

    //업무연락 제출 컨트롤러
    @PostMapping("/business_submit")
    public String bcSubmit(@RequestParam("documentType") int edsmFormId,
                           @RequestParam("drafterId") String draftId,
                           @RequestParam("title") String title,
                           @RequestParam("content") String content,
                           @RequestParam("retentionPeriod") String retentionPeriod,
                           @RequestParam("securityGrade") String securityGrade,
                           @RequestParam("drafterPosition") String writerPosition,
                           @RequestParam("drafterName") String writerName,
                           @RequestParam("approvalLine") String approvalLine, // JSON 문자열 (추가 결재자들)
                           @RequestParam("fileAttachment") MultipartFile[] fileAttachment,
                           Model model, HttpServletRequest request) {

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


        boolean result = edsmFormService.insertByEdsmDocument(edsmFormId, draftId,title,content,
                                                                retentionPeriod,securityGrade,writerPosition,
                                                                writerName,approvalLine,fileAttachment);

        if (result) {
            return "redirect:/edsm/main";
        }
        return "redirect:/edsm/error";

    }

    //지출결의서 제출 컨트롤러
    @PostMapping("/cash_submit")
    public String cashSubmit(@RequestParam("documentType") int edsmFormId,
                               @RequestParam("drafterId") String draftId,
                               @RequestParam("title") String title,
                               @RequestParam("content") String content,
                               @RequestParam("retentionPeriod") String retentionPeriod,
                               @RequestParam("securityGrade") String securityGrade,
                               @RequestParam("drafterPosition") String writerPosition,
                               @RequestParam("drafterName") String writerName,
                               @RequestParam("accountingDate") String accountingDate,
                               @RequestParam("bank") String bank,
                               @RequestParam("bankAccount") String bankAccount,
                               @RequestParam("spender") String spenderId,
                               @RequestParam("approvalLine") String approvalLine, // JSON 문자열 (추가 결재자들)
                               @RequestParam("fileAttachment") MultipartFile[] fileAttachment,
                               Model model, HttpServletRequest request) {

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

        boolean result = edsmFormService.insertByCash(edsmFormId, draftId,title,content,
                                                        retentionPeriod,securityGrade,writerPosition,writerName,
                                                        accountingDate,bank,bankAccount,spenderId,
                                                        approvalLine,fileAttachment);


        if (result) {
            return "redirect:/edsm/main";
        }
        return "redirect:/edsm/error";
    }


    //품의서 제출 컨트롤러
    @PostMapping("/letter_submit")
    public String letterSubmit(@RequestParam("documentType") int edsmFormId,
                                  @RequestParam("drafterId") String drafterId,
                                  @RequestParam("title") String title,
                                  @RequestParam("content") String content,
                                  @RequestParam("retentionPeriod") String retentionPeriod,
                                  @RequestParam("securityGrade") String securityGrade,
                                  @RequestParam("drafterPosition") String writerPosition,
                                  @RequestParam("drafterName") String writerName,
                                  @RequestParam("expectedCost") String expectedCost,
                                  @RequestParam("approvalLine") String approvalLine, // JSON 문자열 (추가 결재자들)
                                  @RequestParam("fileAttachment") MultipartFile[] fileAttachment,
                                  Model model, HttpServletRequest request) {

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

        boolean result = edsmFormService.insertLetterOfApproval(edsmFormId,
                                                                drafterId,title,content,retentionPeriod,
                                                                securityGrade,writerPosition,writerName,
                                                                expectedCost,approvalLine,fileAttachment);


        if (result) {
            return "redirect:/edsm/main";
        }
        return "redirect:/edsm/error";
    }

}
