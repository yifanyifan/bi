package com.stnts.bi.schedule.config;

import com.stnts.bi.schedule.annatation.StntsScheduleAnnatation;
import com.stnts.bi.schedule.task.IScheduleTask;
import com.stnts.bi.schedule.util.ApplicationContextHolder;
import org.quartz.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Map;

@Configuration
public class QuartzConfig implements ApplicationRunner {


    @Override
    public void run(ApplicationArguments args) throws Exception {

        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();

        Scheduler scheduler = applicationContext.getBean(SchedulerFactoryBean.class).getScheduler();

        Map<String, IScheduleTask> tasks = applicationContext.getBeansOfType(IScheduleTask.class);

        for(String taskName : tasks.keySet()){

            IScheduleTask task = tasks.get(taskName);

            //在方法上用自定义注解标注的定时任务会被执行
            if(task.getClass().isAnnotationPresent(StntsScheduleAnnatation.class)){

                BiQuartzJobBean biQuartzJobBean = new BiQuartzJobBean();

                StntsScheduleAnnatation annotation = task.getClass().getAnnotation(StntsScheduleAnnatation.class);
                String name = annotation.name();
                String cron = annotation.cron();
                String description = annotation.description();
                JobDetail jobDetail = JobBuilder.newJob(biQuartzJobBean.getClass()).storeDurably().withIdentity(name).withDescription(description).build();
                jobDetail.getJobDataMap().put("jobRunner",task);

                CronScheduleBuilder schedule = CronScheduleBuilder.cronSchedule(cron)
                        //如果任务没有准点执行，在下次启动的时候执行
                        .withMisfireHandlingInstructionIgnoreMisfires();

                CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(name).withSchedule(schedule).build();

                scheduler.scheduleJob(jobDetail,cronTrigger);
                scheduler.start();
            }

        }

    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {

        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setSchedulerName("Stnts-BI-Schedule");
        // 启动时延期1秒开始任务
        schedulerFactoryBean.setStartupDelay(1);
        // 可选，QuartzScheduler
        // 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        // 设置自动启动，默认为true
        schedulerFactoryBean.setAutoStartup(true);
        // schedulerFactoryBean.setJobFactory(tioadJobFactory);
        return schedulerFactoryBean;

    }

}
