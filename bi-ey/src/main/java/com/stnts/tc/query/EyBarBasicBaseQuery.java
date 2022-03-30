package com.stnts.tc.query;

import com.stnts.tc.common.CycleEnum;

import lombok.Data;

/**
 * @author liang.zhang
 * @date 2019年12月10日
 * @desc TODO
 */
@Data
public class EyBarBasicBaseQuery {
	
	private CycleEnum cycle = CycleEnum.DAY;
	private String date;
	private String gid;
}
