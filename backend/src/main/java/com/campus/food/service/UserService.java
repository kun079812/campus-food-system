package com.campus.food.service;

import com.campus.food.entity.SysUser;
import com.campus.food.mapper.SysUserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final SysUserMapper sysUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public SysUser register(
            String username,
            String password,
            String realName,
            String phone,
            String roleCode) {

        // 1. 检查用户名是否已经存在
        if (sysUserMapper.findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 检查手机号是否已经注册
        if (sysUserMapper.findByPhone(phone) != null) {
            throw new RuntimeException("手机号已注册");
        }

        // 3. 密码至少 8 位
        if (password == null || password.length() < 8) {
            throw new RuntimeException("密码长度不能少于8位");
        }

        // 4. 目前只允许学生和商家两种角色
        if (!"STUDENT".equals(roleCode)
                && !"MERCHANT".equals(roleCode)) {
            throw new RuntimeException("用户角色不正确");
        }

        // 5. 创建新的用户对象
        SysUser user = new SysUser();

        user.setUsername(username);

        // 原始密码不能直接存数据库，先进行 BCrypt 加密
        user.setPasswordHash(passwordEncoder.encode(password));

        user.setRealName(realName);
        user.setPhone(phone);
        user.setRoleCode(roleCode);

        // 新账号默认正常
        user.setStatus(1);

        // 6. 保存到数据库
        sysUserMapper.insert(user);

        // insert 后，MySQL 生成的 id 会自动填回 user.id
        return user;
    }

    public SysUser login(String username, String password) {

        // 1. 根据用户名查询用户
        SysUser user = sysUserMapper.findByUsername(username);

        // 2. 没有这个账号
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 检查账号是否被停用
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已停用");
        }

        // 4. 检查密码
        if (!passwordEncoder.matches(
                password,
                user.getPasswordHash())) {

            throw new RuntimeException("用户名或密码错误");
        }

        // 5. 登录成功
        return user;
    }
}