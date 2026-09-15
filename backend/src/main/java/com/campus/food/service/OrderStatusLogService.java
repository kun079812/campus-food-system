package com.campus.food.service;

import com.campus.food.entity.OrderStatusLog;
import com.campus.food.mapper.OrderStatusLogMapper;
import org.springframework.stereotype.Service;


@Service
public class OrderStatusLogService {


    private final OrderStatusLogMapper orderStatusLogMapper;


    public OrderStatusLogService(OrderStatusLogMapper orderStatusLogMapper) {
        this.orderStatusLogMapper = orderStatusLogMapper;
    }


    public int insert(OrderStatusLog log) {
        return orderStatusLogMapper.insert(log);
    }

}