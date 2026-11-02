package com.campus.snackmall.module.order.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrderListVO {

    private Long id;
    private String orderNo;
    private BigDecimal totalAmount;
    private Integer status;
    private String statusText;
    private LocalDateTime createTime;
    private Long userId;
    private String username;
}
