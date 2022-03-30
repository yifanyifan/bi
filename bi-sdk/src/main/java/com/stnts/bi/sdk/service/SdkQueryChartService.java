package com.stnts.bi.sdk.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.stnts.bi.sdk.exception.ValidationException;
import com.stnts.bi.sdk.util.DozerUtil;
import com.stnts.bi.sdk.vo.QueryChartParameterForSdkVO;
import com.stnts.bi.sdk.vo.QueryChartResultForCardVO;
import com.stnts.bi.sdk.vo.QueryChartResultForCompareVO;
import com.stnts.bi.sql.constant.ChartTypeConstant;
import com.stnts.bi.sql.constant.FilterLogicConstant;
import com.stnts.bi.sql.constant.TimeUnitConstant;
import com.stnts.bi.sql.entity.OlapChartDimension;
import com.stnts.bi.sql.entity.OlapChartMeasure;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author 刘天元
 */
@Service
@Slf4j
public class SdkQueryChartService {
    private final QueryChartService queryChartService;

    private final SdkBeforeQueryChartService beforeQueryChartService;

    private final SdkAfterQueryChartService afterQueryChartService;

    public static ThreadLocal<Map<Integer, String>> threadLocalDimensionIdToNameMap = new ThreadLocal<>();
    public static ThreadLocal<Map<Integer, String>> threadLocalMeasureIdToNameMap = new ThreadLocal<>();

    public final static String dateFieldName = "dt";

    public SdkQueryChartService(QueryChartService queryChartService, SdkBeforeQueryChartService beforeQueryChartService, SdkAfterQueryChartService afterQueryChartService) {
        this.queryChartService = queryChartService;
        this.beforeQueryChartService = beforeQueryChartService;
        this.afterQueryChartService = afterQueryChartService;
    }

    public QueryChartResultVO getQueryChartResultVO(QueryChartParameterForSdkVO queryChartParameterVO) {
        QueryChartResultVO queryChartResultVO;
        handleParameter(queryChartParameterVO);
        if(StrUtil.equalsAny(queryChartParameterVO.getId(), "data-overview-operation-chart-30", "data-overview-payment-chart-30")) {
            queryChartResultVO = getChartForCompare(queryChartParameterVO, queryChartParameterVO.getMomCompare(), queryChartParameterVO.getYoyCompare());
        } else if(StrUtil.startWithAny(queryChartParameterVO.getId(), "data-overview-operation-text", "data-overview-payment-text")) {
            queryChartResultVO = handleDataOverviewText(queryChartParameterVO);
        } else {
            queryChartResultVO = getChart(queryChartParameterVO, queryChartParameterVO.getTopN());
        }
        return queryChartResultVO;
    }

    private void handleParameter(QueryChartParameterForSdkVO queryChartParameterVO) {
        if(CollectionUtil.isNotEmpty(queryChartParameterVO.getDashboard())) {
            for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
                if(StrUtil.equals(conditionVO.getName(), "product_id")) {
                    String productValue = conditionVO.getValue();
                    if(StrUtil.isEmpty(productValue)) {
                        throw new ValidationException("请选择产品线");
                    }
                    break;
                }
            }
        }

        if(CollectionUtil.isNotEmpty(queryChartParameterVO.getMeasure())) {
            queryChartParameterVO.getMeasure().removeIf(chartMeasure -> BooleanUtil.isFalse(chartMeasure.getShow()));
        }
        String id = queryChartParameterVO.getId();
        queryChartParameterVO.setDatabaseName(SdkBeforeQueryChartService.DATABASE_NAME);
        // sdk2.0使用21.8.3.44版本的clickhouse
        queryChartParameterVO.setDataSource("clickhouse21");
        if(queryChartParameterVO.getAttributionCaliber() == null) {
            queryChartParameterVO.setAttributionCaliber(1);
        }
        if(StrUtil.equals(id, "channel-analyse-tendency-30")) {
            queryChartParameterVO.setAttributionCaliber(2);
        }
        if(StrUtil.equals(id, "channel-analyse-tendency-40")) {
            if("view_register_detail".equals(queryChartParameterVO.getTableName())) {
                // 注册不区分归因
                queryChartParameterVO.setAttributionCaliber(2);
            }
        }
        if(StrUtil.equalsAny(id, "channel-analyse-effect-05",
                "channel-analyse-effect-10", "channel-analyse-effect-20", "channel-analyse-effect-30", "channel-analyse-effect-40",
                "user-analyse-retain-10", "user-analyse-ltv-10")) {
            // 留存不区分归因
            queryChartParameterVO.setAttributionCaliber(2);
        }

        String aggWideTableName = "view_sdk_app_web_register_payment_agg";
        if("channel-analyse-effect-50".equals(id)) {
            queryChartParameterVO.setTableName(aggWideTableName);
        }

        Map<Integer, String> dimensionIdToNameMap = new HashMap<>(queryChartParameterVO.getDimension().size());
        Map<Integer, String> measureIdToNameMap = new HashMap<>(queryChartParameterVO.getMeasure().size());
        for (int i = 0; i < queryChartParameterVO.getDimension().size(); i++) {
            OlapChartDimension chartDimension = queryChartParameterVO.getDimension().get(i);
            if(chartDimension.getId() == null) {
                chartDimension.setId(i);
            }
            dimensionIdToNameMap.put(chartDimension.getId(), chartDimension.getName());
        }
        for (int i = 0; i < queryChartParameterVO.getMeasure().size(); i++) {
            OlapChartMeasure chartMeasure = queryChartParameterVO.getMeasure().get(i);
            if(chartMeasure.getId() == null) {
                chartMeasure.setId(i);
            }
            measureIdToNameMap.put(chartMeasure.getId(), chartMeasure.getName());
        }
        threadLocalDimensionIdToNameMap.set(dimensionIdToNameMap);
        threadLocalMeasureIdToNameMap.set(measureIdToNameMap);

        beforeQueryChartService.renameConditionName(queryChartParameterVO);
        if(StrUtil.equals(queryChartParameterVO.getTableName(), aggWideTableName)) {
            if(!StrUtil.equalsAny(id, "channel-analyse-tendency-45", "channel-analyse-tendency-50", "channel-analyse-tendency-60")) {
                beforeQueryChartService.renameDimensionName(queryChartParameterVO);
                beforeQueryChartService.renameMeasureName(queryChartParameterVO);
            }
        }


        if(CollectionUtil.isNotEmpty(queryChartParameterVO.getDimension())) {
            OlapChartDimension dateDimension = queryChartParameterVO.getDimension().get(0);
            if(dateFieldName.equals(dateDimension.getName()) && TimeUnitConstant.WEEK.equals(dateDimension.getGroup())) {
                dateDimension.setFormat("Y-w-d");
            }
        }
    }

    private QueryChartResultVO getChart(QueryChartParameterVO queryChartParameterVO, Integer topN) {
        queryChartParameterVO = beforeQueryChartService.beforeQuery(queryChartParameterVO, topN);
        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
        afterQueryChartService.afterQuery(queryChartParameterVO, queryChartResultVO, topN);
        return queryChartResultVO;
    }


    public QueryChartResultForCompareVO getChartForCompare(QueryChartParameterVO queryChartParameter, Boolean momCompare, Boolean yoyCompare) {
        String dimensionGroup = queryChartParameter.getDimension().get(0).getGroup();
        List<String> startEndDate = getStartEndDate(queryChartParameter);
        Objects.requireNonNull(startEndDate);
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
        if(BooleanUtil.isTrue(momCompare)) {
            if(StrUtil.equals(dimensionGroup, TimeUnitConstant.HOUR)) {
                dimensionGroup = TimeUnitConstant.DAY;
            }

            momCompareStartDateStr = queryChartService.getMOMPreCycleTimeStr(dimensionGroup, startDateStr);
            momCompareEndDateStr = queryChartService.getMOMPreCycleTimeStr(dimensionGroup, endDateStr);

            String value = StrUtil.format("[\"{}\",\"{}\"]",
                    momCompareStartDateStr,
                    momCompareEndDateStr);
            QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
            conditionVO.setName(dateFieldName);
            conditionVO.setFunc(dimensionGroup);
            conditionVO.setLogic(FilterLogicConstant.BETWEEN);
            conditionVO.setValue(value);
            momCompareCondition = conditionVO;
        }
        if(BooleanUtil.isTrue(yoyCompare)) {
            if(StrUtil.equals(dimensionGroup, TimeUnitConstant.HOUR)) {
                dimensionGroup = TimeUnitConstant.DAY;
            }

            String contrast = queryChartService.autoYoy(dimensionGroup, "yoy_rate");
            yoyCompareStartDateStr = queryChartService.getYOYPreCycleTimeStr(contrast, dimensionGroup, startDateStr);
            yoyCompareEndDateStr = queryChartService.getYOYPreCycleTimeStr(contrast, dimensionGroup, endDateStr);

            String value = StrUtil.format("[\"{}\",\"{}\"]",
                    yoyCompareStartDateStr,
                    yoyCompareEndDateStr);
            QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
            conditionVO.setName(dateFieldName);
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

        if(DateUtil.today().equals(endDateStr)) {
            for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameter.getDashboard()) {
                if (dateFieldName.equals(conditionVO.getName())) {
                    conditionVO.setFunc(TimeUnitConstant.HOUR);
                    int thisHour = DateUtil.thisHour(true);
                    String value = StrUtil.format("[\"{}\",\"{}\"]",
                            startDateStr + " 00",
                            endDateStr + " " + (thisHour<10?"0":"") + thisHour
                    );
                    conditionVO.setValue(value);
                }
            }
        }
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
                    handleMaxMinAvgData(cloneQueryChartParameter, queryChartResultForCompareVO);
                }),
                CompletableFuture.runAsync(() -> queryCompareResult(queryChartParameter, queryChartResultForMomCompareVO, finalMomCompareCondition)),
                CompletableFuture.runAsync(() -> queryCompareResult(queryChartParameter, queryChartResultForYoyCompareVO, finalYoyCompareCondition))
        ).join();
        handleCompareData(queryChartResultForCompareVO, 1, queryChartResultForMomCompareVO);
        handleCompareData(queryChartResultForCompareVO, 2, queryChartResultForYoyCompareVO);
        return queryChartResultForCompareVO;
    }

    private void handleMaxMinAvgData(QueryChartParameterVO queryChartParameter, QueryChartResultForCompareVO queryChartResultForCompareVO) {
        if(CollectionUtil.isEmpty(queryChartResultForCompareVO.getDatas())) {
            return;
        }
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

    private void queryCompareResult(QueryChartParameterVO queryChartParameter, QueryChartResultVO queryChartResultVO, QueryChartParameterVO.ConditionVO compareCondition) {
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


    /**
     * 处理数据概况中的卡片。跨周期不计算同环比。
     * @param queryChartParameterVO
     */
    public QueryChartResultVO handleDataOverviewText(QueryChartParameterVO queryChartParameterVO) {
        queryChartParameterVO.setRollup(false);
        List<String> startEndDate = getStartEndDate(queryChartParameterVO);
        Objects.requireNonNull(startEndDate);
        String startDateStr = startEndDate.get(0);
        String endDateStr = startEndDate.get(1);

        String dimensionGroup = queryChartParameterVO.getDimension().get(0).getGroup();
        if(StrUtil.equals(dimensionGroup, TimeUnitConstant.HOUR)) {
            // 小时当作天处理
            dimensionGroup = TimeUnitConstant.DAY;
            queryChartParameterVO.getDimension().get(0).setGroup(TimeUnitConstant.DAY);
        }
        //beforeQueryChartService.renameMeasureName(queryChartParameterVO);
        Assert.notEmpty(dimensionGroup);
        Assert.notEmpty(startDateStr);
        Assert.notEmpty(endDateStr);
        DateTime startDate = DateUtil.parse(startDateStr);
        DateTime endDate = DateUtil.parse(endDateStr);

        // 标记是否记录同环比
        Boolean momYoyFlag = false;
        if(StrUtil.equals(dimensionGroup, TimeUnitConstant.DAY)) {
            if(StrUtil.equals(startDateStr, endDateStr)) {
                momYoyFlag = true;
            }
        } else if(StrUtil.equals(dimensionGroup, TimeUnitConstant.WEEK)) {
            if(DateUtil.between(startDate, endDate, DateUnit.DAY) == 6 && queryChartService.isoWeekOfYear(startDate)==queryChartService.isoWeekOfYear(endDate)) {
                momYoyFlag = true;
            }
        } else if(StrUtil.equals(dimensionGroup, TimeUnitConstant.MONTH)) {
            if(DateUtil.year(startDate)==DateUtil.year(endDate) && DateUtil.month(startDate)==DateUtil.month(endDate)) {
                momYoyFlag = true;
            }
        }

        if(momYoyFlag) {
            // 设置同环比
            queryChartParameterVO.getMeasure().forEach(v -> v.setContrast("yoy_rate,mom_rate"));

            // 当前周期不完整不能和上一个完整周期对比。
            String today = DateUtil.today();
            Date date = new Date();
            if(TimeUnitConstant.DAY.equals(dimensionGroup)) {
                if(today.equals(startDateStr) && today.equals(endDateStr)) {
                    // 当日同环比精确到前n小时，前n分钟。
                    queryChartParameterVO.setShowCurrentGroup(true);
                    queryChartParameterVO.setTableName("rt_ads_sdk_all_agg_min");
                    queryChartParameterVO.setChartType(ChartTypeConstant.TEXT);
                    queryChartParameterVO.getDimension().get(0).setOrder(-1);
                    String conditionSql1 = StrUtil.format("if(toHour({}) = toHour(now()), toMinute({}), 0) <= toMinute(now())", dateFieldName, dateFieldName);
                    String conditionSql2 = StrUtil.format("toDate({}) in ('{}','{}','{}')", dateFieldName,
                            DateUtil.today(),
                            DateUtil.formatDate(DateUtil.offsetDay(date, -1)),
                            DateUtil.formatDate(DateUtil.offsetDay(date, -7))
                    );
                    queryChartParameterVO.setConditionSql(conditionSql1 + " and " + conditionSql2);
                    queryChartParameterVO.getDashboard().removeIf(v -> dateFieldName.equals(v.getName()));
                }
            } else if(TimeUnitConstant.WEEK.equals(dimensionGroup)) {
                if(DateUtil.formatDate(DateUtil.beginOfWeek(date)).equals(startDateStr) && DateUtil.formatDate(DateUtil.endOfWeek(date)).equals(endDateStr)) {
                    queryChartParameterVO.setShowCurrentGroup(true);
                }
            } else if(TimeUnitConstant.MONTH.equals(dimensionGroup)) {
                if(DateUtil.formatDate(DateUtil.beginOfMonth(date)).equals(startDateStr) && DateUtil.formatDate(DateUtil.endOfMonth(date)).equals(endDateStr)) {
                    queryChartParameterVO.setShowCurrentGroup(true);
                }
            }
        } else {
            queryChartParameterVO.getDimension().remove(0);
        }

        QueryChartResultForCardVO queryChartResultForCardVO = new QueryChartResultForCardVO();
        List<QueryChartResultForCardVO.CardData> cardDataList = new ArrayList<>(queryChartParameterVO.getMeasure().size());
        queryChartResultForCardVO.setCardDataList(cardDataList);
        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO, queryChartResultForCardVO);
        if(CollectionUtil.isEmpty(queryChartResultVO.getDatas())) {
            for (OlapChartMeasure chartMeasure : queryChartParameterVO.getMeasure()) {
                QueryChartResultForCardVO.CardData cardData = new QueryChartResultForCardVO.CardData();
                cardData.setTextValue("-");
                cardData.setMomRateValue("-");
                cardData.setYoyRateValue("-");
                cardData.setMomDate("-");
                cardData.setYoyDate("-");
                cardDataList.add(cardData);
            }
            return queryChartResultVO;
        }
        if(momYoyFlag) {
            if(TimeUnitConstant.WEEK.equals(dimensionGroup)) {
                startDateStr = StrUtil.format("{}({})", DateUtil.year(startDate), queryChartService.isoWeekOfYear(startDate));
            } else if(TimeUnitConstant.MONTH.equals(dimensionGroup)) {
                startDateStr = DateUtil.format(startDate, "yyyy-MM");
            }
            String contrast = queryChartService.autoYoy(dimensionGroup, "yoy_rate");
            String momPreCycleTimeStr = queryChartService.getMOMPreCycleTimeStr(dimensionGroup, startDateStr);
            String yoyPreCycleTimeStr = queryChartService.getYOYPreCycleTimeStr(contrast, dimensionGroup, startDateStr);
            QueryChartResultForCardVO.CardData cardData = null;
            for (int i = 1; i < queryChartResultVO.getDatas().size(); i++) {
                QueryChartResultVO.MeasureData measureData = (QueryChartResultVO.MeasureData) queryChartResultVO.getDatas().get(i);
                int slot = i % 3;
                if(slot == 1) {
                    cardData = new QueryChartResultForCardVO.CardData();
                    cardData.setShowContrast(true);
                    cardData.setMomDate(TimeUnitConstant.WEEK.equals(dimensionGroup) ? queryChartService.formatWeek(momPreCycleTimeStr) : momPreCycleTimeStr);
                    cardData.setYoyDate(TimeUnitConstant.WEEK.equals(dimensionGroup) ? queryChartService.formatWeek(yoyPreCycleTimeStr) : yoyPreCycleTimeStr);
                    cardData.setDisplayName(measureData.getDisplayName());
                    cardData.setTextValue(measureData.getData().get(0));
                } else if(slot == 2) {
                    cardData.setYoyRateValue(measureData.getData().get(0));
                } else if(slot == 0) {
                    cardData.setMomRateValue(measureData.getData().get(0));
                    cardDataList.add(DozerUtil.toBean(cardData, QueryChartResultForCardVO.CardData.class));
                }
            }
        } else {
            for (Object data : queryChartResultVO.getDatas()) {
                QueryChartResultVO.MeasureData measureData = (QueryChartResultVO.MeasureData) data;
                QueryChartResultForCardVO.CardData cardData = new QueryChartResultForCardVO.CardData();
                cardData.setDisplayName(measureData.getDisplayName());
                cardData.setTextValue(measureData.getData().get(0));
                cardData.setMomRateValue("-");
                cardData.setYoyRateValue("-");
                cardData.setMomDate("-");
                cardData.setYoyDate("-");
                cardData.setShowContrast(false);
                cardDataList.add(cardData);
            }
        }
        return queryChartResultVO;
    }

    public static List<String> getStartEndDate(QueryChartParameterVO queryChartParameterVO) {
        for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
            if (dateFieldName.equals(conditionVO.getName())) {
                if (FilterLogicConstant.BETWEEN.equals(conditionVO.getLogic()) && TimeUnitConstant.DAY.equals(conditionVO.getFunc()) && JSONUtil.isJson(conditionVO.getValue())) {
                    List<String> stringList = com.stnts.bi.sql.util.JacksonUtil.fromJSONArray(conditionVO.getValue(), String.class);
                    return stringList;
                }
                break;
            }
        }
        return null;
    }

}
