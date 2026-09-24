package com.campus.food.mapper;

import com.campus.food.entity.Stall;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StallMapper {

    @Select("""
            SELECT *
            FROM stall
            WHERE merchant_id = #{merchantId}
            """)
    Stall findByMerchantId(@Param("merchantId") Long merchantId);

    @Select("""
            SELECT s.*,
                   (
                       SELECT COUNT(*)
                       FROM dish d
                       WHERE d.stall_id = s.id
                         AND d.status = 1
                   ) AS dishCount
            FROM stall s
            WHERE s.canteen_id = #{canteenId}
              AND s.status = 1
            ORDER BY s.id
            """)
    List<Stall> findEnabledByCanteenId(@Param("canteenId") Long canteenId);

    @Update("""
            UPDATE stall
            SET name = #{name},
                business_hours = #{businessHours}
            WHERE id = #{id}
            """)
    int update(Stall stall);

    @Update("""
            UPDATE stall
            SET status = #{status}
            WHERE id = #{id}
            """)
    int updateStatus(
            @Param("id") Long id,
            @Param("status") Integer status
    );
}