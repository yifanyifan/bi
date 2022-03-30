package com.stnts.bi.plugin.special.handler;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

/**
 * @author liang.zhang
 * @date 2020年8月27日
 * @desc TODO
 * 异常分析 - 数据明细
 */
public class PluginErrTableHandler extends BaseHandler{

	public static final String HANDLER_ID = "p010";

	@Override
	public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService,
			QueryChartParameterVO queryChartParameterVO) {
		
		QueryChartResultVO resultVO = queryChartService.queryChart(queryChartParameterVO);
		//追加对比上周  对比上月
		appendCompNew(queryChartService, queryChartParameterVO, resultVO);
		
		return ResultEntity.success(resultVO);
	}
}
