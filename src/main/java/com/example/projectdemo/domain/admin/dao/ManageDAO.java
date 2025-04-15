package com.example.projectdemo.domain.admin.dao;

import com.example.projectdemo.domain.employees.dto.DepartmentsDTO;
import com.example.projectdemo.domain.employees.dto.PositionsDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ManageDAO {

    @Autowired
    private SqlSession mybatis;

    public List<DepartmentsDTO> getDepartmentList() {
        return mybatis.selectList("AdminManageMapper.getDepartmentList");
    }

    public List<PositionsDTO> getPositionList() {
        return mybatis.selectList("AdminManageMapper.getPositionList");
    }

    public String addDepartment(String name) {
        if (mybatis.insert("AdminManageMapper.addDepartment",name) > 0) {

            return "success";
        }
        return "false";
    }
    public String addPosition(String title) {
        if (mybatis.insert("AdminManageMapper.addPosition",title) > 0) {

            return "success";
        }
        return "false";
    }
    public String deleteDepartment(int[] ids) {
        int count = 0;
        for(int id:ids) {
            count+= mybatis.delete("AdminManageMapper.deleteDepartment",id);
        }
        return count > 0 ? "success" : "false";
    }
}
