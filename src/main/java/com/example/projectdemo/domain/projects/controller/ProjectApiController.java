package com.example.projectdemo.domain.projects.controller;

import com.example.projectdemo.domain.projects.dto.ProjectDTO;
import com.example.projectdemo.domain.projects.dto.ProjectMemberDTO;
import com.example.projectdemo.domain.projects.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectApiController {

    @Autowired
    private ProjectService projectService;

    /**
     * 프로젝트 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    /**
     * 직원이 참여중인 프로젝트 목록 조회
     */
    @GetMapping("/employee")
    public ResponseEntity<List<ProjectDTO>> getProjectsByEmployee(HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(projectService.getProjectsByEmployee(empNum));
    }

    /**
     * 부서별 프로젝트 목록 조회
     */
    @GetMapping("/department/{depId}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByDepartment(@PathVariable Integer depId) {
        return ResponseEntity.ok(projectService.getProjectsByDepartment(depId));
    }

    /**
     * 프로젝트 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Integer id) {
        ProjectDTO project = projectService.getProjectById(id);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(project);
    }

    /**
     * 신규 프로젝트 등록
     */
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO project, HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 현재 사용자를 프로젝트 매니저로 설정
        project.setManagerEmpNum(empNum);

        ProjectDTO createdProject = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    /**
     * 프로젝트 정보 업데이트
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Integer id, @RequestBody ProjectDTO project) {
        project.setId(id);
        ProjectDTO updatedProject = projectService.updateProject(project);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * 프로젝트 상태 업데이트
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectDTO> updateProjectStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ProjectDTO updatedProject = projectService.updateProjectStatus(id, status);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * 프로젝트 완료 처리
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ProjectDTO> completeProject(@PathVariable Integer id) {
        ProjectDTO completedProject = projectService.completeProject(id);
        return ResponseEntity.ok(completedProject);
    }

    /**
     * 프로젝트 멤버 목록 조회
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<ProjectMemberDTO>> getProjectMembers(@PathVariable Integer id) {
        return ResponseEntity.ok(projectService.getProjectMembers(id));
    }

    /**
     * 프로젝트에 멤버 추가
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<?> addProjectMember(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String empNum = body.get("empNum");
        String role = body.get("role");

        if (empNum == null || role == null) {
            return ResponseEntity.badRequest().build();
        }

        projectService.addProjectMember(id, empNum, role);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 프로젝트 멤버 역할 변경
     */
    @PutMapping("/{id}/members/{empNum}")
    public ResponseEntity<?> updateProjectMemberRole(@PathVariable Integer id, @PathVariable String empNum, @RequestBody Map<String, String> body) {
        String role = body.get("role");

        if (role == null) {
            return ResponseEntity.badRequest().build();
        }

        projectService.updateProjectMemberRole(id, empNum, role);
        return ResponseEntity.ok().build();
    }

    /**
     * 프로젝트에서 멤버 제거
     */
    @DeleteMapping("/{id}/members/{empNum}")
    public ResponseEntity<?> removeProjectMember(@PathVariable Integer id, @PathVariable String empNum) {
        projectService.removeProjectMember(id, empNum);
        return ResponseEntity.ok().build();
    }
}