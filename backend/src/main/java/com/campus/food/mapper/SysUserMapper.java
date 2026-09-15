package com.campus.food.mapper;

import com.campus.food.entity.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper//操作数据库的接口提示
public interface SysUserMapper {

    @Select("""
            SELECT *
            FROM sys_user
            WHERE username = #{username}
            """)
    SysUser findByUsername(String username);


    @Select("""
            SELECT *
            FROM sys_user
            WHERE phone = #{phone}
            """)
    SysUser findByPhone(String phone);


    @Insert("""
            INSERT INTO sys_user
            (username, password_hash, real_name, phone, role_code, status)
            VALUES
            (#{username}, #{passwordHash}, #{realName}, #{phone}, #{roleCode}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")//MySQL 自动生成 id 以后，把这个 ID 再传回 Java 对象。
    int insert(SysUser user);//成功影响了几行数据
}