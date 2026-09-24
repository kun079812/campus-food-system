package com.campus.food.mapper;

import com.campus.food.entity.DishCategory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishCategoryMapper {

    @Select("""
            SELECT *
            FROM dish_category
            WHERE stall_id = #{stallId}
            ORDER BY id
            """)
    List<DishCategory> findByStallId(
            @Param("stallId") Long stallId
    );

    @Select("""
            SELECT *
            FROM dish_category
            WHERE id = #{id}
            """)
    DishCategory findById(
            @Param("id") Long id
    );

    @Select("""
            SELECT *
            FROM dish_category
            WHERE stall_id = #{stallId}
              AND name = #{name}
            """)
    DishCategory findByStallIdAndName(
            @Param("stallId") Long stallId,
            @Param("name") String name
    );

    @Insert("""
            INSERT INTO dish_category (stall_id, name)
            VALUES (#{stallId}, #{name})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DishCategory category);

    @Update("""
            UPDATE dish_category
            SET name = #{name}
            WHERE id = #{id}
            """)
    int update(DishCategory category);

    @Select("""
            SELECT COUNT(*)
            FROM dish
            WHERE category_id = #{categoryId}
            """)
    Integer countDishByCategoryId(
            @Param("categoryId") Long categoryId
    );

    @Delete("""
            DELETE FROM dish_category
            WHERE id = #{id}
            """)
    int deleteById(@Param("id") Long id);
}