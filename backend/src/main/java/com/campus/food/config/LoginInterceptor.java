package com.campus.food.config;

import com.campus.food.common.ApiResponse;
import com.campus.food.entity.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public LoginInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        HttpSession session = request.getSession(false);
        SysUser user = session == null
                ? null
                : (SysUser) session.getAttribute("loginUser");

        if (user == null) {
            writeError(response, 401, "请先登录");
            return false;
        }

        if (!"MERCHANT".equals(user.getRoleCode())) {
            writeError(response, 403, "没有商家权限");
            return false;
        }

        return true;
    }

    private void writeError(
            HttpServletResponse response,
            int code,
            String message
    ) throws Exception {
        response.setStatus(code);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(
                response.getWriter(),
                ApiResponse.failure(code, message)
        );
    }
}