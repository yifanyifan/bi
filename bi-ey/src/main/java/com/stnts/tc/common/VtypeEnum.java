package com.stnts.tc.common;

import lombok.AllArgsConstructor;

/**
 * @author liang.zhang
 * @date 2019年11月20日
 * @desc TODO
 * 值类型
 */
@AllArgsConstructor
public enum VtypeEnum {
	
	V("V"), TV("TV"), TR("TR"), HV("HV"), HR("HR"), R("R"), AV("AV"), AR("AR");
	
	private String vtype;

	public String getVtype() {
		return vtype;
	}

	public void setVtype(String vtype) {
		this.vtype = vtype;
	}

	@Override
	public String toString() {
		return vtype;
	}
}
