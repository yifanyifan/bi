package com.stnts.tc.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stnts.tc.common.Constants;
import com.stnts.tc.common.CycleEnum;
import com.stnts.tc.common.HbaseConfiguration;
import com.stnts.tc.common.KeyConfiguration;
import com.stnts.tc.common.KeyUtils;
import com.stnts.tc.common.OpEnum;
import com.stnts.tc.common.OptionFactory;
import com.stnts.tc.common.VtypeEnum;
import com.stnts.tc.query.EyBarBasicBaseQuery;
import com.stnts.tc.query.EyBarBasicChannelQuery;
import com.stnts.tc.query.EyBarBasicKpiQuery;
import com.stnts.tc.service.EyBarBasicService;
import com.stnts.tc.utils.DateUtil;
import com.stnts.tc.utils.HbaseClient;
import com.stnts.tc.utils.TcUtil;
import com.stnts.tc.vo.KpiSearch;

import lombok.extern.slf4j.Slf4j;

@Service("eyBarBasicService")
@Slf4j
public class EyBarBasicServiceImpl implements EyBarBasicService, Constants{
	
	@Autowired
	private HbaseConfiguration hbaseConf;
	
	@Autowired
	private HbaseClient hbase;
	
	@Autowired
	private KeyConfiguration keyConf;

	@Override
	public Map<String, Object> base(EyBarBasicBaseQuery query) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			log.info("query: {}", query.toString());
			String date = query.getDate();
			String gid = query.getGid();
			//先取网吧信息
			String barRowkey = KeyUtils.B_DE_BA(gid);
			String barInfoStr = hbase.get(barRowkey, B_DE_BA_F, B_DE_BA_C, hbaseConf.getBarInfoTable());
			JSONObject barInfo = new JSONObject();
			barInfo = StringUtils.isNotBlank(barInfoStr) ? JSON.parseObject(barInfoStr) : barInfo;
			result.put("base", barInfo);
			
			int col = DateUtil.dayOfYear(date);
			String year = date.substring(2, 4);  //2019-09-09
			String envRowkey = KeyUtils.B_DE_ENV(year, gid);
			String envInfoStr = hbase.get(envRowkey, TABLE_KPI_FAMILY, String.valueOf(col), hbaseConf.getTableName());
			JSONObject envInfo = new JSONObject();
			envInfo = StringUtils.isNotBlank(envInfoStr) ? JSON.parseObject(envInfoStr) : envInfo;
			result.put("env", envInfo);
			//再取网吧环境信息
		} catch (Exception e) {
			log.warn("[易游-网吧-基础-基本]:{}", e.getMessage());
		}
		return result;
	}

	@Override
	public Map<String, Object> kpi(EyBarBasicKpiQuery query) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			log.info("query: {}", query.toString());
			String gid = query.getGid();
			String k = KeyUtils.B_DE_K(gid);
			CycleEnum cycle = CycleEnum.cycle(query.getCycle());
			List<KpiSearch> kpiSearches = TcUtil.kpis(k, query.getSrcBeginDate(), query.getSrcEndDate(), cycle, VtypeEnum.V.toString());
//			Map<String, Object> srcDat = hbase.list(kpiSearches);
//			//mergeSrcDat = B_DE_K_V, List<JSON>
//			Map<String, Object> mergeSrcDat = TcUtil.merge2Obj(srcDat, null);
			Map<String, Object> mergeSrcDat = hbase.pulls(kpiSearches, query.getSrcBeginDate(), query.getSrcEndDate(), cycle);
			boolean isComp = query.isComp();
			//第一维是指标项  第二维是指标值数组
			//key = kpi   object = list<值>
			List<String> kpis = KeyUtils.B_DE_K_LIST(isComp);
			Map<String, Object> srcObjMap = TcUtil.toObjMap(mergeSrcDat, isComp, kpis);
			OpEnum op = query.isIndex() ? OpEnum.LAST : OpEnum.AVG;
			Map<String, BigDecimal> srcAvgMap = TcUtil.objMap2Card(srcObjMap, op);
//			Map<String, BigDecimal> srcAvgMap = TcUtil.objMap2Avg(srcObjMap);
			result = null != srcObjMap ? srcObjMap : result;
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
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("[易游-网吧-基础-指标]:{}", e.getMessage());
		}
		result.put(KEY_OPTIONS, keyConf.getEyBarBasicKpi());
		result.put(KEY_IS_SHOW_COMP, query.isShowComp());
		return result;
	}
	
	@Override
	public Map<String, Object> channel(EyBarBasicChannelQuery query) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			log.info("query: {}", query.toString());
			String gid = query.getGid();
			String k = KeyUtils.B_DE_C(gid);
			CycleEnum cycle = CycleEnum.cycle(query.getCycle());
			List<KpiSearch> kpiSearches = TcUtil.kpis(k, query.getSrcBeginDate(), query.getSrcEndDate(), cycle, VtypeEnum.V.toString());
//			Map<String, Object> srcDat = hbase.list(kpiSearches);
//			//mergeSrcDat = B_DE_K_V, List<JSON>
//			Map<String, Object> mergeSrcDat = TcUtil.merge2Obj(srcDat, null);
			Map<String, Object> mergeSrcDat = hbase.pulls(kpiSearches, query.getSrcBeginDate(), query.getSrcEndDate(), cycle);
			boolean isComp = query.isComp();
			//第一维是指标项  第二维是指标值数组
			//key = kpi   object = list<值>
			List<String> kpis = KeyUtils.B_DE_C_LIST(isComp);
			Map<String, Object> srcObjMap = TcUtil.toObjMap(mergeSrcDat, isComp, kpis);
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
			
			//加载6月均值  只需要最近一天的数据
			String[] appendVtyps = new String[] {VtypeEnum.AV.toString()};
			String yesterday = DateUtil.yesterday2str();
//			yesterday = "2019-01-01";  //TEST
			List<String> mKpis = KeyUtils.EY_BAR_CHL_LIST(gid);
//			Map<String, Object> avMap = hbase.pulls(appendKpiSearches, null, false, yesterday, yesterday, CycleEnum.DAY);
			Map<String, Object> avMap = hbase.gets(mKpis, CycleEnum.DAY, yesterday, yesterday, OpEnum.LAST, appendVtyps);
			handlerAr(result, avMap, query.isComp());
			result.put(KEY_OPTIONS, OptionFactory.EY_BAR_BASIC_CHANNEL());
			result.put(KEY_IS_SHOW_COMP, query.isShowComp());
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("[易游-网吧-基础-通道]:{}", e.getMessage());
		}
		return result;
	}
	
	/**
	 * 处理6月均值  比率的数据
	 * 这里网吧6月均值没有延续之前网吧设计风格，数据已经写了，就这样了
	 * @param result
	 * @param avMapToo
	 * @param comp
	 */
	@SuppressWarnings("unchecked")
	private void handlerAr(Map<String, Object> result, Map<String, Object> avMap, boolean comp) {
		
		Map<String, BigDecimal> cardMap = (Map<String, BigDecimal>) result.get(RESULT_CARD);
		if(null == cardMap) {
			cardMap = new HashMap<String, BigDecimal>();
			result.put(RESULT_CARD, cardMap);
		}
		if(null != avMap && !avMap.isEmpty()) {
			
			Map<String, String> keyMap = KeyUtils.EY_BAR_CHL_MAP();
			Map<String, BigDecimal> card = (Map<String, BigDecimal>) avMap.get(RESULT_CARD);
			Set<Entry<String, BigDecimal>> entrys = card.entrySet();
			for(Iterator<Entry<String, BigDecimal>> it = entrys.iterator() ; it.hasNext() ; ) {
				
				Entry<String, BigDecimal> entry = it.next();
				//entry是C_O_R_ER_10849429_AV_D这种key
				String avKey = entry.getKey();
				String[] avKeyArr = avKey.split(KEY_SPLIT);
				List<String> newAvKeyList = new ArrayList<String>();
				for(int i = 0 ; i < avKeyArr.length ; i++) {
					if(i != avKeyArr.length - 2) {
						newAvKeyList.add(avKeyArr[i]);
					}
				}
				String avKeySrc = StringUtils.join(newAvKeyList, KEY_SPLIT);
				String vKey = keyMap.get(avKeySrc);
				String _avKey = vKey.concat("_av");  //保持跟json风格一致
				String arKey = vKey.concat("_ar");
				BigDecimal v = null;
				if(comp) {
					Map<String, BigDecimal> subCardMap = (Map<String, BigDecimal>) cardMap.get(vKey);
					v = null != subCardMap && !subCardMap.isEmpty() ? subCardMap.get(KEY_SRC) : null;
				}else {
					v = cardMap.get(vKey);;
				}
				BigDecimal av = entry.getValue();
				BigDecimal ar = (null != v && null != av && av.compareTo(BigDecimal.ZERO) != 0) ? TcUtil.v2p(v, av) : null;
				cardMap.put(_avKey, av);
				cardMap.put(arKey, ar);
			}
		}
	}
}
