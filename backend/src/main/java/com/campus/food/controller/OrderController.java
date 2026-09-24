package com.campus.food.controller;

import com.campus.food.entity.OrderEntity;
import com.campus.food.entity.SysUser;
import com.campus.food.service.OrderService;
import com.campus.food.vo.OrderDetailVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/merchant/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderEntity> list(
            @RequestParam(required = false) String status,
            HttpSession session
    ) {
        SysUser merchant = requireMerchant(session);
        return orderService.findByMerchantIdAndStatus(merchant.getId(), status);
    }

    @PutMapping("/{id}/status")
    public String changeStatus(
            @PathVariable Long id,
            @RequestParam String status,
            HttpSession session
    ) {
        SysUser merchant = requireMerchant(session);
        orderService.changeStatusByMerchant(merchant.getId(), id, status);
        return "订单状态修改成功";
    }

    @GetMapping("/{orderId}")
    public OrderDetailVO detail(
            @PathVariable Long orderId,
            HttpSession session
    ) {
        SysUser merchant = requireMerchant(session);
        return orderService.getDetailByMerchant(merchant.getId(), orderId);
    }

    private SysUser requireMerchant(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        if (!"MERCHANT".equals(user.getRoleCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有商家可以操作订单");
        }
        return user;
    }
}