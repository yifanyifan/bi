package com.stnts.tc.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liang.zhang
 * @date 2019年12月10日
 * @desc TODO
 * 易游-网吧-基础-指标
 */
@Getter
@Setter
public class EyBarBasicKpiQuery extends TopQuery{

	private String gid;	
	
	public static void main(String[] args) {
		
		EyBarBasicKpiQuery query = new EyBarBasicKpiQuery();
		query.setCycle("D");
		System.out.println(query.toString());
	}
}
