package com.campus.food.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Stall {

    private Long id;

    private Long canteenId;

    private Long merchantId;

    private String name;

    private String businessHours;

    private Integer status;

    // Response-only field for the canteen stall list; it is not a database column.
    private Integer dishCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}