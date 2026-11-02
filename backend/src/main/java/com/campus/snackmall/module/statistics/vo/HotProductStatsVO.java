package com.campus.snackmall.module.statistics.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class HotProductStatsVO {

    private Long productId;
    private String productName;
    private Integer sales;
    private BigDecimal revenue;
}
