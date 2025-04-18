package com.example.projectdemo.domain.edsm.controller;

import com.example.projectdemo.domain.edsm.dao.EdsmDAO;
import com.example.projectdemo.domain.edsm.dto.EdsmDocumentDTO;
import com.example.projectdemo.domain.edsm.services.EdsmService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/edsm")
public class EdsmController {

    @Autowired
    private EdsmDAO edao;

    @Autowired
    private EmployeesService employeesService;

    @Autowired
    private EdsmService edsmService;

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
        EmployeesDTO employee = employeesService.findByEmpNum(empNum);

        if (employee == null) {
            return "redirect:/edsm/main";
        }

        model.addAttribute("employee", employee);
        return null;
    }

    // 전자결재 메인화면
    @RequestMapping("/main")
    public String main(Model model, HttpServletRequest request) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        String empNum = (String) request.getAttribute("empNum");
        model.addAttribute("main", "전체");

        // 모든 문서 유형의 데이터 조회
        List<EdsmDocumentDTO> allDocument = edsmService.selectByAllDocument(empNum);
        List<EdsmDocumentDTO> allBusinessDocument = edsmService.selectByAllBusinessDocument(empNum);
        List<EdsmDocumentDTO> allCashDocument = edsmService.selectByAllCashDocument(empNum);
        List<EdsmDocumentDTO> allLetterDocument = edsmService.selectByAllLetterDocument(empNum);
        List<EdsmDocumentDTO> allLeavesDocument = edsmService.selectByAllLeavesDocument(empNum);
        List<EdsmDocumentDTO> allOvertimeDocument = edsmService.selectByAllOvertimeDocument(empNum);

        //결재 대기 갯수 조회
        int waitCount = edsmService.selectByWaitCount(empNum);
        model.addAttribute("waitCount", waitCount);
        //결재 예정 갯수 조회
        int expectedCount = edsmService.selectByExpectedCount(empNum);
        model.addAttribute("expectedCount", expectedCount);


        // 모델에 데이터 추가
        model.addAttribute("allDocumentList", allDocument);
        model.addAttribute("allBusinessDocument", allBusinessDocument);
        model.addAttribute("allCashDocument", allCashDocument);
        model.addAttribute("allLetterDocument", allLetterDocument);
        model.addAttribute("allLeavesDocument", allLeavesDocument);
        model.addAttribute("allOvertimeDocument", allOvertimeDocument);

        return "edsm/main";
    }

    // 결재 상태 <대기>
    @RequestMapping("/wait")
    public String wait(Model model, HttpServletRequest request) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        String empNum = (String) request.getAttribute("empNum");
        model.addAttribute("wait", "대기");
        //결재 대기 갯수 조회
        int waitCount = edsmService.selectByWaitCount(empNum);
        model.addAttribute("waitCount", waitCount);
        //결재 예정 갯수 조회
        int expectedCount = edsmService.selectByExpectedCount(empNum);
        model.addAttribute("expectedCount", expectedCount);

        List<EdsmDocumentDTO> waitDocument = edsmService.selectByAllApprovalFromIdWait(empNum);
        model.addAttribute("waitDocumentList", waitDocument);

        return "edsm/wait";
    }

    // 결재 상태 <예정>
    @RequestMapping("/expected")
    public String expected(Model model, HttpServletRequest request) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        String empNum = (String) request.getAttribute("empNum");
        model.addAttribute("expected", "예정");

        //결재 대기 갯수 조회
        int waitCount = edsmService.selectByWaitCount(empNum);
        model.addAttribute("waitCount", waitCount);
        //결재 예정 갯수 조회
        int expectedCount = edsmService.selectByExpectedCount(empNum);
        model.addAttribute("expectedCount", expectedCount);
        List<EdsmDocumentDTO> expectedDocument = edsmService.selectByAllApprovalFromIdExpected(empNum);
        model.addAttribute("expectedDocumentList", expectedDocument);

        return "edsm/expected";
    }

    // 나의 문서함 - 기안한 문서
    @RequestMapping("/myWritten")
    public String myWritten(Model model, HttpServletRequest request) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        String empNum = (String) request.getAttribute("empNum");

        //결재 대기 갯수 조회
        int waitCount = edsmService.selectByWaitCount(empNum);
        model.addAttribute("waitCount", waitCount);
        //결재 예정 갯수 조회
        int expectedCount = edsmService.selectByExpectedCount(empNum);
        model.addAttribute("expectedCount", expectedCount);
        // 각 문서 유형별 기안 문서 조회
        List<EdsmDocumentDTO> myWrittenDocumentBc = edsmService.selectByAllMyWrittenDocumentBc(empNum);
        List<EdsmDocumentDTO> myWrittenDocumentCdv = edsmService.selectByAllMyWrittenDocumentCdv(empNum);
        List<EdsmDocumentDTO> myWrittenDocumentLoa = edsmService.selectByAllMyWrittenDocumentLoa(empNum);
        List<EdsmDocumentDTO> myWrittenDocumentLeaves = edsmService.selectByAllMyWrittenDocumentLeaves(empNum);
        List<EdsmDocumentDTO> myWrittenDocumentOvertime = edsmService.selectByAllMyWrittenDocumentOvertime(empNum);

        // 모델에 데이터 추가
        model.addAttribute("myWrittenDocumentBc", myWrittenDocumentBc);
        model.addAttribute("myWrittenDocumentCdv", myWrittenDocumentCdv);
        model.addAttribute("myWrittenDocumentLoa", myWrittenDocumentLoa);
        model.addAttribute("myWrittenDocumentLeaves", myWrittenDocumentLeaves);
        model.addAttribute("myWrittenDocumentOvertime", myWrittenDocumentOvertime);

        return "edsm/edsmMyDocument/myWrittenDocument";
    }

    // 나의 문서함 - 승인된 문서
    @RequestMapping("/approval")
    public String approval(Model model, HttpServletRequest request) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        String empNum = (String) request.getAttribute("empNum");
//결재 대기 갯수 조회
        int waitCount = edsmService.selectByWaitCount(empNum);
        model.addAttribute("waitCount", waitCount);
        //결재 예정 갯수 조회
        int expectedCount = edsmService.selectByExpectedCount(empNum);
        model.addAttribute("expectedCount", expectedCount);
        // 각 문서 유형별 승인 문서 조회
        List<EdsmDocumentDTO> myApprovalDocumentBc = edsmService.selectByAllMyApprovalDocumentBc(empNum);
        List<EdsmDocumentDTO> myApprovalDocumentCdv = edsmService.selectByAllMyApprovalDocumentCdv(empNum);
        List<EdsmDocumentDTO> myApprovalDocumentLoa = edsmService.selectByAllMyApprovalDocumentLoa(empNum);
        List<EdsmDocumentDTO> myApprovalDocumentLeaves = edsmService.selectByAllMyApprovalDocumentLeaves(empNum);
        List<EdsmDocumentDTO> myApprovalDocumentOvertime = edsmService.selectByAllMyApprovalDocumentOvertime(empNum);

        // 모델에 데이터 추가
        model.addAttribute("myApprovalDocumentBc", myApprovalDocumentBc);
        model.addAttribute("myApprovalDocumentCdv", myApprovalDocumentCdv);
        model.addAttribute("myApprovalDocumentLoa", myApprovalDocumentLoa);
        model.addAttribute("myApprovalDocumentLeaves", myApprovalDocumentLeaves);
        model.addAttribute("myApprovalDocumentOvertime", myApprovalDocumentOvertime);

        return "edsm/edsmMyDocument/approvalDocument";
    }

    // 나의 문서함 - 반려된 문서
    @RequestMapping("/rejected")
    public String rejected(Model model, HttpServletRequest request) {
        String redirectPath = validateEmployeeAndAddToModel(model, request);
        if (redirectPath != null) {
            return redirectPath;
        }

        String empNum = (String) request.getAttribute("empNum");
//결재 대기 갯수 조회
        int waitCount = edsmService.selectByWaitCount(empNum);
        model.addAttribute("waitCount", waitCount);
        //결재 예정 갯수 조회
        int expectedCount = edsmService.selectByExpectedCount(empNum);
        model.addAttribute("expectedCount", expectedCount);
        // 각 문서 유형별 반려 문서 조회
        List<EdsmDocumentDTO> myRejectedDocumentBc = edsmService.selectByAllMyRejectedDocumentBc(empNum);
        List<EdsmDocumentDTO> myRejectedDocumentCdv = edsmService.selectByAllMyRejectedDocumentCdv(empNum);
        List<EdsmDocumentDTO> myRejectedDocumentLoa = edsmService.selectByAllMyRejectedDocumentLoa(empNum);
        List<EdsmDocumentDTO> myRejectedDocumentLeaves = edsmService.selectByAllMyRejectedDocumentLeaves(empNum);
        List<EdsmDocumentDTO> myRejectedDocumentOvertime = edsmService.selectByAllMyRejectedDocumentOvertime(empNum);

        // 모델에 데이터 추가
        model.addAttribute("myRejectedDocumentBc", myRejectedDocumentBc);
        model.addAttribute("myRejectedDocumentCdv", myRejectedDocumentCdv);
        model.addAttribute("myRejectedDocumentLoa", myRejectedDocumentLoa);
        model.addAttribute("myRejectedDocumentLeaves", myRejectedDocumentLeaves);
        model.addAttribute("myRejectedDocumentOvertime", myRejectedDocumentOvertime);

        return "edsm/edsmMyDocument/rejectedDocument";
    }
}