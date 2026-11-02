package com.campus.snackmall.module.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.snackmall.common.enums.UserRole;
import com.campus.snackmall.common.exception.BusinessException;
import com.campus.snackmall.module.auth.dto.LoginRequest;
import com.campus.snackmall.module.auth.dto.RegisterRequest;
import com.campus.snackmall.module.auth.service.AuthService;
import com.campus.snackmall.module.auth.vo.LoginVO;
import com.campus.snackmall.module.auth.vo.UserInfoVO;
import com.campus.snackmall.module.user.entity.SysUser;
import com.campus.snackmall.module.user.mapper.SysUserMapper;
import com.campus.snackmall.security.JwtUtil;
import com.campus.snackmall.security.UserContext;
import java.time.LocalDateTime;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(SysUserMapper sysUserMapper, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean register(RegisterRequest request) {
        return doRegister(request, UserRole.USER);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean registerMerchant(RegisterRequest request) {
        return doRegister(request, UserRole.MERCHANT);
    }

    @Override
    public LoginVO login(LoginRequest request) {
        return doLogin(request, false, UserRole.USER);
    }

    @Override
    public LoginVO merchantLogin(LoginRequest request) {
        return doLogin(request, true, UserRole.MERCHANT);
    }

    @Override
    public LoginVO adminLogin(LoginRequest request) {
        return doLogin(request, true, UserRole.ADMIN);
    }

    @Override
    public UserInfoVO currentUser() {
        SysUser user = sysUserMapper.selectById(UserContext.getUserId());
        if (user == null) {
            throw new BusinessException(401, "未登录或token失效");
        }
        return toUserInfo(user);
    }

    @Override
    public Boolean logout() {
        return true;
    }

    private Boolean doRegister(RegisterRequest request, UserRole role) {
        Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException(409, "用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setRole(role.name());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
        return true;
    }

    private LoginVO doLogin(LoginRequest request, boolean strictRole, UserRole expectedRole) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername())
                .last("limit 1"));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(401, "账号已被禁用，请联系管理员");
        }
        if (strictRole && !expectedRole.name().equals(user.getRole())) {
            String message = switch (expectedRole) {
                case ADMIN -> "无管理员权限";
                case MERCHANT -> "无入驻用户权限";
                default -> "无权限";
            };
            throw new BusinessException(401, message);
        }

        LoginVO vo = new LoginVO();
        vo.setToken(jwtUtil.generateToken(user.getId(), user.getRole()));
        vo.setUserInfo(toUserInfo(user));
        return vo;
    }

    private UserInfoVO toUserInfo(SysUser user) {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        return vo;
    }
}
