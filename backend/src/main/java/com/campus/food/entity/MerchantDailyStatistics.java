package com.campus.food.entity;

import lombok.Data;

@Data
public class MerchantDailyStatistics {

    private String statDate;

    private Integer orderCount;

    private Double salesAmount;
}