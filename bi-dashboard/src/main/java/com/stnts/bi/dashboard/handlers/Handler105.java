package com.stnts.bi.dashboard.handlers;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/10/22
 *
 * 带带业务大盘-用户分析-留存
 */
public class Handler105 extends BaseHandler {

    public static final String HANDLER_ID = "dashboard-dd-105";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO){

        changeDay2Month("month_dt", queryChartParameterVO);
        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);

        return ResultEntity.success(queryChartResultVO);
    }
}
