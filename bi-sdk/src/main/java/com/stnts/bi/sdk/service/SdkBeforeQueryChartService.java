package com.stnts.bi.sdk.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.json.JSONUtil;
import com.stnts.bi.sdk.constant.MeasureNameConstant;
import com.stnts.bi.sdk.util.TemplateHelper;
import com.stnts.bi.sdk.vo.QueryChartParameterForSdkVO;
import com.stnts.bi.sql.bo.BuildSqlResultBO;
import com.stnts.bi.sql.bo.QueryChartBO;
import com.stnts.bi.sql.constant.ChartTypeConstant;
import com.stnts.bi.sql.constant.FilterLogicConstant;
import com.stnts.bi.sql.constant.FunctionConstant;
import com.stnts.bi.sql.constant.TimeUnitConstant;
import com.stnts.bi.sql.entity.OlapChartDimension;
import com.stnts.bi.sql.entity.OlapChartMeasure;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 刘天元
 */
@Service
public class SdkBeforeQueryChartService {

    private final QueryChartService queryChartService;

    public final static String AGG_LOCAL_TABLE_NAME = "rt_ads_sdk_all_agg_d";

    public final static String DATABASE_NAME = "bi_sdk";

    public SdkBeforeQueryChartService(QueryChartService queryChartService) {
        this.queryChartService = queryChartService;
    }

    public QueryChartParameterVO beforeQuery(QueryChartParameterVO queryChartParameter, Integer topN) {
        String id = queryChartParameter.getId();

        if(StrUtil.equalsAny(id, "data-overview-operation-chart-50")) {
            String dimensionName = Optional.ofNullable(CollectionUtil.get(queryChartParameter.getDimension(), 1)).map(OlapChartDimension::getName).orElse(null);
            if("app_version".equals(dimensionName)) {
                QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
                conditionVO.setName("app_version");
                conditionVO.setLogic(FilterLogicConstant.ISNOTEMPTY);
                queryChartParameter.getDashboard().add(conditionVO);
            }
        }

        if(topN != null && StrUtil.equalsAny(id, "data-overview-operation-chart-40", "data-overview-payment-chart-40", "channel-analyse-distribution-10")) {
            handleTopNLeft(queryChartParameter);
        }

        if(topN != null && StrUtil.equalsAny(id, "data-overview-operation-chart-50", "data-overview-payment-chart-50", "channel-analyse-distribution-20", "tendency-analyse-10")) {
            queryChartParameter = handleTopNRight(queryChartParameter, topN);
        }

        if(StrUtil.equalsAny(id, "data-overview-operation-chart-40", "data-overview-payment-chart-40", "channel-analyse-distribution-10")) {
            queryChartParameter = setMeasureOrder(queryChartParameter);
        }

        if(StrUtil.equalsAny(id, "channel-analyse-distribution-10")) {
            if(StrUtil.equalsAny(
                    Optional.ofNullable(CollectionUtil.get(queryChartParameter.getMeasure(), 0)).map(OlapChartMeasure::getAliasName).orElse(""),
                    MeasureNameConstant.percentMeasure)) {
                queryChartParameter.setHavingSql("measure0 > 0");
            }
        }

        if(StrUtil.equalsAny(id, "channel-analyse-distribution-30", "tendency-analyse-20", "channel-analyse-tendency-20")) {
            List<String> measureAsNames = new ArrayList<>();
            for (int i = 0; i < queryChartParameter.getMeasure().size(); i++) {
                String str = "measure" + i;
                str = StrUtil.format("if(isFinite({}) , {}, 0)", str, str);
                measureAsNames.add(str);
            }
            String sql = String.join("+", measureAsNames);
            queryChartParameter.setHavingSql(StrUtil.format("({}) > 0", sql));
        }

        if(StrUtil.equalsAny(id, "channel-analyse-distribution-30", "channel-analyse-distribution-40")) {
            queryChartParameter = handleAvgValue(queryChartParameter, id);
        }

        if(StrUtil.equals(id, "channel-analyse-tendency-30")) {
            queryChartParameter.setTableName("rt_dwd_sdk_uuid_first_detail");
            queryChartParameter.getMeasure().forEach(x -> {
                if("uv".equals(x.getName())) {
                    x.setName("uuid");
                }
            });
        }

        if(StrUtil.equals(id, "channel-analyse-tendency-40")) {
            if("view_register_detail".equals(queryChartParameter.getTableName())) {
                queryChartParameter.setTableName("rt_dwd_sdk_user_reg_detail");
            } else if("view_payment_detail".equals(queryChartParameter.getTableName())) {
                queryChartParameter.setTableName("rt_dwd_sdk_payment_detail");
            }
        }

        if(StrUtil.equalsAny(id, "channel-analyse-tendency-45", "channel-analyse-tendency-50", "channel-analyse-tendency-60")) {
            handleActiveUserOsDistribution(queryChartParameter, id);
        }

        if(StrUtil.equals(id, "channel-analyse-effect-05")) {
            if("view_user_retain".equals(queryChartParameter.getTableName())) {
                queryChartParameter.setTableName("rt_ads_sdk_retain_user_day");
            } else if("view_payment_retain".equals(queryChartParameter.getTableName())) {
                queryChartParameter.setTableName("rt_ads_sdk_retain_payment_day");
            } else if("view_active_retain".equals(queryChartParameter.getTableName())) {
                queryChartParameter.setTableName("rt_ads_sdk_retain_active_day");
            } else if("view_payment_first_ltv".equals(queryChartParameter.getTableName())) {
                queryChartParameter.setTableName("rt_ads_sdk_ltv_payment_day");
            } else if("view_accumulate_ltv".equals(queryChartParameter.getTableName())) {
                queryChartParameter.setTableName("rt_ads_sdk_ltv_accumulate_day");
            }
            queryChartParameter.getMeasure().forEach(x -> {
                String name = x.getName();
                if("initial".equals(name)) {
                    x.setName("initial_value");
                    x.setFunc("groupBitmapOr");
                }
            });
        }

        if(StrUtil.equalsAny(id, "channel-analyse-effect-10", "channel-analyse-effect-20", "channel-analyse-effect-30", "channel-analyse-effect-40",
                "user-analyse-retain-10", "user-analyse-ltv-10")) {
            handleRetain(queryChartParameter, id);
        }

        return queryChartParameter;
    }

    private void handleRetain(QueryChartParameterVO queryChartParameter, String id) {
        // 初始化retainTimeNum
        if (StrUtil.equals(queryChartParameter.getChartType(), ChartTypeConstant.RETAIN_LINE_CHANGE)) {
            String intervalValue = "";
            for (QueryChartParameterVO.ConditionVO conditionVO : Optional.ofNullable(queryChartParameter.getScreen()).orElse(Collections.emptyList())) {
                if("interval".equals(conditionVO.getName())) {
                    intervalValue = conditionVO.getValue();
                }
            }
            if(StrUtil.isNotEmpty(intervalValue) && StrUtil.isEmpty(queryChartParameter.getRetainTimeNum())) {
                queryChartParameter.setRetainTimeNum(intervalValue);
            }
        }
        String group = queryChartParameter.getDimension().get(0).getGroup();

        // 重命名表名。 处理周留存、月留存。
        Function<String, String> getViewFunction = x -> {
            Template template = TemplateHelper.getTemplateEngine().getTemplate(StrUtil.format("sdk/{}.sql", x));
            return template.render(Dict.create());
        };
        Consumer<QueryChartParameterVO> userRetainConsumer = x -> {
            if(TimeUnitConstant.DAY.equals(group)) {
                x.setTableName("rt_ads_sdk_retain_user_day");
            } else if(TimeUnitConstant.WEEK.equals(group)) {
                x.setTableName("rt_ads_sdk_retain_user_day");
                x.setViewSql(getViewFunction.apply("rt_ads_sdk_retain_user_week"));
            } else if(TimeUnitConstant.MONTH.equals(group)) {
                x.setTableName("rt_ads_sdk_retain_user_day");
                x.setViewSql(getViewFunction.apply("rt_ads_sdk_retain_user_month"));
            }
        };

        if(StrUtil.equalsAny(id, "channel-analyse-effect-10", "channel-analyse-effect-20", "user-analyse-retain-10")) {
            if("view_user_retain".equals(queryChartParameter.getTableName())) {
                userRetainConsumer.accept(queryChartParameter);
            } else if("view_payment_retain".equals(queryChartParameter.getTableName())) {
                if(TimeUnitConstant.DAY.equals(group)) {
                    queryChartParameter.setTableName("rt_ads_sdk_retain_payment_day");
                } else if(TimeUnitConstant.WEEK.equals(group)) {
                    queryChartParameter.setTableName("rt_ads_sdk_retain_payment_day");
                    queryChartParameter.setViewSql(getViewFunction.apply("rt_ads_sdk_retain_payment_week"));
                } else if(TimeUnitConstant.MONTH.equals(group)) {
                    queryChartParameter.setTableName("rt_ads_sdk_retain_payment_day");
                    queryChartParameter.setViewSql(getViewFunction.apply("rt_ads_sdk_retain_payment_month"));
                }
            } else if("view_active_retain".equals(queryChartParameter.getTableName())) {
                if(TimeUnitConstant.DAY.equals(group)) {
                    queryChartParameter.setTableName("rt_ads_sdk_retain_active_day");
                } else if(TimeUnitConstant.WEEK.equals(group)) {
                    queryChartParameter.setTableName("rt_ads_sdk_retain_active_day");
                    queryChartParameter.setViewSql(getViewFunction.apply("rt_ads_sdk_retain_active_week"));
                } else if(TimeUnitConstant.MONTH.equals(group)) {
                    queryChartParameter.setTableName("rt_ads_sdk_retain_active_day");
                    queryChartParameter.setViewSql(getViewFunction.apply("rt_ads_sdk_retain_active_month"));
                }
            }
        }

        if(StrUtil.equalsAny(id, "channel-analyse-effect-30", "channel-analyse-effect-40", "user-analyse-ltv-10")) {
            if("view_user_retain".equals(queryChartParameter.getTableName())) {
                userRetainConsumer.accept(queryChartParameter);
            } else if("view_payment_first_ltv".equals(queryChartParameter.getTableName())) {
                if(TimeUnitConstant.DAY.equals(group)) {
                    queryChartParameter.setTableName("rt_ads_sdk_ltv_payment_day");
                } else if(TimeUnitConstant.WEEK.equals(group)) {
                    queryChartParameter.setTableName("rt_ads_sdk_ltv_payment_day");
                    queryChartParameter.setViewSql(getViewFunction.apply("rt_ads_sdk_ltv_payment_week"));
                } else if(TimeUnitConstant.MONTH.equals(group)) {
                    queryChartParameter.setTableName("rt_ads_sdk_ltv_payment_day");
                    queryChartParameter.setViewSql(getViewFunction.apply("rt_ads_sdk_ltv_payment_month"));
                }
            } else if("view_accumulate_ltv".equals(queryChartParameter.getTableName())) {
                if(TimeUnitConstant.DAY.equals(group)) {
                    queryChartParameter.setTableName("rt_ads_sdk_ltv_accumulate_day");
                } else if(TimeUnitConstant.WEEK.equals(group)) {
                    queryChartParameter.setTableName("rt_ads_sdk_ltv_accumulate_day");
                    queryChartParameter.setViewSql(getViewFunction.apply("rt_ads_sdk_ltv_accumulate_week"));
                } else if(TimeUnitConstant.MONTH.equals(group)) {
                    queryChartParameter.setTableName("rt_ads_sdk_ltv_accumulate_day");
                    queryChartParameter.setViewSql(getViewFunction.apply("rt_ads_sdk_ltv_accumulate_month"));
                }
            }
        }

        // 重命名维度和度量
        queryChartParameter.getDimension().forEach(x -> {
            String name = x.getName();
            if("date".equals(name)) {
                x.setName(SdkQueryChartService.dateFieldName);
            }
            if("interval".equals(name)) {
                x.setName("time_interval");
            }
        });

        queryChartParameter.getMeasure().forEach(x -> {
            String name = x.getName();
            if("initial".equals(name)) {
                if("rt_ads_sdk_ltv_accumulate_day".equals(queryChartParameter.getTableName())) {
                    x.setName("initial_value");
                } else {
                    x.setName("initial_value_0");
                }
                x.setFunc("groupBitmapOr");
            } else if("retain".equals(name)) {
                x.setName("retain_value");
                x.setFunc("groupBitmapOr");
            } else if("pay_fee".equals(name)) {
                x.setFunc("arraySum(maxMap({}).2)");
                //x.setFunc("sum(arraySum({}.2))");
            }
        });

        Consumer<QueryChartParameterVO.ConditionVO> fun = condition -> {
            String name = condition.getName();
            if("date".equals(name)) {
                condition.setName(SdkQueryChartService.dateFieldName);
            } else if("interval".equals(name)) {
                condition.setName("time_interval");
            }
        };
        queryChartParameter.getDashboard().forEach(fun);
        queryChartParameter.getScreen().forEach(fun);

        // 构建视图，获取初始值
        if(!"rt_ads_sdk_ltv_accumulate_day".equals(queryChartParameter.getTableName())) {
            BuildSqlResultBO buildSqlResultBO = handleRetainTableView(queryChartParameter);
            String viewSql = getRetainTableViewSql(queryChartParameter, buildSqlResultBO);
            queryChartParameter.setViewSql(viewSql);
            queryChartParameter.setViewBindValues(buildSqlResultBO.getBindValues());
        }

        // 重写chartType
        if(ChartTypeConstant.RETAIN_TABLE.equals(queryChartParameter.getChartType())) {
            if(StrUtil.equals(id, "channel-analyse-effect-20")) {
                queryChartParameter.setChartType(ChartTypeConstant.RETAIN_TABLE_WITH_OUT_TOTAL);
            } else if(StrUtil.equalsAny(id, "channel-analyse-effect-30", "user-analyse-ltv-10")) {
                queryChartParameter.setChartType(ChartTypeConstant.RETAIN_TABLE_LTV);
                if("rt_ads_sdk_ltv_accumulate_day".equals(queryChartParameter.getTableName())) {
                    queryChartParameter.setChartType(ChartTypeConstant.RETAIN_TABLE_LTV_ACCUMULATE);
                }
            } else if(StrUtil.equals(id, "channel-analyse-effect-40")) {
                queryChartParameter.setChartType(ChartTypeConstant.RETAIN_TABLE_LTV_WITH_OUT_TOTAL);
                if("rt_ads_sdk_ltv_accumulate_day".equals(queryChartParameter.getTableName())) {
                    queryChartParameter.setChartType(ChartTypeConstant.RETAIN_TABLE_LTV_ACCUMULATE_WITH_OUT_TOTAL);
                }
            }
        }

        if(StrUtil.equalsAny(id, "channel-analyse-effect-30", "user-analyse-ltv-10")) {
            if(ChartTypeConstant.RETAIN_LINE_TOTAL.equals(queryChartParameter.getChartType())) {
                queryChartParameter.setChartType(ChartTypeConstant.RETAIN_LINE_TOTAL_LTV);
            }
            if(ChartTypeConstant.RETAIN_LINE_CHANGE.equals(queryChartParameter.getChartType())) {
                queryChartParameter.setChartType(ChartTypeConstant.RETAIN_LINE_CHANGE_LTV);
            }
        }

        // 过滤未来数据
        if(StrUtil.equalsAny(id, "channel-analyse-effect-10", "channel-analyse-effect-20", "user-analyse-retain-10")) {
            QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
            if(TimeUnitConstant.DAY.equals(group)) {
                conditionVO.setName("addDays(`dt`, `time_interval`)");
                conditionVO.setFunc("");
            } else if(TimeUnitConstant.WEEK.equals(group)) {
                conditionVO.setName("addWeeks(`dt`, `time_interval`)");
                conditionVO.setFunc("");
            } else if(TimeUnitConstant.MONTH.equals(group)) {
                conditionVO.setName("addMonths(`dt`, `time_interval`)");
                conditionVO.setFunc("");
            }
            conditionVO.setLogic(FilterLogicConstant.LTE);
            conditionVO.setValue(DateUtil.today());
            queryChartParameter.getDashboard().add(conditionVO);
        }

        // 图例排序
        if(StrUtil.equalsAny(id, "user-analyse-retain-10", "user-analyse-ltv-10")) {
            if(StrUtil.equalsAny(queryChartParameter.getChartType(),  ChartTypeConstant.RETAIN_LINE_CHANGE, ChartTypeConstant.RETAIN_LINE_CHANGE_LTV)) {
                if(queryChartParameter.getDimension().size() == 2) {
                    queryChartParameter.getDimension().get(1).setGroupDataOrderContentList(CollectionUtil.newArrayList(queryChartParameter.getRetainTimeNum().split(",")));
                }
            }
        }

        queryChartParameter.getDimension().get(0).setGroup(TimeUnitConstant.DAY);
    }

    private BuildSqlResultBO handleRetainTableView(QueryChartParameterVO queryChartParameter) {
        QueryChartParameterVO newQueryChartParameterVO = new QueryChartParameterVO();
        newQueryChartParameterVO.setLimit((double) Integer.MAX_VALUE);
        newQueryChartParameterVO.setTableName(queryChartParameter.getTableName());
        newQueryChartParameterVO.setViewSql(queryChartParameter.getViewSql());

        List<OlapChartMeasure> measures = new ArrayList<>();
        OlapChartMeasure chartMeasure0 = new OlapChartMeasure();
        chartMeasure0.setName("dt");
        chartMeasure0.setAsName("dt");
        chartMeasure0.setOrder(-2);
        measures.add(chartMeasure0);
        OlapChartMeasure chartMeasure1 = new OlapChartMeasure();
        chartMeasure1.setName("product_id");
        chartMeasure1.setAsName("product_id");
        chartMeasure1.setOrder(-2);
        measures.add(chartMeasure1);
        OlapChartMeasure chartMeasure2 = new OlapChartMeasure();
        chartMeasure2.setName("pid");
        chartMeasure2.setAsName("pid");
        chartMeasure2.setOrder(-2);
        measures.add(chartMeasure2);
        OlapChartMeasure chartMeasure3 = new OlapChartMeasure();
        chartMeasure3.setName("os_type");
        chartMeasure3.setAsName("os_type");
        chartMeasure3.setOrder(-2);
        measures.add(chartMeasure3);
        OlapChartMeasure chartMeasure4 = new OlapChartMeasure();
        chartMeasure4.setName("initial_value");
        chartMeasure4.setAsName("initial_value_0");
        chartMeasure4.setOrder(-2);
        measures.add(chartMeasure4);

        List<QueryChartParameterVO.ConditionVO> dashboards = new ArrayList<>();
        QueryChartParameterVO.ConditionVO condition0 = new QueryChartParameterVO.ConditionVO();
        condition0.setName("time_interval");
        condition0.setLogic(FilterLogicConstant.EQ);
        condition0.setValue("0");
        dashboards.add(condition0);
        for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameter.getDashboard()) {
            if("time_interval".equals(conditionVO.getName())) {
                continue;
            }
            dashboards.add(conditionVO);
        }
        newQueryChartParameterVO.setDashboard(dashboards);
        newQueryChartParameterVO.setMeasure(measures);
        newQueryChartParameterVO.setDataSource("clickhouse21");
        newQueryChartParameterVO.setDatabaseName("bi_sdk");
        QueryChartBO queryChartBO = queryChartService.initQueryChartBO(newQueryChartParameterVO, new QueryChartResultVO());
        BuildSqlResultBO buildSqlResultBO = queryChartService.buildSql(queryChartBO);
        return buildSqlResultBO;
    }

    private String getRetainTableViewSql(QueryChartParameterVO queryChartParameter, BuildSqlResultBO buildSqlResultBO) {
        String viewSql = StrUtil.format("{} as _tt1 left join ({}) as _tt2 using ({})",
                StrUtil.isEmpty(queryChartParameter.getViewSql()) ? queryChartParameter.getDatabaseName() + "." + queryChartParameter.getTableName() : queryChartParameter.getViewSql(),
                buildSqlResultBO.getSql(),
                "`dt`,product_id,pid,os_type"
        );
        return viewSql;
    }

    private void handleActiveUserOsDistribution(QueryChartParameterVO queryChartParameter, String id) {
        QueryChartParameterVO newQueryChartParameterVO = new QueryChartParameterVO();
        newQueryChartParameterVO.setLimit(Double.MAX_VALUE);

        List<OlapChartDimension> dimensions = new ArrayList<>();
        if(StrUtil.equals(id, "channel-analyse-tendency-60")) {
            OlapChartDimension chartDimension0 = new OlapChartDimension();
            chartDimension0.setName("dt");
            chartDimension0.setAsName("date");
            dimensions.add(chartDimension0);
        }
        OlapChartDimension chartDimension1 = new OlapChartDimension();
        chartDimension1.setName("uid");
        chartDimension1.setAsName("uid");
        dimensions.add(chartDimension1);
        OlapChartMeasure chartMeasure0 = new OlapChartMeasure();
        chartMeasure0.setName("arrayStringConcat(groupArrayDistinct(os_name), '+')");
        chartMeasure0.setAsName("os_name_array");
        chartMeasure0.setOrder(-2);
        newQueryChartParameterVO.setDimension(dimensions);
        newQueryChartParameterVO.setMeasure(CollectionUtil.newArrayList(chartMeasure0));
        newQueryChartParameterVO.setDataSource("clickhouse21");
        newQueryChartParameterVO.setDatabaseName("bi_sdk");
        newQueryChartParameterVO.setTableName("rt_dwd_sdk_visitor_detail_min");
        newQueryChartParameterVO.setDashboard(queryChartParameter.getDashboard());
        QueryChartBO queryChartBO = queryChartService.initQueryChartBO(newQueryChartParameterVO, new QueryChartResultVO());
        BuildSqlResultBO buildSqlResultBO = queryChartService.buildSql(queryChartBO);
        String view = StrUtil.format("({})", buildSqlResultBO.getSql());
        queryChartParameter.setViewSql(view);
        queryChartParameter.setViewBindValues(buildSqlResultBO.getBindValues());
        queryChartParameter.setDashboard(null);
        String group = Optional.ofNullable(CollectionUtil.get(queryChartParameter.getDimension(), 0)).map(OlapChartDimension::getGroup).orElse("");
        if(TimeUnitConstant.MINUTE.equals(group)) {
            queryChartParameter.setLimit(100000.0);
        }
    }

    private QueryChartParameterVO setMeasureOrder(QueryChartParameterVO queryChartParameter) {
        queryChartParameter.getMeasure().forEach(v -> v.setOrder(-1));
        return queryChartParameter;
    }

    private QueryChartParameterVO handleAvgValue(QueryChartParameterVO queryChartParameter, String id) {
        List<String> startEndDate = SdkQueryChartService.getStartEndDate(queryChartParameter);
        Objects.requireNonNull(startEndDate);
        String startDateStr = startEndDate.get(0);
        String endDateStr = startEndDate.get(1);
        String dimensionGroup = queryChartParameter.getDimension().get(0).getGroup();
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
            if(DateUtil.year(startDate)==DateUtil.year(endDate) && DateUtil.weekOfYear(startDate)==DateUtil.weekOfYear(endDate)) {
                momYoyFlag = true;
            }
        } else if(StrUtil.equals(dimensionGroup, TimeUnitConstant.MONTH)) {
            if(DateUtil.year(startDate)==DateUtil.year(endDate) && DateUtil.month(startDate)==DateUtil.month(endDate)) {
                momYoyFlag = true;
            }
        }
        if(momYoyFlag) {
            // 命中单自然周期时，自定义里面可以配置同比环比
        } else {
            if(StrUtil.equals(id, "channel-analyse-distribution-30")) {
                queryChartParameter.getDimension().remove(0);
            } else if(StrUtil.equals(id, "channel-analyse-distribution-40")) {
                // 处理指标均值
                QueryChartBO queryChartBO = queryChartService.initQueryChartBO(queryChartParameter, new QueryChartResultVO());
                BuildSqlResultBO buildSqlResultBO = queryChartService.buildSql(queryChartBO);
                String sql = buildSqlResultBO.getSql();
                sql = StrUtil.replace(sql, "dimension", "key");
                sql = StrUtil.replace(sql, "measure", "value");
                sql = StrUtil.format("({})", sql);
                QueryChartParameterVO newQueryChartParameterVO = new QueryChartParameterVO();
                newQueryChartParameterVO.setDataSource("clickhouse21");
                newQueryChartParameterVO.setDatabaseName(DATABASE_NAME);
                newQueryChartParameterVO.setTableName(AGG_LOCAL_TABLE_NAME);
                newQueryChartParameterVO.setViewSql(sql);
                newQueryChartParameterVO.setViewBindValues(buildSqlResultBO.getBindValues());
                newQueryChartParameterVO.setLimit((double) Integer.MAX_VALUE);

                List<OlapChartDimension> dimensions = new ArrayList<>();
                for (int i = 0; i < queryChartParameter.getDimension().size(); i++) {
                    if(i == 0) {
                        continue;
                    }
                    OlapChartDimension chartDimension = new OlapChartDimension();
                    chartDimension.setName("key" + i);
                    chartDimension.setId(i);
                    chartDimension.setAliasName(queryChartParameter.getDimension().get(i).getAliasName());
                    dimensions.add(chartDimension);
                }

                newQueryChartParameterVO.setDimension(dimensions);
                newQueryChartParameterVO.setMeasure(new ArrayList<>(queryChartParameter.getMeasure().size()));
                for(int i = 0; i< queryChartParameter.getMeasure().size(); i++) {
                    OlapChartMeasure chartMeasure = new OlapChartMeasure();
                    String s = "value" + i;
                    chartMeasure.setName(StrUtil.format("avgIf({}, isFinite({}))", s, s));
                    chartMeasure.setAliasName(queryChartParameter.getMeasure().get(i).getAliasName());
                    chartMeasure.setDecimal(queryChartParameter.getMeasure().get(i).getDecimal());
                    chartMeasure.setDigitDisplay(queryChartParameter.getMeasure().get(i).getDigitDisplay());
                    newQueryChartParameterVO.getMeasure().add(chartMeasure);
                }
                queryChartParameter = newQueryChartParameterVO;
            }
        }
        return queryChartParameter;
    }

    private QueryChartParameterVO handleTopNRight(QueryChartParameterVO queryChartParameter, Integer topN) {
        QueryChartParameterVO cloneQueryChartParameter = JSONUtil.toBean(JSONUtil.toJsonStr(queryChartParameter), queryChartParameter.getClass());
        cloneQueryChartParameter.getDimension().remove(0);
        handleTopNLeft(cloneQueryChartParameter);
        if(cloneQueryChartParameter.getMeasure().size() == 3) {
            // 百分比指标根据分子排序
            cloneQueryChartParameter.getMeasure().remove(2);
            cloneQueryChartParameter.getMeasure().remove(1);
        }
        cloneQueryChartParameter.getMeasure().get(0).setOrder(-1);
        cloneQueryChartParameter.setScreen(Collections.emptyList());
        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(cloneQueryChartParameter);
        if(CollectionUtil.isEmpty(queryChartResultVO.getDatas())) {
            return queryChartParameter;
        }
        QueryChartResultVO.DimensionData dimensionData = (QueryChartResultVO.DimensionData) queryChartResultVO.getDatas().get(0);
        if(CollectionUtil.isEmpty(dimensionData.getData())) {
            return queryChartParameter;
        }
        List<String> topNDimensionData = CollectionUtil.sub(dimensionData.getData(), 0, topN);

        QueryChartParameterVO queryChartParameterTemp = JSONUtil.toBean(JSONUtil.toJsonStr(queryChartParameter), queryChartParameter.getClass());
        queryChartParameterTemp.getDimension().get(0).setAsName("key0");
        queryChartParameterTemp.getDimension().get(1).setAsName("key1");
        queryChartParameterTemp.getMeasure().get(0).setAsName("value0");
        queryChartParameterTemp.setScreen(Collections.emptyList());
        queryChartParameterTemp.setLimit(Double.MAX_VALUE);
        QueryChartBO queryChartBO = queryChartService.initQueryChartBO(queryChartParameterTemp, new QueryChartResultVO());
        BuildSqlResultBO buildSqlResultBO = queryChartService.buildSql(queryChartBO);
        String sql = buildSqlResultBO.getSql();
        sql = StrUtil.format("({})", sql);

        String otherName = StrUtil.format("Top {}以外", topN);
        QueryChartParameterVO newQueryChartParameterVO = new QueryChartParameterVO();
        OlapChartDimension chartDimension0 = new OlapChartDimension();
        chartDimension0.setName("key0");
        chartDimension0.setAliasName(queryChartParameter.getDimension().get(0).getAliasName());
        chartDimension0.setAsName(queryChartParameter.getDimension().get(0).getName());
        chartDimension0.setFormat(queryChartParameter.getDimension().get(0).getFormat());
        OlapChartDimension chartDimension1 = new OlapChartDimension();
        String str = StrUtil.format("multiIf({})", topNDimensionData.stream().map(x -> StrUtil.format("key1 = '{}', '{}'", x, x)).collect(Collectors.joining(",")) + StrUtil.format(", '{}'", otherName));
        chartDimension1.setName(str);
        chartDimension1.setAliasName(queryChartParameter.getDimension().get(1).getAliasName());
        chartDimension1.setAsName(queryChartParameter.getDimension().get(1).getName());
        chartDimension1.setGroupDataOrderContentList(topNDimensionData);
        chartDimension1.getGroupDataOrderContentList().add(otherName);
        if(StrUtil.equalsAny(queryChartParameter.getDimension().get(1).getName(), "os_name", "source_os_name")) {
            chartDimension1.setGroupDataOrderContentList(CollectionUtil.newArrayList("PC","Web","iOS","Android","H5","微信小程序","微信小游戏","TV","other","未知","其他","其它", otherName, "总计"));
        }
        OlapChartMeasure chartMeasure = new OlapChartMeasure();
        chartMeasure.setName("value0");
        chartMeasure.setFunc("sumIf({}, isFinite({}))");
        chartMeasure.setAliasName(queryChartParameter.getMeasure().get(0).getAliasName());
        chartMeasure.setDecimal(queryChartParameter.getMeasure().get(0).getDecimal());
        chartMeasure.setDigitDisplay(queryChartParameter.getMeasure().get(0).getDigitDisplay());
        newQueryChartParameterVO.setDimension(CollectionUtil.newArrayList(chartDimension0, chartDimension1));
        newQueryChartParameterVO.setMeasure(CollectionUtil.newArrayList(chartMeasure));
        newQueryChartParameterVO.setDataSource("clickhouse21");
        newQueryChartParameterVO.setDatabaseName(DATABASE_NAME);
        newQueryChartParameterVO.setTableName(AGG_LOCAL_TABLE_NAME);
        newQueryChartParameterVO.setViewSql(sql);
        newQueryChartParameterVO.setViewBindValues(buildSqlResultBO.getBindValues());
        newQueryChartParameterVO.setScreen(queryChartParameter.getScreen());
        newQueryChartParameterVO.setLimit(queryChartParameter.getLimit());
        if(StrUtil.equals("channel-analyse-distribution-20", queryChartParameter.getId())) {
            if(queryChartParameter.getDimension().size()>=2) {
                if("终端".equals(queryChartParameter.getDimension().get(1).getAliasName())) {
                    newQueryChartParameterVO.setWithRollup(true);
                    newQueryChartParameterVO.setHavingSql("key0 != ''");
                }
            }
        }
        // 过滤指标为0的数据
        if(StrUtil.isEmpty(newQueryChartParameterVO.getHavingSql())) {
            newQueryChartParameterVO.setHavingSql("measure0 > 0");
        } else {
            newQueryChartParameterVO.setHavingSql(newQueryChartParameterVO.getHavingSql() + " and measure0 > 0");
        }
        // 百分比指标不显示top N以外
        if(StrUtil.equalsAny(
                Optional.ofNullable(CollectionUtil.get(queryChartParameter.getMeasure(), 0)).map(OlapChartMeasure::getAliasName).orElse(""),
                MeasureNameConstant.percentMeasure)) {
            QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
            conditionVO.setName("key1");
            conditionVO.setLogic(FilterLogicConstant.IN);
            conditionVO.setValue(StrUtil.format("[{}]", topNDimensionData.stream().map(x -> StrUtil.format("\"{}\"", x)).collect(Collectors.joining(","))));
            if(CollectionUtil.isEmpty(newQueryChartParameterVO.getDashboard())) {
                newQueryChartParameterVO.setDashboard(new ArrayList<>());
            }
            newQueryChartParameterVO.getDashboard().add(conditionVO);
        }
        return newQueryChartParameterVO;
    }

    private void handleTopNLeft(QueryChartParameterVO queryChartParameter) {
        String measureAliasName = Optional.ofNullable(CollectionUtil.get(queryChartParameter.getMeasure(), 0)).map(OlapChartMeasure::getAliasName).orElse("");
        OlapChartMeasure measure1 = new OlapChartMeasure();
        OlapChartMeasure measure2 = new OlapChartMeasure();
        if(StrUtil.equalsAny(measureAliasName, "新增用户转化率", "新用户转化率")) {
            measure1.setName("groupBitmapOr(register_user_bit)");
            measure1.setAliasName("新增用户");
            measure2.setName("bitmapCardinality(bitmapAndnot(groupBitmapOrState(uuid_bit), groupBitmapOrState(uuid_with_uid_bit)))");
            measure2.setAliasName("游客数");
        } else if(StrUtil.equalsAny(measureAliasName, "活跃构成(新用户占比)", "活跃构成（新用户占比）")) {
            measure1.setName("bitmapCardinality(bitmapAnd(groupBitmapOrState(uid_bit), groupBitmapOrState(register_user_bit)))");
            measure1.setAliasName("新增用户");
            measure2.setName("groupBitmapOr(uid_bit)");
            measure2.setAliasName("活跃用户数");
        } else if("新付费转化率".equals(measureAliasName)) {
            measure1.setName("groupBitmapOr(pay_user_register_today_bit)");
            measure1.setAliasName("新付费用户数");
            measure2.setName("groupBitmapOr(register_user_bit)");
            measure2.setAliasName("新增用户");
        } else if("新用户ARPPU".equals(measureAliasName)) {
            measure1.setName("arraySum(maxMap(pay_fee_register_today).2)");
            measure1.setAliasName("新付费金额");
            measure2.setName("groupBitmapOr(pay_user_register_today_bit)");
            measure2.setAliasName("新付费用户数");
        } else if("活跃付费率".equals(measureAliasName)) {
            measure1.setName("groupBitmapOr(pay_user_bit)");
            measure1.setAliasName("付费用户数");
            measure2.setName("groupBitmapOr(uid_bit)");
            measure2.setAliasName("活跃用户数");
        } else if("活跃ARPU".equals(measureAliasName)) {
            measure1.setName("arraySum(maxMap(pay_fee).2)");
            measure1.setAliasName("付费金额");
            measure2.setName("groupBitmapOr(uid_bit)");
            measure2.setAliasName("活跃用户数");
        } else if("付费ARPPU".equals(measureAliasName)) {
            measure1.setName("arraySum(maxMap(pay_fee).2)");
            measure1.setAliasName("付费金额");
            measure2.setName("groupBitmapOr(pay_user_bit)");
            measure2.setAliasName("付费用户数");
        } else if("新付费用户占比".equals(measureAliasName)) {
            measure1.setName("groupBitmapOr(pay_user_register_today_bit)");
            measure1.setAliasName("新付费用户数");
            measure2.setName("groupBitmapOr(pay_user_bit)");
            measure2.setAliasName("付费用户数");
        } else if("新付费金额占比".equals(measureAliasName)) {
            measure1.setName("arraySum(maxMap(pay_fee_register_today).2)");
            measure1.setAliasName("新付费金额");
            measure2.setName("arraySum(maxMap(pay_fee).2)");
            measure2.setAliasName("付费金额");
        } else if("复购占比".equals(measureAliasName)) {
            measure1.setName("groupBitmapOr(pay_user_again_bit)");
            measure1.setAliasName("复购用户数");
            measure2.setName("groupBitmapOr(pay_user_bit)");
            measure2.setAliasName("付费用户数");
        } else if("付费成功率".equals(measureAliasName)) {
            measure1.setName("groupBitmapOr(success_order_id_bit)");
            measure1.setAliasName("付费次数");
            measure2.setName("groupBitmapOr(order_id_bit)");
            measure2.setAliasName("总付费次数");
        } else if("平均访问时长(m)".equals(measureAliasName)) {
            measure1.setName("round(arraySum(maxMap(online_duration).2) / 60000, 2)");
            measure1.setAliasName("访问时长(m)");
            measure2.setName("uniqArrayMerge(session_uniq)");
            measure2.setAliasName("访问(启动)次数");
        } else if("平均页面停留时长(m)".equals(measureAliasName)) {
            measure1.setName("arraySum(maxMap(online_duration).2) / 60000");
            measure1.setAliasName("访问时长(m)");
            measure2.setName("arraySum(maxMap(pv).2)");
            measure2.setAliasName("浏览量(PV)");
        } else if("平均访问深度".equals(measureAliasName)) {
            measure1.setName("arraySum(maxMap(pv).2)");
            measure1.setAliasName("浏览量(PV)");
            measure2.setName("uniqArrayMerge(session_uniq)");
            measure2.setAliasName("访问(启动)次数");
        } else if("跳出率".equals(measureAliasName)) {
            measure1.setName("(uniqArrayMerge(session_uniq) - uniqArrayMerge(session_again_uniq))");
            measure1.setAliasName("跳出次数");
            measure2.setName("uniqArrayMerge(session_uniq)");
            measure2.setAliasName("访问(启动)次数");
        }
        if(StrUtil.isNotEmpty(measure1.getName()) && StrUtil.isNotEmpty(measure2.getName())) {
            queryChartParameter.getMeasure().add(0, measure2);
            queryChartParameter.getMeasure().add(0, measure1);
        }
    }

    public void renameMeasureName(QueryChartParameterVO queryChartParameterVO) {
        String group = Optional.ofNullable(CollectionUtil.get(queryChartParameterVO.getDimension(), 0)).map(OlapChartDimension::getGroup).orElse("");
        Set<String> dayEnum = null;
        if(TimeUnitConstant.HOUR.equals(group)) {
            queryChartParameterVO.setTableName("rt_ads_sdk_all_agg_h");
        } else if(TimeUnitConstant.MINUTE.equals(group)) {
            queryChartParameterVO.setTableName("rt_ads_sdk_all_agg_min");
            queryChartParameterVO.setLimit(100000.0);
        } else {
            queryChartParameterVO.setTableName("rt_ads_sdk_all_agg_d");
        }
        List<String> withSql = new ArrayList<>();
        for (OlapChartMeasure chartMeasure : queryChartParameterVO.getMeasure()) {
            // TODO 简化 bitmapCardinality
            String measureAliasName = chartMeasure.getAliasName();
            if("新游客".equals(measureAliasName)) {
                chartMeasure.setName("bitmapCardinality(bitmapAndnot(groupBitmapOrState(uuid_first_bit), groupBitmapOrState(uuid_with_uid_bit)))");
                chartMeasure.setFunc("");
            } else if("老游客".equals(measureAliasName)) {
                chartMeasure.setName("bitmapCardinality(bitmapAndnot(groupBitmapOrState(uuid_bit), bitmapOr(groupBitmapOrState(uuid_with_uid_bit), groupBitmapOrState(uuid_first_bit))))");
                chartMeasure.setFunc("");
            } else if("新注册".equals(measureAliasName)) {
                chartMeasure.setName("bitmapCardinality(bitmapAnd(groupBitmapOrState(register_user_bit), groupBitmapOrState(uid_bit)))");
                chartMeasure.setFunc("");
            } else if("老用户".equals(measureAliasName)) {
                chartMeasure.setName("bitmapCardinality(bitmapAndnot(groupBitmapOrState(uid_bit), groupBitmapOrState(register_user_bit)))");
                chartMeasure.setFunc("");
            } else if(StrUtil.equalsAny(measureAliasName, "访客数(UV)", "访客数")) {
                chartMeasure.setName("bitmapCardinality(bitmapAndnot(groupBitmapOrState(uuid_bit), groupBitmapOrState(uuid_with_uid_bit))) + groupBitmapOr(uid_bit)");
                chartMeasure.setFunc("");
            } else if("游客数".equals(measureAliasName)) {
                chartMeasure.setName("bitmapCardinality(bitmapAndnot(groupBitmapOrState(uuid_bit), groupBitmapOrState(uuid_with_uid_bit)))");
                chartMeasure.setFunc("");
            } else if("新访客".equals(measureAliasName)) {
                chartMeasure.setName("bitmapCardinality(bitmapAndnot(groupBitmapOrState(uuid_first_bit), groupBitmapOrState(uuid_with_uid_bit))) + bitmapCardinality(bitmapAnd(groupBitmapOrState(register_user_bit), groupBitmapOrState(uid_bit)))");
                chartMeasure.setFunc("");
            } else if(StrUtil.equalsAny(measureAliasName, "新增用户", "注册数")) {
                chartMeasure.setName("groupBitmapOr(register_user_bit)");
                chartMeasure.setFunc("");
            } else if(StrUtil.equalsAny(measureAliasName, "新增用户转化率", "新用户转化率")) {
                chartMeasure.setName("groupBitmapOr(register_user_bit) / (groupBitmapOr(register_user_bit) + bitmapCardinality(bitmapAndnot(groupBitmapOrState(uuid_bit), groupBitmapOrState(uuid_with_uid_bit))))");
                chartMeasure.setFunc("");
            } else if("活跃用户数".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(uid_bit)");
                chartMeasure.setFunc("");
            } else if("次日留存率".equals(measureAliasName)) {
                if(StrUtil.equalsAny(group, TimeUnitConstant.MINUTE, TimeUnitConstant.HOUR)) {
                    chartMeasure.setName("0");
                } /*else if(StrUtil.equals(group, TimeUnitConstant.DAY)) {
                    chartMeasure.setName("bitmapCardinality(bitmapAnd(groupBitmapOrState(uid_bit), groupBitmapOrState(yesterday_register_user_bit))) / uniqArrayMerge(yesterday_register_user_uniq)");
                }*/ else {
                    if(dayEnum == null) {
                        dayEnum = dayEnum(queryChartParameterVO);
                    }
                    withSql.add(StrUtil.format("[{}] as sort_arr1", dayEnum.stream().map(x->StrUtil.format("groupArrayIf(tuple(tomorrow_uid_bit,register_user_bit), `dt`='{}')", x)).collect(Collectors.joining(","))));
                    chartMeasure.setName("arrayReduce('avg', arrayFilter(x->x>0,arrayMap(x-> bitmapCardinality(bitmapAnd(arrayReduce('groupBitmapOrState', arrayMap(y->y.1,x)), arrayReduce('groupBitmapOrState', arrayMap(y->y.2,x)))) / bitmapCardinality(arrayReduce('groupBitmapOrState', arrayMap(y->y.2,x))), sort_arr1)))");
                }
                chartMeasure.setFunc("");
            } else if(StrUtil.equalsAny(measureAliasName, "活跃构成(新用户占比)", "活跃构成（新用户占比）")) {
                chartMeasure.setName("bitmapCardinality(bitmapAnd(groupBitmapOrState(uid_bit), groupBitmapOrState(register_user_bit))) / groupBitmapOr(uid_bit)");
                chartMeasure.setFunc("");
            } else if(StrUtil.equalsAny(measureAliasName, "活跃粘度(昨日活跃数/30日活跃数)", "活跃粘度（昨日活跃数/30日活跃数）")) {
                if(StrUtil.equalsAny(group, TimeUnitConstant.MINUTE, TimeUnitConstant.HOUR)) {
                    chartMeasure.setName("0");
                } /*else if(StrUtil.equals(group, TimeUnitConstant.DAY)) {
                    chartMeasure.setName("uniqArrayMerge(yesterday_uid_uniq) / uniqArrayMerge(last_thirty_days_uid_uniq)");
                }*/ else {
                    if(dayEnum == null) {
                        dayEnum = dayEnum(queryChartParameterVO);
                    }
                    withSql.add(StrUtil.format("[{}] as sort_arr2", dayEnum.stream().map(x->StrUtil.format("groupArrayIf(tuple(yesterday_uid_bit,last_thirty_days_uid_bit), `dt`='{}')", x)).collect(Collectors.joining(","))));
                    chartMeasure.setName("arrayReduce('avg', arrayFilter(x->x>0,arrayMap(x-> arrayReduce('groupBitmapOr', arrayMap(y->y.1,x)) / arrayReduce('groupBitmapOr', arrayMap(y->y.2,x)), sort_arr2)))");
                }
                chartMeasure.setFunc("");
            }  else if("总体转化率".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(register_user_bit) / (groupBitmapOr(register_user_bit) + bitmapCardinality(bitmapAndnot(groupBitmapOrState(uuid_bit), groupBitmapOrState(uuid_with_uid_bit)))) * groupBitmapOr(pay_user_register_today_bit) / groupBitmapOr(register_user_bit)");
                chartMeasure.setFunc("");
            } else if("新付费用户数".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(pay_user_register_today_bit)");
                chartMeasure.setFunc("");
            } else if("新付费转化率".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(pay_user_register_today_bit) / groupBitmapOr(register_user_bit)");
                chartMeasure.setFunc("");
            } else if("新付费金额".equals(measureAliasName)) {
                chartMeasure.setName("arraySum(maxMap(pay_fee_register_today).2)");
                chartMeasure.setFunc("");
            } else if("新用户ARPPU".equals(measureAliasName)) {
                chartMeasure.setName("arraySum(maxMap(pay_fee_register_today).2) / groupBitmapOr(pay_user_register_today_bit)");
                chartMeasure.setFunc("");
            } else if("付费用户数".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(pay_user_bit)");
                chartMeasure.setFunc("");
            } else if("付费金额".equals(measureAliasName)) {
                chartMeasure.setName("arraySum(maxMap(pay_fee).2)");
                chartMeasure.setFunc("");
            }  else if("活跃付费率".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(pay_user_bit) / groupBitmapOr(uid_bit)");
                chartMeasure.setFunc("");
            } else if("活跃ARPU".equals(measureAliasName)) {
                chartMeasure.setName("arraySum(maxMap(pay_fee).2) / groupBitmapOr(uid_bit)");
                chartMeasure.setFunc("");
            } else if("付费ARPPU".equals(measureAliasName)) {
                chartMeasure.setName("arraySum(maxMap(pay_fee).2) / groupBitmapOr(pay_user_bit)");
                chartMeasure.setFunc("");
            } else if("新付费用户占比".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(pay_user_register_today_bit) / groupBitmapOr(pay_user_bit)");
                chartMeasure.setFunc("");
            } else if("新付费金额占比".equals(measureAliasName)) {
                chartMeasure.setName("arraySum(maxMap(pay_fee_register_today).2) / arraySum(maxMap(pay_fee).2)");
                chartMeasure.setFunc("");
            } else if("复购用户数".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(pay_user_again_bit)");
                chartMeasure.setFunc("");
            } else if("复购占比".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(pay_user_again_bit) / groupBitmapOr(pay_user_bit)");
                chartMeasure.setFunc("");
            } else if("首付用户数".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(pay_user_first_bit)");
                chartMeasure.setFunc("");
            } else if("首次付费金额".equals(measureAliasName)) {
                chartMeasure.setName("arraySum(maxMap(pay_fee_first_day).2)");
                chartMeasure.setFunc("");
            } else if("LTV0".equals(measureAliasName)) {
                if(StrUtil.equalsAny(group, TimeUnitConstant.MINUTE, TimeUnitConstant.HOUR)) {
                    chartMeasure.setName("0");
                } /*else if(StrUtil.equals(group, TimeUnitConstant.DAY)) {
                    chartMeasure.setName("arraySum(maxMap(pay_fee_register_today).2) / uniqArrayMerge(register_user_uniq)");
                }*/ else {
                    if(dayEnum == null) {
                        dayEnum = dayEnum(queryChartParameterVO);
                    }
                    withSql.add(StrUtil.format("[{}] as sort_arr4", dayEnum.stream().map(x->StrUtil.format("groupArrayIf(tuple(pay_fee_register_today, register_user_bit), `dt`='{}')", x)).collect(Collectors.joining(","))));
                    chartMeasure.setName("arrayReduce('avg', arrayFilter(x->x>0,arrayMap(x-> arraySum(arrayReduce('maxMap', arrayMap(y->y.1,x)).2) / arrayReduce('groupBitmapOr', arrayMap(y->y.2,x)), sort_arr4)))");
                }
                chartMeasure.setFunc("");
            } else if("付费次数".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(success_order_id_bit)");
                chartMeasure.setFunc("");
            } else if("付费成功率".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(success_order_id_bit) / groupBitmapOr(order_id_bit)");
                chartMeasure.setFunc("");
            } else if("浏览量(PV)".equals(measureAliasName)) {
                //chartMeasure.setName("arraySum(maxMap(pv).2)");
                 chartMeasure.setName("sum(arraySum(pv.2))");
                chartMeasure.setFunc("");
            } else if("访问(启动)次数".equals(measureAliasName)) {
                chartMeasure.setName("uniqArrayMerge(session_uniq)");
                chartMeasure.setFunc("");
            } else if("平均访问时长(m)".equals(measureAliasName)) {
                //chartMeasure.setName("arraySum(maxMap(online_duration).2)/uniqArrayMerge(session_uniq)/60000");
                chartMeasure.setName("sum(arraySum(online_duration.2))/uniqArrayMerge(session_uniq)/60000");
                chartMeasure.setFunc("");
            } else if("平均页面停留时长(m)".equals(measureAliasName)) {
                //chartMeasure.setName("arraySum(maxMap(online_duration).2)/arraySum(maxMap(pv).2)/60000");
                chartMeasure.setName("sum(arraySum(online_duration.2))/sum(arraySum(pv.2))/60000");
                chartMeasure.setFunc("");
            } else if("平均访问深度".equals(measureAliasName)) {
                chartMeasure.setName("arraySum(maxMap(pv).2)/uniqArrayMerge(session_uniq)");
                chartMeasure.setFunc("");
            } else if("跳出率".equals(measureAliasName)) {
                chartMeasure.setName("(uniqArrayMerge(session_uniq) - uniqArrayMerge(session_again_uniq)) / uniqArrayMerge(session_uniq)");
                chartMeasure.setFunc("");
            }  else if("新付费数".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(pay_user_register_today_bit)");
                chartMeasure.setFunc("");
            } else if("退款用户数".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(refund_pay_user_bit)");
                chartMeasure.setFunc("");
            } else if("退款金额".equals(measureAliasName)) {
                chartMeasure.setName("arraySum(maxMap(refund_pay_fee).2)");
                chartMeasure.setFunc("");
            } else if("退款订单数".equals(measureAliasName)) {
                chartMeasure.setName("groupBitmapOr(refund_order_id_bit)");
                chartMeasure.setFunc("");
            }

            if(CollectionUtil.isNotEmpty(withSql)) {
                queryChartParameterVO.setWithSql(CollectionUtil.join(withSql, ","));
            }
        }
    }

    public void renameDimensionName(QueryChartParameterForSdkVO queryChartParameterVO) {
        Integer attributionCaliber = queryChartParameterVO.getAttributionCaliber();
        List<OlapChartDimension> dimensions = queryChartParameterVO.getDimension();
        if(CollectionUtil.isNotEmpty(dimensions)) {
            for (OlapChartDimension dimension : dimensions) {
                String name = dimension.getName();
                if("date".equals(name)) {
                    dimension.setName("dt");
                }

                if(attributionCaliber == 1) {
                    if(StrUtil.equalsAny(name, "pid", "agent_name", "channel_name", "sub_channel_name", "billing_name", "os_type", "os_name")) {
                        dimension.setName("source_" + name);
                    }
                }

            }
        }
    }

    public void renameConditionName(QueryChartParameterForSdkVO queryChartParameterVO) {
        Integer attributionCaliber = queryChartParameterVO.getAttributionCaliber();
        Consumer<QueryChartParameterVO.ConditionVO> consumer = condition -> {
            String name = condition.getName();
            if("date".equals(name)) {
                condition.setName(SdkQueryChartService.dateFieldName);
            }

            if(attributionCaliber == 1) {
                if(StrUtil.equalsAny(name, "pid", "agent_name", "channel_name", "sub_channel_name", "billing_name", "os_type", "os_name")) {
                    condition.setName("source_" + name);
                }
            }
        };
        if(CollectionUtil.isNotEmpty(queryChartParameterVO.getDashboard())) {
            queryChartParameterVO.getDashboard().forEach(consumer);
        }
        if(CollectionUtil.isNotEmpty(queryChartParameterVO.getScreen())) {
            queryChartParameterVO.getScreen().forEach(consumer);
        }
        if(CollectionUtil.isNotEmpty(queryChartParameterVO.getCompare())) {
            queryChartParameterVO.getCompare().forEach(consumer);
        }
    }

    private Set<String> dayEnum(QueryChartParameterVO queryChartParameter) {
        Set<String> result = new HashSet<>();
        List<String> startEndDate = SdkQueryChartService.getStartEndDate(queryChartParameter);
        Objects.requireNonNull(startEndDate);
        String startDateStr = startEndDate.get(0);
        String endDateStr = startEndDate.get(1);
        DateTime startDate = DateUtil.parse(startDateStr);
        DateTime endDate = DateUtil.parse(endDateStr);
        addToResult(result, startDate, endDate);

        String dimensionGroup = Optional.ofNullable(CollectionUtil.get(queryChartParameter.getDimension(), 0)).map(OlapChartDimension::getGroup).orElse(null);
        if(StrUtil.isNotEmpty(dimensionGroup) && StrUtil.equalsAny(dimensionGroup, TimeUnitConstant.DAY, TimeUnitConstant.WEEK, TimeUnitConstant.MONTH)) {

            if(TimeUnitConstant.WEEK.equals(dimensionGroup)) {
                startDateStr = StrUtil.format("{}({})", DateUtil.year(startDate), queryChartService.isoWeekOfYear(startDate));
                endDateStr = StrUtil.format("{}({})", DateUtil.year(endDate), queryChartService.isoWeekOfYear(endDate));
            } else if(TimeUnitConstant.MONTH.equals(dimensionGroup)) {
                startDateStr = DateUtil.format(startDate, "yyyy-MM");
                endDateStr = DateUtil.format(endDate, "yyyy-MM");
            }

            String contrast = queryChartService.autoYoy(dimensionGroup, "yoy_rate");
            String momPreCycleTimeStartDateStr = queryChartService.getMOMPreCycleTimeStr(dimensionGroup, startDateStr);
            String momPreCycleTimeEndDateStr = queryChartService.getMOMPreCycleTimeStr(dimensionGroup, endDateStr);
            String yoyPreCycleTimeStartDateStr = queryChartService.getYOYPreCycleTimeStr(contrast, dimensionGroup, startDateStr);
            String yoyPreCycleTimeEndDateStr = queryChartService.getYOYPreCycleTimeStr(contrast, dimensionGroup, endDateStr);
            if(TimeUnitConstant.DAY.equals(dimensionGroup)) {
                addToResult(result, DateUtil.parseDate(momPreCycleTimeStartDateStr), DateUtil.parseDate(momPreCycleTimeEndDateStr));
                addToResult(result, DateUtil.parseDate(yoyPreCycleTimeStartDateStr), DateUtil.parseDate(yoyPreCycleTimeEndDateStr));
            } else if(TimeUnitConstant.MONTH.equals(dimensionGroup)) {
                addToResult(result, DateUtil.beginOfMonth(DateUtil.parse(momPreCycleTimeStartDateStr, "yyyy-MM")), DateUtil.endOfMonth(DateUtil.parse(momPreCycleTimeEndDateStr, "yyyy-MM")));
                addToResult(result, DateUtil.beginOfMonth(DateUtil.parse(yoyPreCycleTimeStartDateStr, "yyyy-MM")), DateUtil.endOfMonth(DateUtil.parse(yoyPreCycleTimeEndDateStr, "yyyy-MM")));
            } else if(TimeUnitConstant.WEEK.equals(dimensionGroup)) {
                addToResult(result, DateUtil.parse(queryChartService.formatWeek(momPreCycleTimeStartDateStr).split("-")[0], "yyyyMMdd"), DateUtil.parse(queryChartService.formatWeek(momPreCycleTimeEndDateStr).split("-")[1], "yyyyMMdd"));
                addToResult(result, DateUtil.parse(queryChartService.formatWeek(yoyPreCycleTimeStartDateStr).split("-")[0], "yyyyMMdd"), DateUtil.parse(queryChartService.formatWeek(yoyPreCycleTimeEndDateStr).split("-")[1], "yyyyMMdd"));
            }
        }
        return result;
    }

    private void addToResult(Set<String> result, DateTime startDate, DateTime endDate) {
        while (DateUtil.compare(startDate, endDate) <=0 ) {
            result.add(DateUtil.formatDate(startDate));
            startDate = DateUtil.offsetDay(startDate, 1);
        }
    }
}
