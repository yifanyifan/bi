package com.stnts.bi.gameop.handlers;

import cn.hutool.core.collection.CollectionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.entity.OlapChartDimension;
import com.stnts.bi.sql.entity.OlapChartMeasure;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

import java.util.ArrayList;

/**
 * @author: liang.zhang
 * @description:  充值分布处理
 * @date: 2021/9/26
 */
public class MoneyDisHandler extends BaseHandler {

    public static final String HANDLER_ID = "gameop-203";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {

        OlapChartDimension chartDimension0 = new OlapChartDimension();
        chartDimension0.setName("uid");
        chartDimension0.setAsName("uid");
        chartDimension0.setOrder(-2);
//        OlapChartDimension partitionDateDim = getPartitionDateDim(queryChartParameterVO);
        OlapChartDimension partitionDateDim = partitionDimension();
        OlapChartMeasure chartMeasure0 = new OlapChartMeasure();
        chartMeasure0.setName("sum(charge_money)");
        chartMeasure0.setAsName("charge_money");
        chartMeasure0.setOrder(-2);

        ArrayList<OlapChartDimension> dimensions = CollectionUtil.newArrayList(chartDimension0, partitionDateDim);
        ArrayList<OlapChartMeasure> measures = CollectionUtil.newArrayList(chartMeasure0);

        return getResultAfterViewSql(queryChartService, queryChartParameterVO, dimensions, measures);
    }

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO, int sankeyNodeNumberPerLayer) {
        return null;
    }
}
