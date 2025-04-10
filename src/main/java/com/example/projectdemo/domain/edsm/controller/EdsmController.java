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
    EdsmDAO edao;

    @Autowired
    private EmployeesService employeeService;


    @Autowired
    private EdsmService edsmService;



    // 결재상태 사이드바 컨트롤러
    // 전자결재 메인화면
    @RequestMapping("/main")
    public String main(Model model, HttpServletRequest request) {
// JWT 필터에서 설정한 사원번호 추출
        String empNum = (String)request.getAttribute("empNum");

        if (empNum == null) { //예외처리
            return "redirect:/edsm/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { //예외처리
            return "redirect:/edsm/main";
        }
        model.addAttribute("employee", employee);
        model.addAttribute("main","전체");

        List<EdsmDocumentDTO> allDocument = edsmService.selectByAllDocument(empNum);
        List<EdsmDocumentDTO> allBusinessDocument = edsmService.selectByAllBusinessDocument(empNum);
        List<EdsmDocumentDTO> allCashDocument = edsmService.selectByAllCashDocument(empNum);
        List<EdsmDocumentDTO> allLetterDocument = edsmService.selectByAllLetterDocument(empNum);
        List<EdsmDocumentDTO> allLeavesDocument = edsmService.selectByAllLeavesDocument(empNum);
        List<EdsmDocumentDTO> allOvertimeDocument = edsmService.selectByAllOvertimeDocument(empNum);

        model.addAttribute("allBusinessDocument", allBusinessDocument);
        model.addAttribute("allCashDocument", allCashDocument);
        model.addAttribute("allLetterDocument", allLetterDocument);
        model.addAttribute("allLeavesDocument", allLeavesDocument);
        model.addAttribute("allDocumentList",allDocument);
        model.addAttribute("allOvertimeDocument",allOvertimeDocument);

        return "edsm/main";
    }


    //결재 상태 <대기>
    @RequestMapping("/wait")
    public String wait(Model model, HttpServletRequest request) {
// JWT 필터에서 설정한 사원번호 추출
        String empNum = (String)request.getAttribute("empNum");

        if (empNum == null) { //예외처리
            return "redirect:/edsm/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { //예외처리
            return "redirect:/edsm/main";
        }

        model.addAttribute("employee", employee);
        model.addAttribute("wait","대기");

        List<EdsmDocumentDTO> waitDocument = edsmService.selectByAllApprovalFromIdWait(empNum);

        model.addAttribute("waitDocumentList",waitDocument);

        return "edsm/wait";
    }


    //결재 상태 <예정>
    @RequestMapping("/expected")
    public String excepted(Model model, HttpServletRequest request) {
// JWT 필터에서 설정한 사원번호 추출
        String empNum = (String)request.getAttribute("empNum");

        if (empNum == null) { //예외처리
            return "redirect:/edsm/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { //예외처리
            return "redirect:/edsm/main";
        }

        model.addAttribute("employee", employee);
        model.addAttribute("expected","예정");

        List<EdsmDocumentDTO> expectedDocument=edsmService.selectByAllApprovalFromIdExpected(empNum);

        model.addAttribute("expectedDocumentList",expectedDocument);

        return "edsm/expected";
    }


    //결재 상태 <진행 중>
    @RequestMapping("/progress")
    public String progress(Model model, HttpServletRequest request) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String)request.getAttribute("empNum");

        if (empNum == null) { //예외처리
            return "redirect:/edsm/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { //예외처리
            return "redirect:/edsm/main";
        }

        model.addAttribute("employee", employee);

        model.addAttribute("progress","진행");

        model.addAttribute("main","전체");

        //로그인한 사원이 결재권자이거나 본인이 기안한문서가 진행중인경우(!수정해야함)
        List<EdsmDocumentDTO> allDocument = edao.selectByAllDocument(empNum);

        model.addAttribute("allDocumentList",allDocument);

        return "edsm/main";
    }







}
