package com.campus.food.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderEntity {

    private Long id;

    private String orderNo;

    private Long userId;

    private Long stallId;

    private Double totalAmount;

    private Double payAmount;

    private String status;

    private String deliveryType;

    private Long addressId;

    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
