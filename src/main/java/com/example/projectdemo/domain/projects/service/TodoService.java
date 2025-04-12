package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.TaskDTO;
import com.example.projectdemo.domain.projects.dto.TodoDTO;

import java.util.List;

import com.example.projectdemo.domain.projects.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    @Autowired
    private final TodoMapper todoMapper;



    public List<TodoDTO> getTodoListByEmployee(String empNum) {
        return todoMapper.selectTodosByEmployee(empNum);
    }

    public TodoDTO getTodoById(Integer id) {
        return todoMapper.selectTodoById(id);
    }

    public TodoDTO createTodo(TodoDTO todo) {
        todoMapper.insertTodo(todo);
        return todo;
    }

    public TodoDTO updateTodo(TodoDTO todo) {
        todoMapper.updateTodo(todo);
        return todo;
    }

    public TodoDTO toggleTodoCompletion(Integer id) {
        TodoDTO todo = todoMapper.selectTodoById(id);
        boolean completed = !todo.isCompleted();
        todoMapper.updateTodoCompletion(id, completed);
        todo.setCompleted(completed);
        return todo;
    }

    public void deleteTodo(Integer id) {
        todoMapper.deleteTodo(id);
    }

    public List<TodoDTO> getTodosByDate(String empNum, LocalDate date) {
        return todoMapper.selectTodosByDate(empNum, date);
    }

    public List<TodoDTO> getTodosByPriority(String empNum, String priority) {
        return todoMapper.selectTodosByPriority(empNum, priority);
    }
}
