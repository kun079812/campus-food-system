package com.campus.food.controller;

import com.campus.food.entity.SysUser;
import com.campus.food.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public SysUser register(@RequestBody Map<String, String> request) {
        return userService.register(
                request.get("username"),
                request.get("password"),
                request.get("realName"),
                request.get("phone"),
                request.get("roleCode")
        );
    }

    @PostMapping("/auth/login")
    public SysUser login(
            @RequestBody Map<String, String> request,
            HttpSession session
    ) {
        SysUser user = userService.login(
                request.get("username"),
                request.get("password")
        );

        session.setAttribute("loginUser", user);
        return user;
    }

    @PostMapping("/auth/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "退出登录成功";
    }

    @GetMapping("/users/me")
    public SysUser info(HttpSession session) {
        SysUser user = requireLogin(session);
        return userService.findById(user.getId());
    }

    @PutMapping("/users/me")
    public SysUser updateInfo(
            @RequestBody SysUser requestUser,
            HttpSession session
    ) {
        SysUser user = requireLogin(session);
        SysUser updatedUser = userService.updateProfile(
                user.getId(),
                requestUser
        );
        session.setAttribute("loginUser", updatedUser);
        return updatedUser;
    }

    @PutMapping("/users/me/password")
    public String updatePassword(
            @RequestBody Map<String, String> request,
            HttpSession session
    ) {
        SysUser user = requireLogin(session);
        userService.updatePassword(
                user.getId(),
                request.get("oldPassword"),
                request.get("newPassword")
        );
        session.invalidate();
        return "密码修改成功，请重新登录";
    }

    private SysUser requireLogin(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "请先登录"
            );
        }
        return user;
    }
}