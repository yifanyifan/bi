package com.stnts.tc.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liang.zhang
 * @date 2019年12月11日
 * @desc TODO
 * 包含所有条件的query
 */
@Getter
@Setter
public class FinalQuery extends TopQuery{

	private String gid;
	private String pid;
}
