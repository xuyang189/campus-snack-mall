package com.campus.snackmall.module.cart.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CartItemVO {

    private Long id;
    private Long productId;
    private String productName;
    private String coverImage;
    private BigDecimal price;
    private Integer quantity;
    private Boolean checked;
    private BigDecimal subtotal;
    private Integer stock;
    private Integer productStatus;
}
