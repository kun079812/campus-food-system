package com.campus.food.mapper;

import com.campus.food.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper {


    @Select("""
        SELECT
            oi.id,
            oi.order_id,
            oi.dish_id,
            d.name AS dish_name,
            oi.quantity,
            oi.price
        FROM order_item oi
        JOIN dish d
        ON oi.dish_id = d.id
        WHERE oi.order_id = #{orderId}
        """)
    List<OrderItem> findByOrderId(Long orderId);
}