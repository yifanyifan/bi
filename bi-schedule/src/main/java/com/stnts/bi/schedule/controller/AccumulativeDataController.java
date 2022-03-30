package com.stnts.bi.schedule.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.stnts.bi.schedule.sdk.SdkAccRegPayTask;
import com.stnts.bi.schedule.sdk.SdkAccVisitActiveTask;
import com.stnts.bi.schedule.util.SqlUtil;
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
public class AccumulativeDataController {

    private final SdkAccRegPayTask sdkAccRegPayTask;

    private final SdkAccVisitActiveTask sdkAccVisitActiveTask;

    private final SqlUtil sqlUtil;

    public AccumulativeDataController(SdkAccRegPayTask sdkAccRegPayTask, SdkAccVisitActiveTask sdkAccVisitActiveTask, SqlUtil sqlUtil) {
        this.sdkAccRegPayTask = sdkAccRegPayTask;
        this.sdkAccVisitActiveTask = sdkAccVisitActiveTask;
        this.sqlUtil = sqlUtil;
    }

    @PostMapping("/accumulative")
    public String accumulative(String startTime, String endTime, Integer table) {
        if(table == 1) {
            String truncateSql = "truncate table banyan_bi_sdk.acc_reg_pay";
            sqlUtil.executeSql(truncateSql);
        } else if(table == 2) {
            String truncateSql = "truncate table banyan_bi_sdk.acc_sdk_app_web";
            sqlUtil.executeSql(truncateSql);
        }
        insert(startTime, endTime, table);
        return "done";
    }

    private void insert(String startTime, String endTime, Integer table) {
        DateTime startDateTime = DateUtil.parseDate(startTime);
        DateTime endDateTime = DateUtil.parseDate(endTime);

        while (DateUtil.compare(startDateTime, endDateTime) <= 0) {
            if(table == 1) {
                try {
                    String dropPartitionSql = StrUtil.format("ALTER TABLE banyan_bi_sdk.acc_reg_pay DROP PARTITION '{}'", DateUtil.formatDate(endDateTime));
                    sqlUtil.executeSql(dropPartitionSql);
                    sdkAccRegPayTask.insert(endDateTime);
                } catch (Exception e) {
                    log.error("acc_reg_pay累计数据计算异常", e);
                    ThreadUtil.sleep(1000*60);
                }
            } else if(table == 2) {
                try {
                    String dropPartitionSql = StrUtil.format("ALTER TABLE banyan_bi_sdk.acc_sdk_app_web DROP PARTITION '{}'", DateUtil.formatDate(endDateTime));
                    sqlUtil.executeSql(dropPartitionSql);
                    sdkAccVisitActiveTask.insert(endDateTime);
                } catch (Exception e) {
                    log.error("acc_sdk_app_web累计数据计算异常", e);
                    ThreadUtil.sleep(1000*60);
                }
            }
            endDateTime = DateUtil.offsetDay(endDateTime, -1);
        }
    }

    @PostMapping("/accumulative/without/truncate")
    public String accumulativeWithoutTruncate(String startTime, String endTime, Integer table) {
        insert(startTime, endTime, table);
        return "done";
    }


}
