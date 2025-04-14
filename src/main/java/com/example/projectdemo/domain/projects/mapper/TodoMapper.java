package com.example.projectdemo.domain.projects.mapper;

import com.example.projectdemo.domain.projects.dto.TodoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TodoMapper {

    // 할 일 조회 관련 메서드
    List<TodoDTO> selectTodosByEmployee(@Param("empNum") String empNum);
    TodoDTO selectTodoById(@Param("id") Integer id);
    List<TodoDTO> selectTodosByDate(
            @Param("empNum") String empNum,
            @Param("date") LocalDate date);
    List<TodoDTO> selectTodosByPriority(
            @Param("empNum") String empNum,
            @Param("priority") String priority);
    List<TodoDTO> selectTodosByDateRange(
            @Param("empNum") String empNum,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    List<TodoDTO> selectOverdueTodos(@Param("empNum") String empNum);
    List<TodoDTO> selectCompletedTodos(@Param("empNum") String empNum);

    // 할 일 관리 관련 메서드
    int insertTodo(TodoDTO todo);
    int updateTodo(TodoDTO todo);
    int updateTodoCompletion(
            @Param("id") Integer id,
            @Param("completed") boolean completed);
    int deleteTodo(@Param("id") Integer id);

    // 통계 관련 메서드
    int countTodosByEmployee(@Param("empNum") String empNum);
    int countCompletedTodosByEmployee(@Param("empNum") String empNum);
    int countOverdueTodosByEmployee(@Param("empNum") String empNum);
}