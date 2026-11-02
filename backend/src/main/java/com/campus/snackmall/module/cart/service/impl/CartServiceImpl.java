package com.campus.snackmall.module.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.campus.snackmall.common.exception.BusinessException;
import com.campus.snackmall.module.cart.dto.CartAddRequest;
import com.campus.snackmall.module.cart.dto.CartBatchDeleteRequest;
import com.campus.snackmall.module.cart.dto.CartCheckedRequest;
import com.campus.snackmall.module.cart.dto.CartUpdateQuantityRequest;
import com.campus.snackmall.module.cart.entity.SnackCart;
import com.campus.snackmall.module.cart.mapper.SnackCartMapper;
import com.campus.snackmall.module.cart.service.CartService;
import com.campus.snackmall.module.cart.vo.CartItemVO;
import com.campus.snackmall.module.cart.vo.CartListVO;
import com.campus.snackmall.module.product.entity.SnackProduct;
import com.campus.snackmall.module.product.mapper.SnackProductMapper;
import com.campus.snackmall.security.UserContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements CartService {

    private final SnackCartMapper cartMapper;
    private final SnackProductMapper productMapper;

    public CartServiceImpl(SnackCartMapper cartMapper, SnackProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    @Override
    public CartListVO list() {
        List<SnackCart> carts = cartMapper.selectList(new LambdaQueryWrapper<SnackCart>()
                .eq(SnackCart::getUserId, UserContext.getUserId())
                .orderByDesc(SnackCart::getUpdateTime));
        List<Long> productIds = carts.stream().map(SnackCart::getProductId).distinct().toList();
        Map<Long, SnackProduct> productMap = productIds.isEmpty() ? Map.of() :
                productMapper.selectBatchIds(productIds).stream().collect(Collectors.toMap(SnackProduct::getId, Function.identity()));

        List<CartItemVO> items = carts.stream().map(item -> {
            SnackProduct product = productMap.get(item.getProductId());
            CartItemVO vo = new CartItemVO();
            vo.setId(item.getId());
            vo.setProductId(item.getProductId());
            vo.setProductName(product == null ? "商品不存在" : product.getName());
            vo.setCoverImage(product == null ? null : product.getCoverImage());
            vo.setPrice(product == null ? BigDecimal.ZERO : product.getPrice());
            vo.setQuantity(item.getQuantity());
            vo.setChecked(item.getChecked() != null && item.getChecked() == 1);
            vo.setStock(product == null ? 0 : product.getStock());
            vo.setProductStatus(product == null ? 0 : product.getStatus());
            vo.setSubtotal(vo.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return vo;
        }).toList();

        BigDecimal totalAmount = items.stream()
                .filter(item -> Boolean.TRUE.equals(item.getChecked()) && item.getProductStatus() != null && item.getProductStatus() == 1)
                .map(CartItemVO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartListVO vo = new CartListVO();
        vo.setItems(items);
        vo.setCheckedCount((int) items.stream().filter(item -> Boolean.TRUE.equals(item.getChecked())).count());
        vo.setTotalAmount(totalAmount);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean add(CartAddRequest request) {
        SnackProduct product = productMapper.selectById(request.getProductId());
        if (product == null || product.getIsDeleted() == 1 || product.getStatus() != 1) {
            throw new BusinessException(404, "商品不存在或已下架");
        }
        SnackCart existed = cartMapper.selectOne(new LambdaQueryWrapper<SnackCart>()
                .eq(SnackCart::getUserId, UserContext.getUserId())
                .eq(SnackCart::getProductId, request.getProductId())
                .last("limit 1"));
        if (existed != null) {
            existed.setQuantity(existed.getQuantity() + request.getQuantity());
            existed.setUpdateTime(LocalDateTime.now());
            cartMapper.updateById(existed);
            return true;
        }
        SnackCart cart = new SnackCart();
        cart.setUserId(UserContext.getUserId());
        cart.setProductId(request.getProductId());
        cart.setQuantity(request.getQuantity());
        cart.setChecked(1);
        cart.setCreateTime(LocalDateTime.now());
        cart.setUpdateTime(LocalDateTime.now());
        cartMapper.insert(cart);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateQuantity(Long id, CartUpdateQuantityRequest request) {
        SnackCart cart = getOwnedCart(id);
        cart.setQuantity(request.getQuantity());
        cart.setUpdateTime(LocalDateTime.now());
        cartMapper.updateById(cart);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateChecked(Long id, CartCheckedRequest request) {
        SnackCart cart = getOwnedCart(id);
        cart.setChecked(Boolean.TRUE.equals(request.getChecked()) ? 1 : 0);
        cart.setUpdateTime(LocalDateTime.now());
        cartMapper.updateById(cart);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        getOwnedCart(id);
        cartMapper.deleteById(id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDelete(CartBatchDeleteRequest request) {
        for (Long id : request.getIds()) {
            getOwnedCart(id);
        }
        cartMapper.deleteBatchIds(request.getIds());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCheckedAll(CartCheckedRequest request) {
        cartMapper.update(null, new LambdaUpdateWrapper<SnackCart>()
                .eq(SnackCart::getUserId, UserContext.getUserId())
                .set(SnackCart::getChecked, Boolean.TRUE.equals(request.getChecked()) ? 1 : 0)
                .set(SnackCart::getUpdateTime, LocalDateTime.now()));
        return true;
    }

    private SnackCart getOwnedCart(Long id) {
        SnackCart cart = cartMapper.selectById(id);
        if (cart == null) {
            throw new BusinessException(404, "购物车记录不存在");
        }
        if (!UserContext.getUserId().equals(cart.getUserId())) {
            throw new BusinessException(403, "无权限");
        }
        return cart;
    }
}
