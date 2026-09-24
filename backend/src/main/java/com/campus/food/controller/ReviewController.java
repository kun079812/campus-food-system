package com.campus.food.controller;

import com.campus.food.entity.Review;
import com.campus.food.entity.SysUser;
import com.campus.food.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews")
    public List<Review> list() {
        return reviewService.findPublicList();
    }

    @PostMapping("/reviews")
    public Review submit(
            @RequestBody Review review,
            HttpSession session
    ) {
        SysUser student = requireStudent(session);
        return reviewService.submit(student.getId(), review);
    }

    @GetMapping("/student/reviews")
    public List<Review> myList(HttpSession session) {
        SysUser student = requireStudent(session);
        return reviewService.findByStudentId(student.getId());
    }

    private SysUser requireStudent(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        if (!"STUDENT".equals(user.getRoleCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有学生可以评价订单");
        }
        return user;
    }
}