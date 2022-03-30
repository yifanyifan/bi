package com.stnts.tc.service;

import java.util.Map;

import com.stnts.tc.query.EyGlobalBasicBarQuery;
import com.stnts.tc.query.EyGlobalBasicChannelQuery;
import com.stnts.tc.query.EyGlobalBasicProfileQuery;

/**
 * @author liang.zhang
 * @date 2019年11月18日
 * @desc TODO
 * 易乐游-全局-基础数据
 */
public interface EyGlobalBasicService {
	
	/**
	 * 易乐游-全局-基础数据-数据概况
	 * @param query 
	 * @return
	 */
	public Map<String, Object> eyGlobalBasicProfile(EyGlobalBasicProfileQuery query);

	/**
	 * 易乐游-全局-基础数据-通道数据
	 * @param query
	 * @return
	 */
	public Map<String, Object> eyGlobalBasicChannel(EyGlobalBasicChannelQuery query);

	/**
	 * 易乐游-全局-基础数据-网吧评分
	 * @param query
	 * @return
	 */
	public Map<String, Object> eyGlobalBasicBar(EyGlobalBasicBarQuery query);
}
