package com.campus.food.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveryAddress {

    private Long id;

    private Long studentId;

    private String contactName;

    private String phone;

    private String detail;

    private Integer isDefault;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}