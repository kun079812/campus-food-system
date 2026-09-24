package com.campus.food.mapper;

import com.campus.food.entity.Dish;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {

    @Select("""
        SELECT d.*
        FROM dish d
        JOIN stall s ON d.stall_id = s.id
        WHERE d.status = 1
          AND s.status = 1
        """)
    List<Dish> findAll();

    @Select("""
        SELECT *
        FROM dish
        WHERE id = #{id}
        """)
    Dish findById(Long id);

    @Select("""
        SELECT *
        FROM dish
        WHERE stall_id = #{stallId}
        """)
    List<Dish> findByStallId(Long stallId);

    @Insert("""
        INSERT INTO dish
        (stall_id, category_id, name, price, stock,
         description, image_url, status)
        VALUES
        (#{stallId}, #{categoryId}, #{name}, #{price}, #{stock},
         #{description}, #{imageUrl}, 1)
        """)
    int insert(Dish dish);

    @Update("""
        UPDATE dish
        SET category_id = #{categoryId},
            name = #{name},
            price = #{price},
            stock = #{stock},
            description = COALESCE(#{description}, description),
            image_url = COALESCE(#{imageUrl}, image_url)
        WHERE id = #{id}
        """)
    int update(Dish dish);

    @Update("""
        UPDATE dish
        SET status = #{status}
        WHERE id = #{id}
        """)
    int updateStatus(Long id, Integer status);

    @Update("""
        UPDATE dish
        SET stock = stock - #{quantity}
        WHERE id = #{dishId}
          AND status = 1
          AND stock >= #{quantity}
          AND EXISTS (
              SELECT 1
              FROM stall s
              WHERE s.id = dish.stall_id
                AND s.status = 1
          )
        """)
    int decreaseStock(
            @Param("dishId") Long dishId,
            @Param("quantity") Integer quantity
    );

    @Select("""
            SELECT d.*
            FROM dish d
            JOIN stall s ON d.stall_id = s.id
            WHERE d.id = #{id}
              AND d.status = 1
              AND s.status = 1
            """)
    Dish findEnabledById(Long id);

    @Select("""
            SELECT d.*
            FROM dish d
            JOIN stall s ON d.stall_id = s.id
            WHERE d.stall_id = #{stallId}
              AND d.status = 1
              AND s.status = 1
            """)
    List<Dish> findEnabledByStallId(Long stallId);

    @Select("""
            SELECT d.*
            FROM dish d
            JOIN stall s ON d.stall_id = s.id
            WHERE d.category_id = #{categoryId}
              AND d.status = 1
              AND s.status = 1
            """)
    List<Dish> findByCategoryId(Long categoryId);

    @Select("""
            SELECT d.*
            FROM dish d
            JOIN stall s ON d.stall_id = s.id
            WHERE d.status = 1
              AND s.status = 1
              AND (
                  d.name LIKE CONCAT('%', #{keyword}, '%')
                  OR d.description LIKE CONCAT('%', #{keyword}, '%')
              )
            ORDER BY d.id DESC
            """)
    List<Dish> searchByKeyword(
            @Param("keyword") String keyword
    );
    @Select("""
            SELECT d.*
            FROM dish d
            JOIN stall s ON d.stall_id = s.id
            WHERE d.status = 1
              AND s.status = 1
            ORDER BY d.id DESC
            LIMIT #{pageSize} OFFSET #{offset}
            """)
    List<Dish> findAllPage(
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );

    @Select("""
            SELECT d.*
            FROM dish d
            JOIN stall s ON d.stall_id = s.id
            WHERE d.status = 1
              AND s.status = 1
              AND (
                  d.name LIKE CONCAT('%', #{keyword}, '%')
                  OR d.description LIKE CONCAT('%', #{keyword}, '%')
              )
            ORDER BY d.id DESC
            LIMIT #{pageSize} OFFSET #{offset}
            """)
    List<Dish> searchByKeywordPage(
            @Param("keyword") String keyword,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );
}