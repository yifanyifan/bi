package com.stnts.bi.dashboard.handlers;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.dashboard.vo.QueryChartResultForSankeyVO;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

import java.util.*;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/11/3
 */
public class Handler303 extends BaseHandler {

    public static final String HANDLER_ID = "dashboard-dd-303";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {
        return handler(queryChartService, queryChartParameterVO, 5);
    }

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO, int sankeyNodeNumberPerLayer) {

        rmConds(queryChartParameterVO);
        queryChartParameterVO.setLimit(100000.0);
        QueryChartResultForSankeyVO queryChartResultForSankeyVO = new QueryChartResultForSankeyVO();
        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO, queryChartResultForSankeyVO);
        handleChannelAnalysis50(queryChartResultForSankeyVO, Optional.ofNullable(sankeyNodeNumberPerLayer).orElse(5));
        return ResultEntity.success(queryChartResultVO);
    }
}
