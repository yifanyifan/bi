package com.stnts.bi.plugin.special.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.plugin.common.DateEnum;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import com.stnts.bi.sql.vo.QueryChartParameterVO.ConditionVO;
import com.stnts.bi.sql.vo.QueryChartResultVO.DimensionData;
import com.stnts.bi.sql.vo.QueryChartResultVO.MeasureData;

import cn.hutool.core.collection.CollectionUtil;

/**
 * @author liang.zhang
 * @date 2020年7月24日
 * @desc TODO
 * 转化 - 各**分布
 */
public class PluginTransDisHandler extends BaseHandler{
	
	public static final String HANDLER_ID = "p004";
	
	@Override
	public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService,
			QueryChartParameterVO queryChartParameterVO) {
		
		choiceTable(queryChartParameterVO);
		QueryChartResultVO resultVO = queryChartService.queryChart(queryChartParameterVO);
		System.out.println("choiceTable: " + queryChartParameterVO.getTableName());
		if(CollectionUtil.isNotEmpty(resultVO.getDatas())) {
			
			/** 结构体 */
			Map<String, List<String>> compareMap = new HashMap<String, List<String>>();
			compareMap.put(COMPARE_LAST_WEEK, null);
			compareMap.put(COMPARE_LAST_WEEK_RATE, null);
			compareMap.put(COMPARE_LAST_MONTH, null);
			compareMap.put(COMPARE_LAST_MONTH_RATE, null);
			QueryChartResultVO.MeasureData meaData = (MeasureData) resultVO.getDatas().get(1);
			meaData.setCompareMap(compareMap);
			
			appendContrast(queryChartParameterVO, resultVO, queryChartService, DateEnum.LAST_WEEK);
			appendContrast(queryChartParameterVO, resultVO, queryChartService, DateEnum.LAST_MONTH);
		}
		
		return ResultEntity.success(resultVO);
	}

	public ResultEntity<QueryChartResultVO> handlerOld(QueryChartService queryChartService,
			QueryChartParameterVO queryChartParameterVO) {
		
		choiceTable(queryChartParameterVO);
		QueryChartResultVO resultVO = queryChartService.queryChart(queryChartParameterVO);
		System.out.println("choiceTable: " + queryChartParameterVO.getTableName());
		
		if(CollectionUtil.isNotEmpty(resultVO.getDatas())) {
			
			QueryChartResultVO.DimensionData  nameData = (DimensionData) resultVO.getDatas().get(0);
			ConditionVO dimNameCond = initDimNameCond(nameData);
			List<ConditionVO> conds = initConds(nameData, DateEnum.LAST_WEEK, dimNameCond);
		    queryChartParameterVO.setCompare(conds);
		    
		    List<ConditionVO> condsNew = queryChartParameterVO.getDashboard().stream().map(cond -> {
		    	if(StringUtils.equals(cond.getName(), nameData.getName())) {
		    		return dimNameCond;
		    	}
		    	return cond;
		    }).collect(Collectors.toList());
		    
		    queryChartParameterVO.setDashboard(condsNew);
		    
		    QueryChartResultVO _topResultVO = queryChartService.queryChart(queryChartParameterVO);
		    
		    QueryChartResultVO.MeasureData data = (MeasureData) resultVO.getDatas().get(1);
		    QueryChartResultVO.MeasureData wData = (MeasureData) _topResultVO.getDatas().get(1);
		    List<String> dList = wData.getData();
		    List<String> cdList = wData.getCompareData();
		    
		    List<String> compareRate = toCompareRateList(dList, cdList);
		    
		    Map<String, List<String>> compareMap = data.getCompareMap();
		    compareMap = null == compareMap ? new HashMap<String, List<String>>() : compareMap;
		    compareMap.put(COMPARE_LAST_WEEK, cdList);
		    compareMap.put(COMPARE_LAST_WEEK_RATE, compareRate);
		    
		    List<ConditionVO> monthConds = initConds(nameData, DateEnum.LAST_MONTH, dimNameCond);
		    queryChartParameterVO.setCompare(monthConds);
		    
		    QueryChartResultVO monthResultVO = queryChartService.queryChart(queryChartParameterVO);
		    QueryChartResultVO.MeasureData mData = (MeasureData) monthResultVO.getDatas().get(1);
		    List<String> _dList = mData.getData();
		    List<String> _cdList = mData.getCompareData();
		    List<String> _compareRate = toCompareRateList(_dList, _cdList);
		    compareMap.put(COMPARE_LAST_MONTH, _cdList);
		    compareMap.put(COMPARE_LAST_MONTH_RATE, _compareRate);
		    
		    data.setCompareMap(compareMap);
		}
		
		return ResultEntity.success(resultVO);
	}
	
	/**
	 * @param nameData
	 * @return
	 */
	private ConditionVO initDimNameCond(DimensionData nameData) {
		
		//维度中的枚举值写入条件中
		ConditionVO dimNameCond = new ConditionVO();
		dimNameCond.setName(nameData.getName());
		dimNameCond.setLogic("in");
		dimNameCond.setValue(JSON.toJSONString(nameData.getData()));
		return dimNameCond;
	}
	
	/**
	 * 生成不同时间维度的对比条件
	 * @param resultVO
	 * @param cycle
	 * @return
	 */
	private List<ConditionVO> initConds(DimensionData nameData, DateEnum cycle, ConditionVO dimNameCond){
		
		List<ConditionVO> conds = new ArrayList<QueryChartParameterVO.ConditionVO>();
		//添加对比上周条件:DateEnum.LAST_WEEK
		ConditionVO dateCond  = initCompareDateCond(cycle);
		conds.add(dateCond);
		//维度中的枚举值写入条件中
		conds.add(dimNameCond);
		
		return conds;
	}
}
