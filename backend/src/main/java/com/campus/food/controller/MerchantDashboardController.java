package com.campus.food.controller;

import com.campus.food.entity.MerchantDashboard;
import com.campus.food.entity.MerchantDishSales;
import com.campus.food.entity.SysUser;
import com.campus.food.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/merchant/dashboard")
public class MerchantDashboardController {

    private final OrderService orderService;

    public MerchantDashboardController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/summary")
    public MerchantDashboard summary(HttpSession session) {
        SysUser merchant = requireMerchant(session);
        return orderService.getDashboardByMerchantId(merchant.getId());
    }

    @GetMapping("/dish-sales")
    public List<MerchantDishSales> dishSales(HttpSession session) {
        SysUser merchant = requireMerchant(session);
        return orderService.getDishSalesByMerchantId(merchant.getId());
    }

    private SysUser requireMerchant(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        if (!"MERCHANT".equals(user.getRoleCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有商家可以查看首页数据");
        }
        return user;
    }
}