package com.example.projectdemo.domain.projects.mapper;

import com.example.projectdemo.domain.projects.dto.TaskLogDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskLogMapper {

    /**
     * 최근 업무 로그 조회
     */
    List<TaskLogDTO> getRecentTaskLogs(int limit);

    /**
     * 특정 사원의 업무 로그 조회
     */
    List<TaskLogDTO> getTaskLogsByEmpNum(@Param("empNum") String empNum, @Param("limit") int limit);

    /**
     * 특정 프로젝트의 업무 로그 조회
     */
    List<TaskLogDTO> getTaskLogsByProjectId(@Param("projectId") Integer projectId, @Param("limit") int limit);

    /**
     * 특정 업무의 로그 조회
     */
    List<TaskLogDTO> getTaskLogsByTaskId(Integer taskId);

    /**
     * 특정 기간 내의 업무 로그 조회
     */
    List<TaskLogDTO> getTaskLogsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 업무 로그 추가
     */
    int insertTaskLog(TaskLogDTO taskLog);
}