package com.campus.food.entity;

import lombok.Data;

@Data
public class Dish {

    private Long id;

    private Long stallId;

    private Long categoryId;

    private String name;

    private Double price;

    private Integer stock;

    private String description;

    private String imageUrl;

    private Integer status;
}