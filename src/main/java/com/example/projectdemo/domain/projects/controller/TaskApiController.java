package com.example.projectdemo.domain.projects.controller;

import com.example.projectdemo.domain.projects.dto.SubTaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskLogDTO;
import com.example.projectdemo.domain.projects.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {

    @Autowired
    private TaskService taskService;

    /**
     * 프로젝트별 업무 목록 조회
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDTO>> getTasksByProject(@PathVariable Integer projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
    }

    /**
     * 담당자별 업무 목록 조회
     */
    @GetMapping("/assignee/{empNum}")
    public ResponseEntity<List<TaskDTO>> getTasksByAssignee(@PathVariable String empNum) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(empNum));
    }

    /**
     * 보고자별 업무 목록 조회
     */
    @GetMapping("/reporter/{empNum}")
    public ResponseEntity<List<TaskDTO>> getTasksByReporter(@PathVariable String empNum) {
        return ResponseEntity.ok(taskService.getTasksByReporter(empNum));
    }

    /**
     * 상태별 업무 목록 조회
     */
    @GetMapping("/project/{projectId}/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(
            @PathVariable Integer projectId,
            @PathVariable String status) {
        return ResponseEntity.ok(taskService.getTasksByStatus(projectId, status));
    }

    /**
     * 업무 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Integer id) {
        TaskDTO task = taskService.getTaskById(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    /**
     * 신규 업무 등록
     */
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO task, HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 현재 사용자를 업무 등록자로 설정
        task.setReporterEmpNum(empNum);

        TaskDTO createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    /**
     * 업무 정보 업데이트
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Integer id, @RequestBody TaskDTO task) {
        task.setId(id);
        TaskDTO updatedTask = taskService.updateTask(task);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * 업무 상태 업데이트
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        TaskDTO updatedTask = taskService.updateTaskStatus(id, status);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * 업무 진행률 업데이트
     */
    @PatchMapping("/{id}/progress")
    public ResponseEntity<TaskDTO> updateTaskProgress(@PathVariable Integer id, @RequestBody Map<String, Integer> body) {
        Integer progress = body.get("progress");
        if (progress == null || progress < 0 || progress > 100) {
            return ResponseEntity.badRequest().build();
        }

        TaskDTO updatedTask = taskService.updateTaskProgress(id, progress);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * 업무 완료 처리
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskDTO> completeTask(@PathVariable Integer id) {
        TaskDTO completedTask = taskService.completeTask(id);
        return ResponseEntity.ok(completedTask);
    }

    /**
     * 업무 로그 조회
     */
    @GetMapping("/{id}/logs")
    public ResponseEntity<List<TaskLogDTO>> getTaskLogs(@PathVariable Integer id) {
        return ResponseEntity.ok(taskService.getTaskLogs(id));
    }

    /**
     * 업무 로그 추가
     */
    @PostMapping("/{id}/logs")
    public ResponseEntity<TaskLogDTO> addTaskLog(
            @PathVariable Integer id,
            @RequestBody TaskLogDTO log,
            HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.setTaskId(id);
        log.setEmpNum(empNum);

        TaskLogDTO addedLog = taskService.addTaskLog(log);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedLog);
    }

    /**
     * 하위 업무 목록 조회
     */
    @GetMapping("/{id}/subtasks")
    public ResponseEntity<List<SubTaskDTO>> getSubTasksByTask(@PathVariable Integer id) {
        return ResponseEntity.ok(taskService.getSubTasksByTask(id));
    }

    /**
     * 하위 업무 추가
     */
    @PostMapping("/{id}/subtasks")
    public ResponseEntity<SubTaskDTO> addSubTask(
            @PathVariable Integer id,
            @RequestBody SubTaskDTO subTask) {
        subTask.setTaskId(id);
        SubTaskDTO addedSubTask = taskService.addSubTask(subTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedSubTask);
    }

    /**
     * 하위 업무 업데이트
     */
    @PutMapping("/subtasks/{id}")
    public ResponseEntity<SubTaskDTO> updateSubTask(
            @PathVariable Integer id,
            @RequestBody SubTaskDTO subTask) {
        subTask.setId(id);
        SubTaskDTO updatedSubTask = taskService.updateSubTask(subTask);
        return ResponseEntity.ok(updatedSubTask);
    }

    /**
     * 하위 업무 상태 업데이트
     */
    @PatchMapping("/subtasks/{id}/status")
    public ResponseEntity<SubTaskDTO> updateSubTaskStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        SubTaskDTO updatedSubTask = taskService.updateSubTaskStatus(id, status);
        return ResponseEntity.ok(updatedSubTask);
    }

    /**
     * 하위 업무 완료 처리
     */
    @PatchMapping("/subtasks/{id}/complete")
    public ResponseEntity<SubTaskDTO> completeSubTask(@PathVariable Integer id) {
        SubTaskDTO completedSubTask = taskService.completeSubTask(id);
        return ResponseEntity.ok(completedSubTask);
    }
}