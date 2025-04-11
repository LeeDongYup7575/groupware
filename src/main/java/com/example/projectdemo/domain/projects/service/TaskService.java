package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.SubTaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskLogDTO;

import java.util.List;

public interface TaskService {

    /**
     * 프로젝트별 업무 목록 조회
     */
    List<TaskDTO> getTasksByProject(Integer projectId);

    /**
     * 담당자별 업무 목록 조회
     */
    List<TaskDTO> getTasksByAssignee(String empNum);

    /**
     * 보고자별 업무 목록 조회
     */
    List<TaskDTO> getTasksByReporter(String empNum);

    /**
     * 상태별 업무 목록 조회
     */
    List<TaskDTO> getTasksByStatus(Integer projectId, String status);

    /**
     * 업무 상세 조회
     */
    TaskDTO getTaskById(Integer id);

    /**
     * 신규 업무 등록
     */
    TaskDTO createTask(TaskDTO task);

    /**
     * 업무 정보 업데이트
     */
    TaskDTO updateTask(TaskDTO task);

    /**
     * 업무 상태 업데이트
     */
    TaskDTO updateTaskStatus(Integer id, String status);

    /**
     * 업무 진행률 업데이트
     */
    TaskDTO updateTaskProgress(Integer id, Integer progress);

    /**
     * 업무 완료 처리
     */
    TaskDTO completeTask(Integer id);

    /**
     * 업무 로그 조회
     */
    List<TaskLogDTO> getTaskLogs(Integer taskId);

    /**
     * 업무 로그 추가
     */
    TaskLogDTO addTaskLog(TaskLogDTO log);

    /**
     * 하위 업무 목록 조회
     */
    List<SubTaskDTO> getSubTasksByTask(Integer taskId);

    /**
     * 하위 업무 상세 조회
     */
    SubTaskDTO getSubTaskById(Integer id);

    /**
     * 하위 업무 추가
     */
    SubTaskDTO addSubTask(SubTaskDTO subTask);

    /**
     * 하위 업무 업데이트
     */
    SubTaskDTO updateSubTask(SubTaskDTO subTask);

    /**
     * 하위 업무 상태 업데이트
     */
    SubTaskDTO updateSubTaskStatus(Integer id, String status);

    /**
     * 하위 업무 완료 처리
     */
    SubTaskDTO completeSubTask(Integer id);
}