package com.stnts.bi.schedule.sdk;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.extra.template.Template;
import com.stnts.bi.schedule.annatation.StntsScheduleAnnatation;
import com.stnts.bi.schedule.task.IScheduleTask;
import com.stnts.bi.schedule.util.SqlUtil;
import com.stnts.bi.schedule.util.TemplateHelper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 每天凌晨10分计算前一天的累加注册、付费
 * @author 刘天元
 */
@Service
//@StntsScheduleAnnatation(name = "sdk_acc_reg_pay", cron = "0 10 0 ? * *", description = "Sdk累计注册/付费用户数")
@Slf4j
public class SdkAccRegPayTask implements IScheduleTask {

    private final SqlUtil sqlUtil;

    public SdkAccRegPayTask(SqlUtil sqlUtil) {
        this.sqlUtil = sqlUtil;
    }

    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext) {
        long startTime = System.currentTimeMillis();
        String description = jobExecutionContext.getJobDetail().getDescription();
        log.info("\n\n=============开始执行任务[" + description + "]===========");
        insert(DateUtil.yesterday());
        long endTime = System.currentTimeMillis();
        log.info("=============执行任务[" + description + "]结束,耗时["+(endTime - startTime)+"]ms===========\n\n");
    }

    public void insert(Date date) {
        Template template = TemplateHelper.getSdkTemplateEngine().getTemplate("acc_reg_pay.sql");
        String sql = template.render(Dict.create().set("date", DateUtil.formatDate(date)));
        sqlUtil.executeSql(sql);
    }


}
