package com.campus.snackmall.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.snackmall.module.order.entity.SnackOrder;
import com.campus.snackmall.module.order.mapper.SnackOrderMapper;
import com.campus.snackmall.module.product.entity.SnackProduct;
import com.campus.snackmall.module.product.mapper.SnackProductMapper;
import com.campus.snackmall.module.statistics.service.StatisticsService;
import com.campus.snackmall.module.statistics.vo.HotProductStatsVO;
import com.campus.snackmall.module.statistics.vo.OrderTrendVO;
import com.campus.snackmall.module.statistics.vo.StatisticsOverviewVO;
import com.campus.snackmall.module.user.entity.SysUser;
import com.campus.snackmall.module.user.mapper.SysUserMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final SysUserMapper userMapper;
    private final SnackProductMapper productMapper;
    private final SnackOrderMapper orderMapper;

    public StatisticsServiceImpl(SysUserMapper userMapper, SnackProductMapper productMapper, SnackOrderMapper orderMapper) {
        this.userMapper = userMapper;
        this.productMapper = productMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public StatisticsOverviewVO overview() {
        StatisticsOverviewVO vo = new StatisticsOverviewVO();
        vo.setUserCount(userMapper.selectCount(new LambdaQueryWrapper<SysUser>()));
        vo.setProductCount(productMapper.selectCount(new LambdaQueryWrapper<SnackProduct>().eq(SnackProduct::getIsDeleted, 0)));
        vo.setOrderCount(orderMapper.selectCount(new LambdaQueryWrapper<SnackOrder>()));
        List<SnackOrder> orders = orderMapper.selectList(new LambdaQueryWrapper<>());
        vo.setSalesAmount(orders.stream().map(SnackOrder::getTotalAmount).filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        LocalDate today = LocalDate.now();
        List<SnackOrder> todayOrders = orders.stream()
                .filter(item -> item.getCreateTime() != null && item.getCreateTime().toLocalDate().isEqual(today))
                .toList();
        vo.setTodayOrderCount((long) todayOrders.size());
        vo.setTodaySalesAmount(todayOrders.stream().map(SnackOrder::getTotalAmount).filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return vo;
    }

    @Override
    public List<HotProductStatsVO> hotProducts(Integer limit) {
        int actualLimit = limit == null || limit <= 0 ? 10 : Math.min(limit, 20);
        return productMapper.selectList(new LambdaQueryWrapper<SnackProduct>()
                        .eq(SnackProduct::getIsDeleted, 0)
                        .orderByDesc(SnackProduct::getSales)
                        .last("limit " + actualLimit))
                .stream().map(item -> {
                    HotProductStatsVO vo = new HotProductStatsVO();
                    vo.setProductId(item.getId());
                    vo.setProductName(item.getName());
                    vo.setSales(item.getSales());
                    vo.setRevenue(item.getPrice() == null || item.getSales() == null
                            ? BigDecimal.ZERO
                            : item.getPrice().multiply(BigDecimal.valueOf(item.getSales())));
                    return vo;
                }).toList();
    }

    @Override
    public List<OrderTrendVO> orderTrend() {
        List<OrderTrendVO> result = new ArrayList<>();
        List<SnackOrder> orders = orderMapper.selectList(new LambdaQueryWrapper<>());
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            List<SnackOrder> dayOrders = orders.stream()
                    .filter(item -> item.getCreateTime() != null && item.getCreateTime().toLocalDate().isEqual(date))
                    .toList();
            OrderTrendVO vo = new OrderTrendVO();
            vo.setDate(date.format(DateTimeFormatter.ISO_DATE));
            vo.setOrderCount((long) dayOrders.size());
            vo.setSalesAmount(dayOrders.stream().map(SnackOrder::getTotalAmount).filter(v -> v != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            result.add(vo);
        }
        return result;
    }
}
