package com.campus.snackmall.module.admin.controller;

import com.campus.snackmall.common.model.PageResponse;
import com.campus.snackmall.common.result.Result;
import com.campus.snackmall.module.admin.dto.AdminCategoryQueryRequest;
import com.campus.snackmall.module.admin.dto.AdminCategorySaveRequest;
import com.campus.snackmall.module.admin.dto.AdminNoticeQueryRequest;
import com.campus.snackmall.module.admin.dto.AdminNoticeSaveRequest;
import com.campus.snackmall.module.admin.dto.AdminOrderQueryRequest;
import com.campus.snackmall.module.admin.dto.AdminProductQueryRequest;
import com.campus.snackmall.module.admin.dto.AdminStatusUpdateRequest;
import com.campus.snackmall.module.admin.dto.AdminUserQueryRequest;
import com.campus.snackmall.module.admin.dto.ProductAuditRequest;
import com.campus.snackmall.module.category.service.CategoryService;
import com.campus.snackmall.module.category.vo.CategoryVO;
import com.campus.snackmall.module.notice.service.NoticeService;
import com.campus.snackmall.module.notice.vo.NoticeVO;
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
import com.campus.snackmall.module.user.service.UserService;
import com.campus.snackmall.module.user.vo.UserProfileVO;
import com.campus.snackmall.security.RequireAdmin;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequireAdmin
public class AdminController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;
    private final NoticeService noticeService;
    private final StatisticsService statisticsService;

    public AdminController(CategoryService categoryService, ProductService productService, OrderService orderService,
                           UserService userService, NoticeService noticeService, StatisticsService statisticsService) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
        this.noticeService = noticeService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/api/admin/categories")
    public Result<PageResponse<CategoryVO>> categoryPage(AdminCategoryQueryRequest request) {
        return Result.success("查询成功", categoryService.adminPage(request));
    }

    @PostMapping("/api/admin/categories")
    public Result<Boolean> createCategory(@Valid @RequestBody AdminCategorySaveRequest request) {
        return Result.success("新增成功", categoryService.create(request));
    }

    @PutMapping("/api/admin/categories/{id}")
    public Result<Boolean> updateCategory(@PathVariable Long id, @Valid @RequestBody AdminCategorySaveRequest request) {
        return Result.success("修改成功", categoryService.update(id, request));
    }

    @DeleteMapping("/api/admin/categories/{id}")
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        return Result.success("删除成功", categoryService.delete(id));
    }

    @GetMapping("/api/admin/products")
    public Result<PageResponse<ProductListVO>> productPage(AdminProductQueryRequest request) {
        return Result.success("查询成功", productService.adminPage(request));
    }

    @PutMapping("/api/admin/products/{id}/status")
    public Result<Boolean> updateProductStatus(@PathVariable Long id, @Valid @RequestBody AdminStatusUpdateRequest request) {
        return Result.success("操作成功", productService.adminUpdateStatus(id, request));
    }

    @PutMapping("/api/admin/products/{id}/audit")
    public Result<Boolean> auditProduct(@PathVariable Long id, @Valid @RequestBody ProductAuditRequest request) {
        return Result.success("审核成功", productService.adminAudit(id, request));
    }

    @GetMapping("/api/admin/orders")
    public Result<PageResponse<OrderListVO>> orderPage(AdminOrderQueryRequest request) {
        return Result.success("查询成功", orderService.adminPage(request));
    }

    @GetMapping("/api/admin/orders/{id}")
    public Result<OrderDetailVO> orderDetail(@PathVariable Long id) {
        return Result.success("查询成功", orderService.detail(id, true));
    }

    @PutMapping("/api/admin/orders/{id}/status")
    public Result<Boolean> updateOrderStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateRequest request) {
        return Result.success("状态更新成功", orderService.adminUpdateStatus(id, request));
    }

    @GetMapping("/api/admin/users")
    public Result<PageResponse<UserProfileVO>> userPage(AdminUserQueryRequest request) {
        return Result.success("查询成功", userService.adminPage(request));
    }

    @PutMapping("/api/admin/users/{id}/status")
    public Result<Boolean> updateUserStatus(@PathVariable Long id, @Valid @RequestBody AdminStatusUpdateRequest request) {
        return Result.success("操作成功", userService.updateUserStatus(id, request));
    }

    @GetMapping("/api/admin/notices")
    public Result<PageResponse<NoticeVO>> noticePage(AdminNoticeQueryRequest request) {
        return Result.success("查询成功", noticeService.adminPage(request));
    }

    @PostMapping("/api/admin/notices")
    public Result<Boolean> createNotice(@Valid @RequestBody AdminNoticeSaveRequest request) {
        return Result.success("新增成功", noticeService.create(request));
    }

    @PutMapping("/api/admin/notices/{id}")
    public Result<Boolean> updateNotice(@PathVariable Long id, @Valid @RequestBody AdminNoticeSaveRequest request) {
        return Result.success("修改成功", noticeService.update(id, request));
    }

    @DeleteMapping("/api/admin/notices/{id}")
    public Result<Boolean> deleteNotice(@PathVariable Long id) {
        return Result.success("删除成功", noticeService.delete(id));
    }

    @GetMapping("/api/admin/statistics/overview")
    public Result<StatisticsOverviewVO> overview() {
        return Result.success("查询成功", statisticsService.overview());
    }

    @GetMapping("/api/admin/statistics/hot-products")
    public Result<List<HotProductStatsVO>> hotProducts(@RequestParam(required = false) Integer limit) {
        return Result.success("查询成功", statisticsService.hotProducts(limit));
    }

    @GetMapping("/api/admin/statistics/order-trend")
    public Result<List<OrderTrendVO>> orderTrend() {
        return Result.success("查询成功", statisticsService.orderTrend());
    }
}