package com.example.projectdemo.domain.admin.service;

import com.example.projectdemo.domain.admin.dao.ManageDAO;
import com.example.projectdemo.domain.employees.dto.DepartmentsDTO;
import com.example.projectdemo.domain.employees.dto.PositionsDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageService {

    @Autowired
    private ManageDAO managedao;

    @Autowired
    private EmployeesService employeesService;

    public List<DepartmentsDTO> getDepartmentList() {
        return managedao.getDepartmentList();
    }
    public List<PositionsDTO> getPositionList() {
        return managedao.getPositionList();
    }
    public String addDepartment(String name) {
        return managedao.addDepartment(name);
    }
    public String addPosition(String title) {
        return managedao.addPosition(title);
    }
    public String deleteDepartment(int[] ids) {
//        mangedao. 부서를
        for(Integer id : ids) {
            employeesService.updateByDepId(id);
        }
        return managedao.deleteDepartment(ids);
    }
}
