package com.campus.snackmall.module.order.service;

import com.campus.snackmall.common.model.PageResponse;
import com.campus.snackmall.module.admin.dto.AdminOrderQueryRequest;
import com.campus.snackmall.module.order.dto.CreateOrderRequest;
import com.campus.snackmall.module.order.dto.OrderQueryRequest;
import com.campus.snackmall.module.order.dto.OrderStatusUpdateRequest;
import com.campus.snackmall.module.order.vo.OrderDetailVO;
import com.campus.snackmall.module.order.vo.OrderListVO;
import java.util.Map;

public interface OrderService {

    Map<String, String> create(CreateOrderRequest request);

    PageResponse<OrderListVO> page(OrderQueryRequest request);

    OrderDetailVO detail(Long id, boolean adminView);

    Boolean cancel(Long id);

    Boolean confirm(Long id);

    PageResponse<OrderListVO> adminPage(AdminOrderQueryRequest request);

    OrderDetailVO merchantDetail(Long id);

    PageResponse<OrderListVO> merchantPage(AdminOrderQueryRequest request);

    Boolean merchantUpdateStatus(Long id, OrderStatusUpdateRequest request);

    Boolean adminUpdateStatus(Long id, OrderStatusUpdateRequest request);
}