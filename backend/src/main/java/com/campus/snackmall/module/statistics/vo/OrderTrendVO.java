package com.campus.snackmall.module.statistics.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderTrendVO {

    private String date;
    private Long orderCount;
    private BigDecimal salesAmount;
}
