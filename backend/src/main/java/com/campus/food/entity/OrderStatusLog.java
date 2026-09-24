package com.campus.food.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStatusLog {

    private Long id;

    private Long orderId;

    private String oldStatus;

    private String newStatus;

    private LocalDateTime createdAt;
}
