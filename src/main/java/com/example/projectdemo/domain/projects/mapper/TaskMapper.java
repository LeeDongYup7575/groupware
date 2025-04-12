package com.example.projectdemo.domain.projects.mapper;

import com.example.projectdemo.domain.projects.dto.SubTaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskLogDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskMapper {

    List<TaskDTO> getAllTasks();

    TaskDTO getTaskById(Integer taskId);

    List<TaskDTO> getTasksByProject(Integer projectId);

    List<TaskDTO> getTasksByAssignee(@Param("empNum") String empNum);

    List<TaskDTO> getTasksByCreator(String empNum);

    List<TaskDTO> getRecentTasks(int limit);

    List<TaskDTO> getRecentTasksByEmpNum(@Param("empNum") String empNum, @Param("limit") int limit);

    List<TaskDTO> getTasksByStatus(@Param("projectId") Integer projectId, @Param("status") String status);

    int insertTask(TaskDTO task);

    int updateTask(TaskDTO task);

    int updateTaskStatus(@Param("taskId") Integer taskId, @Param("status") String status);

    int updateTaskProgress(@Param("taskId") Integer taskId, @Param("progress") Integer progress);

    int deleteTask(Integer taskId);

    // 하위 업무
    List<SubTaskDTO> getSubTasksByTask(Integer taskId);

    SubTaskDTO getSubTaskById(Integer subTaskId);

    int insertSubTask(SubTaskDTO subTask);

    int updateSubTask(SubTaskDTO subTask);

    int updateSubTaskStatus(@Param("subTaskId") Integer subTaskId, @Param("status") String status);

    int deleteSubTask(Integer subTaskId);

    // 업무 로그
    List<TaskLogDTO> getTaskLogs(Integer taskId);

    int insertTaskLog(TaskLogDTO log);
}
