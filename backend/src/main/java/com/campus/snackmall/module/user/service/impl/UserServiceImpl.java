package com.campus.snackmall.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.snackmall.common.exception.BusinessException;
import com.campus.snackmall.common.model.PageResponse;
import com.campus.snackmall.module.admin.dto.AdminStatusUpdateRequest;
import com.campus.snackmall.module.admin.dto.AdminUserQueryRequest;
import com.campus.snackmall.module.user.dto.UpdatePasswordRequest;
import com.campus.snackmall.module.user.dto.UpdateProfileRequest;
import com.campus.snackmall.module.user.entity.SysUser;
import com.campus.snackmall.module.user.mapper.SysUserMapper;
import com.campus.snackmall.module.user.service.UserService;
import com.campus.snackmall.module.user.vo.UserProfileVO;
import com.campus.snackmall.security.UserContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(SysUserMapper sysUserMapper, BCryptPasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserProfileVO getProfile() {
        return toProfile(getCurrentUser());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProfile(UpdateProfileRequest request) {
        SysUser user = getCurrentUser();
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(user);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePassword(UpdatePasswordRequest request) {
        SysUser user = getCurrentUser();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(400, "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(user);
        return true;
    }

    @Override
    public PageResponse<UserProfileVO> adminPage(AdminUserQueryRequest request) {
        Page<SysUser> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w.like(SysUser::getUsername, request.getKeyword())
                    .or().like(SysUser::getNickname, request.getKeyword())
                    .or().like(SysUser::getPhone, request.getKeyword()));
        }
        if (request.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, request.getStatus());
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = sysUserMapper.selectPage(page, wrapper);
        List<UserProfileVO> records = result.getRecords().stream().map(this::toProfile).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserStatus(Long id, AdminStatusUpdateRequest request) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setStatus(request.getStatus());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(user);
        return true;
    }

    private SysUser getCurrentUser() {
        SysUser user = sysUserMapper.selectById(UserContext.getUserId());
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private UserProfileVO toProfile(SysUser user) {
        UserProfileVO vo = new UserProfileVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
