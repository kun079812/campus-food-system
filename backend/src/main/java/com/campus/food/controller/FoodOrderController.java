package com.campus.food.controller;

import com.campus.food.entity.FoodOrder;
import com.campus.food.entity.OrderItem;
import com.campus.food.service.FoodOrderService;
import com.campus.food.service.OrderItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/merchant/order")
public class FoodOrderController {


    private final FoodOrderService foodOrderService;

    private final OrderItemService orderItemService;


    public FoodOrderController(
            FoodOrderService foodOrderService,
            OrderItemService orderItemService
    ) {
        this.foodOrderService = foodOrderService;
        this.orderItemService = orderItemService;
    }


    @GetMapping("/list/{stallId}")
    public List<FoodOrder> list(@PathVariable Long stallId) {

        return foodOrderService.findByStallId(stallId);

    }


    @PutMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {

        foodOrderService.updateStatus(id, status);

        return "订单状态修改成功";
    }


    @GetMapping("/detail/{orderId}")
    public List<OrderItem> detail(
            @PathVariable Long orderId
    ) {

        return orderItemService.findByOrderId(orderId);

    }

}