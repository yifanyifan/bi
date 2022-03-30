package com.stnts.bi.vo;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author liang.zhang
 * @date 2020年5月20日
 * @desc TODO
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimplePermVO{

	private String id;
	private String name;
	private String code;
	private List<SimplePermVO> perms;
	
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof SimplePermVO) {
			SimplePermVO vo = (SimplePermVO) obj;
			return StringUtils.equals(id, vo.getId());
		}
		return false;
	}
}
