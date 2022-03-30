package com.stnts.bi.sdk.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sdk.exception.ValidationException;
import com.stnts.bi.sdk.service.ChartService;
import com.stnts.bi.sdk.util.TemplateHelper;
import com.stnts.bi.sql.bo.QueryTableColumnResultBO;
import com.stnts.bi.sql.constant.AdvancedComputingConstant;
import com.stnts.bi.sql.constant.ColumnTypeConstant;
import com.stnts.bi.sql.constant.FilterLogicConstant;
import com.stnts.bi.sql.constant.TimeUnitConstant;
import com.stnts.bi.sql.entity.OlapChartDimension;
import com.stnts.bi.sql.entity.OlapChartMeasure;
import com.stnts.bi.sql.exception.BusinessException;
import com.stnts.bi.sql.service.ExportChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * @author liutianyuan
 */
@RestController
@RequestMapping("/analyse/page/chart/pc")
@Slf4j
@Validated
public class PageChartPCController {

    private final ChartService chartService;

    private final ExportChartService exportChartService;

    private static Pattern pattern = Pattern.compile("\\b(and|exec|insert|select|drop|grant|alter|delete|update|count|chr|mid|master|truncate|char|declare|or)\\b|(\\*|;|\\+|'|%)");

    public PageChartPCController(ChartService chartService, ExportChartService exportChartService) {
        this.chartService = chartService;
        this.exportChartService = exportChartService;
    }


    public ResultEntity<QueryChartResultVO> getChart(QueryChartParameterVO queryChartParameterVO) {
        preQuery(queryChartParameterVO);

        String id = "";
        String product = "";
        String startDate = "";
        String endDate = "";

        String agentName = "";
        String channelName = "";
        String cidName = "";
        String billingName = "";
        Integer userType = null;
        Integer osType = null;
        String appVersion = "";
        String url = "";
        String pageUrl = "";
        String parentPageUrl = "";
        String title = "";
        id = queryChartParameterVO.getId();
        Iterator<QueryChartParameterVO.ConditionVO> iterator = queryChartParameterVO.getDashboard().iterator();
        while (iterator.hasNext()) {
            QueryChartParameterVO.ConditionVO conditionVO = iterator.next();
            if("os_type".equals(conditionVO.getName()) && FilterLogicConstant.IN.equals(conditionVO.getLogic())) {
                iterator.remove();
                continue;
            }

            if("product".equals(conditionVO.getName())) {
                product = conditionVO.getValue();
            } else if("date".equals(conditionVO.getName())) {
                String contrast = hasContrast(queryChartParameterVO);
                if(StrUtil.isNotEmpty(contrast)) {
                    List<String> stringList = com.stnts.bi.sql.util.JacksonUtil.fromJSONArray(conditionVO.getValue(), String.class);
                    startDate = stringList.get(0);
                    endDate = stringList.get(1);
                    String group = queryChartParameterVO.getDimension().get(0).getGroup();
                    if(StrUtil.equals(group, TimeUnitConstant.HOUR)) {
                        startDate = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parseDate(startDate), -1));
                    } else if(StrUtil.equals(group, TimeUnitConstant.DAY)) {
                        if(StrUtil.containsAny(contrast, AdvancedComputingConstant.MOM, AdvancedComputingConstant.MOM_RATE)) {
                            startDate = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parseDate(startDate), -1));
                        }
                        if(StrUtil.containsAny(contrast, AdvancedComputingConstant.YOY, AdvancedComputingConstant.YOY_RATE)) {
                            startDate = DateUtil.formatDate(DateUtil.offsetWeek(DateUtil.parseDate(startDate), -1));
                        }
                    } else {
                        startDate = null;
                        endDate = null;
                    }
                    continue;
                } else {
                    List<String> stringList = com.stnts.bi.sql.util.JacksonUtil.fromJSONArray(conditionVO.getValue(), String.class);
                    startDate = stringList.get(0);
                    endDate = stringList.get(1);
                }
            } else if("agent_name".equals(conditionVO.getName())) {
                agentName = conditionVO.getValue();
            } else if("channel_name".equals(conditionVO.getName())) {
                channelName = conditionVO.getValue();
            } else if("cid_name".equals(conditionVO.getName())) {
                cidName = conditionVO.getValue();
            } else if("billing_name".equals(conditionVO.getName())) {
                billingName = conditionVO.getValue();
            } else if("os_type".equals(conditionVO.getName())) {
                if(StrUtil.isNotEmpty(conditionVO.getValue()) && FilterLogicConstant.EQ.equals(conditionVO.getLogic())) {
                    osType = Integer.parseInt(conditionVO.getValue());
                }
            } else if("app_version".equals(conditionVO.getName())) {
                if(StrUtil.isNotEmpty(conditionVO.getValue()) && FilterLogicConstant.EQ.equals(conditionVO.getLogic())) {
                    appVersion = conditionVO.getValue();
                }
            } else if("user_type".equals(conditionVO.getName())) {
                if(StrUtil.isNotEmpty(conditionVO.getValue())) {
                    userType = Integer.parseInt(conditionVO.getValue());
                }
            } else if("page_url".equals(conditionVO.getName())) {
                if(StrUtil.isNotEmpty(conditionVO.getValue())) {
                    url = conditionVO.getValue();
                }
            } else if("page_title".equals(conditionVO.getName())) {
                if(StrUtil.isNotEmpty(conditionVO.getValue())) {
                    title = conditionVO.getValue();
                }
            }
            if(!StrUtil.startWith(id, "sdk-pageAnalysis-v2-pageDetail") && !StrUtil.startWith(id, "sdk-pageAnalysis-v2-appDetail")) {
                iterator.remove();
            }
        }

        if(StrUtil.startWith(id, "sdk-pageAnalysis-v2-pageDetail") || StrUtil.startWith(id, "sdk-pageAnalysis-v2-appDetail")) {
            pageUrl = url;
        } else if(StrUtil.equals(id, "sdk-pageAnalysis-v2-visitPage-30") || StrUtil.equals(id, "sdk-pageAnalysis-v2-app-60")) {
            parentPageUrl = url;
        }

        if(StrUtil.equals(url, "直接访问")) {
            url = null;
            QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
            conditionVO.setName("parent_page_domain");
            conditionVO.setLogic(FilterLogicConstant.EQ);
            conditionVO.setValue("直接访问");
            queryChartParameterVO.getDashboard().add(conditionVO);
        }
        if(userType != null && userType == 2) {
            userType = null;
            queryChartParameterVO.getDashboard().removeIf(v -> "user_type".equals(v.getName()));
            QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
            conditionVO.setName("is_active_user");
            conditionVO.setLogic(FilterLogicConstant.EQ);
            conditionVO.setValue("1");
            conditionVO.setOlapType(ColumnTypeConstant.INT);
            queryChartParameterVO.getDashboard().add(conditionVO);
        }

        if(CollectionUtil.isNotEmpty(queryChartParameterVO.getCompare())) {
            QueryChartParameterVO.ConditionVO conditionVO = queryChartParameterVO.getCompare().get(0);
            if(StrUtil.equals("date", conditionVO.getName())) {
                List<String> stringList = com.stnts.bi.sql.util.JacksonUtil.fromJSONArray(conditionVO.getValue(), String.class);
                String compareStartDate = stringList.get(0);
                String compareEndDate = stringList.get(1);
                if(DateUtil.compare(DateUtil.parseDate(startDate), DateUtil.parseDate(compareStartDate)) > 0) {
                    startDate = compareStartDate;
                }
                if(DateUtil.compare(DateUtil.parseDate(compareEndDate), DateUtil.parseDate(endDate)) > 0) {
                    endDate = compareEndDate;
                }
            }
        }
        return getChart(queryChartParameterVO, id,product,startDate,endDate,agentName,channelName,cidName,billingName,userType,osType,appVersion,url,pageUrl,parentPageUrl,title);
    }

    private void preQuery(QueryChartParameterVO queryChartParameterVO) {
        if(StrUtil.equalsAny(queryChartParameterVO.getId(), "sdk-pageAnalysis-v2-app-30", "sdk-pageAnalysis-v2-visitPage-0")) {
            OlapChartDimension chartDimension = queryChartParameterVO.getDimension().get(0);
            chartDimension.setOrderContentList(CollectionUtil.newArrayList("1", "2~5", "6~10", "11~20", "20+", "other"));
        } else if(StrUtil.equalsAny(queryChartParameterVO.getId(), "sdk-pageAnalysis-v2-app-40", "sdk-pageAnalysis-v2-visitPage-10")) {
            OlapChartDimension chartDimension = queryChartParameterVO.getDimension().get(0);
            chartDimension.setOrderContentList(CollectionUtil.newArrayList("[0,1]", "(1,5]", "(5,10]", "(10,20]", "(20,30]", "(30,+∞)", "other"));
        }
    }

    @GetMapping("/get")
    public ResultEntity<QueryChartResultVO> getChart(QueryChartParameterVO queryChartParameterVO, String id,String product, String startDate, String endDate,
                                 String agentName, String channelName, String cidName, String billingName, Integer userType, Integer osType, String appVersion, String url, String pageUrl, String parentPageUrl, String title) {
        if(StrUtil.isNotEmpty(startDate)) {
            DateUtil.parseDate(startDate);
        }
        if(StrUtil.isNotEmpty(endDate)) {
            DateUtil.parseDate(endDate);
        }
        validate(product, startDate, endDate, product, "product");
        validate(product, startDate, endDate, agentName, "agent_name", FilterLogicConstant.IN);
        validate(product, startDate, endDate, channelName, "channel_name", FilterLogicConstant.IN);
        validate(product, startDate, endDate, cidName, "cid_name", FilterLogicConstant.IN);
        validate(product, startDate, endDate, billingName, "billing_name", FilterLogicConstant.IN);

        validate(product, startDate, endDate, pageUrl, "page_url", FilterLogicConstant.IN);
        validate(product, startDate, endDate, parentPageUrl, "parent_page_url");
        validate(product, startDate, endDate, title, "page_title");
        validate(product, startDate, endDate, appVersion, "app_version");

        String template = "";
        if(StrUtil.startWith(id, "sdk-pageAnalysis-v2-sourcePage")) {
            validate(product, startDate, endDate,url, "parent_page_domain");
            template = "sdk-pageAnalysis-v2-sourcePage.sql";
        } else if(StrUtil.startWith(id, "sdk-pageAnalysis-v2-entrancePage")) {
            validate(product, startDate, endDate,url, "page_url");
            template = "sdk-pageAnalysis-v2-entrancePage.sql";
        } else if(StrUtil.startWith(id, "sdk-pageAnalysis-v2-quitePage")) {
            validate(product, startDate, endDate,url, "page_url");
            template = "sdk-pageAnalysis-v2-quitePage.sql";
            queryChartParameterVO.setHavingSql("sum(is_quite) > 0");
        } else if(StrUtil.startWith(id, "sdk-pageAnalysis-v2-visitPage")) {
            template = "sdk-pageAnalysis-v2-visitPageDetail.sql";
            setViewColumns(queryChartParameterVO);
        } else if(StrUtil.startWith(id, "sdk-pageAnalysis-v2-pageDetail")) {
            template = "sdk-pageAnalysis-v2-visitPageDetail.sql";
            setViewColumns(queryChartParameterVO);
        } else if(StrUtil.startWith(id, "sdk-pageAnalysis-v2-app")) {
            template = "sdk-pageAnalysis-v2-visitAppDetail.sql";
            setViewColumns(queryChartParameterVO);
        }


        if(StrUtil.equals(id, "sdk-pageAnalysis-v2-entrancePage-110")) {
            validate(product, startDate, endDate,url, "parent_page_url");
            template = "sdk-pageAnalysis-v2-entrancePage-downstream.sql";
        }
        if(StrUtil.equalsAny(id, "sdk-pageAnalysis-v2-visitPage-0", "sdk-pageAnalysis-v2-visitPage-10")) {
            template = "sdk-pageAnalysis-v2-visitPage.sql";
        }
        if(StrUtil.equalsAny(id, "sdk-pageAnalysis-v2-app-10", "sdk-pageAnalysis-v2-app-20")) {
            validate(product, startDate, endDate,url, "page_url");
            template = "sdk-pageAnalysis-v2-quiteApp.sql";
            queryChartParameterVO.setHavingSql("sum(is_quite) > 0");
        }
        if(StrUtil.equalsAny(id, "sdk-pageAnalysis-v2-app-30", "sdk-pageAnalysis-v2-app-40")) {
            template = "sdk-pageAnalysis-v2-visitApp.sql";
        }

        Template sqlTemplate = TemplateHelper.getTemplateEngine().getTemplate(template);
        Dict stringMap = Dict.create().set("product", product).set("page_title", title).set("app_version", appVersion).set("parent_page_url", parentPageUrl);
        Dict inMap = Dict.create().set("agent_name", getInStringValue(agentName)).set("channel_name", getInStringValue(channelName)).set("cid_name", getInStringValue(cidName)).set("billing_name", getInStringValue(billingName))
                .set("page_url", getInStringValue(pageUrl));
        Dict integerMap = Dict.create().set("user_type", userType).set("os_type", osType);
        String viewSql = sqlTemplate.render(Dict.create().set("startDate", startDate).set("endDate", endDate).set("url", url).set("stringMap", stringMap).set("integerMap", integerMap).set("inMap", inMap));
        queryChartParameterVO.setViewSql(viewSql);
        QueryChartResultVO queryChartResultVO = chartService.getChart(queryChartParameterVO);

        if(StrUtil.equalsAny(id, "sdk-pageAnalysis-v2-sourcePage-100", "sdk-pageAnalysis-v2-entrancePage-110", "sdk-pageAnalysis-v2-visitPage-30")) {
            queryChartResultVO.getDatas().removeIf(x -> {
                if(x instanceof QueryChartResultVO.MeasureData) {
                    QueryChartResultVO.MeasureData measureData = (QueryChartResultVO.MeasureData) x;
                    if(StrUtil.equalsAny(id, "sdk-pageAnalysis-v2-sourcePage-100")) {
                        if("pv".equals(measureData.getName()) && !BooleanUtil.isTrue(measureData.getProportion())) {
                            return true;
                        }
                    }else if(StrUtil.equalsAny(id, "sdk-pageAnalysis-v2-entrancePage-110")) {
                        if("downstream_pv".equals(measureData.getName()) && !BooleanUtil.isTrue(measureData.getProportion())) {
                            return true;
                        }
                    } else if(StrUtil.equalsAny(id, "sdk-pageAnalysis-v2-visitPage-30")) {
                        if("sum(pv)".equals(measureData.getName()) && !BooleanUtil.isTrue(measureData.getProportion())) {
                            return true;
                        }
                    } else if(StrUtil.equalsAny(id, "sdk-pageAnalysis-v2-app-60")) {
                        if("sum(pv)".equals(measureData.getName()) && !BooleanUtil.isTrue(measureData.getProportion())) {
                            return true;
                        }
                    }
                }
                return false;
            });
        }

        return ResultEntity.success(queryChartResultVO);
    }

    private void setViewColumns(QueryChartParameterVO queryChartParameterVO) {
        List<QueryTableColumnResultBO> columns = new ArrayList<>();
        QueryTableColumnResultBO queryTableColumnResultBO1 = new QueryTableColumnResultBO();
        queryTableColumnResultBO1.setColumnName("date");
        queryTableColumnResultBO1.setOlapType(ColumnTypeConstant.DATE);
        queryTableColumnResultBO1.setColumnType("DateTime");
        columns.add(queryTableColumnResultBO1);
        QueryTableColumnResultBO queryTableColumnResultBO2 = new QueryTableColumnResultBO();
        queryTableColumnResultBO2.setColumnName("product");
        queryTableColumnResultBO2.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO2);
        QueryTableColumnResultBO queryTableColumnResultBO3 = new QueryTableColumnResultBO();
        queryTableColumnResultBO3.setColumnName("agent_name");
        queryTableColumnResultBO3.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO3);
        QueryTableColumnResultBO queryTableColumnResultBO4 = new QueryTableColumnResultBO();
        queryTableColumnResultBO4.setColumnName("channel_name");
        queryTableColumnResultBO4.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO4);
        QueryTableColumnResultBO queryTableColumnResultBO5 = new QueryTableColumnResultBO();
        queryTableColumnResultBO5.setColumnName("cid_name");
        queryTableColumnResultBO5.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO5);
        QueryTableColumnResultBO queryTableColumnResultBO6 = new QueryTableColumnResultBO();
        queryTableColumnResultBO6.setColumnName("billing_name");
        queryTableColumnResultBO6.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO6);
        QueryTableColumnResultBO queryTableColumnResultBO7 = new QueryTableColumnResultBO();
        queryTableColumnResultBO7.setColumnName("os_type");
        queryTableColumnResultBO7.setOlapType(ColumnTypeConstant.INT);
        queryTableColumnResultBO7.setColumnType("UInt8");
        columns.add(queryTableColumnResultBO7);
        QueryTableColumnResultBO queryTableColumnResultBO8 = new QueryTableColumnResultBO();
        queryTableColumnResultBO8.setColumnName("user_type");
        queryTableColumnResultBO8.setOlapType(ColumnTypeConstant.INT);
        queryTableColumnResultBO8.setColumnType("UInt8");
        columns.add(queryTableColumnResultBO8);
        QueryTableColumnResultBO queryTableColumnResultBO9 = new QueryTableColumnResultBO();
        queryTableColumnResultBO9.setColumnName("app_version");
        queryTableColumnResultBO9.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO9);
        QueryTableColumnResultBO queryTableColumnResultBO10 = new QueryTableColumnResultBO();
        queryTableColumnResultBO10.setColumnName("session");
        queryTableColumnResultBO10.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO10);
        QueryTableColumnResultBO queryTableColumnResultBO11 = new QueryTableColumnResultBO();
        queryTableColumnResultBO11.setColumnName("page_sequence");
        queryTableColumnResultBO11.setOlapType(ColumnTypeConstant.INT);
        queryTableColumnResultBO11.setColumnType("Int64");
        columns.add(queryTableColumnResultBO11);
        QueryTableColumnResultBO queryTableColumnResultBO12 = new QueryTableColumnResultBO();
        queryTableColumnResultBO12.setColumnName("in_time");
        queryTableColumnResultBO12.setOlapType(ColumnTypeConstant.DATE);
        queryTableColumnResultBO12.setColumnType("DateTime");
        columns.add(queryTableColumnResultBO12);
        QueryTableColumnResultBO queryTableColumnResultBO13 = new QueryTableColumnResultBO();
        queryTableColumnResultBO13.setColumnName("page_event_type");
        queryTableColumnResultBO13.setOlapType(ColumnTypeConstant.INT);
        queryTableColumnResultBO13.setColumnType("UInt8");
        columns.add(queryTableColumnResultBO13);
        QueryTableColumnResultBO queryTableColumnResultBO14 = new QueryTableColumnResultBO();
        queryTableColumnResultBO14.setColumnName("is_active_user");
        queryTableColumnResultBO14.setOlapType(ColumnTypeConstant.INT);
        queryTableColumnResultBO14.setColumnType("UInt8");
        columns.add(queryTableColumnResultBO14);
        QueryTableColumnResultBO queryTableColumnResultBO15 = new QueryTableColumnResultBO();
        queryTableColumnResultBO15.setColumnName("page_title");
        queryTableColumnResultBO15.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO15);
        QueryTableColumnResultBO queryTableColumnResultBO16 = new QueryTableColumnResultBO();
        queryTableColumnResultBO16.setColumnName("page_url");
        queryTableColumnResultBO16.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO16);
        QueryTableColumnResultBO queryTableColumnResultBO17 = new QueryTableColumnResultBO();
        queryTableColumnResultBO17.setColumnName("parent_page_url");
        queryTableColumnResultBO17.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO17);
        QueryTableColumnResultBO queryTableColumnResultBO18 = new QueryTableColumnResultBO();
        queryTableColumnResultBO18.setColumnName("parent_page_domain");
        queryTableColumnResultBO18.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO18);
        QueryTableColumnResultBO queryTableColumnResultBO19 = new QueryTableColumnResultBO();
        queryTableColumnResultBO19.setColumnName("online_duration");
        queryTableColumnResultBO19.setOlapType(ColumnTypeConstant.INT);
        queryTableColumnResultBO19.setColumnType("UInt64");
        columns.add(queryTableColumnResultBO19);
        QueryTableColumnResultBO queryTableColumnResultBO20 = new QueryTableColumnResultBO();
        queryTableColumnResultBO20.setColumnName("pv");
        queryTableColumnResultBO20.setOlapType(ColumnTypeConstant.INT);
        queryTableColumnResultBO20.setColumnType("UInt64");
        columns.add(queryTableColumnResultBO20);
        QueryTableColumnResultBO queryTableColumnResultBO21 = new QueryTableColumnResultBO();
        queryTableColumnResultBO21.setColumnName("active_user");
        queryTableColumnResultBO21.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO21);
        QueryTableColumnResultBO queryTableColumnResultBO22 = new QueryTableColumnResultBO();
        queryTableColumnResultBO22.setColumnName("uv");
        queryTableColumnResultBO22.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO22);
        QueryTableColumnResultBO queryTableColumnResultBO23 = new QueryTableColumnResultBO();
        queryTableColumnResultBO23.setColumnName("identifier");
        queryTableColumnResultBO23.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO23);
        QueryTableColumnResultBO queryTableColumnResultBO24 = new QueryTableColumnResultBO();
        queryTableColumnResultBO24.setColumnName("ip");
        queryTableColumnResultBO24.setOlapType(ColumnTypeConstant.TEXT);
        columns.add(queryTableColumnResultBO24);
        QueryTableColumnResultBO queryTableColumnResultBO25 = new QueryTableColumnResultBO();
        queryTableColumnResultBO25.setColumnName("max_page_sequence");
        queryTableColumnResultBO25.setOlapType(ColumnTypeConstant.INT);
        queryTableColumnResultBO25.setColumnType("Int64");
        columns.add(queryTableColumnResultBO25);
        queryChartParameterVO.setViewColumns(columns);
    }

    private String getInStringValue(String strArray) {
        if(StrUtil.isEmpty(strArray)) {
            return null;
        }
        JSONArray jsonArray = JSONUtil.parseArray(strArray);
        if(CollectionUtil.isEmpty(jsonArray)) {
            return null;
        }
        String join = jsonArray.stream().map(Object::toString).map(v -> StrUtil.format("'{}'", v)).collect(Collectors.joining(","));
        return StrUtil.format("({})", join);
    }

    private void validate(String product, String startDate , String endDate, String value, String fieldName) {
        validate(product, startDate , endDate, value, fieldName, FilterLogicConstant.EQ);
    }

    private void validate(String product, String startDate , String endDate, String value, String fieldName, String logic) {
        if(StrUtil.isEmpty(value)) {
            return;
        }
        QueryChartParameterVO queryChartParameterVO = new QueryChartParameterVO();
        queryChartParameterVO.setDatabaseName("banyan_bi_sdk");
        queryChartParameterVO.setTableName("view_sdk_app_sdk_web_session");
        OlapChartMeasure measure = new OlapChartMeasure();
        measure.setName("1");
        queryChartParameterVO.setMeasure(CollectionUtil.newArrayList(measure));

        QueryChartParameterVO.ConditionVO condition1 = new QueryChartParameterVO.ConditionVO();
        condition1.setName(fieldName);
        condition1.setLogic(logic);

        condition1.setValue(value);
        QueryChartParameterVO.ConditionVO condition2 = new QueryChartParameterVO.ConditionVO();
        condition2.setName("product");
        condition2.setLogic(FilterLogicConstant.EQ);
        condition2.setValue(product);
        QueryChartParameterVO.ConditionVO condition3 = new QueryChartParameterVO.ConditionVO();
        if(StrUtil.isNotEmpty(startDate) && StrUtil.isNotEmpty(endDate)) {
            condition3.setName("timeline");
            condition3.setFunc(TimeUnitConstant.DAY);
            condition3.setLogic(FilterLogicConstant.BETWEEN);
            condition3.setValue(StrUtil.format("[\"{}\",\"{}\"]", startDate, endDate));
        }

        queryChartParameterVO.setDashboard(CollectionUtil.newArrayList(condition1, condition2, condition3));
        queryChartParameterVO.setLimit(1.0);
        QueryChartResultVO queryChartResultVO = chartService.getChart(queryChartParameterVO);
        if(CollectionUtil.isEmpty(queryChartResultVO.getDatas())) {
            throw new ValidationException(StrUtil.format("{}为{}，没有数据", fieldName, value));
        }
    }

    /*private void validate(String value, String message) {
        if(containsSqlInjection(value)) {
            throw new BusinessException(message + "参数不合法");
        }
    }*/

    /**
     * 是否含有sql注入，返回true表示含有
     * @param str
     * @return
     */
    private boolean containsSqlInjection(String str) {
        if(StrUtil.isEmpty(str)) {
            return false;
        }
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    private String hasContrast(QueryChartParameterVO queryChartParameterVO) {
        for (OlapChartMeasure olapChartMeasure : queryChartParameterVO.getMeasure()) {
            if(StrUtil.isNotEmpty(olapChartMeasure.getContrast())) {
                return olapChartMeasure.getContrast();
            }
        }
        return "";
    }

}
