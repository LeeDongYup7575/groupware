package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.TaskLogDTO;
import com.example.projectdemo.domain.projects.mapper.TaskLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskLogService {

    @Autowired
    private TaskLogMapper taskLogMapper;

    /**
     * 최근 업무 로그를 조회합니다.
     */
    public List<TaskLogDTO> getRecentTaskLogs(int limit) {
        return taskLogMapper.getRecentTaskLogs(limit);
    }

    /**
     * 특정 사원의 업무 로그를 조회합니다.
     */
    public List<TaskLogDTO> getTaskLogsByEmployee(String empNum, int limit) {
        return taskLogMapper.getTaskLogsByEmpNum(empNum, limit);
    }

    /**
     * 특정 프로젝트의 업무 로그를 조회합니다.
     */
    public List<TaskLogDTO> getTaskLogsByProject(Integer projectId, int limit) {
        return taskLogMapper.getTaskLogsByProjectId(projectId, limit);
    }

    /**
     * 특정 업무의 로그를 조회합니다.
     */
    public List<TaskLogDTO> getTaskLogsByTask(Integer taskId) {
        return taskLogMapper.getTaskLogsByTaskId(taskId);
    }

    /**
     * 특정 기간 내의 업무 로그를 조회합니다.
     */
    public List<TaskLogDTO> getTaskLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return taskLogMapper.getTaskLogsByDateRange(startDate, endDate);
    }

    /**
     * 업무 로그를 생성합니다.
     */
    public void createTaskLog(TaskLogDTO taskLog) {
        if (taskLog.getCreatedAt() == null) {
            taskLog.setCreatedAt(LocalDateTime.now());
        }
        taskLogMapper.insertTaskLog(taskLog);
    }
}