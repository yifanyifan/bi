package com.stnts.bi.dashboard.handlers;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.constant.FilterLogicConstant;
import com.stnts.bi.sql.entity.OlapChartMeasure;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/10/22
 *
 * 带带业务大盘-收入分析-充值构成
 */
public class Handler002 extends BaseHandler {

    public static final String HANDLER_ID = "dashboard-dd-002";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO){

//        setAggDimCond(queryChartParameterVO, HANDLER_ID);
        return super.handler(queryChartService, queryChartParameterVO);
    }
}
