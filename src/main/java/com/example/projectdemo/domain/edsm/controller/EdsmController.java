package com.example.projectdemo.domain.edsm.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.edsm.dao.EdsmDAO;
import com.example.projectdemo.domain.edsm.dto.EdsmDocumentDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/edsm")
public class EdsmController {

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


        List<EdsmDocumentDTO> allDocument = edao.selectByAllDocument(empNum);
        model.addAttribute("allDocumentList",allDocument);

        return "edsm/main";
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


        List<EdsmDocumentDTO> allDocument = edao.selectByAllDocument(empNum);

        model.addAttribute("allDocumentList",allDocument);

        return "edsm/main";
    }
    //결재 상태 <완료>
    @RequestMapping("/complete")
    public String complete(Model model) {

        model.addAttribute("complete","완료");

        return "edsm/complete";
    }






}
