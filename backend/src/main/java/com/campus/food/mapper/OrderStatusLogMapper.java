package com.campus.food.mapper;

import com.campus.food.entity.OrderStatusLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderStatusLogMapper {

    @Insert("""
            INSERT INTO order_status_log
            (order_id, old_status, new_status)
            VALUES
            (#{orderId}, #{oldStatus}, #{newStatus})
            """)
    int insert(OrderStatusLog log);

    @Select("""
            SELECT *
            FROM order_status_log
            WHERE order_id = #{orderId}
            ORDER BY created_at ASC
            """)
    List<OrderStatusLog> findByOrderId(
            @Param("orderId") Long orderId
    );
}