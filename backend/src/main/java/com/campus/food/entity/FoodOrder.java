package com.campus.food.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FoodOrder {

    private Long id;

    private Long userId;

    private Long stallId;

    private Double totalAmount;

    private String status;

    private String deliveryType;

    private Long addressId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}