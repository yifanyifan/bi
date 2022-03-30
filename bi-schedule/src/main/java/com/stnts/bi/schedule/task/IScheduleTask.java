package com.stnts.bi.schedule.task;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public interface IScheduleTask {

    void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException;
}
