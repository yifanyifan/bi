package com.stnts.tc.common;

/**
 * @author liang.zhang
 * @date 2019年11月18日
 * @desc TODO
 *  s数据周期
 */
public enum CycleEnum {
	
	DAY("D"), WEEK("W"), MONTH("M"), YEAR("Y");
	
	private String cycle;
	
	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	private CycleEnum(String cycle) {
		this.cycle = cycle;
	}
	
	public static CycleEnum cycle(String _cycle) {
		for(CycleEnum c : CycleEnum.values()) {
			if(c.getCycle().equals(_cycle)) {
				return c;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this.cycle;
	}
}
