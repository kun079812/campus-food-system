package com.campus.food.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Review {

    private Long id;

    private Long orderId;

    private Long userId;

    private Integer rating;

    private String content;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}