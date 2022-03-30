package com.stnts.tc.kpi;

import java.util.List;

import lombok.Data;

/**
 * @author liang.zhang
 * @date 2019年11月20日
 * @desc TODO
 * 易游统计指标
 */
@Data
public class KpiEy {
	
	private List<String> globalBasicProfile;
	private List<String> globalBasicChannel;
	private List<String> globalBarNewly;
	
	private List<String> globalPluginProfile;
	
	private String pluginSetKey;
}
