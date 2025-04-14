package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.TaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskStatisticsService {

    @Autowired
    private TaskService taskService;

    /**
     * 프로젝트의 업무 통계를 계산합니다.
     */
    public TaskSummaryDTO getProjectTaskSummary(Integer projectId) {
        List<TaskDTO> tasks = taskService.getTasksByProject(projectId);
        return calculateTaskSummary(tasks);
    }

    /**
     * 특정 사원의 업무 통계를 계산합니다.
     */
    public TaskSummaryDTO getEmployeeTaskSummary(String empNum) {
        List<TaskDTO> tasks = taskService.getTasksByAssignee(empNum);
        return calculateTaskSummary(tasks);
    }

    /**
     * 전체 업무 통계를 계산합니다.
     */
    public TaskSummaryDTO getOverallTaskSummary() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return calculateTaskSummary(tasks);
    }

    /**
     * 업무 통계를 계산합니다.
     */
    private TaskSummaryDTO calculateTaskSummary(List<TaskDTO> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return new TaskSummaryDTO(0, 0, 0, 0, 0, 0.0, 0, 0, 0);
        }

        int totalTasks = tasks.size();

        // 상태별 업무 분류
        Map<String, Long> tasksByStatus = tasks.stream()
                .collect(Collectors.groupingBy(TaskDTO::getStatus, Collectors.counting()));

        int notStartedTasks = tasksByStatus.getOrDefault("미시작", 0L).intValue();
        int inProgressTasks = tasksByStatus.getOrDefault("진행중", 0L).intValue();
        int completedTasks = tasksByStatus.getOrDefault("완료", 0L).intValue();

        // 지연된 업무 계산
        int delayedTasks = (int) tasks.stream()
                .filter(task -> task.getDueDate() != null &&
                        task.getDueDate().isBefore(LocalDate.now()) &&
                        !"완료".equals(task.getStatus()))
                .count();

        // 완료율 계산
        double completionRate = totalTasks > 0 ? ((double) completedTasks / totalTasks) * 100 : 0;

        // 우선순위별 업무 분류
        Map<String, Long> tasksByPriority = tasks.stream()
                .collect(Collectors.groupingBy(TaskDTO::getPriority, Collectors.counting()));

        int highPriorityTasks = tasksByPriority.getOrDefault("높음", 0L).intValue();
        int mediumPriorityTasks = tasksByPriority.getOrDefault("중간", 0L).intValue();
        int lowPriorityTasks = tasksByPriority.getOrDefault("낮음", 0L).intValue();

        return TaskSummaryDTO.builder()
                .totalTasks(totalTasks)
                .notStartedTasks(notStartedTasks)
                .inProgressTasks(inProgressTasks)
                .completedTasks(completedTasks)
                .delayedTasks(delayedTasks)
                .completionRate(Math.round(completionRate * 100) / 100.0) // 소수점 둘째 자리까지 반올림
                .highPriorityTasks(highPriorityTasks)
                .mediumPriorityTasks(mediumPriorityTasks)
                .lowPriorityTasks(lowPriorityTasks)
                .build();
    }
}