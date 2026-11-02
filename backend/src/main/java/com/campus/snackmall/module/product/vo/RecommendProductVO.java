package com.campus.snackmall.module.product.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class RecommendProductVO {

    private Long id;
    private String name;
    private String coverImage;
    private BigDecimal price;
    private Integer sales;
}
