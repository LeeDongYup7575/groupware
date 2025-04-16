package com.example.projectdemo.domain.edsm.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.edsm.dao.EdsmDAO;
import com.example.projectdemo.domain.edsm.services.EdsmFormService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
    private EdsmDAO edao;

    @Autowired
    private EmployeesService employeeService;

    @Autowired
    private EmployeesMapper employeesMapper;

    @Autowired
    private EdsmFormService edsmFormService;

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
     * 폼 초기화를 위한 공통 작업을 수행하는 메서드
     * @param model 모델 객체
     * @param documentType 문서 유형
     */
    private void prepareFormCommonData(Model model, String documentType) {
        // 현재시간 가져오기
        String currentTime = edsmFormService.getCurrentTime();
        model.addAttribute("currentTime", currentTime);
        model.addAttribute("documentType", documentType);

        // 전체 사원 출력(사원 번호를 통한 모든정보)
        List<EmployeesDTO> empList = edsmFormService.allEmployeesList();
        // 사번(empNum)을 기준으로 내림차순 정렬
        empList.sort(Comparator.comparing(EmployeesDTO::getEmpNum).reversed());
        model.addAttribute("list_emp", empList);
    }

    // 작성하기 버튼 컨트롤러
    @RequestMapping("/input")
    public String create(Model model, HttpServletRequest request) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        return "edsm/input";
    }

    // 업무연락
    @RequestMapping("/1001")
    public String inputBc(Model model, HttpServletRequest request,
                          @RequestParam(value = "documentType", required = false) String documentType) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        // documentType 값이 없으면 기본값 설정
        if (documentType == null) {
            documentType = "1001"; // 업무연락에 해당하는 값
        }

        prepareFormCommonData(model, documentType);

        return "edsm/edsmForm/businessContact";
    }

    // 지출결의서
    @RequestMapping("/1002")
    public String inputCdv(Model model, HttpServletRequest request,
                           @RequestParam(value = "documentType", required = false) String documentType) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        // documentType 값이 없으면 기본값 설정
        if (documentType == null) {
            documentType = "1002"; // 지출결의서에 해당하는 값
        }

        prepareFormCommonData(model, documentType);

        return "edsm/edsmForm/cashDisbuVoucher";
    }

    // 품의서
    @RequestMapping("/1003")
    public String inputLoa(Model model, HttpServletRequest request,
                           @RequestParam(value = "documentType", required = false) String documentType) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        // documentType 값이 없으면 기본값 설정
        if (documentType == null) {
            documentType = "1003"; // 품의서에 해당하는 값
        }

        prepareFormCommonData(model, documentType);

        return "edsm/edsmForm/letterOfApproval";
    }

    // 업무연락 제출 컨트롤러
    @PostMapping("/business_submit")
    public String bcSubmit(
            @RequestParam("documentType") int edsmFormId,
            @RequestParam("drafterId") String draftId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("retentionPeriod") String retentionPeriod,
            @RequestParam("securityGrade") String securityGrade,
            @RequestParam("drafterPosition") String writerPosition,
            @RequestParam("drafterName") String writerName,
            @RequestParam("approvalLine") String approvalLine,
            @RequestParam("fileAttachment") MultipartFile[] fileAttachment,
            Model model, HttpServletRequest request) throws Exception {

        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        boolean result = edsmFormService.insertByEdsmDocument(
                edsmFormId, draftId, title, content,
                retentionPeriod, securityGrade, writerPosition,
                writerName, approvalLine, fileAttachment);

        return result ? "redirect:/edsm/main" : "redirect:/edsm/error";
    }

    // 지출결의서 제출 컨트롤러
    @PostMapping("/cash_submit")
    public String cashSubmit(
            @RequestParam("documentType") int edsmFormId,
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
            @RequestParam("approvalLine") String approvalLine,
            @RequestParam("fileAttachment") MultipartFile[] fileAttachment,
            Model model, HttpServletRequest request) throws Exception {

        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        boolean result = edsmFormService.insertByCash(
                edsmFormId, draftId, title, content,
                retentionPeriod, securityGrade, writerPosition, writerName,
                accountingDate, bank, bankAccount, spenderId,
                approvalLine, fileAttachment);

        return result ? "redirect:/edsm/main" : "redirect:/edsm/error";
    }

    // 품의서 제출 컨트롤러
    @PostMapping("/letter_submit")
    public String letterSubmit(
            @RequestParam("documentType") int edsmFormId,
            @RequestParam("drafterId") String drafterId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("retentionPeriod") String retentionPeriod,
            @RequestParam("securityGrade") String securityGrade,
            @RequestParam("drafterPosition") String writerPosition,
            @RequestParam("drafterName") String writerName,
            @RequestParam("expectedCost") String expectedCost,
            @RequestParam("approvalLine") String approvalLine,
            @RequestParam("fileAttachment") MultipartFile[] fileAttachment,
            Model model, HttpServletRequest request) throws Exception {

        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        boolean result = edsmFormService.insertLetterOfApproval(
                edsmFormId, drafterId, title, content, retentionPeriod,
                securityGrade, writerPosition, writerName,
                expectedCost, approvalLine, fileAttachment);

        return result ? "redirect:/edsm/main" : "redirect:/edsm/error";
    }
}