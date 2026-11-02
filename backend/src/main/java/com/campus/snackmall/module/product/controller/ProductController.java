package com.campus.snackmall.module.product.controller;

import com.campus.snackmall.common.model.PageResponse;
import com.campus.snackmall.common.result.Result;
import com.campus.snackmall.module.product.dto.ProductQueryRequest;
import com.campus.snackmall.module.product.service.ProductService;
import com.campus.snackmall.module.product.vo.ProductDetailVO;
import com.campus.snackmall.module.product.vo.ProductListVO;
import com.campus.snackmall.module.product.vo.RecommendProductVO;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Result<PageResponse<ProductListVO>> page(ProductQueryRequest request) {
        return Result.success("查询成功", productService.publicPage(request));
    }

    @GetMapping("/recommend/hot")
    public Result<List<RecommendProductVO>> hotList() {
        return Result.success("查询成功", productService.hotList());
    }

    @GetMapping("/recommend/latest")
    public Result<List<RecommendProductVO>> latestList() {
        return Result.success("查询成功", productService.latestList());
    }

    @GetMapping("/{id}")
    public Result<ProductDetailVO> detail(@PathVariable Long id) {
        return Result.success("查询成功", productService.detail(id));
    }
}
