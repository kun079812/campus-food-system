package com.campus.food.vo;

import com.campus.food.entity.OrderStatusLog;
import com.campus.food.entity.DeliveryAddress;
import com.campus.food.entity.OrderEntity;
import com.campus.food.entity.OrderItem;
import lombok.Data;

import java.util.List;

@Data
public class OrderDetailVO {

    private OrderEntity order;

    private List<OrderStatusLog> statusLogs;

    private List<OrderItem> orderItems;

    private DeliveryAddress address;
}