package com.example.projectdemo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbiddenException(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handle403(AccessDeniedException e, Model model) {
        model.addAttribute("statusCode", "403");
        model.addAttribute("message", "접근 권한이 없습니다.");
        return "error/error";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handle405(HttpRequestMethodNotSupportedException e, Model model) {
        model.addAttribute("statusCode", "405");
        model.addAttribute("message", "허용되지 않은 요청 방식입니다.");
        return "error/error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(NoHandlerFoundException e, Model model) {
        model.addAttribute("statusCode", "404");
        model.addAttribute("message", "요청한 페이지가 존재하지 않습니다.");
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    public String handle500(Exception e, Model model) {
        model.addAttribute("statusCode", "500");
        model.addAttribute("message", "서버 내부 오류가 발생했습니다. 관리자에게 문의해주세요.");
        return "error/error";
    }

    @ExceptionHandler(AuthenticationException.class)
    public String handle401(AuthenticationException e, Model model) {
        model.addAttribute("statusCode", "401");
        model.addAttribute("message", "로그인이 필요한 요청입니다.");
        return "error/error";
    }

}