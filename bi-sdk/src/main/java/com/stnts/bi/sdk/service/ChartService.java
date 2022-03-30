package com.stnts.bi.sdk.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.stnts.bi.sdk.constant.EnvironmentProperties;
import com.stnts.bi.sdk.exception.ValidationException;
import com.stnts.bi.sdk.util.TemplateHelper;
import com.stnts.bi.sdk.vo.QueryChartResultForTextLineRollupVO;
import com.stnts.bi.sql.constant.ChartTypeConstant;
import com.stnts.bi.sql.constant.FilterLogicConstant;
import com.stnts.bi.sql.constant.TimeUnitConstant;
import com.stnts.bi.sql.entity.OlapChartDimension;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.util.JacksonUtil;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 刘天元
 */
@Service
@Slf4j
public class ChartService {

    /**
     * 卡片带上卷
     */
    public static final String TEXT_LINE_ROLLUP = "text-line-rollup";

    private final QueryChartService queryChartService;

    private final EnvironmentProperties environmentProperties;

    private final BeforeQueryChartService beforeQueryChartService;

    private final AfterQueryChartService afterQueryChartService;

    public ChartService(QueryChartService queryChartService, EnvironmentProperties environmentProperties, BeforeQueryChartService beforeQueryChartService, AfterQueryChartService afterQueryChartService) {
        this.queryChartService = queryChartService;
        this.environmentProperties = environmentProperties;
        this.beforeQueryChartService = beforeQueryChartService;
        this.afterQueryChartService = afterQueryChartService;
    }

    public QueryChartResultVO getChart(QueryChartParameterVO queryChartParameterVO) {
        queryChartParameterVO.setDatabaseName("banyan_bi_sdk");

        if(StrUtil.equalsAny(queryChartParameterVO.getTableName(),"view_register_info", "view_payment_info_register_info", "sdk_error")) {
            queryChartParameterVO.setTableAppendFinal(true);
        } else if(StrUtil.equals(queryChartParameterVO.getTableName(),"view_sdk_app_sdk_web")) {
            queryChartParameterVO.setViewSql("(select * from banyan_bi_sdk.view_sdk_app_sdk_web where timeline < today() UNION all select * from banyan_bi_sdk.view_sdk_app_sdk_web final where timeline = today())");
        } else if(StrUtil.equals(queryChartParameterVO.getTableName(),"view_sdk_app_sdk_web_page")) {
            queryChartParameterVO.setTableName("view_sdk_app_sdk_web_session");
        } else if(StrUtil.equals(queryChartParameterVO.getTableName(),"view_sdk_app_sdk_web_register_info")) {
            queryChartParameterVO.setViewSql("(select * from banyan_bi_sdk.view_sdk_app_sdk_web_register_info where timeline < today() UNION all select * from banyan_bi_sdk.view_sdk_app_sdk_web_register_info final where timeline = today())");
        } else if(StrUtil.equals(queryChartParameterVO.getTableName(),"view_acc_reg_pay")) {
            Template template = TemplateHelper.getTemplateEngine().getTemplate("view_acc_reg_pay_include_today.sql");
            String view = template.render(Dict.create());
            queryChartParameterVO.setViewSql(view);
        }

        if(StrUtil.equals(queryChartParameterVO.getTableName(), "product")) {
            if(StrUtil.equals(CollectionUtil.get(queryChartParameterVO.getMeasure(), 0).getName(), "sdk_product_name") &&
                    StrUtil.equals(CollectionUtil.get(queryChartParameterVO.getMeasure(), 1).getName(), "product_id") &&
                    StrUtil.equals(CollectionUtil.get(queryChartParameterVO.getMeasure(), 2).getName(), "product_name")) {
                // 产品线直接从梧桐树接口获取
                return queryProduct();
            }
        }

        if(CollectionUtil.isNotEmpty(queryChartParameterVO.getDashboard())) {
            for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
                if(StrUtil.equalsAny(conditionVO.getName(), "product", "product_id")) {
                    String productValue = conditionVO.getValue();
                    if(StrUtil.isEmpty(productValue)) {
                        throw new ValidationException("请选择产品线");
                    }
                    break;
                }
            }
        }

        if (TEXT_LINE_ROLLUP.equals(queryChartParameterVO.getChartType())) {
            return getChartForTextLineRollup(queryChartParameterVO);
        }

        beforeQueryChartService.beforeQuery(queryChartParameterVO);
        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
        afterQueryChartService.afterQuery(queryChartParameterVO, queryChartResultVO);
        return queryChartResultVO;
    }

    public QueryChartResultVO getChartForTextLineRollup(QueryChartParameterVO queryChartParameterVO) {
        if(StrUtil.equalsAny(queryChartParameterVO.getId(), "realTimeData-operatingMetrics-all-60", "realTimeData-operatingMetrics-all-70")) {
            OlapChartDimension olapChartDimension = queryChartParameterVO.getDimension().get(0);
            if(StrUtil.equals(olapChartDimension.getName(), "date")) {
                olapChartDimension.setName("timeline");
            }
            for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
                if(StrUtil.equals(conditionVO.getName(), "date")) {
                    conditionVO.setName("timeline");
                }
            }
        }

        // 当前周期不完整不能和上一个完整周期对比。
        for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
            if ("date".equals(conditionVO.getName())) {
                if (FilterLogicConstant.BETWEEN.equals(conditionVO.getLogic()) && TimeUnitConstant.DAY.equals(conditionVO.getFunc()) && JSONUtil.isJson(conditionVO.getValue())) {
                    List<String> stringList = JacksonUtil.fromJSONArray(conditionVO.getValue(), String.class);
                    String startDate = stringList.get(0);
                    String endDate = stringList.get(1);
                    String group = queryChartParameterVO.getDimension().get(0).getGroup();
                    String today = DateUtil.today();
                    Date date = new Date();
                    if(TimeUnitConstant.DAY.equals(group)) {
                        if(today.equals(startDate) && today.equals(endDate)) {
                            queryChartParameterVO.setShowCurrentGroup(true);
                        }
                    } else if(TimeUnitConstant.WEEK.equals(group)) {
                        if(DateUtil.formatDate(DateUtil.beginOfWeek(date)).equals(startDate) && DateUtil.formatDate(DateUtil.endOfWeek(date)).equals(endDate)) {
                            queryChartParameterVO.setShowCurrentGroup(true);
                        }
                    } else if(TimeUnitConstant.MONTH.equals(group)) {
                        if(DateUtil.formatDate(DateUtil.beginOfMonth(date)).equals(startDate) && DateUtil.formatDate(DateUtil.endOfMonth(date)).equals(endDate)) {
                            queryChartParameterVO.setShowCurrentGroup(true);
                        }
                    }
                }
                break;
            }
        }

        // 按天只查一天的数据定制优化
        if(TimeUnitConstant.DAY.equals(queryChartParameterVO.getDimension().get(0).getGroup())) {
            for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
                if ("date".equals(conditionVO.getName())) {
                    if(FilterLogicConstant.BETWEEN.equals(conditionVO.getLogic()) && TimeUnitConstant.DAY.equals(conditionVO.getFunc()) && JSONUtil.isJson(conditionVO.getValue())) {
                        List<String> stringList = JacksonUtil.fromJSONArray(conditionVO.getValue(), String.class);
                        String startDate = stringList.get(0);
                        String endDate = stringList.get(1);
                        if(StrUtil.equals(startDate, endDate)) {
                            startDate = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parse(endDate), -29));
                            String dateConditionValue = StrUtil.format("[\"{}\",\"{}\"]", startDate, endDate);
                            conditionVO.setValue(dateConditionValue);
                            queryChartParameterVO.setRollup(false);
                            queryChartParameterVO.setChartType(ChartTypeConstant.TEXT);
                            queryChartParameterVO.getDimension().get(0).setOrder(-1);
                            QueryChartResultForTextLineRollupVO queryChartResultForOneDay = new QueryChartResultForTextLineRollupVO();
                            queryChartService.queryChart(queryChartParameterVO, queryChartResultForOneDay);
                            if(CollectionUtil.isNotEmpty(queryChartResultForOneDay.getDatas())) {
                                QueryChartResultVO.DimensionData dimensionData = (QueryChartResultVO.DimensionData) queryChartResultForOneDay.getDatas().get(0);
                                QueryChartResultVO.MeasureData measureData1 = (QueryChartResultVO.MeasureData) queryChartResultForOneDay.getDatas().get(1);
                                QueryChartResultVO.MeasureData measureData2 = (QueryChartResultVO.MeasureData) queryChartResultForOneDay.getDatas().get(2);
                                QueryChartResultVO.MeasureData measureData3 = (QueryChartResultVO.MeasureData) queryChartResultForOneDay.getDatas().get(3);
                                QueryChartResultForTextLineRollupVO.TextLineRollupData textLineRollupData = new QueryChartResultForTextLineRollupVO.TextLineRollupData();
                                if(dimensionData.getData().get(0).equals(endDate)) {
                                    textLineRollupData.setTextValue(measureData1.getData().get(0));
                                    textLineRollupData.setYoyRateValue(measureData2.getData().get(0));
                                    textLineRollupData.setMomRateValue(measureData3.getData().get(0));
                                }
                                queryChartResultForOneDay.setTextLineRollupData(textLineRollupData);
                                if(!BooleanUtil.isTrue(queryChartParameterVO.getShowCurrentGroup())) {
                                    queryChartResultForOneDay.getDatas().remove(3);
                                    queryChartResultForOneDay.getDatas().remove(2);
                                    CollectionUtil.reverse(dimensionData.getData());
                                    CollectionUtil.reverse(measureData1.getData());
                                } else {
                                    queryChartParameterVO.setChartType(ChartTypeConstant.LINE);
                                    queryChartParameterVO.setShowCurrentGroup(false);
                                    queryChartParameterVO.getDimension().get(0).setOrder(null);
                                    queryChartParameterVO.getMeasure().get(0).setContrast("");
                                    queryChartResultForOneDay.setDatas(queryChartService.queryChart(queryChartParameterVO).getDatas());
                                }
                            }
                            return queryChartResultForOneDay;
                        }
                    }
                }
            }
        }

        if(StrUtil.equalsAny(queryChartParameterVO.getId(), "realTimeData-keyMetrics-all-130", "realTimeData-keyMetrics-all-40")) {
            queryChartParameterVO.setRollup(false);
        }
        QueryChartResultVO queryChartResultTemp = queryChartService.queryChart(queryChartParameterVO);
        QueryChartResultForTextLineRollupVO.TextLineRollupData textLineRollupData = new QueryChartResultForTextLineRollupVO.TextLineRollupData();
        if(CollectionUtil.isNotEmpty(queryChartResultTemp.getDatas())) {
            QueryChartResultVO.DimensionData dimensionData = (QueryChartResultVO.DimensionData) queryChartResultTemp.getDatas().get(0);
            QueryChartResultVO.MeasureData measureData1 = (QueryChartResultVO.MeasureData) queryChartResultTemp.getDatas().get(1);
            QueryChartResultVO.MeasureData measureData2 = (QueryChartResultVO.MeasureData) queryChartResultTemp.getDatas().get(2);
            QueryChartResultVO.MeasureData measureData3 = (QueryChartResultVO.MeasureData) queryChartResultTemp.getDatas().get(3);
            if(StrUtil.equalsAny(queryChartParameterVO.getId(), "realTimeData-keyMetrics-all-130", "realTimeData-keyMetrics-all-40")) {
                // 次日留存率取日均值
                double average = measureData1.getData().stream().filter(NumberUtil::isNumber).mapToDouble(Double::parseDouble).average().orElse(0);
                textLineRollupData.setTextValue(StrUtil.toString(average));
            } else {
                textLineRollupData.setTextValue(measureData1.getTotalData());
            }
            if(ObjectUtil.equal(dimensionData.getData().size(), 1)) {
                textLineRollupData.setYoyRateValue(measureData2.getData().get(0));
                textLineRollupData.setMomRateValue(measureData3.getData().get(0));
            } else {
                textLineRollupData.setMomRateValue("-");
                textLineRollupData.setYoyRateValue("-");
            }
        }
        for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
            if("date".equals(conditionVO.getName()) || "timeline".equals(conditionVO.getName())) {
                String endDate = DateUtil.today();
                if(FilterLogicConstant.BETWEEN.equals(conditionVO.getLogic()) && JSONUtil.isJson(conditionVO.getValue())) {
                    List<String> stringList = JacksonUtil.fromJSONArray(conditionVO.getValue(), String.class);
                    endDate = stringList.get(1);
                }
                String startDate = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parse(endDate), -29));
                if(TimeUnitConstant.WEEK.equals(conditionVO.getFunc())) {
                    startDate = DateUtil.formatDate(DateUtil.offsetWeek(new Date(), -29));
                } else if(TimeUnitConstant.MONTH.equals(conditionVO.getFunc())) {
                    startDate = DateUtil.formatDate(DateUtil.offsetMonth(new Date(), -29));
                }
                conditionVO.setValue(StrUtil.format("[\"{}\",\"{}\"]", startDate, endDate));
                conditionVO.setFunc(TimeUnitConstant.DAY);
                conditionVO.setLogic(FilterLogicConstant.BETWEEN);
            }
        }
        queryChartParameterVO.setRollup(false);
        queryChartParameterVO.setShowCurrentGroup(false);
        queryChartParameterVO.getDimension().get(0).setGroup(TimeUnitConstant.DAY);
        queryChartParameterVO.getMeasure().get(0).setContrast("");
        QueryChartResultForTextLineRollupVO queryChartResultForTextLineRollupVO = new QueryChartResultForTextLineRollupVO();
        queryChartService.queryChart(queryChartParameterVO, queryChartResultForTextLineRollupVO);
        queryChartResultForTextLineRollupVO.setTextLineRollupData(textLineRollupData);
        return queryChartResultForTextLineRollupVO;
    }

    public QueryChartResultVO queryProduct() {
        QueryChartResultVO queryChartResultVO = new QueryChartResultVO();
        String url = StrUtil.format("{}/wsgi/api/sdkapp/", environmentProperties.getWutongInterfaceAddress());
        String secret = environmentProperties.getWutongInterfaceSecret();
        long timestamp = System.currentTimeMillis()/1000;
        TreeMap<String, Object> params = MapUtil.newTreeMap(Comparator.comparing(String::toString));
        params.put("time", timestamp);
        String sign = SecureUtil.md5(StrUtil.format("{}{}",timestamp, secret)).toLowerCase();
        params.put("sign", sign);
        log.info("url:" + url);
        String result = HttpUtil.get(url, params);
        log.info("result:" + result);
        JSONObject json = JSONUtil.parseObj(result);
        String status = json.getStr("status");
        if("success".equals(status)) {
            JSONObject data = json.getJSONObject("data");
            JSONArray datas = data.getJSONArray("datas");
            datas.sort(Comparator.comparing(v -> ((JSONObject)v).getStr("classification"))
                    .thenComparing(v -> ((JSONObject)v).getStr("id"))
                    .thenComparing(v -> ((JSONObject)v).getStr("name")));
            List<String> idList = new ArrayList<>();
            List<String> nameList = new ArrayList<>();
            List<String> classificationList = new ArrayList<>();
            for (Object productObj : datas) {
                JSONObject product = (JSONObject) productObj;
                Boolean isActive = product.getBool("is_active");
                if(BooleanUtil.isTrue(isActive)) {
                    String id = product.getStr("id");
                    String name = product.getStr("name");
                    String classification = product.getStr("classification");
                    idList.add(id);
                    nameList.add(name);
                    classificationList.add(classification);
                }
            }
            List<Object> resultDatas = new ArrayList<>();
            QueryChartResultVO.MeasureData measureData1 = new QueryChartResultVO.MeasureData();
            measureData1.setData(classificationList);
            resultDatas.add(measureData1);
            QueryChartResultVO.MeasureData measureData2 = new QueryChartResultVO.MeasureData();
            measureData2.setData(idList);
            resultDatas.add(measureData2);
            QueryChartResultVO.MeasureData measureData3 = new QueryChartResultVO.MeasureData();
            measureData3.setData(nameList);
            resultDatas.add(measureData3);
            queryChartResultVO.setDatas(resultDatas);
        }
        return queryChartResultVO;
    }
}
