package com.campus.food.controller;

import com.campus.food.entity.CartItem;
import com.campus.food.entity.SysUser;
import com.campus.food.service.CartItemService;
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
@RequestMapping("/api/cart/items")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping
    public List<CartItem> list(HttpSession session) {
        SysUser student = requireStudent(session);
        return cartItemService.findByUserId(student.getId());
    }

    @PostMapping
    public CartItem add(
            @RequestBody CartItem cartItem,
            HttpSession session
    ) {
        SysUser student = requireStudent(session);
        return cartItemService.add(student.getId(), cartItem);
    }

    @PutMapping("/{dishId}")
    public String updateQuantity(
            @PathVariable Long dishId,
            @RequestBody CartItem cartItem,
            HttpSession session
    ) {
        SysUser student = requireStudent(session);
        cartItemService.updateQuantity(
                student.getId(),
                dishId,
                cartItem.getQuantity()
        );
        return "购物车数量修改成功";
    }

    @DeleteMapping("/{dishId}")
    public String delete(
            @PathVariable Long dishId,
            HttpSession session
    ) {
        SysUser student = requireStudent(session);
        cartItemService.delete(student.getId(), dishId);
        return "购物车菜品删除成功";
    }

    private SysUser requireStudent(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        if (!"STUDENT".equals(user.getRoleCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有学生可以操作购物车");
        }
        return user;
    }
}