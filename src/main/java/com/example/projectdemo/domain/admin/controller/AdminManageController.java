package com.example.projectdemo.domain.admin.controller;

import com.example.projectdemo.domain.admin.service.ManageService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/manage")
public class AdminManageController {

    @Autowired
    private ManageService manageService;

    @RequestMapping("/department")
    public ResponseEntity<?> getDepartmentList() {
        return ResponseEntity.ok(manageService.getDepartmentList());
    }
    @RequestMapping("/position")
    public ResponseEntity<?> getPositionList() {
        return ResponseEntity.ok(manageService.getPositionList());
    }
    @RequestMapping("/addDepartment/{name}")
    public ResponseEntity<?> addDepartment(@PathVariable String name) {
        return ResponseEntity.ok(manageService.addDepartment(name));
    }
    @RequestMapping("/addPosition/{title}")
    public ResponseEntity<?> addPosition(@PathVariable String title) {
        return ResponseEntity.ok(manageService.addPosition(title));
    }
    @RequestMapping("/deleteDepartment")
    public ResponseEntity<?> deleteDepartment(@RequestParam("ids") int[] ids) {
        return ResponseEntity.ok(manageService.deleteDepartment(ids));
    }
}
