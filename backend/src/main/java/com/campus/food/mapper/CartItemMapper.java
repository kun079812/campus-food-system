package com.campus.food.mapper;

import com.campus.food.entity.CartItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CartItemMapper {

    @Select("""
            SELECT
                ci.id,
                ci.user_id,
                ci.dish_id,
                ci.quantity,
                ci.created_at,
                ci.updated_at,
                d.name AS dish_name,
                d.price AS dish_price,
                d.image_url AS dish_image_url,
                d.stock AS dish_stock,
                d.status AS dish_status,
                d.price * ci.quantity AS subtotal
            FROM cart_item ci
            LEFT JOIN dish d ON ci.dish_id = d.id
            WHERE ci.user_id = #{userId}
            ORDER BY ci.updated_at DESC
            """)
    List<CartItem> findByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT *
            FROM cart_item
            WHERE user_id = #{userId}
              AND dish_id = #{dishId}
            """)
    CartItem findByUserIdAndDishId(
            @Param("userId") Long userId,
            @Param("dishId") Long dishId
    );

    @Insert("""
            INSERT INTO cart_item (user_id, dish_id, quantity)
            VALUES (#{userId}, #{dishId}, #{quantity})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CartItem cartItem);

    @Update("""
            UPDATE cart_item
            SET quantity = #{quantity}
            WHERE id = #{id}
            """)
    int updateQuantityById(
            @Param("id") Long id,
            @Param("quantity") Integer quantity
    );

    @Delete("""
            DELETE FROM cart_item
            WHERE user_id = #{userId}
              AND dish_id = #{dishId}
            """)
    int deleteByUserIdAndDishId(
            @Param("userId") Long userId,
            @Param("dishId") Long dishId
    );
    @Select("""
            SELECT DISTINCT d.stall_id
            FROM cart_item ci
            JOIN dish d ON ci.dish_id = d.id
            WHERE ci.user_id = #{userId}
            """)
    List<Long> findDistinctStallIdsByUserId(
            @Param("userId") Long userId
    );

    @Delete("""
            DELETE FROM cart_item
            WHERE user_id = #{userId}
            """)
    int deleteByUserId(@Param("userId") Long userId);
    @Select("""
            <script>
            SELECT *
            FROM cart_item
            WHERE user_id = #{userId}
              AND id IN
              <foreach collection="itemIds" item="itemId"
                       open="(" separator="," close=")">
                  #{itemId}
              </foreach>
            ORDER BY updated_at DESC
            </script>
            """)
    List<CartItem> findByUserIdAndIds(
            @Param("userId") Long userId,
            @Param("itemIds") List<Long> itemIds
    );

    @Delete("""
            <script>
            DELETE FROM cart_item
            WHERE user_id = #{userId}
              AND id IN
              <foreach collection="itemIds" item="itemId"
                       open="(" separator="," close=")">
                  #{itemId}
              </foreach>
            </script>
            """)
    int deleteByUserIdAndIds(
            @Param("userId") Long userId,
            @Param("itemIds") List<Long> itemIds
    );
}