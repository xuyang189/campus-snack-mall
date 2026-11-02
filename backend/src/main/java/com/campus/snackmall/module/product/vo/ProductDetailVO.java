package com.campus.snackmall.module.product.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductDetailVO {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private Long merchantId;
    private String name;
    private String coverImage;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private Integer status;
    private Integer auditStatus;
    private String description;
}