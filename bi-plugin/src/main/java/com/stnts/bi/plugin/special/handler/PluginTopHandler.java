package com.stnts.bi.plugin.special.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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
import lombok.Getter;
import lombok.Setter;

/**
 * @author liang.zhang
 * @date 2020年7月7日
 * @desc TODO
 * 覆盖 = 渠道排名 | 插件排名
 */
@Getter
@Setter
public class PluginTopHandler extends BaseHandler{
	
	public static final String HANDLER_ID = "p001";
	
	/* (non-Javadoc)
	 * 
	 * @see com.stnts.bi.plugin.special.handler.Handler#handler(com.stnts.bi.sql.service.QueryChartService, com.stnts.bi.sql.vo.QueryChartParameterVO)
	 */
	public ResultEntity<QueryChartResultVO> handlerOld(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {
		
		QueryChartResultVO topResultVO = queryChartService.queryChart(queryChartParameterVO);
		if(CollectionUtil.isNotEmpty(topResultVO.getDatas())) {
			
			QueryChartResultVO.DimensionData  nameData = (DimensionData) topResultVO.getDatas().get(0);
			//添加对比上周条件
			List<ConditionVO> conds = new ArrayList<QueryChartParameterVO.ConditionVO>();
			ConditionVO dateCond  = initCompareDateCond(DateEnum.LAST_WEEK);
			conds.add(dateCond);
			
			ConditionVO dimNameCond = new ConditionVO();
			dimNameCond.setName(nameData.getName());
			dimNameCond.setLogic("in");
			//这个顺序跟后面的不一样
			dimNameCond.setValue(JSON.toJSONString(nameData.getData()));
			conds.add(dimNameCond);
			
		    queryChartParameterVO.setCompare(conds);
		    
		    queryChartParameterVO.getDashboard().add(dimNameCond);
		    
		    QueryChartResultVO _topResultVO = queryChartService.queryChart(queryChartParameterVO);
		    
		    QueryChartResultVO.MeasureData mData = (MeasureData) _topResultVO.getDatas().get(1);
		    List<String> dList = mData.getData();
		    List<String> cdList = mData.getCompareData();


		    List<String> compareRate = toCompareRateList(dList, cdList);
		    
		    Map<String, List<String>> compareMap = mData.getCompareMap();
		    compareMap = null == compareMap ? new HashMap<String, List<String>>() : compareMap;
		    compareMap.put(COMPARE_RATE, compareRate);
		    
		    mData.setCompareMap(compareMap);
			
		    return ResultEntity.success(_topResultVO);
		}
		return ResultEntity.success(topResultVO);
	}
	
	/* (non-Javadoc)
	 * @see com.stnts.bi.plugin.special.handler.BaseHandler#handler(com.stnts.bi.sql.service.QueryChartService, com.stnts.bi.sql.vo.QueryChartParameterVO)
	 * 对比在需要指标排序的时候，维度会错乱   得自己拼装
	 */
	@Override
	public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {
		
		String tableName = queryChartParameterVO.getTableName();
		switch (tableName) {
			case "plugin_dwb_realtime_view_010":queryChartParameterVO.setTableName("plugin_dwb_realtime_daily_010");break;
			case "plugin_dwb_realtime_view_100":queryChartParameterVO.setTableName("plugin_dwb_realtime_daily_100");break;
		}
		
		QueryChartResultVO topResultVO = queryChartService.queryChart(queryChartParameterVO);
		if(CollectionUtil.isNotEmpty(topResultVO.getDatas())) {
			
			QueryChartResultVO.DimensionData nameData = (DimensionData) topResultVO.getDatas().get(0);
			QueryChartResultVO.MeasureData meaData = (MeasureData) topResultVO.getDatas().get(1);
			
			//添加对比上周条件
//			List<ConditionVO> conds = new ArrayList<QueryChartParameterVO.ConditionVO>();
//			ConditionVO dateCond  = initCompareDateCond(DateEnum.LAST_WEEK);
//			conds.add(dateCond);
			
			ConditionVO dimNameCond = new ConditionVO();
			dimNameCond.setName(nameData.getName());
			dimNameCond.setLogic("in");
			//这个顺序跟后面的不一样
			dimNameCond.setValue(JSON.toJSONString(nameData.getData()));
//			conds.add(dimNameCond);
			
			List<ConditionVO> conds = queryChartParameterVO.getDashboard();
		    changeDate(conds);
		    conds.add(dimNameCond);
		    
		    QueryChartResultVO _topResultVO = queryChartService.queryChart(queryChartParameterVO);
		    
		    List<Object> datas = _topResultVO.getDatas();
		    if(CollectionUtil.isNotEmpty(datas)) {
		    	
		    	QueryChartResultVO.DimensionData  _dimData = (DimensionData) _topResultVO.getDatas().get(0);
		    	QueryChartResultVO.MeasureData _meaData = (MeasureData) _topResultVO.getDatas().get(1);
		    	Map<String, String> weekDict = new HashMap<String, String>();
		    	IntStream.range(0, _dimData.getData().size()).forEach(i -> {
		    		weekDict.put(_dimData.getData().get(i), _meaData.getData().get(i));
		    	});
		    	
		    	int nameSize = nameData.getData().size();
		    	List<String> compareList = new ArrayList<String>();
		    	IntStream.range(0, nameSize).forEach(i -> {
		    		String name = nameData.getData().get(i);
		    		String v = weekDict.getOrDefault(name, EMPTY_V);
		    		compareList.add(v);
		    	});
		    	
//		    	meaData.setCompareData(compareList);
		    	
		    	List<String> compareRate = toCompareRateList(meaData.getData(), compareList);
		    	Map<String, List<String>> compareMap = meaData.getCompareMap();
			    compareMap = null == compareMap ? new HashMap<String, List<String>>() : compareMap;
			    compareMap.put(COMPARE_LAST_WEEK, compareList);
			    compareMap.put(COMPARE_LAST_WEEK_RATE, compareRate);
			    meaData.setCompareMap(compareMap);
		    }else {  //前端要这个格式
		    	Map<String, List<String>> compareMap = new HashMap<String, List<String>>();
		    	compareMap.put(COMPARE_LAST_WEEK, null);
			    compareMap.put(COMPARE_LAST_WEEK_RATE, null);
		    	meaData.setCompareMap(compareMap);
		    }
		}
		return ResultEntity.success(topResultVO);
	}
}
