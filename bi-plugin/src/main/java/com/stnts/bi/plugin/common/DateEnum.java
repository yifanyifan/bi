package com.stnts.bi.plugin.common;

/**
 * @author liang.zhang
 * @date 2020年7月8日
 * @desc TODO
 */
public enum DateEnum {
	
	LAST_WEEK("lastWeek"), LAST_MONTH("lastMonth"), IN_THIRTY_DAYS("inThirtyDays");
	
	private String key;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	private DateEnum(String key) {
		this.key = key;
	}
}



	




