package com.campus.food.service;

import com.campus.food.entity.OrderItem;
import com.campus.food.mapper.OrderItemMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {


    private final OrderItemMapper orderItemMapper;


    public OrderItemService(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }


    public List<OrderItem> findByOrderId(Long orderId) {

        return orderItemMapper.findByOrderId(orderId);

    }

}