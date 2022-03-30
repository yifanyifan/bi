package com.stnts.bi.dashboard.handlers;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.dashboard.service.ChartService;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/10/22
 */
public interface Handler {

    /**
     *
     * @param queryChartParameterVO
     * @return
     */
    ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO);

    /**
     * 桑基图
     * @param queryChartService
     * @param queryChartParameterVO
     * @param sankeyNodeNumberPerLayer
     * @return
     */
    ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO, int sankeyNodeNumberPerLayer);
}
