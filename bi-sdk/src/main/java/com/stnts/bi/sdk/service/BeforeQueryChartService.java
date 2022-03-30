package com.stnts.bi.sdk.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.json.JSONUtil;
import com.stnts.bi.sdk.util.TemplateHelper;
import com.stnts.bi.sql.constant.ColumnTypeConstant;
import com.stnts.bi.sql.constant.FilterLogicConstant;
import com.stnts.bi.sql.constant.TimeUnitConstant;
import com.stnts.bi.sql.entity.OlapChartDimension;
import com.stnts.bi.sql.util.JacksonUtil;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 刘天元
 */
@Service
public class BeforeQueryChartService {

    public void beforeQuery(QueryChartParameterVO queryChartParameterVO) {
        if(StrUtil.equalsAny(queryChartParameterVO.getId(), "realTimeData-operatingMetrics-all-180", "realTimeData-operatingMetrics-all-190",
                "realTimeData-operatingMetrics-app-100","realTimeData-operatingMetrics-app-110",
                "realTimeData-operatingMetrics-pc-100","realTimeData-operatingMetrics-pc-110")) {
            OlapChartDimension dimension = new OlapChartDimension();
            dimension.setName(StrUtil.format("if(dateDiff('{}',register_date,`date`) = 0, '新用户', '老用户')", queryChartParameterVO.getDimension().get(0).getGroup()));
            dimension.setAliasName("用户");
            dimension.setGroupDataOrderContentList(Arrays.asList("新用户", "老用户"));
            queryChartParameterVO.getDimension().add(dimension);
        }
        if(StrUtil.equals(queryChartParameterVO.getId(), "sdk-userAnalysis-activeUser-10")) {
            Template template = TemplateHelper.getTemplateEngine().getTemplate("sdk-userAnalysis-activeUser-10.sql");
            String view = template.render(Dict.create());
            queryChartParameterVO.setViewSql(view);
        }
        if(StrUtil.equals(queryChartParameterVO.getId(), "sdk-userAnalysis-activeUser-20")) {
            Template template = TemplateHelper.getTemplateEngine().getTemplate("sdk-userAnalysis-activeUser-20.sql");
            String view = template.render(Dict.create());
            queryChartParameterVO.setViewSql(view);
        }
        if(StrUtil.equals(queryChartParameterVO.getId(), "realTimeData-keyMetrics-all-130")) {
            String group = queryChartParameterVO.getDimension().get(0).getGroup();
            if(TimeUnitConstant.HOUR.equals(group)) {
                for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
                    if ("date".equals(conditionVO.getName())) {
                        if (FilterLogicConstant.BETWEEN.equals(conditionVO.getLogic()) && TimeUnitConstant.DAY.equals(conditionVO.getFunc()) && JSONUtil.isJson(conditionVO.getValue())) {
                            List<String> stringList = JacksonUtil.fromJSONArray(conditionVO.getValue(), String.class);
                            String startDate = stringList.get(0);
                            String endDate = stringList.get(1);
                            if(StrUtil.equals(startDate, endDate)) {
                                Template template = TemplateHelper.getTemplateEngine().getTemplate("realTimeData-keyMetrics-all-130.sql");
                                String view = template.render(Dict.create().set("timeline", startDate));
                                queryChartParameterVO.setViewSql(view);
                            }
                        }
                    }
                }
            }
        }

        if(StrUtil.startWith(queryChartParameterVO.getId(), "sdk-userAnalysis-activeUser") || StrUtil.startWith(queryChartParameterVO.getId(), "sdk-userAnalysis-userRetain")
                || StrUtil.equalsAny(queryChartParameterVO.getId(), "channelDetail-20", "channelDetail-30", "channelDetail-40", "channelDetail-50", "channelDetail-60")
                || StrUtil.equalsAny(queryChartParameterVO.getId(), "realTimeData-keyMetrics-all-50", "realTimeData-keyMetrics-all-60", "realTimeData-keyMetrics-all-70", "realTimeData-keyMetrics-all-80", "realTimeData-keyMetrics-all-90", "realTimeData-operatingMetrics-all-80", "realTimeData-operatingMetrics-all-90", "realTimeData-operatingMetrics-all-100")) {
            QueryChartParameterVO.ConditionVO aggDimConditionVO = null;
            String agentNameValue = "";
            String channelNameValue = "";
            String cidNameValue = "";
            String billingNameValue = "";
            String osTypeValue = "";

            for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
                if(StrUtil.equals(conditionVO.getName(), "agg_dim")) {
                    aggDimConditionVO = conditionVO;
                }
                if(StrUtil.equals(conditionVO.getName(), "agent_name")) {
                    agentNameValue = conditionVO.getValue();
                }
                if(StrUtil.equals(conditionVO.getName(), "channel_name")) {
                    channelNameValue = conditionVO.getValue();
                }
                if(StrUtil.equals(conditionVO.getName(), "cid_name")) {
                    cidNameValue = conditionVO.getValue();
                }
                if(StrUtil.equals(conditionVO.getName(), "billing_name")) {
                    billingNameValue = conditionVO.getValue();
                }
                if(StrUtil.equals(conditionVO.getName(), "os_type")) {
                    osTypeValue = conditionVO.getValue();
                }
            }
            if(aggDimConditionVO == null) {
                for (QueryChartParameterVO.ConditionVO conditionVO : Optional.ofNullable(queryChartParameterVO.getScreen()).orElse(Collections.emptyList())) {
                    if(StrUtil.equals(conditionVO.getName(), "agg_dim")) {
                        aggDimConditionVO = conditionVO;
                    }
                }
            }
            if(aggDimConditionVO == null) {
                aggDimConditionVO = new QueryChartParameterVO.ConditionVO();
                aggDimConditionVO.setName("agg_dim");
                queryChartParameterVO.getDashboard().add(aggDimConditionVO);
            }

            aggDimConditionVO.setLogic(FilterLogicConstant.EQ);
            String emptyConditionWithInLogic = "[]";
            if(StrUtil.isNotEmpty(billingNameValue) && !emptyConditionWithInLogic.equals(billingNameValue)) {
                aggDimConditionVO.setValue("pid:ag:ch:cn:bl");
            } else if(StrUtil.isNotEmpty(cidNameValue) && !emptyConditionWithInLogic.equals(cidNameValue)) {
                aggDimConditionVO.setValue("pid:ag:ch:cn");
            } else if(StrUtil.isNotEmpty(channelNameValue) && !emptyConditionWithInLogic.equals(channelNameValue)) {
                aggDimConditionVO.setValue("pid:ag:ch");
            } else if(StrUtil.isNotEmpty(agentNameValue) && !emptyConditionWithInLogic.equals(agentNameValue)) {
                aggDimConditionVO.setValue("pid:ag");
            } else {
                aggDimConditionVO.setValue("pid");
            }

            if(StrUtil.startWith(queryChartParameterVO.getId(), "sdk-userAnalysis-userRetain")) {
                if(StrUtil.isNotEmpty(osTypeValue)) {
                    aggDimConditionVO.setValue(aggDimConditionVO.getValue() + ":os");
                }
            }

            if(StrUtil.equals(queryChartParameterVO.getId(), "sdk-userAnalysis-activeUser-40")) {
                aggDimConditionVO.setValue(aggDimConditionVO.getValue() + ":rd");
            }

            if(StrUtil.equals(queryChartParameterVO.getId(), "sdk-userAnalysis-activeUser-50")) {
                aggDimConditionVO.setValue(aggDimConditionVO.getValue() + ":hd");
            }
        }

        if(StrUtil.equalsAny(queryChartParameterVO.getId(), "realTimeData-keyMetrics-all-60", "realTimeData-operatingMetrics-all-80", "realTimeData-operatingMetrics-all-90", "realTimeData-operatingMetrics-all-100")) {
            Date date = new Date();
            int hour = DateUtil.hour(date, true);
            int minute = DateUtil.minute(date);
            if(hour == 0 && minute < 20) {
                for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
                    if ("date".equals(conditionVO.getName())) {
                        if (FilterLogicConstant.BETWEEN.equals(conditionVO.getLogic()) && TimeUnitConstant.DAY.equals(conditionVO.getFunc()) && JSONUtil.isJson(conditionVO.getValue())) {
                            List<String> stringList = JacksonUtil.fromJSONArray(conditionVO.getValue(), String.class);
                            String startDateStr = stringList.get(0);
                            String endDateStr = stringList.get(1);
                            DateTime endDate = DateUtil.parseDate(endDateStr);
                            if(DateUtil.isSameDay(date, endDate)) {
                                endDateStr = DateUtil.formatDate(DateUtil.offsetDay(endDate, -1));
                                conditionVO.setValue(StrUtil.format("[null, \"{}\"]", endDateStr));
                            }
                        }
                    }
                }
            }
        }

        if(StrUtil.equalsAny(queryChartParameterVO.getId(), "channelAnalysis-40", "channelAnalysis-50")) {
            QueryChartParameterVO.ConditionVO aggDimConditionVO = null;
            String agentNameValue = "";
            String channelNameValue = "";
            String cidNameValue = "";
            String billingNameValue = "";

            for (OlapChartDimension chartDimension : queryChartParameterVO.getDimension()) {
                if(StrUtil.equals(chartDimension.getName(), "agent_name")) {
                    agentNameValue = chartDimension.getName();
                }
                if(StrUtil.equals(chartDimension.getName(), "channel_name")) {
                    channelNameValue = chartDimension.getName();
                }
                if(StrUtil.equals(chartDimension.getName(), "cid_name")) {
                    cidNameValue = chartDimension.getName();
                }
                if(StrUtil.equals(chartDimension.getName(), "billing_name")) {
                    billingNameValue = chartDimension.getName();
                }
            }

            for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
                if(StrUtil.equals(conditionVO.getName(), "agg_dim")) {
                    aggDimConditionVO = conditionVO;
                }
            }
            if(aggDimConditionVO == null) {
                for (QueryChartParameterVO.ConditionVO conditionVO : Optional.ofNullable(queryChartParameterVO.getScreen()).orElse(Collections.emptyList())) {
                    if(StrUtil.equals(conditionVO.getName(), "agg_dim")) {
                        aggDimConditionVO = conditionVO;
                    }
                }
            }
            if(aggDimConditionVO == null) {
                aggDimConditionVO = new QueryChartParameterVO.ConditionVO();
                aggDimConditionVO.setName("agg_dim");
                queryChartParameterVO.getDashboard().add(aggDimConditionVO);
            }
            aggDimConditionVO.setLogic(FilterLogicConstant.EQ);

            if(StrUtil.isNotEmpty(billingNameValue)) {
                aggDimConditionVO.setValue("pid:ag:ch:cn:bl");
            } else if(StrUtil.isNotEmpty(cidNameValue)) {
                aggDimConditionVO.setValue("pid:ag:ch:cn");
            } else if(StrUtil.isNotEmpty(channelNameValue)) {
                aggDimConditionVO.setValue("pid:ag:ch");
            } else if(StrUtil.isNotEmpty(agentNameValue)) {
                aggDimConditionVO.setValue("pid:ag");
            } else {
                aggDimConditionVO.setValue("pid");
            }
        }

        if(StrUtil.equalsAny(queryChartParameterVO.getId(), "realTimeData-keyMetrics-all-100", "realTimeData-keyMetrics-all-120", "realTimeData-keyMetrics-all-140")) {
            // TODO https://github.com/ClickHouse/ClickHouse/issues/10299#event-3513544809
            OlapChartDimension olapChartDimension = queryChartParameterVO.getDimension().get(0);
            if(TimeUnitConstant.DAY.equals(olapChartDimension.getGroup()) && FilterLogicConstant.NEQ.equals(olapChartDimension.getHavingLogic())) {
                olapChartDimension.setHavingValue("1970-01-01");
            }

            queryChartParameterVO.getDimension().get(1)
                    .setGroupDataOrderContentList(CollectionUtil.newArrayList("Web","iOS","Android","H5","微信小程序","PC","微信小游戏","TV","other","总计"));
        }

        if(StrUtil.equals(queryChartParameterVO.getId(), "channelAnalysis-20")) {
            queryChartParameterVO.getMeasure().forEach(x -> x.setMinValueNotEqual(null));
            queryChartParameterVO.setHavingSql("(measure0 + measure1) > 0");
        }

        if(StrUtil.equalsAny(queryChartParameterVO.getId(), "sdk-userAnalysis-userRetain-active-40", "sdk-userAnalysis-userRetain-pay-90", "channelDetail-50", "channelDetail-60")) {
            if(StrUtil.equals("retain-line-change", queryChartParameterVO.getChartType())) {
                queryChartParameterVO.getDimension().get(1).setGroupDataOrderContentList(CollectionUtil.newArrayList(queryChartParameterVO.getRetainTimeNum().split(",")));
            }
        }
    }
}
