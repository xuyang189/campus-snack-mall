package com.campus.snackmall.module.order.controller;

import com.campus.snackmall.common.model.PageResponse;
import com.campus.snackmall.common.result.Result;
import com.campus.snackmall.module.order.dto.CreateOrderRequest;
import com.campus.snackmall.module.order.dto.OrderQueryRequest;
import com.campus.snackmall.module.order.service.OrderService;
import com.campus.snackmall.module.order.vo.OrderDetailVO;
import com.campus.snackmall.module.order.vo.OrderListVO;
import com.campus.snackmall.security.RequireLogin;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequireLogin
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<Map<String, String>> create(@Valid @RequestBody CreateOrderRequest request) {
        return Result.success("下单成功", orderService.create(request));
    }

    @GetMapping
    public Result<PageResponse<OrderListVO>> page(OrderQueryRequest request) {
        return Result.success("查询成功", orderService.page(request));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        return Result.success("查询成功", orderService.detail(id, false));
    }

    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancel(@PathVariable Long id) {
        return Result.success("订单已取消", orderService.cancel(id));
    }

    @PutMapping("/{id}/confirm")
    public Result<Boolean> confirm(@PathVariable Long id) {
        return Result.success("已确认收货", orderService.confirm(id));
    }
}
