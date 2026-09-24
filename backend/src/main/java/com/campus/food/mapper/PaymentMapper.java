package com.campus.food.mapper;

import com.campus.food.entity.Payment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PaymentMapper {

    @Select("""
            SELECT *
            FROM payment
            WHERE order_id = #{orderId}
            """)
    Payment findByOrderId(@Param("orderId") Long orderId);

    @Insert("""
            INSERT INTO payment
            (payment_no, order_id, user_id, pay_amount,
             pay_method, pay_status, paid_at)
            VALUES
            (#{paymentNo}, #{orderId}, #{userId}, #{payAmount},
             #{payMethod}, #{payStatus}, NOW())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Payment payment);
}