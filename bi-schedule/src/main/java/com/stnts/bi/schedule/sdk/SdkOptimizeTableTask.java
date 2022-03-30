package com.stnts.bi.schedule.sdk;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.stnts.bi.schedule.annatation.StntsScheduleAnnatation;
import com.stnts.bi.schedule.config.JdbcTemplateFactoryTwo;
import com.stnts.bi.schedule.task.IScheduleTask;
import com.stnts.bi.schedule.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 每天凌晨三点十分执行
 * @author 刘天元
 */
@Service
@StntsScheduleAnnatation(name = "SdkOptimizeTableTask", cron = "10 10 3 ? * *", description = "SdkOptimizeTableTask")
@Slf4j
public class SdkOptimizeTableTask implements IScheduleTask {

    private final static String jdbcUrl = "jdbc:clickhouse://clickhouse-group02.stnts.com:8123";
        private final static String username = "bi_sdk";
    private final static String password = "KVv6$D!or@RKmTy!";


    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext) {
        String description = jobExecutionContext.getJobDetail().getDescription();
        log.info(description + " start");
        Date fireTime = jobExecutionContext.getFireTime();
        optimizeTableFinal("rt_ads_sdk_all_agg_d", DateUtil.formatDate(DateUtil.yesterday()));
        optimizeTableFinal("rt_ads_sdk_all_agg_h", DateUtil.formatDate(DateUtil.yesterday()));
        optimizeTableFinal("rt_ads_sdk_all_agg_min", DateUtil.formatDate(DateUtil.yesterday()));
        optimizeTableFinal("rt_dwd_sdk_payment_detail", DateUtil.formatDate(DateUtil.yesterday()));
        log.info(description + " end");
    }

    public void optimizeTableFinal(String tableName, String partitionExpr) {
        String optimizeSql = StrUtil.format("OPTIMIZE TABLE bi_sdk.{} partition '{}' FINAL", tableName, partitionExpr);
        log.info("optimizeSql:{}", optimizeSql);
        JdbcTemplateFactoryTwo.getJdbcTemplateCommon(null, jdbcUrl, username, password).execute(optimizeSql);
    }


}
