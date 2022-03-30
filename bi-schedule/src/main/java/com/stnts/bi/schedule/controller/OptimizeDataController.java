package com.stnts.bi.schedule.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.stnts.bi.schedule.sdk.SdkOptimizeTableTask;
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
public class OptimizeDataController {

    private final SdkOptimizeTableTask sdkOptimizeTableTask;

    public OptimizeDataController(SdkOptimizeTableTask sdkOptimizeTableTask) {
        this.sdkOptimizeTableTask = sdkOptimizeTableTask;
    }

    @PostMapping("/optimize")
    public String optimize(String startTime, String endTime, Integer table) {
        DateTime startDateTime = DateUtil.parseDate(startTime);
        DateTime endDateTime = DateUtil.parseDate(endTime);

        while (DateUtil.compare(startDateTime, endDateTime) <= 0) {
            String tableName = null;
            if(table == 1) {
                tableName = "rt_ads_sdk_all_agg_d";
            } else if(table == 2) {
                tableName = "rt_ads_sdk_all_agg_h";
            } else if(table == 3) {
                tableName = "rt_ads_sdk_all_agg_min";
            } else if(table == 4) {
                tableName = "rt_dwd_sdk_payment_detail";
            }

            sdkOptimizeTableTask.optimizeTableFinal(tableName, DateUtil.formatDate(endDateTime));
            endDateTime = DateUtil.offsetDay(endDateTime, -1);
        }

        return "done";
    }
}
