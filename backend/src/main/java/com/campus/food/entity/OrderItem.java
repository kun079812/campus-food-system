package com.campus.food.entity;

import lombok.Data;

@Data
public class OrderItem {

    private Long id;

    private Long orderId;

    private Long dishId;

    private Integer quantity;

    private Double price;

    private String dishName;

}
