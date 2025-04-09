package com.example.projectdemo.domain.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 관리자 API 요청 처리 중 발생하는 예외를 처리하는 글로벌 예외 핸들러
 */
@RestControllerAdvice(basePackages = "com.example.projectdemo.domain.admin")
public class AdminExceptionHandler {

    /**
     * 일반적인 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "요청 처리 중 오류가 발생했습니다.");
        errorResponse.put("message", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    /**
     * 관리자 권한 부족 예외 처리
     */
    @ExceptionHandler(UnauthorizedAdminAccessException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedAccess(UnauthorizedAdminAccessException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "권한이 없습니다.");
        errorResponse.put("message", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }

    /**
     * 직원 정보 찾을 수 없음 예외 처리
     */
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEmployeeNotFound(EmployeeNotFoundException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "직원 정보를 찾을 수 없습니다.");
        errorResponse.put("message", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    /**
     * 유효하지 않은 예약 정보 예외 처리
     */
    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidBooking(InvalidBookingException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "유효하지 않은 예약 정보입니다.");
        errorResponse.put("message", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}

/**
 * 관리자 권한 관련 예외 클래스
 */
class UnauthorizedAdminAccessException extends RuntimeException {
    public UnauthorizedAdminAccessException(String message) {
        super(message);
    }
}

/**
 * 직원 정보 관련 예외 클래스
 */
class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}

/**
 * 예약 정보 관련 예외 클래스
 */
class InvalidBookingException extends RuntimeException {
    public InvalidBookingException(String message) {
        super(message);
    }
}