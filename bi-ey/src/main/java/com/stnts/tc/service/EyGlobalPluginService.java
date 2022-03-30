package com.stnts.tc.service;

import java.util.Map;

import com.stnts.tc.query.EyGlobalPluginAnalysisQuery;
import com.stnts.tc.query.EyGlobalPluginProfileQuery;

/**
 * @author liang.zhang
 * @date 2019年11月28日
 * @desc TODO
 * 易游-全局-插件
 */
public interface EyGlobalPluginService {

	public Map<String, Object> eyGlobalPluginProfile(EyGlobalPluginProfileQuery query);

	public Map<String, Object> eyGlobalPluginAnalysis(EyGlobalPluginAnalysisQuery query);

}
