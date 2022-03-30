package com.stnts.bi.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.LogOpEntity;

/**
 * @author liang.zhang
 * @date 2020年3月26日
 * @desc TODO
 */
public interface LogOpService {
	
	/**
	 * 列表
	 * @param page
	 * @param beginDate
	 * @param endDate
	 * @param cond
	 * @return
	 */
	public ResultEntity<Page<LogOpEntity>> findLogListByCond(Integer pageNo, String cnname, String beginDate, String endDate,
			String cond);

}
