package com.campus.food.service;

import com.campus.food.entity.OrderEntity;
import com.campus.food.entity.Review;
import com.campus.food.entity.Stall;
import com.campus.food.mapper.OrderMapper;
import com.campus.food.mapper.ReviewMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private final StallService stallService;

    public ReviewService(
            ReviewMapper reviewMapper,
            OrderMapper orderMapper,
            StallService stallService
    ) {
        this.reviewMapper = reviewMapper;
        this.orderMapper = orderMapper;
        this.stallService = stallService;
    }

    public Review submit(Long studentId, Review review) {
        validateReview(review);

        OrderEntity order = orderMapper.findById(review.getOrderId());
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "订单不存在");
        }
        if (!studentId.equals(order.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权评价其他学生的订单");
        }
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "只有已完成订单可以评价");
        }
        if (reviewMapper.findByOrderId(order.getId()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该订单已经评价过");
        }

        review.setUserId(studentId);
        review.setStatus(1);
        reviewMapper.insert(review);
        return review;
    }

    public List<Review> findByStudentId(Long studentId) {
        return reviewMapper.findByUserId(studentId);
    }

    public List<Review> findByMerchantId(Long merchantId) {
        Stall stall = stallService.findByMerchantId(merchantId);
        if (stall == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前商家没有关联档口");
        }
        return reviewMapper.findByStallId(stall.getId());
    }

    public List<Review> findPublicList() {
        return reviewMapper.findAllEnabled();
    }

    private void validateReview(Review review) {
        if (review == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "评价信息不能为空");
        }
        if (review.getOrderId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单不能为空");
        }
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "评分必须是 1 到 5");
        }
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "评价内容不能为空");
        }

        review.setContent(review.getContent().trim());
        if (review.getContent().length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "评价内容不能超过 500 字");
        }
    }
}