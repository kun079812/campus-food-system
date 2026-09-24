package com.campus.food.controller;

import com.campus.food.entity.OrderEntity;
import com.campus.food.entity.Payment;
import com.campus.food.entity.SysUser;
import com.campus.food.service.OrderService;
import com.campus.food.vo.OrderCreateRequest;
import com.campus.food.vo.OrderDetailVO;
import com.campus.food.vo.PaymentRequest;
import com.campus.food.vo.PaymentResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class StudentOrderController {

    private final OrderService orderService;

    public StudentOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderEntity create(
            @RequestBody OrderCreateRequest request,
            HttpSession session
    ) {
        SysUser student = requireStudent(session);
        return orderService.createByStudent(student.getId(), request);
    }

    @GetMapping
    public List<OrderEntity> list(
            @RequestParam(required = false) String status,
            HttpSession session
    ) {
        SysUser student = requireStudent(session);
        return orderService.findByStudentIdAndStatus(student.getId(), status);
    }

    @GetMapping("/{orderId}")
    public OrderDetailVO detail(
            @PathVariable Long orderId,
            HttpSession session
    ) {
        SysUser student = requireStudent(session);
        return orderService.getDetailByStudent(student.getId(), orderId);
    }

    @PostMapping("/{orderId}/pay")
    public PaymentResult pay(
            @PathVariable Long orderId,
            @RequestBody PaymentRequest request,
            HttpSession session
    ) {
        SysUser student = requireStudent(session);
        Payment payment = orderService.payByStudent(
                student.getId(),
                orderId,
                request.getPaymentMethod()
        );

        PaymentResult result = new PaymentResult();
        result.setPaymentNo(payment.getPaymentNo());
        result.setOrderStatus("WAIT_ACCEPT");
        return result;
    }

    @PostMapping("/{orderId}/cancel")
    public String cancel(
            @PathVariable Long orderId,
            HttpSession session
    ) {
        SysUser student = requireStudent(session);
        orderService.cancelByStudent(student.getId(), orderId);
        return "订单已取消";
    }

    @PostMapping("/{orderId}/confirm-pickup")
    public String confirmPickup(
            @PathVariable Long orderId,
            HttpSession session
    ) {
        return confirm(orderId, session);
    }

    @PostMapping("/{orderId}/confirm-receipt")
    public String confirmReceipt(
            @PathVariable Long orderId,
            HttpSession session
    ) {
        return confirm(orderId, session);
    }

    private String confirm(Long orderId, HttpSession session) {
        SysUser student = requireStudent(session);
        orderService.confirmByStudent(student.getId(), orderId);
        return "订单已完成";
    }

    private SysUser requireStudent(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        if (!"STUDENT".equals(user.getRoleCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有学生可以操作订单");
        }
        return user;
    }
}