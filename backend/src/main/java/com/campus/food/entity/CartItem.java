package com.campus.food.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CartItem {

    private Long id;

    private Long userId;

    private Long dishId;

    private Integer quantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 以下字段不存 cart_item 表，只用于返回购物车详情
    private String dishName;

    private Double dishPrice;

    private String dishImageUrl;

    private Integer dishStock;

    private Integer dishStatus;

    private Double subtotal;
}