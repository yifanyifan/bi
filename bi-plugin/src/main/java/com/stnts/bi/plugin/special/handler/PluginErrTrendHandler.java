package com.stnts.bi.plugin.special.handler;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

/**
 * @author liang.zhang
 * @date 2020年7月30日
 * @desc TODO
 * 异常分析  数据趋势
 */
public class PluginErrTrendHandler extends BaseHandler{
	
	public static final String HANDLER_ID = "p006";

	@Override
	public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService,
			QueryChartParameterVO queryChartParameterVO) {
		
//		choiceTable("plugin_all_dwb_realtime_view", queryChartParameterVO);
		choiceTable("plugin_all_dwb_realtime_daily", queryChartParameterVO);
		System.out.println(">>>>>Choice table: " + queryChartParameterVO.getTableName());
		
		return super.handler(queryChartService, queryChartParameterVO);
	}
}
