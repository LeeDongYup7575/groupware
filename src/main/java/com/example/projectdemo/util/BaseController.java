package com.example.projectdemo.util;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.exception.ResourceNotFoundException;
import com.example.projectdemo.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public abstract class BaseController {

    @Autowired
    protected EmployeesService employeesService;

    protected EmployeesDTO validateAndGetEmployee(HttpServletRequest request) {
        String empNum = SecurityUtils.getEmployeeNumber(request);
        if (empNum == null) {
            throw new UnauthorizedException("인증되지 않은 사용자입니다.");
        }

        EmployeesDTO employee = employeesService.findByEmpNum(empNum);
        if (employee == null) {
            throw new ResourceNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        return employee;
    }
}
