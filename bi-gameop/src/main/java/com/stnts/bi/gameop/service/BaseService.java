package com.stnts.bi.gameop.service;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.vo.QueryChartResultVO;

import javax.servlet.http.HttpServletResponse;

/**
 * @author liang.zhang
 * @date 2020年6月28日
 * @desc TODO
 */
public interface BaseService {
	
	/**
	 * 公共的查询方法
	 * @param data
	 * @return
	 */
	public ResultEntity<QueryChartResultVO> getChart(String data);
	
	
	/**
	 * 公共导出方法
	 * @param data
	 * @param response
	 */
	public void export(String data, HttpServletResponse response);
}
