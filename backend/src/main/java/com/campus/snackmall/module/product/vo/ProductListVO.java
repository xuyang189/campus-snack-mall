package com.campus.snackmall.module.product.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ProductListVO {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private Long merchantId;
    private String merchantName;
    private String name;
    private String coverImage;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private String description;
    private Integer status;
    private Integer auditStatus;
    private LocalDateTime createTime;
}