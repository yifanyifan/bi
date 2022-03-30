package com.stnts.bi.dashboard.handlers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/10/22
 *
 * 带带业务大盘-用户分析-留存
 */
public class Handler106 extends BaseHandler {

    public static final String HANDLER_ID = "dashboard-dd-106";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO){

        changeDay2Month("month_dt", queryChartParameterVO);
        //这里要特殊处理一下  结束日期到上月为止
        limitEndMonth(queryChartParameterVO);
        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);

        return ResultEntity.success(queryChartResultVO);
    }

    private void limitEndMonth(QueryChartParameterVO queryChartParameterVO) {

        Optional<QueryChartParameterVO.ConditionVO> month_dt = queryChartParameterVO.getDashboard().stream().filter(d -> StringUtils.equals(d.getName(), "month_dt")).findAny();
        if(month_dt.isPresent()){
            String value = month_dt.get().getValue();
            JSONArray objects = JSON.parseArray(value);
            String beginDate = objects.getString(0);
            String endDate = objects.getString(1);
            YearMonth beginDateObj = YearMonth.parse(beginDate, DateTimeFormatter.ofPattern("yyyy-MM"));
            YearMonth endDateObj = YearMonth.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM"));
            YearMonth preMonth = YearMonth.now().minusMonths(1);
            if(beginDateObj.isAfter(preMonth)){
                return;  //
            }else if(endDateObj.isAfter(preMonth)){
                JSONArray objs = new JSONArray();
                objs.add(beginDate);
                objs.add(preMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")));
                month_dt.get().setValue(objs.toJSONString());
            }

//            LocalDate endDateObj = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//            LocalDate preMonthLastDay = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
//            if(endDateObj.isAfter(preMonthLastDay)){
//                JSONArray objs = new JSONArray();
//                objs.add(beginDate);
//                objs.add(preMonthLastDay);
//                month_dt.get().setValue(objs.toJSONString());
//            }
        }
    }
}
