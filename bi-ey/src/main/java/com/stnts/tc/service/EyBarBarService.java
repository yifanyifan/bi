package com.stnts.tc.service;

import java.util.Map;

import com.stnts.tc.query.EyBarBarAuditQuery;
import com.stnts.tc.query.EyBarBarChannelQuery;

/**
 * @author liang.zhang
 * @date 2019年12月11日
 * @desc TODO
 * 易游-网吧-网吧质量
 */
public interface EyBarBarService {
	
	/** 合规评分 */
	public Map<String, Object> audit(EyBarBarAuditQuery query);
	
	/** 通道评分 */
	public Map<String, Object> channel(EyBarBarChannelQuery query);
}
