package com.campus.food.controller;

import com.campus.food.entity.DeliveryAddress;
import com.campus.food.entity.SysUser;
import com.campus.food.service.DeliveryAddressService;
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
@RequestMapping("/api/addresses")
public class DeliveryAddressController {

    private final DeliveryAddressService deliveryAddressService;

    public DeliveryAddressController(DeliveryAddressService deliveryAddressService) {
        this.deliveryAddressService = deliveryAddressService;
    }

    @GetMapping
    public List<DeliveryAddress> list(HttpSession session) {
        SysUser student = requireStudent(session);
        return deliveryAddressService.findByStudentId(student.getId());
    }

    @PostMapping
    public DeliveryAddress add(
            @RequestBody DeliveryAddress address,
            HttpSession session
    ) {
        SysUser student = requireStudent(session);
        return deliveryAddressService.add(student.getId(), address);
    }

    @PutMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestBody DeliveryAddress address,
            HttpSession session
    ) {
        SysUser student = requireStudent(session);
        deliveryAddressService.update(student.getId(), id, address);
        return "修改成功";
    }

    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable Long id,
            HttpSession session
    ) {
        SysUser student = requireStudent(session);
        deliveryAddressService.delete(student.getId(), id);
        return "删除成功";
    }

    private SysUser requireStudent(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        if (!"STUDENT".equals(user.getRoleCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有学生可以操作收货地址");
        }
        return user;
    }
}