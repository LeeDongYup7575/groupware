package com.example.projectdemo.domain.projects.mapper;

import com.example.projectdemo.domain.projects.dto.TaskLogDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskLogMapper {

    List<TaskLogDTO> getRecentTaskLogs(int limit);

    List<TaskLogDTO> getTaskLogsByEmpNum(@Param("empNum") String empNum, @Param("limit") int limit);

    List<TaskLogDTO> getTaskLogsByProjectId(@Param("projectId") Integer projectId, @Param("limit") int limit);

    List<TaskLogDTO> getTaskLogsByTaskId(Integer taskId);

    List<TaskLogDTO> getTaskLogsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    int insertTaskLog(TaskLogDTO taskLog);
}