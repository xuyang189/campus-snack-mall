package com.campus.snackmall.module.product.dto;

import lombok.Data;

@Data
public class ProductQueryRequest {

    private Long current = 1L;
    private Long size = 10L;
    private String keyword;
    private Long categoryId;
    private String minPrice;
    private String maxPrice;
}
