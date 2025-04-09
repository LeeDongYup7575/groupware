package com.example.projectdemo.config;

import com.example.projectdemo.domain.board.dto.BoardsDTO;
import com.example.projectdemo.domain.board.service.BoardsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


// config/SidebarInterceptor.java
// 사이드바에 데이터 전달하기 위한 인터셉터 추가
@Component
public class SidebarInterceptor implements HandlerInterceptor {

    @Autowired
    private BoardsService boardsService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null) {
            // 현재 로그인한 사용자 ID 가져오기
            Integer empId = (Integer) request.getAttribute("id");

            if (empId != null) {
                // 사용자가 접근 가능한 게시판 목록 가져오기
                List<BoardsDTO> accessibleBoards = boardsService.getAccessibleBoards(empId);
                // 모델에 추가
                modelAndView.addObject("accessibleBoards", accessibleBoards);
            }
        }
    }
}
