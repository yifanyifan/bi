package com.stnts.tc.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.stnts.tc.common.Constants;
import com.stnts.tc.common.CycleEnum;
import com.stnts.tc.common.KeyUtils;
import com.stnts.tc.common.KpiConfiguration;
import com.stnts.tc.common.OpEnum;
import com.stnts.tc.common.OptionFactory;
import com.stnts.tc.common.VtypeEnum;
import com.stnts.tc.query.EyGlobalBarNewQuery;
import com.stnts.tc.query.EyGlobalBarReQuery;
import com.stnts.tc.service.EyGlobalBarService;
import com.stnts.tc.utils.DateUtil;
import com.stnts.tc.utils.HbaseClient;
import com.stnts.tc.utils.TcUtil;
import com.stnts.tc.vo.BarGetVO;
import com.stnts.tc.vo.KpiSearch;
import com.stnts.tc.vo.ObjectValue;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liang.zhang
 * @date 2019年11月28日
 * @desc TODO
 */
@Service("eyGlobalBarService")
@Slf4j
public class EyGlobalBarServiceImpl implements EyGlobalBarService, Constants{
	
	@Autowired
	private KpiConfiguration kpiConf;
	
	@Autowired
	private HbaseClient hbase;

	@Override
	public Map<String, Object> eyGlobalBarNew(EyGlobalBarNewQuery query) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			log.info("query: {}", query.toString());
			List<String> kpis = kpiConf.getEy().getGlobalBarNewly();
			CycleEnum cycle = CycleEnum.cycle(query.getCycle());
			List<KpiSearch> kpiSearches = new ArrayList<KpiSearch>();
			String[] vtypes = query.isComp() ? new String[] {VtypeEnum.V.toString()} : new String[] {VtypeEnum.V.toString(), VtypeEnum.TV.toString(), VtypeEnum.TR.toString(), VtypeEnum.HV.toString(), VtypeEnum.HR.toString()};
			for(String k : kpis) {
				kpiSearches.addAll(TcUtil.kpis(k, query.getSrcBeginDate(), query.getSrcEndDate(), cycle, vtypes));
			}
			OpEnum op = query.isIndex() ? OpEnum.LAST : OpEnum.SUM;
			//新增网吧数  安装PC数
			Map<String, Object> srcMap = hbase.pulls(kpiSearches, op, true, query.getSrcBeginDate(), query.getSrcEndDate(), cycle);
			if(query.isComp()) {
				
				List<KpiSearch> destKpiSearches = new ArrayList<KpiSearch>();
				for(String k : kpis) {
					destKpiSearches.addAll(TcUtil.kpis(k, query.getDestBeginDate(), query.getDestEndDate(), cycle, vtypes));
				}
				Map<String, Object> destMap = hbase.pulls(destKpiSearches, OpEnum.SUM, true, query.getDestBeginDate(), query.getDestEndDate(), cycle);
				result = TcUtil.map2Comp(srcMap, destMap);
			}else {
				result = srcMap;
			}
			//查新增网吧数
			String beginDate = query.isIndex() ? query.getSrcEndDate() : query.getSrcBeginDate();
			List<KpiSearch> newlySearches = TcUtil.kpis("B_N_DE", beginDate, query.getSrcEndDate(), cycle, VtypeEnum.V.toString());
			Map<String, Object> newlyMap = hbase.list(newlySearches);
			//mergeMap = <2019-01-01 List<gid>>
			Map<String, List<Object>> mergeMap = TcUtil.merge4Bar(newlyMap);
			
			//加载基础信息<gid, json>
			Map<String, JSONObject> basicMap = loadBasic(mergeMap);
			//加载基础指标<gid, json>
			Map<String, JSONObject> kpiMap = loadKpi(mergeMap, cycle);
			
			List<JSONObject> barList = new ArrayList<JSONObject>();
			Set<Entry<String,List<Object>>> entrys = mergeMap.entrySet();
			for(Iterator<Entry<String,List<Object>>> it = entrys.iterator() ; it.hasNext() ; ) {
				
				Entry<String,List<Object>> entry = it.next();
				String date = entry.getKey();
				List<Object> gids = entry.getValue();
				if(null != gids && !gids.isEmpty()) {
					List<JSONObject> subList = gids.stream().map(gid -> {
						String _gid = String.valueOf(gid);
						JSONObject basicObj = basicMap.get(_gid);
						if(null == basicObj) {
							basicObj = new JSONObject();
							basicObj.put("gid", _gid);
						}
						basicObj.put("date", date);
						if(null != kpiMap && !kpiMap.isEmpty()) {
							JSONObject kpiObj = kpiMap.get(_gid);
							basicObj.put("szl", kpiObj.get("szl"));
							basicObj.put("pc_build", kpiObj.get("pc_build"));
						}
						return basicObj;
					}).collect(Collectors.toList());
					barList.addAll(subList);
				}
			}
			barList.sort(new Comparator<JSONObject>() {

				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					
					String d1str = o1.getString("date");
					String d2str = o2.getString("date");
					int d1int = TcUtil.date2int(d1str);
					int d2int = TcUtil.date2int(d2str);
					if(d1int < d2int) {
						return -1;
					}else if(d1int > d2int) {
						return 1;
					}else {
						return 0;
					}
//					Date d1 = o1.getDate("date");
//					Date d2 = o2.getDate("date");
//					if(d1.before(d2)){
//						return -1;
//					}else if(d1.after(d2)){
//						return 1;
//					}else {
//						return 0;
//					}
				}
			});
			result.put(RESULT_NEWLY, barList);
			result.put(KEY_IS_SHOW_COMP, query.isShowComp());
			result.put(KEY_OPTIONS, OptionFactory.EY_GLOBAL_BAR_NEW());
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("[易游-全局-网吧-新增]: {}", e.getMessage());
		}
		return result;
	}
	
	/**
	 * 查询网吧基本信息
	 * @return
	 */
	private Map<String, JSONObject> loadBasic(Map<String, List<Object>> dat){

		Map<String, JSONObject> map = new HashMap<>();
		try {
			
			if(null != dat && !dat.isEmpty()) {
				
				List<Integer> gids = dat.values().stream().flatMapToInt(subList -> subList.stream().mapToInt(item -> Integer.parseInt(String.valueOf(item)))).distinct().boxed().collect(Collectors.toList());
				List<String> rowkeys = gids.stream().map(String::valueOf).map(KeyUtils::B_DE_BA).collect(Collectors.toList());
//						.map(gid -> {
//					String kpi = KeyUtils.B_DE_BA(String.valueOf(gid));
//					return kpi;
//				})
				map = hbase.getsForBar(rowkeys, B_DE_BA_F, B_DE_BA_C);
			}
		} catch (Exception e) {
			log.warn("loadBasic failed, msg: {}", e.getMessage());
		}
		return map;
	}
	
	/**
	 * 加载基础指标信息
	 * @param dat
	 * @param cycle 
	 * @return
	 */
	private Map<String, JSONObject> loadKpi(Map<String, List<Object>> dat, CycleEnum cycle){
		
		Map<String, JSONObject> map = null;
		try {
			
			if(null != dat && !dat.isEmpty()) {
				
				List<BarGetVO> vos = new ArrayList<BarGetVO>();
				Set<Entry<String, List<Object>>> entrys = dat.entrySet();
				for(Iterator<Entry<String, List<Object>>> it = entrys.iterator() ; it.hasNext() ;) {
					
					Entry<String, List<Object>> entry = it.next();
					String date = entry.getKey();
					List<Object> vs = entry.getValue();
					/**
					 * .filter(v -> null != v && StringUtils.isNotBlank(String.valueOf(v))).map(v -> {
						return JSON.parseArray(String.valueOf(v)).toArray();
					}).flatMap(Arrays::stream)
					 */
					List<BarGetVO> subVos = vs.stream().map(gid -> {
						try {
							String year = StringUtils.substring(date, 2, 4);
							String rowkey = String.format("%s_B_DE_K_%s_V_%s", year, gid, cycle.getCycle());
							int col = cycle.compareTo(CycleEnum.DAY) == 0 ? DateUtil.dayOfYear(date) : Integer.parseInt(date.split("-")[1]);
							return new BarGetVO(rowkey, String.valueOf(col));
						} catch (Exception e) {
						}
						return null;
					}).filter(Objects::nonNull).collect(Collectors.toList());
					
					vos.addAll(subVos);
				}
				if(!vos.isEmpty()) {
					map = hbase.getsForBar(vos);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("loadBasic failed, msg: {}", e.getMessage());
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> eyGlobalBarRetention(EyGlobalBarReQuery query) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			log.info("query: {}", query.toString());
			//beginDate:2018-11 -> 2018-11  2018-12  2019-1  
			String srcBeginDate = query.getSrcBeginDate();
			int type = query.getType();
			List<KpiSearch> kpiSearches = initKpiSearchs(srcBeginDate, type);
			//<String, List<Value>>
			Map<String, Object> dat = hbase.pulls(kpiSearches, null, false, query.getSrcBeginDate(), query.getSrcEndDate(), CycleEnum.cycle(query.getCycle()));
			fillDat(kpiSearches, dat);//可能留存的key在hbase都不存在
			List<Object> part1 = new ArrayList<Object>();
			Set<Entry<String, Object>> entrys = dat.entrySet();
			List<List<Object>> part3 = new ArrayList<List<Object>>();
			for(Iterator<Entry<String, Object>> it = entrys.iterator() ; it.hasNext() ;) {
				
				Entry<String, Object> entry = it.next();
				//19_P_RE_1_R_M
				String kpi = entry.getKey();
				List<ObjectValue> vs = (List<ObjectValue>) entry.getValue();
				vs.sort(new Comparator<ObjectValue>() {

					@Override
					public int compare(ObjectValue o1, ObjectValue o2) {
						if(o1.getCol() < o2.getCol()) {
							return -1;
						}else if(o1.getCol() > o2.getCol()) {
							return 1;
						}else {
							return 0;
						}
					}
				});
				String[] kArr = StringUtils.split(kpi, Constants.KEY_SPLIT);
				String k = String.format("%s-%s", kArr[0], kArr[3]);
				List<Object> _values = new ArrayList<Object>();
				_values.add(k);
				for(ObjectValue v : vs) {
					_values.add(v.getV2BigDecimal());
				}
				part3.add(_values);
			}
			result.put("part1", part1);
			part3.sort(new Comparator<List<Object>>() {

				@Override
				public int compare(List<Object> o1, List<Object> o2) {
					try {
						String srcDateStr = String.valueOf(o1.get(0));
						String destDateStr = String.valueOf(o2.get(0));
						Date srcDate = new SimpleDateFormat("yyyy-MM").parse(srcDateStr);
						Date destDate = new SimpleDateFormat("yyyy-MM").parse(destDateStr);
						if(srcDate.before(destDate)) {
							return -1;
						}else if(srcDate.after(destDate)) {
							return 1;
						}else {
							return 0;
						}
					} catch (ParseException e) {
					}
					return 0;
				}
			});
			result.put("part3", part3);
			List<Object> oneList = part3.get(0);
			part1.addAll(oneList);
			part1.remove(0);  //月份
			part1.remove(0);  //新增
			List<Object[]> part2 = new ArrayList<Object[]>();
			for(int i = 0 ; i < 12 ; i++) {
				BigDecimal[] reArr = new BigDecimal[12];
				Arrays.fill(reArr, null);
				part2.add(reArr);
			}
			int index = 0;
			for(Iterator<List<Object>> it = part3.iterator() ; it.hasNext() ; ) {
				List<Object> vs = it.next();
				//0\1不是留存数据 忽略
				for(int i  = 2 ; i < vs.size() ; i++) {
					//i=1表示1月留存
					int k = i - 2;//k=0表示1月留存曲线
					if(k > 11) {
						break;
					}
					Object[] reArr = part2.get(k);
					Object value = new BigDecimal(String.valueOf(vs.get(i)));
					reArr[index] = value;
				}
				index++;
			}
			List<Object[]> _part2 = new ArrayList<Object[]>();
			for(Iterator<Object[]> it = part2.iterator() ; it.hasNext() ;) {
				
				Object[] arr = it.next();
				int len = len(arr);
				if(len == 0) {
					break;
				}
				Object[] _arr = new Object[len];
				System.arraycopy(arr, 0, _arr, 0, len);
				_part2.add(_arr);
			}
			result.put("part2", _part2);
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("[易游-全局-网吧-留存]: {}", e.getMessage());
		}
		return result;
	}
	
	/**
	 * 填充数据
	 * @param kpiSearches 
	 * @param dat
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> fillDat(List<KpiSearch> kpiSearches, Map<String, Object> dat) {
		
		if(null != dat && !dat.isEmpty()) {
			
			Map<String, Object> fullDat = new HashMap<String, Object>();
			kpiSearches.stream().map(KpiSearch::getRowkey).forEach(rowkey -> {
				
				ObjectValue[] ovs = new ObjectValue[13];
				String[] params = rowkey.split(Constants.KEY_SPLIT);
				for(int i = 0 ; i < 13; i++) {
					ovs[i] = new ObjectValue(Integer.parseInt(params[0]), i, CycleEnum.MONTH.getCycle(), null, i);
				}
				fullDat.put(rowkey, ovs);
			});
			
			dat.entrySet().stream().forEach(entry -> {
				String key = entry.getKey();
				List<ObjectValue> ovList = (List<ObjectValue>) entry.getValue();
				ovList.forEach(ov -> {
					ObjectValue[] ovArr = (ObjectValue[]) fullDat.get(key);
					ovArr[ov.getCol()] = ov;
				});
			});
			
			return fullDat;
		}
		return dat;
	}

	private int len(Object[] objs) {
		
		return (int)Arrays.asList(objs).stream().filter(obj -> {
			BigDecimal v = (BigDecimal)obj;
			return v != BigDecimal.TEN;
		}).count();
	}

	private List<KpiSearch> initKpiSearchs(String srcBeginDate, int type) {
		
		List<KpiSearch> kpiSearchs = new ArrayList<KpiSearch>();
		String[] dateArr = StringUtils.split(srcBeginDate, "-");
		int year = Integer.parseInt(dateArr[0]);
		int month = Integer.parseInt(dateArr[1]);
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, 0);
		Date d = new Date();
		for(int i = 0; i < 12 ; i++) {
			//0表示当月 1表示下一个月
			int j = i == 0 ? i : 1;
			cal.add(Calendar.MONTH, j);
			Date _d = cal.getTime();
			if(_d.after(d)) {
				break;
			}
			int _year = cal.get(Calendar.YEAR);
			int _month = cal.get(Calendar.MONTH) + 1;
			String prefix = String.valueOf(_year).substring(2);
			String _type = type == 1 ? "B" : "P";
			//19_P_RE_1_R_M
			String kpi = String.format("%s_%s_RE_%s_R_M", prefix, _type, _month);
			kpiSearchs.add(new KpiSearch(kpi, 0, 12));  //这个12可以配置 算了
		}
		return kpiSearchs;
	}

	/**
	 * 初始化要查询的key集合
	 * @param srcBeginDate
	 * @return
	 */
//	private List<String> initKpis(String srcBeginDate, int type) {
//		
//		List<String> kpis = new ArrayList<String>();
//		System.out.println(srcBeginDate);
//		String[] dateArr = StringUtils.split(srcBeginDate, "-");
//		int year = Integer.parseInt(dateArr[0]);
//		int month = Integer.parseInt(dateArr[1]);
//		Calendar cal = Calendar.getInstance();
//		cal.set(year, month, 0);
//		Date d = new Date();
//		for(int i = 0; i <= 12 ; i++) {
//			//0表示当月 1表示下一个月
//			int j = i == 0 ? i : 1;
//			cal.add(Calendar.MONTH, j);
//			Date _d = cal.getTime();
//			if(_d.after(d)) {
//				break;
//			}
//			int _year = cal.get(Calendar.YEAR);
//			int _month = cal.get(Calendar.MONTH) + 1;
//			String prefix = String.valueOf(_year).substring(2);
//			String _type = type == 1 ? "B" : "P";
//			//19_P_RE_1_R_M
//			String kpi = String.format("%s_%s_RE_%s_R_M", prefix, _type, _month);
//			kpis.add(kpi);
//		}
//		return kpis;
//	}
}
