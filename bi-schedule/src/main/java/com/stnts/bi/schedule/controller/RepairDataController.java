package com.stnts.bi.schedule.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import com.stnts.bi.schedule.util.SqlUtil;
import com.stnts.bi.schedule.util.TemplateHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tianyuan
 */
@RestController
@RequestMapping("/data")
@Slf4j
public class RepairDataController {

    private final SqlUtil sqlUtil;

    public RepairDataController(SqlUtil sqlUtil) {
        this.sqlUtil = sqlUtil;
    }

    @PostMapping("/repair")
    public String clean(String startTime, String endTime, Integer table) {
        DateTime startDateTime = DateUtil.parseDate(startTime);
        DateTime endDateTime = DateUtil.parseDate(endTime);

        while (DateUtil.compare(startDateTime, endDateTime) <= 0) {
            insert(endDateTime, table);
            endDateTime = DateUtil.offsetDay(endDateTime, -1);
        }
        if(table == 5) {
            try {
                sqlUtil.executeSql("OPTIMIZE TABLE banyan_bi_sdk.sdk_app_sdk_web_session_page FINAL");
            } catch (Exception e) {
                log.error("OPTIMIZE Exception", e);
            }
        }
        return "done";
    }

    private void insert(DateTime dateTime, Integer table) {
        String tableName = "";
        if(table == 1) {
            tableName = "sdk_app_sdk_web_local";
        } else if(table == 2) {
            tableName = "sdk_app_sdk_web_page_local";
        } else if(table == 3) {
            tableName = "sdk_app_sdk_web_register_info_local";
        } else if(table == 4) {
            tableName = "view_payment_info_register_info_local";
        } else if(table == 5) {
            tableName = "sdk_app_sdk_web_session_page";
        } else {
            return;
        }

        String dropPartitionSql = StrUtil.format("ALTER TABLE banyan_bi_sdk.{} DROP PARTITION '{}'", tableName, DateUtil.formatDate(dateTime));
        sqlUtil.executeSql(dropPartitionSql);

        Template template = TemplateHelper.getSdkTemplateEngine().getTemplate(StrUtil.format("{}.sql", tableName));
        Dict dict = Dict.create();
        if(table == 1 || table == 2 || table == 3 || table == 5) {
            dict.set("start_time", DateUtil.formatDateTime(DateUtil.beginOfDay(dateTime))).set("end_time", DateUtil.formatDateTime(DateUtil.endOfDay(dateTime)));
        } else if (table == 4) {
            dict.set("date", DateUtil.formatDate(dateTime));
        }
        String sql = template.render(dict);
        sqlUtil.executeSql(sql);
    }

}
