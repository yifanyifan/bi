package com.stnts.bi.dashboard.handlers;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.dashboard.config.Constants;
import com.stnts.bi.dashboard.vo.QueryChartResultForSankeyVO;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/10/22
 */
public abstract class BaseHandler implements Handler, Constants {

    public static final String separator = "|||";

//    public void setAggDimCond(QueryChartParameterVO queryChartParameterVO, String handlerId){
//
//        List<QueryChartParameterVO.ConditionVO> conds = queryChartParameterVO.getDashboard();
//        QueryChartParameterVO.ConditionVO aggDimCond = initAggDimCond(handlerId, conds);
//        conds.add(aggDimCond);
//    }

//    /**
//     * 组装agg_dim条件
//     * @param handlerId
//     * @return
//     */
//    public QueryChartParameterVO.ConditionVO initAggDimCond(String handlerId, List<QueryChartParameterVO.ConditionVO> conds){
//
//        Map<String, String> collect = conds.stream().collect(Collectors.toMap(QueryChartParameterVO.ConditionVO::getName, QueryChartParameterVO.ConditionVO::getValue));
//        String version = collect.getOrDefault("channel_sea", KEY_VERSION_ALL);
//        String channel = collect.getOrDefault("channel_id", KEY_CHANNEL_ALL);
//        QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
//        conditionVO.setName(COL_AGG_DIM);
//        conditionVO.setLogic(FilterLogicConstant.EQ);
//        String value = null;
//        switch (handlerId){
//
//            case Handler001.HANDLER_ID : value = toAggDim(KEY_CYCLE_D, version, channel, null); break;
//            case Handler002.HANDLER_ID : value = toAggDim(KEY_CYCLE_D, version, channel, "rt") ; break;
//        }
//        Assert.notNull(value);
//        conditionVO.setValue(value);
//        return conditionVO;
//    }
//
//    private String toAggDim(String cycle, String version, String channel, String module){
//
//        List<String> aggDimList = new ArrayList<>();
//        aggDimList.add(cycle);
//        aggDimList.add(StringUtils.equals(version, KEY_VERSION_ALL) ? null : KEY_VERSION);
//        aggDimList.add(StringUtils.equals(channel, KEY_CHANNEL_ALL) ? null : KEY_CHANNEL);
//        aggDimList.add(module);
//        aggDimList.removeIf(Objects::isNull);
//        return StringUtils.join(aggDimList, KEY_SEPARATOR);
//    }

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {
        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
        return ResultEntity.success(queryChartResultVO);
    }

    /**
     * 将dashboard中上传的日条件格式化为月
     * @param dateKey
     * @param queryChartParameterVO
     */
    public void changeDay2Month(String dateKey, QueryChartParameterVO queryChartParameterVO) {

        try {
            List<QueryChartParameterVO.ConditionVO> conditionVOS = Optional.ofNullable(queryChartParameterVO.getDashboard()).orElse(Collections.emptyList());
            Optional<QueryChartParameterVO.ConditionVO> first = conditionVOS.stream().filter(vo -> {
                return StringUtils.equals(dateKey, vo.getName());
            }).findFirst();
            if (first.isPresent()) {
                QueryChartParameterVO.ConditionVO conditionVO = first.get();
                String condV = conditionVO.getValue();
                JSONArray newMonthArr = new JSONArray();
                JSONArray oldMonthArr = JSONArray.parseArray(condV);
                for (int i = 0; i < oldMonthArr.size(); i++) {
                    String monthV = oldMonthArr.getString(i).substring(0, 7);
                    newMonthArr.add(monthV);
                }
                conditionVO.setValue(newMonthArr.toJSONString());
                conditionVO.setFunc("month");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 算占比
     * @param src
     * @param dest
     * @return
     */
    public String ratio(String src, String dest) {

        try {
            BigDecimal srcV = new BigDecimal(src);
            BigDecimal destV = new BigDecimal(dest);
            return String.valueOf(srcV.divide(destV, 4, BigDecimal.ROUND_HALF_UP));
        } catch (Exception e) {
        }
        return "0";
    }

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO, int sankeyNodeNumberPerLayer) {
        throw new NotImplementedException("未实现");
    }

    public void handleChannelAnalysis50(QueryChartResultForSankeyVO queryChartResultForSankeyVO, int nodeNumberPerLayer) {
        List<QueryChartResultForSankeyVO.SankeyNode> sankeyNodeList = new ArrayList<>();
        List<List<String>> rowFormatDataList = Optional.ofNullable(queryChartResultForSankeyVO.getRowFormatDataList()).orElse(Collections.emptyList());
        Iterator<List<String>> iterator = rowFormatDataList.iterator();
        String totalTemp = "";
        while (iterator.hasNext()) {
            List<String> next = iterator.next();
            if (isAllRollupName(CollectionUtil.sub(next, 0, 4))) {
                totalTemp = next.get(4);
                iterator.remove();
                break;
            }
        }
        final String total = totalTemp;
        String rootPrefix = "level_0";
        String layerOnePrefix = "level_1";
        String layerTwoPrefix = "level_2";
        String layerThreePrefix = "level_3";
        String layerFourPrefix = "level_4";

        String more = "更多";
        nodeNumberPerLayer = nodeNumberPerLayer - 1;
        Set<String> layerOneNodeIndex = new HashSet<>();
        Set<String> layerTwoNodeIndex = new HashSet<>();
        Set<String> layerThreeNodeIndex = new HashSet<>();
        Set<String> layerFourNodeIndex = new HashSet<>();

        Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> layerOneNameToNodeListMap = new HashMap<>();
        Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> layerTwoNameToNodeListMap = new HashMap<>();
        Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> layerThreeNameToNodeListMap = new HashMap<>();
        Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> layerFourNameToNodeListMap = new HashMap<>();

        Map<String, QueryChartResultForSankeyVO.SankeyNode> layerThreeSourceTargetToNodeMap = new HashMap<>();
        Map<String, QueryChartResultForSankeyVO.SankeyNode> layerFourSourceTargetToNodeMap = new HashMap<>();

        Iterator<List<String>> iterator1 = rowFormatDataList.iterator();
        while (iterator1.hasNext()) {
            List<String> strings = iterator1.next();
            if (isAllRollupName(CollectionUtil.sub(strings, 1, 4))) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setPositionNode(false);
                sankeyNode.setSource(StrUtil.concat(false, rootPrefix, separator, QueryChartService.ROLLUP_NAME));
                sankeyNode.setSourceName(QueryChartService.ROLLUP_NAME);
                sankeyNode.setTarget(StrUtil.concat(false, layerOnePrefix, separator, strings.get(0)));
                sankeyNode.setTargetName(strings.get(0));

                sankeyNode.setValue(strings.get(4));
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                if (layerOneNodeIndex.size() >= nodeNumberPerLayer && !layerOneNodeIndex.contains(sankeyNode.getTargetName())) {
                    put(sankeyNode.getTargetName(), sankeyNode, layerOneNameToNodeListMap);
                } else {
                    sankeyNodeList.add(sankeyNode);
                    layerOneNodeIndex.add(sankeyNode.getTargetName());
                }
                iterator1.remove();
            }
        }
        Iterator<List<String>> iterator2 = rowFormatDataList.iterator();
        while (iterator2.hasNext()) {
            List<String> strings = iterator2.next();
            if (isAllRollupName(CollectionUtil.sub(strings, 2, 4))) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setPositionNode(false);
                if (layerOneNameToNodeListMap.containsKey(strings.get(0))) {
                    sankeyNode.setSource(StrUtil.concat(false, layerOnePrefix, separator, more));
                    sankeyNode.setSourceName(more);
                } else {
                    sankeyNode.setSource(StrUtil.concat(false, layerOnePrefix, separator, strings.get(0)));
                    sankeyNode.setSourceName(strings.get(0));
                }
                sankeyNode.setTarget(StrUtil.concat(false, layerTwoPrefix, separator, strings.get(1)));
                sankeyNode.setTargetName(strings.get(1));

                sankeyNode.setValue(strings.get(4));
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                if (layerTwoNodeIndex.size() >= nodeNumberPerLayer && !layerTwoNodeIndex.contains(sankeyNode.getTargetName())) {
                    put(sankeyNode.getTargetName(), sankeyNode, layerTwoNameToNodeListMap);
                } else {
                    sankeyNodeList.add(sankeyNode);
                    layerTwoNodeIndex.add(sankeyNode.getTargetName());
                }
                iterator2.remove();
            }
        }
        Iterator<List<String>> iterator3 = rowFormatDataList.iterator();
        while (iterator3.hasNext()) {
            List<String> strings = iterator3.next();
            if (isAllRollupName(CollectionUtil.sub(strings, 3, 4))) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setPositionNode(false);
                if (layerTwoNameToNodeListMap.containsKey(strings.get(1))) {
                    sankeyNode.setSource(StrUtil.concat(false, layerTwoPrefix, separator, more));
                    sankeyNode.setSourceName(more);
                } else {
                    sankeyNode.setSource(StrUtil.concat(false, layerTwoPrefix, separator, strings.get(1)));
                    sankeyNode.setSourceName(strings.get(1));
                }
                sankeyNode.setTarget(StrUtil.concat(false, layerThreePrefix, separator, strings.get(2)));
                sankeyNode.setTargetName(strings.get(2));

                sankeyNode.setValue(strings.get(4));
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                if (layerThreeNodeIndex.size() >= nodeNumberPerLayer && !layerThreeNodeIndex.contains(sankeyNode.getTargetName())) {
                    put(sankeyNode.getTargetName(), sankeyNode, layerThreeNameToNodeListMap);
                } else {
                    QueryChartResultForSankeyVO.SankeyNode exists = layerThreeSourceTargetToNodeMap.get(StrUtil.join(separator, sankeyNode.getSource(), sankeyNode.getTarget()));
                    if (!ObjectUtil.isNull(exists)) {
                        exists.setValue(NumberUtil.add(sankeyNode.getValue(), exists.getValue()).toString());
                        exists.setPercentOfTotal(getPercentOfTotal(total, exists.getValue()));
                    } else {
                        sankeyNodeList.add(sankeyNode);
                    }
                    layerThreeNodeIndex.add(sankeyNode.getTargetName());
                    layerThreeSourceTargetToNodeMap.put(StrUtil.join(separator, sankeyNode.getSource(), sankeyNode.getTarget()), sankeyNode);
                }
                iterator3.remove();
            }
        }
        Iterator<List<String>> iterator4 = rowFormatDataList.iterator();
        while (iterator4.hasNext()) {
            List<String> strings = iterator4.next();
            QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
            sankeyNode.setPositionNode(true);
            if (layerThreeNameToNodeListMap.containsKey(strings.get(2))) {
                sankeyNode.setSource(StrUtil.concat(false, layerThreePrefix, separator, more));
                sankeyNode.setSourceName(more);
            } else {
                sankeyNode.setSource(StrUtil.concat(false, layerThreePrefix, separator, strings.get(2)));
                sankeyNode.setSourceName(strings.get(2));
            }
            sankeyNode.setTarget(StrUtil.concat(false, layerFourPrefix, separator, strings.get(3)));
            sankeyNode.setTargetName(strings.get(3));

            sankeyNode.setValue(strings.get(4));
            sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
            sankeyNode.setValidPc(strings.get(5));
            if (layerFourNodeIndex.size() >= nodeNumberPerLayer && !layerFourNodeIndex.contains(sankeyNode.getTargetName())) {
                put(sankeyNode.getTargetName(), sankeyNode, layerFourNameToNodeListMap);
            } else {
                QueryChartResultForSankeyVO.SankeyNode exists = layerFourSourceTargetToNodeMap.get(StrUtil.join(separator, sankeyNode.getSource(), sankeyNode.getTarget()));
                if (!ObjectUtil.isNull(exists)) {
                    exists.setValue(NumberUtil.add(sankeyNode.getValue(), exists.getValue()).toString());
                    exists.setPercentOfTotal(getPercentOfTotal(total, exists.getValue()));
                    exists.setValidPc(NumberUtil.add(sankeyNode.getValidPc(), exists.getValidPc()).toString());
                } else {
                    sankeyNodeList.add(sankeyNode);
                }
                layerFourNodeIndex.add(sankeyNode.getTargetName());
                layerFourSourceTargetToNodeMap.put(StrUtil.join(separator, sankeyNode.getSource(), sankeyNode.getTarget()), sankeyNode);
            }
        }

        // 处理更多
        if (!layerOneNameToNodeListMap.isEmpty()) {
            QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
            sankeyNode.setSource(StrUtil.concat(false, rootPrefix, separator, QueryChartService.ROLLUP_NAME));
            sankeyNode.setSourceName(QueryChartService.ROLLUP_NAME);
            sankeyNode.setTarget(StrUtil.concat(false, layerOnePrefix, separator, more));
            sankeyNode.setTargetName(more);
            String[] values = layerOneNameToNodeListMap.values().stream().flatMap(List::stream).map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
            String[] percentOfTotals = layerOneNameToNodeListMap.values().stream().flatMap(List::stream).map(QueryChartResultForSankeyVO.SankeyNode::getPercentOfTotal).toArray(String[]::new);
            sankeyNode.setValue(NumberUtil.add(values).toString());
            sankeyNode.setPercentOfTotal(NumberUtil.add(percentOfTotals).toString());
            sankeyNode.setMore(true);
            sankeyNode.setMoreSankeyNodes(layerOneNameToNodeListMap.values().stream().flatMap(List::stream).collect(Collectors.toList()));
            sankeyNodeList.add(sankeyNode);
        }
        if (!layerTwoNameToNodeListMap.isEmpty()) {
            Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> groupByNodeName = layerTwoNameToNodeListMap.values().stream().flatMap(List::stream).collect(Collectors.groupingBy(QueryChartResultForSankeyVO.SankeyNode::getSourceName));
            List<QueryChartResultForSankeyVO.SankeyNode> sankeyMoreNodeList = new ArrayList<>();
            groupByNodeName.forEach((key, value) -> {
                if (layerOneNameToNodeListMap.containsKey(key)) {
                    sankeyMoreNodeList.addAll(value);
                    return;
                }
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setSource(StrUtil.concat(false, layerOnePrefix, separator, key));
                sankeyNode.setSourceName(key);
                sankeyNode.setTarget(StrUtil.concat(false, layerTwoPrefix, separator, more));
                sankeyNode.setTargetName(more);
                String[] values = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
                sankeyNode.setValue(NumberUtil.add(values).toString());
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                sankeyNode.setMore(true);
                sankeyNode.setMoreSankeyNodes(value);
                sankeyNodeList.add(sankeyNode);
            });
            if (CollectionUtil.isNotEmpty(sankeyMoreNodeList)) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setSource(StrUtil.concat(false, layerOnePrefix, separator, more));
                sankeyNode.setSourceName(more);
                sankeyNode.setTarget(StrUtil.concat(false, layerTwoPrefix, separator, more));
                sankeyNode.setTargetName(more);
                String[] values = sankeyMoreNodeList.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
                sankeyNode.setValue(NumberUtil.add(values).toString());
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                sankeyNode.setMore(true);
                sankeyNode.setMoreSankeyNodes(sankeyMoreNodeList);
                sankeyNodeList.add(sankeyNode);
            }
        }
        if (!layerThreeNameToNodeListMap.isEmpty()) {
            Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> groupByNodeName = layerThreeNameToNodeListMap.values().stream().flatMap(List::stream).collect(Collectors.groupingBy(QueryChartResultForSankeyVO.SankeyNode::getSourceName));
            List<QueryChartResultForSankeyVO.SankeyNode> sankeyMoreNodeList = new ArrayList<>();
            groupByNodeName.forEach((key, value) -> {
                if (layerTwoNameToNodeListMap.containsKey(key)) {
                    sankeyMoreNodeList.addAll(value);
                    return;
                }
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setSource(StrUtil.concat(false, layerTwoPrefix, separator, key));
                sankeyNode.setSourceName(key);
                sankeyNode.setTarget(StrUtil.concat(false, layerThreePrefix, separator, more));
                sankeyNode.setTargetName(more);
                String[] values = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
                sankeyNode.setValue(NumberUtil.add(values).toString());
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                sankeyNode.setMore(true);
                sankeyNode.setMoreSankeyNodes(value);
                sankeyNodeList.add(sankeyNode);
            });
            if (CollectionUtil.isNotEmpty(sankeyMoreNodeList)) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setSource(StrUtil.concat(false, layerTwoPrefix, separator, more));
                sankeyNode.setSourceName(more);
                sankeyNode.setTarget(StrUtil.concat(false, layerThreePrefix, separator, more));
                sankeyNode.setTargetName(more);
                String[] values = sankeyMoreNodeList.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
                sankeyNode.setValue(NumberUtil.add(values).toString());
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                sankeyNode.setMore(true);
                sankeyNode.setMoreSankeyNodes(sankeyMoreNodeList);
                sankeyNodeList.add(sankeyNode);
            }
        }
        if (!layerFourNameToNodeListMap.isEmpty()) {
            Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> groupByNodeName = layerFourNameToNodeListMap.values().stream().flatMap(List::stream).collect(Collectors.groupingBy(QueryChartResultForSankeyVO.SankeyNode::getSourceName));
            List<QueryChartResultForSankeyVO.SankeyNode> sankeyMoreNodeList = new ArrayList<>();
            groupByNodeName.forEach((key, value) -> {
                if (layerThreeNameToNodeListMap.containsKey(key)) {
                    sankeyMoreNodeList.addAll(value);
                    return;
                }
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setPositionNode(true);
                sankeyNode.setSource(StrUtil.concat(false, layerThreePrefix, separator, key));
                sankeyNode.setSourceName(key);
                sankeyNode.setTarget(StrUtil.concat(false, layerFourPrefix, separator, more));
                sankeyNode.setTargetName(more);
                String[] values = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
                String[] validPcs = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValidPc).toArray(String[]::new);
                sankeyNode.setValue(NumberUtil.add(values).toString());
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                sankeyNode.setValidPc(NumberUtil.add(validPcs).toString());
                sankeyNode.setMore(true);
                sankeyNode.setMoreSankeyNodes(value);
                sankeyNodeList.add(sankeyNode);
            });
            if (CollectionUtil.isNotEmpty(sankeyMoreNodeList)) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setPositionNode(true);
                sankeyNode.setSource(StrUtil.concat(false, layerThreePrefix, separator, more));
                sankeyNode.setSourceName(more);
                sankeyNode.setTarget(StrUtil.concat(false, layerFourPrefix, separator, more));
                sankeyNode.setTargetName(more);
                String[] values = sankeyMoreNodeList.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
                String[] validPcs = sankeyMoreNodeList.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValidPc).toArray(String[]::new);
                sankeyNode.setValue(NumberUtil.add(values).toString());
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                sankeyNode.setValidPc(NumberUtil.add(validPcs).toString());
                sankeyNode.setMore(true);
                sankeyNode.setMoreSankeyNodes(sankeyMoreNodeList);
                sankeyNodeList.add(sankeyNode);
            }
        }

        // 处理同一个target有多个source
        Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> groupByTarget = sankeyNodeList.stream().collect(Collectors.groupingBy(QueryChartResultForSankeyVO.SankeyNode::getTarget));
        groupByTarget.forEach((key, value) -> {
            String[] values = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
            String[] validPcs = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValidPc).toArray(String[]::new);
            String sum = NumberUtil.add(values).toString();
            List<QueryChartResultForSankeyVO.SankeyNode> sumMoreSankeyNodes = value.stream()
                    .map(QueryChartResultForSankeyVO.SankeyNode::getMoreSankeyNodes)
                    .filter(CollectionUtil::isNotEmpty)
                    .flatMap(Collection::stream)
                    .sorted(Comparator.comparing((QueryChartResultForSankeyVO.SankeyNode v) -> NumberUtil.toBigDecimal(v.getValue())).reversed())
                    .collect(Collectors.toList());
            Iterator<QueryChartResultForSankeyVO.SankeyNode> sumMoreSankeyNodesIterator = sumMoreSankeyNodes.iterator();
            // 处理target重复的情况
            Map<String, QueryChartResultForSankeyVO.SankeyNode> targetToSankeyNodeMap = new HashMap<>(sumMoreSankeyNodes.size());
            while (sumMoreSankeyNodesIterator.hasNext()) {
                QueryChartResultForSankeyVO.SankeyNode next = sumMoreSankeyNodesIterator.next();
                QueryChartResultForSankeyVO.SankeyNode exist = targetToSankeyNodeMap.get(next.getTarget());
                if (exist != null) {
                    exist.setValue(NumberUtil.add(exist.getValue(), next.getValue()).toString());
                    exist.setPercentOfTotal(getPercentOfTotal(total, exist.getValue()));
                    exist.setValidPc(NumberUtil.add(exist.getValidPc(), next.getValidPc()).toString());
                    sumMoreSankeyNodesIterator.remove();
                } else {
                    targetToSankeyNodeMap.put(next.getTarget(), next);
                }
            }
            for (QueryChartResultForSankeyVO.SankeyNode sankeyNode : value) {
                sankeyNode.setSumPercentOfTotal(getPercentOfTotal(total, sum));
                sankeyNode.setSumValidPc(NumberUtil.add(validPcs).toString());
                sankeyNode.setSumMoreSankeyNodes(sumMoreSankeyNodes);
            }
        });

        queryChartResultForSankeyVO.setSankeyNodeList(sankeyNodeList);
    }

    private void put(String key, QueryChartResultForSankeyVO.SankeyNode value, Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> layerFourNameToNodeMap) {
        List<QueryChartResultForSankeyVO.SankeyNode> sankeyNodes = layerFourNameToNodeMap.get(key);
        if (CollectionUtil.isEmpty(sankeyNodes)) {
            sankeyNodes = new ArrayList<>();
            layerFourNameToNodeMap.put(key, sankeyNodes);
        }
        sankeyNodes.add(value);
    }

    private String getPercentOfTotal(String total, String value) {
        if (!NumberUtil.isNumber(total) || "0".equals(total)) {
            return "--";
        }
        return NumberUtil.round(NumberUtil.div(value, total), 4).toString();
    }

    private Boolean isAllRollupName(List<String> list) {
        for (String s : list) {
            if (!StrUtil.equals(QueryChartService.ROLLUP_NAME, s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除 全部 all条件
     * @param queryChartParameterVO
     */
    public void rmConds(QueryChartParameterVO queryChartParameterVO) {
        List<QueryChartParameterVO.ConditionVO> dashboard = queryChartParameterVO.getDashboard();
        dashboard.removeIf(p -> {
            String condName = p.getName();
            String condValue = p.getValue();
            return (StringUtils.equals(condName, "channel_sea") && StringUtils.equals(condValue, "全部"))
                    || (StringUtils.equals(condName, "channel_id") && StringUtils.equals(condValue, "all"));
        });
    }

    /**
     * 追加相对比例
     * @param first
     * @param queryChartResultVO
     */
//    public void appendRelativeRatio(Optional<QueryChartResultVO.MeasureData> first, QueryChartResultVO queryChartResultVO){
//
//        if(first.isPresent()){
//            List<String> data = first.get().getData();
//            if(CollectionUtil.isNotEmpty(data)){
//                String max = data.get(0);
//                int size = data.size();
//                List<String> compData = new ArrayList<>(size);
//                IntStream.range(0, size).forEach(i -> {
//                    compData.add(ratio(data.get(i), max));
//                });
//                QueryChartResultVO.MeasureData compMea = new QueryChartResultVO.MeasureData();
//                compMea.setData(compData);
//                compMea.setDisplayName("相对占比");
//                queryChartResultVO.getDatas().add(compMea);
//
//                List<List<String>> rowFormatDataList = queryChartResultVO.getRowFormatDataList();
//                IntStream.range(0, rowFormatDataList.size()).forEach(i -> {
//                    rowFormatDataList.get(i).add(compData.get(i));
//                });
//            }
//        }
//    }

    public void appendRelativeRatio(Optional<QueryChartResultVO.MeasureData> first, QueryChartResultVO queryChartResultVO){

        if(first.isPresent()){
            List<BigDecimal> collect = first.get().getData().stream().map(item -> {
                try {
                    return new BigDecimal(item);
                } catch (Exception e) {
                    return BigDecimal.ZERO;
                }
            }).collect(Collectors.toList());
            double max = collect.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO).doubleValue();
            double min = collect.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO).doubleValue();
            //
            String maxUp = null;
            String maxDown = null;
            if(max <= 0){
                //全部为负数
                maxUp = maxDown = String.valueOf(Math.abs(min));
            }else if (max > 0 && min < 0){
                //有正有负
                maxUp = String.valueOf(max);
                maxDown = String.valueOf(Math.abs(min));
            }else{
                //全部为正数
                maxUp = maxDown = String.valueOf(max);
            }

            List<String> data = first.get().getData();
            if(CollectionUtil.isNotEmpty(data)){
                int size = data.size();
                List<String> compData = new ArrayList<>(size);
                String finalMaxUp = maxUp;
                String finalMaxDown = maxDown;
                IntStream.range(0, size).forEach(i -> {
                    String src = data.get(i);
                    String maxStr = null;
                    if(NumberUtil.isNumber(src)){
                        maxStr = new BigDecimal(src).doubleValue() >= 0 ? finalMaxUp : finalMaxDown;
                    }
                    compData.add(ratio(src, maxStr));
                });
                QueryChartResultVO.MeasureData compMea = new QueryChartResultVO.MeasureData();
                compMea.setData(compData);
                compMea.setDisplayName("相对占比");
                queryChartResultVO.getDatas().add(compMea);

                List<List<String>> rowFormatDataList = queryChartResultVO.getRowFormatDataList();
                IntStream.range(0, rowFormatDataList.size()).forEach(i -> {
                    rowFormatDataList.get(i).add(compData.get(i));
                });
            }
        }
    }
}
