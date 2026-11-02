package com.campus.snackmall.module.statistics.service;

import com.campus.snackmall.module.statistics.vo.HotProductStatsVO;
import com.campus.snackmall.module.statistics.vo.OrderTrendVO;
import com.campus.snackmall.module.statistics.vo.StatisticsOverviewVO;
import java.util.List;

public interface StatisticsService {

    StatisticsOverviewVO overview();

    List<HotProductStatsVO> hotProducts(Integer limit);

    List<OrderTrendVO> orderTrend();
}
