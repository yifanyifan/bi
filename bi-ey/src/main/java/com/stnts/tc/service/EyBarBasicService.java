package com.stnts.tc.service;

import java.util.Map;

import com.stnts.tc.query.EyBarBasicBaseQuery;
import com.stnts.tc.query.EyBarBasicChannelQuery;
import com.stnts.tc.query.EyBarBasicKpiQuery;

/**
 * @author liang.zhang
 * @date 2019年12月10日
 * @desc TODO
 * 易游-网吧-基础数据
 */
public interface EyBarBasicService {
	
	/** 基本信息 */
	public Map<String, Object> base(EyBarBasicBaseQuery query);
	
	/** 基础指标 */
	public Map<String, Object> kpi(EyBarBasicKpiQuery query);
	
	/** 通道数据 */
	public Map<String, Object> channel(EyBarBasicChannelQuery query);
}
