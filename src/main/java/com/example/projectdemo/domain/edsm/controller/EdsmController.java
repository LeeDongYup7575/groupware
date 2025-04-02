package com.example.projectdemo.domain.edsm.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/edsm")
public class EdsmController {

    @Autowired
    private HttpSession session;

    @Autowired
    private JwtTokenUtil jwt;

    @Autowired
    private EmployeesService employeeService;

    @Autowired
    private EmployeesMapper employeesMapper;

    @RequestMapping("/main")
    public String main(Model model, HttpServletRequest request) {


    model.addAttribute("main","전체");


        return "edsm/edsmMain";
    }

    @RequestMapping("/wait")
    public String wait(Model model) {

        model.addAttribute("wait","대기");

        return "edsm/edsmWait";
    }

    @RequestMapping("/progress")
    public String progress(Model model) {

        model.addAttribute("progress","진행");

        return "edsm/edsmProgress";
    }

    @RequestMapping("/complete")
    public String complete(Model model) {

        model.addAttribute("complete","완료");

        return "edsm/edsmComplete";
    }

    @RequestMapping("/create")
    public String create(Model model) {



        return "edsm/edsmInput";
    }


    @RequestMapping("/cdv")
    public String inputCdv(Model model) {

        String cdv = "cdv";
        model.addAttribute("cdv",cdv);

        List<EmployeesDTO> list = employeesMapper.selectEmpAll();
        model.addAttribute("list",list);
        return "edsm/edsmInputCdv";
    }

    @RequestMapping("/bc")
    public String inputBc(Model model, HttpServletRequest request) {




    String bc = "bc";
    model.addAttribute("bc",bc);

        List<EmployeesDTO> list = employeesMapper.selectEmpAll();
        List<EmployeesDTO> empList = new ArrayList<>();
        for(int i = 0; list.size()>i; i++) {
            String empNum1 = list.get(i).getEmpNum();
            EmployeesDTO list_emp = employeeService.findByEmpNum(empNum1);
            empList.add(list_emp);
        }
        model.addAttribute("list_emp", empList);


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


        return "edsm/edsmInputBc";
    }

    @RequestMapping("/loa")
    public String inputLoa(Model model) {

        String loa = "loa";
        model.addAttribute("loa",loa);

        List<EmployeesDTO> list = employeesMapper.selectEmpAll();
        model.addAttribute("list",list);
        return "edsm/edsmInputLoa";
    }



}
