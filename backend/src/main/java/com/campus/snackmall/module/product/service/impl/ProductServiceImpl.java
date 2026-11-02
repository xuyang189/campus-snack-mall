package com.campus.snackmall.module.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.snackmall.common.exception.BusinessException;
import com.campus.snackmall.common.model.PageResponse;
import com.campus.snackmall.module.admin.dto.AdminProductQueryRequest;
import com.campus.snackmall.module.admin.dto.AdminProductSaveRequest;
import com.campus.snackmall.module.admin.dto.AdminStatusUpdateRequest;
import com.campus.snackmall.module.admin.dto.ProductAuditRequest;
import com.campus.snackmall.module.category.entity.SnackCategory;
import com.campus.snackmall.module.category.mapper.SnackCategoryMapper;
import com.campus.snackmall.module.order.entity.SnackOrderItem;
import com.campus.snackmall.module.order.mapper.SnackOrderItemMapper;
import com.campus.snackmall.module.product.dto.ProductQueryRequest;
import com.campus.snackmall.module.product.entity.SnackProduct;
import com.campus.snackmall.module.product.mapper.SnackProductMapper;
import com.campus.snackmall.module.product.service.ProductService;
import com.campus.snackmall.module.product.vo.ProductDetailVO;
import com.campus.snackmall.module.product.vo.ProductListVO;
import com.campus.snackmall.module.product.vo.RecommendProductVO;
import com.campus.snackmall.module.user.entity.SysUser;
import com.campus.snackmall.module.user.mapper.SysUserMapper;
import com.campus.snackmall.security.UserContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ProductServiceImpl implements ProductService {

    private static final int PRODUCT_DOWN = 0;
    private static final int PRODUCT_UP = 1;
    private static final int AUDIT_PENDING = 0;
    private static final int AUDIT_APPROVED = 1;
    private static final int AUDIT_REJECTED = 2;

    private final SnackProductMapper productMapper;
    private final SnackCategoryMapper categoryMapper;
    private final SnackOrderItemMapper orderItemMapper;
    private final SysUserMapper userMapper;

    public ProductServiceImpl(SnackProductMapper productMapper, SnackCategoryMapper categoryMapper,
                              SnackOrderItemMapper orderItemMapper, SysUserMapper userMapper) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.orderItemMapper = orderItemMapper;
        this.userMapper = userMapper;
    }

    @Override
    public PageResponse<ProductListVO> publicPage(ProductQueryRequest request) {
        Page<SnackProduct> page = new Page<>(request.getCurrent(), request.getSize());
        Page<SnackProduct> result = productMapper.selectPage(page, buildProductWrapper(request.getKeyword(),
                request.getCategoryId(), request.getMinPrice(), request.getMaxPrice(), PRODUCT_UP,
                AUDIT_APPROVED, null, true));
        return toPageResponse(result);
    }

    @Override
    public ProductDetailVO detail(Long id) {
        SnackProduct product = productMapper.selectById(id);
        if (product == null || isDeleted(product) || !isPublicVisible(product)) {
            throw new BusinessException(404, "商品不存在");
        }
        return toDetailVO(product, categoryMapper.selectById(product.getCategoryId()));
    }

    @Override
    public List<RecommendProductVO> hotList() {
        return productMapper.selectList(new LambdaQueryWrapper<SnackProduct>()
                        .eq(SnackProduct::getStatus, PRODUCT_UP)
                        .eq(SnackProduct::getAuditStatus, AUDIT_APPROVED)
                        .eq(SnackProduct::getIsDeleted, 0)
                        .orderByDesc(SnackProduct::getSales)
                        .last("limit 10"))
                .stream().map(this::toRecommendVO).collect(Collectors.toList());
    }

    @Override
    public List<RecommendProductVO> latestList() {
        return productMapper.selectList(new LambdaQueryWrapper<SnackProduct>()
                        .eq(SnackProduct::getStatus, PRODUCT_UP)
                        .eq(SnackProduct::getAuditStatus, AUDIT_APPROVED)
                        .eq(SnackProduct::getIsDeleted, 0)
                        .orderByDesc(SnackProduct::getCreateTime)
                        .last("limit 10"))
                .stream().map(this::toRecommendVO).collect(Collectors.toList());
    }

    @Override
    public PageResponse<ProductListVO> adminPage(AdminProductQueryRequest request) {
        Page<SnackProduct> page = new Page<>(request.getCurrent(), request.getSize());
        Page<SnackProduct> result = productMapper.selectPage(page, buildProductWrapper(request.getKeyword(),
                request.getCategoryId(), null, null, request.getStatus(), request.getAuditStatus(), null, false));
        return toPageResponse(result);
    }

    @Override
    public PageResponse<ProductListVO> merchantPage(AdminProductQueryRequest request) {
        Page<SnackProduct> page = new Page<>(request.getCurrent(), request.getSize());
        Page<SnackProduct> result = productMapper.selectPage(page, buildProductWrapper(request.getKeyword(),
                request.getCategoryId(), null, null, request.getStatus(), request.getAuditStatus(), UserContext.getUserId(), false));
        return toPageResponse(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean merchantCreate(AdminProductSaveRequest request) {
        SnackProduct product = new SnackProduct();
        fillProduct(product, request);
        product.setMerchantId(UserContext.getUserId());
        product.setSales(0);
        // 商家提交后直接上架：不再等待管理员审核，审核状态自动置为通过
        product.setStatus(PRODUCT_UP);
        product.setAuditStatus(AUDIT_APPROVED);
        product.setIsDeleted(0);
        product.setVersion(0);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.insert(product);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean merchantUpdate(Long id, AdminProductSaveRequest request) {
        SnackProduct product = getMerchantOwnedProduct(id);
        boolean wasPending = !isAuditApproved(product.getAuditStatus());
        fillProduct(product, request);
        product.setAuditStatus(AUDIT_APPROVED);
        // 历史遗留的待审核商品，编辑后直接上架；其余保持原有上架状态（被管理员下架的仍保持下架）
        if (wasPending) {
            product.setStatus(PRODUCT_UP);
        }
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean merchantDelete(Long id) {
        SnackProduct product = getMerchantOwnedProduct(id);
        ensureNoOrderRecord(id);
        product.setIsDeleted(1);
        product.setStatus(PRODUCT_DOWN);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean merchantUpdateStatus(Long id, AdminStatusUpdateRequest request) {
        SnackProduct product = getMerchantOwnedProduct(id);
        Integer targetStatus = request.getStatus();
        if (targetStatus == null || (targetStatus != PRODUCT_UP && targetStatus != PRODUCT_DOWN)) {
            throw new BusinessException(400, "商品状态不合法");
        }
        // 商家自助上架：上架时自动视为审核通过
        if (targetStatus == PRODUCT_UP) {
            product.setAuditStatus(AUDIT_APPROVED);
        }
        product.setStatus(targetStatus);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean adminUpdateStatus(Long id, AdminStatusUpdateRequest request) {
        SnackProduct product = getProductOrThrow(id);
        if (!isAuditApproved(product.getAuditStatus()) && request.getStatus() == PRODUCT_UP) {
            throw new BusinessException(409, "商品未审核通过，不能直接上架");
        }
        product.setStatus(request.getStatus());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean adminAudit(Long id, ProductAuditRequest request) {
        SnackProduct product = getProductOrThrow(id);
        Integer targetStatus = request.getAuditStatus();
        if (targetStatus == null || (targetStatus != AUDIT_APPROVED && targetStatus != AUDIT_REJECTED)) {
            throw new BusinessException(400, "审核状态不合法");
        }
        product.setAuditStatus(targetStatus);
        product.setStatus(targetStatus == AUDIT_APPROVED ? PRODUCT_UP : PRODUCT_DOWN);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        return true;
    }

    private LambdaQueryWrapper<SnackProduct> buildProductWrapper(String keyword, Long categoryId, String minPrice,
                                                                 String maxPrice, Integer status, Integer auditStatus,
                                                                 Long merchantId, boolean publicView) {
        LambdaQueryWrapper<SnackProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SnackProduct::getIsDeleted, 0);
        if (publicView) {
            wrapper.eq(SnackProduct::getStatus, PRODUCT_UP)
                    .eq(SnackProduct::getAuditStatus, AUDIT_APPROVED);
        } else {
            if (status != null) {
                wrapper.eq(SnackProduct::getStatus, status);
            }
            if (auditStatus != null) {
                wrapper.eq(SnackProduct::getAuditStatus, auditStatus);
            }
            if (merchantId != null) {
                wrapper.eq(SnackProduct::getMerchantId, merchantId);
            }
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SnackProduct::getName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(SnackProduct::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(minPrice)) {
            wrapper.ge(SnackProduct::getPrice, new BigDecimal(minPrice));
        }
        if (StringUtils.hasText(maxPrice)) {
            wrapper.le(SnackProduct::getPrice, new BigDecimal(maxPrice));
        }
        wrapper.orderByDesc(SnackProduct::getCreateTime);
        return wrapper;
    }

    private PageResponse<ProductListVO> toPageResponse(Page<SnackProduct> page) {
        List<Long> categoryIds = page.getRecords().stream().map(SnackProduct::getCategoryId).distinct().toList();
        List<Long> merchantIds = page.getRecords().stream().map(SnackProduct::getMerchantId).filter(id -> id != null).distinct().toList();
        Map<Long, SnackCategory> categoryMap = categoryIds.isEmpty() ? Collections.emptyMap()
                : categoryMapper.selectBatchIds(categoryIds).stream().collect(Collectors.toMap(SnackCategory::getId, Function.identity()));
        Map<Long, SysUser> merchantMap = merchantIds.isEmpty() ? Collections.emptyMap()
                : userMapper.selectBatchIds(merchantIds).stream().collect(Collectors.toMap(SysUser::getId, Function.identity()));
        List<ProductListVO> records = page.getRecords().stream()
                .map(item -> toListVO(item, categoryMap.get(item.getCategoryId()), merchantMap.get(item.getMerchantId())))
                .collect(Collectors.toList());
        return new PageResponse<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    private void fillProduct(SnackProduct product, AdminProductSaveRequest request) {
        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setCoverImage(request.getCoverImage());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setDescription(request.getDescription());
    }

    private ProductListVO toListVO(SnackProduct product, SnackCategory category, SysUser merchant) {
        ProductListVO vo = new ProductListVO();
        vo.setId(product.getId());
        vo.setCategoryId(product.getCategoryId());
        vo.setCategoryName(category == null ? null : category.getName());
        vo.setMerchantId(product.getMerchantId());
        vo.setMerchantName(merchant == null ? null : (StringUtils.hasText(merchant.getNickname()) ? merchant.getNickname() : merchant.getUsername()));
        vo.setName(product.getName());
        vo.setCoverImage(product.getCoverImage());
        vo.setPrice(product.getPrice());
        vo.setStock(product.getStock());
        vo.setSales(product.getSales());
        vo.setDescription(product.getDescription());
        vo.setStatus(product.getStatus());
        vo.setAuditStatus(product.getAuditStatus());
        vo.setCreateTime(product.getCreateTime());
        return vo;
    }

    private ProductDetailVO toDetailVO(SnackProduct product, SnackCategory category) {
        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(product.getId());
        vo.setCategoryId(product.getCategoryId());
        vo.setCategoryName(category == null ? null : category.getName());
        vo.setMerchantId(product.getMerchantId());
        vo.setName(product.getName());
        vo.setCoverImage(product.getCoverImage());
        vo.setPrice(product.getPrice());
        vo.setStock(product.getStock());
        vo.setSales(product.getSales());
        vo.setStatus(product.getStatus());
        vo.setAuditStatus(product.getAuditStatus());
        vo.setDescription(product.getDescription());
        return vo;
    }

    private RecommendProductVO toRecommendVO(SnackProduct product) {
        RecommendProductVO vo = new RecommendProductVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setCoverImage(product.getCoverImage());
        vo.setPrice(product.getPrice());
        vo.setSales(product.getSales());
        return vo;
    }

    private SnackProduct getMerchantOwnedProduct(Long id) {
        SnackProduct product = getProductOrThrow(id);
        if (!UserContext.getUserId().equals(product.getMerchantId())) {
            throw new BusinessException(403, "只能操作自己的商品");
        }
        return product;
    }

    private SnackProduct getProductOrThrow(Long id) {
        SnackProduct product = productMapper.selectById(id);
        if (product == null || isDeleted(product)) {
            throw new BusinessException(404, "商品不存在");
        }
        return product;
    }

    private void ensureNoOrderRecord(Long productId) {
        long orderRecordCount = orderItemMapper.selectCount(new LambdaQueryWrapper<SnackOrderItem>()
                .eq(SnackOrderItem::getProductId, productId));
        if (orderRecordCount > 0) {
            throw new BusinessException(409, "该商品已产生订单记录，不允许删除");
        }
    }

    private boolean isDeleted(SnackProduct product) {
        return product.getIsDeleted() != null && product.getIsDeleted() == 1;
    }

    private boolean isPublicVisible(SnackProduct product) {
        return product.getStatus() != null && product.getStatus() == PRODUCT_UP
                && isAuditApproved(product.getAuditStatus());
    }

    private boolean isAuditApproved(Integer auditStatus) {
        return auditStatus != null && auditStatus == AUDIT_APPROVED;
    }
}