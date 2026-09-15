package com.campus.food.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUser {

    private Long id;
    private String username;
    private String passwordHash;
    private String realName;
    private String phone;
    private String roleCode;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}