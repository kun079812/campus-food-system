package com.campus.food.service;

import com.campus.food.entity.Dish;
import com.campus.food.mapper.DishMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {

    private final DishMapper dishMapper;

    public DishService(DishMapper dishMapper) {
        this.dishMapper = dishMapper;
    }

    public Dish findById(Long id) {
        return dishMapper.findById(id);
    }//根据id查询
    public List<Dish> findAll() {
        return dishMapper.findAll();
    }
    public List<Dish> findByStallId(Long stallId) {
        return dishMapper.findByStallId(stallId);
    }
    public int insert(Dish dish) {
        return dishMapper.insert(dish);
    }
    public int update(Dish dish) {
        return dishMapper.update(dish);
    }
    public int updateStatus(Long id, Integer status) {
        return dishMapper.updateStatus(id, status);
    }
}