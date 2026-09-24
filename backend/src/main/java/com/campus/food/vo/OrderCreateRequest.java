package com.campus.food.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {

    private List<Long> itemIds;

    // The request field name follows the design document. Values: PICKUP or DELIVERY.
    private String pickupType;

    private Long addressId;

    private String remark;
}