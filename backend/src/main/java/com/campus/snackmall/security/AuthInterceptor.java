package com.campus.snackmall.security;

import com.campus.snackmall.common.enums.UserRole;
import com.campus.snackmall.common.exception.BusinessException;
import com.campus.snackmall.module.user.entity.SysUser;
import com.campus.snackmall.module.user.mapper.SysUserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final SysUserMapper sysUserMapper;

    public AuthInterceptor(JwtUtil jwtUtil, SysUserMapper sysUserMapper) {
        this.jwtUtil = jwtUtil;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        boolean requireAdmin = handlerMethod.hasMethodAnnotation(RequireAdmin.class)
                || handlerMethod.getBeanType().isAnnotationPresent(RequireAdmin.class);
        boolean requireMerchant = handlerMethod.hasMethodAnnotation(RequireMerchant.class)
                || handlerMethod.getBeanType().isAnnotationPresent(RequireMerchant.class);
        boolean requireLogin = handlerMethod.hasMethodAnnotation(RequireLogin.class)
                || handlerMethod.getBeanType().isAnnotationPresent(RequireLogin.class)
                || requireAdmin
                || requireMerchant;

        if (!requireLogin) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录或 token 已失效");
        }

        String token = authorization.substring(7);
        JwtUserInfo userInfo;
        try {
            userInfo = jwtUtil.parseToken(token);
        } catch (Exception ex) {
            throw new BusinessException(401, "未登录或 token 已失效");
        }

        SysUser user = sysUserMapper.selectById(userInfo.getUserId());
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(401, "未登录或 token 已失效");
        }

        if (requireAdmin && !UserRole.ADMIN.name().equals(userInfo.getRole())) {
            throw new BusinessException(403, "无权限");
        }

        if (requireMerchant && !UserRole.MERCHANT.name().equals(userInfo.getRole())) {
            throw new BusinessException(403, "无商家权限");
        }

        UserContext.set(userInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}