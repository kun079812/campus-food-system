package com.campus.food.service;

import com.campus.food.entity.Canteen;
import com.campus.food.mapper.CanteenMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CanteenService {

    private final CanteenMapper canteenMapper;

    public CanteenService(CanteenMapper canteenMapper) {
        this.canteenMapper = canteenMapper;
    }

    public List<Canteen> findAll() {
        return canteenMapper.findAll();
    }
}