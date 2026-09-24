package com.campus.food.mapper;

import com.campus.food.entity.Review;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewMapper {

    @Select("""
            SELECT *
            FROM review
            WHERE order_id = #{orderId}
            """)
    Review findByOrderId(@Param("orderId") Long orderId);

    @Select("""
            SELECT *
            FROM review
            WHERE user_id = #{userId}
              AND status = 1
            ORDER BY created_at DESC
            """)
    List<Review> findByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT r.*
            FROM review r
            JOIN food_order o ON r.order_id = o.id
            WHERE o.stall_id = #{stallId}
              AND r.status = 1
            ORDER BY r.created_at DESC
            """)
    List<Review> findByStallId(@Param("stallId") Long stallId);

    @Select("""
            SELECT *
            FROM review
            WHERE status = 1
            ORDER BY created_at DESC
            """)
    List<Review> findAllEnabled();

    @Insert("""
            INSERT INTO review
            (order_id, user_id, rating, content, status)
            VALUES
            (#{orderId}, #{userId}, #{rating}, #{content}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Review review);
}