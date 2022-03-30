package com.stnts.bi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liang.zhang
 * @date 2020
 * @desc TODO
 */
@AllArgsConstructor
public enum LogOpTypeEnum {

	VIEW(1, "view", "浏览"), NEW(2, "new", "新增"), MOD(3, "modify", "编辑"), DEL(4, "del", "删除"), EXPORT(5, "export", "导出"), LOGIN(6, "login", "登录");
	
	@Getter
	@Setter
	private Integer logTypeId;
	@Getter
	@Setter
	private String logType;
	@Getter
	@Setter
	private String logTypeDesc;
	
	@Override
	public String toString() {
		return this.getLogType();
	}
	
}
