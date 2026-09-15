package com.campus.food.controller;
import jakarta.servlet.http.HttpSession;
import com.campus.food.entity.SysUser;
import com.campus.food.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController//负责接受前端请求并返回数据
@RequestMapping("/user")//意思是这个类负责处理所有的/user开头的请求（相当于提供路径）
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")//post请求，更适合提交密码
    public SysUser register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String realName,
            @RequestParam String phone,
            @RequestParam String roleCode
    ) {

        return userService.register(
                username,
                password,
                realName,
                phone,
                roleCode
        );
    }


    @PostMapping("/login")
    public SysUser login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session
    ){

        SysUser user = userService.login(
                username,
                password
        );

        session.setAttribute("loginUser", user);

        return user;
    }
}