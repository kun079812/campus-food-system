package com.campus.food.controller;

import com.campus.food.entity.Dish;
import com.campus.food.entity.SysUser;
import com.campus.food.service.DishService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping("/dishes")
    public List<Dish> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        return dishService.search(keyword, page, pageSize);
    }

    @GetMapping("/dishes/{id}")
    public Dish detail(@PathVariable Long id) {
        return dishService.findPublicById(id);
    }

    @GetMapping("/stalls/{stallId}/dishes")
    public List<Dish> listByStall(@PathVariable Long stallId) {
        return dishService.findEnabledByStallId(stallId);
    }

    @GetMapping("/dish-categories/{categoryId}/dishes")
    public List<Dish> listByCategory(@PathVariable Long categoryId) {
        return dishService.findByCategoryId(categoryId);
    }

    @GetMapping("/merchant/dishes")
    public List<Dish> merchantList(HttpSession session) {
        SysUser merchant = requireMerchant(session);
        return dishService.findByMerchantId(merchant.getId());
    }

    @PostMapping("/merchant/dishes")
    public String add(
            @RequestBody Dish dish,
            HttpSession session
    ) {
        SysUser merchant = requireMerchant(session);
        dishService.insertByMerchant(merchant.getId(), dish);
        return "添加成功";
    }

    @PutMapping("/merchant/dishes/{id}")
    public String update(
            @PathVariable Long id,
            @RequestBody Dish dish,
            HttpSession session
    ) {
        SysUser merchant = requireMerchant(session);
        dishService.updateByMerchant(merchant.getId(), id, dish);
        return "修改成功";
    }

    @PutMapping("/merchant/dishes/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status,
            HttpSession session
    ) {
        SysUser merchant = requireMerchant(session);
        dishService.updateStatusByMerchant(merchant.getId(), id, status);
        return "状态修改成功";
    }

    private SysUser requireMerchant(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        if (!"MERCHANT".equals(user.getRoleCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有商家可以操作菜品");
        }
        return user;
    }
}