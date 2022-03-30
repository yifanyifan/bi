package com.stnts.tc.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stnts.tc.common.Constants;
import com.stnts.tc.common.CycleEnum;
import com.stnts.tc.common.KeyConfiguration;
import com.stnts.tc.common.KeyUtils;
import com.stnts.tc.common.OpEnum;
import com.stnts.tc.common.OptionFactory;
import com.stnts.tc.common.VtypeEnum;
import com.stnts.tc.query.EyBarPluginAnalysisQuery;
import com.stnts.tc.query.EyBarPluginProfileQuery;
import com.stnts.tc.service.EyBarPluginService;
import com.stnts.tc.utils.DateUtil;
import com.stnts.tc.utils.HbaseClient;
import com.stnts.tc.utils.TcUtil;
import com.stnts.tc.vo.KpiSearch;
import com.stnts.tc.vo.Option;
import com.stnts.tc.vo.PluginKeyVO;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liang.zhang
 * @date 2019年12月12日
 * @desc TODO
 */
@Service("eyBarPluginService")
@Slf4j
public class EyBarPluginServiceImpl implements EyBarPluginService, Constants{
	
	@Autowired
	private HbaseClient hbase;
	
	@Autowired
	private KeyConfiguration keyConf;

	@Override
	public Map<String, Object> profile(EyBarPluginProfileQuery query) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			log.info("query: {}", query.toString());
			String gid = query.getGid();
			//先查全局
			String k = KeyUtils.B_DE_PI(gid);
			boolean isComp = query.isComp();
			CycleEnum cycle = CycleEnum.cycle(query.getCycle());
			List<KpiSearch> kpiSearches = TcUtil.kpis(k, query.getSrcBeginDate(), query.getSrcEndDate(), cycle, VtypeEnum.V.toString());
//			Map<String, Object> srcDat = hbase.list(kpiSearches);
//			Map<String, Object> srcMergeMap = TcUtil.merge2Obj(srcDat, null);
			Map<String, Object> srcMergeMap = hbase.pulls(kpiSearches, query.getSrcBeginDate(), query.getSrcEndDate(), cycle);
			List<String> kpis = KeyUtils.B_DE_PI_LIST(isComp);
			Map<String, Object> srcObjMap = TcUtil.toObjMap(srcMergeMap, isComp, kpis);
			OpEnum op = query.isIndex() ? OpEnum.LAST : OpEnum.AVG;
			Map<String, BigDecimal> srcAvgMap = TcUtil.objMap2Card(srcObjMap, op);
//			Map<String, BigDecimal> srcAvgMap = TcUtil.objMap2Avg(srcObjMap);
			result = srcObjMap;
			if(isComp) {
				List<KpiSearch> destKpiSearches = TcUtil.kpis(k, query.getDestBeginDate(), query.getDestEndDate(), cycle, VtypeEnum.V.toString());
//				Map<String, Object> destDat = hbase.list(destKpiSearches);
//				Map<String, Object> mergeDestDat = TcUtil.merge2Obj(destDat, null);
				Map<String, Object> mergeDestDat = hbase.pulls(destKpiSearches, query.getDestBeginDate(), query.getDestEndDate(), cycle);
				Map<String, Object> destObjMap = TcUtil.toObjMap(mergeDestDat, isComp, kpis);
				Map<String, BigDecimal> destAvgMap = TcUtil.objMap2Avg(destObjMap);
				Map<String, Map<String, BigDecimal>> compMap = TcUtil.compMap(srcAvgMap, destAvgMap);
				result.put(RESULT_CARD, compMap);
			}else {
				result.put(RESULT_CARD, srcAvgMap);
			}
			//再查插件列表
			String pluginKey = KeyUtils.B_DE_PI_DE(gid);
			//这里不需要同比环比 isComp=true
			List<String> pluginKpis = KeyUtils.B_DE_PI_DE_LIST(true);
			List<KpiSearch> pSearches = TcUtil.kpis(pluginKey, query.getSrcBeginDate(), query.getSrcEndDate(), cycle, VtypeEnum.V.toString());
//			Map<String, Object> pDat = hbase.list(pSearches);
//			Map<String, Object> pMergeMap = TcUtil.merge2Obj(pDat, null);
			Map<String, Object> pMergeMap = hbase.pulls(pSearches, query.getSrcBeginDate(), query.getSrcEndDate(), cycle);
			Map<String, List<Map<String, Object>>> plugins = TcUtil.pluginJson2Map(pMergeMap, pluginKpis);
			
			result.put(KEY_PLUGINS, plugins);
			Map<String, List<Option>> keyOptions = OptionFactory.EY_BAR_PLUGIN_PROFILE();
			keyOptions.put(KEY_TABLE_OPTIONS, keyConf.getEyBarPluginProfile());
			result.put(KEY_OPTIONS, keyOptions);
			result.put(KEY_IS_SHOW_COMP, query.isShowComp());
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("[易游-网吧-插件-概况]:{}", e.getMessage());
		}
		return result;
	}

	@Override
	public Map<String, Object> analysis(EyBarPluginAnalysisQuery query) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			log.info("query: {}", query.toString());
			String gid = query.getGid();
			String k = KeyUtils.B_DE_PI_DE(gid);
			int pid = Integer.parseInt(query.getPid());
			boolean isComp = query.isComp();
			OpEnum op = query.isIndex() ? OpEnum.LAST : OpEnum.AVG;
			CycleEnum cycle = CycleEnum.cycle(query.getCycle());
			List<KpiSearch> kpiSearches = TcUtil.kpis(k, query.getSrcBeginDate(), query.getSrcEndDate(), cycle, VtypeEnum.V.toString());
//			Map<String, Object> srcDat = hbase.list(kpiSearches);
//			Map<String, Object> srcMergeMap = TcUtil.merge2Obj(srcDat, null);
			Map<String, Object> srcMergeMap = hbase.pulls(kpiSearches, query.getSrcBeginDate(), query.getSrcEndDate(), cycle);
			List<String> kpis = KeyUtils.B_DE_PI_DE_LIST(isComp);
//			Map<String, Object> srcObjMap = TcUtil.toObjMap(srcMergeMap, isComp, kpis);
			int len = DateUtil.between(query.getSrcBeginDate(), query.getSrcEndDate(), cycle);
			Map<PluginKeyVO, Map<String, Object>> mergeMap = TcUtil.pluginJSONMergeNew(srcMergeMap, kpis, len);
			PluginKeyVO pluginKey = new PluginKeyVO(pid, null, null);
			Map<String, Object> pluginMap = mergeMap.get(pluginKey);
			result = null != pluginMap ? pluginMap : result;
			Map<String, BigDecimal> avgMap = TcUtil.objMap2Card(result, op);
//			if (query.isOverDate()) {
				handlerPcRate(avgMap);
//			}
			result.put(RESULT_CARD, avgMap);
			result.put(KEY_OPTIONS, OptionFactory.EY_BAR_PLUGIN_ANALYSIS());
			result.put(KEY_IS_SHOW_COMP, query.isShowComp());
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("[易游-网吧-插件-分析]:{}", e.getMessage());
		}
		return result;
	}
	
	private void handlerPcRate(Map<String, BigDecimal> cardMap) {

		if (null != cardMap && !cardMap.isEmpty()) {

			BigDecimal pc_active = cardMap.get("pc_active");
			BigDecimal pc_reach = cardMap.get("pc_reach");
			BigDecimal pc_start = cardMap.get("pc_start");
			BigDecimal pc_effect = cardMap.get("pc_effect");
			BigDecimal pc_business = cardMap.get("pc_business");

			cardMap.put("pc_rate_fin", TcUtil.v4p(pc_business, pc_active));
			cardMap.put("pc_reach_rate", TcUtil.v4p(pc_reach, pc_active));
			cardMap.put("pc_start_rate", TcUtil.v4p(pc_start, pc_reach));
			cardMap.put("pc_effect_rate", TcUtil.v4p(pc_effect, pc_start));
			cardMap.put("pc_business_rate", TcUtil.v4p(pc_business, pc_effect));
		}
	}
}
