package com.stnts.tc.query;

import com.stnts.tc.common.CycleEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liang.zhang
 * @date 2019年11月28日
 * @desc TODO
 * 留存
 */
@Getter
@Setter
public class EyGlobalBarReQuery extends TopQuery{
	
	private int type = 1;
	
	public static void main(String[] args) {
		
		EyGlobalBarReQuery q = new EyGlobalBarReQuery();
		q.setCycle(CycleEnum.MONTH.getCycle());
		System.out.println(q.getSrcEndDate());
	}
}
