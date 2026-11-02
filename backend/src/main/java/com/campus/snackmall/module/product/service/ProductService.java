package com.campus.snackmall.module.product.service;

import com.campus.snackmall.common.model.PageResponse;
import com.campus.snackmall.module.admin.dto.AdminProductQueryRequest;
import com.campus.snackmall.module.admin.dto.AdminProductSaveRequest;
import com.campus.snackmall.module.admin.dto.AdminStatusUpdateRequest;
import com.campus.snackmall.module.admin.dto.ProductAuditRequest;
import com.campus.snackmall.module.product.dto.ProductQueryRequest;
import com.campus.snackmall.module.product.vo.ProductDetailVO;
import com.campus.snackmall.module.product.vo.ProductListVO;
import com.campus.snackmall.module.product.vo.RecommendProductVO;
import java.util.List;

public interface ProductService {

    PageResponse<ProductListVO> publicPage(ProductQueryRequest request);

    ProductDetailVO detail(Long id);

    List<RecommendProductVO> hotList();

    List<RecommendProductVO> latestList();

    PageResponse<ProductListVO> adminPage(AdminProductQueryRequest request);

    PageResponse<ProductListVO> merchantPage(AdminProductQueryRequest request);

    Boolean merchantCreate(AdminProductSaveRequest request);

    Boolean merchantUpdate(Long id, AdminProductSaveRequest request);

    Boolean merchantDelete(Long id);

    Boolean merchantUpdateStatus(Long id, AdminStatusUpdateRequest request);

    Boolean adminUpdateStatus(Long id, AdminStatusUpdateRequest request);

    Boolean adminAudit(Long id, ProductAuditRequest request);
}