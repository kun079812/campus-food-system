package com.campus.food.mapper;

import com.campus.food.entity.DeliveryAddress;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DeliveryAddressMapper {

    @Select("""
            SELECT *
            FROM delivery_address
            WHERE student_id = #{studentId}
              AND status = 1
            ORDER BY is_default DESC, updated_at DESC
            """)
    List<DeliveryAddress> findByStudentId(
            @Param("studentId") Long studentId
    );

    @Select("""
            SELECT *
            FROM delivery_address
            WHERE id = #{id}
            """)
    DeliveryAddress findById(
            @Param("id") Long id
    );

    @Insert("""
            INSERT INTO delivery_address
            (student_id, contact_name, phone, detail, is_default, status)
            VALUES
            (#{studentId}, #{contactName}, #{phone},
             #{detail}, #{isDefault}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DeliveryAddress address);

    @Update("""
            UPDATE delivery_address
            SET contact_name = #{contactName},
                phone = #{phone},
                detail = #{detail},
                is_default = #{isDefault}
            WHERE id = #{id}
            """)
    int update(DeliveryAddress address);

    @Update("""
            UPDATE delivery_address
            SET is_default = 0
            WHERE student_id = #{studentId}
            """)
    int clearDefaultByStudentId(
            @Param("studentId") Long studentId
    );

    @Update("""
            UPDATE delivery_address
            SET status = 0
            WHERE id = #{id}
            """)
    int deleteById(
            @Param("id") Long id
    );
}