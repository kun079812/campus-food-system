package com.campus.food.mapper;

import com.campus.food.entity.MerchantDailyStatistics;
import com.campus.food.entity.OrderEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("""
            SELECT *
            FROM food_order
            WHERE stall_id = #{stallId}
            ORDER BY created_at DESC
            """)
    List<OrderEntity> findByStallId(
            @Param("stallId") Long stallId
    );

    @Select("""
            SELECT *
            FROM food_order
            WHERE user_id = #{userId}
            ORDER BY created_at DESC
            """)
    List<OrderEntity> findByUserId(
            @Param("userId") Long userId
    );

    @Select("""
            SELECT *
            FROM food_order
            WHERE id = #{id}
            """)
    OrderEntity findById(
            @Param("id") Long id
    );

    @Insert("""
            INSERT INTO food_order
             (order_no, user_id, stall_id, total_amount, pay_amount,
            status, delivery_type, address_id, remark)
             VALUES
            (#{orderNo}, #{userId}, #{stallId}, #{totalAmount},
            #{payAmount}, #{status}, #{deliveryType},
            #{addressId}, #{remark})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderEntity order);

    @Update("""
            UPDATE food_order
            SET status = #{status}
            WHERE id = #{id}
            """)
    int changeStatus(
            @Param("id") Long id,
            @Param("status") String status
    );
    @Update("""
            UPDATE food_order
            SET pay_amount = #{payAmount}
            WHERE id = #{id}
            """)
    int updatePayAmount(
            @Param("id") Long id,
            @Param("payAmount") Double payAmount
    );

    @Select("""
            SELECT COUNT(*)
            FROM food_order
            WHERE stall_id = #{stallId}
              AND DATE(created_at) = CURDATE()
            """)
    Integer countTodayOrder(
            @Param("stallId") Long stallId
    );

    @Select("""
            SELECT COALESCE(SUM(total_amount), 0)
            FROM food_order
            WHERE stall_id = #{stallId}
              AND DATE(created_at) = CURDATE()
              AND status NOT IN ('WAIT_PAY', 'CANCELLED')
            """)
    Double sumTodaySales(
            @Param("stallId") Long stallId
    );

    @Select("""
            SELECT COUNT(*)
            FROM food_order
            WHERE stall_id = #{stallId}
              AND status = 'WAIT_ACCEPT'
            """)
    Integer countPendingOrder(
            @Param("stallId") Long stallId
    );

    @Select("""
            SELECT *
            FROM food_order
            WHERE stall_id = #{stallId}
            ORDER BY created_at DESC
            LIMIT 5
            """)
    List<OrderEntity> findLatestByStallId(
            @Param("stallId") Long stallId
    );

    @Select("""
            SELECT *
            FROM food_order
            WHERE stall_id = #{stallId}
              AND status = #{status}
            ORDER BY created_at DESC
            """)
    List<OrderEntity> findByStallIdAndStatus(
            @Param("stallId") Long stallId,
            @Param("status") String status
    );

    @Select("""
            SELECT *
            FROM food_order
            WHERE user_id = #{userId}
              AND status = #{status}
            ORDER BY created_at DESC
            """)
    List<OrderEntity> findByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") String status
    );

    @Select("""
            SELECT
                DATE_FORMAT(created_at, '%Y-%m-%d') AS stat_date,
                COUNT(*) AS order_count,
                COALESCE(SUM(total_amount), 0) AS sales_amount
            FROM food_order
            WHERE stall_id = #{stallId}
              AND DATE(created_at)
                  BETWEEN #{startDate} AND #{endDate}
              AND status NOT IN ('WAIT_PAY', 'CANCELLED')
            GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d')
            ORDER BY stat_date
            """)
    List<MerchantDailyStatistics> findDailyStatistics(
            @Param("stallId") Long stallId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );
}