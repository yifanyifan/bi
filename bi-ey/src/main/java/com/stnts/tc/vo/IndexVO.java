package com.stnts.tc.vo;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 搜索框用到的对象
 * @author liang.zhang
 * @date 2019年12月17日
 * @desc TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexVO {
	
	private String key;
	private String id;
	
	@JSONField(serialize=false)
	public boolean id2Id() {
		try {
			Integer.parseInt(key);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
