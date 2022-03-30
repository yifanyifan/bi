package com.stnts.bi.plugin.special.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.plugin.common.DateEnum;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import com.stnts.bi.sql.vo.QueryChartResultVO.MeasureData;

import cn.hutool.core.collection.CollectionUtil;

import com.stnts.bi.sql.vo.QueryChartParameterVO.ConditionVO;

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
public class PluginTrendCardHandler extends BaseHandler{
	
	public static final String HANDLER_ID = "p002";
	
	public ResultEntity<QueryChartResultVO> handlerOld(QueryChartService queryChartService,
			QueryChartParameterVO queryChartParameterVO) {
		
		//自定义表
		//.filter(name -> StringUtils.equalsAny(name, "plugin_id", "channel_id", "product_id"))
		choiceTable(queryChartParameterVO);
		
		QueryChartResultVO resultVO = queryChartService.queryChart(queryChartParameterVO);
		
		
		if(CollectionUtil.isNotEmpty(resultVO.getDatas())) {
			
			Map<String, List<String>> compareMap = new HashMap<String, List<String>>();
			QueryChartResultVO.MeasureData mData = (MeasureData) resultVO.getDatas().get(0);
			List<String> dList = mData.getData();
			List<String> cdList = mData.getCompareData();
			compareMap.put(COMPARE_RATE, toCompareRateList(dList, cdList));
			
			List<ConditionVO> conds = new ArrayList<QueryChartParameterVO.ConditionVO>();
			//查询对比上周
			ConditionVO lastWeekCond = initCompareDateCond(DateEnum.LAST_WEEK);
			conds.add(lastWeekCond);
			queryChartParameterVO.setCompare(conds);
			
			QueryChartResultVO lastWeekResultVO = queryChartService.queryChart(queryChartParameterVO);
			QueryChartResultVO.MeasureData lastWeekData = (MeasureData) lastWeekResultVO.getDatas().get(0);
			
			List<String> lastWeekCompareData = lastWeekData.getCompareData();
			compareMap.put(COMPARE_LAST_WEEK, lastWeekCompareData);
			
			compareMap.put(COMPARE_LAST_WEEK_RATE, toCompareRateList(dList, lastWeekCompareData));
			
			//查询对比上月
			ConditionVO lastMonthCond = initCompareDateCond(DateEnum.LAST_MONTH);
			conds.clear();
			conds.add(lastMonthCond);
			queryChartParameterVO.setCompare(conds);
			
			QueryChartResultVO lastMonthResultVO = queryChartService.queryChart(queryChartParameterVO);
			QueryChartResultVO.MeasureData lastMonthData = (MeasureData) lastMonthResultVO.getDatas().get(0);
			
			List<String> lastMonthCompareData = lastMonthData.getCompareData();
			compareMap.put(COMPARE_LAST_MONTH, lastMonthCompareData);
			
			compareMap.put(COMPARE_LAST_MONTH_RATE, toCompareRateList(dList, lastMonthCompareData));
			
			mData.setCompareMap(compareMap);
		
		}
		return ResultEntity.success(resultVO);
	}
	
	@Override
	public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService,
			QueryChartParameterVO queryChartParameterVO) {
		
		//自定义表
		//.filter(name -> StringUtils.equalsAny(name, "plugin_id", "channel_id", "product_id"))
		choiceTable(queryChartParameterVO);
		
		QueryChartResultVO resultVO = queryChartService.queryChart(queryChartParameterVO);
		Map<String, List<String>> compareMap = new HashMap<String, List<String>>();
		QueryChartResultVO.MeasureData mData = (MeasureData) resultVO.getDatas().get(0);
		List<String> dList = mData.getData();
		List<String> cdList = mData.getCompareData();
		compareMap.put(COMPARE_RATE, toCompareRateList(dList, cdList));
		
		//查询对比上周
		appendCompare(mData, queryChartService, queryChartParameterVO, compareMap, DateEnum.LAST_WEEK);
		//查询对比上月
		appendCompare(mData, queryChartService, queryChartParameterVO, compareMap, DateEnum.LAST_MONTH);
		
		mData.setCompareMap(compareMap);
		
		return ResultEntity.success(resultVO);
	}
}
