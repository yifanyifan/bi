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
 * 带带业务大盘-收入分析-目标完成度
 */
public class Handler001 extends BaseHandler{

    public static final String HANDLER_ID = "dashboard-dd-001";

    public static final String KEY_KPI_LEVEL = "kpi_level";
    public static final String KEY_DT = "dt";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {

        List<QueryChartParameterVO.ConditionVO> conds = queryChartParameterVO.getDashboard();
//        QueryChartParameterVO.ConditionVO aggDimCond = initAggDimCond(HANDLER_ID, conds);
//        conds.add(aggDimCond);
        QueryChartParameterVO.ConditionVO kpiLevelCond = conds.stream().filter(cond -> StringUtils.equals(cond.getName(), KEY_KPI_LEVEL)).findFirst().orElse(initDefault());
        conds.removeIf(cond -> StringUtils.equals(cond.getName(), KEY_KPI_LEVEL));
        int year = parseDt2Year(conds);
        //  这个查出来是总利润
        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
        if(CollectionUtil.isEmpty(queryChartResultVO.getDatas())){
            return ResultEntity.success(queryChartResultVO);
        }
        //  还得查出目标金额
        queryChartParameterVO.setTableName("bi_dashboard_dd_kpi");
        List<QueryChartParameterVO.ConditionVO> _conds = new ArrayList<>();
        _conds.add(kpiLevelCond);
        QueryChartParameterVO.ConditionVO yearCond = new QueryChartParameterVO.ConditionVO();
        yearCond.setName("kpi_year");
        yearCond.setLogic(FilterLogicConstant.EQ);
        yearCond.setValue(String.valueOf(year));
        _conds.add(yearCond);
        queryChartParameterVO.setDashboard(_conds);
        List<OlapChartMeasure> _meas = new ArrayList<>();
        OlapChartMeasure kpiTarget = new OlapChartMeasure();
        kpiTarget.setName("kpi_target");
        kpiTarget.setAliasName("目标金额");
        _meas.add(kpiTarget);
        queryChartParameterVO.setMeasure(_meas);
        queryChartParameterVO.setDimension(Collections.EMPTY_LIST);
        QueryChartResultVO _queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
        if(CollectionUtil.isNotEmpty(_queryChartResultVO.getDatas())){
            QueryChartResultVO.MeasureData targetMea = (QueryChartResultVO.MeasureData) _queryChartResultVO.getDatas().get(0);
            queryChartResultVO.getDatas().add(targetMea);
            //需要计算完成度
            QueryChartResultVO.MeasureData ratioMea = new QueryChartResultVO.MeasureData();
            ratioMea.setDisplayName("完成度");
            ratioMea.setDigitDisplay("percent");
            ratioMea.setDecimal(2);
            List<String> ratioList = new ArrayList<>(1);
            QueryChartResultVO.MeasureData srcMea = (QueryChartResultVO.MeasureData) queryChartResultVO.getDatas().get(0);
            ratioList.add(ratio(srcMea.getData().get(0), targetMea.getData().get(0)));
            ratioMea.setData(ratioList);
            queryChartResultVO.getDatas().add(ratioMea);
        }else{
            queryChartResultVO.getDatas().add(initDefaultMea("kpi_target", "目标金额"));
            queryChartResultVO.getDatas().add(initDefaultMea(null, "完成度"));
        }

        return ResultEntity.success(queryChartResultVO);
    }

    public QueryChartParameterVO.ConditionVO initDefault(){
        QueryChartParameterVO.ConditionVO cond = new QueryChartParameterVO.ConditionVO();
        cond.setName(KEY_KPI_LEVEL);
        cond.setLogic(FilterLogicConstant.EQ);
        cond.setValue("B");
        return cond;
    }

    public QueryChartResultVO.MeasureData initDefaultMea(String name, String displayName){
        QueryChartResultVO.MeasureData olapChartMeasure = new QueryChartResultVO.MeasureData();
        olapChartMeasure.setName(name);
        olapChartMeasure.setDisplayName(displayName);
        olapChartMeasure.setData(Collections.emptyList());
        return olapChartMeasure;
    }

    /**
     * 把 日期 转化为全年的
     * @param conds
     */
    public int parseDt2Year(List<QueryChartParameterVO.ConditionVO> conds){

        int year = LocalDate.now().getYear();
        Optional<QueryChartParameterVO.ConditionVO> dtCondOp = conds.stream().filter(cond -> StringUtils.equals(cond.getName(), "dt")).findFirst();
        if(dtCondOp.isPresent()){
            QueryChartParameterVO.ConditionVO dtCond = dtCondOp.get();
            JSONArray dts = JSON.parseArray(dtCond.getValue());
            String beginDate = dts.getString(0);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            year = LocalDate.parse(beginDate, dateTimeFormatter).getYear();
//            String newBeginDate = String.format("%s-01-01", year);
//            String newEndDate = String.format("%s-12-31", year);
//            JSONArray dateArr = new JSONArray();
//            dateArr.add(newBeginDate);
//            dateArr.add(newEndDate);
//            dtCond.setValue(JSON.toJSONString(dateArr));
        }
        return year;
    }
}
