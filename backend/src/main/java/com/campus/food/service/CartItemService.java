package com.campus.food.service;

import com.campus.food.entity.CartItem;
import com.campus.food.entity.Dish;
import com.campus.food.mapper.CartItemMapper;
import com.campus.food.mapper.DishMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CartItemService {

    private final CartItemMapper cartItemMapper;
    private final DishMapper dishMapper;

    public CartItemService(
            CartItemMapper cartItemMapper,
            DishMapper dishMapper
    ) {
        this.cartItemMapper = cartItemMapper;
        this.dishMapper = dishMapper;
    }

    public List<CartItem> findByUserId(Long userId) {
        return cartItemMapper.findByUserId(userId);
    }

    public CartItem add(Long userId, CartItem cartItem) {
        validateQuantity(cartItem.getQuantity());

        Dish dish = requireAvailableDish(cartItem.getDishId());

        validateCartStall(userId, dish.getStallId());

        CartItem oldCartItem = cartItemMapper.findByUserIdAndDishId(
                userId,
                cartItem.getDishId()
        );

        if (oldCartItem != null) {
            int newQuantity = oldCartItem.getQuantity() + cartItem.getQuantity();

            verifyStock(dish, newQuantity);

            cartItemMapper.updateQuantityById(
                    oldCartItem.getId(),
                    newQuantity
            );

            oldCartItem.setQuantity(newQuantity);
            return oldCartItem;
        }

        verifyStock(dish, cartItem.getQuantity());

        cartItem.setUserId(userId);
        cartItemMapper.insert(cartItem);

        return cartItem;
    }

    public void updateQuantity(
            Long userId,
            Long dishId,
            Integer quantity
    ) {
        validateQuantity(quantity);

        CartItem cartItem = requireCartItem(userId, dishId);
        Dish dish = requireAvailableDish(dishId);

        verifyStock(dish, quantity);

        cartItemMapper.updateQuantityById(cartItem.getId(), quantity);
    }

    public void delete(Long userId, Long dishId) {
        requireCartItem(userId, dishId);

        cartItemMapper.deleteByUserIdAndDishId(userId, dishId);
    }

    private void validateCartStall(Long userId, Long dishStallId) {
        List<Long> cartStallIds =
                cartItemMapper.findDistinctStallIdsByUserId(userId);

        if (cartStallIds.isEmpty()) {
            return;
        }

        if (cartStallIds.size() != 1
                || !cartStallIds.get(0).equals(dishStallId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "购物车中已有其他档口的菜品，请先清空购物车后再加入"
            );
        }
    }

    private CartItem requireCartItem(Long userId, Long dishId) {
        CartItem cartItem = cartItemMapper.findByUserIdAndDishId(
                userId,
                dishId
        );

        if (cartItem == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "购物车中没有该菜品"
            );
        }

        return cartItem;
    }

    private Dish requireAvailableDish(Long dishId) {
        if (dishId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "菜品不能为空"
            );
        }

        Dish dish = dishMapper.findEnabledById(dishId);

        if (dish == null || !Integer.valueOf(1).equals(dish.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "菜品不存在或已下架"
            );
        }

        if (dish.getStock() == null || dish.getStock() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "菜品库存不足"
            );
        }

        return dish;
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "菜品数量必须大于 0"
            );
        }
    }

    private void verifyStock(Dish dish, Integer quantity) {
        if (dish.getStock() < quantity) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "菜品库存不足"
            );
        }
    }
}