package com.campus.snackmall.module.order.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemVO {

    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal amount;
}
