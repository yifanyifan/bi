package com.stnts.tc.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stnts.tc.common.Constants;
import com.stnts.tc.common.CycleEnum;
import com.stnts.tc.common.KeyUtils;
import com.stnts.tc.common.OpEnum;
import com.stnts.tc.common.OptionFactory;
import com.stnts.tc.common.VtypeEnum;
import com.stnts.tc.query.EyBarBarAuditQuery;
import com.stnts.tc.query.EyBarBarChannelQuery;
import com.stnts.tc.service.EyBarBarService;
import com.stnts.tc.utils.HbaseClient;
import com.stnts.tc.utils.TcUtil;
import com.stnts.tc.vo.KpiSearch;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liang.zhang
 * @date 2019年12月12日
 * @desc TODO
 */
@Service("eyBarBarService")
@Slf4j
public class EyBarBarServiceImpl implements EyBarBarService, Constants{
	
	@Autowired
	private HbaseClient hbase;

	@Override
	public Map<String, Object> audit(EyBarBarAuditQuery query) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			log.info("query: {}", query.toString());
			String gid = query.getGid();
			boolean isComp = query.isComp();
			CycleEnum cycle = CycleEnum.cycle(query.getCycle());
			String k = KeyUtils.B_DE_S_AU(gid);
			List<KpiSearch> srcSearches = TcUtil.kpis(k, query.getSrcBeginDate(), query.getSrcEndDate(), cycle, VtypeEnum.V.toString());
//			Map<String, Object> srcDat = hbase.list(srcSearches);
//			Map<String, Object> srcMergeMap = TcUtil.merge2Obj(srcDat, null);
			Map<String, Object> srcMergeMap = hbase.pulls(srcSearches, query.getSrcBeginDate(), query.getSrcEndDate(), cycle);
			List<String> kpis = KeyUtils.B_S_AU_LIST(isComp);
			Map<String, Object> srcObjMap = TcUtil.toObjMap(srcMergeMap, isComp, kpis);
			Object totalList = srcObjMap.get("total_score");
			OpEnum op = query.isIndex() ? OpEnum.LAST : OpEnum.AVG;
			Map<String, BigDecimal> srcAvgMap = TcUtil.objMap2Card(srcObjMap, op);
//			Map<String, BigDecimal> srcAvgMap = TcUtil.objMap2Avg(srcObjMap);
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
			//详情
			List<String> listKpis = KeyUtils.B_S_AU_LIST(true);
			List<Object> details = TcUtil.toObjList(srcMergeMap, listKpis);
			result.put("total_score", totalList);
			result.put("details", details);
			result.put(KEY_OPTIONS, OptionFactory.EY_BAR_BAR_AUDIT());
			result.put(KEY_IS_SHOW_COMP, query.isShowComp());
		} catch (Exception e) {
			log.warn("[易游-网吧-评分-合规]:{}", e.getMessage());
		}
		return result;
	}

	@Override
	public Map<String, Object> channel(EyBarBarChannelQuery query) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			log.info("query: {}", query.toString());
			String gid = query.getGid();
			boolean isComp = query.isComp();
			CycleEnum cycle = CycleEnum.cycle(query.getCycle());
			String k = KeyUtils.B_DE_S_C(gid);
			List<KpiSearch> srcSearches = TcUtil.kpis(k, query.getSrcBeginDate(), query.getSrcEndDate(), cycle, VtypeEnum.V.toString());
//			Map<String, Object> srcDat = hbase.list(srcSearches);
//			Map<String, Object> srcMergeMap = TcUtil.merge2Obj(srcDat, null);
			Map<String, Object> srcMergeMap = hbase.pulls(srcSearches, query.getSrcBeginDate(), query.getSrcEndDate(), cycle);
			List<String> kpis = KeyUtils.B_S_C_LIST(isComp);
			Map<String, Object> srcObjMap = TcUtil.toObjMap(srcMergeMap, isComp, kpis);
//			Map<String, BigDecimal> srcAvgMap = TcUtil.objMap2Avg(srcObjMap);
			OpEnum op = query.isIndex() ? OpEnum.LAST : OpEnum.AVG;
			Map<String, BigDecimal> srcAvgMap = TcUtil.objMap2Card(srcObjMap, op);
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
			//详情
			List<String> listKpis = KeyUtils.B_S_C_LIST(true);
			List<Object> details = TcUtil.toObjList(srcMergeMap, listKpis);
			result.put("details", details);
			result.put(KEY_OPTIONS, OptionFactory.EY_BAR_BAR_CHANNEL());
			result.put(KEY_IS_SHOW_COMP, query.isShowComp());
		} catch (Exception e) {
			log.warn("[易游-网吧-评分-通道]:{}", e.getMessage());
		}
		return result;
	}
}
