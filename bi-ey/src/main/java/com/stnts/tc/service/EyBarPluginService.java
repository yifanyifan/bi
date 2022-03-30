package com.stnts.tc.service;

import java.util.Map;

import com.stnts.tc.query.EyBarPluginAnalysisQuery;
import com.stnts.tc.query.EyBarPluginProfileQuery;

/**
 * @author liang.zhang
 * @date 2019年12月11日
 * @desc TODO
 */
public interface EyBarPluginService {
	
	
	public Map<String, Object> profile(EyBarPluginProfileQuery query);
	
	public Map<String, Object> analysis(EyBarPluginAnalysisQuery query);
}
