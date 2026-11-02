package com.campus.snackmall.module.merchant.controller;

import com.campus.snackmall.common.model.PageResponse;
import com.campus.snackmall.common.result.Result;
import com.campus.snackmall.module.admin.dto.AdminOrderQueryRequest;
import com.campus.snackmall.module.admin.dto.AdminProductQueryRequest;
import com.campus.snackmall.module.admin.dto.AdminProductSaveRequest;
import com.campus.snackmall.module.admin.dto.AdminStatusUpdateRequest;
import com.campus.snackmall.module.order.dto.OrderStatusUpdateRequest;
import com.campus.snackmall.module.order.service.OrderService;
import com.campus.snackmall.module.order.vo.OrderDetailVO;
import com.campus.snackmall.module.order.vo.OrderListVO;
import com.campus.snackmall.module.product.service.ProductService;
import com.campus.snackmall.module.product.vo.ProductListVO;
import com.campus.snackmall.module.statistics.service.StatisticsService;
import com.campus.snackmall.module.statistics.vo.HotProductStatsVO;
import com.campus.snackmall.module.statistics.vo.OrderTrendVO;
import com.campus.snackmall.module.statistics.vo.StatisticsOverviewVO;
import com.campus.snackmall.security.RequireMerchant;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/merchant")
@RequireMerchant
public class MerchantController {

    private final ProductService productService;
    private final OrderService orderService;
    private final StatisticsService statisticsService;

    public MerchantController(ProductService productService, OrderService orderService, StatisticsService statisticsService) {
        this.productService = productService;
        this.orderService = orderService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/products")
    public Result<PageResponse<ProductListVO>> productPage(AdminProductQueryRequest request) {
        return Result.success("查询成功", productService.merchantPage(request));
    }

    @PostMapping("/products")
    public Result<Boolean> createProduct(@Valid @RequestBody AdminProductSaveRequest request) {
        return Result.success("商品已上架", productService.merchantCreate(request));
    }

    @PutMapping("/products/{id}")
    public Result<Boolean> updateProduct(@PathVariable Long id, @Valid @RequestBody AdminProductSaveRequest request) {
        return Result.success("商品已保存", productService.merchantUpdate(id, request));
    }

    @DeleteMapping("/products/{id}")
    public Result<Boolean> deleteProduct(@PathVariable Long id) {
        return Result.success("删除成功", productService.merchantDelete(id));
    }

    @PutMapping("/products/{id}/status")
    public Result<Boolean> updateProductStatus(@PathVariable Long id, @Valid @RequestBody AdminStatusUpdateRequest request) {
        return Result.success("操作成功", productService.merchantUpdateStatus(id, request));
    }

    @GetMapping("/orders")
    public Result<PageResponse<OrderListVO>> orderPage(AdminOrderQueryRequest request) {
        return Result.success("查询成功", orderService.merchantPage(request));
    }

    @GetMapping("/orders/{id}")
    public Result<OrderDetailVO> orderDetail(@PathVariable Long id) {
        return Result.success("查询成功", orderService.merchantDetail(id));
    }

    @PutMapping("/orders/{id}/status")
    public Result<Boolean> updateOrderStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateRequest request) {
        return Result.success("状态更新成功", orderService.merchantUpdateStatus(id, request));
    }

    @GetMapping("/statistics/overview")
    public Result<StatisticsOverviewVO> overview() {
        return Result.success("查询成功", statisticsService.overview());
    }

    @GetMapping("/statistics/hot-products")
    public Result<List<HotProductStatsVO>> hotProducts(@RequestParam(required = false) Integer limit) {
        return Result.success("查询成功", statisticsService.hotProducts(limit));
    }

    @GetMapping("/statistics/order-trend")
    public Result<List<OrderTrendVO>> orderTrend() {
        return Result.success("查询成功", statisticsService.orderTrend());
    }
}