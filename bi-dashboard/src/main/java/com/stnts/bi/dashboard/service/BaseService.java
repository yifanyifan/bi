package com.stnts.bi.dashboard.service;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
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
	ResultEntity<QueryChartResultVO> getChart(String data);

	/**
	 * 桑基图
	 * @param data
	 * @param sankeyNodeNumberPerLayer
	 * @return
	 */
	ResultEntity<QueryChartResultVO> getChart(QueryChartParameterVO queryChartParameterVO, Integer sankeyNodeNumberPerLayer);

	/**
	 * 桑基图
	 * @param data
	 * @param sankeyNodeNumberPerLayer
	 * @return
	 */
	ResultEntity<QueryChartResultVO> getChart(String data, Integer sankeyNodeNumberPerLayer);
	
	
	/**
	 * 公共导出方法
	 * @param data
	 * @param response
	 */
	void export(String data, HttpServletResponse response);

	/**
	 * 桑基图那个
	 * @param data
	 * @param sankeyNodeNumberPerLayer
	 * @param response
	 */
	void export(String data, Integer sankeyNodeNumberPerLayer, HttpServletResponse response);
}
