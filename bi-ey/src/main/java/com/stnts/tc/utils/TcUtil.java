package com.stnts.tc.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stnts.tc.common.Constants;
import com.stnts.tc.common.CycleEnum;
import com.stnts.tc.common.JsonDict;
import com.stnts.tc.common.KeyUtils;
import com.stnts.tc.common.OpEnum;
import com.stnts.tc.common.VtypeEnum;
import com.stnts.tc.enums.PluginTypeEnum;
import com.stnts.tc.vo.KpiSearch;
import com.stnts.tc.vo.ObjValue;
import com.stnts.tc.vo.ObjectValue;
import com.stnts.tc.vo.PluginKeyVO;
import com.stnts.tc.vo.Value;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liang.zhang
 * @date 2019年11月18日
 * @desc TODO
 */
@Slf4j
public class TcUtil implements Constants {

	/**
	 * 组成rowkey
	 * 
	 * @param year
	 * @param kpi
	 * @param vtype
	 * @param cycle
	 * @return
	 */
	public static String rowkey(int year, String kpi, String vtype, String cycle) {
		String y = String.valueOf(year).substring(2);
		return String.join("_", y, kpi, vtype, cycle);
	}

	/**
	 * @param k            指标：B_VA
	 * @param srcBeginDate 开始日期 d：201-01-01; w：2019-45; m：2019-12
	 * @param srcEndDate   结束日期
	 * @param cycle        周期：日（D）、周（W）、月（M）
	 * @param vtypes       值类型：V TBV TVR HBV HBR
	 * @throws Exception
	 */
	public static List<KpiSearch> kpis(String k, String srcBeginDate, String srcEndDate, CycleEnum cycle,
			String... vtypes) throws Exception {

		List<KpiSearch> kpis = new ArrayList<KpiSearch>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String _cycle = cycle.toString();
		switch (cycle) {
		case DAY: {

			Date beginDate = sdf.parse(srcBeginDate);
			Date endDate = sdf.parse(srcEndDate);
			int gapDays = DateUtil.gapDays(beginDate, endDate);
			if (gapDays > 365) {
				throw new RuntimeException("不得超过365天");
			}
			if (DateUtil.isSameYear(beginDate, endDate)) { // 同一年就好说了
				int year = DateUtil.year(beginDate);
				int begin = DateUtil.dayOfYear(beginDate);
				int end = DateUtil.dayOfYear(endDate);
				appendKpi(kpis, k, _cycle, year, begin, end, vtypes);
			} else {
				int beginYear = DateUtil.year(beginDate);
				int endYear = DateUtil.year(endDate);
				int gapYears = endYear - beginYear;
				for (int i = 0; i <= gapYears; i++) {
					int year = beginYear + i;
					int begin = DEFAULT;
					int end = DEFAULT;
					if (year == beginYear) { // 是起始年
						begin = DateUtil.dayOfYear(beginDate);
						end = DateUtil.days(year);
					} else if (year == endYear) { // 是结束年
						begin = BEGIN_DEFAULT;
						end = DateUtil.dayOfYear(endDate);
					} else { // 中间年份
						begin = BEGIN_DEFAULT;
						end = DateUtil.days(year);
					}
					if (begin != DEFAULT && end != DEFAULT) {
						appendKpi(kpis, k, _cycle, year, begin, end, vtypes);
					}
				}
			}

		}
			;
			break;
		case WEEK: {
			// 这里的日期不是规范的 只能取字符串比较: 2019-52
			int beginYear = Integer.parseInt(srcBeginDate.substring(0, 4));
			int endYear = Integer.parseInt(srcEndDate.substring(0, 4));
			if (beginYear == endYear) {

				int begin = Integer.parseInt(srcBeginDate.substring(5));
				int end = Integer.parseInt(srcEndDate.substring(5));
				appendKpi(kpis, k, _cycle, beginYear, begin, end, vtypes);
			} else {
				int gapYears = endYear - beginYear;
				for (int i = 0; i <= gapYears; i++) {
					int year = beginYear + i;
					int begin = DEFAULT;
					int end = DEFAULT;
					if (year == beginYear) {
						begin = Integer.parseInt(srcBeginDate.substring(5));
						end = DateUtil.weeks(year);
					} else if (year == endYear) {
						begin = BEGIN_DEFAULT;
						end = Integer.parseInt(srcEndDate.substring(5));
					} else {
						begin = BEGIN_DEFAULT;
						end = DateUtil.weeks(year);
					}
					if (begin != DEFAULT && end != DEFAULT) {
						appendKpi(kpis, k, _cycle, year, begin, end, vtypes);
					}
				}
			}
		}
			;
			break;
		case MONTH: {
			// 这里的日期不是规范的 只能取字符串比较: 2019-52
			int beginYear = Integer.parseInt(srcBeginDate.substring(0, 4));
			int endYear = Integer.parseInt(srcEndDate.substring(0, 4));
			if (beginYear == endYear) {

				int begin = Integer.parseInt(srcBeginDate.substring(5));
				int end = Integer.parseInt(srcEndDate.substring(5));
				appendKpi(kpis, k, _cycle, beginYear, begin, end, vtypes);
			} else {
				int gapYears = endYear - beginYear;
				for (int i = 0; i <= gapYears; i++) {
					int year = beginYear + i;
					int begin = DEFAULT;
					int end = DEFAULT;
					if (year == beginYear) {
						begin = Integer.parseInt(srcBeginDate.substring(5));
						end = END_DEFAULT_M;
					} else if (year == endYear) {
						begin = BEGIN_DEFAULT;
						end = Integer.parseInt(srcEndDate.substring(5));
					} else {
						begin = BEGIN_DEFAULT;
						end = END_DEFAULT_M;
					}
					if (begin != DEFAULT && end != DEFAULT) {
						appendKpi(kpis, k, _cycle, year, begin, end, vtypes);
					}
				}
			}
		}
			;
			break;
		default: {
			/** do Nothing */
		}
		}
		return kpis;
	}

	public static void appendKpi(List<KpiSearch> kpis, String k, String cycle, int year, int begin, int end,
			String... vtypes) {
		for (String vtype : vtypes) { // 不同类型的值是一行
			String rowkey = TcUtil.rowkey(year, k, vtype, cycle.toString());
			KpiSearch kpi = new KpiSearch(rowkey, begin, end);
			kpis.add(kpi);
		}
	}

	public static Value v2V(String rowkey, String col, String v) {

		int y = Integer.parseInt(StringUtils.splitByWholeSeparator(rowkey, "_")[0]);
		int c = Integer.parseInt(col);
		return new Value(y, c, new BigDecimal(v));
	}

	public static ObjectValue v2ObjectValue(String rowkey, String col, String v, String srcBeginDate) {

		String[] rowkeyArr = StringUtils.splitByWholeSeparator(rowkey, "_");
		int y = Integer.parseInt(rowkeyArr[0]);
		int c = Integer.parseInt(col);
		String cycle = rowkeyArr[rowkeyArr.length - 1];
		Date currDate = DateUtil.toDate(y, c, cycle);
		int index = DateUtil.between(srcBeginDate, currDate, CycleEnum.cycle(cycle));
//		System.out.println("index:" + index);
		return new ObjectValue(y, c, cycle, v, index);
	}

	/**
	 * @param rowkey
	 * @param col
	 * @param v
	 * @return
	 */
	public static ObjValue v2ObjV(String rowkey, String col, String v) {

		String[] rowkeyArr = StringUtils.splitByWholeSeparator(rowkey, "_");
		int y = Integer.parseInt(rowkeyArr[0]);
		// 正常情况下 这里返回 D W M
		String cycle = rowkeyArr[rowkeyArr.length - 1];
		int c = Integer.parseInt(col);
		return new ObjValue(y, c, cycle, v);
	}

	/**
	 * 求增长率
	 * 
	 * @param src
	 * @param dest
	 * @return
	 */
	public static BigDecimal v2p(BigDecimal src, BigDecimal dest) {
		if (null == src || null == dest || dest.compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}
		return src.subtract(dest).divide(dest, 4, BigDecimal.ROUND_HALF_UP);// .divide(new BigDecimal(100));
	}

	/**
	 * 求占比
	 * 
	 * @param src
	 * @param dest
	 * @return
	 */
	public static BigDecimal v4p(BigDecimal src, BigDecimal dest) {
		if (null == src || null == dest || dest.compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}
		return src.divide(dest, 4, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 这个merge主要针对json格式（考虑与merge合并;待优化） 不同年份同一个指标的值合并到一起
	 * 
	 * @param dat
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> merge2ObjBck(Map<String, Object> dat, OpEnum op) {

		Map<String, Object> _dat = new HashMap<String, Object>();
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		Set<String> keys = dat.keySet();
		keys.stream().forEach(key -> {
			// _key=B_A_V key=19_B_A_V_D
			String[] kArr = key.split(KEY_SPLIT);
			String[] _kArr = new String[kArr.length - 2];
			System.arraycopy(kArr, 1, _kArr, 0, kArr.length - 2);
//			String _key = key.substring(3);
			String _key = StringUtils.join(_kArr, KEY_SPLIT);
			List<String> ks = map.get(_key);
			if (null == ks) {
				ks = new ArrayList<String>();
				map.put(_key, ks);
			}
			ks.add(key);
		});
		/**
		 * map => B_A_V: - 18_B_A_V_D - 19_B_A_V_D
		 */
		Set<Entry<String, List<String>>> entrys = map.entrySet();
		Map<String, BigDecimal> cardMap = new HashMap<String, BigDecimal>();
		for (Iterator<Entry<String, List<String>>> it = entrys.iterator(); it.hasNext();) {

			List<ObjValue> vlist = new ArrayList<ObjValue>();
			Entry<String, List<String>> entry = it.next();
			String _key = entry.getKey(); // B_A_V
			List<String> ks = entry.getValue();
			ks.stream().forEach(k -> {
				vlist.addAll((Collection<? extends ObjValue>) dat.get(k));
			});
			Collections.sort(vlist);
			List<Object> vols = new ArrayList<Object>();
			vlist.stream().forEach(v -> {
				vols.add(v.getV());
			});
			// 在这里把这个列表填充完整好不
			_dat.put(_key, vols);

			if (null != op) {
				switch (op) {
				case AVG: {

//						if(!vols.isEmpty()) {
//							
//							BigDecimal sum = BigDecimal.ZERO;
//							BigDecimal size = BigDecimal.ZERO;
//							for(Object volObj : vols){
//								BigDecimal vol = new BigDecimal(String.valueOf(volObj));
//								sum = sum.add(vol);
//								size = size.add(BigDecimal.ONE);
//							}
//							if(size.intValue() != 0) {
//								cardMap.put(_key, sum.divide(size, BigDecimal.ROUND_HALF_UP));
//							}
//						}
					List<BigDecimal> vs = vols.stream().map(vol -> new BigDecimal(String.valueOf(vol)))
							.collect(Collectors.toList());
					BigDecimal avg = list2Avg(vs, false);
//					if (null != avg) {
						cardMap.put(_key, avg);
//					}
				}
					;
					break;
				default: {

				}
				}
			}
		}
		if (!cardMap.isEmpty()) {
			_dat.put(RESULT_CARD, cardMap);
		}
		return _dat;
	}

	/**
	 * 这个merge主要针对json格式（考虑与merge合并;待优化） 不同年份同一个指标的值合并到一起
	 * 
	 * @param dat
	 * @return
	 */
	public static Map<String, Object> merge2Obj(Map<String, List<ObjectValue>> dat, String srcBeginDate,
			String srcEndDate, CycleEnum cycle) {

		Map<String, Object> _dat = new HashMap<String, Object>();
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		Set<String> keys = dat.keySet();
		keys.stream().forEach(key -> {
			// _key=B_A_V key=19_B_A_V_D
			String[] kArr = key.split(KEY_SPLIT);
			String[] _kArr = new String[kArr.length - 2];
			System.arraycopy(kArr, 1, _kArr, 0, kArr.length - 2);
//			String _key = key.substring(3);
			String _key = StringUtils.join(_kArr, KEY_SPLIT);
			List<String> ks = map.get(_key);
			if (null == ks) {
				ks = new ArrayList<String>();
				map.put(_key, ks);
			}
			ks.add(key);
		});
		/**
		 * map => B_A_V: - 18_B_A_V_D - 19_B_A_V_D
		 */
		Set<Entry<String, List<String>>> entrys = map.entrySet();
		int len = DateUtil.between(srcBeginDate, srcEndDate, cycle);
		for (Iterator<Entry<String, List<String>>> it = entrys.iterator(); it.hasNext();) {

			ObjectValue[] ovs = new ObjectValue[len];
			Entry<String, List<String>> entry = it.next();
			String _key = entry.getKey(); // B_A_V
			List<String> ks = entry.getValue();
			ks.stream().forEach(k -> {
				dat.get(k).stream().forEach(item -> {
					ovs[item.getIndex()] = item;
				});
			});
//			Collections.sort(vlist);
			List<Object> vols = new ArrayList<Object>();
			List<ObjectValue> vlist = Arrays.asList(ovs);
			vlist.stream().forEach(v -> {
				vols.add(null != v ? v.getV() : null);
			});
			_dat.put(_key, vols);

		}
		return _dat;
	}

	/**
	 * 新增网吧处理（） 把所有数据处理为按天的
	 * 
	 * @param dat
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, List<Object>> merge4Bar(Map<String, Object> dat) {

		Map<String, List<Object>> result = new HashMap<String, List<Object>>();
		if (null != dat && !dat.isEmpty()) {

			// dat = Map<18_KPI_V_D, List<ObjValue>>
			dat.values().forEach(item -> {

				List<ObjValue> objList = (List<ObjValue>) item;
				objList.forEach(obj -> {
					// obj = "[1,2,3]"
					if (null != obj) {

						String date = obj.toDate();
//						List<Object> resultValueList = result.get(date);
//						if (null == resultValueList) {
//							resultValueList = new ArrayList<Object>();
//							result.put(date, resultValueList);
//						}
						List<Object> resultValueList = result.computeIfAbsent(date, key -> new ArrayList<>());
						result.put(date, resultValueList);
//						List<Integer> collect = resultValueList.stream().map(i -> Integer.parseInt(String.valueOf(i))).collect(Collectors.toList());
						String objArrStr = String.valueOf(obj.getV());
						Set<Integer> gidSet = new HashSet<>();
						if (StringUtils.isNotBlank(objArrStr)) {
							JSONArray jsonArr = JSON.parseArray(objArrStr);
							for (int i = 0; i < jsonArr.size(); i++) {
								int gid = jsonArr.getIntValue(i);
								gidSet.add(gid);
//								if (!collect.contains(gid)) {
//									resultValueList.add(gid);
//								}
							}
						}
						resultValueList.addAll(gidSet);
					}
				});
			});
		}
		return result;
	}

	/**
	 * 将json格式转化为不同指标的数组
	 * 
	 * @param mergeDat
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object[][] toObjs(Map<String, Object> mergeDat, int kpiSize, boolean isComp) {

		Object[][] objs = null;
//		int kpiSize = isComp ? 9 : 45;
		if (null != mergeDat && !mergeDat.isEmpty()) {
			Collection<Object> colls = mergeDat.values();
			objs = new Object[kpiSize][colls.size()];
			for (Iterator<Object> it = colls.iterator(); it.hasNext();) {

				List<String> jsonList = (List<String>) it.next();
				int i = 0;
				for (String jsonStr : jsonList) {

					int j = 0;
					JSONObject jsonObj = JSON.parseObject(jsonStr);
					for (String _k : KeyUtils.B_DE_K_LIST(isComp)) {

						Object kv = jsonObj.get(_k);
						kv = null == kv ? 0 : kv; // 默认给0
						objs[j++][i] = kv;
					}
					i++;
				}
			}
		}
		return objs;
	}

	public static int date2int(String date) {

		try {
			String[] items = date.split("-");
			List<String> newItems = Arrays.asList(items).stream().map(item -> {
				if (item.length() < 2) {
					return "0".concat(item);
				} else {
					return item;
				}
			}).collect(Collectors.toList());
			return Integer.parseInt(StringUtils.join(newItems, ""));
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 将json格式转化为不同指标的MAP结构
	 * 
	 * @param mergeDat
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toObjMap(Map<String, Object> mergeDat, boolean isComp, List<String> kpis) {

		Map<String, Object> objMap = new HashMap<String, Object>();
		if (null != mergeDat && !mergeDat.isEmpty()) {

//			List<String> kpis = KeyUtils.B_DE_K_LIST(isComp);
//			int kpiSize = kpis.size();
//			for(int i = 0 ; i < kpiSize ; i++) {
//				
//				String kpi = kpis.get(i);  //pc_start ...
//				List<Object> vlist = (List<Object>) objMap.get(kpi);
//				if(null == vlist) {
//					vlist = new ArrayList<Object>();
//					objMap.put(kpi, objMap);
//				}
//			}
			Collection<Object> colls = mergeDat.values();
			for (Iterator<Object> it = colls.iterator(); it.hasNext();) {

				List<String> jsonList = (List<String>) it.next();
				for (String jsonStr : jsonList) {

//					if (StringUtils.isNotBlank(jsonStr)) {

					for (String _k : kpis) {

						Object _v = StringUtils.isNotBlank(jsonStr) ? JSON.parseObject(jsonStr).get(_k) : null;
						List<Object> vlist = (List<Object>) objMap.get(_k);
						if (null == vlist) {
							vlist = new ArrayList<Object>();
							objMap.put(_k, vlist);
						}
						vlist.add(_v);
					}
//					} 
				}
			}
		}
		return objMap;
	}

	/**
	 * 把key list<json字符串> 格式转化为 list<json>
	 * 
	 * @param mergeDat
	 * @param listKpis
	 * @param isComp
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> toObjList(Map<String, Object> mergeDat, List<String> listKpis) {

		List<Object> list = new ArrayList<Object>();
		if (null != mergeDat && !mergeDat.isEmpty()) {

			Collection<Object> colls = mergeDat.values();
			for (Iterator<Object> it = colls.iterator(); it.hasNext();) {

				List<String> jsonList = (List<String>) it.next();
				for (String jsonStr : jsonList) {

					if (StringUtils.isNotBlank(jsonStr)) {

						JSONObject jsonObj = JSON.parseObject(jsonStr);
						JSONObject _jsonObj = new JSONObject();
						_jsonObj.put("date", jsonObj.getString("date"));
						listKpis.stream().forEach(kpi -> {
							_jsonObj.put(kpi, jsonObj.get(kpi));
						});
						list.add(_jsonObj);
					}
				}
			}
		}
		list.sort(new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {
				try {
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					JSONObject src = (JSONObject) o1;
					JSONObject dest = (JSONObject) o2;
					Date srcDate = src.getDate("date");
					Date destDate = dest.getDate("date");
					if (srcDate.after(destDate)) {
						return 1;
					} else if (srcDate.before(destDate)) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
				}
				return 0;
			}
		});
		return list;
	}

	/**
	 * @param mergeDat
	 * @param kpis
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, List<Map<String, Object>>> pluginJson2Map(Map<String, Object> mergeDat,
			List<String> kpis) {

		Map<String, List<Map<String, Object>>> resultMap = new HashMap<String, List<Map<String, Object>>>();
		List<Map<String, Object>> bizList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> baseList = new ArrayList<Map<String, Object>>();
		if (null != mergeDat && !mergeDat.isEmpty()) {

			Map<String, Map<String, List<BigDecimal>>> statMap = new HashMap<String, Map<String, List<BigDecimal>>>();
			Collection<Object> colls = mergeDat.values();
			for (Iterator<Object> it = colls.iterator(); it.hasNext();) {
				// 所有的记录
				List<String> jsonList = (List<String>) it.next();
				for (String jsonStr : jsonList) {

					if (StringUtils.isNotBlank(jsonStr)) {
						// 一列下的一个json数组
						JSONArray jsonArrObj = JSON.parseArray(jsonStr);
						for (int i = 0; i < jsonArrObj.size(); i++) {

							JSONObject jsonObj = jsonArrObj.getJSONObject(i);
							int pluginId = jsonObj.getIntValue(JsonDict.B_DE_PI_DE_PLUGIN_ID);
							String pluginName = jsonObj.getString(JsonDict.B_DE_PI_DE_PLUGIN_NAME);
							// plugin_type
							int pluginType = jsonObj.getIntValue(JsonDict.B_DE_PI_DE_PLUGIN_TYPE);
							// key = plugin_id::plugin_name::plugin_type
							String statKey = String.format("%s::%s::%s", pluginId, pluginName, pluginType);
							Map<String, List<BigDecimal>> subStatMap = statMap.get(statKey);
							if (null == subStatMap) {
								subStatMap = new HashMap<String, List<BigDecimal>>();
								statMap.put(statKey, subStatMap);
							}
							for (String kpi : kpis) {

								if (!StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_ID)
										&& !StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_NAME)
										&& !StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_TYPE)) {

									BigDecimal kv = jsonObj.getBigDecimal(kpi);
									List<BigDecimal> vs = subStatMap.get(kpi);
									if (null == vs) {
										vs = new ArrayList<BigDecimal>();
										subStatMap.put(kpi, vs);
									}
									if (kv != null) {
										vs.add(kv);
									}
								}
							}
						}
					}
				}
			}
			//
			if (!statMap.isEmpty()) {

				Set<Entry<String, Map<String, List<BigDecimal>>>> entrys = statMap.entrySet();
				for (Iterator<Entry<String, Map<String, List<BigDecimal>>>> it = entrys.iterator(); it.hasNext();) {

					Map<String, Object> map = new HashMap<String, Object>();
					Entry<String, Map<String, List<BigDecimal>>> entry = it.next();
					String key = entry.getKey();
					String[] keyArr = StringUtils.split(key, "::");
					int pluginId = Integer.parseInt(keyArr[0]);
					int pluginType = Integer.parseInt(keyArr[2]);
					String pluginName = keyArr[1];
					map.put(JsonDict.B_DE_PI_DE_PLUGIN_ID, pluginId);
					map.put(JsonDict.B_DE_PI_DE_PLUGIN_NAME, pluginName);

					Map<String, List<BigDecimal>> _subStatMap = entry.getValue();
					Map<String, BigDecimal> avgStatMap = map2Avg(_subStatMap);
					if (null != avgStatMap && !avgStatMap.isEmpty()) {

						Set<Entry<String, BigDecimal>> subEntrys = avgStatMap.entrySet();
						for (Iterator<Entry<String, BigDecimal>> subIt = subEntrys.iterator(); subIt.hasNext();) {

							Entry<String, BigDecimal> subEntry = subIt.next();
							String kpi = subEntry.getKey();
							BigDecimal kv = subEntry.getValue();
							map.put(kpi, kv);
						}
					}
					PluginTypeEnum pluginTypeEnum = PluginTypeEnum.pluginType(pluginType);
					if (pluginTypeEnum.equals(PluginTypeEnum.BASE)) {
						baseList.add(map);
					} else {
						bizList.add(map);
					}
				}
			}
		}
		Comparator<Map<String, Object>> comparator = new Comparator<Map<String, Object>>() {

			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {

				int p1 = (int) o1.get(JsonDict.B_DE_PI_DE_PLUGIN_ID);
				int p2 = (int) o2.get(JsonDict.B_DE_PI_DE_PLUGIN_ID);
				if (p1 < p2) {
					return -1;
				} else if (p1 > p2) {
					return 1;
				} else {
					return 0;
				}
			}
		};
		if (!baseList.isEmpty()) {
			baseList.sort(comparator);
		}
		if (!bizList.isEmpty()) {
			bizList.sort(comparator);
		}
		resultMap.put(PluginTypeEnum.BASE.getName(), baseList);
		resultMap.put(PluginTypeEnum.BIZ.getName(), bizList);

		return resultMap;
	}

	/**
	 * 复制于pluginJson2Map 列值为jsonObject处理 待优化 pluginJson2Map中列值为jsonArray
	 * 用户全国插件概览界面
	 * @param mergeDat
	 * @param kpis
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, List<Map<String, Object>>> pluginJson2Map2(Map<String, Object> mergeDat,
			List<String> kpis) {

		Map<String, List<Map<String, Object>>> resultMap = new HashMap<String, List<Map<String, Object>>>();
		List<Map<String, Object>> bizList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> baseList = new ArrayList<Map<String, Object>>();
		if (null != mergeDat && !mergeDat.isEmpty()) {

			Map<String, Map<String, List<BigDecimal>>> statMap = new HashMap<String, Map<String, List<BigDecimal>>>();
			Collection<Object> colls = mergeDat.values();
			for (Iterator<Object> it = colls.iterator(); it.hasNext();) {
				// 所有的记录
				List<String> jsonList = (List<String>) it.next();
				for (String jsonStr : jsonList) {

					if (StringUtils.isBlank(jsonStr)) {
						continue;
					}
					try {

						JSONObject jsonObj = JSON.parseObject(jsonStr);
						int pluginId = jsonObj.getIntValue(JsonDict.B_DE_PI_DE_PLUGIN_ID);
						String pluginName = jsonObj.getString(JsonDict.B_DE_PI_DE_PLUGIN_NAME);
						// plugin_type
						int pluginType = jsonObj.getIntValue(JsonDict.B_DE_PI_DE_PLUGIN_TYPE);
						// key = plugin_id::plugin_name::plugin_type
						String statKey = String.format("%s::%s::%s", pluginId, pluginName, pluginType);
						Map<String, List<BigDecimal>> subStatMap = statMap.get(statKey);
						if (null == subStatMap) {
							subStatMap = new HashMap<String, List<BigDecimal>>();
							statMap.put(statKey, subStatMap);
						}
						for (String kpi : kpis) {

							if (!StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_ID)
									&& !StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_NAME)
									&& !StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_TYPE)) {

								BigDecimal kv = jsonObj.getBigDecimal(kpi);
								List<BigDecimal> vs = subStatMap.get(kpi);
								if (null == vs) {
									vs = new ArrayList<BigDecimal>();
									subStatMap.put(kpi, vs);
								}
								if (kv != null) {
									vs.add(kv);
								}
							}
						}
					} catch (Exception e) {
					}
				}
			}
			//
			if (!statMap.isEmpty()) {

				Set<Entry<String, Map<String, List<BigDecimal>>>> entrys = statMap.entrySet();
				for (Iterator<Entry<String, Map<String, List<BigDecimal>>>> it = entrys.iterator(); it.hasNext();) {

					Map<String, Object> map = new HashMap<String, Object>();
					Entry<String, Map<String, List<BigDecimal>>> entry = it.next();
					String key = entry.getKey();
					String[] keyArr = StringUtils.split(key, "::");
					int pluginId = Integer.parseInt(keyArr[0]);
					int pluginType = Integer.parseInt(keyArr[2]);
					String pluginName = keyArr[1];
					map.put(JsonDict.B_DE_PI_DE_PLUGIN_ID, pluginId);
					map.put(JsonDict.B_DE_PI_DE_PLUGIN_NAME, pluginName);

					Map<String, List<BigDecimal>> _subStatMap = entry.getValue();
					Map<String, BigDecimal> avgStatMap = map2Avg(_subStatMap);
					if (null != avgStatMap && !avgStatMap.isEmpty()) {

						Set<Entry<String, BigDecimal>> subEntrys = avgStatMap.entrySet();
						for (Iterator<Entry<String, BigDecimal>> subIt = subEntrys.iterator(); subIt.hasNext();) {

							Entry<String, BigDecimal> subEntry = subIt.next();
							String kpi = subEntry.getKey();
							BigDecimal kv = subEntry.getValue();
							map.put(kpi, kv);
						}
					}
					PluginTypeEnum pluginTypeEnum = PluginTypeEnum.pluginType(pluginType);
					if (pluginTypeEnum.equals(PluginTypeEnum.BASE)) {
						baseList.add(map);
					} else {
						bizList.add(map);
					}
				}
			}
		}

		Comparator<Map<String, Object>> comparator = new Comparator<Map<String, Object>>() {

			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {

				int p1 = (int) o1.get(JsonDict.B_DE_PI_DE_PLUGIN_ID);
				int p2 = (int) o2.get(JsonDict.B_DE_PI_DE_PLUGIN_ID);
				if (p1 < p2) {
					return -1;
				} else if (p1 > p2) {
					return 1;
				} else {
					return 0;
				}
			}
		};
		if (!baseList.isEmpty()) {
			baseList.sort(comparator);
			//比率全部放在这里算
			calcPcRate(baseList);
		}
		if (!bizList.isEmpty()) {
			bizList.sort(comparator);
			calcPcRate(bizList);
		}
		resultMap.put(PluginTypeEnum.BASE.getName(), baseList);
		resultMap.put(PluginTypeEnum.BIZ.getName(), bizList);

		return resultMap;
	}

	/**
	 * 重写插件PC相关的比率
	 * @param baseList
	 */
	private static void calcPcRate(List<Map<String, Object>> baseList) {

		try {
			
			baseList.forEach(m -> {
				
				if (null != m && !m.isEmpty()) {
					
//					System.out.println("pre: " + m.get("pc_start_rate"));
					BigDecimal pc_active = (BigDecimal) m.get("pc_active");
					BigDecimal pc_reach = (BigDecimal) m.get("pc_reach");
					BigDecimal pc_start = (BigDecimal) m.get("pc_start");
					BigDecimal pc_effect = (BigDecimal) m.get("pc_effect");
					BigDecimal pc_business = (BigDecimal) m.get("pc_business");
					m.put("pc_rate_fin", TcUtil.v4p(pc_business, pc_active));
					m.put("pc_reach_rate", TcUtil.v4p(pc_reach, pc_active));
//					System.out.println("post: " + TcUtil.v4p(pc_start, pc_reach));
					m.put("pc_start_rate", TcUtil.v4p(pc_start, pc_reach));
					m.put("pc_effect_rate", TcUtil.v4p(pc_effect, pc_start));
					m.put("pc_business_rate", TcUtil.v4p(pc_business, pc_effect));
				}
			});
		} catch (Exception e) {
			log.warn("calcPcRate err, msg: " + e.getMessage());
		}
	}

	/**
	 * 网吧插件用的 
	 * 网吧插件用   修复null值版本
	 * @param mergeDat
	 * @param kpis
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<PluginKeyVO, Map<String, Object>> pluginJSONMergeNew(Map<String, Object> mergeDat,
			List<String> kpis, int len) {

		Map<PluginKeyVO, Map<String, Object>> mergeMap = new HashMap<PluginKeyVO, Map<String, Object>>();
		if (null != mergeDat && !mergeDat.isEmpty()) {

			Collection<Object> colls = mergeDat.values();
			for (Iterator<Object> it = colls.iterator(); it.hasNext();) {
				// 所有的记录
				List<String> jsonList = (List<String>) it.next();
				int index = 0;
				for (String jsonStr : jsonList) {  //jsonList = list<json array string>

					if (StringUtils.isNotBlank(jsonStr)) {
						// 一列下的一个json数组
						JSONArray jsonArrObj = JSON.parseArray(jsonStr);
						for (int i = 0; i < jsonArrObj.size(); i++) {

							JSONObject jsonObj = jsonArrObj.getJSONObject(i);
							int pluginId = jsonObj.getIntValue(JsonDict.B_DE_PI_DE_PLUGIN_ID);
							String pluginName = jsonObj.getString(JsonDict.B_DE_PI_DE_PLUGIN_NAME);
							// plugin_type
							int pluginType = jsonObj.getIntValue(JsonDict.B_DE_PI_DE_PLUGIN_TYPE);
							// key = plugin_id::plugin_name::plugin_type
							PluginKeyVO pkey = new PluginKeyVO(pluginId, pluginName, pluginType);
							Map<String, Object> pluginMap = mergeMap.get(pkey);
							if (null == pluginMap) {
								pluginMap = new HashMap<String, Object>();
								mergeMap.put(pkey, pluginMap);
							}
							for (String kpi : kpis) {

								if (!StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_ID)
										&& !StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_NAME)
										&& !StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_TYPE)) {

									BigDecimal kv = jsonObj.getBigDecimal(kpi);
									List<BigDecimal> vs = (List<BigDecimal>) pluginMap.get(kpi);
									if (null == vs) {
										BigDecimal[] vsArr = new BigDecimal[len];
										vs = Arrays.asList(vsArr);
										pluginMap.put(kpi, vs);
									}
									vs.set(index, kv);
								}
							}
						}
					}
					index++;
				}
			}
		}
		return mergeMap;
	}
	
	/**
	 * @param mergeDat
	 * @param kpis
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<PluginKeyVO, Map<String, Object>> pluginJSONMerge(Map<String, Object> mergeDat,
			List<String> kpis) {

		/**
		 * mergeDat: 网吧key   value= List<JsonArrayString>
		 */
		Map<PluginKeyVO, Map<String, Object>> mergeMap = new HashMap<PluginKeyVO, Map<String, Object>>();
		if (null != mergeDat && !mergeDat.isEmpty()) {

			Collection<Object> colls = mergeDat.values();
			for (Iterator<Object> it = colls.iterator(); it.hasNext();) {
				// 所有的记录
				List<String> jsonList = (List<String>) it.next();
				for (String jsonStr : jsonList) {

					if (StringUtils.isNotBlank(jsonStr)) {
						// 一列下的一个json数组
						JSONArray jsonArrObj = JSON.parseArray(jsonStr);
						for (int i = 0; i < jsonArrObj.size(); i++) {

							JSONObject jsonObj = jsonArrObj.getJSONObject(i);
							int pluginId = jsonObj.getIntValue(JsonDict.B_DE_PI_DE_PLUGIN_ID);
							String pluginName = jsonObj.getString(JsonDict.B_DE_PI_DE_PLUGIN_NAME);
							// plugin_type
							int pluginType = jsonObj.getIntValue(JsonDict.B_DE_PI_DE_PLUGIN_TYPE);
							// key = plugin_id::plugin_name::plugin_type
							PluginKeyVO pkey = new PluginKeyVO(pluginId, pluginName, pluginType);
							Map<String, Object> pluginMap = mergeMap.get(pkey);
							if (null == pluginMap) {
								pluginMap = new HashMap<String, Object>();
								mergeMap.put(pkey, pluginMap);
							}
							for (String kpi : kpis) {

								if (!StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_ID)
										&& !StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_NAME)
										&& !StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_TYPE)) {

									BigDecimal kv = jsonObj.getBigDecimal(kpi);
									List<BigDecimal> vs = (List<BigDecimal>) pluginMap.get(kpi);
									if (null == vs) {
										vs = new ArrayList<BigDecimal>();
										pluginMap.put(kpi, vs);
									}
//									if (kv != null) {
//										vs.add(kv);
//									} else {
//										vs.add(BigDecimal.ZERO);
//									}
									vs.add(kv);
								}
							}
						}
					}else {
					}
				}
			}
		}
		return mergeMap;
	}

	/**
	 * 为插件详情服务 数据需要各指标的list
	 * 
	 * @param mergeDat
	 * @param kpis
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, List<Map<String, Object>>> pluginJson2MapForDe(Map<String, Object> mergeDat,
			List<String> kpis) {

		Map<String, List<Map<String, Object>>> resultMap = new HashMap<String, List<Map<String, Object>>>();
		List<Map<String, Object>> bizList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> baseList = new ArrayList<Map<String, Object>>();
		if (null != mergeDat && !mergeDat.isEmpty()) {

			Map<String, Map<String, List<BigDecimal>>> statMap = new HashMap<String, Map<String, List<BigDecimal>>>();
			Collection<Object> colls = mergeDat.values();
			for (Iterator<Object> it = colls.iterator(); it.hasNext();) {
				// 所有的记录
				List<String> jsonList = (List<String>) it.next();
				for (String jsonStr : jsonList) {

					if (StringUtils.isNotBlank(jsonStr)) {
						// 一列下的一个json数组
						JSONArray jsonArrObj = JSON.parseArray(jsonStr);
						for (int i = 0; i < jsonArrObj.size(); i++) {

							JSONObject jsonObj = jsonArrObj.getJSONObject(i);
							int pluginId = jsonObj.getIntValue(JsonDict.B_DE_PI_DE_PLUGIN_ID);
							String pluginName = jsonObj.getString(JsonDict.B_DE_PI_DE_PLUGIN_NAME);
							// plugin_type
							int pluginType = jsonObj.getIntValue(JsonDict.B_DE_PI_DE_PLUGIN_TYPE);
							// key = plugin_id::plugin_name::plugin_type
							String statKey = String.format("%s::%s::%s", pluginId, pluginName, pluginType);
							Map<String, List<BigDecimal>> subStatMap = statMap.get(statKey);
							if (null == subStatMap) {
								subStatMap = new HashMap<String, List<BigDecimal>>();
								statMap.put(statKey, subStatMap);
							}
							for (String kpi : kpis) {

								if (!StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_ID)
										&& !StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_NAME)
										&& !StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_TYPE)) {

									BigDecimal kv = jsonObj.getBigDecimal(kpi);
									List<BigDecimal> vs = subStatMap.get(kpi);
									if (null == vs) {
										vs = new ArrayList<BigDecimal>();
										subStatMap.put(kpi, vs);
									}
									if (kv != null) {
										vs.add(kv);
									}
								}
							}
						}
					}
				}
			}
			//
			if (!statMap.isEmpty()) {

				Set<Entry<String, Map<String, List<BigDecimal>>>> entrys = statMap.entrySet();
				for (Iterator<Entry<String, Map<String, List<BigDecimal>>>> it = entrys.iterator(); it.hasNext();) {

					Map<String, Object> map = new HashMap<String, Object>();
					Entry<String, Map<String, List<BigDecimal>>> entry = it.next();
					String key = entry.getKey();
					String[] keyArr = StringUtils.split(key, "::");
					int pluginId = Integer.parseInt(keyArr[0]);
					String pluginName = keyArr[1];
					map.put(JsonDict.B_DE_PI_DE_PLUGIN_ID, pluginId);
					map.put(JsonDict.B_DE_PI_DE_PLUGIN_NAME, pluginName);

					Map<String, List<BigDecimal>> _subStatMap = entry.getValue();
					Map<String, BigDecimal> avgStatMap = map2Avg(_subStatMap);
					if (null != avgStatMap && !avgStatMap.isEmpty()) {

						Set<Entry<String, BigDecimal>> subEntrys = avgStatMap.entrySet();
						for (Iterator<Entry<String, BigDecimal>> subIt = subEntrys.iterator(); subIt.hasNext();) {

							Entry<String, BigDecimal> subEntry = subIt.next();
							String kpi = subEntry.getKey();
							BigDecimal kv = subEntry.getValue();
							map.put(kpi, kv);
						}
					}
					PluginTypeEnum pluginTypeEnum = PluginTypeEnum.pluginType(pluginId);
					if (pluginTypeEnum.equals(PluginTypeEnum.BASE)) {
						baseList.add(map);
					} else {
						bizList.add(map);
					}
				}
			}
		}
		resultMap.put(PluginTypeEnum.BASE.getName(), baseList);
		resultMap.put(PluginTypeEnum.BIZ.getName(), bizList);

		return resultMap;
	}

	public static String[] vtypes(boolean isComp) {

		return isComp ? new String[] { VtypeEnum.V.toString() }
				: new String[] { VtypeEnum.V.toString(), VtypeEnum.TV.toString(), VtypeEnum.TR.toString(),
						VtypeEnum.HV.toString(), VtypeEnum.HR.toString() };
	}

	/**
	 * 针对json这种求平均值的
	 * 
	 * @param objs
	 * 
	 * @return
	 */
	public static Map<Integer, BigDecimal> objs2Avg(Object[][] objs) {

		Map<Integer, BigDecimal> avgMap = new HashMap<Integer, BigDecimal>();
		if (null != objs && objs.length > 0) {

			for (int i = 0; i < objs.length; i++) {

				// 指标项=0
				Object[] vArr = objs[i];
				BigDecimal sum = BigDecimal.ZERO;
				BigDecimal size = BigDecimal.ZERO;
				for (int j = 0; j < vArr.length; j++) {

					Object v = vArr[j];
					if (v != null && StringUtils.isNotBlank(String.valueOf(v))) {
						BigDecimal vol = new BigDecimal(String.valueOf(v));
						sum = sum.add(vol);
						size = size.add(BigDecimal.ONE);
					}
				}
				if (size.intValue() != 0) {
					avgMap.put(i, sum.divide(size, BigDecimal.ROUND_HALF_UP));
				}
			}
		}
		return avgMap;
	}

	/**
	 * 针对json这种求平均值的
	 * 
	 * @param objs 把这个注释掉 用那个 objMap2Card
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, BigDecimal> objMap2Avg(Map<String, Object> objMap) {

		Map<String, BigDecimal> avgMap = new HashMap<String, BigDecimal>();
		if (null != objMap && objMap.size() > 0) {

			Set<Entry<String, Object>> entrys = objMap.entrySet();
			for (Iterator<Entry<String, Object>> it = entrys.iterator(); it.hasNext();) {

				Entry<String, Object> entry = it.next();
				String kpi = entry.getKey();
				List<Object> vols = (List<Object>) entry.getValue();
				List<BigDecimal> vs = vols.stream().map(vol -> null != vol ? new BigDecimal(String.valueOf(vol)) : null)
						.collect(Collectors.toList());
				BigDecimal avg = list2Avg(vs, false);
//				if (null != avg) {
					avgMap.put(kpi, avg);
//				}
//				if(!vols.isEmpty()) {
//					
//					BigDecimal sum = BigDecimal.ZERO;
//					BigDecimal size = BigDecimal.ZERO;
//					for(Object volObj : vols){
//						BigDecimal vol = new BigDecimal(String.valueOf(volObj));
//						sum = sum.add(vol);
//						if(vol.intValue() != 0) {
//							size = size.add(BigDecimal.ONE);
//						}
//					}
//					if(size.intValue() != 0) {
//						avgMap.put(kpi, sum.divide(size, BigDecimal.ROUND_HALF_UP));
//					}
//				}
			}
		}
		return avgMap;
	}

	/**
	 * 针对json这种 可能求平均值 可能取最后一个值的
	 * 
	 * @param objs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, BigDecimal> objMap2Card(Map<String, Object> objMap, OpEnum op) {

		Map<String, BigDecimal> avgMap = new HashMap<String, BigDecimal>();
		if (null != objMap && objMap.size() > 0) {

			Set<Entry<String, Object>> entrys = objMap.entrySet();
			for (Iterator<Entry<String, Object>> it = entrys.iterator(); it.hasNext();) {

				Entry<String, Object> entry = it.next();
				String kpi = entry.getKey();
				List<Object> vols = (List<Object>) entry.getValue();
				List<BigDecimal> vs = vols.stream().map(vol -> {
					try {
						return null != vol ? new BigDecimal(String.valueOf(vol)) : null;
					} catch (Exception e) {
						return null;
					}
				}).collect(Collectors.toList());
				BigDecimal avg = op.equals(OpEnum.AVG) ? list2Avg(vs, false) : list2Last(vs);
//				if (null != avg) {
					avgMap.put(kpi, avg);
//				}
//				if(!vols.isEmpty()) {
//					
//					BigDecimal sum = BigDecimal.ZERO;
//					BigDecimal size = BigDecimal.ZERO;
//					for(Object volObj : vols){
//						BigDecimal vol = new BigDecimal(String.valueOf(volObj));
//						sum = sum.add(vol);
//						if(vol.intValue() != 0) {
//							size = size.add(BigDecimal.ONE);
//						}
//					}
//					if(size.intValue() != 0) {
//						avgMap.put(kpi, sum.divide(size, BigDecimal.ROUND_HALF_UP));
//					}
//				}
			}
		}
		return avgMap;
	}

	private static BigDecimal list2Last(List<BigDecimal> vs) {

		if (null != vs && !vs.isEmpty()) {
			return vs.get(vs.size() - 1);
		}
		return null;
	}

	/**
	 * 针对json这种求平均值的
	 * 
	 * @param objs
	 * 
	 * @return
	 */
	public static Map<String, BigDecimal> map2Avg(Map<String, List<BigDecimal>> objMap) {

		Map<String, BigDecimal> avgMap = new HashMap<String, BigDecimal>();
		if (null != objMap && objMap.size() > 0) {

			Set<Entry<String, List<BigDecimal>>> entrys = objMap.entrySet();
			for (Iterator<Entry<String, List<BigDecimal>>> it = entrys.iterator(); it.hasNext();) {

				Entry<String, List<BigDecimal>> entry = it.next();
				String kpi = entry.getKey();
				List<BigDecimal> vols = entry.getValue();
				BigDecimal avg = list2Avg(vols, false);
//				if(!vols.isEmpty()) {
//					
//					BigDecimal sum = BigDecimal.ZERO;
//					BigDecimal size = BigDecimal.ZERO;
//					for(BigDecimal vol : vols){
//						sum = sum.add(vol);
//						if(!vol.equals(BigDecimal.ZERO)) {
//							size = size.add(BigDecimal.ONE);
//						}
//					}
//					if(size.intValue() != 0) {
//						avgMap.put(kpi, sum.divide(size, BigDecimal.ROUND_HALF_UP));
//					}
//				}
//				if (null != avg) {
					avgMap.put(kpi, avg);
//				}
			}
		}
		return avgMap;
	}

	/**
	 * 求list平均值
	 * 
	 * @param dat
	 * @param ignoreZero
	 * @return
	 */
	public static BigDecimal list2Avg(List<BigDecimal> dat, boolean ignoreZero) {

		BigDecimal avg = null;
		if (null != dat && !dat.isEmpty()) {

//			Stream<BigDecimal> s = dat.stream().filter(Objects::nonNull);
			Optional<BigDecimal> sumOp = dat.stream().filter(Objects::nonNull).reduce((x, y) -> x.add(y));
			BigDecimal sum = sumOp.isPresent() ? sumOp.get() : BigDecimal.ZERO;
//			 ignoreZero = false这里应该永远不忽略 0是有效值
			BigDecimal size = ignoreZero
					? BigDecimal.valueOf(dat.stream().filter(Objects::nonNull)
							.filter(x -> !(x.compareTo(BigDecimal.ZERO) == 0)).count())
					: BigDecimal.valueOf(dat.stream().filter(Objects::nonNull).count());
			if (size.intValue() != 0) {
				//TODO 这里确定返回值类型
				avg = sum.divide(size, 4, BigDecimal.ROUND_HALF_UP);
				BigDecimal sample = dat.stream().filter(Objects::nonNull).collect(Collectors.toList()).get(0);
				if (NumberUtils.isDigits(String.valueOf(sample))) {
					avg = avg.setScale(0, BigDecimal.ROUND_HALF_UP);
				}
			}
		}
		return avg;
	}

	public static Map<String, Map<String, BigDecimal>> compMap(Map<String, BigDecimal> srcMap,
			Map<String, BigDecimal> destMap) {

		Map<String, Map<String, BigDecimal>> compMap = new HashMap<String, Map<String, BigDecimal>>();
		if (srcMap == null || srcMap.isEmpty() || destMap == null || destMap.isEmpty()
				|| srcMap.size() != destMap.size()) {
			log.warn("比较两边有为空或者长度不一致");
		}
		Set<Entry<String, BigDecimal>> srcEntrys = Optional.ofNullable(srcMap).orElse(new HashMap<>(0)).entrySet();
		for (Iterator<Entry<String, BigDecimal>> it = srcEntrys.iterator(); it.hasNext();) {

			Map<String, BigDecimal> subCompMap = new HashMap<String, BigDecimal>();
			Entry<String, BigDecimal> srcEntry = it.next();
			String kpi = srcEntry.getKey();
			BigDecimal srcV = srcEntry.getValue();
			BigDecimal destV = Optional.ofNullable(destMap).orElse(new HashMap<>(0)).get(kpi);
			subCompMap.put(KEY_SRC, srcV);
			subCompMap.put(KEY_DEST, destV);
			BigDecimal compV = v2p(srcV, destV);
			subCompMap.put(KEY_COMP, compV);
			compMap.put(kpi, subCompMap);
		}
		return compMap;
	}

	/**
	 * 判断是否是一个同比环比的key
	 * 
	 * @param key
	 * @return
	 */
	public static boolean isCompKey(String key) {
		return StringUtils.endsWithAny(key, "_tv", "_tr", "_hv", "_hr");
	}

	public static boolean isNotCompKey(String key) {
		return !isCompKey(key);
	}

	/**
	 * 网吧评分相关的KEY初始化
	 * 
	 * @param keyTpl
	 * @return
	 */
	public static List<String> initScoreKey(String keyTpl) {

		List<String> ks = new ArrayList<String>();
		for (int i = 0; i <= 100; i++) {
			// B_S_AU_%s_V
			String key = String.format(keyTpl, i);
			ks.add(key);
		}
		return ks;
	}

	/**
	 * 网吧等级相关key集合
	 * 
	 * @return
	 */
	public static List<String> initLevelKeys() {
		List<String> ks = new ArrayList<String>();
		ks.add("B_S_AUL_LA");
		ks.add("B_S_AUL_LB");
		ks.add("B_S_AUL_LC");
		ks.add("B_S_AUL_LD");
		ks.add("P_S_AUL_LA");
		ks.add("P_S_AUL_LB");
		ks.add("P_S_AUL_LC");
		ks.add("P_S_AUL_LD");

		ks.add("B_S_CL_LA");
		ks.add("B_S_CL_LB");
		ks.add("B_S_CL_LC");
		ks.add("B_S_CL_LD");
		ks.add("P_S_CL_LA");
		ks.add("P_S_CL_LB");
		ks.add("P_S_CL_LC");
		ks.add("P_S_CL_LD");
		return ks;
	}

	public static void main(String[] args) {

		try {
//			System.out.println(rowkey(2019, "B_VA", "V", "D"));
//			String srcBeginDate = "2018-11-19";
//			String srcEndDate = "2019-11-10";
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			Date beginDate = sdf.parse(srcBeginDate);
//			Date endDate = sdf.parse(srcEndDate);
//			int gapYears = DateUtil.gapYears(srcBeginDate, srcEndDate);
//			System.out.println(gapYears);
//			kpis("B_VA", "2019-1", "2019-1", CycleEnum.WEEK, "V").stream().forEach(item -> {
//				System.out.println(item);
//			});
//			System.out.println("P_PR_A_V_D".hashCode());

//			System.out.println(Math.abs("B_VA_TBR_D".hashCode()));
//			System.out.println("P_PR_A_HBR_D".hashCode());
//			System.out.println(v2p(new BigDecimal(13551), new BigDecimal(14606)));
//			List<BigDecimal> vs = new ArrayList<BigDecimal>();
//			vs.add(BigDecimal.ONE);
//			vs.add(BigDecimal.ONE);
//			vs.add(BigDecimal.ONE);
//			vs.add(BigDecimal.ZERO);
//			System.out.println(list2Avg(vs, true));
//			List<BigDecimal> vs = new ArrayList<BigDecimal>();
//			vs.add(new BigDecimal(17970));
//			vs.add(new BigDecimal(15934));
//			vs.add(new BigDecimal(6747));
//			vs.add(new BigDecimal(8295));
//			System.out.println(list2Avg(vs, false));
//			System.out.println(date2int("2019-3"));
//			ObjectValue[] ovs = new ObjectValue[10];
//			System.out.println(Arrays.asList(ovs).size());
//			BigDecimal avg = new BigDecimal(31.5833);
//			avg = avg.setScale(0, BigDecimal.ROUND_HALF_UP);
//			System.out.println(avg);
			
			BigDecimal[] bs =new BigDecimal[10];
			List<BigDecimal> blist = Arrays.asList(bs);
			blist.set(3, BigDecimal.ZERO);
			
			blist.forEach(System.out::println);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	private static long toSortMap(Set<String> keys) {
//		
//		Map<String, List<Value>> _dat = new HashMap<String, List<Value>>();
//		Map<String, List<String>> map = new HashMap<String, List<String>>();
//		keys.stream().forEach(key -> {
//			//_key=B_A_V_D  key=19_B_A_V_D
//			String _key = key.substring(3);
//			List<String> ks = map.get(_key);
//			if(null == ks) {
//				ks = new ArrayList<String>();
//				map.put(_key, ks);
//			}
//			ks.add(key);
//		});
//		Set<Entry<String, List<String>>> entrys = map.entrySet();
//		for(Iterator<Entry<String, List<String>>> it = entrys.iterator() ; it.hasNext() ;) {
//			
//			Entry<String, List<String>> entry = it.next();
//			List<String> ks = entry.getValue();
//			ks.stream().forEach(k -> {
//				
//				
//			});
//		}
//		
//		
//		List<Long> hashes = new ArrayList<Long>();
//		keys.forEach(key -> {
//			
//			int year = Integer.parseInt(StringUtils.substring(key, 0, 2));
//			int k = StringUtils.substring(key, 2).hashCode();
//		});
//		return 1L;
//	}

	/**
	 * 将src和dest结果集合并为最终输出格式
	 * 
	 * @param srcMap
	 * @param destMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> map2Comp(Map<String, Object> srcMap, Map<String, Object> destMap) {

		if (null != srcMap && !srcMap.isEmpty() && null != destMap) {
			// destMap为null表示没有对比
			Map<String, BigDecimal> srcCardMap = (Map<String, BigDecimal>) srcMap.remove(RESULT_CARD);
			Map<String, BigDecimal> destCardMap = (Map<String, BigDecimal>) destMap.get(RESULT_CARD);
			Set<Entry<String, BigDecimal>> entrys = srcCardMap.entrySet();
			Map<String, Map<String, BigDecimal>> compMap = new HashMap<String, Map<String, BigDecimal>>();
			for (Iterator<Entry<String, BigDecimal>> it = entrys.iterator(); it.hasNext();) {

				Map<String, BigDecimal> kpiMap = new HashMap<String, BigDecimal>();
				Entry<String, BigDecimal> entry = it.next();
				String srcKey = entry.getKey();
				BigDecimal srcV = entry.getValue();
				BigDecimal destV = (null == destCardMap || !destCardMap.containsKey(srcKey)) ? null
						: destCardMap.get(srcKey);
				kpiMap.put(KEY_SRC, srcV);
				kpiMap.put(KEY_DEST, destV);
				kpiMap.put(KEY_COMP, TcUtil.v2p(srcV, destV));
				compMap.put(srcKey, kpiMap);
			}
			srcMap.put(RESULT_CARD, compMap);
		}
		return srcMap;
	}

	/**
	 * 将hbase返回结果数据中相同指标的数据进行合并
	 * 
	 * @param dat
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> map2Dat(Map<String, Object> dat, OpEnum op) {

		Map<String, Object> _dat = new HashMap<String, Object>();
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		Set<String> keys = dat.keySet();
		keys.stream().forEach(key -> {
			// _key=B_A_V key=19_B_A_V_D
			String[] kArr = key.split(KEY_SPLIT);
			String[] _kArr = new String[kArr.length - 2];
			System.arraycopy(kArr, 1, _kArr, 0, kArr.length - 2);
//			String _key = key.substring(3);
			String _key = StringUtils.join(_kArr, KEY_SPLIT);
			List<String> ks = map.get(_key);
			if (null == ks) {
				ks = new ArrayList<String>();
				map.put(_key, ks);
			}
			ks.add(key);
		});
		/**
		 * map => B_A_V: - 18_B_A_V_D - 19_B_A_V_D
		 */
		Set<Entry<String, List<String>>> entrys = map.entrySet();
		Map<String, BigDecimal> cardMap = new HashMap<String, BigDecimal>();
		for (Iterator<Entry<String, List<String>>> it = entrys.iterator(); it.hasNext();) {

			List<Value> vlist = new ArrayList<Value>();
			Entry<String, List<String>> entry = it.next();
			String _key = entry.getKey(); // B_A_V
			List<String> ks = entry.getValue();
			ks.stream().forEach(k -> {
				vlist.addAll((Collection<? extends Value>) dat.get(k));
			});
			Collections.sort(vlist);
			List<BigDecimal> vols = new ArrayList<BigDecimal>();
			vlist.stream().forEach(v -> {
				vols.add(v.getV());
			});
			_dat.put(_key, vols);

			if (null != op && !vols.isEmpty()) {
				switch (op) {
				case AVG: {

//						if(!vols.isEmpty()) {
//							
//							BigDecimal sum = BigDecimal.ZERO;
//							BigDecimal size = BigDecimal.ZERO;
//							for(BigDecimal vol : vols){
//								sum = sum.add(vol);
//								size = size.add(BigDecimal.ONE);
//							}
//							if(size.intValue() != 0) {
//								cardMap.put(_key, sum.divide(size, BigDecimal.ROUND_HALF_UP));
//							}
//						}
					BigDecimal avg = list2Avg(vols, false);
//					if (null != avg) {
						cardMap.put(_key, avg);
//					}
				}
					;
					break;
				case LAST: {
					BigDecimal last = vols.get(vols.size() - 1);
					cardMap.put(_key, last);
				}
				default: {

				}
				}
			}
		}
		if (!cardMap.isEmpty()) {
			_dat.put(RESULT_CARD, cardMap);
		}
		return _dat;
	}

	/**
	 * 将hbase返回结果数据中相同指标的数据进行合并
	 * 
	 * @param dat
	 * @return
	 */
	public static Map<String, Object> merge(Map<String, List<ObjectValue>> dat, OpEnum op, String srcBeginDate,
			String srcEndDate, CycleEnum cycle) {

		Map<String, Object> _dat = new HashMap<String, Object>();
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		Set<String> keys = dat.keySet();
		keys.stream().forEach(key -> {
			// _key=B_A_V key=19_B_A_V_D
			String[] kArr = key.split(KEY_SPLIT);
			String[] _kArr = new String[kArr.length - 2];
			System.arraycopy(kArr, 1, _kArr, 0, kArr.length - 2);
//			String _key = key.substring(3);
			String _key = StringUtils.join(_kArr, KEY_SPLIT);
			List<String> ks = map.get(_key);
			if (null == ks) {
				ks = new ArrayList<String>();
				map.put(_key, ks);
			}
			ks.add(key);
		});
		/**
		 * map => B_A_V: - 18_B_A_V_D - 19_B_A_V_D
		 */
		Set<Entry<String, List<String>>> entrys = map.entrySet();
		Map<String, BigDecimal> cardMap = new HashMap<String, BigDecimal>();
		// 曲线长度
		int len = DateUtil.between(srcBeginDate, srcEndDate, cycle);
		for (Iterator<Entry<String, List<String>>> it = entrys.iterator(); it.hasNext();) {

			ObjectValue[] ovs = new ObjectValue[len];
			Entry<String, List<String>> entry = it.next();
			String _key = entry.getKey(); // B_A_V
			List<String> ks = entry.getValue();
			ks.stream().forEach(k -> {
				dat.get(k).stream().forEach(item -> {
					ovs[item.getIndex()] = item;
				});
			});
			List<BigDecimal> vols = new ArrayList<BigDecimal>();
			List<ObjectValue> vlist = Arrays.asList(ovs);
			vlist.stream().forEach(v -> {
				vols.add(null != v ? v.getV2BigDecimal() : null);
			});
			_dat.put(_key, vols);

			if (null != op && !vols.isEmpty()) {
				switch (op) {
				case AVG: {
					BigDecimal avg = list2Avg(vols, false);
					cardMap.put(_key, avg);
				}
					;
					break;
				case LAST: {
					BigDecimal last = vols.get(vols.size() - 1);
					cardMap.put(_key, last);
				}
					;
					break;
				case SUM: {
					Optional<BigDecimal> sumOp = vols.stream().filter(Objects::nonNull).reduce((x, y) -> x.add(y));
					BigDecimal sum = sumOp.isPresent() ? sumOp.get() : BigDecimal.ZERO;
					cardMap.put(_key, sum);
				}
					;
					break;
				default: {

				}
				}
			}
		}
		if (!cardMap.isEmpty()) {
			_dat.put(RESULT_CARD, cardMap);
		}
		return _dat;
	}

	public static List<String> initPluginKey(Set<String> plugins) {

		List<String> kpis = new ArrayList<String>();
		if (null != plugins && !plugins.isEmpty()) {
			plugins.stream().forEach(plugin -> {

				String kpi = String.format("PI_%s", plugin);
				kpis.add(kpi);
			});
		}
		return kpis;
	}

	/**
	 * 由Json对象变成指标对象
	 * 目前只服务于全国插件
	 * 返回的为  指标 对应的数据集合
	 * @param mergeDat
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toObjMap(Map<String, Object> mergeDat, List<String> kpis) {

		Map<String, Object> objMap = new HashMap<String, Object>();
		Collection<Object> colls = mergeDat.values();
		for (Iterator<Object> it = colls.iterator(); it.hasNext();) {
			//都是json字符串..这里it.next应该只有一个值
			List<String> jsonList = (List<String>) it.next();
			for (String jsonStr : jsonList) {

				JSONObject _obj = StringUtils.isNotBlank(jsonStr) ? JSON.parseObject(jsonStr) : null;
				for(String kpi : kpis) {
					
					if (!StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_ID)
							&& !StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_NAME)
							&& !StringUtils.equals(kpi, JsonDict.B_DE_PI_DE_PLUGIN_TYPE)) {
						
						List<Object> vlist = (List<Object>) objMap.get(kpi);
						if (null == vlist) {
							vlist = new ArrayList<Object>();
							objMap.put(kpi, vlist);
						}
						
						Object v = null != _obj ? _obj.get(kpi) : null;
						vlist.add(v);
					}
				}
//				if (StringUtils.isNotBlank(jsonStr)) {
//
//					JSONObject jsonObj = JSON.parseObject(jsonStr);
//					Set<String> jsonKeys = jsonObj.keySet();
//					for (String jsonKey : jsonKeys) {
//
//						if (StringUtils.isNotBlank(jsonKey) && !isNotNumberKpi(jsonKey)) {
//
//							List<Object> vlist = (List<Object>) objMap.get(jsonKey);
//							if (null == vlist) {
//								vlist = new ArrayList<Object>();
//								objMap.put(jsonKey, vlist);
//							}
//							Object v = jsonObj.get(jsonKey);
//							vlist.add(v);
//						}
//					}
//				}else {
//					//如果json为null
//				}
			}
		}
		return objMap;
	}

	/**
	 * 插件数据中 插件id 插件名称 和 插件类型 不做计算
	 * 
	 * @param jsonKey
	 * @return
	 */
	private static boolean isNotNumberKpi(String jsonKey) {

		return StringUtils.equalsAnyIgnoreCase(jsonKey, JsonDict.B_DE_PI_DE_PLUGIN_ID)
				|| StringUtils.equalsAnyIgnoreCase(jsonKey, JsonDict.B_DE_PI_DE_PLUGIN_NAME)
				|| StringUtils.equalsAnyIgnoreCase(jsonKey, JsonDict.B_DE_PI_DE_PLUGIN_TYPE);
	}
}
