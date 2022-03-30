package com.stnts.bi.schedule.gameop;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateEngine;
import com.stnts.bi.schedule.annatation.StntsScheduleAnnatation;
import com.stnts.bi.schedule.task.IScheduleTask;
import com.stnts.bi.schedule.util.SqlUtil;
import com.stnts.bi.schedule.util.TemplateHelper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/8
 */
@Component
@Slf4j
@StntsScheduleAnnatation(name = "gameop_dws_yesterday_agg", cron = "0 10 0 ? * *", description = "昨日明细数据合并")
public class WideDwsAggTask implements IScheduleTask {

    private final SqlUtil sqlUtil;

    public WideDwsAggTask(SqlUtil sqlUtil) {
        this.sqlUtil = sqlUtil;
    }

    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info("游戏运营昨日数据合并任务开始...");
        long ts = System.currentTimeMillis();
        TemplateEngine te = TemplateHelper.getPluginTemplateEngine();
        String resource = "gameop_wide_dw_2dws.sql";
        String date = DateUtil.formatDate(DateUtil.yesterday());
        Template template = te.getTemplate(resource);
        String sql = template.render(Dict.create().set("timeline_date", date));
        sqlUtil.executeSql(sql);
        log.info("游戏运营昨日数据合并任务结束...耗时(ms)：" + (System.currentTimeMillis() - ts));
    }
}
