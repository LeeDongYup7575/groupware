package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.SubTaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskLogDTO;
import com.example.projectdemo.domain.projects.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskMapper taskMapper;

    /**
     * 모든 업무 목록을 조회합니다.
     */
    public List<TaskDTO> getAllTasks() {
        List<TaskDTO> tasks = taskMapper.getAllTasks();
        return processTasksMetadata(tasks);
    }

    /**
     * 특정 업무를 ID로 조회합니다.
     */
    public TaskDTO getTaskById(Integer id) {
        TaskDTO task = taskMapper.getTaskById(id);
        if (task != null) {
            processTaskMetadata(task);
            // 하위 업무도 함께 조회
            task.setSubTasks(taskMapper.getSubTasksByTask(id));
        }
        return task;
    }

    /**
     * 프로젝트에 속한 모든 업무를 조회합니다.
     */
    public List<TaskDTO> getTasksByProject(Integer projectId) {
        List<TaskDTO> tasks = taskMapper.getTasksByProject(projectId);
        return processTasksMetadata(tasks);
    }

    /**
     * 특정 사원이 담당하는 업무 목록을 조회합니다.
     */
    public List<TaskDTO> getTasksByAssignee(String empNum) {
        List<TaskDTO> tasks = taskMapper.getTasksByAssignee(empNum);
        return processTasksMetadata(tasks);
    }

    /**
     * 특정 사원이 생성한 업무 목록을 조회합니다.
     */
    public List<TaskDTO> getTasksByCreator(String empNum) {
        List<TaskDTO> tasks = taskMapper.getTasksByCreator(empNum);
        return processTasksMetadata(tasks);
    }

    /**
     * 최근 업무 목록을 조회합니다.
     */
    public List<TaskDTO> getRecentTasks(int limit) {
        List<TaskDTO> tasks = taskMapper.getRecentTasks(limit);
        return processTasksMetadata(tasks);
    }

    /**
     * 특정 사원의 최근 업무 목록을 조회합니다.
     */
    public List<TaskDTO> getRecentTasksByEmpNum(String empNum, int limit) {
        List<TaskDTO> tasks = taskMapper.getRecentTasksByEmpNum(empNum, limit);
        return processTasksMetadata(tasks);
    }

    /**
     * 새 업무를 등록합니다.
     */
    @Transactional
    public TaskDTO createTask(TaskDTO task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setStatus(task.getStatus() == null ? "미시작" : task.getStatus());
        task.setProgress(task.getProgress() == null ? 0 : task.getProgress());

        if (taskMapper.insertTask(task) > 0) {
            // 업무 로그 생성
            createTaskLog(task.getId(), task.getReporterEmpNum(), "업무 생성", null, task.getTitle(), task.getDescription());
            return getTaskById(task.getId());
        }

        throw new RuntimeException("업무를 등록할 수 없습니다.");
    }

    /**
     * 업무 정보를 수정합니다.
     */
    @Transactional
    public TaskDTO updateTask(TaskDTO task) {
        TaskDTO oldTask = taskMapper.getTaskById(task.getId());

        if (oldTask == null) {
            throw new RuntimeException("존재하지 않는 업무입니다.");
        }

        task.setUpdatedAt(LocalDateTime.now());

        // 완료 상태로 변경되면 완료일시 설정
        if ("완료".equals(task.getStatus()) && !"완료".equals(oldTask.getStatus())) {
            task.setCompletedDate(LocalDate.now()); // completedAt -> completedDate로 변경
        }

        if (taskMapper.updateTask(task) > 0) {
            // 업무 로그 생성
            createTaskLog(task.getId(), task.getAssigneeEmpNum(), "업무 수정",
                    oldTask.getStatus(), task.getStatus(), "업무 정보가 수정되었습니다.");

            return getTaskById(task.getId());
        }

        throw new RuntimeException("업무를 수정할 수 없습니다.");
    }

    /**
     * 업무 상태를 변경합니다.
     */
    @Transactional
    public TaskDTO updateTaskStatus(Integer taskId, String status, String empNum) {
        TaskDTO task = taskMapper.getTaskById(taskId);

        if (task == null) {
            throw new RuntimeException("존재하지 않는 업무입니다.");
        }

        String oldStatus = task.getStatus();

        if (taskMapper.updateTaskStatus(taskId, status) > 0) {
            // 업무 로그 생성
            createTaskLog(taskId, empNum, "상태 변경", oldStatus, status, null);

            return getTaskById(taskId);
        }

        throw new RuntimeException("업무 상태를 변경할 수 없습니다.");
    }

    /**
     * 업무 진행률을 업데이트합니다.
     */
    @Transactional
    public TaskDTO updateTaskProgress(Integer taskId, Integer progress, String empNum) {
        try {
            TaskDTO task = taskMapper.getTaskById(taskId);

            if (task == null) {
                throw new RuntimeException("존재하지 않는 업무입니다.");
            }

            Integer oldProgress = task.getProgress();
            System.out.println("Updating task progress: taskId=" + taskId + ", oldProgress=" + oldProgress + ", newProgress=" + progress); // 로깅 추가

            // 업무 진행률 업데이트
            int updateResult = taskMapper.updateTaskProgress(taskId, progress);
            System.out.println("Update result: " + updateResult); // 업데이트 결과 로깅

            // 업무 로그 생성
            TaskLogDTO log = new TaskLogDTO();
            log.setTaskId(taskId);
            log.setEmpNum(empNum);
            log.setLogType("진행률 변경");
            log.setOldValue(oldProgress + "%");
            log.setNewValue(progress + "%");
            log.setCreatedAt(LocalDateTime.now());
            taskMapper.insertTaskLog(log);

            return getTaskById(taskId);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 스택 트레이스 출력
            throw e;
        }
    }

    /**
     * 업무를 삭제합니다.
     */
    @Transactional
    public void deleteTask(Integer taskId, String empNum) {
        TaskDTO task = taskMapper.getTaskById(taskId);

        if (task == null) {
            throw new RuntimeException("존재하지 않는 업무입니다.");
        }

        // 하위 업무가 있으면 먼저 삭제
        List<SubTaskDTO> subTasks = taskMapper.getSubTasksByTask(taskId);
        if (subTasks != null && !subTasks.isEmpty()) {
            for (SubTaskDTO subTask : subTasks) {
                taskMapper.deleteSubTask(subTask.getId());
            }
        }

        if (taskMapper.deleteTask(taskId) <= 0) {
            throw new RuntimeException("업무를 삭제할 수 없습니다.");
        }

        // 업무가 삭제되었으므로 로그는 따로 기록하지 않음
    }

    /**
     * 하위 업무를 등록합니다.
     */
    @Transactional
    public SubTaskDTO createSubTask(SubTaskDTO subTask) {
        subTask.setCreatedAt(LocalDateTime.now());

        if (taskMapper.insertSubTask(subTask) > 0) {
            // 부모 업무 조회
            TaskDTO parentTask = taskMapper.getTaskById(subTask.getTaskId());

            // 업무 로그 생성
            createTaskLog(subTask.getTaskId(), parentTask.getAssigneeEmpNum(),
                    "하위 업무 추가", null, subTask.getTitle(), subTask.getDescription());

            return subTask;
        }

        throw new RuntimeException("하위 업무를 등록할 수 없습니다.");
    }

    /**
     * 하위 업무를 수정합니다.
     */
    @Transactional
    public SubTaskDTO updateSubTask(SubTaskDTO subTask) {
        subTask.setUpdatedAt(LocalDateTime.now());

        // 완료 상태로 변경되면 완료일시 설정
        if (subTask.getStatus().equals("완료")) {
            subTask.setCompletedAt(LocalDateTime.now());
        }

        if (taskMapper.updateSubTask(subTask) > 0) {
            // 부모 업무 조회
            TaskDTO parentTask = taskMapper.getTaskById(subTask.getTaskId());

            // 업무 로그 생성
            createTaskLog(subTask.getTaskId(), parentTask.getAssigneeEmpNum(),
                    "하위 업무 수정", null, subTask.getTitle(), "하위 업무가 수정되었습니다.");

            return subTask;
        }

        throw new RuntimeException("하위 업무를 수정할 수 없습니다.");
    }

    /**
     * 하위 업무를 삭제합니다.
     */
    @Transactional
    public void deleteSubTask(Integer subTaskId) {
        // 먼저 하위 업무 정보를 조회
        SubTaskDTO subTask = taskMapper.getSubTaskById(subTaskId);

        if (subTask == null) {
            throw new RuntimeException("존재하지 않는 하위 업무입니다.");
        }

        // 부모 업무 조회
        TaskDTO parentTask = taskMapper.getTaskById(subTask.getTaskId());

        if (taskMapper.deleteSubTask(subTaskId) <= 0) {
            throw new RuntimeException("하위 업무를 삭제할 수 없습니다.");
        }

        // 업무 로그 생성
        createTaskLog(subTask.getTaskId(), parentTask.getAssigneeEmpNum(),
                "하위 업무 삭제", null, subTask.getTitle(), "하위 업무가 삭제되었습니다.");
    }

    /**
     * 하위 업무의 상태를 변경합니다.
     */
    @Transactional
    public void updateSubTaskStatus(Integer subTaskId, String status) {
        // 먼저 하위 업무 정보를 조회
        SubTaskDTO subTask = taskMapper.getSubTaskById(subTaskId);

        if (subTask == null) {
            throw new RuntimeException("존재하지 않는 하위 업무입니다.");
        }

        String oldStatus = subTask.getStatus();

        if (taskMapper.updateSubTaskStatus(subTaskId, status) <= 0) {
            throw new RuntimeException("하위 업무 상태를 변경할 수 없습니다.");
        }

        // 부모 업무 조회
        TaskDTO parentTask = taskMapper.getTaskById(subTask.getTaskId());

        // 업무 로그 생성
        createTaskLog(subTask.getTaskId(), parentTask.getAssigneeEmpNum(),
                "하위 업무 상태 변경", oldStatus, status,
                subTask.getTitle() + " 업무가 " + status + " 상태로 변경되었습니다.");

        // 부모 업무의 진행률을 업데이트
        updateParentTaskProgress(subTask.getTaskId());
    }

    /**
     * 하위 업무 상태에 따라 부모 업무의 진행률을 업데이트합니다.
     */
    private void updateParentTaskProgress(Integer taskId) {
        List<SubTaskDTO> subTasks = taskMapper.getSubTasksByTask(taskId);

        if (subTasks == null || subTasks.isEmpty()) {
            return;
        }

        int totalCount = subTasks.size();
        int completedCount = 0;

        for (SubTaskDTO subTask : subTasks) {
            if (subTask.getStatus().equals("완료")) {
                completedCount++;
            }
        }

        // 진행률 계산 (모든 하위 업무가 같은 가중치를 가진다고 가정)
        int progress = totalCount > 0 ? (completedCount * 100) / totalCount : 0;

        // 업무 진행률 업데이트
        taskMapper.updateTaskProgress(taskId, progress);
    }

    /**
     * 업무 메타데이터를 처리합니다. (남은 일수, 지연 여부 등)
     */
    public TaskDTO processTaskMetadata(TaskDTO task) {
        if (task != null) {
            // 남은 일수 계산
            if (task.getDueDate() != null) {
                LocalDate now = LocalDate.now();
                long daysUntilDue = ChronoUnit.DAYS.between(now, task.getDueDate());
                task.setRemainingDays((int) daysUntilDue);
                task.setOverdue(daysUntilDue < 0 && !"완료".equals(task.getStatus()));
            }

            // 현재 진행도에 따라 상태 자동 업데이트
            if (task.getProgress() != null) {
                if (task.getProgress() == 0 && !"미시작".equals(task.getStatus())) {
                    task.setStatus("미시작");
                } else if (task.getProgress() == 100 && !"완료".equals(task.getStatus())) {
                    task.setStatus("완료");
                    task.setCompletedDate(LocalDate.now());
                } else if (task.getProgress() > 0 && task.getProgress() < 100 && !"진행중".equals(task.getStatus())) {
                    task.setStatus("진행중");
                }
            }
        }
        return task;
    }

    /**
     * 업무 목록의 메타데이터를 일괄 처리합니다.
     */
    public List<TaskDTO> processTasksMetadata(List<TaskDTO> tasks) {
        if (tasks != null) {
            return tasks.stream()
                    .map(this::processTaskMetadata)
                    .collect(Collectors.toList());
        }
        return tasks;
    }

    /**
     * 업무 로그를 생성합니다.
     */
    private void createTaskLog(Integer taskId, String empNum, String logType,
                               Object oldValue, Object newValue, String comment) {
        TaskDTO task = taskMapper.getTaskById(taskId);
        if (task == null) {
            return; // 업무가 없으면 로그를 생성하지 않음
        }

        TaskLogDTO log = new TaskLogDTO();
        log.setTaskId(taskId);
        log.setEmpNum(empNum);
        log.setLogType(logType);
        log.setOldValue(oldValue != null ? oldValue.toString() : null);
        log.setNewValue(newValue != null ? newValue.toString() : null);
        log.setComment(comment);
        log.setCreatedAt(LocalDateTime.now());

        taskMapper.insertTaskLog(log);
    }

    /**
     * 특정 업무의 로그를 조회합니다.
     */
    public List<TaskLogDTO> getTaskLogs(Integer taskId) {
        return taskMapper.getTaskLogs(taskId);
    }
}