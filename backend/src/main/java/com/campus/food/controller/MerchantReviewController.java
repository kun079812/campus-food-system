package com.campus.food.controller;

import com.campus.food.entity.Review;
import com.campus.food.entity.SysUser;
import com.campus.food.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/merchant/reviews")
public class MerchantReviewController {

    private final ReviewService reviewService;

    public MerchantReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<Review> list(HttpSession session) {
        SysUser merchant = requireMerchant(session);
        return reviewService.findByMerchantId(merchant.getId());
    }

    private SysUser requireMerchant(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        if (!"MERCHANT".equals(user.getRoleCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有商家可以查看评价");
        }
        return user;
    }
}