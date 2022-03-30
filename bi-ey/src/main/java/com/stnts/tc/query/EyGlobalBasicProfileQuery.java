package com.stnts.tc.query;

/**
 * @author liang.zhang
 * @date 2019年11月20日
 * @desc TODO
 */
public class EyGlobalBasicProfileQuery extends BaseQuery{

	@Override
	public String getSrcBeginDate() {
		return null != srcBeginDate ? srcBeginDate : beginDateDefault();
	}

	@Override
	public String getSrcEndDate() {
		return null != srcEndDate ? srcEndDate : endDateDefault();
	}
}
