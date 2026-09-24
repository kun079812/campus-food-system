package com.campus.food.common;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(
            ResponseStatusException exception
    ) {
        int code = exception.getStatusCode().value();
        String message = exception.getReason();

        if (message == null || message.isBlank()) {
            message = "请求处理失败";
        }

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(ApiResponse.failure(code, message));
    }

    @ExceptionHandler({
            DataAccessException.class,
            PersistenceException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleDatabaseException(
            Exception exception
    ) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "数据库操作失败，请稍后重试"
                ));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleRequestException(
            Exception exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(
                        HttpStatus.BAD_REQUEST.value(),
                        "请求参数格式错误"
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
            RuntimeException exception
    ) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = "请求处理失败";
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(
                        HttpStatus.BAD_REQUEST.value(),
                        message
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception exception
    ) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "服务器内部错误"
                ));
    }
}