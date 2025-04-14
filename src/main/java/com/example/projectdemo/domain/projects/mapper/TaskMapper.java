package com.example.projectdemo.domain.projects.mapper;

import com.example.projectdemo.domain.projects.dto.SubTaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskLogDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TaskMapper {

    // 업무 관련 메서드
    List<TaskDTO> getAllTasks();
    TaskDTO getTaskById(@Param("taskId") Integer id);
    List<TaskDTO> getTasksByProject(@Param("projectId") Integer projectId);
    List<TaskDTO> getTasksByAssignee(@Param("empNum") String empNum);
    List<TaskDTO> getTasksByCreator(@Param("empNum") String empNum);
    List<TaskDTO> getRecentTasks(@Param("limit") int limit);
    List<TaskDTO> getRecentTasksByEmpNum(@Param("empNum") String empNum, @Param("limit") int limit);
    List<TaskDTO> getTasksByStatus(@Param("projectId") Integer projectId, @Param("status") String status);
    List<TaskDTO> getTasksByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    List<TaskDTO> getTasksByPriority(@Param("priority") String priority);
    List<TaskDTO> getOverdueTasks(); // 기한이 지난 업무

    int insertTask(TaskDTO task);
    int updateTask(TaskDTO task);
    int updateTaskStatus(@Param("taskId") Integer taskId, @Param("status") String status);
    int updateTaskProgress(@Param("taskId") Integer taskId, @Param("progress") Integer progress);
    int deleteTask(@Param("taskId") Integer taskId);

    // 하위 업무 관련 메서드
    List<SubTaskDTO> getSubTasksByTask(@Param("taskId") Integer taskId);
    SubTaskDTO getSubTaskById(@Param("subTaskId") Integer subTaskId);
    int insertSubTask(SubTaskDTO subTask);
    int updateSubTask(SubTaskDTO subTask);
    int deleteSubTask(@Param("subTaskId") Integer subTaskId);
    int updateSubTaskStatus(@Param("subTaskId") Integer subTaskId, @Param("status") String status);

    // 업무 로그 관련 메서드
    List<TaskLogDTO> getTaskLogs(@Param("taskId") Integer taskId);
    int insertTaskLog(TaskLogDTO log);

    // 통계 관련 메서드
    int countTasksByStatus(@Param("status") String status);
    int countTasksByPriority(@Param("priority") String priority);
    int countTasksByEmployee(@Param("empNum") String empNum);
    int countOverdueTasks();
}