package com.stnts.tc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liang.zhang
 * @date 2019年12月13日
 * @desc TODO
 */
@AllArgsConstructor
@Getter
public enum PluginTypeEnum {
	
	BASE(1, "base"), BIZ(2, "biz");
	
	private int id;
	private String name;
	
	public static PluginTypeEnum pluginType(int id) {
		
		for(PluginTypeEnum p : PluginTypeEnum.values()) {
			
			if(p.getId() == id) {
				return p;
			}
		}
		return null;
	}
}
