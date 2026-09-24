package com.campus.food.common;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {

    private Integer code;

    private String message;

    private LocalDateTime timestamp;

    public ErrorResponse(
            Integer code,
            String message
    ) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}