package com.example.projectdemo.domain.projects.mapper;

import com.example.projectdemo.domain.projects.dto.TodoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TodoMapper {

    List<TodoDTO> selectTodosByEmployee(@Param("empNum") String empNum);

    TodoDTO selectTodoById(@Param("id") Integer id);

    void insertTodo(TodoDTO todo);

    void updateTodo(TodoDTO todo);

    void updateTodoCompletion(@Param("id") Integer id, @Param("completed") boolean completed);

    void deleteTodo(@Param("id") Integer id);

    List<TodoDTO> selectTodosByDate(@Param("empNum") String empNum, @Param("date") LocalDate date);

    List<TodoDTO> selectTodosByPriority(@Param("empNum") String empNum, @Param("priority") String priority);
}
