package com.stnts.bi.utils;

import org.springframework.util.Assert;

/**
 * @author liang.zhang
 * @date 2020
 * @desc TODO
 */
public class BiUtil {
	
	public static String biSessionId(String sessionId) {
		//bi:sessions:7d77fd47-eea4-4e8e-8682-cefd269e9eb1
		Assert.notNull(sessionId, "sessionId must not be null");
		return "bi:sessions:".concat(sessionId);
	}
}
