package com.stnts.bi.plugin.special.handler;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

/**
 * @author liang.zhang
 * @date 2020年7月17日
 * @desc TODO
 * 转化
 */
public class PluginTransHandler extends BaseHandler{
	
	public static final String HANDLER_ID = "p003";

	@Override
	public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService,
			QueryChartParameterVO queryChartParameterVO) {
		
		choiceTable(queryChartParameterVO);
		System.out.println("choiceTable: " + queryChartParameterVO.getTableName());
		QueryChartResultVO resultVO = queryChartService.queryChart(queryChartParameterVO);
		
		appendCompNew(queryChartService, queryChartParameterVO, resultVO);
		
		//对比一个上周均值  一个上月均值  然后各指标对比
		//pre_week_avg  pre_month_avg
//		QueryChartParameterVO weekQueryVO = new QueryChartParameterVO();
//		BeanUtil.copyProperties(queryChartParameterVO, weekQueryVO);
		//contrast得清空  在此之前需要把计算上周对比 上月对比的字段记录下来
//		rmDimPartitionCol(queryChartParameterVO.getDimension());
//		List<OlapChartMeasure> meas = queryChartParameterVO.getMeasure();
//		List<String> weekMeas = meas.stream().filter(mea -> StringUtils.contains(mea.getContrast(), COMP_PRE_WEEK_KEY)).map(OlapChartMeasure::getAliasName).collect(Collectors.toList());
//		List<String> monthMeas = meas.stream().filter(mea -> StringUtils.contains(mea.getContrast(), COMP_PRE_MONTH_KEY)).map(OlapChartMeasure::getAliasName).collect(Collectors.toList());
//		
//		rmMeaContrast(meas);
//		
//		//week
//		Map<String, MeasureData> weekMeaDatas = new HashMap<String, QueryChartResultVO.MeasureData>();
//		if(CollectionUtil.isNotEmpty(weekMeas)) {
//			setDashboardDate(queryChartParameterVO.getDashboard(), PRE_WEEK);
//			QueryChartResultVO weekResultVO = queryChartService.queryChart(queryChartParameterVO);
//			weekMeaDatas = weekResultVO.getDatas().stream().filter(obj -> obj instanceof MeasureData).map(mea -> (MeasureData)mea)
//			.filter(mea -> weekMeas.contains(mea.getDisplayName())).collect(Collectors.toMap(MeasureData::getDisplayName, mea -> mea));
//		}
//		
//		//month
//		Map<String, MeasureData> monthMeaDatas = new HashMap<String, QueryChartResultVO.MeasureData>();
//		if(CollectionUtil.isNotEmpty(monthMeas)) {
//			setDashboardDate(queryChartParameterVO.getDashboard(), PRE_MONTH);
//			QueryChartResultVO monthResultVO = queryChartService.queryChart(queryChartParameterVO);
//			monthMeaDatas = monthResultVO.getDatas().stream().filter(obj -> obj instanceof MeasureData).map(mea -> (MeasureData)mea)
//					.filter(mea -> monthMeas.contains(mea.getDisplayName())).collect(Collectors.toMap(MeasureData::getDisplayName, mea -> mea));
//		}
//		/** 
//		   *  这里有多少种维度，周和月的对比值就有多少个..
//		 * 把原始数据变为map形式，对比上周、对比上月变为map形式，再做关联 
//		 */
//		
//		Map<String, MeasureData> rawData = resultVO.getDatas().stream().filter(obj -> obj instanceof MeasureData).map(mea -> (MeasureData)mea).collect(Collectors.toMap(MeasureData::getDisplayName, mea -> mea));
//		if(CollectionUtil.isNotEmpty(rawData)) {
//			
//			Set<Entry<String, MeasureData>> entrys = rawData.entrySet();
//			for(Iterator<Entry<String, MeasureData>> it = entrys.iterator() ; it.hasNext() ;) {
//				
//				Entry<String, MeasureData> entry = it.next();
//				String rawKey = entry.getKey();
//				if(weekMeaDatas.containsKey(rawKey)) {  //需要对比上周 
//					doComp4Week(weekMeaDatas, entry, rawKey);
//				}
//				if(monthMeaDatas.containsKey(rawKey)) {  //需要对比上月
//					doComp4Month(monthMeaDatas, entry, rawKey);
//				}
//			}
//		}
		
		return ResultEntity.success(resultVO);
	}

}
