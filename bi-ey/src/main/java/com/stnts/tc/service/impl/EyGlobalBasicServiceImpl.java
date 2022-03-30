package com.stnts.tc.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stnts.tc.common.Constants;
import com.stnts.tc.common.CycleEnum;
import com.stnts.tc.common.KpiConfiguration;
import com.stnts.tc.common.OpEnum;
import com.stnts.tc.common.OptionFactory;
import com.stnts.tc.common.VtypeEnum;
import com.stnts.tc.query.EyGlobalBasicBarQuery;
import com.stnts.tc.query.EyGlobalBasicChannelQuery;
import com.stnts.tc.query.EyGlobalBasicProfileQuery;
import com.stnts.tc.service.EyGlobalBasicService;
import com.stnts.tc.utils.DateUtil;
import com.stnts.tc.utils.HbaseClient;
import com.stnts.tc.utils.TcUtil;
import com.stnts.tc.vo.KpiSearch;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liang.zhang
 * @date 2019年11月18日
 * @desc TODO
 */
@Service("eyGlobalBasicService")
@Slf4j
public class EyGlobalBasicServiceImpl implements EyGlobalBasicService, Constants{
	
	@Autowired
	private KpiConfiguration kpiConf;
	
	@Autowired
	private HbaseClient hbase;

	@Override
	public Map<String, Object> eyGlobalBasicProfile(EyGlobalBasicProfileQuery query) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			log.info("query: {}", query.toString());
			List<String> kpis = kpiConf.getEy().getGlobalBasicProfile();
			CycleEnum cycle = CycleEnum.cycle(query.getCycle());
			String[] vtypes = query.isComp() ? new String[] {VtypeEnum.V.toString()} : new String[] {VtypeEnum.V.toString(), VtypeEnum.TV.toString(), VtypeEnum.TR.toString(), VtypeEnum.HV.toString(), VtypeEnum.HR.toString()};
			OpEnum op = query.isIndex() ? OpEnum.LAST : OpEnum.AVG;
			Map<String, Object> srcMap = hbase.gets(kpis, cycle, query.getSrcBeginDate(), query.getSrcEndDate(), op, vtypes);
			Map<String, Object> destMap = query.isComp() ? hbase.gets(kpis, cycle, query.getDestBeginDate(), query.getDestEndDate(), OpEnum.AVG, vtypes) : null;
			result = TcUtil.map2Comp(srcMap, destMap);
			result.put(KEY_IS_SHOW_COMP, query.isShowComp());
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("[易游-全局-基础-概况]：{}", e.getMessage());
		} 
		result.put(KEY_OPTIONS, OptionFactory.EY_GLOBAL_BASIC_PROFILE());
		return result;
	}

	@Override
	public Map<String, Object> eyGlobalBasicChannel(EyGlobalBasicChannelQuery query) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			log.info("query: {}", query.toString());
			List<String> kpis = kpiConf.getEy().getGlobalBasicChannel();
			CycleEnum cycle = CycleEnum.cycle(query.getCycle());
			String[] vtypes = query.isComp() ? new String[] {VtypeEnum.V.toString()} : new String[] {VtypeEnum.V.toString(), VtypeEnum.TV.toString(), VtypeEnum.TR.toString(), VtypeEnum.HV.toString(), VtypeEnum.HR.toString()};
			OpEnum op = query.isIndex() ? OpEnum.LAST : OpEnum.AVG;
			Map<String, Object> srcMap = hbase.gets(kpis, cycle, query.getSrcBeginDate(), query.getSrcEndDate(), op, vtypes);
			Map<String, Object> destMap = query.isComp() ? hbase.gets(kpis, cycle, query.getDestBeginDate(), query.getDestEndDate(), OpEnum.AVG, vtypes) : null;
			Map<String, Object> resultMap = TcUtil.map2Comp(srcMap, destMap);
			result = null != resultMap ? resultMap : result;
			//加载6月均值  只需要最近一天的数据
			String[] appendVtyps = new String[] {VtypeEnum.AV.toString()};
			String yesterday = DateUtil.yesterday2str();
//			yesterday = "2019-01-01";  //TEST
			Map<String, Object> avMap = hbase.gets(kpis, CycleEnum.DAY, yesterday, yesterday, OpEnum.LAST, appendVtyps);
			handlerAr(result, avMap, query.isComp());
			result.put(KEY_IS_SHOW_COMP, query.isShowComp());
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("[易游-全局-基础-通道]：{}", e.getMessage());
		} 
		result.put(KEY_OPTIONS, OptionFactory.EY_GLOBAL_BASIC_CHANNEL());
		return result;
	}

	/**
	 * 处理6月均值  比率的数据
	 * @param result
	 * @param avMapToo
	 * @param comp
	 */
	@SuppressWarnings("unchecked")
	private void handlerAr(Map<String, Object> result, Map<String, Object> avMap, boolean comp) {
		
		Map<String, BigDecimal> cardMap = (Map<String, BigDecimal>) result.get(RESULT_CARD);
		if(null == cardMap) {  //数据为空也要展示6月均值,，已约定放在card中不可更改
			cardMap = new HashMap<String, BigDecimal>();
			result.put(RESULT_CARD, cardMap);
		}
		if(null != avMap && !avMap.isEmpty()) {
			
			Map<String, BigDecimal> card = (Map<String, BigDecimal>) avMap.get(RESULT_CARD);
			
			Set<Entry<String, BigDecimal>> entrys = card.entrySet();
			for(Iterator<Entry<String, BigDecimal>> it = entrys.iterator() ; it.hasNext() ; ) {
				
				Entry<String, BigDecimal> entry = it.next();
				String avKey = entry.getKey();
				String vKey = avKey.replace("_AV", "_V");
				String arKey = avKey.replace("_AV", "_AR");
				BigDecimal v = null;
				if(comp) {
					Map<String, BigDecimal> subCardMap = (Map<String, BigDecimal>) cardMap.get(vKey);
					v = null != subCardMap && !subCardMap.isEmpty() ? subCardMap.get(KEY_SRC) : null;
				}else {
					v = cardMap.get(vKey);;
				}
				BigDecimal av = entry.getValue();
				BigDecimal ar = (null != v && null != av && av.compareTo(BigDecimal.ZERO) != 0) ? TcUtil.v2p(v, av) : null;
				cardMap.put(avKey, av);
				cardMap.put(arKey, ar);
			}
		}
	}

	@Override
	public Map<String, Object> eyGlobalBasicBar(EyGlobalBasicBarQuery query) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			log.info("query: {}", query.toString());
			List<KpiSearch> kpiSearches = new ArrayList<KpiSearch>();
			List<String> scoreKpis = new ArrayList<String>();
			//初始化合规网吧KEY集合
			List<String> bsauKeys = TcUtil.initScoreKey("B_S_AU_%s");
			scoreKpis.addAll(bsauKeys);
			//初始化合规PC KEY集合
			List<String> psauKeys = TcUtil.initScoreKey("P_S_AU_%s");
			scoreKpis.addAll(psauKeys);
			//初始化通道评分网吧 KEY集合
			List<String> bscKeys = TcUtil.initScoreKey("B_S_C_%s");
			scoreKpis.addAll(bscKeys);
			//初始化通道评分PC KEY集合
			List<String> pscKeys = TcUtil.initScoreKey("P_S_C_%s");
			scoreKpis.addAll(pscKeys);
			for(String kpi : scoreKpis) {
				kpiSearches.addAll(TcUtil.kpis(kpi, query.getSrcEndDate(), query.getSrcEndDate(), CycleEnum.DAY, VtypeEnum.V.toString()));
			}
			//等级
			List<String> levelKpis = TcUtil.initLevelKeys();
			for(String kpi : levelKpis) {
				kpiSearches.addAll(TcUtil.kpis(kpi, query.getSrcBeginDate(), query.getSrcEndDate(), CycleEnum.DAY, VtypeEnum.V.toString(), VtypeEnum.R.toString()));
			}
			
			result = hbase.pulls(kpiSearches, null, true, query.getSrcBeginDate(), query.getSrcEndDate(), CycleEnum.cycle(query.getCycle()));
			if(null != result && !result.isEmpty()) {
				//必须进一步处理  将分值合并到一个key中
				toList(result, "B_S_AU_V", "B_S_AU_%s_V");
				toList(result, "P_S_AU_V", "P_S_AU_%s_V");
				toList(result, "B_S_C_V", "B_S_C_%s_V");
				toList(result, "P_S_C_V", "P_S_C_%s_V");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("[易游-全局-基础-网吧]：{}", e.getMessage());
		}
		result.put(KEY_OPTIONS, OptionFactory.EY_GLOBAL_BASIC_SCORE());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private void toList(Map<String, Object> result, String kpi, String keyTemplate){
		
		List<Object> vs = new ArrayList<Object>();
		for(int i = 0 ; i <= 100 ; i++) { 
			
			String key = String.format(keyTemplate, i);
			List<BigDecimal> subVs = (List<BigDecimal>) result.remove(key);
			if(null != subVs && !subVs.isEmpty()) {
				vs.add(subVs.get(subVs.size() - 1));
			}else {
				vs.add(null);
			}
		}
		result.put(kpi, vs);
	}
}
