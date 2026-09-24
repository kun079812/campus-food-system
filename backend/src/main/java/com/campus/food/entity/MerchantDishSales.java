package com.campus.food.entity;

import lombok.Data;

@Data
public class MerchantDishSales {

    private Long dishId;

    private String dishName;

    private Integer salesQuantity;

    private Double salesAmount;
}