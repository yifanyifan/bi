package com.stnts.tc.query;

/**
 * @author liang.zhang
 * @date 2019年11月28日
 * @desc TODO
 */
public abstract class TopQuery extends BaseQuery{

	@Override
	public String getSrcBeginDate() {
		return null != srcBeginDate ? srcBeginDate : beginDateDefault();
	}

	@Override
	public String getSrcEndDate() {
		return null != srcEndDate ? srcEndDate : endDateDefault();
	}

	@Override
	public String toString() {
		return String.format("cycle: %s, srcBeginDate: %s, srcEndDate: %s, destBeginDate: %s, destEndDate: %s, index: %s", cycle, getSrcBeginDate(), getSrcEndDate(), destBeginDate, destEndDate, index);
	}
}
