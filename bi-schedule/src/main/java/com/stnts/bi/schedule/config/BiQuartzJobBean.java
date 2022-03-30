package com.stnts.bi.schedule.config;

import com.stnts.bi.schedule.task.IScheduleTask;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class BiQuartzJobBean extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        IScheduleTask task = (IScheduleTask)context.getMergedJobDataMap().get("jobRunner");
        task.executeInternal(context);
    }
}
