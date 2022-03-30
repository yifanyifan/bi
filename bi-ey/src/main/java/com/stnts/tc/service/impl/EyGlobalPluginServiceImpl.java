package com.stnts.tc.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.stnts.tc.common.Constants;
import com.stnts.tc.common.CycleEnum;
import com.stnts.tc.common.KeyConfiguration;
import com.stnts.tc.common.KeyUtils;
import com.stnts.tc.common.KpiConfiguration;
import com.stnts.tc.common.OpEnum;
import com.stnts.tc.common.OptionFactory;
import com.stnts.tc.common.VtypeEnum;
import com.stnts.tc.query.EyGlobalPluginAnalysisQuery;
import com.stnts.tc.query.EyGlobalPluginProfileQuery;
import com.stnts.tc.service.EyGlobalPluginService;
import com.stnts.tc.utils.HbaseClient;
import com.stnts.tc.utils.TcUtil;
import com.stnts.tc.vo.KpiSearch;
import com.stnts.tc.vo.Option;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liang.zhang
 * @date 2019年11月28日
 * @desc TODO 易游-全局-插件
 */
@Service("eyGlobalPluginService")
@Slf4j
public class EyGlobalPluginServiceImpl implements EyGlobalPluginService, Constants {

	@Autowired
	private KpiConfiguration kpiConf;

	@Autowired
	private HbaseClient hbase;

	@Autowired
	private KeyConfiguration keyConf;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Override
	public Map<String, Object> eyGlobalPluginProfile(EyGlobalPluginProfileQuery query) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			log.info("query: {}", query.toString());
			List<String> kpis = kpiConf.getEy().getGlobalPluginProfile();
			CycleEnum cycle = CycleEnum.cycle(query.getCycle());
			String[] vtypes = query.isComp() ? new String[] { VtypeEnum.V.toString() }
					: new String[] { VtypeEnum.V.toString(), VtypeEnum.TV.toString(), VtypeEnum.TR.toString(),
							VtypeEnum.HV.toString(), VtypeEnum.HR.toString() };
			OpEnum op = query.isIndex() ? OpEnum.LAST : OpEnum.AVG;
			Map<String, Object> srcMap = hbase.gets(kpis, cycle, query.getSrcBeginDate(), query.getSrcEndDate(), op,
					vtypes);
			Map<String, Object> destMap = query.isComp()
					? hbase.gets(kpis, cycle, query.getDestBeginDate(), query.getDestEndDate(), OpEnum.AVG, vtypes)
					: null;
			result = TcUtil.map2Comp(srcMap, destMap);

			// 加载插件数据 
			String pluginSetKey = kpiConf.getEy().getPluginSetKey();
			System.out.println(pluginSetKey);
			// 这里可以优化为查询插件索引表（不及时）
			Set<String> pluginIds = redisTemplate.opsForSet().members(pluginSetKey);
			if (null != pluginIds && !pluginIds.isEmpty()) {

				List<String> pluginKpis = TcUtil.initPluginKey(pluginIds);
				List<KpiSearch> kpiSearches = new ArrayList<KpiSearch>();
				for (String pluginKpi : pluginKpis) {
					kpiSearches.addAll(TcUtil.kpis(pluginKpi, query.getSrcBeginDate(), query.getSrcEndDate(), cycle,
							VtypeEnum.V.toString()));
				}
				Map<String, Object> pMergeMap = hbase.pulls(kpiSearches, query.getSrcBeginDate(), query.getSrcEndDate(),
						cycle);
//				Map<String, Object> pMergeMap = TcUtil.merge2Obj(pDat, query.getSrcBeginDate(), query.getSrcEndDate(), cycle);
				List<String> jsonKpis = KeyUtils.PI_LIST(true);
				Map<String, List<Map<String, Object>>> plugins = TcUtil.pluginJson2Map2(pMergeMap, jsonKpis);

				result.put("plugins", plugins);
			} else {
				log.info("pluginIds is empty!!!");
			}
			result.put(KEY_IS_SHOW_COMP, query.isShowComp());
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("[易游-全局-插件-概览]: {}", e.getMessage());
		}
		Map<String, List<Option>> optionMap = OptionFactory.EY_GLOBAL_PLUGIN_PROFILE();
		optionMap.put(KEY_TABLE_OPTIONS, keyConf.getEyGlobalPluginProfile());
		result.put(KEY_OPTIONS, optionMap);

		return result;
	}

	@Override
	public Map<String, Object> eyGlobalPluginAnalysis(EyGlobalPluginAnalysisQuery query) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {

			log.info("query: {}", query.toString());
			String pid = query.getPid();
			boolean isComp = query.isComp();
			CycleEnum cycle = CycleEnum.cycle(query.getCycle());
			String k = KeyUtils.PI(pid);
			List<KpiSearch> kpiSearches = TcUtil.kpis(k, query.getSrcBeginDate(), query.getSrcEndDate(), cycle,
					VtypeEnum.V.toString());
//			Map<String, Object> srcDat = hbase.list(kpiSearches);
			Map<String, Object> srcMergeMap = hbase.pulls(kpiSearches, query.getSrcBeginDate(), query.getSrcEndDate(),
					cycle);
//			Map<String, Object> srcMergeMap = TcUtil.merge2Obj(srcDat, null);
			List<String> jsonKpis = KeyUtils.PI_LIST(isComp);
			Map<String, Object> objMap = TcUtil.toObjMap(srcMergeMap, jsonKpis);
			OpEnum op = query.isIndex() ? OpEnum.LAST : OpEnum.AVG;
//			Map<String, BigDecimal> avgMap = TcUtil.objMap2Avg(objMap);
			Map<String, BigDecimal> cardMap = TcUtil.objMap2Card(objMap, op);
			// 计算网吧各指标比率
			handlerBarRate(cardMap);
//			if (query.isOverDate()) {
			handlerPcRate(cardMap);
//			}
			result = objMap;
			result.put(KEY_OPTIONS, OptionFactory.EY_GLOBAL_PLUGIN_ANALYSIS());
			result.put(RESULT_CARD, cardMap);
			result.put(KEY_IS_SHOW_COMP, query.isShowComp());
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("[易游-全局-插件-分析]: {}", e.getMessage());
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

	private void handlerBarRate(Map<String, BigDecimal> cardMap) {

		if (null != cardMap && !cardMap.isEmpty()) {

			BigDecimal bar_active = cardMap.get("bar_active");
			BigDecimal bar_reach = cardMap.get("bar_reach");
			BigDecimal bar_start = cardMap.get("bar_start");
			BigDecimal bar_effect = cardMap.get("bar_effect");
			BigDecimal bar_business = cardMap.get("bar_business");

			cardMap.put("bar_reach_rate", TcUtil.v4p(bar_reach, bar_active));
			cardMap.put("bar_start_rate", TcUtil.v4p(bar_start, bar_reach));
			cardMap.put("bar_effect_rate", TcUtil.v4p(bar_effect, bar_start));
			cardMap.put("bar_business_rate", TcUtil.v4p(bar_business, bar_effect));
		}
	}
}
