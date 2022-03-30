package com.stnts.bi.plugin.service.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.plugin.common.Constants;
import com.stnts.bi.plugin.service.BaseService;
import com.stnts.bi.plugin.special.ServiceHandlerFactory;
import com.stnts.bi.plugin.special.handler.Handler;
import com.stnts.bi.sql.service.ExportChartService;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.util.JacksonUtil;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartParameterVO.ConditionVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import com.stnts.bi.sql.vo.QueryChartResultVO.MeasureData;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BaseServiceImpl implements BaseService, Constants{
	
	@Autowired
    private ExportChartService exportChartService;

	@Autowired
	private QueryChartService queryChartService;
	
	@Override
	public ResultEntity<QueryChartResultVO> getChart(String data) {
		try {

			QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
			String id = queryChartParameterVO.getId();
			
			//temp
			
			if(StringUtils.isNotBlank(id)) {  //如果ID不为空,则特殊处理 特殊处理 特殊处理
				Handler handler = ServiceHandlerFactory.getHandler(id);
				if(null != handler) {
					return handler.handler(queryChartService, queryChartParameterVO);
				}
			}
			
			String tableName = queryChartParameterVO.getTableName();
			switch (tableName) {
				case "plugin_dwb_realtime_cross_view_010":queryChartParameterVO.setTableName("plugin_dwb_realtime_cross_off_010");break;
				case "plugin_dwb_realtime_cross_view_100":queryChartParameterVO.setTableName("plugin_dwb_realtime_cross_off_100");break;
				case "plugin_all_dwb_realtime_view_100":queryChartParameterVO.setTableName("plugin_all_dwb_realtime_daily_100");break;
			}
			
			QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
			return ResultEntity.success(queryChartResultVO);
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("[ENGINE]: {}", e.getMessage());
			return ResultEntity.exception(e.getMessage());
		}
	}

	@Override
	public void export(String data, HttpServletResponse response) {

		try {
			
			QueryChartParameterVO queryChartParameterVO = JacksonUtil.fromJSON(data, QueryChartParameterVO.class);
			List<ConditionVO> dashboard = queryChartParameterVO.getDashboard();
			List<ConditionVO> dashboardOld = new ArrayList<ConditionVO>(dashboard.size());
			dashboardOld.addAll(dashboard);
			String id = queryChartParameterVO.getId();
			QueryChartResultVO queryChartResultVO = null;
			if(StringUtils.isNotBlank(id)) {  //如果ID不为空,则特殊处理 特殊处理 特殊处理
				Handler handler = ServiceHandlerFactory.getHandler(id);
				if(null != handler) {
					ResultEntity<QueryChartResultVO> _resultVO = handler.handler(queryChartService, queryChartParameterVO);
					queryChartResultVO = _resultVO.getData();
				}
			}else {
				queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
			}
			
			//处理compareMap结构
			handlerResultVO(queryChartResultVO);
			
			queryChartParameterVO.setDashboard(dashboardOld);
			exportChartService.exportChart(queryChartParameterVO, queryChartResultVO, response);
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("[EXPORT]: {}", e.getMessage());
		}
	}

	/**
	 * 处理特殊的返回结果
	 * @param queryChartResultVO
	 */
	private void handlerResultVO(QueryChartResultVO queryChartResultVO) {
		
		if(null != queryChartResultVO) {
			
			List<Object> datas = queryChartResultVO.getDatas();
			if(CollectionUtil.isNotEmpty(datas)) {
				
				List<Object> moreDatas = new ArrayList<Object>();
				datas.stream().filter(data -> data instanceof MeasureData).forEach(mea -> {
					MeasureData md = (MeasureData) mea;
					Map<String, List<String>> compareMap = md.getCompareMap();
					if(CollectionUtil.isNotEmpty(compareMap)) {
						
						Set<Entry<String, List<String>>> entrys = compareMap.entrySet();
						for(Iterator<Entry<String, List<String>>> it = entrys.iterator() ; it.hasNext() ;) {
							
							MeasureData _md = new MeasureData();
							Entry<String, List<String>> entry = it.next();
							String mdName = md.getDisplayName();
							String type = entry.getKey();
							List<String> dat = entry.getValue();
							String suff = "(对比上周)";
							switch (type) {
								case COMPARE_LAST_WEEK:suff = "(上周日均值)";break;
								case COMPARE_LAST_WEEK_RATE:suff = "(对比上周)";break;
								case COMPARE_LAST_MONTH:suff = "(上月日均值)";break;
								case COMPARE_LAST_MONTH_RATE:suff = "(对比上月)";break;
								case COMPARE_PRE_WEEK_AVG_RATE:suff = "(对比上周)";break;
								case COMPARE_PRE_MONTH_AVG_RATE:suff = "(对比上月)";break;
								default:
									break;
							}
							String displayName = mdName.concat(suff);
							_md.setDisplayName(displayName);
							dat = null == dat ? Collections.emptyList() : dat;
							_md.setData(dat);
							if(StringUtils.containsIgnoreCase(type, "rate")) {
								_md.setDigitDisplay("percent");
							}
							
							moreDatas.add(_md);
						}
						
					}
				});;
				datas.addAll(moreDatas);
			}
		}
	}
}
