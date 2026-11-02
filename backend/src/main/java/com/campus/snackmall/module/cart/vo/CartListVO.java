package com.campus.snackmall.module.cart.vo;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class CartListVO {

    private List<CartItemVO> items;
    private Integer checkedCount;
    private BigDecimal totalAmount;
}
