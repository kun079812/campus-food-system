package com.campus.food.mapper;

import com.campus.food.entity.OrderStatusLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface OrderStatusLogMapper {


    @Insert("""
            INSERT INTO order_status_log
            (order_id, old_status, new_status)
            VALUES
            (#{orderId}, #{oldStatus}, #{newStatus})
            """)
    int insert(OrderStatusLog log);

}