package com.campus.food.controller;

import com.campus.food.entity.Dish;
import com.campus.food.service.DishService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }
    @GetMapping("/{id}")
    public Dish detail(@PathVariable Long id) {
        return dishService.findById(id);
    }

    @GetMapping("/list")
    public List<Dish> list() {
        return dishService.findAll();
    }
    @GetMapping("/stall/{stallId}")
    public List<Dish> listByStall(@PathVariable Long stallId) {
        return dishService.findByStallId(stallId);
    }
    @PostMapping("/add")
    public String add(@RequestBody Dish dish) {

        dishService.insert(dish);

        return "添加成功";
    }
    @PutMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestBody Dish dish
    ) {

        dish.setId(id);

        dishService.update(dish);

        return "修改成功";
    }
    @PutMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {

        dishService.updateStatus(id, status);

        return "状态修改成功";
    }
}