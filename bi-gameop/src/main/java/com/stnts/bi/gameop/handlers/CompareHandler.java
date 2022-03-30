package com.stnts.bi.gameop.handlers;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.exception.BiException;
import com.stnts.bi.gameop.util.DozerUtil;
import com.stnts.bi.gameop.vo.QueryChartResultForCompareVO;
import com.stnts.bi.sql.constant.FilterLogicConstant;
import com.stnts.bi.sql.constant.TimeUnitConstant;
import com.stnts.bi.sql.entity.OlapChartMeasure;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/12/17
 */
public class CompareHandler extends BaseHandler{

    public static final String HANDLER_ID_MOM = "gameop-211";
    public static final String HANDLER_ID_YOY = "gameop-212";
    public static final String HANDLER_ID_ALL = "gameop-213";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {

        boolean mom = StrUtil.equalsAny(queryChartParameterVO.getId(), HANDLER_ID_MOM, HANDLER_ID_ALL);
        boolean yoy = StrUtil.equalsAny(queryChartParameterVO.getId(), HANDLER_ID_YOY, HANDLER_ID_ALL);
        return ResultEntity.success(getChartFroCompare(queryChartService, queryChartParameterVO, mom, yoy));
    }

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO, int sankeyNodeNumberPerLayer) {
        return null;
    }

    public QueryChartResultForCompareVO getChartFroCompare(QueryChartService queryChartService, QueryChartParameterVO queryChartParameter, Boolean momCompare, Boolean yoyCompare) {
        String dimensionGroup = queryChartParameter.getDimension().get(0).getGroup();
        List<String> startEndDate = getStartEndDate(queryChartParameter);
        if(null == startEndDate || startEndDate.isEmpty()){
            throw new BiException("不包含日期类型无法进行比较");
        }
        String startDateStr = startEndDate.get(0);
        String endDateStr = startEndDate.get(1);
        DateTime startDate = DateUtil.parseDate(startDateStr);
        DateTime endDate = DateUtil.parseDate(endDateStr);
        if(TimeUnitConstant.WEEK.equals(dimensionGroup)) {
            startDateStr = StrUtil.format("{}({})", DateUtil.year(startDate), queryChartService.isoWeekOfYear(startDate));
            endDateStr = StrUtil.format("{}({})", DateUtil.year(endDate), queryChartService.isoWeekOfYear(endDate));
        } else if(TimeUnitConstant.MONTH.equals(dimensionGroup)) {
            startDateStr = DateUtil.format(startDate, "yyyy-MM");
            endDateStr = DateUtil.format(endDate, "yyyy-MM");
        }
        String momCompareStartDateStr = "";
        String momCompareEndDateStr = "";
        String yoyCompareStartDateStr = "";
        String yoyCompareEndDateStr = "";
        QueryChartParameterVO.ConditionVO momCompareCondition = null;
        QueryChartParameterVO.ConditionVO yoyCompareCondition = null;
        String condDateKey = "partition_date";
        if(StrUtil.equals(dimensionGroup, TimeUnitConstant.HOUR)) {
            dimensionGroup = TimeUnitConstant.DAY;
            condDateKey = "partition_hour";
        }
        if(BooleanUtil.isTrue(momCompare)) {

            momCompareStartDateStr = queryChartService.getMOMPreCycleTimeStr(dimensionGroup, startDateStr);
            momCompareEndDateStr = queryChartService.getMOMPreCycleTimeStr(dimensionGroup, endDateStr);

            String value = StrUtil.format("[\"{}\",\"{}\"]",
                    momCompareStartDateStr,
                    momCompareEndDateStr);
            QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
            conditionVO.setName(condDateKey);
            conditionVO.setFunc(dimensionGroup);
            conditionVO.setLogic(FilterLogicConstant.BETWEEN);
            conditionVO.setValue(value);
            momCompareCondition = conditionVO;
        }
        if(BooleanUtil.isTrue(yoyCompare)) {

            String contrast = queryChartService.autoYoy(dimensionGroup, "yoy_rate");
            yoyCompareStartDateStr = queryChartService.getYOYPreCycleTimeStr(contrast, dimensionGroup, startDateStr);
            yoyCompareEndDateStr = queryChartService.getYOYPreCycleTimeStr(contrast, dimensionGroup, endDateStr);

            String value = StrUtil.format("[\"{}\",\"{}\"]",
                    yoyCompareStartDateStr,
                    yoyCompareEndDateStr);
            QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
            conditionVO.setName(condDateKey);
            conditionVO.setFunc(dimensionGroup);
            conditionVO.setLogic(FilterLogicConstant.BETWEEN);
            conditionVO.setValue(value);
            yoyCompareCondition = conditionVO;
        }

        QueryChartResultForCompareVO queryChartResultForCompareVO = new QueryChartResultForCompareVO();
        queryChartResultForCompareVO.setMomCompareStartDateStr(momCompareStartDateStr);
        queryChartResultForCompareVO.setMomCompareEndDateStr(momCompareEndDateStr);
        queryChartResultForCompareVO.setYoyCompareStartDateStr(yoyCompareStartDateStr);
        queryChartResultForCompareVO.setYoyCompareEndDateStr(yoyCompareEndDateStr);


        QueryChartResultForCompareVO queryChartResultForMomCompareVO = new QueryChartResultForCompareVO();
        QueryChartResultForCompareVO queryChartResultForYoyCompareVO = new QueryChartResultForCompareVO();
        QueryChartParameterVO.ConditionVO finalMomCompareCondition = momCompareCondition;
        QueryChartParameterVO.ConditionVO finalYoyCompareCondition = yoyCompareCondition;
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> {
                    QueryChartParameterVO cloneQueryChartParameter = JSONUtil.toBean(JSONUtil.toJsonStr(queryChartParameter), queryChartParameter.getClass());
                    if(BooleanUtil.isTrue(momCompare) || BooleanUtil.isTrue(yoyCompare)) {
                        QueryChartParameterVO.ConditionVO trueConditionVO = new QueryChartParameterVO.ConditionVO();
                        trueConditionVO.setValue("1=1");
                        cloneQueryChartParameter.setCompare(CollectionUtil.newArrayList(trueConditionVO));
                    }
                    queryChartService.queryChart(cloneQueryChartParameter, queryChartResultForCompareVO);
//                    handleMaxMinAvgData(cloneQueryChartParameter, queryChartResultForCompareVO);
                }),
                CompletableFuture.runAsync(() -> queryCompareResult(queryChartService, queryChartParameter, queryChartResultForMomCompareVO, finalMomCompareCondition)),
                CompletableFuture.runAsync(() -> queryCompareResult(queryChartService, queryChartParameter, queryChartResultForYoyCompareVO, finalYoyCompareCondition))
        ).join();
        handleCompareData(queryChartResultForCompareVO, 1, queryChartResultForMomCompareVO);
        handleCompareData(queryChartResultForCompareVO, 2, queryChartResultForYoyCompareVO);
        return queryChartResultForCompareVO;
    }

    private void handleCompareData(QueryChartResultVO queryChartResultVO, int flag, QueryChartResultVO compareResult) {
        if(CollectionUtil.isNotEmpty(queryChartResultVO.getDatas()) && CollectionUtil.isNotEmpty(compareResult.getDatas())) {
            for (int i = 0; i < queryChartResultVO.getDatas().size(); i++) {
                Object queryChartResultData = queryChartResultVO.getDatas().get(i);
                Object compareResultData = compareResult.getDatas().get(i);
                if(compareResultData instanceof QueryChartResultVO.DimensionData) {
                    QueryChartResultVO.DimensionData dimensionData = (QueryChartResultVO.DimensionData) compareResultData;
                    List<String> data = dimensionData.getData();
                    List<String> distinctData = dimensionData.getDistinctData();

                    QueryChartResultForCompareVO.DimensionData dimensionDataForCompare = queryChartResultData instanceof QueryChartResultForCompareVO.DimensionData ?
                            (QueryChartResultForCompareVO.DimensionData) queryChartResultData :
                            DozerUtil.toBean(queryChartResultData, QueryChartResultForCompareVO.DimensionData.class);
                    if(flag == 1) {
                        dimensionDataForCompare.setMomCompareData(data);
                        dimensionDataForCompare.setMomCompareDistinctData(distinctData);
                    } else if(flag == 2) {
                        dimensionDataForCompare.setYoyCompareData(data);
                        dimensionDataForCompare.setYoyCompareDistinctData(distinctData);
                    }
                    queryChartResultVO.getDatas().set(i, dimensionDataForCompare);
                }
                if(compareResultData instanceof QueryChartResultVO.MeasureData) {
                    QueryChartResultVO.MeasureData measureData = (QueryChartResultVO.MeasureData) compareResultData;
                    List<String> data = measureData.getData();
                    List<Object> groupData = measureData.getGroupData();

                    QueryChartResultForCompareVO.MeasureData measureDataForCompare = queryChartResultData instanceof QueryChartResultForCompareVO.MeasureData ?
                            (QueryChartResultForCompareVO.MeasureData) queryChartResultData :
                            DozerUtil.toBean(queryChartResultData, QueryChartResultForCompareVO.MeasureData.class);
                    if(flag == 1) {
                        measureDataForCompare.setMomCompareData(data);
                        measureDataForCompare.setMomCompareGroupData(groupData);
                    } else if(flag == 2) {
                        measureDataForCompare.setYoyCompareData(data);
                        measureDataForCompare.setYoyCompareGroupData(groupData);
                    }
                    queryChartResultVO.getDatas().set(i, measureDataForCompare);
                }
            }
        }
    }

    private void handleMaxMinAvgData(QueryChartParameterVO queryChartParameter, QueryChartResultForCompareVO queryChartResultForCompareVO) {
        String aliasName = Optional.ofNullable(CollectionUtil.get(queryChartParameter.getMeasure(), 0)).map(OlapChartMeasure::getAliasName).orElse("");
        QueryChartResultVO.MeasureData measureData = (QueryChartResultVO.MeasureData) queryChartResultForCompareVO.getDatas().get(1);
        BigDecimal max = null;
        BigDecimal min = null;
        BigDecimal sum = null;
        List<String> data = measureData.getData();

        int notNumberCount = 0;
        for (int i = 0; i < data.size(); i++) {
            String datum = data.get(i);
            if(StrUtil.isEmpty(datum) || StrUtil.equalsAny(datum,"nan", "inf")) {
                notNumberCount++;
                continue;
            }
            BigDecimal temp = NumberUtil.toBigDecimal(datum);
            if(i == 0) {
                sum = temp;
                max = temp;
                min = temp;
            } else {
                sum = NumberUtil.add(sum, temp);
                max = NumberUtil.max(max, temp);
                min = NumberUtil.min(min, temp);
            }
        }
        queryChartResultForCompareVO.setMeasureMax(max);
        queryChartResultForCompareVO.setMeasureMin(min);
        int size = measureData.getData().size();
        queryChartResultForCompareVO.setMeasureAvg(sum == null || size == 0 ? null : NumberUtil.div(sum, size-notNumberCount));
        if(StrUtil.equalsAny(aliasName, "浏览量(PV)", "访客数", "游客数", "新访客", "新增用户", "活跃用户数", "新付费用户数", "付费用户数", "复购用户数")) {
            queryChartResultForCompareVO.setMeasureAvg(NumberUtil.round(queryChartResultForCompareVO.getMeasureAvg(), 0));
        }
    }

    private void queryCompareResult(QueryChartService queryChartService, QueryChartParameterVO queryChartParameter, QueryChartResultVO queryChartResultVO, QueryChartParameterVO.ConditionVO compareCondition) {
        if(ObjectUtil.isNull(compareCondition)) {
            return;
        }
        QueryChartParameterVO cloneQueryChartParameter = JSONUtil.toBean(JSONUtil.toJsonStr(queryChartParameter), queryChartParameter.getClass());
        // 处理对比数据
        for (int i = 0; i < cloneQueryChartParameter.getDashboard().size(); i++) {
            QueryChartParameterVO.ConditionVO dashboardConditionVO = cloneQueryChartParameter.getDashboard().get(i);
            if(StrUtil.equals(dashboardConditionVO.getName(), compareCondition.getName())) {
                cloneQueryChartParameter.getDashboard().set(i, compareCondition);
            }
        }
        queryChartService.queryChart(cloneQueryChartParameter, queryChartResultVO);
    }

    private static List<String> getStartEndDate(QueryChartParameterVO queryChartParameterVO) {
        for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
            if (StrUtil.equalsAny(conditionVO.getName(), "partition_date", "partition_hour")) {
                if (FilterLogicConstant.BETWEEN.equals(conditionVO.getLogic()) && TimeUnitConstant.DAY.equals(conditionVO.getFunc()) && JSONUtil.isJson(conditionVO.getValue())) {
                    return com.stnts.bi.sql.util.JacksonUtil.fromJSONArray(conditionVO.getValue(), String.class);
                }else if(FilterLogicConstant.EQ.equals(conditionVO.getLogic()) && TimeUnitConstant.DAY.equals(conditionVO.getFunc())){
                    return Arrays.asList(conditionVO.getValue(), conditionVO.getValue());
                }
                break;
            }
        }
        return null;
    }
}
