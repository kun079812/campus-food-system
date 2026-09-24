package com.campus.food.service;

import com.campus.food.entity.Dish;
import com.campus.food.entity.DishCategory;
import com.campus.food.entity.Stall;
import com.campus.food.mapper.DishCategoryMapper;
import com.campus.food.mapper.DishMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DishService {

    private final DishMapper dishMapper;
    private final StallService stallService;
    private final DishCategoryMapper dishCategoryMapper;

    public DishService(
            DishMapper dishMapper,
            StallService stallService,
            DishCategoryMapper dishCategoryMapper
    ) {
        this.dishMapper = dishMapper;
        this.stallService = stallService;
        this.dishCategoryMapper = dishCategoryMapper;
    }

    public Dish findById(Long id) {
        return dishMapper.findById(id);
    }

    public Dish findPublicById(Long id) {
        Dish dish = dishMapper.findEnabledById(id);

        if (dish == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "菜品不存在或已下架"
            );
        }

        return dish;
    }

    public List<Dish> findAll() {
        return dishMapper.findAll();
    }

    public List<Dish> findByStallId(Long stallId) {
        return dishMapper.findByStallId(stallId);
    }

    public List<Dish> findEnabledByStallId(Long stallId) {
        return dishMapper.findEnabledByStallId(stallId);
    }

    public List<Dish> findByCategoryId(Long categoryId) {
        return dishMapper.findByCategoryId(categoryId);
    }

    public List<Dish> findByMerchantId(Long merchantId) {
        Stall stall = requireMerchantStall(merchantId);

        return dishMapper.findByStallId(stall.getId());
    }

    public int insert(Dish dish) {
        validateDishForSave(dish, null);

        return dishMapper.insert(dish);
    }

    public int update(Dish dish) {
        validateDishForSave(dish, null);

        return dishMapper.update(dish);
    }

    public int updateStatus(Long id, Integer status) {
        validateDishStatus(status);

        return dishMapper.updateStatus(id, status);
    }

    public int insertByMerchant(Long merchantId, Dish dish) {
        Stall stall = requireMerchantStall(merchantId);

        validateDishForSave(dish, stall.getId());

        // 不相信前端传来的 stallId，强制使用当前商家的档口
        dish.setStallId(stall.getId());

        return dishMapper.insert(dish);
    }

    public int updateByMerchant(
            Long merchantId,
            Long dishId,
            Dish dish
    ) {
        Stall stall = verifyDishOwnership(merchantId, dishId);

        validateDishForSave(dish, stall.getId());

        dish.setId(dishId);

        return dishMapper.update(dish);
    }

    public int updateStatusByMerchant(
            Long merchantId,
            Long dishId,
            Integer status
    ) {
        verifyDishOwnership(merchantId, dishId);
        validateDishStatus(status);

        return dishMapper.updateStatus(dishId, status);
    }

    public List<Dish> search(
            String keyword,
            Integer page,
            Integer pageSize
    ) {
        if (page == null && pageSize == null) {
            if (keyword == null || keyword.isBlank()) {
                return dishMapper.findAll();
            }

            return dishMapper.searchByKeyword(keyword.trim());
        }

        if (page == null || pageSize == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "分页查询必须同时传 page 和 pageSize"
            );
        }

        if (page <= 0 || pageSize <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "page 和 pageSize 必须大于 0"
            );
        }

        int offset = (page - 1) * pageSize;

        if (keyword == null || keyword.isBlank()) {
            return dishMapper.findAllPage(offset, pageSize);
        }

        return dishMapper.searchByKeywordPage(
                keyword.trim(),
                offset,
                pageSize
        );
    }

    private void validateDishForSave(
            Dish dish,
            Long expectedStallId
    ) {
        if (dish == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "菜品信息不能为空"
            );
        }

        if (dish.getCategoryId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "菜品分类不能为空"
            );
        }

        DishCategory category =
                dishCategoryMapper.findById(dish.getCategoryId());

        if (category == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "菜品分类不存在"
            );
        }

        if (expectedStallId != null
                && !expectedStallId.equals(category.getStallId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "请选择当前档口的菜品分类"
            );
        }

        if (dish.getName() == null || dish.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "菜品名称不能为空"
            );
        }

        dish.setName(dish.getName().trim());

        if (dish.getPrice() == null || dish.getPrice() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "菜品价格必须大于 0"
            );
        }

        if (dish.getStock() == null || dish.getStock() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "菜品库存不能小于 0"
            );
        }
    }

    private void validateDishStatus(Integer status) {
        if (!Integer.valueOf(0).equals(status)
                && !Integer.valueOf(1).equals(status)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "菜品状态只能是 0 或 1"
            );
        }
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

    private Stall verifyDishOwnership(
            Long merchantId,
            Long dishId
    ) {
        Stall stall = requireMerchantStall(merchantId);
        Dish dish = dishMapper.findById(dishId);

        if (dish == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "菜品不存在"
            );
        }

        if (!stall.getId().equals(dish.getStallId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "无权操作其他档口的菜品"
            );
        }

        return stall;
    }
}