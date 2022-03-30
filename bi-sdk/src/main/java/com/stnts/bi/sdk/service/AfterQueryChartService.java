package com.stnts.bi.sdk.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.stnts.bi.sql.constant.FilterLogicConstant;
import com.stnts.bi.sql.constant.FunctionConstant;
import com.stnts.bi.sql.entity.OlapChartDimension;
import com.stnts.bi.sql.entity.OlapChartMeasure;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 刘天元
 */
@Service
public class AfterQueryChartService {

    private final QueryChartService queryChartService;

    public AfterQueryChartService(QueryChartService queryChartService) {
        this.queryChartService = queryChartService;
    }

    public void afterQuery(QueryChartParameterVO queryChartParameterVO, QueryChartResultVO queryChartResultVO) {
        if(StrUtil.equals(queryChartParameterVO.getId(), "sdk-pageAnalysis-pc-0")) {
            QueryChartParameterVO queryChartParameterVOTemp = new QueryChartParameterVO();
            queryChartParameterVOTemp.setDatabaseName("banyan_bi_sdk");
            queryChartParameterVOTemp.setTableName("view_sdk_app_sdk_web_session");
            OlapChartDimension olapChartDimension = new OlapChartDimension();
            olapChartDimension.setName("session");
            queryChartParameterVOTemp.setDimension(CollectionUtil.newArrayList(olapChartDimension));
            OlapChartMeasure olapChartMeasure = new OlapChartMeasure();
            olapChartMeasure.setName("page_sequence");
            olapChartMeasure.setFunc(FunctionConstant.MAX);
            olapChartMeasure.setMaxvalue(1L);
            queryChartParameterVOTemp.setMeasure(CollectionUtil.newArrayList(olapChartMeasure));
            queryChartParameterVOTemp.setDashboard(queryChartParameterVO.getDashboard());
            QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
            conditionVO.setName("in_time");
            conditionVO.setLogic(FilterLogicConstant.LT);
            conditionVO.setValue(DateUtil.formatDateTime(DateUtil.offsetMinute(new Date(), -30)));
            queryChartParameterVOTemp.getDashboard().add(conditionVO);
            queryChartParameterVOTemp.setLimit(0.0);
            QueryChartResultVO queryChartResultVOTemp = queryChartService.queryChart(queryChartParameterVOTemp);
            queryChartResultVO.getDatas().forEach(data -> {
                if(data instanceof QueryChartResultVO.MeasureData) {
                    QueryChartResultVO.MeasureData measureData = (QueryChartResultVO.MeasureData) data;
                    if(StrUtil.equals(measureData.getName(), "start_sessions")) {
                        measureData.getData().set(0, NumberUtil.div(queryChartResultVOTemp.getTotal().toString(), measureData.getData().get(0), 2).toString());
                    }
                }
            });
        }
    }

}
