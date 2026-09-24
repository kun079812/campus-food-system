package com.campus.food.mapper;

import com.campus.food.entity.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;import com.campus.food.entity.MerchantDishSales;
import org.apache.ibatis.annotations.Param;

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

    @Select("""
            SELECT
                oi.dish_id AS dish_id,
                d.name AS dish_name,
                SUM(oi.quantity) AS sales_quantity,
                COALESCE(SUM(oi.price * oi.quantity), 0) AS sales_amount
            FROM order_item oi
            JOIN food_order o ON oi.order_id = o.id
            JOIN dish d ON oi.dish_id = d.id
            WHERE o.stall_id = #{stallId}
              AND o.status NOT IN ('WAIT_PAY', 'CANCELLED')
            GROUP BY oi.dish_id, d.name
            ORDER BY oi.dish_id
            """)
    List<MerchantDishSales> findDishSalesByStallId(
            @Param("stallId") Long stallId
    );

    @Insert("""
            INSERT INTO order_item
            (order_id, dish_id, quantity, price)
            VALUES
            (#{orderId}, #{dishId}, #{quantity}, #{price})
            """)
    int insert(OrderItem orderItem);
}