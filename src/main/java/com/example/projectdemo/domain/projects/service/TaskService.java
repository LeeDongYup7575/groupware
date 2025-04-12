package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.SubTaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskLogDTO;
import com.example.projectdemo.domain.projects.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    @Autowired
    private final TaskMapper taskMapper;

    public List<TaskDTO> getTasksByProject(Integer projectId) {
        return taskMapper.getTasksByProject(projectId);
    }

    public List<TaskDTO> getTasksByAssignee(String empNum) {
        return taskMapper.getTasksByAssignee(empNum);
    }

    public List<TaskDTO> getTasksByReporter(String empNum) {
        return taskMapper.getTasksByCreator(empNum);
    }

    public List<TaskDTO> getTasksByStatus(Integer projectId, String status) {
        return taskMapper.getTasksByStatus(projectId, status);
    }

    public TaskDTO getTaskById(Integer id) {
        return taskMapper.getTaskById(id);
    }

    public TaskDTO createTask(TaskDTO task) {
        taskMapper.insertTask(task);
        return task;
    }

    public TaskDTO updateTask(TaskDTO task) {
        taskMapper.updateTask(task);
        return task;
    }

    public TaskDTO updateTaskStatus(Integer id, String status) {
        taskMapper.updateTaskStatus(id, status);
        return taskMapper.getTaskById(id);
    }

    public TaskDTO updateTaskProgress(Integer id, Integer progress) {
        taskMapper.updateTaskProgress(id, progress);
        return taskMapper.getTaskById(id);
    }

    public TaskDTO completeTask(Integer id) {
        taskMapper.updateTaskStatus(id, "완료");
        return taskMapper.getTaskById(id);
    }

    public List<TaskLogDTO> getTaskLogs(Integer taskId) {
        return taskMapper.getTaskLogs(taskId);
    }

    public TaskLogDTO addTaskLog(TaskLogDTO log) {
        taskMapper.insertTaskLog(log);
        return log;
    }

    public List<SubTaskDTO> getSubTasksByTask(Integer taskId) {
        return taskMapper.getSubTasksByTask(taskId);
    }

    public SubTaskDTO getSubTaskById(Integer id) {
        return taskMapper.getSubTaskById(id);
    }

    public SubTaskDTO addSubTask(SubTaskDTO subTask) {
        taskMapper.insertSubTask(subTask);
        return subTask;
    }

    public SubTaskDTO updateSubTask(SubTaskDTO subTask) {
        taskMapper.updateSubTask(subTask);
        return subTask;
    }

    public SubTaskDTO updateSubTaskStatus(Integer id, String status) {
        taskMapper.updateSubTaskStatus(id, status);
        return taskMapper.getSubTaskById(id);
    }

    public SubTaskDTO completeSubTask(Integer id) {
        taskMapper.updateSubTaskStatus(id, "완료");
        return taskMapper.getSubTaskById(id);
    }
}
