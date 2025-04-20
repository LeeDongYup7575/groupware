package com.example.projectdemo.domain.projects.controller;

import com.example.projectdemo.domain.notification.service.NotificationEventHandler;
import com.example.projectdemo.domain.projects.dto.ProjectDTO;
import com.example.projectdemo.domain.projects.dto.ProjectMemberDTO;
import com.example.projectdemo.domain.projects.dto.TaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskSummaryDTO;
import com.example.projectdemo.domain.projects.service.ProjectService;
import com.example.projectdemo.domain.projects.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectApiController {

    private static final Logger log = LoggerFactory.getLogger(ProjectApiController.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private NotificationEventHandler notificationEventHandler;

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

    @GetMapping("/my-projects")
    public ResponseEntity<List<ProjectDTO>> getMyProjects(HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ProjectDTO> projects = projectService.getProjectsByEmployee(empNum);
        return ResponseEntity.ok(projects);
    }

    // 새 프로젝트 등록
    @PostMapping("/create")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO project) {
        try {
            log.info("프로젝트 등록 요청: {}", project);

            // 데이터 유효성 검사 추가
            if (project.getName() == null || project.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            if (project.getManagerEmpNum() == null || project.getManagerEmpNum().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            // 필수 필드가 없는 경우 기본값 설정
            if (project.getStatus() == null) {
                project.setStatus("준비중");
            }

            ProjectDTO createdProject = projectService.createProject(project);

            // 프로젝트 멤버 추가 처리
            if (project.getMembers() != null && !project.getMembers().isEmpty()) {
                for (Map<String, String> member : project.getMembers()) {
                    String empNum = member.get("empNum");
                    String role = member.get("role");

                    // 유효성 검사
                    if (empNum == null || empNum.trim().isEmpty() ||
                            role == null || role.trim().isEmpty()) {
                        continue;  // 유효하지 않은 멤버는 건너뜀
                    }

                    // 본인(매니저)은 멤버로 추가하지 않음 (이미 프로젝트 생성 시 포함됨)
                    if (!empNum.equals(project.getManagerEmpNum())) {
                        projectService.addProjectMember(
                                createdProject.getId(),
                                empNum,
                                role
                        );
                    }
                }
            }

            // 프로젝트 멤버 목록 조회
            List<ProjectMemberDTO> members = projectService.getProjectMembers(createdProject.getId());

            // 새 프로젝트 생성 알림 발송
            notificationEventHandler.handleProjectCreationNotification(createdProject, members);

            log.info("프로젝트 등록 성공: ID={}", createdProject.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
        } catch (Exception e) {
            log.error("프로젝트 등록 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // 프로젝트 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Integer id, @RequestBody ProjectDTO project) {
        try {
            project.setId(id);
            ProjectDTO updatedProject = projectService.updateProject(project);
            return ResponseEntity.ok(updatedProject);
        } catch (Exception e) {
            log.error("프로젝트 수정 실패: ID={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // 프로젝트 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectDTO> updateProjectStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> statusMap) {
        try {
            String status = statusMap.get("status");
            if (status == null) {
                return ResponseEntity.badRequest().build();
            }
            ProjectDTO project = projectService.updateProjectStatus(id, status);
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            log.error("프로젝트 상태 변경 실패: ID={}, status={}", id, statusMap.get("status"), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // 프로젝트 완료 처리
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ProjectDTO> completeProject(@PathVariable Integer id) {
        try {
            ProjectDTO project = projectService.completeProject(id);

            // 프로젝트 완료 알림 발송
            List<ProjectMemberDTO> members = projectService.getProjectMembers(id);
            notificationEventHandler.handleProjectCompletionNotification(project, members);

            return ResponseEntity.ok(project);
        } catch (Exception e) {
            log.error("프로젝트 완료 처리 실패: ID={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // 프로젝트 멤버 추가
    @PostMapping("/{id}/members")
    public ResponseEntity<?> addProjectMember(
            @PathVariable Integer id,
            @RequestBody Map<String, String> memberInfo) {
        try {
            String empNum = memberInfo.get("empNum");
            String role = memberInfo.get("role");

            if (empNum == null || role == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "사원번호와 역할은 필수입니다.")
                );
            }

            projectService.addProjectMember(id, empNum, role);

            // 프로젝트 멤버 추가 알림
            ProjectDTO project = projectService.getProjectById(id);
            ProjectMemberDTO newMember = new ProjectMemberDTO();
            newMember.setEmpNum(empNum);
            newMember.setRole(role);

            notificationEventHandler.handleProjectCreationNotification(project, List.of(newMember));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "멤버가 성공적으로 추가되었습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("프로젝트 멤버 추가 실패: projectId={}, empNum={}", id, memberInfo.get("empNum"), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // 프로젝트 멤버 역할 수정
    @PatchMapping("/{id}/members/{empNum}/role")
    public ResponseEntity<?> updateMemberRole(
            @PathVariable Integer id,
            @PathVariable String empNum,
            @RequestBody Map<String, String> roleInfo) {
        try {
            String role = roleInfo.get("role");

            if (role == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "역할은 필수입니다.")
                );
            }

            projectService.updateProjectMemberRole(id, empNum, role);

            return ResponseEntity.ok(
                    Map.of("success", true, "message", "멤버 역할이 수정되었습니다.")
            );
        } catch (Exception e) {
            log.error("프로젝트 멤버 역할 수정 실패: projectId={}, empNum={}", id, empNum, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // 프로젝트 멤버 제거
    @DeleteMapping("/{id}/members/{empNum}")
    public ResponseEntity<?> removeProjectMember(
            @PathVariable Integer id,
            @PathVariable String empNum) {
        try {
            projectService.removeProjectMember(id, empNum);

            return ResponseEntity.ok(
                    Map.of("success", true, "message", "멤버가 제거되었습니다.")
            );
        } catch (Exception e) {
            log.error("프로젝트 멤버 제거 실패: projectId={}, empNum={}", id, empNum, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}