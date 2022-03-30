package com.stnts.bi.plugin.special.handler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.plugin.common.Constants;
import com.stnts.bi.plugin.common.DateEnum;
import com.stnts.bi.sql.entity.OlapChartDimension;
import com.stnts.bi.sql.entity.OlapChartMeasure;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.util.JacksonUtil;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartParameterVO.ConditionVO;
import com.stnts.bi.sql.vo.QueryChartResultVO.DimensionData;
import com.stnts.bi.sql.vo.QueryChartResultVO.MeasureData;
import com.stnts.bi.sql.vo.QueryChartResultVO;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.Data;

/**
 * @author liang.zhang
 * @date 2020年7月7日
 * @desc TODO
 */
@Data
public abstract class BaseHandler implements Handler, Constants {

    public static final String PRE_WEEK = "lastWeek";
    public static final String PRE_MONTH = "lastMonth";
    // 高级计算自定义key
    public static final String COMP_PRE_WEEK_KEY = "pre_week_avg";
    public static final String COMP_PRE_MONTH_KEY = "pre_month_avg";

    public static final String PARTITION_COLNAME = "partition_date";

    /**
     * 天级数据 插件选择对应的视图进行查询
     *
     * @param queryChartParameterVO
     */
    protected void choiceTable(QueryChartParameterVO queryChartParameterVO) {

//		List<String> names = queryChartParameterVO.getDashboard().stream().map(ConditionVO::getName).collect(Collectors.toList());
//		int plugin = 0 , channel = 0 , product = 0;
//		plugin = names.contains("plugin_id")  || names.contains("plugin_name") ? 1 : plugin;
//		channel = names.contains("channel_id") || names.contains("channel_name") ? 1 : channel;
//		product = names.contains("product_id") || names.contains("product_name") ? 1 : product;
//		String tableName = String.format("plugin_dwb_realtime_view_%s%s%s", plugin, channel, product);
//		queryChartParameterVO.setTableName(tableName);

//		choiceTable("plugin_dwb_realtime_view", queryChartParameterVO);
        choiceTable("plugin_dwb_realtime_daily", queryChartParameterVO);
    }

    protected void choiceTable(String tableTempate, QueryChartParameterVO queryChartParameterVO) {

        List<String> names = queryChartParameterVO.getDashboard().stream().map(ConditionVO::getName)
                .collect(Collectors.toList());
        int plugin = 0, channel = 0, product = 0;
        plugin = names.contains("plugin_id") || names.contains("plugin_name") ? 1 : plugin;
        channel = names.contains("channel_id") || names.contains("channel_name") ? 1 : channel;
        product = names.contains("product_id") || names.contains("product_name") || names.contains("err_code") ? 1
                : product;
        // plugin_dwb_realtime_view
        String tableName = String.format("%s_%s%s%s", tableTempate, plugin, channel, product);
        queryChartParameterVO.setTableName(tableName);
    }

    protected boolean isPartitionCol(OlapChartDimension cond) {
        return StringUtils.equals(cond.getName(), PARTITION_COLNAME);
    }

    /**
     * 替换dashboard中的日期值
     *
     * @param conds
     * @param preWeek
     */
    protected void setDashboardDate(List<ConditionVO> conds, String preWeek) {

        if (CollectionUtil.isNotEmpty(conds)) {
            conds.stream().filter(cond -> StringUtils.equals(cond.getName(), PARTITION_COLNAME)).map(cond -> {
                cond.setValue(preWeek);
                return cond;
            }).collect(Collectors.toList());
        }
    }

    /**
     * @param condOp
     * @return
     */
    protected String initDateCond(Optional<ConditionVO> condOp) {

        if(condOp.isPresent()){
            String dateRangeStr = condOp.get().getValue();
            String endDateStr = JSON.parseArray(dateRangeStr).getString(1);
            LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String beginDateStr = endDate.minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            JSONArray jsonArr = new JSONArray(2);
            jsonArr.add(beginDateStr);
            jsonArr.add(endDateStr);
            return jsonArr.toJSONString();
        }
        return null;
    }

    /**
     * 用于判断条件中时间区间是否垮天|周|月
     *
     * @return
     */
    protected boolean isSameCycle(Optional<ConditionVO> condOp, Optional<OlapChartDimension> dim) {

        if (condOp.isPresent() && dim.isPresent()) {
            ConditionVO condVO = condOp.get();
            String dates = condVO.getValue();
            JSONArray dateArr = JSON.parseArray(dates);
            Date begin = dateArr.getDate(0);
            Date end = dateArr.getDate(1);
            switch (dim.get().getGroup()) {
                case "day":
                    return DateUtils.isSameDay(begin, end);
                // 相差不超过7天 且属于同一周
                case "week":
                    return DateUtil.between(begin, end, DateUnit.DAY) <= 7
                            && DateUtil.between(begin, end, DateUnit.WEEK) == 0;
                // 相差不超过31天 且属于同一个月
                case "month":
                    return DateUtil.between(begin, end, DateUnit.DAY) <= 31 && DateUtil.month(begin) == DateUtil.month(end);
            }
        }
        return true;
    }

    /**
     * 剔除维度中的时间维度
     *
     * @param dims
     */
    public void rmDimPartitionCol(List<OlapChartDimension> dims) {

        for (Iterator<OlapChartDimension> it = dims.iterator(); it.hasNext(); ) {
            if (it.next().getName().equals(PARTITION_COLNAME)) {
                it.remove();
            }
        }
    }

    /**
     * 将条件中日期改变
     *
     * @param conds
     */
    public void changeDate(List<ConditionVO> conds) {
        changeDate(conds, DateEnum.LAST_WEEK);
    }

    public void changeDate(List<ConditionVO> conds, DateEnum date) {

        for (Iterator<ConditionVO> it = conds.iterator(); it.hasNext(); ) {
            if (it.next().getName().equals(PARTITION_COLNAME)) {
                it.remove();
            }
        }
        conds.add(initCompareDateCond(date));
    }

    /**
     * 追加对比上周、对比上月
     *
     * @param queryChartService
     * @param queryChartParameterVO
     * @param resultVO
     */
//	protected void appendComp(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO,
//			QueryChartResultVO resultVO) {
//
//		rmDimPartitionCol(queryChartParameterVO.getDimension());
//		List<OlapChartMeasure> meas = queryChartParameterVO.getMeasure();
//		List<String> weekMeas = meas.stream().filter(mea -> StringUtils.contains(mea.getContrast(), COMP_PRE_WEEK_KEY))
//				.map(OlapChartMeasure::getAliasName).collect(Collectors.toList());
//		List<String> monthMeas = meas.stream()
//				.filter(mea -> StringUtils.contains(mea.getContrast(), COMP_PRE_MONTH_KEY))
//				.map(OlapChartMeasure::getAliasName).collect(Collectors.toList());
//
//		if (CollectionUtil.isEmpty(weekMeas) && CollectionUtil.isEmpty(monthMeas)) {
//			return;
//		}
//
//		rmMeaContrast(meas);
//
//		// week
//		Map<String, MeasureData> weekMeaDatas = new HashMap<String, QueryChartResultVO.MeasureData>();
//		if (CollectionUtil.isNotEmpty(weekMeas)) {
//			setDashboardDate(queryChartParameterVO.getDashboard(), PRE_WEEK);
//			/**
//			 * 这里有个严重的问题... 算周数据时 如果条件里没有某些维度，而上周数据是有的，此时如何做关联?
//			 */
//			QueryChartResultVO weekResultVO = queryChartService.queryChart(queryChartParameterVO);
//			weekMeaDatas = weekResultVO.getDatas().stream().filter(obj -> obj instanceof MeasureData)
//					.map(mea -> (MeasureData) mea).filter(mea -> weekMeas.contains(mea.getDisplayName()))
//					.collect(Collectors.toMap(MeasureData::getDisplayName, mea -> mea));
//		}
//
//		// month
//		Map<String, MeasureData> monthMeaDatas = new HashMap<String, QueryChartResultVO.MeasureData>();
//		if (CollectionUtil.isNotEmpty(monthMeas)) {
//			setDashboardDate(queryChartParameterVO.getDashboard(), PRE_MONTH);
//			QueryChartResultVO monthResultVO = queryChartService.queryChart(queryChartParameterVO);
//			monthMeaDatas = monthResultVO.getDatas().stream().filter(obj -> obj instanceof MeasureData)
//					.map(mea -> (MeasureData) mea).filter(mea -> monthMeas.contains(mea.getDisplayName()))
//					.collect(Collectors.toMap(MeasureData::getDisplayName, mea -> mea));
//		}
//		/**
//		 * 这里有多少种维度，周和月的对比值就有多少个.. 把原始数据变为map形式，对比上周、对比上月变为map形式，再做关联
//		 */
//
//		Map<String, MeasureData> rawData = resultVO.getDatas().stream().filter(obj -> obj instanceof MeasureData)
//				.map(mea -> (MeasureData) mea).collect(Collectors.toMap(MeasureData::getDisplayName, mea -> mea));
//		if (CollectionUtil.isNotEmpty(rawData)) {
//
//			Set<Entry<String, MeasureData>> entrys = rawData.entrySet();
//			for (Iterator<Entry<String, MeasureData>> it = entrys.iterator(); it.hasNext();) {
//
//				Entry<String, MeasureData> entry = it.next();
//				String rawKey = entry.getKey();
//				if (weekMeaDatas.containsKey(rawKey)) { // 需要对比上周
//					doComp4Week(weekMeaDatas, entry, rawKey);
//				}
//				if (monthMeaDatas.containsKey(rawKey)) { // 需要对比上月
//					doComp4Month(monthMeaDatas, entry, rawKey);
//				}
//			}
//		}
//	}

    /**
     * 追加对比上周、对比上月
     *
     * @param queryChartService
     * @param queryChartParameterVO
     * @param resultVO
     */
    protected void appendCompNew(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO,
                                 QueryChartResultVO resultVO) {

        if (CollectionUtil.isEmpty(resultVO.getDatas())) {
            return;
        }

        rmDimPartitionCol(queryChartParameterVO.getDimension());
        List<OlapChartMeasure> meas = queryChartParameterVO.getMeasure();
        List<String> weekMeas = meas.stream().filter(mea -> StringUtils.contains(mea.getContrast(), COMP_PRE_WEEK_KEY))
                .map(OlapChartMeasure::getAliasName).collect(Collectors.toList());
        List<String> monthMeas = meas.stream()
                .filter(mea -> StringUtils.contains(mea.getContrast(), COMP_PRE_MONTH_KEY))
                .map(OlapChartMeasure::getAliasName).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(weekMeas) && CollectionUtil.isEmpty(monthMeas)) {
            return;
        }

        //获取一个维度列表
        List<String> dimNames = getDimGroup(resultVO);

        rmMeaContrast(meas);
        //TODO 这里有个优化，是加上查询的维度条件...

        // week
        Map<String, Map<String, String>> weekMap = null;
        if (CollectionUtil.isNotEmpty(weekMeas)) {
            setDashboardDate(queryChartParameterVO.getDashboard(), PRE_WEEK);
            QueryChartResultVO weekResultVO = queryChartService.queryChart(queryChartParameterVO);
            weekMap = result2Map(weekResultVO, weekMeas);
        }
        // month
        Map<String, Map<String, String>> monthMap = null;
        if (CollectionUtil.isNotEmpty(monthMeas)) {
            setDashboardDate(queryChartParameterVO.getDashboard(), PRE_MONTH);
            QueryChartResultVO monthResultVO = queryChartService.queryChart(queryChartParameterVO);
            monthMap = result2Map(monthResultVO, monthMeas);
        }

        /**
         * 这里有多少种维度，周和月的对比值就有多少个.. 把原始数据变为map形式，对比上周、对比上月变为map形式，再做关联
         */
        List<String> allMeas = new ArrayList<String>();
        if (CollectionUtil.isNotEmpty(weekMeas)) {
            allMeas.addAll(weekMeas);
        }
        if (CollectionUtil.isNotEmpty(monthMeas)) {
            allMeas.addAll(monthMeas);
        }
        // map 是原始数据 通过key去找对应维度的度量值，再计算比率
        // 启动PC  2020-8-08,插件   值
        Map<String, Map<String, String>> map = result2Map(resultVO, allMeas);  //这里dimNames重复查一次

        Map<String, MeasureData> rawData = resultVO.getDatas().stream().filter(obj -> obj instanceof MeasureData)
                .map(mea -> (MeasureData) mea).filter(mea -> allMeas.contains(mea.getDisplayName())).collect(Collectors.toMap(MeasureData::getDisplayName, mea -> mea));
        if (CollectionUtil.isNotEmpty(rawData)) {

            Set<Entry<String, MeasureData>> entrys = rawData.entrySet();
            for (Iterator<Entry<String, MeasureData>> it = entrys.iterator(); it.hasNext(); ) {
                //用这个map循环主要是为了设置比较的值
                Entry<String, MeasureData> entry = it.next();
                String rawKey = entry.getKey();
                Map<String, List<String>> compMap = new HashMap<>();
                if (weekMeas.contains(rawKey))
                    compMap.put(COMPARE_PRE_WEEK_AVG_RATE, null);
                if (monthMeas.contains(rawKey))
                    compMap.put(COMPARE_PRE_MONTH_AVG_RATE, null);
                entry.getValue().setCompareMap(compMap);
                // 需要对比上周
                if (CollectionUtil.isNotEmpty(weekMap) && weekMap.containsKey(rawKey)) {
                    doComp4Week(weekMap, map, entry, rawKey, dimNames);
                }
                // 需要对比上月
                if (CollectionUtil.isNotEmpty(monthMap) && monthMap.containsKey(rawKey)) {
                    doComp4Month(monthMap, map, entry, rawKey, dimNames);
                }
            }
        }
    }

    /**
     * -把所有维度组合起来，形成行key
     * @param weekResultVO
     * @return
     */
    private List<String> getDimGroup(QueryChartResultVO weekResultVO) {

        List<List<String>> dims = weekResultVO.getDatas().stream().filter(obj -> obj instanceof DimensionData)
                .map(dim -> (DimensionData) dim).sorted(Comparator.comparingInt(DimensionData::getId))
                .map(DimensionData::getData).collect(Collectors.toList());

        return IntStream.range(0, dims.get(0).size()).mapToObj(i -> {
            List<String> dimNames = IntStream.range(0, dims.size()).mapToObj(index -> {
                String dimName = dims.get(index).get(i);
                return dimName;
            }).collect(Collectors.toList());
            return StringUtils.join(dimNames, SPLIT_KEY);
        }).collect(Collectors.toList());
    }

    /**
     * 将结果数据组装为map形式
     *
     * @param queryChartService
     * @param queryChartParameterVO
     * @param weekMeas
     * @return
     */
    private Map<String, Map<String, String>> result2Map(QueryChartResultVO weekResultVO, List<String> weekMeas) {

        // 度量的key 维度 度量值
        Map<String, Map<String, String>> weekMap = null;
        if (CollectionUtil.isNotEmpty(weekMeas) && CollectionUtil.isNotEmpty(weekResultVO.getDatas())) {

            // dims = 维度集合
            List<List<String>> dims = weekResultVO.getDatas().stream().filter(obj -> obj instanceof DimensionData)
                    .map(dim -> (DimensionData) dim).sorted(Comparator.comparingInt(DimensionData::getId))
                    .map(DimensionData::getData).collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(dims)) { // 结果有数据

//				List<String> dimNames = new ArrayList<String>();
//				IntStream.range(0, dims.get(0).size()).forEach(i -> {
//					StringBuilder sb = new StringBuilder();
//					IntStream.range(0, dims.size()).forEach(index -> {
//						String dimName = dims.get(index).get(i);
//						sb.append(dimName).append(SPLIT_KEY);
//					});
//					dimNames.add(sb.toString());
//				});
                List<String> dimNames = getDimGroup(weekResultVO);

                //// 拿到周的度量数据,接下来是绑定度量和维度的关系
                weekMap = weekResultVO.getDatas().stream().filter(obj -> obj instanceof MeasureData)
                        .map(mea -> (MeasureData) mea).filter(mea -> weekMeas.contains(mea.getDisplayName()))
                        .collect(Collectors.toMap(MeasureData::getDisplayName, mea -> {

                            Map<String, String> dimAndMea = new HashMap<String, String>();
                            List<String> vs = mea.getData();
                            IntStream.range(0, vs.size()).forEach(i -> {
                                dimAndMea.put(dimNames.get(i), vs.get(i));
                            });
                            return dimAndMea;
                        }));
            }

//			System.out.println(JSON.toJSONString(weekMap));
        }

        return weekMap;
    }

    /**
     * 剔除度量信息中的高级计算部分
     *
     * @param meas
     */
    protected void rmMeaContrast(List<OlapChartMeasure> meas) {
        meas.stream().forEach(mea -> mea.setContrast(null));
    }

    /**
     * @param dList  data list
     * @param cdList compare data list
     * @return
     */
    public List<String> toCompareRateList(List<String> dList, List<String> cdList) {

        if (null == dList || dList.isEmpty() || null == cdList) {
            return null;
        }
        String[] compareRateArr = new String[dList.size()];
        Arrays.fill(compareRateArr, EMPTY_V);
        for (int i = 0; i < dList.size(); i++) {
            try {
                BigDecimal src = new BigDecimal(dList.get(i));
                BigDecimal dest = new BigDecimal(cdList.get(i));
                compareRateArr[i] = String.valueOf(src.subtract(dest).divide(dest, 4, BigDecimal.ROUND_HALF_UP));
            } catch (Exception e) {
            }
        }

        return Arrays.asList(compareRateArr);
    }

    /**
     * 求增长率
     *
     * @param src
     * @param dest
     * @return
     */
    public String ratio(String src, String dest) {

        try {
            BigDecimal _src = new BigDecimal(src);
            BigDecimal _dest = new BigDecimal(dest);
            return String.valueOf(_src.subtract(_dest).divide(_dest, 4, BigDecimal.ROUND_HALF_UP));
//			return String.valueOf(_src.divide(_dest, 4, BigDecimal.ROUND_HALF_UP).doubleValue() - 1);
        } catch (Exception e) {
        }
        return EMPTY_V;
    }

    /**
     * 获取对比日期条件
     *
     * @param dateColumnName
     * @param dateV
     * @return
     */
    public ConditionVO initCompareDateCond(String dateColumnName, DateEnum dateV) {
        ConditionVO lastWeekCond = new ConditionVO();
        lastWeekCond.setName(dateColumnName);
        lastWeekCond.setLogic("between");
        lastWeekCond.setValue(dateV.getKey());
        lastWeekCond.setFunc("day");
        return lastWeekCond;
    }

//	public ConditionVO initPartitionCond(String dateValue, String group) {
//		ConditionVO lastWeekCond  = new ConditionVO();
//		lastWeekCond.setName(PARTITION_COLNAME);
//		lastWeekCond.setLogic("between");
//		lastWeekCond.setValue(dateValue);
//		lastWeekCond.setFunc(group);
//		return lastWeekCond;
//	}

    /**
     * 初始化 时间周期 维度
     *
     * @param group
     * @return
     */
    public OlapChartDimension initPartitionDim(Optional<ConditionVO> condVO) {

        if (condVO.isPresent()) {

            OlapChartDimension dim = new OlapChartDimension();
            dim.setName(PARTITION_COLNAME);
            dim.setAliasName("周期");
            dim.setGroup(condVO.get().getFunc());
            return dim;
        }
        return null;
    }

    public ConditionVO initCompareDateCond(DateEnum dateV) {
        return initCompareDateCond(PARTITION_COLNAME, dateV);
    }

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService,
                                                    QueryChartParameterVO queryChartParameterVO) {
        return ResultEntity.success(queryChartService.queryChart(queryChartParameterVO));
    }

    public void doComp4Week(Map<String, Map<String, String>> weekMap, Map<String, Map<String, String>> map,
                            Entry<String, MeasureData> entry, String rawKey, List<String> dimNames) {
        doCompNew(weekMap, map, entry, rawKey, COMPARE_PRE_WEEK_AVG_RATE, dimNames);
    }

    public void doComp4Month(Map<String, Map<String, String>> weekMap, Map<String, Map<String, String>> map,
                             Entry<String, MeasureData> entry, String rawKey, List<String> dimNames) {
        doCompNew(weekMap, map, entry, rawKey, COMPARE_PRE_MONTH_AVG_RATE, dimNames);
    }

    public void appendLimit104Channel(List<ConditionVO> conds, QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {
        appendLimit10(conds, queryChartService, queryChartParameterVO, PluginCross4Channel.TABLE_NAME, PluginCross4Channel.PARAM_CHANNEL);
    }

    public void appendLimit104Plugin(List<ConditionVO> conds, QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {
        appendLimit10(conds, queryChartService, queryChartParameterVO, PluginCross4Plugin.TABLE_NAME, PluginCross4Plugin.PARAM_PLUGIN);
    }

    /**
     * 只显示10个
     */
    public void appendLimit10(List<ConditionVO> conds, QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO, String tableName, String limitParam) {

        //有且只有一个日期条件  需要给默认limit10
        if (CollectionUtil.isNotEmpty(conds) && conds.size() == 1 && StringUtils.equals(conds.get(0).getName(), PARTITION_COLNAME)) {

            QueryChartParameterVO channelParamVO = JacksonUtil.fromJSON(limitParam, QueryChartParameterVO.class);
            queryChartParameterVO.setTableName(tableName);
            channelParamVO.setDashboard(conds);
            QueryChartResultVO channelResultVO = queryChartService.queryChart(channelParamVO);
            if (CollectionUtil.isNotEmpty(channelResultVO.getDatas())) {
                DimensionData dd = (DimensionData) channelResultVO.getDatas().get(0);
                String channelIds = JSON.toJSONString(dd.getData());
                ConditionVO lCond = new ConditionVO();
                lCond.setName("lid");
                lCond.setLogic("in");
                lCond.setValue(channelIds);

                ConditionVO rCond = new ConditionVO();
                rCond.setName("rid");
                rCond.setLogic("in");
                rCond.setValue(channelIds);

                conds.add(lCond);
                conds.add(rCond);
            }
        }
    }

//	public void doComp4Week(Map<String, MeasureData> weekMeaDatas, Entry<String, MeasureData> entry, String rawKey) {
//		doComp(weekMeaDatas, entry, rawKey, 1);
//	}
//
//	public void doComp4Month(Map<String, MeasureData> weekMeaDatas, Entry<String, MeasureData> entry, String rawKey) {
//		doComp(weekMeaDatas, entry, rawKey, 2);
//	}

    /**
     *  - 处理单个指标的 对比上周 和 对比上月 数据
     * @param weekMap  不带日期维度的值
     * @param map  带维度的值
     * @param entry  不带维度的值
     * @param rawKey  指标
     * @param type
     * @param dimNames
     */
    private void doCompNew(Map<String, Map<String, String>> weekMap, Map<String, Map<String, String>> map,
                           Entry<String, MeasureData> entry, String rawKey, String cycleKey, List<String> dimNames) {

        List<String> md = entry.getValue().getData(); // 原始数据

        String[] compareData = new String[md.size()];
        Arrays.fill(compareData, EMPTY_V);

        //TODO 这里改为不要通过原始数据map去找对比数据，而是通过dimNames顺序查找原始map和对比map
        IntStream.range(0, dimNames.size()).forEach(index -> {
            try {
                String dimName = dimNames.get(index);
                String joinKey = dimName.substring(dimName.indexOf(SPLIT_KEY) + 1);
                String colData = map.get(rawKey).get(dimName);
                String cycleData = weekMap.get(rawKey).get(joinKey);
                compareData[index] = ratio(colData, cycleData);
            } catch (Exception e) {
                compareData[index] = EMPTY_V;
            }
        });

//		Set<Entry<String, String>> datEntrys = datDict.entrySet();
//		int i = 0;
//		for (Iterator<Entry<String, String>> it = datEntrys.iterator(); it.hasNext();) {
//
//			Entry<String, String> datEntry = it.next();
//			String datDim = datEntry.getKey();
//			String datMea = datEntry.getValue();
//			String joinKey = datDim.substring(datDim.indexOf(SPLIT_KEY) + 1);
//			String weekMea = weekDict.get(joinKey);
////			System.out.println("joinKey: " + joinKey + ", datMea: " + datMea + ", weekMea: " + weekMea + ", rawKey: " + rawKey + ", cycle: " + cycleKey);
//			compareData[i++] = ratio(datMea, weekMea);
//		}

        List<String> weekList = Arrays.asList(compareData);
        Map<String, List<String>> compMap = entry.getValue().getCompareMap();
        compMap = compMap == null ? new HashMap<String, List<String>>() : compMap;
        compMap.put(cycleKey, weekList);
        entry.getValue().setCompareMap(compMap);
    }

//	private void doComp(Map<String, MeasureData> weekMeaDatas, Entry<String, MeasureData> entry, String rawKey,
//			int type) {
//
//		String cycleKey = type == 1 ? COMPARE_PRE_WEEK_AVG_RATE : COMPARE_PRE_MONTH_AVG_RATE;
//		List<String> md = entry.getValue().getData(); // 原始数据
//		List<String> weekMd = weekMeaDatas.get(rawKey).getData(); // 周数据
//		String[] compareData = new String[md.size()];
//		Arrays.fill(compareData, EMPTY_V);
//		for (int i = 0; i < md.size(); i++) {
//			int index = i % weekMd.size();
//			// 计算增长率
//			compareData[i] = ratio(md.get(i), weekMd.get(index));
//		}
//		List<String> weekList = Arrays.asList(compareData);
//		Map<String, List<String>> compMap = entry.getValue().getCompareMap();
//		compMap = compMap == null ? new HashMap<String, List<String>>() : compMap;
//		compMap.put(cycleKey, weekList);
//		entry.getValue().setCompareMap(compMap);
//	}

    /**
     * 追加对比增长率
     */
    protected void appendRate(QueryChartResultVO.MeasureData mData, Map<String, List<String>> compareMap) {

        List<String> dList = mData.getData();
        List<String> cdList = mData.getCompareData();
        compareMap.put(COMPARE_RATE, toCompareRateList(dList, cdList));
    }

    /**
     * 追加对比增长率
     */
    protected void appendCompare(QueryChartResultVO.MeasureData mData, QueryChartService queryChartService,
                                 QueryChartParameterVO queryChartParameterVO, Map<String, List<String>> compareMap, DateEnum cycle) {
        // 改变时间条件
        changeDate(queryChartParameterVO.getDashboard(), cycle);
        QueryChartResultVO _resultVO = queryChartService.queryChart(queryChartParameterVO);
        QueryChartResultVO.MeasureData _mData = (MeasureData) _resultVO.getDatas().get(0);
        List<String> dList = mData.getData();
        List<String> cdList = _mData.getData();

        String dataKey = cycle.equals(DateEnum.LAST_WEEK) ? COMPARE_LAST_WEEK : COMPARE_LAST_MONTH;
        String rateKey = cycle.equals(DateEnum.LAST_WEEK) ? COMPARE_LAST_WEEK_RATE : COMPARE_LAST_MONTH_RATE;

        compareMap.put(dataKey, cdList);
        compareMap.put(rateKey, toCompareRateList(dList, cdList));
    }

    /**
     * top场景，追加对比上周、对比上月
     * @param queryChartParameterVO
     * @param topResultVO
     */
    protected void appendContrast(QueryChartParameterVO queryChartParameterVO, QueryChartResultVO topResultVO, QueryChartService queryChartService, DateEnum dateCycle) {

        if (CollectionUtil.isNotEmpty(topResultVO.getDatas())) {

            QueryChartResultVO.DimensionData nameData = (DimensionData) topResultVO.getDatas().get(0);
            QueryChartResultVO.MeasureData meaData = (MeasureData) topResultVO.getDatas().get(1);

            // 添加对比上周条件
//			List<ConditionVO> conds = new ArrayList<QueryChartParameterVO.ConditionVO>();
//			ConditionVO dateCond  = initCompareDateCond(DateEnum.LAST_WEEK);
//			conds.add(dateCond);

            ConditionVO dimNameCond = new ConditionVO();
            dimNameCond.setName(nameData.getName());
            dimNameCond.setLogic("in");
            // 这个顺序跟后面的不一样
            dimNameCond.setValue(JSON.toJSONString(nameData.getData()));
//			conds.add(dimNameCond);

            List<ConditionVO> conds = queryChartParameterVO.getDashboard();
            changeDate(conds, dateCycle);
            conds.add(dimNameCond);

            QueryChartResultVO _topResultVO = queryChartService.queryChart(queryChartParameterVO);

            List<Object> datas = _topResultVO.getDatas();

            String datKey = dateCycle == DateEnum.LAST_WEEK ? COMPARE_LAST_WEEK : COMPARE_LAST_MONTH;
            String rateKey = dateCycle == DateEnum.LAST_WEEK ? COMPARE_LAST_WEEK_RATE : COMPARE_LAST_MONTH_RATE;

            if (CollectionUtil.isNotEmpty(datas)) {

                QueryChartResultVO.DimensionData _dimData = (DimensionData) _topResultVO.getDatas().get(0);
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

//				meaData.setCompareData(compareList);


                List<String> compareRate = toCompareRateList(meaData.getData(), compareList);
                Map<String, List<String>> compareMap = meaData.getCompareMap();
                compareMap = null == compareMap ? new HashMap<String, List<String>>() : compareMap;
                compareMap.put(datKey, compareList);
                compareMap.put(rateKey, compareRate);
                meaData.setCompareMap(compareMap);
            }
//			else { // 前端要这个格式
//				Map<String, List<String>> compareMap = new HashMap<String, List<String>>();
//				compareMap.put(datKey, null);
//				compareMap.put(rateKey, null);
//				meaData.setCompareMap(compareMap);
//			}
        }
    }
}
