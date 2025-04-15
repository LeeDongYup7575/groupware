package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.TodoDTO;
import com.example.projectdemo.domain.projects.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    @Autowired
    private final TodoMapper todoMapper;

    public List<TodoDTO> getTodoListByEmployee(String empNum) {
        List<TodoDTO> todos = todoMapper.selectTodosByEmployee(empNum);
        return processTodosMetadata(todos);
    }

    public TodoDTO getTodoById(Integer id) {
        TodoDTO todo = todoMapper.selectTodoById(id);
        return processTodoMetadata(todo);
    }

    public TodoDTO createTodo(TodoDTO todo) {
        todo.setCreatedAt(LocalDateTime.now());
        todoMapper.insertTodo(todo);
        return processTodoMetadata(todo);
    }

    public TodoDTO updateTodo(TodoDTO todo) {
        todo.setUpdatedAt(LocalDateTime.now());
        todoMapper.updateTodo(todo);
        return processTodoMetadata(todo);
    }

    public TodoDTO toggleTodoCompletion(Integer id) {
        TodoDTO todo = todoMapper.selectTodoById(id);
        boolean completed = !todo.isCompleted();

        todoMapper.updateTodoCompletion(id, completed);

        // 완료 상태로 변경된 경우 완료 시간 기록
        if (completed) {
            todo.setCompletedAt(LocalDateTime.now());
        } else {
            todo.setCompletedAt(null);
        }

        todo.setCompleted(completed);
        return processTodoMetadata(todo);
    }

    public void deleteTodo(Integer id) {
        todoMapper.deleteTodo(id);
    }

    public List<TodoDTO> getTodosByDate(String empNum, LocalDate date) {
        List<TodoDTO> todos = todoMapper.selectTodosByDate(empNum, date);
        return processTodosMetadata(todos);
    }

    public List<TodoDTO> getTodosByPriority(String empNum, String priority) {
        List<TodoDTO> todos = todoMapper.selectTodosByPriority(empNum, priority);
        return processTodosMetadata(todos);
    }

    /**
     * Todo 아이템의 메타데이터 처리 (남은 일수, 지연 여부 등)
     */
    public TodoDTO processTodoMetadata(TodoDTO todo) {
        if (todo != null && todo.getDueDate() != null) {
            LocalDate now = LocalDate.now();
            long daysUntilDue = ChronoUnit.DAYS.between(now, todo.getDueDate());
            todo.setRemainingDays((int) daysUntilDue);
            todo.setOverdue(daysUntilDue < 0 && !todo.isCompleted());
        }
        return todo;
    }

    /**
     * Todo 목록의 메타데이터 일괄 처리.
     */
    public List<TodoDTO> processTodosMetadata(List<TodoDTO> todos) {
        if (todos != null) {
            return todos.stream()
                    .map(this::processTodoMetadata)
                    .collect(Collectors.toList());
        }
        return todos;
    }
}