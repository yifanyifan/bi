package com.stnts.tc.common;

/**
 * @author liang.zhang
 * @date 2019年11月22日
 * @desc TODO
 */
public enum OpEnum {
	
	AVG("AVG"), SUM("SUM"), APPEND("APPEND"), LAST("LAST");
	
	private String op;

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	private OpEnum(String op) {
		this.op = op;
	}
}
