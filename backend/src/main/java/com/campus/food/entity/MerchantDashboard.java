package com.campus.food.entity;

import lombok.Data;

import java.util.List;

@Data
public class MerchantDashboard {

    private Integer todayOrderCount;

    private Double todaySales;

    private Integer pendingOrderCount;

    private List<OrderEntity> latestOrders;
}