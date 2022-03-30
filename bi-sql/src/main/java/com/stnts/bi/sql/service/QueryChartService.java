package com.stnts.bi.sql.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.*;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.stnts.bi.sql.bo.*;
import com.stnts.bi.sql.constant.*;
import com.stnts.bi.sql.entity.*;
import com.stnts.bi.sql.exception.BusinessException;
import com.stnts.bi.sql.util.DatabaseUtil;
import com.stnts.bi.sql.util.JacksonUtil;
import com.stnts.bi.sql.util.SqlUtil;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;

/**
 * @author liutianyuan
 * @date 2019-04-02 10:10
 */

@Slf4j
@Service
public class QueryChartService {

    private final TableColumnService tableColumnService;

    private final ExecuteSqlService executeSqlService;

    public static final String ROLLUP_NAME = "总计";

    public QueryChartService(TableColumnService tableColumnService, ExecuteSqlService executeSqlService) {
        this.tableColumnService = tableColumnService;
        this.executeSqlService = executeSqlService;
    }

    /**
     * 查询图表，实现对比与总计功能
     * @param queryChartParameter
     * @return
     */
    public QueryChartResultVO queryChart(QueryChartParameterVO queryChartParameter) {
        QueryChartResultVO queryChartResultVO = new QueryChartResultVO();
        if(!isAllEmptyCondition(queryChartParameter.getCompare())) {
            handleCompare(queryChartParameter, queryChartResultVO);
            return queryChartResultVO;
        }
        if(BooleanUtil.isTrue(queryChartParameter.getRollup())) {
            handleRollup(queryChartParameter, queryChartResultVO);
            return queryChartResultVO;
        }
        queryChart(queryChartParameter, queryChartResultVO);
        return queryChartResultVO;
    }

    /**
     * 处理总计数据
     * @param queryChartParameter
     * @param queryChartResultVO
     */
    private void handleRollup(QueryChartParameterVO queryChartParameter, QueryChartResultVO queryChartResultVO) {
        QueryChartParameterVO queryChartParameterForRollUp = JSONUtil.toBean(JSONUtil.toJsonStr(queryChartParameter), queryChartParameter.getClass());
        queryChartParameterForRollUp.setDimension(Collections.emptyList());
        QueryChartResultVO rollupResult = new QueryChartResultVO();
        CompletableFuture.allOf(
                /// 暂时去掉动态代理
                /*CompletableFuture.runAsync(ProxyUtil.proxy(new Thread(() -> queryChart(queryChartParameter, queryChartResultVO)), new RequestIdAspect("queryChart"))),
                CompletableFuture.runAsync(ProxyUtil.proxy(new Thread(() -> queryChart(queryChartParameterForRollUp, rollupResult)), new RequestIdAspect("queryChartForRollup")))*/
                CompletableFuture.runAsync(() -> queryChart(queryChartParameter, queryChartResultVO)),
                CompletableFuture.runAsync(() -> queryChart(queryChartParameterForRollUp, rollupResult))
        ).join();
        Map<Integer, List<String>> rollupResultMeasureDataMap = MapUtil.newHashMap();
        for (Object data : Optional.ofNullable(rollupResult.getDatas()).orElseGet(Collections::emptyList)) {
            if(data instanceof QueryChartResultVO.MeasureData) {
                QueryChartResultVO.MeasureData measureData = (QueryChartResultVO.MeasureData) data;
                rollupResultMeasureDataMap.put(measureData.getId(), measureData.getData());
            }
        }
        for (int i = 0; i < queryChartResultVO.getDatas().size(); i++) {
            if(queryChartResultVO.getDatas().get(i) instanceof QueryChartResultVO.MeasureData) {
                QueryChartResultVO.MeasureData measureData = (QueryChartResultVO.MeasureData) queryChartResultVO.getDatas().get(i);
                if(ObjectUtil.isNotNull(measureData.getId()) &&  StrUtil.isEmpty(measureData.getContrast())) {
                    measureData.setTotalData(rollupResultMeasureDataMap.get(measureData.getId()).get(0));
                }
            }
        }
    }

    /**
     * 处理对比数据
     * @param queryChartParameter
     * @param queryChartResultVO
     */
    private void handleCompare(QueryChartParameterVO queryChartParameter, QueryChartResultVO queryChartResultVO) {
        QueryChartParameterVO queryChartParameterForCompare = JSONUtil.toBean(JSONUtil.toJsonStr(queryChartParameter), queryChartParameter.getClass());
        Map<String, QueryChartParameterVO.ConditionVO> nameToConditionMap = queryChartParameterForCompare.getCompare().stream().collect(Collectors.toMap(QueryChartParameterVO.ConditionVO::getName, Function.identity()));
        for (int i = 0; i < queryChartParameterForCompare.getDashboard().size(); i++) {
            QueryChartParameterVO.ConditionVO dashboardConditionVO = queryChartParameterForCompare.getDashboard().get(i);
            QueryChartParameterVO.ConditionVO existConditionVO = nameToConditionMap.get(dashboardConditionVO.getName());
            if(existConditionVO != null) {
                queryChartParameterForCompare.getDashboard().set(i, existConditionVO);
                nameToConditionMap.remove(dashboardConditionVO.getName());
            }
        }
        if(!nameToConditionMap.isEmpty()) {
            queryChartParameterForCompare.getDashboard().addAll(nameToConditionMap.values());
        }
        QueryChartResultVO compareResult = new QueryChartResultVO();
        CompletableFuture.allOf(
                /// 暂时去掉动态代理
                /*CompletableFuture.runAsync(ProxyUtil.proxy(new Thread(() -> queryChart(queryChartParameter, queryChartResultVO)), new RequestIdAspect("queryChart"))),
                CompletableFuture.runAsync(ProxyUtil.proxy(new Thread(() -> queryChart(queryChartParameterForCompare, compareResult)), new RequestIdAspect("queryChartForCompare")))*/
                CompletableFuture.runAsync(() -> queryChart(queryChartParameter, queryChartResultVO)),
                CompletableFuture.runAsync(() -> queryChart(queryChartParameterForCompare, compareResult))
        ).join();
        if(CollectionUtil.isNotEmpty(queryChartResultVO.getDatas()) && CollectionUtil.isNotEmpty(compareResult.getDatas())) {
            for (int i = 0; i < queryChartResultVO.getDatas().size(); i++) {
                List<String> data = ReflectUtil.invoke(compareResult.getDatas().get(i), "getData");
                ReflectUtil.invoke(queryChartResultVO.getDatas().get(i), "setCompareData", data);
                if(compareResult.getDatas().get(i) instanceof QueryChartResultVO.DimensionData) {
                    List<String> distinctData = ((QueryChartResultVO.DimensionData) compareResult.getDatas().get(i)).getDistinctData();
                    ((QueryChartResultVO.DimensionData) queryChartResultVO.getDatas().get(i)).setCompareDistinctData(distinctData);
                }
                if(compareResult.getDatas().get(i) instanceof QueryChartResultVO.MeasureData) {
                    List<Object> groupData = ((QueryChartResultVO.MeasureData) compareResult.getDatas().get(i)).getGroupData();
                    ((QueryChartResultVO.MeasureData) queryChartResultVO.getDatas().get(i)).setCompareGroupData(groupData);

                    String contrast = ((QueryChartResultVO.MeasureData) queryChartResultVO.getDatas().get(i)).getContrast();
                    if(AdvancedComputingConstant.NAME_MAP.containsKey(contrast)) {
                        ((QueryChartResultVO.MeasureData) queryChartResultVO.getDatas().get(i)).setCompareData(null);
                        ((QueryChartResultVO.MeasureData) queryChartResultVO.getDatas().get(i)).setCompareGroupData(null);
                    }
                }
            }
        }
    }

    /**
     * 查询图表
     * @param queryChartParameterVO
     * @param queryChartResultVO
     * @return
     */
    public QueryChartResultVO queryChart(QueryChartParameterVO queryChartParameterVO, QueryChartResultVO queryChartResultVO) {
        long start = System.currentTimeMillis();
        if(queryChartResultVO == null) {
            queryChartResultVO = new QueryChartResultVO();
        }
        if (CollectionUtil.isEmpty(queryChartParameterVO.getDimension()) && CollectionUtil.isEmpty(queryChartParameterVO.getMeasure())) {
            return queryChartResultVO;
        }
        QueryChartBO queryChartBO = initQueryChartBO(queryChartParameterVO, queryChartResultVO);
        log.info("queryChart1--------------------------------" + (System.currentTimeMillis() - start));
        if (StrUtil.startWith(queryChartBO.getOlapChart().getType(), ChartTypeConstant.RETAIN)) {
            // 留存图单独处理
            handleRetainData(queryChartBO, queryChartResultVO);
            log.info("queryChart2--------------------------------" + (System.currentTimeMillis() - start));
        } else {
            handleChartData(queryChartBO, queryChartResultVO);
            log.info("queryChart3--------------------------------" + (System.currentTimeMillis() - start));
        }
        return queryChartResultVO;
    }

    private void handleChartData(QueryChartBO queryChartBO, QueryChartResultVO queryChartResultVO) {
        long start = System.currentTimeMillis();
        addVirtualMeasure(queryChartBO);
        ExecuteSqlBO executeSqlBO = buildAndExecuteSql(queryChartBO);
        log.info("handleChartData1--------------------------------" + (System.currentTimeMillis() - start));
        List<Object> datas = handleData(queryChartBO.getDimensionList(), queryChartBO.getMeasureList(), queryChartBO.getTableColumnMap(), executeSqlBO.getResult());
        handleContrastData(datas, queryChartBO);
        handleProportionData(datas);
        handlePercentOfMaxData(datas);
        removeVirtualMeasureAndVirtualDimension(queryChartBO.getDimensionList(), queryChartBO.getMeasureList(), datas);
        handleGroupData(queryChartBO.getDimensionList(), queryChartBO.getMeasureList(), datas, queryChartBO.getOlapChart().getType());
        formatDimension(datas);
        queryChartResultVO.setDatas(handleEmptyData(datas));
        queryChartResultVO.setCacheHit(executeSqlBO.getCacheHit());
        queryChartResultVO.setDatasTime(executeSqlBO.getExecuteTime());
        queryChartResultVO.setRowFormatDataList(handleRowFormatDataList(queryChartBO.getRowFormat(), queryChartResultVO.getDatas()));
        log.info("handleChartData2--------------------------------" + (System.currentTimeMillis() - start));
    }

    /**
     * 组装查询图表业务类
     * @param queryChartParameterVO
     * @param queryChartResultVO
     * @return
     */
    public QueryChartBO initQueryChartBO(QueryChartParameterVO queryChartParameterVO, QueryChartResultVO queryChartResultVO) {
        preQueryChart(queryChartParameterVO);
        OlapChart olapChart = new OlapChart();
        olapChart.setType(queryChartParameterVO.getChartType());
        olapChart.setLimit(queryChartParameterVO.getLimit());
        List<OlapChartDimension> dimensionList =  queryChartParameterVO.getDimension();
        List<OlapChartMeasure> measureList = queryChartParameterVO.getMeasure();
        Map<String, QueryTableColumnResultBO> tableColumnMap = tableColumnService.getTableColumn(queryChartParameterVO);
        QueryChartBO queryChartBO = new QueryChartBO();
        queryChartBO.setOlapChart(olapChart);
        queryChartBO.setDimensionList(dimensionList);
        queryChartBO.setMeasureList(measureList);
        queryChartBO.setTableColumnMap(tableColumnMap);
        queryChartBO.setConditionSql(queryChartParameterVO.getConditionSql());
        queryChartBO.setHavingSql(queryChartParameterVO.getHavingSql());
        queryChartBO.setWithSql(queryChartParameterVO.getWithSql());
        queryChartBO.setShowCurrentGroup(queryChartParameterVO.getShowCurrentGroup());
        queryChartBO.setRowFormat(queryChartParameterVO.getRowFormat());
        queryChartBO.setQueryChartParameterVO(queryChartParameterVO);
        queryChartBO.setQueryChartResultVO(queryChartResultVO);
        OlapDsDatabase dsDatabase = new OlapDsDatabase();
        dsDatabase.setName(queryChartParameterVO.getDatabaseName());
        dsDatabase.setNamespace(NameSpaceConstant.CLICKHOUSE);
        dsDatabase.setDataSource(queryChartParameterVO.getDataSource());
        queryChartBO.setDsDatabase(dsDatabase);
        OlapDsTable olapDsTable = getTable(queryChartParameterVO, queryChartBO);
        queryChartBO.setOlapDsTable(olapDsTable);
        return queryChartBO;
    }

    private OlapDsTable getTable(QueryChartParameterVO queryChartParameterVO, QueryChartBO queryChartBO) {
        OlapDsTable olapDsTable = new OlapDsTable();
        olapDsTable.setName(queryChartParameterVO.getTableName());
        if(StrUtil.isNotEmpty(queryChartParameterVO.getViewSql())) {
            olapDsTable.setViewSql(queryChartParameterVO.getViewSql());
            olapDsTable.setIsView(true);
        }
        if(BooleanUtil.isTrue(queryChartParameterVO.getTableAppendFinal())) {
            olapDsTable.setAppendFinal(true);
        }
        if(!isAllEmptyCondition(queryChartParameterVO.getCompare())) {
            handleTableForCompare(queryChartParameterVO, olapDsTable, queryChartBO);
        }
        return olapDsTable;
    }

    /**
     * 数据比对需要填充数据保证数据是连续的
     * @param queryChartParameterVO
     * @param olapDsTable
     */
    private void handleTableForCompare(QueryChartParameterVO queryChartParameterVO, OlapDsTable olapDsTable, QueryChartBO queryChartBO) {
        List<QueryTableColumnResultBO> tableColumnResultBOList = CollectionUtil.isNotEmpty(queryChartParameterVO.getViewColumns())
                ? queryChartParameterVO.getViewColumns()
                : tableColumnService.queryColumnList(queryChartParameterVO);
        Map<String, QueryTableColumnResultBO> nameToColumnMap = tableColumnResultBOList.stream().collect(Collectors.toMap(QueryTableColumnResultBO::getColumnName, Function.identity()));

        Collection<QueryChartParameterVO.ConditionVO> allCondition = CollectionUtil.union(queryChartParameterVO.getDashboard(), queryChartParameterVO.getScreen());
        Map<String, QueryChartParameterVO.ConditionVO> nameToConditionMap = allCondition
                .stream().collect(Collectors.toMap(QueryChartParameterVO.ConditionVO::getName, Function.identity(), (v1, v2) -> v1));
        List<String> conditionNameList = allCondition.stream()
                .filter(v -> StrUtil.isNotEmpty(v.getValue()))
                .filter(v -> !StrUtil.equals(v.getValue(), "[]"))
                .filter(v -> nameToColumnMap.containsKey(v.getName()))
                .map(QueryChartParameterVO.ConditionVO::getName).collect(Collectors.toList());

        Map<String, List<String>> conditionNameToEnumMap = getConditionNameToEnumList(queryChartParameterVO, nameToConditionMap, conditionNameList);

        // 条件枚举值的笛卡儿积
        List<List<String>> result = new ArrayList<>();
        recursionGenerateSku(conditionNameList.stream().filter(conditionNameToEnumMap::containsKey).map(conditionNameToEnumMap::get).collect(Collectors.toList()),
                result, 0, new ArrayList<String>());

        StringBuffer templateStringBuffer = getTemplate(conditionNameToEnumMap, tableColumnResultBOList);
        StringBuilder sql1 = new StringBuilder();
        for (List<String> subList : result) {
            String template = templateStringBuffer.toString();
            for (int i = 0; i < subList.size(); i++) {
                String value = subList.get(i);
                String name = conditionNameList.get(i);
                String columnType = Optional.ofNullable(nameToColumnMap.get(name)).map(QueryTableColumnResultBO::getColumnType).orElse("");
                String nameOccupy =  "|||" + name + "|||";
                if(StrUtil.equals(columnType, "Date")) {
                    template = StrUtil.replace(template, nameOccupy, StrUtil.format("toDate('{}')", value));
                } else if(StrUtil.equals(columnType, "DateTime")) {
                    template = StrUtil.replace(template, nameOccupy, StrUtil.format("toDateTime('{}')", value));
                } else if(StrUtil.contains(columnType, "Int")) {
                    template = StrUtil.replace(template, nameOccupy, StrUtil.format("{}", value));
                } else {
                    template = StrUtil.replace(template, nameOccupy, StrUtil.format("'{}'", value));
                }
            }
            if(sql1.length() == 0) {
                sql1.append(template);
            } else {
                sql1.append(" union all ").append(template);
            }
        }
        if(StrUtil.isEmpty(sql1)) {
            return;
        }

        StringBuilder sql2 = new StringBuilder(StrUtil.format("select {} from ",
                tableColumnResultBOList.stream().map(x -> StrUtil.format("`{}`", x.getColumnName())).collect(Collectors.joining(","))));
        if(BooleanUtil.isTrue(olapDsTable.getIsView())) {
            sql2.append(olapDsTable.getViewSql());
        } else {
            sql2.append(StrUtil.format("{}.{}", queryChartParameterVO.getDatabaseName(), queryChartParameterVO.getTableName()))
                    .append(BooleanUtil.isTrue(olapDsTable.getAppendFinal()) ? " final" : "");
        }
        /// clickhouse的bug，手动下推条件会导致clickhouse崩溃。
//        Condition whereSql = getWhereSql(queryChartBO);
//        sql2.append(" where ").append(whereSql.toString());
        //String viewSql = StrUtil.format("({}) as _t1 full join ({}) as _t2 using ({})", sql2, sql1, CollectionUtil.join(conditionNameList, ","));
        String viewSql = StrUtil.format("({} as _t1 full join ({}) as _t2 using ({}))", sql2, sql1, CollectionUtil.join(conditionNameList, ","));

        /// clickhouse full join配合where查询有bug，https://github.com/ClickHouse/ClickHouse/issues/20497。如果clickhouse的bug修复，可以用如下方法。
        /*String sql2;
        if(BooleanUtil.isTrue(olapDsTable.getIsView())) {
            sql2 = olapDsTable.getViewSql();
        } else {
            sql2 = StrUtil.format("{}.{}", queryChartParameterVO.getDatabaseName(), queryChartParameterVO.getTableName())
                    + (BooleanUtil.isTrue(olapDsTable.getAppendFinal()) ? " final" : "");
        }
        String viewSql = StrUtil.format("{} as _t1 full join ({}) as _t2 using ({})", sql2, sql1, CollectionUtil.join(conditionNameList, ","));*/
        olapDsTable.setViewSql(viewSql);
        olapDsTable.setIsView(true);
    }

    /**
     * 获取条件名对应条件枚举值
     * @param nameToConditionMap
     * @param conditionNameList
     * @return
     */
    private Map<String, List<String>> getConditionNameToEnumList(QueryChartParameterVO queryChartParameterVO, Map<String, QueryChartParameterVO.ConditionVO> nameToConditionMap, List<String> conditionNameList) {
        Map<String, List<String>> conditionNameToEnumList = MapUtil.newHashMap();
        Map<String, OlapChartDimension> dimensionNameToDimensionMap = queryChartParameterVO.getDimension().stream().collect(Collectors.toMap(OlapChartDimension::getName, Function.identity()));
        Set<String> measureNameSet = queryChartParameterVO.getMeasure().stream().map(OlapChartMeasure::getName).collect(Collectors.toSet());
        Iterator<String> conditionNameListIterator = conditionNameList.iterator();
        while (conditionNameListIterator.hasNext()) {
            String conditionName = conditionNameListIterator.next();
            if(measureNameSet.contains(conditionName)) {
                conditionNameListIterator.remove();
                continue;
            }
            QueryChartParameterVO.ConditionVO conditionVO = nameToConditionMap.get(conditionName);
            List<String> conditionEnum = new ArrayList<>();
            if(FilterLogicConstant.EQ.equals(conditionVO.getLogic())) {
                conditionEnum.add(conditionVO.getValue());
            } else if(FilterLogicConstant.IN.equals(conditionVO.getLogic())) {
                conditionEnum.addAll(JacksonUtil.fromJSONArray(conditionVO.getValue(), String.class).stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList()));
            } else if(FilterLogicConstant.BETWEEN.equals(conditionVO.getLogic())) {
                String  value = handleDateValue(conditionVO.getValue());
                List<String> startAndEndValue = JacksonUtil.fromJSONArray(value, String.class);
                String startValue = startAndEndValue.get(0);
                String endValue = startAndEndValue.get(1);
                oneByOneTimeEnum(conditionEnum, startValue, endValue, Optional.ofNullable(dimensionNameToDimensionMap.get(conditionName)).map(OlapChartDimension::getGroup).orElse(TimeUnitConstant.DAY), conditionVO.getFunc());
            }

            if(!dimensionNameToDimensionMap.containsKey(conditionName)) {
                // 如果条件不在维度中，取任意一个值即可，减少笛卡尔积的数量。
                conditionEnum = CollectionUtil.sub(conditionEnum, 0, 1);
            }
            conditionNameToEnumList.put(conditionVO.getName(), conditionEnum);
        }
        return conditionNameToEnumList;
    }

    private void oneByOneTimeEnum(List<String> dimensionEnum, String startValue, String endValue, String dimensionGroup, String conditionFunc) {
        if(!StrUtil.equalsAny(dimensionGroup, TimeUnitConstant.YEAR, TimeUnitConstant.MONTH, TimeUnitConstant.WEEK, TimeUnitConstant.DAY, TimeUnitConstant.HOUR)) {
            return;
        }
        DateTime startDate = parseDateByLength(DateUtil.format(parseDateByLength(startValue), getFormat(conditionFunc)));
        DateTime endDate = parseDateByLength(DateUtil.format(parseDateByLength(endValue), getFormat(conditionFunc)));

        if (startDate.isBeforeOrEquals(endDate)) {
            dimensionEnum.add(DateUtil.format(parseDateByLength(startValue), DatePattern.NORM_DATETIME_PATTERN));
            DateField dateField = StrUtil.equals(dimensionGroup, TimeUnitConstant.YEAR) ? DateField.YEAR :
                    StrUtil.equals(dimensionGroup, TimeUnitConstant.MONTH) ? DateField.MONTH :
                            StrUtil.equals(dimensionGroup, TimeUnitConstant.WEEK) ? DateField.WEEK_OF_YEAR :
                                    StrUtil.equals(dimensionGroup, TimeUnitConstant.DAY) ? DateField.DAY_OF_YEAR :
                                            StrUtil.equals(dimensionGroup, TimeUnitConstant.HOUR) ? DateField.HOUR_OF_DAY : DateField.DAY_OF_YEAR;
            startDate = DateUtil.offset(parseDateByLength(startValue), dateField, 1);
            oneByOneTimeEnum(dimensionEnum, DateUtil.format(startDate, getFormat(dimensionGroup)), endValue, dimensionGroup, conditionFunc);
        }
    }

    private String getFormat(String conditionFunc) {
        return StrUtil.equals(conditionFunc, TimeUnitConstant.YEAR) ? "yyyy" :
                StrUtil.equals(conditionFunc, TimeUnitConstant.MONTH) ? "yyyy-MM" :
                        StrUtil.equals(conditionFunc, TimeUnitConstant.WEEK) ? "YYYY(ww)" :
                                StrUtil.equals(conditionFunc, TimeUnitConstant.DAY) ? "yyyy-MM-dd" :
                                        StrUtil.equals(conditionFunc, TimeUnitConstant.HOUR) ? "yyyy-MM-dd HH" : "yyyy-MM-dd";
    }

    private DateTime parseDateByLength(String value) {
        int length = value.length();
        if("yyyy".length() == length) {
            return DateUtil.parse(value, "yyyy");
        }
        if("yyyy-MM".length() == length) {
            return DateUtil.parse(value, "yyyy-MM");
        }
        if("yyyy-MM-dd".length() == length) {
            return DateUtil.parse(value, "yyyy-MM-dd");
        }
        if("yyyy-MM-dd HH".length() == length) {
            return DateUtil.parse(value, "yyyy-MM-dd HH");
        }
        if("YYYY(ww)".length() == length) {
            return DateUtil.parse(value, "YYYY(ww)");
        }
        return DateUtil.parse(value);
    }

    /**
     * 拼接union的SQL模板
     * @param dimensionNameToEnumList
     * @param tableColumnResultBOList
     * @return
     */
    private StringBuffer getTemplate(Map<String, List<String>> dimensionNameToEnumList, List<QueryTableColumnResultBO> tableColumnResultBOList) {
        StringBuffer sb = new StringBuffer("select ");
        for (QueryTableColumnResultBO tableColumnResultBO : tableColumnResultBOList) {
            if(dimensionNameToEnumList.containsKey(tableColumnResultBO.getColumnName())) {
                sb.append("|||" +  tableColumnResultBO.getColumnName() + "|||").append(" as ").append(StrUtil.format("`{}`", tableColumnResultBO.getColumnName())).append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb;
    }

    /**
     * 功能描述:
     * 〈获取笛卡尔积〉
     *
     * @params : [sourceData, targetData, level, appendData]
     * @return : void
     * @author : cwl
     * @date : 2019/7/10 16:24
     */
    private <T> void recursionGenerateSku(List<List<T>> sourceData,
                                          List<List<T>> targetData,
                                          int level,
                                          List<T> appendData) {
        if(CollectionUtil.isEmpty(sourceData)) {
            return;
        }
        if (level < sourceData.size() - 1) {
            List<T> innerSkuPropertyValueList = sourceData.get(level);
            for (T spv : innerSkuPropertyValueList) {
                List<T> innerAppendData = new ArrayList<>(appendData);
                innerAppendData.add(spv);
                recursionGenerateSku(sourceData, targetData, level + 1, innerAppendData);
            }
        } else {
            List<T> innerSkuPropertyValueList = sourceData.get(level);
            for (T spv : innerSkuPropertyValueList) {
                List<T> innerAppendData = new ArrayList<>(appendData);
                innerAppendData.add(spv);
                targetData.add(innerAppendData);
            }
        }
    }

    private void preQueryChart(QueryChartParameterVO queryChartParameterVO) {
        if(queryChartParameterVO.getDashboard() == null) {
            queryChartParameterVO.setDashboard(Collections.emptyList());
        }
        if(queryChartParameterVO.getScreen() == null) {
            queryChartParameterVO.setScreen(Collections.emptyList());
        }
        if(queryChartParameterVO.getDimension() == null) {
            queryChartParameterVO.setDimension(Collections.emptyList());
        }
        if(queryChartParameterVO.getMeasure() == null) {
            queryChartParameterVO.setMeasure(Collections.emptyList());
        }
        for (int i = 0; i < queryChartParameterVO.getDimension().size(); i++) {
            if(queryChartParameterVO.getDimension().get(i).getId() == null) {
                queryChartParameterVO.getDimension().get(i).setId(i);
            }
        }
        for (int i = 0; i < queryChartParameterVO.getMeasure().size(); i++) {
            if(queryChartParameterVO.getMeasure().get(i).getId() == null) {
                queryChartParameterVO.getMeasure().get(i).setId(i);
            }
        }
    }

    /**
     * 维度格式化
     * @param datas
     */
    private void formatDimension(List<Object> datas) {
        if(CollectionUtil.isEmpty(datas)) {
            return;
        }
        for (int i = 0; i < datas.size(); i++) {
            Object data = datas.get(i);
            if(data instanceof QueryChartResultVO.DimensionData) {
                QueryChartResultVO.DimensionData dimensionData = (QueryChartResultVO.DimensionData) data;
                String format = dimensionData.getFormat();
                if(StrUtil.isEmpty(format)) {
                    continue;
                }
                dimensionData.setData(formatData(dimensionData.getData(), format));
                dimensionData.setDistinctData(formatData(dimensionData.getDistinctData(), format));
            }
        }
    }

    /**
     * 列格式转为行格式
     * @param rowFormatData
     * @param datas
     */
    private List<List<String>> handleRowFormatDataList(Boolean rowFormatData, List<Object> datas) {
        if(!BooleanUtil.isTrue(rowFormatData)) {
            return null;
        }
        List<List<String>> rowDataList = Collections.emptyList();
        for (int i = 0; i < datas.size(); i++) {
            Object dataObj = datas.get(i);
            List<String> colData = ReflectUtil.invoke(dataObj, "getData");
            if(i == 0) {
                rowDataList = new ArrayList<>(colData.size());
            }
            for (int j = 0; j < colData.size(); j++) {
                String value = colData.get(j);
                if(i == 0) {
                    rowDataList.add(new ArrayList<>(datas.size()));
                }
                List<String> rowData = rowDataList.get(j);
                rowData.add(value);
            }
        }
        return rowDataList;
    }

    private List<String> formatData(List<String> strList, String format) {
        if(CollectionUtil.isEmpty(strList)) {
            return strList;
        }
        List<String> formatList = new ArrayList<>();
        for (String str : strList) {
            try {
                if (format.equals(FormatConstant.Y)) {
                    DateTime dateTime = DateUtil.parse(str, "yyyy");
                    str = DateUtil.format(dateTime, "yyyy");
                } else if (format.equals(FormatConstant.Ym)) {
                    DateTime dateTime = DateUtil.parse(str, "yyyy-MM");
                    str = DateUtil.format(dateTime, "yyyy-MM");
                } else if (format.equals(FormatConstant.Ymd)) {
                    DateTime dateTime = DateUtil.parse(str, "yyyy-MM-dd");
                    str = DateUtil.format(dateTime, "yyyy-MM-dd");
                } else if (format.equals(FormatConstant.zhY)) {
                    DateTime dateTime = DateUtil.parse(str, "yyyy");
                    str = DateUtil.format(dateTime, "yyyy年");
                } else if (format.equals(FormatConstant.zhYm)) {
                    DateTime dateTime = DateUtil.parse(str, "yyyy-MM");
                    str = DateUtil.format(dateTime, "yyyy年MM月");
                } else if (format.equals(FormatConstant.zhYmd)) {
                    DateTime dateTime = DateUtil.parse(str, "yyyy-MM-dd");
                    str = DateUtil.format(dateTime, "yyyy年MM月dd日");
                } else if (format.equals(FormatConstant.zhH)) {
                    DateTime dateTime = DateUtil.parse(str, "yyyy-MM-dd HH");
                    str = DateUtil.format(dateTime, "HH时");
                } else if (format.equals(FormatConstant.zhHM)) {
                    DateTime dateTime = DateUtil.parse(str, "yyyy-MM-dd HH:mm");
                    str = DateUtil.format(dateTime, "HH时mm分");
                } else if (format.equals(FormatConstant.zhM)) {
                    DateTime dateTime = DateUtil.parse(str, "yyyy-MM-dd HH:mm");
                    str = DateUtil.format(dateTime, "mm分");
                } else if (format.equals(FormatConstant.zhS)) {
                    DateTime dateTime = DateUtil.parse(str, "yyyy-MM-dd HH:mm:ss");
                    str = DateUtil.format(dateTime, "ss秒");
                } else if (format.equals(FormatConstant.Ywd)) {
                    str = formatWeek(str);
                }
            } catch (Exception e) {
                log.warn("{} format error using {}", str, format);
            }
            formatList.add(str);
        }
        if (strList.size() == formatList.size()) {
            return formatList;
        } else {
            return strList;
        }
    }

    public String formatWeek(String str) {
        String year = getYearUsingWeekGroup(str);
        String week = getWeekUsingWeekGroup(str);
        DateTime firstDayOfYear = DateUtil.parse(StrUtil.format("{}-01-01", year));
        while (DateUtil.dayOfWeek(firstDayOfYear) != 5) {
            // IW : ISO week number of year (The first Thursday of the new year is in week 1.)
            firstDayOfYear = DateUtil.offsetDay(firstDayOfYear, 1);
        }

        DateTime offsetDay = DateUtil.offsetDay(firstDayOfYear, (Integer.parseInt(week) -1) * 7);
        str = StrUtil.format("{}-{}",
                DateUtil.format(DateUtil.beginOfWeek(offsetDay), "yyyyMMdd"),
                DateUtil.format(DateUtil.endOfWeek(offsetDay), "yyyyMMdd"));
        return str;
    }

    public int isoWeekOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setMinimalDaysInFirstWeek(4);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        /* Set date */
        calendar.setTime(date);

        /* Get ISO8601 week number */
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 添加虚拟度量。处理行转列
     *
     * @param queryChartBO
     */
    private void addVirtualMeasure(QueryChartBO queryChartBO) {
        List<OlapChartMeasure> measureList = queryChartBO.getMeasureList();
        if (CollectionUtil.isEmpty(measureList)) {
            return;
        }

        //行转列
        if (measureList.size() == 1) {
            OlapChartDimension chartDimension = null;
            for (OlapChartDimension dimension : queryChartBO.getDimensionList()) {
                if (BooleanUtil.isTrue(Optional.ofNullable(dimension).map(OlapChartDimension::getIsColumn).orElse(null))) {
                    chartDimension = dimension;
                }
            }
            if (ObjectUtil.isNotNull(chartDimension)) {
                String dimensionName = chartDimension.getName();
                Integer dimensionOrder = chartDimension.getOrder();
                Integer dimensionId = chartDimension.getId();
                List<String> sqlResult = getDimensionGroupForRowToColumn(queryChartBO, dimensionName, dimensionOrder);
                OlapChartMeasure onlyOneMeasure = measureList.get(0);
                String measureName = onlyOneMeasure.getName();
                measureName = Optional.ofNullable(queryChartBO.getTableColumnMap().get(measureName)).map(QueryTableColumnResultBO::getColumnExp).filter(StrUtil::isNotEmpty).orElse(measureName);
                String measureFunc = onlyOneMeasure.getFunc();

                if (sqlResult.size() > 150) {
                    throw new BusinessException("列数太多，无法使用行转列");
                }
                for (String sqlResultValue : sqlResult) {
                    String virtualMeasureName = getRowToColumnVirtualMeasureName(dimensionName, measureName, measureFunc, sqlResultValue);
                    OlapChartMeasure virtualChartMeasure = new OlapChartMeasure();
                    virtualChartMeasure.setName(virtualMeasureName);
                    virtualChartMeasure.setFunc(measureFunc);
                    virtualChartMeasure.setAliasName(StrUtil.isEmpty(sqlResultValue) ? "null" : sqlResultValue);
                    virtualChartMeasure.setVirtual(2);
                    measureList.add(virtualChartMeasure);
                }
                measureList.remove(0);
                queryChartBO.getDimensionList().removeIf(dimension -> ObjectUtil.equal(dimension.getId(), dimensionId));
            }
        }
    }

    private String getRowToColumnVirtualMeasureName(String dimensionName, String measureName, String measureFunc, String sqlResultValue) {
        String virtualMeasureName = "";
        if (StrUtil.equalsAny(measureFunc, "sum", "max", "min", "avg", "count_distinct")) {
            if (sqlResultValue == null) {
                virtualMeasureName = StrUtil.format("if({} is null , {}, null)", dimensionName, measureName);
            } else {
                virtualMeasureName = StrUtil.format("if( {} = '{}', {}, null)", dimensionName, sqlResultValue, measureName);
            }
        } else if ("count".equals(measureFunc)) {
            if (sqlResultValue == null) {
                virtualMeasureName = StrUtil.format("if({} is null , 1, null)", dimensionName);
            } else {
                virtualMeasureName = StrUtil.format("if({} = '{}', 1, null)", dimensionName, sqlResultValue);
            }
        } else {
            if (sqlResultValue == null) {
                virtualMeasureName = StrUtil.format("if({} is null , {}, assumeNotNull(null))", dimensionName, measureName);
            } else {
                virtualMeasureName = StrUtil.format("if( {} = '{}', {}, assumeNotNull(null))", dimensionName, sqlResultValue, measureName);
            }
        }
        if (StrUtil.isEmpty(virtualMeasureName)) {
            throw new BusinessException("无效的度量方法" + measureFunc);
        }
        return virtualMeasureName;
    }

    private List<String> getDimensionGroupForRowToColumn(QueryChartBO queryChartBO, String dimensionName, Integer dimensionOrder) {
        OlapDsDatabase database = queryChartBO.getDsDatabase();
        OlapDsTable table = queryChartBO.getOlapDsTable();

        String asName = "dimension1";
        String selectName = StrUtil.format("`{}` as {}", dimensionName, asName);
        List<SortField<?>> sortFieldList = new LinkedList<>();
        List<SortField<?>> defaultSortFieldList = new LinkedList<>();
        buildDimensionOrder(sortFieldList, dimensionName, asName, dimensionOrder, null, database.getNamespace(), defaultSortFieldList);

        Condition condition = DSL.trueCondition();
        condition = buildCondition(condition, queryChartBO.getQueryChartParameterVO().getScreen(), table, database, queryChartBO.getTableColumnMap(),null);
        condition = buildCondition(condition, queryChartBO.getQueryChartParameterVO().getDashboard(), table, database, queryChartBO.getTableColumnMap(), null);
        condition = buildConditionSql(condition, queryChartBO.getConditionSql());

        String fromSql = getFromSql(database, table);
        DSLContext create = DSL.using(SQLDialect.DEFAULT);

        SelectSeekStepN<Record1<Object>> query = create.select(field(selectName))
                .from(table(fromSql))
                .where(condition)
                .groupBy(field(dimensionName))
                .orderBy(sortFieldList);

        List<Object> bindValues = getBindValues(queryChartBO.getQueryChartParameterVO().getViewBindValues(), query.getBindValues());
        List<List<String>> salResult = SqlUtil.queryColumnList(query.getSQL(), bindValues, DatabaseUtil.getDB(database.getNamespace(), database.getDataSource()));
        return salResult.get(0);
    }

    /**
     * 移除虚拟的维度和度量
     *
     * @param dimensionList
     * @param measureList
     */
    private void removeVirtualMeasureAndVirtualDimension(List<OlapChartDimension> dimensionList, List<OlapChartMeasure> measureList, List<Object> datas) {
        if (Optional.ofNullable(CollectionUtil.getFirst(dimensionList)).map(OlapChartDimension::getVirtual).orElse(0) == 1) {
            datas.remove(0);
        }
        measureList.removeIf(measure -> Optional.ofNullable(measure).map(OlapChartMeasure::getVirtual).orElse(0) > 0);
        dimensionList.removeIf(dimension -> Optional.ofNullable(dimension).map(OlapChartDimension::getVirtual).orElse(0) > 0);
    }

    /**
     * 处理sql执行结果
     * @param dimensionList
     * @param measureList
     * @param tableColumnMap
     * @param sqlResult
     * @return
     */
    private List<Object> handleData(List<OlapChartDimension> dimensionList, List<OlapChartMeasure> measureList, Map<String, QueryTableColumnResultBO> tableColumnMap, List<List<String>> sqlResult) {
        List<Object> datas = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(sqlResult)) {
            int index = 0;
            for (OlapChartDimension dimension : dimensionList) {
                QueryChartResultVO.DimensionData dimensionData = BeanUtil.toBean(dimension, QueryChartResultVO.DimensionData.class);
                dimensionData.setCategory("dimension");
                dimensionData.setData(sqlResult.get(index));
                String columnNickname = Optional.ofNullable(tableColumnMap.get(dimension.getName())).map(QueryTableColumnResultBO::getColumnNickname).orElse("");
                String displayName = StrUtil.isNotEmpty(dimension.getAliasName()) ? dimension.getAliasName() : (StrUtil.isNotEmpty(columnNickname) ? columnNickname : dimension.getName());
                dimensionData.setDisplayName(displayName);
                if (StrUtil.isNotEmpty(dimension.getGroup()) && StrUtil.isEmpty(dimension.getAliasName())) {
                    dimensionData.setDisplayName(dimensionData.getDisplayName() + "_" + TimeUnitConstant.NAME_MAP.get(dimension.getGroup()));
                }
                dimensionData.setFormat(Optional.ofNullable(dimension).map(OlapChartDimension::getFormat).orElse(null));
                datas.add(dimensionData);
                index++;
            }
            for (OlapChartMeasure measure : measureList) {
                QueryChartResultVO.MeasureData measureData = BeanUtil.toBean(measure, QueryChartResultVO.MeasureData.class);
                measureData.setCategory("measure");
                measureData.setData(sqlResult.get(index));
                String columnNickname = Optional.ofNullable(tableColumnMap.get(measure.getName())).map(QueryTableColumnResultBO::getColumnNickname).orElse("");
                String displayName = StrUtil.isNotEmpty(measure.getAliasName()) ? measure.getAliasName() : (StrUtil.isNotEmpty(columnNickname) ? columnNickname : measure.getName());
                measureData.setDisplayName(displayName);
                if (StrUtil.isNotEmpty(measure.getFunc()) && StrUtil.isEmpty(measure.getAliasName())) {
                    measureData.setDisplayName(measureData.getDisplayName() + "_" + FunctionConstant.NAME_MAP.get(measure.getFunc()));
                }
                measureData.setSummary(Optional.ofNullable(measure).map(OlapChartMeasure::getSummary).orElse(null));
                datas.add(measureData);
                index++;
            }
        }
        return datas;
    }

    private List<Object> handleEmptyData(List<Object> datas) {
        Integer emptyData = 0;
        for (Object dataObj : datas) {
            List<String> data = ReflectUtil.invoke(dataObj, "getData");
            if (CollectionUtil.isEmpty(data)) {
                emptyData++;
            }
        }
        if (emptyData == datas.size()) {
            return Collections.EMPTY_LIST;
        }
        return datas;
    }

    /**
     * @param datas 处理总计占比
     */
    private void handleProportionData(List<Object> datas) {
        ListIterator<Object> iterator = datas.listIterator();
        while (iterator.hasNext()) {
            Object dataObj = iterator.next();
            if (dataObj instanceof QueryChartResultVO.MeasureData) {
                QueryChartResultVO.MeasureData currentMeasureData = (QueryChartResultVO.MeasureData) dataObj;
                if(BooleanUtil.isTrue(currentMeasureData.getProportion())) {
                    List<String> data = currentMeasureData.getData();
                    BigDecimal total = NumberUtil.add(data.stream().filter(NumberUtil::isNumber).toArray(String[]::new));
                    QueryChartResultVO.MeasureData proportionMeasureData = new QueryChartResultVO.MeasureData();
                    proportionMeasureData.setProportion(currentMeasureData.getProportion());
                    proportionMeasureData.setName(currentMeasureData.getName());
                    proportionMeasureData.setCategory(currentMeasureData.getCategory());
                    proportionMeasureData.setDisplayName(currentMeasureData.getDisplayName());
                    proportionMeasureData.setDisplayName(proportionMeasureData.getDisplayName() + "占比");
                    List<String> proportionData = new ArrayList<>(data.size());
                    for (String datum : data) {
                        proportionData.add(total.equals(new BigDecimal(0)) || !NumberUtil.isNumber(datum) ? "--" : NumberUtil.round(NumberUtil.div(datum, total.toString()), 4) .toString());
                    }
                    proportionMeasureData.setData(proportionData);
                    iterator.add(proportionMeasureData);
                    currentMeasureData.setProportion(null);
                }
            }
        }
    }

    /**
     * @param datas 处理最大值占比
     */
    private void handlePercentOfMaxData(List<Object> datas) {
        ListIterator<Object> iterator = datas.listIterator();
        while (iterator.hasNext()) {
            Object dataObj = iterator.next();
            if (dataObj instanceof QueryChartResultVO.MeasureData) {
                QueryChartResultVO.MeasureData currentMeasureData = (QueryChartResultVO.MeasureData) dataObj;
                if(BooleanUtil.isTrue(currentMeasureData.getPercentOfMax())) {
                    List<String> data = currentMeasureData.getData();
                    BigDecimal max = CollectionUtil.isEmpty(data) ? new BigDecimal(0) : NumberUtil.max(data.stream().filter(NumberUtil::isNumber).map(BigDecimal::new).toArray(BigDecimal[]::new));
                    QueryChartResultVO.MeasureData measureData = new QueryChartResultVO.MeasureData();
                    measureData.setPercentOfMax(currentMeasureData.getPercentOfMax());
                    measureData.setName(currentMeasureData.getName());
                    measureData.setCategory(currentMeasureData.getCategory());
                    measureData.setDisplayName(currentMeasureData.getDisplayName());
                    measureData.setDisplayName(measureData.getDisplayName() + "最大值占比");
                    List<String> percentOfMaxData = new ArrayList<>(data.size());
                    for (String datum : data) {
                        percentOfMaxData.add(max.equals(new BigDecimal(0)) || !NumberUtil.isNumber(datum) ? "--" : NumberUtil.round(NumberUtil.div(datum, max.toString()), 4).toString());
                    }
                    measureData.setData(percentOfMaxData);
                    iterator.add(measureData);
                    currentMeasureData.setPercentOfMax(null);
                }
            }
        }
    }

    /**
     * 处理同比环比
     *
     * @param datas
     */
    private void handleContrastData(List<Object> datas, QueryChartBO queryChartBO) {
        List<OlapChartDimension> dimensionList = queryChartBO.getDimensionList();
        List<OlapChartMeasure> measureList = queryChartBO.getMeasureList();
        if (CollectionUtil.isEmpty(dimensionList) || CollectionUtil.isEmpty(datas)) {
            return;
        }
        if (!TimeUnitConstant.NAME_MAP.containsKey(dimensionList.get(0).getGroup())) {
            return;
        }
        List<List<String>> dimensionDataList = new ArrayList<>();
        QueryChartResultVO.DimensionData firstDimensionData = (QueryChartResultVO.DimensionData) datas.get(0);
        String group = firstDimensionData.getGroup();

        Map<String, Map<String, String>> allDimensionToMeasureData = getAllDimensionToMeasureData(datas);
        Set<String> preCycleTimeSet = new HashSet<>();
        ListIterator<Object> iterator = datas.listIterator();
        while (iterator.hasNext()) {
            Object data = iterator.next();
            if (data instanceof QueryChartResultVO.DimensionData) {
                QueryChartResultVO.DimensionData dimensionData = (QueryChartResultVO.DimensionData) data;
                dimensionDataList.add(dimensionData.getData());
            } else if (data instanceof QueryChartResultVO.MeasureData) {
                QueryChartResultVO.MeasureData currentMeasureData = (QueryChartResultVO.MeasureData) data;
                Map<String, String> dimensionToMeasureData = allDimensionToMeasureData.get(currentMeasureData.getName());

                String contrasts = Optional.ofNullable(currentMeasureData.getContrast()).orElse("");
                currentMeasureData.setContrast("");
                String[] splitContrast = contrasts.split(",");
                for (String contrast : splitContrast) {
                    if (StrUtil.isEmpty(contrast) || !AdvancedComputingConstant.NAME_MAP.containsKey(contrast)) {
                        continue;
                    }
                    contrast = autoYoy(group, contrast);
                    QueryChartResultVO.MeasureData contrastMeasureData = new QueryChartResultVO.MeasureData();
                    contrastMeasureData.setId(currentMeasureData.getId());
                    contrastMeasureData.setName(currentMeasureData.getName());
                    contrastMeasureData.setCategory(currentMeasureData.getCategory());
                    contrastMeasureData.setDisplayName(currentMeasureData.getDisplayName());
                    contrastMeasureData.setContrast(contrast);
                    contrastMeasureData.setDisplayName(contrastMeasureData.getDisplayName() + "(" + AdvancedComputingConstant.NAME_MAP.get(contrast) + ")");

                    List<String> advancedComputingData = new LinkedList<>();
                    String chartType = queryChartBO.getOlapChart().getType();
                    if (contrast.contains("mom")) {
                        handleMOMData(firstDimensionData, dimensionDataList, currentMeasureData, dimensionToMeasureData, contrast, advancedComputingData, preCycleTimeSet, chartType);
                    }
                    if (contrast.contains("yoy")) {
                        handleYOYData(firstDimensionData, dimensionDataList, currentMeasureData, dimensionToMeasureData, contrast, advancedComputingData, preCycleTimeSet, chartType);
                    }
                    contrastMeasureData.setData(advancedComputingData);
                    contrastMeasureData.setDimensionData(firstDimensionData.getData());
                    iterator.add(contrastMeasureData);
                }
            }
        }
        if(preCycleTimeSet.isEmpty()) {
            return;
        }
        // 如果没有命中同比或环比数据，再去数据库中查一次
        Map<String, Map<String, String>> secondAllDimensionToMeasureData = handleEmptyPreCycleTime(dimensionList, measureList, queryChartBO, preCycleTimeSet);
        if (MapUtil.isNotEmpty(secondAllDimensionToMeasureData)) {
            QueryChartResultVO.MeasureData currentMeasureData = null;
            Map<String, String> dimensionToMeasureData = null;
            ListIterator<Object> secondIterator = datas.listIterator();
            while (secondIterator.hasNext()) {
                Object data = secondIterator.next();
                if (data instanceof QueryChartResultVO.MeasureData) {
                    QueryChartResultVO.MeasureData measureData = (QueryChartResultVO.MeasureData) data;
                    String contrast = Optional.ofNullable(measureData.getContrast()).orElse("");
                    if (AdvancedComputingConstant.NAME_MAP.containsKey(contrast)) {
                        QueryChartResultVO.MeasureData contrastMeasureData = measureData;
                        List<String> advancedComputingData = contrastMeasureData.getData();
                        if (contrast.contains("mom") && currentMeasureData != null && dimensionToMeasureData != null) {
                            handleMOMData(firstDimensionData, dimensionDataList, currentMeasureData, dimensionToMeasureData, contrast, advancedComputingData);
                        }
                        if (contrast.contains("yoy") && currentMeasureData != null && dimensionToMeasureData != null) {
                            handleYOYData(firstDimensionData, dimensionDataList, currentMeasureData, dimensionToMeasureData, contrast, advancedComputingData);
                        }
                    } else {
                        currentMeasureData = measureData;
                        dimensionToMeasureData = secondAllDimensionToMeasureData.get(measureData.getName());
                    }
                }
            }
        }
    }

    /**
     * 感觉时间粒度，自动获取对应的同比
     * @param group
     * @param contrast
     * @return
     */
    public String autoYoy(String group, String contrast) {
        if(AdvancedComputingConstant.YOY.equals(contrast)) {
            if(TimeUnitConstant.HOUR.equals(group)) {
                contrast = AdvancedComputingConstant.D_YOY;
            } else if(TimeUnitConstant.DAY.equals(group)) {
                contrast = AdvancedComputingConstant.W_YOY;
            } else if(TimeUnitConstant.WEEK.equals(group)) {
                contrast = AdvancedComputingConstant.Y_YOY;
            } else if(TimeUnitConstant.MONTH.equals(group)) {
                contrast = AdvancedComputingConstant.Y_YOY;
            }
        } else if(AdvancedComputingConstant.YOY_RATE.equals(contrast)) {
            if(TimeUnitConstant.HOUR.equals(group)) {
                contrast = AdvancedComputingConstant.D_YOY_RATE;
            } else if(TimeUnitConstant.DAY.equals(group)) {
                contrast = AdvancedComputingConstant.W_YOY_RATE;
            } else if(TimeUnitConstant.WEEK.equals(group)) {
                contrast = AdvancedComputingConstant.Y_YOY_RATE;
            } else if(TimeUnitConstant.MONTH.equals(group)) {
                contrast = AdvancedComputingConstant.Y_YOY_RATE;
            }
        }
        return contrast;
    }

    private Map<String, Map<String, String>> handleEmptyPreCycleTime(List<OlapChartDimension> dimensionList, List<OlapChartMeasure> measureList, QueryChartBO queryChartBO,
                                                                     Set<String> preCycleTimeList) {
        if (CollectionUtil.isEmpty(preCycleTimeList)) {
            return null;
        }
        OlapChartDimension firstChartDimension = dimensionList.get(0);
        String fieldName = buildDimensionFieldName(queryChartBO.getDsDatabase().getNamespace(), queryChartBO.getTableColumnMap(), firstChartDimension);
        Condition condition = DSL.field(fieldName).in(preCycleTimeList);

        //图内筛选器
        condition = buildCondition(condition, queryChartBO.getQueryChartParameterVO().getScreen(), queryChartBO.getOlapDsTable(), queryChartBO.getDsDatabase(), queryChartBO.getTableColumnMap(), firstChartDimension.getName());
        //仪表盘全局筛选器
        condition = buildCondition(condition, queryChartBO.getQueryChartParameterVO().getDashboard(), queryChartBO.getOlapDsTable(), queryChartBO.getDsDatabase(), queryChartBO.getTableColumnMap(), firstChartDimension.getName());
        condition = buildShowCurrentGroupCondition(condition, queryChartBO);
        condition = buildConditionSql(condition, queryChartBO.getConditionSql());

        SelectConditionStep<Record> record = buildSelectFromWhereGroup(condition, queryChartBO.getBuildSqlBO());
        String sql = record.getSQL();
        List<Object> bindValues = record.getBindValues();
        if(StrUtil.isNotEmpty(queryChartBO.getWithSql())) {
            sql = StrUtil.format("with {} {}", queryChartBO.getWithSql(), sql);
        }

        ExecuteSqlBO executeSqlBO = executeSqlService.executeSql(queryChartBO.getDsDatabase(), queryChartBO.getOlapDsTable(), sql, getBindValues(queryChartBO.getQueryChartParameterVO().getViewBindValues(), bindValues),
                queryChartBO.getQueryChartParameterVO().getCache(), SqlUtil.QUERY_TYPE_COLUMN);
        List<Object> datas = handleData(dimensionList, measureList, queryChartBO.getTableColumnMap(), executeSqlBO.getResult());

        Map<String, Map<String, String>> allDimensionToMeasureData1 = getAllDimensionToMeasureData(datas);
        return allDimensionToMeasureData1;
    }

    private Map<String, Map<String, String>> getAllDimensionToMeasureData(List<Object> datas) {
        Map<String, Map<String, String>> allDimensionToMeasureData = MapUtil.newHashMap(true);
        if (CollectionUtil.isNotEmpty(datas)) {
            List<List<String>> dimensionDataList = new ArrayList<>();
            ListIterator<Object> iterator = datas.listIterator();
            while (iterator.hasNext()) {
                Object data = iterator.next();
                if (data instanceof QueryChartResultVO.DimensionData) {
                    QueryChartResultVO.DimensionData dimensionData = (QueryChartResultVO.DimensionData) data;
                    dimensionDataList.add(dimensionData.getData());
                } else if (data instanceof QueryChartResultVO.MeasureData) {
                    QueryChartResultVO.MeasureData measureData = (QueryChartResultVO.MeasureData) data;
                    Map<String, String> dimensionToMeasureData = MapUtil.newHashMap(true);
                    buildDimensionToMeasureData(dimensionDataList, measureData.getData(), dimensionToMeasureData);
                    allDimensionToMeasureData.put(measureData.getName(), dimensionToMeasureData);
                }
            }
        }
        return allDimensionToMeasureData;
    }

    /**
     * 维度与度量的对应关系放在map中
     *
     * @param dimensionDataList
     * @param data
     * @param dimensionToMeasureData
     */
    private void buildDimensionToMeasureData(List<List<String>> dimensionDataList, List<String> data, Map<String, String> dimensionToMeasureData) {
        for (int i = 0; i < data.size(); i++) {
            final int index = i;
            String key = dimensionDataList.stream().map(sublist -> sublist.get(index)).collect(Collectors.joining("-"));
            dimensionToMeasureData.put(key, data.get(i));
        }
    }

    /**
     * 处理同比
     *
     * @param firstDimensionData
     * @param dimensionDataList
     * @param measureData
     * @param dimensionToMeasureData
     * @param contrast
     * @param advancedComputingData
     */
    private void handleYOYData(QueryChartResultVO.DimensionData firstDimensionData, List<List<String>> dimensionDataList, QueryChartResultVO.MeasureData measureData,
                               Map<String, String> dimensionToMeasureData, String contrast, List<String> advancedComputingData, Set<String> preCycleTimeSet, String chartType) {
        String dimensionGroup = firstDimensionData.getGroup();
        for (int i = 0; i < firstDimensionData.getData().size(); i++) {
            String currentData = measureData.getData().get(i);
            String dimensionTimeStr = firstDimensionData.getData().get(i);
            String preCycleData = null;
            if (StrUtil.isNotEmpty(dimensionTimeStr)) {
                String preCycleTimeStr = getYOYPreCycleTimeStr(contrast, dimensionGroup, dimensionTimeStr);
                preCycleData = getPreCycleData(dimensionDataList, dimensionToMeasureData, i, preCycleTimeStr, preCycleTimeSet, chartType);
            }
            advancedComputingData.add(getYOYValue(contrast, currentData, preCycleData));
        }
    }

    private String getYOYValue(String contrast, String currentData, String preCycleData) {
        String value = "-";
        if (contrast.contains("rate")) {
            value = getIncreaseRate(currentData, preCycleData);
        } else if (contrast.contains("value")) {
            value = StrUtil.isEmpty(preCycleData) ? "-" : preCycleData;
        } else {
            value = getRate(currentData, preCycleData);
        }
        return value;
    }

    private void handleYOYData(QueryChartResultVO.DimensionData firstDimensionData, List<List<String>> dimensionDataList, QueryChartResultVO.MeasureData measureData,
                               Map<String, String> dimensionToMeasureData, String contrast, List<String> advancedComputingData) {
        String dimensionGroup = firstDimensionData.getGroup();
        for (int i = 0; i < firstDimensionData.getData().size(); i++) {
            String currentData = measureData.getData().get(i);
            String dimensionTimeStr = firstDimensionData.getData().get(i);
            String data = advancedComputingData.get(i);
            if (StrUtil.equals("-", data) && StrUtil.isNotEmpty(dimensionTimeStr)) {
                String preCycleTimeStr = getYOYPreCycleTimeStr(contrast, dimensionGroup, dimensionTimeStr);
                String preCycleData = getPreCycleData(dimensionDataList, dimensionToMeasureData, i, preCycleTimeStr);
                data = getYOYValue(contrast, currentData, preCycleData);
                advancedComputingData.set(i, data);
            }
        }
    }

    /**
     * 获取同比上个周期
     * @param contrast
     * @param dimensionGroup
     * @param dimensionTimeStr
     * @return
     */
    public String getYOYPreCycleTimeStr(String contrast, String dimensionGroup, String dimensionTimeStr) {
        String preCycleTimeStr = "";
        if(ROLLUP_NAME.equals(dimensionTimeStr)) {
            return preCycleTimeStr;
        }
        if (TimeUnitConstant.HOUR.equals(dimensionGroup)) {
            DateTime dimensionTime = DateUtil.parse(dimensionTimeStr, "yyyy-MM-dd HH");
            DateTime preCycleTime = null;
            if (contrast.contains(AdvancedComputingConstant.D_YOY)) {
                preCycleTime = DateUtil.offsetDay(dimensionTime, -1);
            } else if (contrast.contains(AdvancedComputingConstant.W_YOY)) {
                preCycleTime = DateUtil.offsetWeek(dimensionTime, -1);
            }
            preCycleTimeStr = DateUtil.format(preCycleTime, "yyyy-MM-dd HH");
        } else if (TimeUnitConstant.DAY.equals(dimensionGroup)) {
            DateTime dimensionTime = DateUtil.parse(dimensionTimeStr);
            DateTime preCycleTime = null;
            if (contrast.contains(AdvancedComputingConstant.W_YOY)) {
                preCycleTime = DateUtil.offsetWeek(dimensionTime, -1);
            } else if (contrast.contains(AdvancedComputingConstant.M_YOY)) {
                preCycleTime = DateUtil.offsetMonth(dimensionTime, -1);
            } else if (contrast.contains(AdvancedComputingConstant.Y_YOY)) {
                preCycleTime = DateUtil.offset(dimensionTime, DateField.YEAR, -1);
            }
            preCycleTimeStr = DateUtil.formatDate(preCycleTime);

        } else if (TimeUnitConstant.WEEK.equals(dimensionGroup)) {
            String year = getYearUsingWeekGroup(dimensionTimeStr);
            String week = getWeekUsingWeekGroup(dimensionTimeStr);
            if (contrast.contains(AdvancedComputingConstant.Y_YOY)) {
                DateTime lastYear = DateUtil.offset(new DateTime(year, "yyyy"), DateField.YEAR, -1);
                preCycleTimeStr = DateUtil.format(lastYear, "yyyy") + "(" + week + ")";
            }
        } else if (TimeUnitConstant.MONTH.equals(dimensionGroup)) {
            if (contrast.contains(AdvancedComputingConstant.Y_YOY)) {
                DateTime preCycleTime = DateUtil.offset(new DateTime(dimensionTimeStr, "yyyy-MM"), DateField.YEAR, -1);
                preCycleTimeStr = DateUtil.format(preCycleTime, "yyyy-MM");
            }
        }
        return preCycleTimeStr;
    }

    private String getYearUsingWeekGroup(String dimensionTimeStr) {
        return dimensionTimeStr.substring(0, dimensionTimeStr.indexOf("("));
    }

    private String getWeekUsingWeekGroup(String dimensionTimeStr) {
        return dimensionTimeStr.substring(dimensionTimeStr.indexOf("(") + 1, dimensionTimeStr.indexOf(")"));
    }

    /**
     * 处理环比
     *
     * @param firstDimensionData
     * @param dimensionDataList
     * @param measureData
     * @param dimensionToMeasureData
     * @param advancedComputingData
     */
    private void handleMOMData(QueryChartResultVO.DimensionData firstDimensionData, List<List<String>> dimensionDataList, QueryChartResultVO.MeasureData measureData,
                               Map<String, String> dimensionToMeasureData, String contrast, List<String> advancedComputingData, Set<String> preCycleTimeList, String chartType) {
        String dimensionGroup = firstDimensionData.getGroup();
        for (int i = 0; i < firstDimensionData.getData().size(); i++) {
            String currentData = measureData.getData().get(i);
            String dimensionTimeStr = firstDimensionData.getData().get(i);
            String preCycleData = null;
            if (StrUtil.isNotEmpty(dimensionTimeStr)) {
                String preCycleTimeStr = getMOMPreCycleTimeStr(dimensionGroup, dimensionTimeStr);
                preCycleData = getPreCycleData(dimensionDataList, dimensionToMeasureData, i, preCycleTimeStr, preCycleTimeList, chartType);
            }
            String data = getMOMValue(contrast, currentData, preCycleData);
            advancedComputingData.add(data);
        }
    }

    private String getMOMValue(String contrast, String currentData, String preCycleData) {
        String data = "-";
        if (AdvancedComputingConstant.MOM_RATE.equals(contrast)) {
            data = getIncreaseRate(currentData, preCycleData);
        } else if (AdvancedComputingConstant.MOM.equals(contrast)) {
            data = getRate(currentData, preCycleData);
        }
        return data;
    }

    private void handleMOMData(QueryChartResultVO.DimensionData firstDimensionData, List<List<String>> dimensionDataList, QueryChartResultVO.MeasureData measureData,
                               Map<String, String> dimensionToMeasureData, String contrast, List<String> advancedComputingData) {
        String dimensionGroup = firstDimensionData.getGroup();
        for (int i = 0; i < firstDimensionData.getData().size(); i++) {
            String currentData = measureData.getData().get(i);
            String dimensionTimeStr = firstDimensionData.getData().get(i);
            String data = advancedComputingData.get(i);
            if (StrUtil.equals("-", data) && StrUtil.isNotEmpty(dimensionTimeStr)) {
                String preCycleTimeStr = getMOMPreCycleTimeStr(dimensionGroup, dimensionTimeStr);
                String preCycleData = getPreCycleData(dimensionDataList, dimensionToMeasureData, i, preCycleTimeStr);
                data = getMOMValue(contrast, currentData, preCycleData);
                advancedComputingData.set(i, data);
            }
        }
    }

    private String getPreCycleData(List<List<String>> dimensionDataList, Map<String, String> dimensionToMeasureData, int i, String preCycleTimeStr) {
        String joinKey = getKey(dimensionDataList, i, preCycleTimeStr);
        return dimensionToMeasureData.get(joinKey);
    }

    private String getPreCycleData(List<List<String>> dimensionDataList, Map<String, String> dimensionToMeasureData, int i, String preCycleTimeStr,
                                   Set<String> preCycleTimeSet, String chartType) {
        String joinKey = getKey(dimensionDataList, i, preCycleTimeStr);
        if(!dimensionToMeasureData.containsKey(joinKey)) {
            if (ChartTypeConstant.TEXT.equals(chartType)) {
                // 文本图表只展示一行数据
                if(i==0) {
                    preCycleTimeSet.add(preCycleTimeStr);
                }
            } else {
                preCycleTimeSet.add(preCycleTimeStr);
            }
        }
        return dimensionToMeasureData.get(joinKey);
    }

    private String getKey(List<List<String>> dimensionDataList, int i, String preCycleTimeStr) {
        List<String> key = CollectionUtil.newArrayList(preCycleTimeStr);

        // 多个维度时，需要拼上其他维度
        for (int j = 1; j < dimensionDataList.size(); j++) {
            key.add(dimensionDataList.get(j).get(i));
        }
        return CollectionUtil.join(key, "-");
    }

    /**
     * 获取环比上个周期
     *
     * @param dimensionGroup
     * @param dimensionTimeStr
     * @return
     */
    public String getMOMPreCycleTimeStr(String dimensionGroup, String dimensionTimeStr) {
        String preCycleTimeStr = "";
        if(ROLLUP_NAME.equals(dimensionTimeStr)) {
            return preCycleTimeStr;
        }
        if (TimeUnitConstant.HOUR.equals(dimensionGroup)) {
            DateTime dimensionTime = DateUtil.parse(dimensionTimeStr, "yyyy-MM-dd HH");
            DateTime preCycleTime = DateUtil.offsetHour(dimensionTime, -1);
            preCycleTimeStr = DateUtil.format(preCycleTime, "yyyy-MM-dd HH");
        } else if (TimeUnitConstant.DAY.equals(dimensionGroup)) {
            DateTime dimensionTime = DateUtil.parse(dimensionTimeStr);
            DateTime preCycleTime = DateUtil.offsetDay(dimensionTime, -1);
            preCycleTimeStr = DateUtil.formatDate(preCycleTime);

        } else if (TimeUnitConstant.WEEK.equals(dimensionGroup)) {
            String year = getYearUsingWeekGroup(dimensionTimeStr);
            String week = getWeekUsingWeekGroup(dimensionTimeStr);
            int lastWeek = Integer.parseInt(week) - 1;
            if (lastWeek < 1) {
                DateTime lastYear = DateUtil.offsetDay(new DateTime(year, "yyyy"), -1);
                lastWeek = DateUtil.weekOfYear(lastYear);
                String lastWeekStr = lastWeek < 10 ? "0" + lastWeek : String.valueOf(lastWeek);
                preCycleTimeStr = DateUtil.format(lastYear, "yyyy") + "(" + lastWeekStr + ")";
            } else {
                String lastWeekStr = lastWeek < 10 ? "0" + lastWeek : String.valueOf(lastWeek);
                preCycleTimeStr = year + "(" + lastWeekStr + ")";
            }
        } else if (TimeUnitConstant.MONTH.equals(dimensionGroup)) {
            DateTime preCycleTime = DateUtil.offsetMonth(new DateTime(dimensionTimeStr, "yyyy-MM"), -1);
            preCycleTimeStr = DateUtil.format(preCycleTime, "yyyy-MM");

        } else if (TimeUnitConstant.YEAR.equals(dimensionGroup)) {
            DateTime preCycleTime = DateUtil.offset(new DateTime(dimensionTimeStr, "yyyy"), DateField.YEAR, -1);
            preCycleTimeStr = DateUtil.format(preCycleTime, "yyyy");

        }
        return preCycleTimeStr;
    }

    private String getIncreaseRate(String currentData, String preCycleData) {
        if (NumberUtil.isNumber(currentData) && NumberUtil.isNumber(preCycleData)) {
            if (StrUtil.isNotEmpty(preCycleData)) {
                double preCycleDataDouble = Double.parseDouble(preCycleData);
                if (preCycleDataDouble == 0) {
                    return "-";
                }
                Double percent = (Double.parseDouble(currentData) - preCycleDataDouble) / preCycleDataDouble;
                return String.valueOf(NumberUtil.round(percent, 4));
            }
        }
        return "-";
    }

    private String getRate(String currentData, String preCycleData) {
        if (NumberUtil.isNumber(currentData) && NumberUtil.isNumber(preCycleData)) {
            if (StrUtil.isNotEmpty(preCycleData)) {
                double preCycleDoubleData = Double.parseDouble(preCycleData);
                if (preCycleDoubleData == 0) {
                    return "-";
                }
                Double percent = (Double.parseDouble(currentData) / preCycleDoubleData);
                return String.valueOf(NumberUtil.round(percent, 4));
            }
        }
        return "-";
    }

    /**
     * 两个维度，一个度量,处理图例。
     *
     * @param dimensionList
     * @param measureList
     * @param datas
     */
    private void handleGroupData(List<OlapChartDimension> dimensionList, List<OlapChartMeasure> measureList, List<Object> datas, String chartType) {
        if (CollectionUtil.isEmpty(datas)) {
            return;
        }
        if (StrUtil.equals(chartType, ChartTypeConstant.TABLE)) {
            return;
        }
        int dimensionNum = 2;
        int measureNum = 1;
        if (dimensionList.size() == dimensionNum && measureList.size() == measureNum) {
            QueryChartResultVO.DimensionData firstDimension = (QueryChartResultVO.DimensionData) datas.get(0);
            QueryChartResultVO.DimensionData secondDimension = (QueryChartResultVO.DimensionData) datas.get(1);
            QueryChartResultVO.MeasureData oneMeasure = (QueryChartResultVO.MeasureData) datas.get(2);
            ArrayList<String> firstDimensionDistinctData = CollectionUtil.distinct(firstDimension.getData());
            ArrayList<String> secondDimensionDistinctData = CollectionUtil.distinct(secondDimension.getData());
            if((firstDimensionDistinctData.size() * secondDimensionDistinctData.size()) > 100000) {
                throw new BusinessException("维度的笛卡尔积不能超过100000");
            }
            Map<String, String> dimensionToMeasureMap = MapUtil.newHashMap();
            String template = "{}-{}";
            for (int i = 0; i < firstDimension.getData().size(); i++) {
                String key = StrUtil.format(template, firstDimension.getData().get(i), secondDimension.getData().get(i));
                String value = oneMeasure.getData().get(i);
                dimensionToMeasureMap.put(key, value);
            }
            List<Object> groupDataList = new LinkedList<>();
            if(CollectionUtil.isNotEmpty(dimensionList.get(1).getGroupDataOrderContentList())) {
                secondDimensionDistinctData.sort(Comparator.comparingInt(dimensionList.get(1).getGroupDataOrderContentList()::indexOf));
            }
            for (String secondDimensionDistinctDatum : secondDimensionDistinctData) {
                List<Object> subGroupDataList = new LinkedList<>();
                subGroupDataList.add(
                        getIntegerOrDecimalOrStringValue(
                                Optional.ofNullable(secondDimensionDistinctDatum).filter(StrUtil::isNotEmpty).orElse("None")));
                List<Object> measures = new LinkedList<>();
                for (String firstDimensionDistinctDatum : firstDimensionDistinctData) {
                    String measure = dimensionToMeasureMap.get(StrUtil.format(template, firstDimensionDistinctDatum, secondDimensionDistinctDatum));
                    Object measureObj = getIntegerOrDecimalOrStringValue(measure);
                    measures.add(measureObj);
                }
                subGroupDataList.add(measures);
                groupDataList.add(subGroupDataList);
            }
            firstDimension.setDistinctData(firstDimensionDistinctData);
            oneMeasure.setGroupData(groupDataList);
        }
    }

    private Object getIntegerOrDecimalOrStringValue(String value) {
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        try {
            BigInteger bigInteger = new BigInteger(value);
            return bigInteger;
        } catch (Exception newBigIntegerException) {
            try {
                BigDecimal bigDecimal = new BigDecimal(value);
                return bigDecimal;
            } catch (Exception newBigDecimalException) {
                return value;
            }
        }
    }

    /**
     * 图内筛选
     *
     * @param condition
     * @param olapDsTable
     * @param dsDatabase
     * @param tableColumnMap
     * @return
     */
    public Condition buildCondition(Condition condition, List<QueryChartParameterVO.ConditionVO> conditionVOList, OlapDsTable olapDsTable, OlapDsDatabase dsDatabase,
                                    Map<String, QueryTableColumnResultBO> tableColumnMap, String excludeField) {
        if(CollectionUtil.isEmpty(conditionVOList)) {
            return condition;
        }
        List<Condition> conditionList = getConditions(conditionVOList, dsDatabase, tableColumnMap, excludeField);
        if (CollectionUtil.isNotEmpty(conditionList)) {
            condition = condition.and(DSL.and(conditionList));
        }
        return condition;
    }

    private List<Condition> getConditions(List<QueryChartParameterVO.ConditionVO> conditionVOList, OlapDsDatabase dsDatabase, Map<String, QueryTableColumnResultBO> tableColumnMap, String excludeField) {
        String namespace = dsDatabase.getNamespace();
        List<Condition> conditionList = new LinkedList<>();
        for (QueryChartParameterVO.ConditionVO conditionVO : conditionVOList) {
            String value = conditionVO.getValue();
            String logic = conditionVO.getLogic();
            String name = conditionVO.getName();
            String func = conditionVO.getFunc();
            if (StrUtil.isEmpty(name)) {
                continue;
            }
            if (StrUtil.equals(name, excludeField)) {
                continue;
            }
            if (StrUtil.isEmpty(logic)) {
                continue;
            }
            if (StrUtil.isEmpty(value) && !StrUtil.equalsAny(logic, FilterLogicConstant.ISNULL, FilterLogicConstant.ISBLANK, FilterLogicConstant.ISEMPTY, FilterLogicConstant.ISNOTNULL, FilterLogicConstant.ISNOTBLANK, FilterLogicConstant.ISNOTEMPTY)) {
                continue;
            }
            String olapType = Optional.ofNullable(tableColumnMap.get(name)).map(QueryTableColumnResultBO::getOlapType).orElse("");
            conditionVO.setOlapType(olapType);
            name = handleFieldName(dsDatabase.getNamespace(), name, tableColumnMap);
            if (ColumnTypeConstant.DATE.equals(olapType)) {
                name = buildTimeFieldName(name, func, namespace);
                value = handleDateValue(value);
            }
            buildConditionList(name, conditionList, value, logic, olapType);
        }
        return conditionList;
    }

    public Condition buildFilter(Condition condition, List<QueryChartParameterVO.FilterVO> filterVOList, OlapDsTable dsTable, OlapDsDatabase dsDatabase,
                                 Map<String, QueryTableColumnResultBO> tableColumnMap, String excludeField) {
        if (CollectionUtil.isEmpty(filterVOList)) {
            return condition;
        }
        for (QueryChartParameterVO.FilterVO chartFilterBO : filterVOList) {
            List<Condition> conditionList = getConditions(chartFilterBO.getMember(), dsDatabase, tableColumnMap, excludeField);
            String relation = chartFilterBO.getRelation();
            Condition subCondition;
            if ("or".equals(relation)) {
                subCondition = or(conditionList);
            } else {
                subCondition = and(conditionList);
            }
            condition = condition.and(subCondition);
        }
        return condition;
    }


    /**
     * 当前周期不完整不能和上一个完整周期对比。
     * @param condition
     * @param queryChartBO
     * @return
     */
    private Condition buildShowCurrentGroupCondition(Condition condition, QueryChartBO queryChartBO) {
        if(!BooleanUtil.isTrue(queryChartBO.getShowCurrentGroup())) {
            return condition;
        }
        String namespace = queryChartBO.getDsDatabase().getNamespace();
        OlapChartDimension firstChartDimension = CollectionUtil.get(queryChartBO.getDimensionList(), 0);

        if(ObjectUtil.isNull(firstChartDimension) || !TimeUnitConstant.NAME_MAP.containsKey(firstChartDimension.getGroup())) {
            return condition;
        }
        String group = firstChartDimension.getGroup();
        String name = firstChartDimension.getName();

        String filedName = Optional.ofNullable(queryChartBO.getTableColumnMap().get(name)).map(QueryTableColumnResultBO::getColumnExp).filter(StrUtil::isNotEmpty)
                .orElseGet(() -> handleFieldName(namespace, name, queryChartBO.getTableColumnMap()));
        if (TimeUnitConstant.YEAR.equals(group)) {
            if(NameSpaceConstant.CLICKHOUSE.equals(namespace)) {
                condition = condition.and(DSL.field(StrUtil.format("toDayOfYear({})", filedName)).le(DSL.inline(LocalDate.now().getDayOfYear())));
            } else {
                condition = condition.and(DSL.field(StrUtil.format("DAYOFYEAR({})", filedName)).le(DSL.inline(LocalDate.now().getDayOfYear())));
            }
        }
        if (TimeUnitConstant.MONTH.equals(group)) {
            if(NameSpaceConstant.CLICKHOUSE.equals(namespace)) {
                condition = condition.and(DSL.field(StrUtil.format("toDayOfMonth({})", filedName)).le(DSL.inline(LocalDate.now().getDayOfMonth())));
            } else {
                condition = condition.and(DSL.field(StrUtil.format("DAYOFMONTH({})", filedName)).le(DSL.inline(LocalDate.now().getDayOfMonth())));
            }
        }
        if (TimeUnitConstant.WEEK.equals(group)) {
            // 一周的开始从周一算起
            int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
            if (NameSpaceConstant.DSSP.equals(namespace) || NameSpaceConstant.VERTICA.equals(namespace)) {
                condition = condition.and(DSL.field(StrUtil.format("DAYOFWEEK_ISO({})", filedName)).le(DSL.inline(dayOfWeek)));
            } else if (NameSpaceConstant.MYSQL.equals(namespace)) {
                condition = condition.and(DSL.field(StrUtil.format("(WEEKDAY({}) + 1)", filedName)).le(DSL.inline(dayOfWeek)));
            } else if (NameSpaceConstant.CLICKHOUSE.equals(namespace)) {
                condition = condition.and(DSL.field(StrUtil.format("(toDayOfWeek({}))", filedName)).le(DSL.inline(dayOfWeek)));
            }
        }
        if (TimeUnitConstant.DAY.equals(group)) {
            if(NameSpaceConstant.CLICKHOUSE.equals(namespace)) {
                condition = condition.and(DSL.field(StrUtil.format("toHour({})", filedName)).le(DSL.inline(DateUtil.thisHour(true))));
            } else {
                condition = condition.and(DSL.field(StrUtil.format("hour({})", filedName)).le(DSL.inline(DateUtil.thisHour(true))));
            }
        }
        return condition;
    }

    /**
     * 手动指定的查询条件
     *
     * @param condition
     * @param conditionSql
     * @return
     */
    private Condition buildConditionSql(Condition condition, String conditionSql) {
        if (StrUtil.isNotEmpty(conditionSql)) {
            condition = condition.and(conditionSql);
        }
        return condition;
    }

    public void buildConditionList(String name, List<Condition> conditionList, String value, String logic, String olapType) {
        if(StrUtil.equals(logic, FilterLogicConstant.ISNULL)) {
            conditionList.add(field(name).isNull());
        }
        if(StrUtil.equals(logic, FilterLogicConstant.ISBLANK)) {
            conditionList.add(field(name).eq(""));
        }
        if(StrUtil.equals(logic, FilterLogicConstant.ISEMPTY)) {
            conditionList.add(DSL.or(field(name).isNull(), field(name).eq("")));
        }
        if(StrUtil.equals(logic, FilterLogicConstant.ISNOTNULL)) {
            conditionList.add(field(name).isNotNull());
        }
        if(StrUtil.equals(logic, FilterLogicConstant.ISNOTBLANK)) {
            conditionList.add(field(name).ne(""));
        }
        if(StrUtil.equals(logic, FilterLogicConstant.ISNOTEMPTY)) {
            conditionList.add(DSL.and(field(name).isNotNull(), field(name).ne("")));
        }

        if (FilterLogicConstant.IN.equals(logic)) {
            List<Object> jsonArray;
            if(StrUtil.equals(olapType, ColumnTypeConstant.INT)) {
                jsonArray = JacksonUtil.fromJSONArray(value, Integer.class).stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
            } else if(StrUtil.equals(olapType, ColumnTypeConstant.TEXT)) {
                jsonArray = JacksonUtil.fromJSONArray(value, String.class).stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
            } else {
                jsonArray = JacksonUtil.fromJSONArray(value, Object.class).stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(jsonArray)) {
                conditionList.add(field(name).in(jsonArray));
            }
        }
        if (FilterLogicConstant.NOTIN.equals(logic)) {
            JSONArray jsonArray = JSONUtil.parseArray(value);
            conditionList.add(DSL.or(field(name).notIn(jsonArray), field(name).isNull()));
        }
        if (FilterLogicConstant.LIKE.equals(logic)) {
            conditionList.add(field(name).like(StrUtil.format("%{}%", likeEscape(value))));
        }
        if (FilterLogicConstant.NOTLIKE.equals(logic)) {
            conditionList.add(field(name).notLike(StrUtil.format("%{}%", likeEscape(value))));
        }
        if (FilterLogicConstant.STARTSWITH.equals(logic)) {
            conditionList.add(field(name).like(StrUtil.format("{}%", likeEscape(value))));
        }
        if (FilterLogicConstant.ENDSWITH.equals(logic)) {
            conditionList.add(field(name).like(StrUtil.format("%{}", likeEscape(value))));
        }
        if (FilterLogicConstant.BETWEEN.equals(logic)) {
            List<String> stringList = JacksonUtil.fromJSONArray(value, String.class);
            for (int i = 0; i < stringList.size(); i++) {
                String betweenValue = stringList.get(i);
                if (StrUtil.isEmpty(betweenValue)) {
                    continue;
                }
                if (i == 0) {
                    conditionList.add(field(name).ge(betweenValue));
                }
                if (i == 1) {
                    conditionList.add(field(name).le(betweenValue));
                }
            }
        }
        Object valueObj = value;
        if(ColumnTypeConstant.INT.equals(olapType) && NumberUtil.isNumber(value)) {
            valueObj = NumberUtil.parseInt(value);
        }
        if (FilterLogicConstant.EQ.equals(logic)) {
            if (ObjectUtil.isNotNull(valueObj)) {
                conditionList.add(field(name).eq(valueObj));
            }
        }
        if (FilterLogicConstant.NEQ.equals(logic)) {
            conditionList.add(DSL.or(field(name).ne(valueObj), field(name).isNull()));
        }
        if (FilterLogicConstant.GT.equals(logic)) {
            conditionList.add(field(name).gt(valueObj));
        }
        if (FilterLogicConstant.GTE.equals(logic)) {
            conditionList.add(field(name).ge(valueObj));
        }
        if (FilterLogicConstant.LT.equals(logic)) {
            conditionList.add(field(name).lt(valueObj));
        }
        if (FilterLogicConstant.LTE.equals(logic)) {
            conditionList.add(field(name).le(valueObj));
        }
    }

    private String likeEscape(String value) {
        value = StrUtil.replaceChars(value, "%", "\\%");
        value = StrUtil.replaceChars(value, "_", "\\_");
        return value;
    }

    /**
     * 图内筛选器和全局筛选处理日期动态条件
     * @param value
     * @return
     */
    public String handleDateValue(String value) {
        String today = DateUtil.today();
        String yesterday = DateUtil.formatDate(DateUtil.yesterday());
        DateTime dateTime = new DateTime();
        if (DateValueConstant.TODAY.equals(value)) {
            // 今天
            value = today;
        } else if (DateValueConstant.YESTERDAY.equals(value)) {
            // 昨天
            value = yesterday;
        } else if (DateValueConstant.THIS_WEEK.equals(value)) {
            // 本周
            String min = DateUtil.formatDate(DateUtil.beginOfWeek(dateTime));
            String max = DateUtil.formatDate(DateUtil.endOfWeek(dateTime));
            value = JSONUtil.toJsonStr(Arrays.asList(min, max));
        } else if (DateValueConstant.LAST_WEEK.equals(value)) {
            // 上周
            DateTime lastWeek = DateUtil.lastWeek();
            String min = DateUtil.formatDate(DateUtil.beginOfWeek(lastWeek));
            String max = DateUtil.formatDate(DateUtil.endOfWeek(lastWeek));
            value = JSONUtil.toJsonStr(Arrays.asList(min, max));
        } else if (DateValueConstant.THIS_MONTH.equals(value)) {
            // 本月
            String min = DateUtil.formatDate(DateUtil.beginOfMonth(dateTime));
            String max = DateUtil.formatDate(DateUtil.endOfMonth(dateTime));
            value = JSONUtil.toJsonStr(Arrays.asList(min, max));
        } else if (DateValueConstant.LAST_MONTH.equals(value)) {
            // 上月
            DateTime lastMonth = DateUtil.lastMonth();
            String min = DateUtil.formatDate(DateUtil.beginOfMonth(lastMonth));
            String max = DateUtil.formatDate(DateUtil.endOfMonth(lastMonth));
            value = JSONUtil.toJsonStr(Arrays.asList(min, max));
        } else if (DateValueConstant.IN_SEVEN_DAYS.equals(value)) {
            // 近7天
            String min = DateUtil.formatDate(DateUtil.offsetDay(dateTime, -6));
            String max = today;
            value = JSONUtil.toJsonStr(Arrays.asList(min, max));
        } else if (DateValueConstant.LAST_SEVEN_DAYS.equals(value)) {
            // 过去7天
            String min = DateUtil.formatDate(DateUtil.offsetDay(dateTime, -7));
            String max = yesterday;
            value = JSONUtil.toJsonStr(Arrays.asList(min, max));
        } else if (DateValueConstant.IN_THIRTY_DAYS.equals(value)) {
            // 近30天
            String min = DateUtil.formatDate(DateUtil.offsetDay(dateTime, -29));
            String max = today;
            value = JSONUtil.toJsonStr(Arrays.asList(min, max));
        } else if (DateValueConstant.LAST_THIRTY_DAYS.equals(value)) {
            // 过去30天
            String min = DateUtil.formatDate(DateUtil.offsetDay(dateTime, -30));
            String max = yesterday;
            value = JSONUtil.toJsonStr(Arrays.asList(min, max));
        } else if (DateValueConstant.IN_NINETY_DAYS.equals(value)) {
            // 近90天
            String min = DateUtil.formatDate(DateUtil.offsetDay(dateTime, -89));
            String max = today;
            value = JSONUtil.toJsonStr(Arrays.asList(min, max));
        } else if (DateValueConstant.LAST_NINETY_DAYS.equals(value)) {
            // 过去90天
            String min = DateUtil.formatDate(DateUtil.offsetDay(dateTime, -90));
            String max = yesterday;
            value = JSONUtil.toJsonStr(Arrays.asList(min, max));
        } else if (DateValueConstant.IN_FIFTEEN_DAYS.equals(value)) {
            // 近15天
            String min = DateUtil.formatDate(DateUtil.offsetDay(dateTime, -14));
            String max = today;
            value = JSONUtil.toJsonStr(Arrays.asList(min, max));
        } else if (DateValueConstant.LAST_FIFTEEN_DAYS.equals(value)) {
            // 过去15天
            String min = DateUtil.formatDate(DateUtil.offsetDay(dateTime, -15));
            String max = yesterday;
            value = JSONUtil.toJsonStr(Arrays.asList(min, max));
        }
        return value;
    }

    /**
     * 构建和执行sql
     * @param queryChartBO
     * @return
     */
    private ExecuteSqlBO buildAndExecuteSql(QueryChartBO queryChartBO) {
        BuildSqlResultBO buildSqlResultBO = buildSql(queryChartBO);
        ExecuteSqlBO executeSqlBO = executeSqlService.executeSql(queryChartBO.getDsDatabase(), queryChartBO.getOlapDsTable(), buildSqlResultBO.getSql(), buildSqlResultBO.getBindValues(), queryChartBO.getQueryChartParameterVO().getCache(), SqlUtil.QUERY_TYPE_COLUMN);
        return executeSqlBO;
    }

    /**
     * 使用jooq构建sql
     * @param queryChartBO
     * @return
     */
    public BuildSqlResultBO buildSql(QueryChartBO queryChartBO) {
        QueryChartParameterVO queryChartParameterVO = queryChartBO.getQueryChartParameterVO();
        OlapDsTable olapDsTable = queryChartBO.getOlapDsTable();
        OlapDsDatabase dsDatabase = queryChartBO.getDsDatabase();

        Condition condition = getWhereSql(queryChartBO);

        BuildSqlBO buildSqlBO = new BuildSqlBO();
        queryChartBO.setBuildSqlBO(buildSqlBO);
        String fromSql = getFromSql(dsDatabase, olapDsTable);
        buildSqlBO.setFromSql(fromSql);
        buildSqlBO.setWithRollup(queryChartParameterVO.getWithRollup());
        buildSqlBO.setWithRollupName(queryChartParameterVO.getWithRollupName());

        buildSqlUsingDimensionAndMeasure(queryChartBO, dsDatabase.getNamespace(), buildSqlBO);
        SelectConditionStep<Record> step1 = buildSelectFromWhereGroup(condition, buildSqlBO);
        LimitAndOffsetBO limitAndOffsetBO = handleLimitAndOffset(queryChartBO, step1.getSQL(), step1.getBindValues());
        SelectForUpdateStep<Record> step2 = step1.orderBy(buildSqlBO.getSortFieldList())
                .limit(inline(limitAndOffsetBO.getLimit())).offset(inline(limitAndOffsetBO.getOffset()));
        String sql = step2.getSQL();
        List<Object> bindValues = getBindValues(queryChartParameterVO.getViewBindValues(), step2.getBindValues());

        if(StrUtil.isNotEmpty(queryChartBO.getWithSql())) {
            sql = StrUtil.format("with {} {}", queryChartBO.getWithSql(), sql);
        }

        if(!StrUtil.equalsAny(ProfileConstant.ACTIVE_PROFILE, ProfileConstant.PROD)) {
            queryChartBO.getQueryChartResultVO().setSql(sql);
            queryChartBO.getQueryChartResultVO().setBindValues(bindValues);
        }
        BuildSqlResultBO buildSqlResultBO = new BuildSqlResultBO();
        buildSqlResultBO.setSql(sql);
        buildSqlResultBO.setBindValues(bindValues);
        return buildSqlResultBO;
    }

    private List<Object> getBindValues(List<Object> viewBindValues, List<Object> queryBindValues) {
        List<Object> bindValues = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(viewBindValues)) {
            bindValues.addAll(viewBindValues);
        }
        if(CollectionUtil.isNotEmpty(queryBindValues)) {
            bindValues.addAll(queryBindValues);
        }
        return bindValues;
    }

    /**
     * 构造查询条件sql
     * @param queryChartBO
     * @return
     */
    public Condition getWhereSql(QueryChartBO queryChartBO) {
        QueryChartParameterVO queryChartParameterVO = queryChartBO.getQueryChartParameterVO();
        OlapDsTable olapDsTable = queryChartBO.getOlapDsTable();
        OlapDsDatabase dsDatabase = queryChartBO.getDsDatabase();

        Condition condition = DSL.trueCondition();
        condition = buildFilter(condition, queryChartParameterVO.getFilter(), olapDsTable, dsDatabase, queryChartBO.getTableColumnMap(), null);
        condition = buildCondition(condition, queryChartParameterVO.getScreen(), olapDsTable, dsDatabase, queryChartBO.getTableColumnMap(), null);
        condition = buildCondition(condition, queryChartParameterVO.getDashboard(), olapDsTable, dsDatabase, queryChartBO.getTableColumnMap(), null);
        condition = buildShowCurrentGroupCondition(condition, queryChartBO);
        condition = buildConditionSql(condition, queryChartBO.getConditionSql());
        return condition;
    }

    public SelectConditionStep<Record> buildSelectFromWhereGroup(Condition condition, BuildSqlBO buildSqlBO) {
        List<SelectField<?>> selectFieldList = buildSqlBO.getSelectFieldList();
        List<Field> groupFieldList = buildSqlBO.getGroupFieldList();
        List<Condition> havingConditionList = buildSqlBO.getHavingConditionList();
        String fromSql = buildSqlBO.getFromSql();
        DSLContext create = DSL.using(SQLDialect.DEFAULT);
        SelectConditionStep<Record> step = create.select(selectFieldList)
                .from(table(fromSql))
                .where(condition);
        if (CollectionUtil.isNotEmpty(groupFieldList)) {
            if(BooleanUtil.isTrue(buildSqlBO.getWithRollup())) {
                step.groupBy(DSL.rollup(groupFieldList.toArray(new Field[0])));
            } else {
                step.groupBy(groupFieldList);
            }
            if (CollectionUtil.isNotEmpty(havingConditionList)) {
                step.having(and(havingConditionList));
            }
        }
        return step;
    }

    private LimitAndOffsetBO handleLimitAndOffset(QueryChartBO queryChartBO, String sql, List<Object> bindValues) {
        Integer limit = 500;
        Integer offset = 0;
        if(queryChartBO.getDimensionList().size() == 2 && !ChartTypeConstant.TABLE.equals(queryChartBO.getOlapChart().getType())) {
            limit = 3000;
        }

        // 如果前端指定limit，优先使用
        OlapDsDatabase dsDatabase = queryChartBO.getDsDatabase();
        Double doubleLimit = queryChartBO.getQueryChartParameterVO().getLimit() != null ?
                queryChartBO.getQueryChartParameterVO().getLimit().doubleValue() :
                Optional.of(queryChartBO.getOlapChart()).map(OlapChart::getLimit).filter(temp -> temp != 0).orElse(limit.doubleValue());
        if (doubleLimit < 1) {
            String countSql = StrUtil.format("SELECT COUNT(*) FROM ({}) as _t1 ", sql);
            String countStr = SqlUtil.queryString(countSql, bindValues, DatabaseUtil.getDB(dsDatabase.getNamespace(), dsDatabase.getDataSource()));
            int count = Integer.parseInt(countStr);
            queryChartBO.getQueryChartResultVO().setTotal(count);
            if (doubleLimit <= -1) {
                limit = Math.abs(doubleLimit.intValue());
                offset = count - limit;
                if (offset < 0) {
                    offset = 0;
                }
            } else if (doubleLimit < 0) {
                Double temp = count * Math.abs(doubleLimit);
                limit = temp.intValue();
                offset = count - limit;
                if (offset < 0) {
                    offset = 0;
                }
            } else if (doubleLimit < 1) {
                Double temp = count * doubleLimit;
                limit = temp.intValue();
            }
        } else {
            limit = doubleLimit.intValue();
        }
        return new LimitAndOffsetBO(limit, offset);
    }

    /**
     * 通过维度与度量构建
     * @param queryChartBO
     * @param namespace
     * @param buildSqlBO
     */
    public void buildSqlUsingDimensionAndMeasure(QueryChartBO queryChartBO, String namespace, BuildSqlBO buildSqlBO) {
        List<SelectField<?>> selectFieldList = buildSqlBO.getSelectFieldList();
        List<Field> groupFieldList = buildSqlBO.getGroupFieldList();
        List<SortField<?>> sortFieldList = buildSqlBO.getSortFieldList();
        List<Condition> havingConditionList = buildSqlBO.getHavingConditionList();
        List<OlapChartDimension> dimensionList = queryChartBO.getDimensionList();
        Map<String, QueryTableColumnResultBO> tableColumnMap = queryChartBO.getTableColumnMap();
        List<OlapChartMeasure> measureList = queryChartBO.getMeasureList();
        List<SortField<?>> defaultSortFieldList = new LinkedList<>();
        int dimensionSize = dimensionList.size();
        for (int i = 0; i < dimensionSize; i++) {
            OlapChartDimension dimension = dimensionList.get(i);
            String fieldName = buildDimensionFieldName(namespace, tableColumnMap, dimension);
            String asName = StrUtil.isEmpty(dimension.getAsName()) ? StrUtil.format("dimension{}", i) : dimension.getAsName();
            String selectName = StrUtil.format("{} as {}", fieldName, asName);
            if(BooleanUtil.isTrue(buildSqlBO.getWithRollup())) {
                String withRollupName = Optional.ofNullable(buildSqlBO).map(BuildSqlBO::getWithRollupName).orElse(ROLLUP_NAME);
                selectName = StrUtil.format("{} as {}", StrUtil.format("if({} = '', '{}', {})", fieldName, withRollupName, fieldName), asName);
            }
            selectFieldList.add(field(selectName));
            groupFieldList.add(field(fieldName));
            buildDimensionOrder(sortFieldList, fieldName, asName, dimension.getOrder(), dimension.getOrderContentList(), queryChartBO.getDsDatabase().getNamespace(), defaultSortFieldList);

            if(StrUtil.isNotEmpty(dimension.getHavingLogic())) {
                if (FilterLogicConstant.EQ.equals(dimension.getHavingLogic())) {
                    havingConditionList.add(field(fieldName).eq(dimension.getHavingValue()));
                }
                if (FilterLogicConstant.NEQ.equals(dimension.getHavingLogic())) {
                    havingConditionList.add(field(fieldName).ne(dimension.getHavingValue()));
                }
            }
        }

        for (int i = 0; i < measureList.size(); i++) {
            OlapChartMeasure measure = measureList.get(i);
            String fieldName = buildMeasureFieldName(measure.getName(), measure.getFunc(), tableColumnMap, namespace);
            String asName = StrUtil.isEmpty(measure.getAsName()) ? StrUtil.format("measure{}", i) : measure.getAsName();
            Integer order = measure.getOrder();

            String selectName = StrUtil.format("{} as {}", fieldName, asName);
            selectFieldList.add(field(selectName));
            if (order != null) {
                if (order == -1) {
                    sortFieldList.add(field(asName).desc());
                } else if (order == 0) {
                    sortFieldList.add(field(asName).asc());
                }
            } else {
                defaultSortFieldList.add(field(asName).asc());
            }

            if (ObjectUtil.isNotNull(measure.getMaxvalue())) {
                havingConditionList.add(field(fieldName).le(measure.getMaxvalue()));
            }
            if (ObjectUtil.isNotNull(measure.getMinvalue())) {
                havingConditionList.add(field(fieldName).ge(measure.getMinvalue()));
            }
            if (ObjectUtil.isNotNull(measure.getMaxValueNotEqual())) {
                havingConditionList.add(field(fieldName).lt(measure.getMaxValueNotEqual()));
            }
            if (ObjectUtil.isNotNull(measure.getMinValueNotEqual())) {
                havingConditionList.add(field(fieldName).gt(measure.getMinValueNotEqual()));
            }
        }

        if(StrUtil.isNotEmpty(queryChartBO.getHavingSql())) {
            havingConditionList.add(DSL.condition(queryChartBO.getHavingSql()));
        }

        //未设置维度和度量默认排序
        sortFieldList.addAll(defaultSortFieldList);
    }

    private String buildDimensionFieldName(String namespace, Map<String, QueryTableColumnResultBO> tableColumnMap, OlapChartDimension dimension) {
        String dimensionName = dimension.getName();
        String fieldName = buildFieldName(namespace, tableColumnMap, dimension.getGroup(), dimensionName);
        if (StrUtil.isNotEmpty(dimension.getFunc())) {
            fieldName = StrUtil.format("{}({})", dimension.getFunc(), fieldName);
        }
        return fieldName;
    }

    private String buildMeasureFieldName(String measureName, String measureFunc, Map<String, QueryTableColumnResultBO> tableColumnMap, String namespace) {
        String fieldName = Optional.ofNullable(tableColumnMap.get(measureName)).map(QueryTableColumnResultBO::getColumnExp).filter(StrUtil::isNotEmpty)
                .orElseGet(() -> handleFieldName(namespace, measureName, tableColumnMap));

        if (StrUtil.isNotEmpty(measureFunc)) {
            if(StrUtil.equals(measureFunc, FunctionConstant.UNIQ_ARRAY)) {
                fieldName = StrUtil.format("uniqExactArrayIf({}, notEmpty({}))", fieldName, fieldName);
            } else if (measureFunc.contains("_")) {
                String[] split = measureFunc.split("_");
                fieldName = StrUtil.format("{}({} {})", split[0], split[1], fieldName);
            } else if (measureFunc.contains("{}")) {
                String[] params = Collections.nCopies(StrUtil.count(measureFunc, "{}"), fieldName).toArray(new String[0]);
                fieldName = StrUtil.format(measureFunc, params);
            } else {
                fieldName = StrUtil.format("{}({})", measureFunc, fieldName);
            }
        }
        return fieldName;
    }

    public String buildFieldName(String namespace, Map<String, QueryTableColumnResultBO> tableColumnMap, String group, String name) {
        String fieldName = Optional.ofNullable(tableColumnMap.get(name)).map(QueryTableColumnResultBO::getColumnExp).filter(StrUtil::isNotEmpty)
                .orElseGet(() -> handleFieldName(namespace, name, tableColumnMap));
        fieldName = buildTimeFieldName(fieldName, group, namespace);
        return fieldName;
    }

    private String handleFieldName(String namespace, String name, Map<String, QueryTableColumnResultBO> tableColumnMap) {
        if (tableColumnMap.containsKey(name)) {
            if (NameSpaceConstant.MYSQL.equals(namespace)) {
                return StrUtil.format("`{}`", name);
            }
            if (NameSpaceConstant.CLICKHOUSE.equals(namespace)) {
                return StrUtil.format("`{}`", name);
            }
        }
        return name;
    }

    private void buildDimensionOrder(List<SortField<?>> sortFieldList, String fieldName, String asName, Integer order, List<String> orderContentList, String namespace, List<SortField<?>> defaultSortFieldList) {
        if (order != null) {
            if (order == 0) {
                sortFieldList.add(field(asName).asc());
            } else if (order == -1) {
                sortFieldList.add(field(asName).desc());
            } else if (order > 0) {

                String[] contentArray = new String[]{};
                buildDimensionCustomOrder(sortFieldList, fieldName, asName, namespace, contentArray);
            }
        } else if(CollectionUtil.isNotEmpty(orderContentList)) {
            buildDimensionCustomOrder(sortFieldList, fieldName, asName, namespace, ArrayUtil.toArray(orderContentList, String.class));
        } else {
            defaultSortFieldList.add(field(asName).asc());
        }
    }

    private void buildDimensionCustomOrder(List<SortField<?>> sortFieldList, String fieldName, String asName, String namespace, String[] contentArray) {
        HashMap<Object, Object> contentMap = MapUtil.newHashMap(true);
        int index = 1;
        for (String content : contentArray) {
            contentMap.put(StrUtil.removeAllLineBreaks(content), DSL.inline(index));
            index++;
        }
        String sortFieldName = asName;
        if (NameSpaceConstant.DSSP.equals(namespace) || NameSpaceConstant.VERTICA.equals(namespace)) {
            sortFieldName = StrUtil.format("to_char({})", fieldName);
        }
        SortField<Object> customerSort = choose(field(sortFieldName))
                .mapValues(contentMap)
                .otherwise(DSL.inline(index)).asc();
        sortFieldList.add(customerSort);
        sortFieldList.add(field(asName).asc());
    }

    public String getFromSql(OlapDsDatabase dsDatabase, OlapDsTable dsTable) {
        String namespace = dsDatabase.getNamespace();
        String databaseName = dsDatabase.getName();
        String tableName = dsTable.getName();
        Boolean isView = dsTable.getIsView();
        String fromSql = "";
        if(BooleanUtil.isTrue(isView)) {
            fromSql = dsTable.getViewSql();
        } else {
            if (NameSpaceConstant.CLICKHOUSE.equals(namespace)) {
                if(StrUtil.isEmpty(databaseName)) {
                    fromSql = tableName;
                } else {
                    fromSql = StrUtil.format("{}.{}", databaseName, tableName);
                }
                if(BooleanUtil.isTrue(dsTable.getAppendFinal())) {
                    fromSql = fromSql + " final ";
                }
            }
        }
        return fromSql;
    }

    public String buildTimeFieldName(String fieldName, String group, String nameSpace) {
        if (TimeUnitConstant.YEAR.equals(group)) {
            fieldName = StrUtil.format(" formatDateTime({}, '%Y')", fieldName);
        }
        if (TimeUnitConstant.QUARTER.equals(group)) {
            fieldName = StrUtil.format("concat(toString(toYear({})), '年Q', toString(toQuarter({})))", fieldName, fieldName);
        }
        if (TimeUnitConstant.MONTH.equals(group)) {
            fieldName = StrUtil.format(" formatDateTime({}, '%Y-%m')", fieldName);
        }
        if (TimeUnitConstant.WEEK.equals(group)) {
            fieldName = StrUtil.format(" formatDateTime({}, '%G(%V)')", fieldName);
        }
        if (TimeUnitConstant.DAY.equals(group)) {
            fieldName = StrUtil.format(" formatDateTime({}, '%Y-%m-%d')", fieldName);
        }
        if (TimeUnitConstant.HOUR.equals(group)) {
            fieldName = StrUtil.format(" formatDateTime({}, '%Y-%m-%d %H')", fieldName);
        }
        if (TimeUnitConstant.MINUTE.equals(group)) {
            fieldName = StrUtil.format(" formatDateTime({}, '%Y-%m-%d %H:%M')", fieldName);
        }
        return fieldName;
    }

    /**
     * 处理留存图, 留存图固定两个维度,一个可选维度，两个度量
     *
     * @param queryChartResultVO
     * @param queryChartBO
     */
    private void handleRetainData(QueryChartBO queryChartBO, QueryChartResultVO queryChartResultVO) {
        List<OlapChartDimension> dimensionList = queryChartBO.getDimensionList();
        List<OlapChartMeasure> measureList = queryChartBO.getMeasureList();
        // 起始时间
        OlapChartDimension timeStartDimension = CollectionUtil.get(dimensionList, 0);
        // 距起始时间间隔
        OlapChartDimension timeNumDimension = CollectionUtil.get(dimensionList, 1);
        // 可选维度
        OlapChartDimension selectableDimension = CollectionUtil.get(dimensionList, 2);
        // 起始度量
        OlapChartMeasure startMeasure = CollectionUtil.get(measureList, 0);
        // 留存度量
        OlapChartMeasure retainMeasure = CollectionUtil.get(measureList, 1);

        if (IterUtil.hasNull(Arrays.asList(timeStartDimension, timeNumDimension, startMeasure, retainMeasure))) {
            return;
        }

        String chartType = queryChartBO.getOlapChart().getType();
        if (StrUtil.equalsAny(chartType, ChartTypeConstant.RETAIN_LINE_TOTAL, ChartTypeConstant.RETAIN_LINE_TOTAL_LTV)) {
            getRetainLine(new ArrayList<>(dimensionList), new ArrayList<>(measureList), queryChartBO, queryChartResultVO);
        } else if (StrUtil.equalsAny(chartType, ChartTypeConstant.RETAIN_LINE_CHANGE, ChartTypeConstant.RETAIN_LINE_CHANGE_LTV)) {
            getRetainLineRate(new ArrayList<>(dimensionList), new ArrayList<>(measureList), queryChartBO, queryChartResultVO);
        } else if (StrUtil.equalsAny(chartType, ChartTypeConstant.RETAIN_TABLE, ChartTypeConstant.RETAIN_TABLE_WITH_OUT_TOTAL,
                ChartTypeConstant.RETAIN_TABLE_LTV, ChartTypeConstant.RETAIN_TABLE_LTV_WITH_OUT_TOTAL,
                ChartTypeConstant.RETAIN_TABLE_LTV_ACCUMULATE, ChartTypeConstant.RETAIN_TABLE_LTV_ACCUMULATE_WITH_OUT_TOTAL)) {
            getRetainTable(new ArrayList<>(dimensionList), new ArrayList<>(measureList), queryChartBO, queryChartResultVO);
        }
        formatDimension(queryChartResultVO.getDatas());
    }

    private <T> T cloneQueryChartBO(QueryChartBO queryChartBO, Class<T> clazz) {
        return JSONUtil.toBean(JSONUtil.toJsonStr(queryChartBO), clazz);
    }

    /**
     * 处理留存图中的留存率线图
     *
     * @param dimensionList
     * @param measureList
     * @param queryChartBO
     * @param queryChartResultVO
     */
    private void getRetainLineRate(List<OlapChartDimension> dimensionList, List<OlapChartMeasure> measureList, QueryChartBO queryChartBO, QueryChartResultVO queryChartResultVO) {
        queryChartBO.setDimensionList(dimensionList);
        queryChartBO.setMeasureList(measureList);
        // 距起始时间间隔
        OlapChartDimension timeNumDimension = CollectionUtil.get(dimensionList, 1);
        // 可选维度
        OlapChartDimension selectableDimension = CollectionUtil.get(dimensionList, 2);

        String timeNum = queryChartBO.getQueryChartParameterVO().getRetainTimeNum();
        if(StrUtil.isEmpty(timeNum)) {
            timeNum = "0";
        }

        String condition = StrUtil.format("{} in ({})", timeNumDimension.getName(), timeNum);
        queryChartBO.setConditionSql(condition);
        if (selectableDimension != null) {
            dimensionList.removeIf(dimension -> ObjectUtil.equal(dimension.getId(), timeNumDimension.getId()));
        }

        handleRetainVirtualMeasure(measureList, queryChartBO);

        ExecuteSqlBO executeSqlBO = buildAndExecuteSql(queryChartBO);
        List<Object> datas = handleData(dimensionList, measureList, queryChartBO.getTableColumnMap(), executeSqlBO.getResult());
        if (selectableDimension == null) {
            QueryChartResultVO.DimensionData timeNumDimensionData = (QueryChartResultVO.DimensionData) datas.get(1);
            /*timeNumDimensionData.setData(timeNumDimensionData.getData().stream().map(str -> StrUtil.format("{}{}留存", str,
                    Optional.ofNullable(queryChartBO.getQueryChartResultVO()).map(QueryChartResultVO::getStyle_retain).map(OlapChartStyleRetain::getCycle_name).orElse("日")))
                    .collect(Collectors.toList()));*/
        }
        handleGroupData(dimensionList, measureList, datas, ChartTypeConstant.RETAIN);
        queryChartResultVO.setDatas(datas);
        if(!StrUtil.equalsAny(ProfileConstant.ACTIVE_PROFILE, ProfileConstant.PROD)) {
            queryChartResultVO.setSql(executeSqlBO.getSql());
            queryChartResultVO.setBindValues(executeSqlBO.getBindValues());
        }
        queryChartResultVO.setCacheHit(executeSqlBO.getCacheHit());
    }

    /**
     * 处理留存图虚拟度量
     *
     * @param measureList
     * @param queryChartBO
     */
    private void handleRetainVirtualMeasure(List<OlapChartMeasure> measureList, QueryChartBO queryChartBO) {
        OlapChartMeasure firstChartMeasure = measureList.get(0);
        OlapChartMeasure lastChartMeasure = measureList.get(1);
        measureList.remove(1);
        measureList.remove(0);

        OlapChartMeasure retainRateMeasure = new OlapChartMeasure();
        String firstMeasureFieldName = buildMeasureFieldName(firstChartMeasure.getName(), firstChartMeasure.getFunc(), queryChartBO.getTableColumnMap(), queryChartBO.getDsDatabase().getNamespace());
        String lastMeasureFieldName = buildMeasureFieldName(lastChartMeasure.getName(), lastChartMeasure.getFunc(), queryChartBO.getTableColumnMap(), queryChartBO.getDsDatabase().getNamespace());
        retainRateMeasure.setName(StrUtil.format("round({} / {}, 4)", lastMeasureFieldName, firstMeasureFieldName));
        if (measureList.size() == 0) {
            //无维度时规则：固定展示 总体留存率
            retainRateMeasure.setAliasName("总体留存率");
            if(StrUtil.equalsAny(queryChartBO.getQueryChartParameterVO().getChartType(), ChartTypeConstant.RETAIN_LINE_CHANGE_LTV, ChartTypeConstant.RETAIN_LINE_TOTAL_LTV)) {
                retainRateMeasure.setAliasName("LTV");
            }
        }
        measureList.add(retainRateMeasure);
    }

    /**
     * 处理留存图中的留存线图
     *
     * @param dimensionList
     * @param measureList
     * @param queryChartBO
     * @param queryChartResultVO
     */
    private void getRetainLine(List<OlapChartDimension> dimensionList, List<OlapChartMeasure> measureList, QueryChartBO queryChartBO, QueryChartResultVO queryChartResultVO) {
        queryChartBO.setDimensionList(dimensionList);
        queryChartBO.setMeasureList(measureList);
        dimensionList.remove(0);

        String timeNum = queryChartBO.getQueryChartParameterVO().getRetainTimeNum();
        if(StrUtil.isNotEmpty(timeNum)) {
            String conditionSql = StrUtil.format("{} in ({})", CollectionUtil.get(dimensionList, 0).getName(), timeNum);
            queryChartBO.setConditionSql(conditionSql);
        }

        handleRetainVirtualMeasure(measureList, queryChartBO);

        ExecuteSqlBO executeSqlBO = buildAndExecuteSql(queryChartBO);
        List<Object> datas = handleData(dimensionList, measureList, queryChartBO.getTableColumnMap(), executeSqlBO.getResult());
        handleGroupData(dimensionList, measureList, datas, ChartTypeConstant.RETAIN);
        queryChartResultVO.setDatas(datas);
        if(!StrUtil.equalsAny(ProfileConstant.ACTIVE_PROFILE, ProfileConstant.PROD)) {
            queryChartResultVO.setSql(executeSqlBO.getSql());
            queryChartResultVO.setBindValues(executeSqlBO.getBindValues());
        }
        queryChartResultVO.setCacheHit(executeSqlBO.getCacheHit());
    }

    /**
     * 留存图中的表格
     *
     * @param dimensionList
     * @param measureList
     * @param queryChartBO
     * @return
     */
    private List<Object> getRetainTable(List<OlapChartDimension> dimensionList, List<OlapChartMeasure> measureList, QueryChartBO queryChartBO, QueryChartResultVO queryChartResultVO) {
        // 起始时间
        OlapChartDimension timeStartDimension = CollectionUtil.get(dimensionList, 0);
        // 距起始时间间隔
        OlapChartDimension timeNumDimension = CollectionUtil.get(dimensionList, 1);
        // 可选维度
        OlapChartDimension selectableDimension = CollectionUtil.get(dimensionList, 2);

        String timeNum = queryChartBO.getQueryChartParameterVO().getRetainTimeNum();
        if(StrUtil.isNotEmpty(timeNum)) {
            String conditionSql = StrUtil.format("{} in ({})", timeNumDimension.getName(), timeNum);
            queryChartBO.setConditionSql(conditionSql);
        }

        queryChartBO.setDimensionList(dimensionList);
        queryChartBO.setMeasureList(measureList);
        OlapChartMeasure firstChartMeasure = measureList.get(0);
        OlapChartMeasure lastChartMeasure = measureList.get(1);
        measureList.remove(1);
        measureList.remove(0);

        String dimensionName = timeNumDimension.getName();
        Integer dimensionId = timeNumDimension.getId();
        List<String> dimensionGroupSqlResult = getDimensionGroupForRowToColumn(queryChartBO, dimensionName, 0);
        if (CollectionUtil.isEmpty(dimensionGroupSqlResult)) {
            return Collections.emptyList();
        }

        String chartType = queryChartBO.getQueryChartParameterVO().getChartType();
        OlapChartMeasure firstVirtualChartMeasure = new OlapChartMeasure();
        String rowToColumnVirtualMeasureName = getRowToColumnVirtualMeasureName(dimensionName, firstChartMeasure.getName(), firstChartMeasure.getFunc(),
                StrUtil.equalsAny(chartType, ChartTypeConstant.RETAIN_TABLE_LTV_ACCUMULATE, ChartTypeConstant.RETAIN_TABLE_LTV_ACCUMULATE_WITH_OUT_TOTAL) ? dimensionGroupSqlResult.get(dimensionGroupSqlResult.size()-1) : dimensionGroupSqlResult.get(0));
        firstVirtualChartMeasure.setName(buildMeasureFieldName(rowToColumnVirtualMeasureName, firstChartMeasure.getFunc(), queryChartBO.getTableColumnMap(), queryChartBO.getDsDatabase().getNamespace()));
        firstVirtualChartMeasure.setAliasName("用户群");
        firstVirtualChartMeasure.setVirtual(2);
        measureList.add(firstVirtualChartMeasure);

        if(StrUtil.equalsAny(chartType, ChartTypeConstant.RETAIN_TABLE_LTV, ChartTypeConstant.RETAIN_TABLE_LTV_WITH_OUT_TOTAL, ChartTypeConstant.RETAIN_TABLE_LTV_ACCUMULATE, ChartTypeConstant.RETAIN_TABLE_LTV_ACCUMULATE_WITH_OUT_TOTAL)) {
            OlapChartMeasure secondVirtualChartMeasure = new OlapChartMeasure();
            secondVirtualChartMeasure.setName(buildMeasureFieldName(getRowToColumnVirtualMeasureName(dimensionName, lastChartMeasure.getName(), lastChartMeasure.getFunc(), dimensionGroupSqlResult.get(dimensionGroupSqlResult.size()-1)),
                    lastChartMeasure.getFunc(), queryChartBO.getTableColumnMap(), queryChartBO.getDsDatabase().getNamespace()));
            secondVirtualChartMeasure.setAliasName("累计付费金额");
            secondVirtualChartMeasure.setVirtual(2);
            measureList.add(secondVirtualChartMeasure);
        }

        for (String sqlResultValue : dimensionGroupSqlResult) {
            String virtualMeasureNameDenominator = getRowToColumnVirtualMeasureName(dimensionName, firstChartMeasure.getName(), firstChartMeasure.getFunc(), sqlResultValue);
            String virtualMeasureNameNumerator = getRowToColumnVirtualMeasureName(dimensionName, lastChartMeasure.getName(), lastChartMeasure.getFunc(), sqlResultValue);
            String virtualMeasureFieldNameNumerator = buildMeasureFieldName(virtualMeasureNameNumerator, lastChartMeasure.getFunc(), queryChartBO.getTableColumnMap(), queryChartBO.getDsDatabase().getNamespace());
            String virtualMeasureFieldNameDenominator = buildMeasureFieldName(virtualMeasureNameDenominator, firstChartMeasure.getFunc(), queryChartBO.getTableColumnMap(), queryChartBO.getDsDatabase().getNamespace());
            String virtualMeasureName = StrUtil.format("{} / {}", virtualMeasureFieldNameNumerator, virtualMeasureFieldNameDenominator);
            if(StrUtil.equalsAny(chartType, ChartTypeConstant.RETAIN_TABLE_LTV, ChartTypeConstant.RETAIN_TABLE_LTV_WITH_OUT_TOTAL, ChartTypeConstant.RETAIN_TABLE_LTV_ACCUMULATE, ChartTypeConstant.RETAIN_TABLE_LTV_ACCUMULATE_WITH_OUT_TOTAL)) {
                virtualMeasureName = StrUtil.format("if({}=0, null, concat(toString({}),'-',toString({}),'-',toString({})))",
                        virtualMeasureFieldNameDenominator, virtualMeasureName, virtualMeasureFieldNameNumerator, virtualMeasureFieldNameDenominator);
            } else {
                virtualMeasureName = StrUtil.format("if({}=0, null, concat(concat(toString({}),'-'),toString({})))",
                        virtualMeasureFieldNameDenominator, virtualMeasureName, virtualMeasureFieldNameNumerator);
            }

            OlapChartMeasure virtualChartMeasure = new OlapChartMeasure();
            virtualChartMeasure.setName(virtualMeasureName);
            virtualChartMeasure.setAliasName(sqlResultValue);
            virtualChartMeasure.setVirtual(2);
            measureList.add(virtualChartMeasure);
        }
        dimensionList.removeIf(dimension -> ObjectUtil.equal(dimension.getId(), dimensionId));
        if (selectableDimension != null) {
            dimensionList.removeIf(dimension -> ObjectUtil.equal(dimension.getId(), timeStartDimension.getId()));
            if(StrUtil.equalsAny(chartType, ChartTypeConstant.RETAIN_TABLE, ChartTypeConstant.RETAIN_TABLE_LTV, ChartTypeConstant.RETAIN_TABLE_LTV_ACCUMULATE)) {
                // 使用rollup计算全部数据，全部数据排在第一。
                selectableDimension.setOrderContentList(CollectionUtil.newArrayList("全部"));
            }
            // 默认估计初始值排序
            if(selectableDimension.getOrder() == null) {
                QueryChartBO cloneQueryChartBOForSort = cloneQueryChartBO(queryChartBO, QueryChartBO.class);
                int size = cloneQueryChartBOForSort.getMeasureList().size();
                while (size > 1) {
                    size = size - 1;
                    cloneQueryChartBOForSort.getMeasureList().remove(size);
                }
                BuildSqlResultBO buildSqlResultBOForSort = buildSql(cloneQueryChartBOForSort);
                ExecuteSqlBO executeSqlBO = executeSqlService.executeSql(cloneQueryChartBOForSort.getDsDatabase(), cloneQueryChartBOForSort.getOlapDsTable(), buildSqlResultBOForSort.getSql(), buildSqlResultBOForSort.getBindValues(),
                        cloneQueryChartBOForSort.getQueryChartParameterVO().getCache(), SqlUtil.QUERY_TYPE_ROW);
                List<List<String>> result = executeSqlBO.getResult();
                if(CollectionUtil.isNotEmpty(result)) {
                    Comparator<List<String>> comparator = Comparator.comparingDouble(v -> Double.parseDouble(v.get(1)));
                    List<String> orderContentList = result.stream().filter(v -> v.get(1)!=null).sorted(comparator.reversed()).map(v -> v.get(0)).collect(Collectors.toList());
                    selectableDimension.getOrderContentList().addAll(orderContentList);
                }
            }
        } else {
            if(StrUtil.equalsAny(chartType, ChartTypeConstant.RETAIN_TABLE, ChartTypeConstant.RETAIN_TABLE_LTV, ChartTypeConstant.RETAIN_TABLE_LTV_ACCUMULATE)) {
                // 使用rollup计算全部数据，全部数据排在第一。
                timeStartDimension.setOrderContentList(CollectionUtil.newArrayList("全部"));
            }
        }

        if(StrUtil.equalsAny(chartType, ChartTypeConstant.RETAIN_TABLE, ChartTypeConstant.RETAIN_TABLE_LTV, ChartTypeConstant.RETAIN_TABLE_LTV_ACCUMULATE)) {
            queryChartBO.getQueryChartParameterVO().setWithRollup(true);
            queryChartBO.getQueryChartParameterVO().setWithRollupName("全部");
        }

        BuildSqlResultBO buildSqlResultBO = buildSql(queryChartBO);
        ExecuteSqlBO executeSqlBO = executeSqlService.executeSql(queryChartBO.getDsDatabase(), queryChartBO.getOlapDsTable(), buildSqlResultBO.getSql(), buildSqlResultBO.getBindValues(), queryChartBO.getQueryChartParameterVO().getCache(), SqlUtil.QUERY_TYPE_COLUMN);

        List<Object> datas = handleData(dimensionList, measureList, queryChartBO.getTableColumnMap(), executeSqlBO.getResult());
        queryChartResultVO.setDatas(datas);
        if(!StrUtil.equalsAny(ProfileConstant.ACTIVE_PROFILE, ProfileConstant.PROD)) {
            queryChartResultVO.setSql(buildSqlResultBO.getSql());
            queryChartResultVO.setBindValues(buildSqlResultBO.getBindValues());
        }
        queryChartResultVO.setCacheHit(executeSqlBO.getCacheHit());
        return datas;
    }

    private Boolean isEmptyCondition(QueryChartParameterVO.ConditionVO conditionVO) {
        String value = conditionVO.getValue();
        if(StrUtil.isEmpty(value) || "[]".equals(value)) {
            return true;
        }
        return false;
    }

    private Boolean isAllEmptyCondition(List<QueryChartParameterVO.ConditionVO> conditionVOList) {
        if(CollectionUtil.isEmpty(conditionVOList)) {
            return true;
        }

        Boolean[] booleanArray = conditionVOList.stream().map(this::isEmptyCondition).toArray(Boolean[]::new);
        if(BooleanUtil.andOfWrap(booleanArray)) {
            return true;
        }
        return false;
    }

}
