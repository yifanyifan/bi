package com.stnts.tc.service;

import java.util.Map;

import com.stnts.tc.query.EyGlobalBarNewQuery;
import com.stnts.tc.query.EyGlobalBarReQuery;

/**
 * @author liang.zhang
 * @date 2019年11月28日
 * @desc TODO
 * 易游-全局-网吧
 */
public interface EyGlobalBarService {

	/**
	 * 新增网吧
	 * @param eyGlobalBarNewQuery
	 * @return
	 */
	public Map<String, Object> eyGlobalBarNew(EyGlobalBarNewQuery eyGlobalBarNewQuery); 
	
	/**
	 * 网吧留存数据
	 * @param eyGlobalBarReQuery
	 * @return
	 */
	public Map<String, Object> eyGlobalBarRetention(EyGlobalBarReQuery eyGlobalBarReQuery); 
}
