package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.TodoDTO;

import java.util.List;

public interface TodoService {

    /**
     * 직원별 할 일 목록 조회
     */
    List<TodoDTO> getTodoListByEmployee(String empNum);

    /**
     * 할 일 상세 조회
     */
    TodoDTO getTodoById(Integer id);

    /**
     * 신규 할 일 추가
     */
    TodoDTO createTodo(TodoDTO todo);

    /**
     * 할 일 정보 업데이트
     */
    TodoDTO updateTodo(TodoDTO todo);

    /**
     * 할 일 완료 상태 토글
     */
    TodoDTO toggleTodoCompletion(Integer id);

    /**
     * 할 일 삭제
     */
    void deleteTodo(Integer id);

    /**
     * 특정 날짜의 할 일 목록 조회
     */
    List<TodoDTO> getTodosByDate(String empNum, java.time.LocalDate date);

    /**
     * 우선순위별 할 일 목록 조회
     */
    List<TodoDTO> getTodosByPriority(String empNum, String priority);
}