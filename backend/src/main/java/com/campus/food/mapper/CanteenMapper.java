package com.campus.food.mapper;

import com.campus.food.entity.Canteen;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CanteenMapper {

    @Select("""
            SELECT *
            FROM canteen
            ORDER BY id
            """)
    List<Canteen> findAll();
}