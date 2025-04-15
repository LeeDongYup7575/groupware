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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {

    @Autowired
    private TaskService taskService;

    /**
     * 모든 업무 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
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
     * 특정 사원이 담당하는 업무 목록 조회
     */
    @GetMapping("/assignee")
    public ResponseEntity<List<TaskDTO>> getTasksByAssignee(HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(taskService.getTasksByAssignee(empNum));
    }

    /**
     * 특정 사원이 생성한 업무 목록 조회
     */
    @GetMapping("/created")
    public ResponseEntity<List<TaskDTO>> getTasksByCreator(HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(taskService.getTasksByCreator(empNum));
    }

    /**
     * 프로젝트별 업무 목록 조회
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDTO>> getTasksByProject(@PathVariable Integer projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
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

        // 현재 사용자를 업무 작성자로 설정
        task.setReporterEmpNum(empNum);
        task.setCreatedAt(LocalDateTime.now());

        TaskDTO createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    /**
     * 업무 정보 업데이트
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Integer id, @RequestBody TaskDTO task,
                                              HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        task.setId(id);
        TaskDTO updatedTask = taskService.updateTask(task);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * 업무 상태 업데이트
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable Integer id,
                                                    @RequestBody Map<String, String> body,
                                                    HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String status = body.get("status");
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        TaskDTO updatedTask = taskService.updateTaskStatus(id, status, empNum);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * 업무 진행률 업데이트
     */
    @PatchMapping("/{id}/progress")
    public ResponseEntity<TaskDTO> updateTaskProgress(@PathVariable Integer id,
                                                      @RequestBody Map<String, Integer> body,
                                                      HttpServletRequest request) {
        try {
            String empNum = (String) request.getAttribute("empNum");
            if (empNum == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Integer progress = body.get("progress");
            if (progress == null || progress < 0 || progress > 100) {
                return ResponseEntity.badRequest().build();
            }

            System.out.println("TaskId: " + id + ", Progress: " + progress + ", EmpNum: " + empNum); // 로깅 추가

            TaskDTO updatedTask = taskService.updateTaskProgress(id, progress, empNum);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 스택 트레이스 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 업무 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Integer id, HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            taskService.deleteTask(id, empNum);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 하위 업무 추가
     */
    @PostMapping("/{taskId}/subtasks")
    public ResponseEntity<SubTaskDTO> addSubTask(@PathVariable Integer taskId,
                                                 @RequestBody SubTaskDTO subTask,
                                                 HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        subTask.setTaskId(taskId);
        SubTaskDTO createdSubTask = taskService.createSubTask(subTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubTask);
    }

    /**
     * 하위 업무 수정
     */
    @PutMapping("/{taskId}/subtasks/{subTaskId}")
    public ResponseEntity<SubTaskDTO> updateSubTask(@PathVariable Integer taskId,
                                                    @PathVariable Integer subTaskId,
                                                    @RequestBody SubTaskDTO subTask,
                                                    HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        subTask.setId(subTaskId);
        subTask.setTaskId(taskId);
        SubTaskDTO updatedSubTask = taskService.updateSubTask(subTask);
        return ResponseEntity.ok(updatedSubTask);
    }

    /**
     * 하위 업무 삭제
     */
    @DeleteMapping("/{taskId}/subtasks/{subTaskId}")
    public ResponseEntity<?> deleteSubTask(@PathVariable Integer taskId,
                                           @PathVariable Integer subTaskId,
                                           HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            taskService.deleteSubTask(subTaskId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 하위 업무 완료 상태 변경
     */
    @PatchMapping("/{taskId}/subtasks/{subTaskId}/completion")
    public ResponseEntity<?> updateSubTaskCompletion(@PathVariable Integer taskId,
                                                     @PathVariable Integer subTaskId,
                                                     @RequestBody Map<String, Boolean> body,
                                                     HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Boolean completed = body.get("completed");
        if (completed == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // completed 값에 따라 상태를 "완료" 또는 "미완료"로 설정
            String status = completed ? "완료" : "미완료";

            // 메서드 시그니처도 변경해야 함 (TaskService에서)
            taskService.updateSubTaskStatus(subTaskId, status);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 특정 업무의 로그 조회
     */
    @GetMapping("/{taskId}/logs")
    public ResponseEntity<List<TaskLogDTO>> getTaskLogs(@PathVariable Integer taskId) {
        return ResponseEntity.ok(taskService.getTaskLogs(taskId));
    }
}