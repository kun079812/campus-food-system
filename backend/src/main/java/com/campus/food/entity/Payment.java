package com.campus.food.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Payment {

    private Long id;

    private String paymentNo;

    private Long orderId;

    private Long userId;

    private Double payAmount;

    private String payMethod;

    private String payStatus;

    private LocalDateTime paidAt;

    private LocalDateTime createdAt;
}