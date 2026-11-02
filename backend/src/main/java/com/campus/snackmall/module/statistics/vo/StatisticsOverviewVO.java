package com.campus.snackmall.module.statistics.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class StatisticsOverviewVO {

    private Long userCount;
    private Long productCount;
    private Long orderCount;
    private BigDecimal salesAmount;
    private Long todayOrderCount;
    private BigDecimal todaySalesAmount;
}
