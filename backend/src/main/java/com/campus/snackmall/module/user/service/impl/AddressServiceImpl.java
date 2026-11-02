package com.campus.snackmall.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.campus.snackmall.common.exception.BusinessException;
import com.campus.snackmall.module.user.dto.AddressSaveRequest;
import com.campus.snackmall.module.user.entity.SysUserAddress;
import com.campus.snackmall.module.user.mapper.SysUserAddressMapper;
import com.campus.snackmall.module.user.service.AddressService;
import com.campus.snackmall.module.user.vo.AddressVO;
import com.campus.snackmall.security.UserContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressServiceImpl implements AddressService {

    private final SysUserAddressMapper addressMapper;

    public AddressServiceImpl(SysUserAddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    @Override
    public List<AddressVO> list() {
        return addressMapper.selectList(new LambdaQueryWrapper<SysUserAddress>()
                        .eq(SysUserAddress::getUserId, UserContext.getUserId())
                        .orderByDesc(SysUserAddress::getIsDefault)
                        .orderByDesc(SysUserAddress::getUpdateTime))
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(AddressSaveRequest request) {
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            resetDefault();
        }
        SysUserAddress address = new SysUserAddress();
        address.setUserId(UserContext.getUserId());
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setReceiverAddress(request.getReceiverAddress());
        address.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()) ? 1 : 0);
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        addressMapper.insert(address);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(Long id, AddressSaveRequest request) {
        SysUserAddress address = getOwnedAddress(id);
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            resetDefault();
        }
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setReceiverAddress(request.getReceiverAddress());
        address.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()) ? 1 : 0);
        address.setUpdateTime(LocalDateTime.now());
        addressMapper.updateById(address);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        getOwnedAddress(id);
        addressMapper.deleteById(id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setDefault(Long id) {
        SysUserAddress address = getOwnedAddress(id);
        resetDefault();
        address.setIsDefault(1);
        address.setUpdateTime(LocalDateTime.now());
        addressMapper.updateById(address);
        return true;
    }

    private void resetDefault() {
        addressMapper.update(null, new LambdaUpdateWrapper<SysUserAddress>()
                .eq(SysUserAddress::getUserId, UserContext.getUserId())
                .set(SysUserAddress::getIsDefault, 0)
                .set(SysUserAddress::getUpdateTime, LocalDateTime.now()));
    }

    private SysUserAddress getOwnedAddress(Long id) {
        SysUserAddress address = addressMapper.selectById(id);
        if (address == null) {
            throw new BusinessException(404, "地址不存在");
        }
        if (!UserContext.getUserId().equals(address.getUserId())) {
            throw new BusinessException(403, "无权限");
        }
        return address;
    }

    private AddressVO toVO(SysUserAddress address) {
        AddressVO vo = new AddressVO();
        vo.setId(address.getId());
        vo.setReceiverName(address.getReceiverName());
        vo.setReceiverPhone(address.getReceiverPhone());
        vo.setReceiverAddress(address.getReceiverAddress());
        vo.setIsDefault(address.getIsDefault() != null && address.getIsDefault() == 1);
        return vo;
    }
}
