package com.example.projectdemo.domain.projects.controller;

import com.example.projectdemo.domain.projects.dto.ProjectDTO;
import com.example.projectdemo.domain.projects.dto.ProjectMemberDTO;
import com.example.projectdemo.domain.projects.dto.TaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskSummaryDTO;
import com.example.projectdemo.domain.projects.service.ProjectService;
import com.example.projectdemo.domain.projects.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectApiController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    // 모든 프로젝트 조회
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    // 특정 프로젝트 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Integer id) {
        ProjectDTO project = projectService.getProjectById(id);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(project);
    }

    // 프로젝트에 속한 업무 목록 조회
    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskDTO>> getProjectTasks(@PathVariable Integer id) {
        List<TaskDTO> tasks = taskService.getTasksByProject(id);
        return ResponseEntity.ok(tasks);
    }

    // 프로젝트 업무 요약 정보 조회
    @GetMapping("/{id}/task-summary")
    public ResponseEntity<TaskSummaryDTO> getProjectTaskSummary(@PathVariable Integer id) {
        TaskSummaryDTO summary = projectService.getProjectTaskSummary(id);
        return ResponseEntity.ok(summary);
    }

    // 프로젝트 멤버 목록 조회
    @GetMapping("/{id}/members")
    public ResponseEntity<List<ProjectMemberDTO>> getProjectMembers(@PathVariable Integer id) {
        List<ProjectMemberDTO> members = projectService.getProjectMembers(id);
        return ResponseEntity.ok(members);
    }

    // 새 프로젝트 등록
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO project) {
        ProjectDTO createdProject = projectService.createProject(project);
        return ResponseEntity.ok(createdProject);
    }

    // 프로젝트 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Integer id, @RequestBody ProjectDTO project) {
        project.setId(id);
        ProjectDTO updatedProject = projectService.updateProject(project);
        return ResponseEntity.ok(updatedProject);
    }

    // 프로젝트 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectDTO> updateProjectStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> statusMap) {
        String status = statusMap.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }
        ProjectDTO project = projectService.updateProjectStatus(id, status);
        return ResponseEntity.ok(project);
    }

    // 프로젝트 완료 처리
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ProjectDTO> completeProject(@PathVariable Integer id) {
        ProjectDTO project = projectService.completeProject(id);
        return ResponseEntity.ok(project);
    }

    // 프로젝트 멤버 추가
    @PostMapping("/{id}/members")
    public ResponseEntity<?> addProjectMember(
            @PathVariable Integer id,
            @RequestBody Map<String, String> memberInfo) {
        String empNum = memberInfo.get("empNum");
        String role = memberInfo.get("role");

        if (empNum == null || role == null) {
            return ResponseEntity.badRequest().build();
        }

        projectService.addProjectMember(id, empNum, role);
        return ResponseEntity.ok().build();
    }

    // 프로젝트 멤버 역할 수정
    @PatchMapping("/{id}/members/{empNum}/role")
    public ResponseEntity<?> updateMemberRole(
            @PathVariable Integer id,
            @PathVariable String empNum,
            @RequestBody Map<String, String> roleInfo) {
        String role = roleInfo.get("role");

        if (role == null) {
            return ResponseEntity.badRequest().build();
        }

        projectService.updateProjectMemberRole(id, empNum, role);
        return ResponseEntity.ok().build();
    }

    // 프로젝트 멤버 제거
    @DeleteMapping("/{id}/members/{empNum}")
    public ResponseEntity<?> removeProjectMember(
            @PathVariable Integer id,
            @PathVariable String empNum) {
        projectService.removeProjectMember(id, empNum);
        return ResponseEntity.ok().build();
    }
}