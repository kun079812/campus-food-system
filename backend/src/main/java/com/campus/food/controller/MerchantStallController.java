package com.campus.food.controller;

import com.campus.food.entity.Stall;
import com.campus.food.entity.SysUser;
import com.campus.food.service.StallService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/merchant/stall")
public class MerchantStallController {

    private final StallService stallService;

    public MerchantStallController(StallService stallService) {
        this.stallService = stallService;
    }

    @GetMapping
    public Stall info(HttpSession session) {
        SysUser merchant = requireMerchant(session);
        return stallService.getByMerchantId(merchant.getId());
    }

    @PutMapping
    public String update(
            @RequestBody Stall stall,
            HttpSession session
    ) {
        SysUser merchant = requireMerchant(session);
        stallService.updateByMerchant(merchant.getId(), stall);
        return "店铺信息修改成功";
    }

    @PutMapping("/status")
    public String updateStatus(
            @RequestParam Integer status,
            HttpSession session
    ) {
        SysUser merchant = requireMerchant(session);
        stallService.updateStatusByMerchant(merchant.getId(), status);
        return status == 1 ? "档口已恢复营业" : "档口已设为休息";
    }

    private SysUser requireMerchant(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        if (!"MERCHANT".equals(user.getRoleCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有商家可以操作店铺信息");
        }
        return user;
    }
}