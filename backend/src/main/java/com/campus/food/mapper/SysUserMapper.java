package com.campus.food.mapper;

import com.campus.food.entity.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
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

    @Select("""
            SELECT *
            FROM sys_user
            WHERE id = #{id}
            """)
    SysUser findById(@Param("id") Long id);

    @Insert("""
            INSERT INTO sys_user
            (username, password_hash, real_name, phone, role_code, status)
            VALUES
            (#{username}, #{passwordHash}, #{realName},
             #{phone}, #{roleCode}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser user);

    @Update("""
            UPDATE sys_user
            SET real_name = #{realName},
                phone = #{phone}
            WHERE id = #{id}
            """)
    int updateProfile(SysUser user);
    @Update("""
            UPDATE sys_user
            SET password_hash = #{passwordHash}
            WHERE id = #{id}
            """)
    int updatePassword(
            @Param("id") Long id,
            @Param("passwordHash") String passwordHash
    );
}
