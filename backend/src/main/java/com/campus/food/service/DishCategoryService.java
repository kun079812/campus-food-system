package com.campus.food.service;

import com.campus.food.entity.DishCategory;
import com.campus.food.entity.Stall;
import com.campus.food.mapper.DishCategoryMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DishCategoryService {

    private final DishCategoryMapper dishCategoryMapper;
    private final StallService stallService;

    public DishCategoryService(
            DishCategoryMapper dishCategoryMapper,
            StallService stallService
    ) {
        this.dishCategoryMapper = dishCategoryMapper;
        this.stallService = stallService;
    }

    public List<DishCategory> findByStallId(Long stallId) {
        if (stallId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "档口不能为空"
            );
        }

        return dishCategoryMapper.findByStallId(stallId);
    }

    public List<DishCategory> findByMerchantId(Long merchantId) {
        Stall stall = requireMerchantStall(merchantId);

        return dishCategoryMapper.findByStallId(stall.getId());
    }

    public DishCategory addByMerchant(
            Long merchantId,
            DishCategory category
    ) {
        Stall stall = requireMerchantStall(merchantId);
        String name = normalizeName(category);

        DishCategory existing =
                dishCategoryMapper.findByStallIdAndName(
                        stall.getId(),
                        name
                );

        if (existing != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "当前档口已存在同名分类"
            );
        }

        category.setStallId(stall.getId());
        category.setName(name);

        dishCategoryMapper.insert(category);

        return category;
    }

    public DishCategory updateByMerchant(
            Long merchantId,
            Long categoryId,
            DishCategory requestCategory
    ) {
        Stall stall = requireMerchantStall(merchantId);

        DishCategory category = requireOwnedCategory(
                stall.getId(),
                categoryId
        );

        String name = normalizeName(requestCategory);

        DishCategory existing =
                dishCategoryMapper.findByStallIdAndName(
                        stall.getId(),
                        name
                );

        if (existing != null
                && !existing.getId().equals(categoryId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "当前档口已存在同名分类"
            );
        }

        category.setName(name);
        dishCategoryMapper.update(category);

        return category;
    }

    public void deleteByMerchant(
            Long merchantId,
            Long categoryId
    ) {
        Stall stall = requireMerchantStall(merchantId);

        requireOwnedCategory(stall.getId(), categoryId);

        if (dishCategoryMapper.countDishByCategoryId(categoryId) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "该分类下还有菜品，不能删除"
            );
        }

        dishCategoryMapper.deleteById(categoryId);
    }

    private String normalizeName(DishCategory category) {
        if (category == null
                || category.getName() == null
                || category.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "分类名称不能为空"
            );
        }

        String name = category.getName().trim();

        if (name.length() > 80) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "分类名称不能超过 80 个字"
            );
        }

        return name;
    }

    private DishCategory requireOwnedCategory(
            Long stallId,
            Long categoryId
    ) {
        DishCategory category =
                dishCategoryMapper.findById(categoryId);

        if (category == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "菜品分类不存在"
            );
        }

        if (!stallId.equals(category.getStallId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "无权操作其他档口的菜品分类"
            );
        }

        return category;
    }

    private Stall requireMerchantStall(Long merchantId) {
        Stall stall = stallService.findByMerchantId(merchantId);

        if (stall == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "当前商家没有关联档口"
            );
        }

        return stall;
    }
}