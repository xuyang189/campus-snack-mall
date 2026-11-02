package com.campus.snackmall.module.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.snackmall.common.enums.OrderStatus;
import com.campus.snackmall.common.exception.BusinessException;
import com.campus.snackmall.common.model.PageResponse;
import com.campus.snackmall.module.admin.dto.AdminOrderQueryRequest;
import com.campus.snackmall.module.cart.entity.SnackCart;
import com.campus.snackmall.module.cart.mapper.SnackCartMapper;
import com.campus.snackmall.module.order.dto.CreateOrderRequest;
import com.campus.snackmall.module.order.dto.OrderQueryRequest;
import com.campus.snackmall.module.order.dto.OrderStatusUpdateRequest;
import com.campus.snackmall.module.order.entity.SnackOrder;
import com.campus.snackmall.module.order.entity.SnackOrderItem;
import com.campus.snackmall.module.order.mapper.SnackOrderItemMapper;
import com.campus.snackmall.module.order.mapper.SnackOrderMapper;
import com.campus.snackmall.module.order.service.OrderService;
import com.campus.snackmall.module.order.vo.OrderDetailVO;
import com.campus.snackmall.module.order.vo.OrderItemVO;
import com.campus.snackmall.module.order.vo.OrderListVO;
import com.campus.snackmall.module.product.entity.SnackProduct;
import com.campus.snackmall.module.product.mapper.SnackProductMapper;
import com.campus.snackmall.module.user.entity.SysUser;
import com.campus.snackmall.module.user.mapper.SysUserMapper;
import com.campus.snackmall.security.UserContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class OrderServiceImpl implements OrderService {

    private final SnackOrderMapper orderMapper;
    private final SnackOrderItemMapper orderItemMapper;
    private final SnackCartMapper cartMapper;
    private final SnackProductMapper productMapper;
    private final SysUserMapper userMapper;

    public OrderServiceImpl(SnackOrderMapper orderMapper, SnackOrderItemMapper orderItemMapper, SnackCartMapper cartMapper,
                            SnackProductMapper productMapper, SysUserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> create(CreateOrderRequest request) {
        List<SnackCart> carts = cartMapper.selectBatchIds(request.getCartIds());
        if (carts.isEmpty()) {
            throw new BusinessException(400, "购物车不能为空");
        }
        for (SnackCart cart : carts) {
            if (!UserContext.getUserId().equals(cart.getUserId())) {
                throw new BusinessException(403, "无权限");
            }
        }
        List<SnackCart> checkedCarts = carts.stream().filter(item -> item.getChecked() != null && item.getChecked() == 1).toList();
        if (checkedCarts.isEmpty()) {
            throw new BusinessException(400, "未选择结算商品");
        }

        List<Long> productIds = checkedCarts.stream().map(SnackCart::getProductId).distinct().toList();
        Map<Long, SnackProduct> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(SnackProduct::getId, Function.identity()));

        BigDecimal totalAmount = BigDecimal.ZERO;
        String orderNo = generateOrderNo();

        SnackOrder order = new SnackOrder();
        order.setOrderNo(orderNo);
        order.setUserId(UserContext.getUserId());
        order.setStatus(OrderStatus.CREATED.getCode());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setRemark(request.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);

        for (SnackCart cart : checkedCarts) {
            SnackProduct product = productMap.get(cart.getProductId());
            if (product == null || product.getStatus() != 1 || product.getAuditStatus() != 1 || product.getIsDeleted() == 1) {
                throw new BusinessException(404, "商品不存在或暂不可购买");
            }
            if (product.getStock() < cart.getQuantity()) {
                throw new BusinessException(409, "库存不足");
            }
            BigDecimal amount = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(amount);

            SnackOrderItem item = new SnackOrderItem();
            item.setOrderId(order.getId());
            item.setOrderNo(orderNo);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getCoverImage());
            item.setPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setAmount(amount);
            item.setCreateTime(LocalDateTime.now());
            orderItemMapper.insert(item);

            product.setStock(product.getStock() - cart.getQuantity());
            product.setSales((product.getSales() == null ? 0 : product.getSales()) + cart.getQuantity());
            product.setUpdateTime(LocalDateTime.now());
            productMapper.updateById(product);
        }

        order.setTotalAmount(totalAmount);
        orderMapper.updateById(order);
        cartMapper.deleteBatchIds(checkedCarts.stream().map(SnackCart::getId).toList());
        return Map.of("orderNo", orderNo);
    }

    @Override
    public PageResponse<OrderListVO> page(OrderQueryRequest request) {
        Page<SnackOrder> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<SnackOrder> wrapper = new LambdaQueryWrapper<SnackOrder>()
                .eq(SnackOrder::getUserId, UserContext.getUserId());
        if (request.getStatus() != null) {
            wrapper.eq(SnackOrder::getStatus, request.getStatus());
        }
        wrapper.orderByDesc(SnackOrder::getCreateTime);
        Page<SnackOrder> result = orderMapper.selectPage(page, wrapper);
        List<OrderListVO> records = result.getRecords().stream().map(order -> toListVO(order, null, null)).toList();
        return new PageResponse<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public OrderDetailVO detail(Long id, boolean adminView) {
        SnackOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!adminView && !UserContext.getUserId().equals(order.getUserId())) {
            throw new BusinessException(403, "无权限");
        }
        return buildOrderDetail(order, listOrderItems(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancel(Long id) {
        SnackOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!UserContext.getUserId().equals(order.getUserId())) {
            throw new BusinessException(403, "无权限");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(409, "订单状态不允许此操作");
        }
        revertOrder(order);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirm(Long id) {
        SnackOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!UserContext.getUserId().equals(order.getUserId())) {
            throw new BusinessException(403, "无权限");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException(409, "订单状态不允许此操作");
        }
        order.setStatus(OrderStatus.FINISHED.getCode());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return true;
    }

    @Override
    public PageResponse<OrderListVO> adminPage(AdminOrderQueryRequest request) {
        Page<SnackOrder> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<SnackOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getOrderNo())) {
            wrapper.eq(SnackOrder::getOrderNo, request.getOrderNo());
        }
        if (request.getStatus() != null) {
            wrapper.eq(SnackOrder::getStatus, request.getStatus());
        }
        wrapper.orderByDesc(SnackOrder::getCreateTime);
        Page<SnackOrder> result = orderMapper.selectPage(page, wrapper);
        Map<Long, SysUser> userMap = loadUsers(result.getRecords().stream().map(SnackOrder::getUserId).distinct().toList());
        List<OrderListVO> records = result.getRecords().stream()
                .map(order -> toListVO(order, userMap.get(order.getUserId()), null)).toList();
        return new PageResponse<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public OrderDetailVO merchantDetail(Long id) {
        SnackOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        List<SnackOrderItem> merchantItems = listMerchantItemsByOrderId(id, UserContext.getUserId());
        if (merchantItems.isEmpty()) {
            throw new BusinessException(403, "只能查看自己的订单商品");
        }
        return buildOrderDetail(order, merchantItems);
    }

    @Override
    public PageResponse<OrderListVO> merchantPage(AdminOrderQueryRequest request) {
        Long merchantId = UserContext.getUserId();
        Map<Long, List<SnackOrderItem>> merchantItemsMap = listMerchantItemsMap(merchantId);
        if (merchantItemsMap.isEmpty()) {
            return new PageResponse<>(Collections.emptyList(), 0, request.getCurrent(), request.getSize());
        }

        List<SnackOrder> orders = orderMapper.selectBatchIds(merchantItemsMap.keySet()).stream()
                .filter(order -> order != null)
                .filter(order -> !StringUtils.hasText(request.getOrderNo()) || request.getOrderNo().equals(order.getOrderNo()))
                .filter(order -> request.getStatus() == null || request.getStatus().equals(order.getStatus()))
                .sorted(Comparator.comparing(SnackOrder::getCreateTime).reversed())
                .toList();

        int fromIndex = Math.min((int) ((request.getCurrent() - 1) * request.getSize()), orders.size());
        int toIndex = Math.min(fromIndex + request.getSize().intValue(), orders.size());
        List<SnackOrder> pageRecords = orders.subList(fromIndex, toIndex);
        Map<Long, SysUser> userMap = loadUsers(pageRecords.stream().map(SnackOrder::getUserId).distinct().toList());
        List<OrderListVO> records = pageRecords.stream()
                .map(order -> toListVO(order, userMap.get(order.getUserId()), sumItems(merchantItemsMap.get(order.getId()))))
                .toList();
        return new PageResponse<>(records, (long) orders.size(), request.getCurrent(), request.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean merchantUpdateStatus(Long id, OrderStatusUpdateRequest request) {
        List<SnackOrderItem> merchantItems = listMerchantItemsByOrderId(id, UserContext.getUserId());
        if (merchantItems.isEmpty()) {
            throw new BusinessException(403, "只能操作自己的订单商品");
        }
        return adminUpdateStatus(id, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean adminUpdateStatus(Long id, OrderStatusUpdateRequest request) {
        SnackOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        validateStatusTransition(order.getStatus(), request.getStatus());
        if (request.getStatus() == 4 && order.getStatus() != 4) {
            revertOrder(order);
            return true;
        }
        order.setStatus(request.getStatus());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return true;
    }

    private void revertOrder(SnackOrder order) {
        List<SnackOrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<SnackOrderItem>()
                .eq(SnackOrderItem::getOrderId, order.getId()));
        for (SnackOrderItem item : items) {
            SnackProduct product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock((product.getStock() == null ? 0 : product.getStock()) + item.getQuantity());
                product.setSales(Math.max(0, (product.getSales() == null ? 0 : product.getSales()) - item.getQuantity()));
                product.setUpdateTime(LocalDateTime.now());
                productMapper.updateById(product);
            }
        }
        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    private void validateStatusTransition(Integer currentStatus, Integer targetStatus) {
        boolean valid = (currentStatus == 1 && (targetStatus == 2 || targetStatus == 4))
                || (currentStatus == 2 && (targetStatus == 3 || targetStatus == 4));
        if (!valid) {
            throw new BusinessException(409, "订单状态流转不合法");
        }
    }

    private OrderListVO toListVO(SnackOrder order, SysUser user, BigDecimal totalAmountOverride) {
        OrderListVO vo = new OrderListVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(totalAmountOverride == null ? order.getTotalAmount() : totalAmountOverride);
        vo.setStatus(order.getStatus());
        vo.setStatusText(OrderStatus.getText(order.getStatus()));
        vo.setCreateTime(order.getCreateTime());
        if (user != null) {
            vo.setUserId(user.getId());
            vo.setUsername(user.getUsername());
        }
        return vo;
    }

    private OrderDetailVO buildOrderDetail(SnackOrder order, List<SnackOrderItem> items) {
        SysUser user = userMapper.selectById(order.getUserId());
        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setUsername(user == null ? null : user.getUsername());
        vo.setTotalAmount(sumItems(items));
        vo.setStatus(order.getStatus());
        vo.setStatusText(OrderStatus.getText(order.getStatus()));
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setItems(items.stream().map(this::toItemVO).toList());
        return vo;
    }

    private OrderItemVO toItemVO(SnackOrderItem item) {
        OrderItemVO vo = new OrderItemVO();
        vo.setProductId(item.getProductId());
        vo.setProductName(item.getProductName());
        vo.setProductImage(item.getProductImage());
        vo.setPrice(item.getPrice());
        vo.setQuantity(item.getQuantity());
        vo.setAmount(item.getAmount());
        return vo;
    }

    private List<SnackOrderItem> listOrderItems(Long orderId) {
        return orderItemMapper.selectList(new LambdaQueryWrapper<SnackOrderItem>().eq(SnackOrderItem::getOrderId, orderId));
    }

    private List<SnackOrderItem> listMerchantItemsByOrderId(Long orderId, Long merchantId) {
        return listMerchantItemsMap(merchantId).getOrDefault(orderId, Collections.emptyList());
    }

    private Map<Long, List<SnackOrderItem>> listMerchantItemsMap(Long merchantId) {
        List<Long> productIds = productMapper.selectList(new LambdaQueryWrapper<SnackProduct>()
                        .eq(SnackProduct::getMerchantId, merchantId)
                        .eq(SnackProduct::getIsDeleted, 0))
                .stream().map(SnackProduct::getId).toList();
        if (productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return orderItemMapper.selectList(new LambdaQueryWrapper<SnackOrderItem>().in(SnackOrderItem::getProductId, productIds))
                .stream().collect(Collectors.groupingBy(SnackOrderItem::getOrderId, LinkedHashMap::new, Collectors.toList()));
    }

    private Map<Long, SysUser> loadUsers(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(SysUser::getId, Function.identity()));
    }

    private BigDecimal sumItems(List<SnackOrderItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream().map(SnackOrderItem::getAmount).filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String generateOrderNo() {
        return "SN" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }
}