package com.stnts.bi.dashboard.handlers;

import cn.hutool.core.collection.CollectionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/10/22
 *
 * 带带业务大盘-用户分析-充值排名
 */
public class Handler110 extends BaseHandler {

    public static final String HANDLER_ID = "dashboard-dd-110";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO){

//        setAggDimCond(queryChartParameterVO, HANDLER_ID);
        //要查一个总充值   每个用户充值除以总充值得到占比
        rmConds(queryChartParameterVO);
        return super.handler(queryChartService, queryChartParameterVO);
    }
}
