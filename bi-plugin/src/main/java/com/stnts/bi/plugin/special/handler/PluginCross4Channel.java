package com.stnts.bi.plugin.special.handler;

import java.util.List;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartParameterVO.ConditionVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;


/**
 * @author liang.zhang
 * @date 2020年8月27日
 * @desc TODO
 * 渠道交叉
 */
public class PluginCross4Channel extends BaseHandler{
	
	public static final String HANDLER_ID = "p011";
	
	public static final String TABLE_NAME = "plugin_dwb_realtime_cross_off_010";
	
//	public static final String PARAM_PLUGIN = "{\"chartType\":\"histogram\",\"databaseName\":\"bi_plugin\",\"tableName\":\"plugin_dwb_realtime_view_100\",\"rollup\":0,\"limit\":10,\"dimension\":[{\"name\":\"plugin_id\",\"aliasName\":\"插件ID\"}],\"measure\":[],\"dashboard\":[{\"name\":\"partition_date\",\"logic\":\"between\",\"value\":[\"2020-06-15\",\"2020-06-15\"],\"func\":\"day\"}],\"screen\":[]}";
	public static final String PARAM_CHANNEL = "{\"chartType\":\"histogram\",\"databaseName\":\"bi_plugin\",\"tableName\":\"plugin_dwb_realtime_view_010\",\"rollup\":0,\"limit\":10,\"dimension\":[{\"name\":\"channel_id\",\"aliasName\":\"渠道ID\"}],\"measure\":[],\"dashboard\":[{\"name\":\"partition_date\",\"logic\":\"between\",\"value\":[\"2020-06-15\",\"2020-06-15\"],\"func\":\"day\"}],\"screen\":[]}";

	@Override
	public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService,
			QueryChartParameterVO queryChartParameterVO) {
		
		List<ConditionVO> conds = queryChartParameterVO.getDashboard();
		appendLimit104Channel(conds, queryChartService, queryChartParameterVO);
		
		return super.handler(queryChartService, queryChartParameterVO);
	}
}
