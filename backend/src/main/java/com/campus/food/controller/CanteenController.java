package com.campus.food.controller;

import com.campus.food.entity.Canteen;
import com.campus.food.entity.Stall;
import com.campus.food.service.CanteenService;
import com.campus.food.service.StallService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/canteens")
public class CanteenController {

    private final CanteenService canteenService;
    private final StallService stallService;

    public CanteenController(
            CanteenService canteenService,
            StallService stallService
    ) {
        this.canteenService = canteenService;
        this.stallService = stallService;
    }

    @GetMapping
    public List<Canteen> list() {
        return canteenService.findAll();
    }

    @GetMapping("/{canteenId}/stalls")
    public List<Stall> stallList(@PathVariable Long canteenId) {
        return stallService.findEnabledByCanteenId(canteenId);
    }
}