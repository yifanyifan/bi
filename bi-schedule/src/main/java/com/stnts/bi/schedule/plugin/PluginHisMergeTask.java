package com.stnts.bi.schedule.plugin;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

import com.stnts.bi.schedule.annatation.StntsScheduleAnnatation;
import com.stnts.bi.schedule.task.IScheduleTask;
import com.stnts.bi.schedule.util.SqlUtil;
import com.stnts.bi.schedule.util.TemplateHelper;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.template.TemplateEngine;

@Service
@StntsScheduleAnnatation(name = "plugin_merge_his", cron = "0 10 0 ? * *", description = "商业插件历史数据合并")
public class PluginHisMergeTask extends PluginMergeTaskBase implements IScheduleTask {
	
	private final SqlUtil sqlUtil;

    public PluginHisMergeTask(SqlUtil sqlUtil) {
        this.sqlUtil = sqlUtil;
    }
	
	@Override
	public void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		TemplateEngine te = TemplateHelper.getPluginTemplateEngine();
//		LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String date = DateUtil.formatDate(DateUtil.yesterday());
//		doPlugin(te, date);
		doPlugin(sqlUtil, te, date, false);
	}

	/**
	 * @param te
	 * @param date
	 */
//	private void doPlugin(TemplateEngine te, String date) {
//		
//		Stopwatch stopwatch = Stopwatch.createStarted();
//		log.info("==>商业插件[插件]昨日数据合并任务开始，周期：{}", date);
//		for(int x = 0 ; x <= 1 ; x++) {
//			for(int y = 0 ; y <= 1; y++ ) {
//				for(int z = 0 ; z <= 1; z++) {
//					String tableName = String.format("plugin_dwb_realtime_daily_%s%s%s", x, y, z);
//					String resource = String.format("%s.sql", tableName);
//					merge(sqlUtil, date, te, resource);
//					log.info("[{}]完成合并", tableName);
//					String tableNameErr = String.format("plugin_err_dwb_realtime_daily_%s%s%s", x, y, z);
//					String resourceErr = String.format("%s.sql", tableNameErr);
//					merge(sqlUtil, date, te, resourceErr);
//					log.info("[{}]完成合并", tableNameErr);
//					//delay
//					optimize(sqlUtil, date, tableName);
//					log.info("[{}]完成Optimize", tableName);
//					optimize(sqlUtil, date, tableNameErr);
//					log.info("[{}]完成Optimize", tableNameErr);
//				}
//			}
//		}
//		log.info("==>商业插件[插件]历史数据天级合并任务结束，总耗时：{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
//	}

}
