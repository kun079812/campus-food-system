package com.campus.food.mapper;

import com.campus.food.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {

    @Select("""
            SELECT *
            FROM dish
            WHERE status = 1
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
        (stall_id, category_id, name, price, stock, status)
        VALUES
        (#{stallId}, #{categoryId}, #{name}, #{price}, #{stock}, 1)
        """)
    int insert(Dish dish);

    @Update("""
        UPDATE dish
        SET name = #{name},
            price = #{price},
            stock = #{stock}
        WHERE id = #{id}
        """)
    int update(Dish dish);

    @Update("""
        UPDATE dish
        SET status = #{status}
        WHERE id = #{id}
        """)
    int updateStatus(Long id, Integer status);
}