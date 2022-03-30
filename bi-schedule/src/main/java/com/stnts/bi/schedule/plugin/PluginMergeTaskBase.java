package com.stnts.bi.schedule.plugin;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.stnts.bi.schedule.util.SqlUtil;

import cn.hutool.core.lang.Dict;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateEngine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PluginMergeTaskBase {
	
	public static final String OPTIMIZE_SQL_TEMPLATE = "OPTIMIZE TABLE bi_plugin.%s partition '%s' FINAL";
	
	/**
	 * 执行数据合并   按天合并后需要执行 optimize
	 * @param date
	 * @param te
	 * @param resource
	 */
	public void merge(SqlUtil sqlUtil, String date, TemplateEngine te, String resource) {
		
		Template template = te.getTemplate(resource);
		String sql = template.render(Dict.create().set("partition_date", date));
        sqlUtil.executeSql(sql);
	}
	
	/**
	 * 执行优化
	 * @param dateStr
	 * @param tableName
	 */
	public void optimize(SqlUtil sqlUtil, String dateStr, String tableName) {
		
		String sql = String.format(OPTIMIZE_SQL_TEMPLATE, tableName, dateStr);
		sqlUtil.executeSql(sql);
	}
	
	/**
	 * @param te
	 * @param date
	 */
	public void doPlugin(SqlUtil sqlUtil, TemplateEngine te, String date, boolean isToday) {
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		String title = isToday ? "今日" : "昨日";
		log.info("==>商业插件{}数据合并任务开始，周期：{}", title, date);
		for(int x = 0 ; x <= 1 ; x++) {
			for(int y = 0 ; y <= 1; y++ ) {
				for(int z = 0 ; z <= 1; z++) {
					String tableName = String.format("plugin_dwb_realtime_daily_%s%s%s", x, y, z);
					String resource = String.format("%s.sql", tableName);
					merge(sqlUtil, date, te, resource);
					log.info("[{}]完成合并", tableName);
					String tableNameErr = String.format("plugin_err_dwb_realtime_daily_%s%s%s", x, y, z);
					String resourceErr = String.format("%s.sql", tableNameErr);
					merge(sqlUtil, date, te, resourceErr);
					log.info("[{}]完成合并", tableNameErr);
				}
			}
		}
		//delay optimize
		for(int x = 0 ; x <= 1 ; x++) {
			for(int y = 0 ; y <= 1; y++ ) {
				for(int z = 0 ; z <= 1; z++) {
					String tableName = String.format("plugin_dwb_realtime_daily_%s%s%s", x, y, z);
					String tableNameErr = String.format("plugin_err_dwb_realtime_daily_%s%s%s", x, y, z);
					optimize(sqlUtil, date, tableName);
					log.info("[{}]完成Optimize", tableName);
					optimize(sqlUtil, date, tableNameErr);
					log.info("[{}]完成Optimize", tableNameErr);
				}
			}
		}
		log.info("==>商业插件{}数据合并任务结束，总耗时：{}", title, stopwatch.elapsed(TimeUnit.MILLISECONDS));
	}
}
