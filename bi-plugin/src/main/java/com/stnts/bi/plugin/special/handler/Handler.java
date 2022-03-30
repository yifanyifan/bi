package com.stnts.bi.plugin.special.handler;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

/**
 * @author liang.zhang
 * @date 2020年7月7日
 * @desc TODO
 */
public interface Handler {
	
	/**
	 * @param queryChartService
	 * @param queryChartParameterVO
	 * @return
	 */
	public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, 
			QueryChartParameterVO queryChartParameterVO);
	
//	/**
//	 * @param queryChartService
//	 * @param cardChartResultVO
//	 * @return
//	 */
//	public ResultEntity<CardChartResultVO> handler(QueryChartService queryChartService, 
//			CardChartResultVO cardChartResultVO);
}
