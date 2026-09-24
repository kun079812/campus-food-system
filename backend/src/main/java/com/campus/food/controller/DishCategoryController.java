package com.campus.food.controller;

import com.campus.food.entity.DishCategory;
import com.campus.food.entity.SysUser;
import com.campus.food.service.DishCategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DishCategoryController {

    private final DishCategoryService dishCategoryService;

    public DishCategoryController(DishCategoryService dishCategoryService) {
        this.dishCategoryService = dishCategoryService;
    }

    @GetMapping("/stalls/{stallId}/dish-categories")
    public List<DishCategory> list(@PathVariable Long stallId) {
        return dishCategoryService.findByStallId(stallId);
    }

    @GetMapping("/merchant/dish-categories")
    public List<DishCategory> merchantList(HttpSession session) {
        SysUser merchant = requireMerchant(session);
        return dishCategoryService.findByMerchantId(merchant.getId());
    }

    @PostMapping("/merchant/dish-categories")
    public DishCategory add(
            @RequestBody DishCategory category,
            HttpSession session
    ) {
        SysUser merchant = requireMerchant(session);
        return dishCategoryService.addByMerchant(merchant.getId(), category);
    }

    @PutMapping("/merchant/dish-categories/{id}")
    public DishCategory update(
            @PathVariable Long id,
            @RequestBody DishCategory category,
            HttpSession session
    ) {
        SysUser merchant = requireMerchant(session);
        return dishCategoryService.updateByMerchant(
                merchant.getId(), id, category
        );
    }

    @DeleteMapping("/merchant/dish-categories/{id}")
    public String delete(
            @PathVariable Long id,
            HttpSession session
    ) {
        SysUser merchant = requireMerchant(session);
        dishCategoryService.deleteByMerchant(merchant.getId(), id);
        return "分类删除成功";
    }

    private SysUser requireMerchant(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        if (!"MERCHANT".equals(user.getRoleCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有商家可以操作菜品分类");
        }
        return user;
    }
}