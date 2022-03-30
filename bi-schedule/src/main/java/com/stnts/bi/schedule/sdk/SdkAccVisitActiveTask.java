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
 * @author liutianyuan
 * 2020年12月18日 已废弃
 */
@Service
//@StntsScheduleAnnatation(name = "sdk_acc_visit_active",cron = "0 20 0 ? * *",description = "Sdk累计访问用户数/累计访问次数/累计活跃用户")
@Slf4j
public class SdkAccVisitActiveTask implements IScheduleTask {

    private final SqlUtil sqlUtil;

    public SdkAccVisitActiveTask(SqlUtil sqlUtil) {
        this.sqlUtil = sqlUtil;
    }

    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext)  {
        String description = jobExecutionContext.getJobDetail().getDescription();
        long startTime = System.currentTimeMillis();
        log.info("\n\n=============开始执行任务[" + description + "]===========");
        insert(DateUtil.yesterday());
        long endTime = System.currentTimeMillis();
        log.info("=============执行任务[" + description + "]结束,耗时["+(endTime - startTime)+"]ms===========\n\n");
    }

    public void insert(Date date) {
        Template template = TemplateHelper.getSdkTemplateEngine().getTemplate("acc_sdk_app_web.sql");
        String sql = template.render(Dict.create().set("date", DateUtil.formatDate(date)));
        sqlUtil.executeSql(sql);
    }

}
