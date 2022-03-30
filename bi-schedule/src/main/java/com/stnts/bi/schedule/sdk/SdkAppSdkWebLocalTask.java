package com.stnts.bi.schedule.sdk;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.stnts.bi.schedule.annatation.StntsScheduleAnnatation;
import com.stnts.bi.schedule.constant.EnvironmentProperties;
import com.stnts.bi.schedule.controller.RepairDataController;
import com.stnts.bi.schedule.task.IScheduleTask;
import com.stnts.bi.schedule.util.SqlUtil;
import com.stnts.bi.schedule.util.TemplateHelper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 每分钟执行一次
 * @author 刘天元
 */
@Service
@StntsScheduleAnnatation(name = "sdk_app_sdk_web_local", cron = "0 */1 * * * ?", description = "write sdk_app_sdk_web_local、sdk_app_sdk_web_page_local、sdk_app_sdk_web_register_info_local")
@Slf4j
public class SdkAppSdkWebLocalTask implements IScheduleTask {

    private final SqlUtil sqlUtil;

    private final EnvironmentProperties environmentProperties;

    private final RepairDataController repairDataController;

    public SdkAppSdkWebLocalTask(SqlUtil sqlUtil, EnvironmentProperties environmentProperties, RepairDataController repairDataController) {
        this.sqlUtil = sqlUtil;
        this.environmentProperties = environmentProperties;
        this.repairDataController = repairDataController;
    }

    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext) {
        long startTime = System.currentTimeMillis();
        String description = jobExecutionContext.getJobDetail().getDescription();
        log.info(description + " start");
        Date fireTime = jobExecutionContext.getFireTime();
        /*if("01:00:00".equals(DateUtil.formatTime(fireTime))) {
            String yesterday = DateUtil.formatDate(DateUtil.offsetDay(fireTime, -1));
            insertViewPaymentInfoRegisterInfoLocal(yesterday);

        }*/
        log.info("分钟：{}", DateUtil.minute(fireTime));
        if(DateUtil.minute(fireTime) == 0) {
            log.info("同步渠道");
            syncChannel();
            //syncProduct();
            /*if("06:00:00".equals(DateUtil.formatTime(fireTime))) {
                repairDataController.clean(DateUtil.formatDate(fireTime), DateUtil.formatDate(fireTime), 1);
                repairDataController.clean(DateUtil.formatDate(fireTime), DateUtil.formatDate(fireTime), 4);
                repairDataController.clean(DateUtil.formatDate(fireTime), DateUtil.formatDate(fireTime), 5);
            }*/
            return;
        }
        log.info("step1--------------------------------" + (System.currentTimeMillis() - startTime));
        String thisHour = DateUtil.format(fireTime, "yyyy-MM-dd HH:00:00");
        String lastHour = DateUtil.formatDateTime(DateUtil.offsetHour(DateUtil.parseDateTime(thisHour), -1));
        String start = thisHour;
        String end = thisHour;
        if(DateUtil.minute(fireTime) < 10) {
            start = lastHour;
        }
        insertSdkAppSdkWebLocal(start, end);
        log.info("step2--------------------------------" + (System.currentTimeMillis() - startTime));
        //insertSdkAppSdkWebPageLocal(start, end);
        log.info("step3--------------------------------" + (System.currentTimeMillis() - startTime));
        //insertSdkAppSdkWebRegisterInfoLocal(start, end);
        log.info("step4--------------------------------" + (System.currentTimeMillis() - startTime));
        insertViewSdkAppSdkWebSessionLocal(start, end);
        log.info("step5--------------------------------" + (System.currentTimeMillis() - startTime));

        if(!DateUtil.isSameDay(DateUtil.parseDate(start), DateUtil.parseDate(end)) || StrUtil.equals(thisHour, DateUtil.formatDateTime(fireTime))) {
            optimizeSdkAppSdkWebLocal(start, end);
            log.info("step6--------------------------------" + (System.currentTimeMillis() - startTime));
            //optimizeSdkAppSdkWebPageLocal(start, end);
            log.info("step7--------------------------------" + (System.currentTimeMillis() - startTime));
            //optimizeSdkAppSdkWebRegisterInfoLocal(start, end);
            log.info("step8--------------------------------" + (System.currentTimeMillis() - startTime));
            optimizeSdkAppSdkWebSessionPage(start, end);
            log.info("end--------------------------------" + (System.currentTimeMillis() - startTime));
        }

    }

    public void insertSdkAppSdkWebLocal(String start, String end) {
        Template template = TemplateHelper.getSdkTemplateEngine().getTemplate("sdk_app_sdk_web_local.sql");
        Dict dict = Dict.create().set("start_time", start).set("end_time", end);
        String sql = template.render(dict);
        sqlUtil.executeSql(sql);
    }

    public void optimizeSdkAppSdkWebLocal(String start, String end) {
        optimizeTableFinal(start, end, "sdk_app_sdk_web_local");
    }

    public void insertSdkAppSdkWebPageLocal(String start, String end) {
        Template template = TemplateHelper.getSdkTemplateEngine().getTemplate("sdk_app_sdk_web_page_local.sql");
        Dict dict = Dict.create().set("start_time", start).set("end_time", end);
        String sql = template.render(dict);
        sqlUtil.executeSql(sql);
    }

    public void optimizeSdkAppSdkWebPageLocal(String start, String end) {
        optimizeTableFinal(start, end, "sdk_app_sdk_web_page_local");
    }

    public void insertSdkAppSdkWebRegisterInfoLocal(String start, String end) {
        Template template = TemplateHelper.getSdkTemplateEngine().getTemplate("sdk_app_sdk_web_register_info_local.sql");
        Dict dict = Dict.create().set("start_time", start).set("end_time", end);
        String sql = template.render(dict);
        sqlUtil.executeSql(sql);
    }

    public void optimizeSdkAppSdkWebRegisterInfoLocal(String start, String end) {
        optimizeTableFinal(start, end, "sdk_app_sdk_web_register_info_local");
    }

    public void optimizeSdkAppSdkWebSessionPage(String start, String end) {
        optimizeTableFinal(start, end, "sdk_app_sdk_web_session_page");
    }

    private void optimizeTableFinal(String start, String end, String tableName) {
        String optimizeSql = StrUtil.format("OPTIMIZE TABLE banyan_bi_sdk.{} partition '{}' FINAL", tableName, DateUtil.formatDate(DateUtil.parseDate(end)));
        sqlUtil.executeSql(optimizeSql);
        if(!DateUtil.isSameDay(DateUtil.parseDate(start), DateUtil.parseDate(end))) {
            String optimizeYesterdaySql = StrUtil.format("OPTIMIZE TABLE banyan_bi_sdk.{} partition '{}' FINAL", tableName, DateUtil.formatDate(DateUtil.parseDate(start)));
            sqlUtil.executeSql(optimizeYesterdaySql);
        }
    }

    public void insertViewPaymentInfoRegisterInfoLocal(String date) {
        Template template = TemplateHelper.getSdkTemplateEngine().getTemplate("view_payment_info_register_info_local.sql");
        Dict dict = Dict.create().set("date", date);
        String sql = template.render(dict);
        sqlUtil.executeSql(sql);
    }

    public void insertViewSdkAppSdkWebSessionLocal(String start, String end) {
        Template template = TemplateHelper.getSdkTemplateEngine().getTemplate("sdk_app_sdk_web_session_page.sql");
        Dict dict = Dict.create().set("start_time", start).set("end_time", end);
        String sql = template.render(dict);
        sqlUtil.executeSql(sql);
    }

    private void syncChannel() {
        String selectSql = "select count(*) from banyan_bi_sdk.bi_channel_maintain_transfer";
        Integer count = sqlUtil.queryForOne(selectSql);
        if(count > 0) {
            String truncateSql = "truncate table banyan_bi_sdk.bi_channel_maintain";
            sqlUtil.executeSql(truncateSql);
            String insertSql = "insert into banyan_bi_sdk.bi_channel_maintain select * from banyan_bi_sdk.bi_channel_maintain_transfer";
            sqlUtil.executeSql(insertSql);
        }
    }

    private void syncProduct() {
        String url = StrUtil.format("{}/wsgi/api/sdkapp/", environmentProperties.getWutongInterfaceAddress());
        String secret = environmentProperties.getWutongInterfaceSecret();
        long timestamp = System.currentTimeMillis()/1000;
        TreeMap<String, Object> params = MapUtil.newTreeMap(Comparator.comparing(String::toString));
        params.put("time", timestamp);
        String sign = SecureUtil.md5(StrUtil.format("{}{}",timestamp, secret)).toLowerCase();
        params.put("sign", sign);
        String result = HttpUtil.get(url, params);
        JSONObject json = JSONUtil.parseObj(result);
        String status = json.getStr("status");
        if("success".equals(status)) {
            JSONObject data = json.getJSONObject("data");
            JSONArray datas = data.getJSONArray("datas");
            List<String> values = new ArrayList<>();
            for (Object productObj : datas) {
                JSONObject product = (JSONObject) productObj;
                Integer id = product.getInt("id");
                Integer sdkproduct = product.getInt("sdkproduct");
                Boolean isActive = product.getBool("is_active");
                String business = product.getStr("business");
                String classification = product.getStr("classification");
                String name = product.getStr("name");
                if(StrUtil.isEmpty(business)) {
                    business = StrUtil.toString(id);
                }
                if(BooleanUtil.isTrue(isActive)) {
                    String value = StrUtil.format("({}, '{}', '{}', {}, '{}', 1)", id, name, business, sdkproduct, classification);
                    values.add(value);
                }
            }
            if(CollectionUtil.isNotEmpty(values)) {
                StringBuffer sqlStringBuffer = new StringBuffer("insert into banyan_bi_sdk.product (product_id, product_name, business, sdk_product_id, sdk_product_name, is_valid) values ");
                String joinValues = String.join(",", values);
                sqlStringBuffer.append(joinValues);
                String truncateSql = "truncate table banyan_bi_sdk.product";
                sqlUtil.executeSql(truncateSql);
                sqlUtil.executeSql(sqlStringBuffer.toString());
            }
        }
    }

    private void optimizeTable() {
        String register = "OPTIMIZE TABLE banyan_bi_sdk.view_register_info_local";
        sqlUtil.executeSql(register);
        String payment = "OPTIMIZE TABLE banyan_bi_sdk.view_payment_info_register_info_local";
        sqlUtil.executeSql(payment);
        String sdk = "OPTIMIZE TABLE banyan_bi_sdk.sdk_app_sdk_web_local";
        sqlUtil.executeSql(sdk);
        String sdkPage = "OPTIMIZE TABLE banyan_bi_sdk.sdk_app_sdk_web_page_local";
        sqlUtil.executeSql(sdkPage);
        String sdkRegisterPayment = "OPTIMIZE TABLE banyan_bi_sdk.sdk_app_sdk_web_register_info_local";
        sqlUtil.executeSql(sdkRegisterPayment);
    }

}
