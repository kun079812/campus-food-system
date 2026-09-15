package com.campus.food.mapper;

import com.campus.food.entity.FoodOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FoodOrderMapper {


    @Select("""
            SELECT *
            FROM food_order
            WHERE stall_id = #{stallId}
            ORDER BY created_at DESC
            """)
    List<FoodOrder> findByStallId(Long stallId);

    @Update("""
        UPDATE food_order
        SET status = #{status}
        WHERE id = #{id}
        """)
    int updateStatus(Long id, String status);
    @Select("""
        SELECT *
        FROM food_order
        WHERE id = #{id}
        """)
    FoodOrder findById(Long id);
}