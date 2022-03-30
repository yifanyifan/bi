package com.stnts.bi.plugin.special.handler;


import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liang.zhang
 * @date 2020年7月8日
 * @desc TODO
 * 处理数据趋势卡片  主要解决选择哪个表的问题  还有对比上周  对比上月  指定时间对比的数据融合
 */
@Getter
@Setter
public class PluginTrendHandler extends BaseHandler{
	
	public static final String HANDLER_ID = "p009";
	
	@Override
	public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService,
			QueryChartParameterVO queryChartParameterVO) {
		
		//自定义表
		//.filter(name -> StringUtils.equalsAny(name, "plugin_id", "channel_id", "product_id"))
		choiceTable(queryChartParameterVO);
		
		QueryChartResultVO resultVO = queryChartService.queryChart(queryChartParameterVO);
		
		return ResultEntity.success(resultVO);
	}
}
