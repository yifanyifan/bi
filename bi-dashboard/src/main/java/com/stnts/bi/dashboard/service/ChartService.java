package com.stnts.bi.dashboard.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.stnts.bi.dashboard.util.TemplateHelper;
import com.stnts.bi.dashboard.vo.QueryChartResultForSankeyVO;
import com.stnts.bi.dashboard.vo.QueryChartResultForTreeVO;
import com.stnts.bi.sql.constant.FilterLogicConstant;
import com.stnts.bi.sql.constant.TimeUnitConstant;
import com.stnts.bi.sql.entity.OlapChartDimension;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.util.JacksonUtil;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 刘天元
 */
@Service
@Slf4j
public class ChartService {

    private final String separator = "|||";

    private final QueryChartService queryChartService;

    public ChartService(QueryChartService queryChartService) {
        this.queryChartService = queryChartService;
    }

    public QueryChartResultVO getChart(QueryChartParameterVO queryChartParameterVO) {
        return getChart(queryChartParameterVO, null, null);
    }

    public QueryChartResultVO getChart(QueryChartParameterVO queryChartParameterVO, Integer sankeyNodeNumberPerLayer, Integer roiType) {
        queryChartParameterVO.setDatabaseName("bi_dashboard");

        if(CollectionUtil.isEmpty(queryChartParameterVO.getDimension())) {
            queryChartParameterVO.setDimension(Collections.emptyList());
        }
        if(CollectionUtil.isEmpty(queryChartParameterVO.getMeasure())) {
            queryChartParameterVO.setMeasure(Collections.emptyList());
        }
        if(CollectionUtil.isEmpty(queryChartParameterVO.getDashboard())) {
            queryChartParameterVO.setDashboard(Collections.emptyList());
        }

        queryChartParameterVO.getMeasure().forEach(v -> v.setName(StrUtil.replace(v.getName(), "uniqArrayIf", "uniqExactArrayIf")));

        if(StrUtil.startWithAny(queryChartParameterVO.getId(), "user-quality", "channel-analysis")) {
            QueryChartParameterVO.ConditionVO conditionVO = new QueryChartParameterVO.ConditionVO();
            conditionVO.setName("agg_dim");
            conditionVO.setLogic(FilterLogicConstant.EQ);
            conditionVO.setValue("ALL");
            /*if(StrUtil.equalsAny(queryChartParameterVO.getId(), "user-quality-30","user-quality-40", "user-quality-50", "user-quality-60", "user-quality-70")) {
                Map<String, QueryChartParameterVO.ConditionVO> nameToConditionMap = queryChartParameterVO.getDashboard().stream().collect(Collectors.toMap(QueryChartParameterVO.ConditionVO::getName, Function.identity()));
                List<String> tempList = new ArrayList<>();
                if(isNotEmpty(nameToConditionMap, "date_time")) {
                    tempList.add("dt");
                }
                if(isNotEmpty(nameToConditionMap, "agent_id")) {
                    tempList.add("ag");
                }
                if(isNotEmpty(nameToConditionMap, "game_category_name") || StrUtil.equalsAny(queryChartParameterVO.getId(), "user-quality-50", "user-quality-60")) {
                    tempList.add("gc");
                }
                if(isNotEmpty(nameToConditionMap, "game_name") || StrUtil.equals(queryChartParameterVO.getId(), "user-quality-70")) {
                    tempList.add("gn");
                }
                conditionVO.setValue(CollectionUtil.join(tempList, ":"));
            }*/
            queryChartParameterVO.getDashboard().add(conditionVO);
        }

        if(StrUtil.equals(queryChartParameterVO.getId(), "product-analysis-30")) {
            for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
                if(StrUtil.equals(conditionVO.getName(), "game_name")) {
                    String value = conditionVO.getValue();
                    if(StrUtil.isNotEmpty(value)) {
                        String[] split = StrUtil.split(value, "--");
                        String gameName = ArrayUtil.get(split, 0);
                        conditionVO.setValue(gameName);
                        String gameCode = ArrayUtil.get(split, 1);
                        if(StrUtil.isNotEmpty(gameCode)) {
                            QueryChartParameterVO.ConditionVO gameCodeCondition = new QueryChartParameterVO.ConditionVO();
                            gameCodeCondition.setName("game_code");
                            gameCodeCondition.setLogic(FilterLogicConstant.EQ);
                            gameCodeCondition.setValue(gameCode);
                            queryChartParameterVO.getDashboard().add(gameCodeCondition);
                        }
                    }
                    break;
                }
            }
        }

        if(StrUtil.equals(queryChartParameterVO.getId(), "income-analysis-45")) {
            for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
                if(StrUtil.equals(conditionVO.getName(), "date_time")) {
                    String value = conditionVO.getValue();
                    if(StrUtil.isNotEmpty(value)) {
                        if(FilterLogicConstant.BETWEEN.equals(conditionVO.getLogic()) && TimeUnitConstant.DAY.equals(conditionVO.getFunc()) && JSONUtil.isJson(conditionVO.getValue())) {
                            List<String> stringList = JacksonUtil.fromJSONArray(conditionVO.getValue(), String.class);
                            String startDate = stringList.get(0);
                            String endDate = stringList.get(1);
                            startDate = StrUtil.sub(startDate, 0, 7);
                            endDate = StrUtil.sub(endDate, 0, 7);
                            value = new JSONArray(CollectionUtil.newArrayList(startDate, endDate)).toString();
                            conditionVO.setValue(value);
                            conditionVO.setFunc(TimeUnitConstant.MONTH);
                        }
                    }
                    break;
                }
            }
        }

        if(StrUtil.equals(queryChartParameterVO.getId(), "income-analysis-50")) {
            String startDate = "";
            String endDate = "";
            String category1 = "";
            String category2 = "";
            String category3 = "";
            String customer_name = "";
            String agent_id = "";
            String game_category_name = "";
            String game_name = "";
            String game_code = "";
            Iterator<QueryChartParameterVO.ConditionVO> iterator = queryChartParameterVO.getDashboard().iterator();
            while (iterator.hasNext()) {
                QueryChartParameterVO.ConditionVO conditionVO = iterator.next();
                if ("date_time".equals(conditionVO.getName())) {
                    if (FilterLogicConstant.BETWEEN.equals(conditionVO.getLogic()) && TimeUnitConstant.DAY.equals(conditionVO.getFunc()) && JSONUtil.isJson(conditionVO.getValue())) {
                        List<String> stringList = JacksonUtil.fromJSONArray(conditionVO.getValue(), String.class);
                        startDate = stringList.get(0);
                        endDate = stringList.get(1);
                    }
                    iterator.remove();
                    continue;
                }
                if("category1".equals(conditionVO.getName())) {
                    category1 = conditionVO.getValue();
                    iterator.remove();
                    continue;
                }
                if("category2".equals(conditionVO.getName())) {
                    category2 = conditionVO.getValue();
                    iterator.remove();
                    continue;
                }
                if("category3".equals(conditionVO.getName())) {
                    category3 = conditionVO.getValue();
                    iterator.remove();
                    continue;
                }
                if("customer_name".equals(conditionVO.getName())) {
                    customer_name = conditionVO.getValue();
                    iterator.remove();
                    continue;
                }
                if("agent_id".equals(conditionVO.getName())) {
                    agent_id = conditionVO.getValue();
                    iterator.remove();
                    continue;
                }
                if("game_category_name".equals(conditionVO.getName())) {
                    game_category_name = conditionVO.getValue();
                    iterator.remove();
                    continue;
                }
                if("game_name".equals(conditionVO.getName())) {
                    game_name = conditionVO.getValue();
                    iterator.remove();
                    continue;
                }
                if("game_code".equals(conditionVO.getName())) {
                    game_code = conditionVO.getValue();
                    iterator.remove();
                    continue;
                }
            }
            Dict stringMap = Dict.create().set("customer_name", customer_name).set("category1", category1).set("category2", category2).set("category3", category3);
            Dict inMap = Dict.create().set("agent_id", getInStringValue(agent_id)).set("game_category_name", getInStringValue(game_category_name))
                    .set("game_name", getInStringValue(game_name)).set("game_code", getInStringValue(game_code));
            Template template = TemplateHelper.getTemplateEngine().getTemplate("income-analysis-50.sql");
            String view = template.render(Dict.create().set("startDate", startDate).set("endDate", endDate).set("stringMap", stringMap).set("inMap", inMap));
            queryChartParameterVO.setViewSql(view);
            queryChartParameterVO.getMeasure().forEach(v -> v.setName(StrUtil.replace(v.getName(), "any", "sum")));
            queryChartParameterVO.setRollup(true);
        }

        QueryChartResultVO queryChartResultVO;
        if(StrUtil.equals("channel-analysis-50", queryChartParameterVO.getId())) {
            queryChartParameterVO.setLimit(100000.0);
            QueryChartResultForSankeyVO queryChartResultForSankeyVO = new QueryChartResultForSankeyVO();
            queryChartResultVO = queryChartService.queryChart(queryChartParameterVO, queryChartResultForSankeyVO);
            handleChannelAnalysis50(queryChartResultForSankeyVO, Optional.ofNullable(sankeyNodeNumberPerLayer).orElse(5));
        } else if(StrUtil.equals("channel-analysis-v2-20", queryChartParameterVO.getId())) {
            queryChartParameterVO.setLimit(100000.0);
            QueryChartResultForSankeyVO queryChartResultForSankeyVO = new QueryChartResultForSankeyVO();
            queryChartParameterVO.setViewSql(TemplateHelper.getTemplateEngine().getTemplate("channel-analysis-v2-20.sql").render(Dict.create()));
            queryChartResultVO = queryChartService.queryChart(queryChartParameterVO, queryChartResultForSankeyVO);
            handleSankeyChart(queryChartParameterVO, queryChartResultForSankeyVO, Optional.ofNullable(sankeyNodeNumberPerLayer).orElse(5));
        } else if(StrUtil.equals("product-analysis-30", queryChartParameterVO.getId())) {
            queryChartParameterVO.setLimit(100000.0);
            QueryChartResultForTreeVO queryChartResultForTreeVO = new QueryChartResultForTreeVO();
            queryChartResultVO = queryChartService.queryChart(queryChartParameterVO, queryChartResultForTreeVO);
            handleProductAnalysis30(queryChartResultForTreeVO, roiType);
        }  else {
            queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
        }
        if(StrUtil.equals("channel-analysis-40", queryChartParameterVO.getId())) {
            handleChannelAnalysis40(queryChartResultVO);
        }
        if(StrUtil.equals("channel-analysis-v2-0", queryChartParameterVO.getId())) {
            if(CollectionUtil.isNotEmpty(queryChartResultVO.getDatas())) {
                Object temp = queryChartResultVO.getDatas().get(1);
                queryChartResultVO.getDatas().remove(1);
                queryChartResultVO.getDatas().add(temp);
            }
            handleChannelAnalysisV20(queryChartResultVO);
        }
        if(StrUtil.equals("channel-analysis-v2-30", queryChartParameterVO.getId())) {
            queryChartResultVO.getDatas().removeIf(v -> {
                if(v instanceof QueryChartResultVO.DimensionData) {
                    QueryChartResultVO.DimensionData dimensionData = (QueryChartResultVO.DimensionData) v;
                    if(StrUtil.equals(dimensionData.getName(), "source_tag")) {
                        return true;
                    }
                }
                return false;
            });
        }
        return queryChartResultVO;
    }

    private boolean isNotEmpty(Map<String, QueryChartParameterVO.ConditionVO> nameToConditionMap, String name) {
        return StrUtil.isNotEmpty(Optional.ofNullable(nameToConditionMap.get(name)).map(QueryChartParameterVO.ConditionVO::getValue).orElse(""));
    }

    private String getInStringValue(String strArray) {
        if(StrUtil.isEmpty(strArray)) {
            return null;
        }
        JSONArray jsonArray = JSONUtil.parseArray(strArray);
        if(CollectionUtil.isEmpty(jsonArray)) {
            return null;
        }
        String join = jsonArray.stream().map(Object::toString).map(v -> StrUtil.format("'{}'", v)).collect(Collectors.joining(","));
        return StrUtil.format("({})", join);
    }

    private void handleChannelAnalysis40(QueryChartResultVO queryChartResultVO) {
        ListIterator<Object> iterator = queryChartResultVO.getDatas().listIterator();
        while (iterator.hasNext()) {
            Object dataObj = iterator.next();
            if (dataObj instanceof QueryChartResultVO.MeasureData) {
                QueryChartResultVO.MeasureData currentMeasureData = (QueryChartResultVO.MeasureData) dataObj;
                List<String> data = currentMeasureData.getData();
                BigDecimal total = NumberUtil.add(data.stream().filter(NumberUtil::isNumber).toArray(String[]::new));
                QueryChartResultVO.MeasureData measureData = new QueryChartResultVO.MeasureData();
                measureData.setName(currentMeasureData.getName());
                measureData.setCategory(currentMeasureData.getCategory());
                measureData.setDisplayName(currentMeasureData.getDisplayName());
                measureData.setDisplayName(measureData.getDisplayName() + "累计百分比");
                measureData.setDigitDisplay("percent");
                List<String> accumulativePercentOfTotalData = new ArrayList<>(data.size());
                for (int i = 0; i < data.size(); i++) {
                    String temp;
                    if (total.equals(new BigDecimal(0)) || !NumberUtil.isNumber(data.get(i))) {
                        temp = "--";
                    } else {
                        temp = NumberUtil.round(NumberUtil.div(
                                NumberUtil.add(CollectionUtil.sub(data, 0, i+1).stream().filter(NumberUtil::isNumber).toArray(String[]::new)),
                                total
                        ), 4).toString();
                    }
                    accumulativePercentOfTotalData.add(temp);
                }
                measureData.setData(accumulativePercentOfTotalData);
                iterator.add(measureData);
            }
        }
    }

    private void handleChannelAnalysisV20(QueryChartResultVO queryChartResultVO) {
        List<Object> datas = queryChartResultVO.getDatas();
        Map<Integer, Double> averageNap =  new HashMap<>(datas.size());
        Map<Integer, Double> standardDeviationMap = new HashMap<>(datas.size());
        Map<Integer, List<Double>> subScoreMap = new HashMap<>(datas.size());

        // 求标准差
        for (int i =0; i<datas.size(); i++) {
            Object data = datas.get(i);
            if(data instanceof QueryChartResultVO.MeasureData) {
                if(i > 1) {
                    QueryChartResultVO.MeasureData measureData = (QueryChartResultVO.MeasureData) data;
                    if(StrUtil.equalsAny(measureData.getDisplayName(), "收入", "数据来源")) {
                        continue;
                    }
                    Double average = measureData.getData().stream().filter(NumberUtil::isNumber).mapToDouble(Double::parseDouble).average().orElse(0);
                    Double sum = 0.0;
                    for (String datum : measureData.getData()) {
                        if(!NumberUtil.isNumber(datum)) {
                            datum = average.toString();
                        }
                        sum = sum + Math.pow((Double.parseDouble(datum) - average), 2);
                    }
                    Double standardDeviation = Math.pow((sum/measureData.getData().size()), 0.5);
                    standardDeviationMap.put(i, standardDeviation);
                    averageNap.put(i, average);
                }
            }

        }

        for (int i =0; i<datas.size(); i++) {
            Object data = datas.get(i);
            if(data instanceof QueryChartResultVO.MeasureData) {
                if(i > 1) {
                    List<Double> subScoreList = new ArrayList<>();
                    QueryChartResultVO.MeasureData measureData = (QueryChartResultVO.MeasureData) data;
                    if(StrUtil.equalsAny(measureData.getDisplayName(), "收入", "数据来源")) {
                        continue;
                    }
                    for (String datum : measureData.getData()) {
                        if(!NumberUtil.isNumber(datum)) {
                            datum = averageNap.get(i).toString();
                        }
                        Double sub = Double.parseDouble(datum) - averageNap.get(i);
                        Double subScore = sub==0 ? 0 : sub / standardDeviationMap.get(i);
                        subScoreList.add(subScore);
                    }
                    subScoreMap.put(i, subScoreList);
                }
            }
        }

        if(CollectionUtil.isNotEmpty(datas)) {
            QueryChartResultVO.MeasureData scoreMeasureData = (QueryChartResultVO.MeasureData) datas.get(1);
            for (int i = 0; i < scoreMeasureData.getData().size(); i++) {
                Double score = subScoreMap.get(3).get(i)*30 + subScoreMap.get(4).get(i)*-10 + subScoreMap.get(5).get(i)*100 + subScoreMap.get(6).get(i)*2 + subScoreMap.get(7).get(i)*5
                        + subScoreMap.get(8).get(i)*3 + subScoreMap.get(9).get(i)*2;
                scoreMeasureData.getData().set(i, score.toString());
            }
        }
    }

    /**
     * @param queryChartResultForTreeVO
     * @param roiType 1：分成前roi；2：分成前roi；
     */
    private void handleProductAnalysis30(QueryChartResultForTreeVO queryChartResultForTreeVO, Integer roiType) {
        Map<String, String> childToParentMap = MapUtil.newHashMap();
        Map<String, QueryChartResultForTreeVO.TreeNode> nameToNodeMap = MapUtil.newHashMap();

        List<List<String>> rowFormatDataList = Optional.ofNullable(queryChartResultForTreeVO.getRowFormatDataList()).orElse(Collections.emptyList());
        QueryChartResultForTreeVO.TreeNode rootTreeNode = null;
        for (List<String> strings : rowFormatDataList) {
            QueryChartResultForTreeVO.TreeNode treeNode = new QueryChartResultForTreeVO.TreeNode();
            String cpCost = strings.get(3);
            String channelCost = strings.get(4);
            String otherCost = strings.get(5);
            String income = strings.get(6);

            if(isAllRollupName(CollectionUtil.sub(strings,0, 3))) {
                rootTreeNode = treeNode;
                rootTreeNode.setName(QueryChartService.ROLLUP_NAME);
            } else if(isAllRollupName(CollectionUtil.sub(strings,1, 3))) {
                // 合作商
                treeNode.setName(strings.get(0));
                childToParentMap.put(treeNode.getName(), QueryChartService.ROLLUP_NAME);
            } else if(isAllRollupName(CollectionUtil.sub(strings,2, 3))) {
                // 游戏
                treeNode.setName(StrUtil.join(separator,strings.get(0), strings.get(1)));
                String cost = NumberUtil.add(cpCost, channelCost, otherCost).toString();
                treeNode.setCost(cost);
                treeNode.setIncome(income);
                treeNode.setRoi(getPercentOfTotal(treeNode.getCost(), treeNode.getIncome()));
                childToParentMap.put(treeNode.getName(), strings.get(0));
            } else {
                // 渠道
                treeNode.setName(StrUtil.join(separator,strings.get(0), strings.get(1), strings.get(2)));
                String cost = NumberUtil.add(channelCost, otherCost).toString();
                if(roiType == 1) {
                    //log.info("分成前roi");
                } else if(roiType == 2) {
                    //log.info("分成后roi");
                    cost = NumberUtil.add(cpCost, cost).toString();
                }
                treeNode.setCost(cost);
                treeNode.setIncome(income);
                treeNode.setRoi(getPercentOfTotal(treeNode.getCost(), treeNode.getIncome()));
                childToParentMap.put(treeNode.getName(), StrUtil.join(separator,strings.get(0), strings.get(1)));
            }
            nameToNodeMap.put(treeNode.getName(), treeNode);
        }
        if(rootTreeNode == null) {
            return;
        }

        Map<String, List<String>> parentToChildrenMap = MapUtil.newHashMap();
        childToParentMap.forEach((key, value) -> {
            List<String> strings = parentToChildrenMap.get(value);
            if(CollectionUtil.isEmpty(strings)) {
                strings = CollectionUtil.newArrayList();
            }
            strings.add(key);
            parentToChildrenMap.put(value, strings);
        });

        findChildren(rootTreeNode, parentToChildrenMap, nameToNodeMap);
        rename(rootTreeNode);
        sort(rootTreeNode);
        queryChartResultForTreeVO.setTreeNode(rootTreeNode.getChildren().size() == 1? rootTreeNode.getChildren().get(0) : rootTreeNode);
    }

    private void findChildren(QueryChartResultForTreeVO.TreeNode node, Map<String, List<String>> parentToChildrenMap, Map<String, QueryChartResultForTreeVO.TreeNode> nameToNodeMap) {
        if(ObjectUtil.isNull(node)) {
            return;
        }
        List<String> strings = Optional.ofNullable(parentToChildrenMap.get(node.getName())).orElse(Collections.emptyList());
        node.setChildren(new ArrayList<>(strings.size()));
        for (String name : strings) {
            QueryChartResultForTreeVO.TreeNode treeNode = nameToNodeMap.get(name);
            node.getChildren().add(treeNode);
            findChildren(treeNode, parentToChildrenMap, nameToNodeMap);
        }
        node.setChildrenNumber(node.getChildren().size());
    }

    private void rename(QueryChartResultForTreeVO.TreeNode node) {
        if(ObjectUtil.isNull(node)) {
            return;
        }
        final int pos = node.getName().lastIndexOf(separator);
        if(pos > 0) {
            node.setName(node.getName().substring(pos + separator.length()));
        }
        node.getChildren().forEach(this::rename);
    }

    /**
     * 子节点多的放在中间
     * @param node
     */
    private void sort(QueryChartResultForTreeVO.TreeNode node) {
        List<QueryChartResultForTreeVO.TreeNode> children = node.getChildren();
        children.sort(Comparator.comparing(QueryChartResultForTreeVO.TreeNode::getChildrenNumber));
        List<QueryChartResultForTreeVO.TreeNode> l1 = new ArrayList<>(children.size());
        List<QueryChartResultForTreeVO.TreeNode> l2 = new ArrayList<>(children.size());
        for (int i = 0; i < children.size(); i++) {
            if(i%2 == 0) {
                l1.add(children.get(i));
            } else {
                l2.add(children.get(i));
            }
        }
        Collections.reverse(l2);
        l1.addAll(l2);
        node.setChildren(l1);
        children.forEach(this::sort);
    }

    private void handleSankeyChart(QueryChartParameterVO queryChartParameterVO, QueryChartResultForSankeyVO queryChartResultForSankeyVO, int nodeNumberPerLayer) {
        List<OlapChartDimension> dimensionList = queryChartParameterVO.getDimension();
        int dimensionNumber = dimensionList.size();

        List<QueryChartResultForSankeyVO.SankeyNode> sankeyNodeList = new ArrayList<>();
        List<List<String>> rowFormatDataList = Optional.ofNullable(queryChartResultForSankeyVO.getRowFormatDataList()).orElse(Collections.emptyList());
        Iterator<List<String>> iterator = rowFormatDataList.iterator();
        String totalTemp = "";
        while (iterator.hasNext()) {
            List<String> next = iterator.next();
            if(isAllRollupName(CollectionUtil.sub(next,0, dimensionNumber))) {
                totalTemp = next.get(dimensionNumber);
                iterator.remove();
                break;
            }
        }
        final String total = totalTemp;

        String rootPrefix = "root";
        Map<Integer, String> layerPrefixMap = new HashMap<>(dimensionNumber);
        Map<Integer, Set<String>> layerNodeIndex = new HashMap<>(dimensionNumber);
        Map<Integer, Map<String, List<QueryChartResultForSankeyVO.SankeyNode>>> layerNameToNodeListMap = new HashMap<>(dimensionNumber);
        Map<Integer, Map<String, QueryChartResultForSankeyVO.SankeyNode>> layerSourceTargetToNodeMap = new HashMap<>(dimensionNumber);
        for (int i = 1; i <= dimensionNumber; i++) {
            layerPrefixMap.put(i, "layerPrefix"+i);
            layerNodeIndex.put(i, new HashSet<>());
            layerNameToNodeListMap.put(i, new HashMap<>());
            layerSourceTargetToNodeMap.put(i, new HashMap<>());
        }

        String more = "更多";
        nodeNumberPerLayer = nodeNumberPerLayer - 1;

        Iterator<List<String>> iterator1 = rowFormatDataList.iterator();
        while (iterator1.hasNext()) {
            List<String> strings = iterator1.next();
            if(isAllRollupName(CollectionUtil.sub(strings,1, dimensionNumber))) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setPositionNode(false);
                sankeyNode.setSource(StrUtil.concat(false,rootPrefix, separator, QueryChartService.ROLLUP_NAME));
                sankeyNode.setSourceName(QueryChartService.ROLLUP_NAME);
                sankeyNode.setTarget(StrUtil.concat(false, layerPrefixMap.get(1), separator, strings.get(0)));
                sankeyNode.setTargetName(strings.get(0));

                sankeyNode.setValue(strings.get(dimensionNumber));
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                if(layerNodeIndex.get(1).size() >= nodeNumberPerLayer && !layerNodeIndex.get(1).contains(sankeyNode.getTargetName())) {
                    put(sankeyNode.getTargetName(), sankeyNode, layerNameToNodeListMap.get(1));
                } else {
                    sankeyNodeList.add(sankeyNode);
                    layerNodeIndex.get(1).add(sankeyNode.getTargetName());
                }
                iterator1.remove();
            }
        }
        for (int i = 2; i <= dimensionNumber; i++) {
            handleSankeyNode(i, sankeyNodeList, rowFormatDataList, layerPrefixMap, layerNodeIndex, layerNameToNodeListMap, layerSourceTargetToNodeMap, dimensionNumber, more, total, nodeNumberPerLayer);
        }

        // 处理更多
        if(!layerNameToNodeListMap.get(1).isEmpty()) {
            QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
            sankeyNode.setSource(StrUtil.concat(false,rootPrefix, separator, QueryChartService.ROLLUP_NAME));
            sankeyNode.setSourceName(QueryChartService.ROLLUP_NAME);
            sankeyNode.setTarget(StrUtil.concat(false,layerPrefixMap.get(1), separator, more));
            sankeyNode.setTargetName(more);
            String[] values = layerNameToNodeListMap.get(1).values().stream().flatMap(List::stream).map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
            String[] percentOfTotals = layerNameToNodeListMap.get(1).values().stream().flatMap(List::stream).map(QueryChartResultForSankeyVO.SankeyNode::getPercentOfTotal).toArray(String[]::new);
            sankeyNode.setValue(NumberUtil.add(values).toString());
            sankeyNode.setPercentOfTotal(NumberUtil.add(Arrays.stream(percentOfTotals).filter(NumberUtil::isNumber).toArray(String[]::new)).toString());
            sankeyNode.setMore(true);
            sankeyNode.setMoreSankeyNodes(layerNameToNodeListMap.get(1).values().stream().flatMap(List::stream).collect(Collectors.toList()));
            sankeyNodeList.add(sankeyNode);
        }
        for (int i = 2; i <= dimensionNumber; i++) {
            handleMore(i, sankeyNodeList, layerPrefixMap, layerNameToNodeListMap, more, total);
        }

        // 处理同一个target有多个source
        Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> groupByTarget = sankeyNodeList.stream().collect(Collectors.groupingBy(QueryChartResultForSankeyVO.SankeyNode::getTarget));
        groupByTarget.forEach((key, value) -> {
            String[] values = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
            String[] startPcs = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getStartPc).toArray(String[]::new);
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
                if(exist != null) {
                    exist.setValue(NumberUtil.add(exist.getValue(), next.getValue()).toString());
                    exist.setPercentOfTotal(getPercentOfTotal(total, exist.getValue()));
                    exist.setStartPc(NumberUtil.add(exist.getStartPc(), next.getStartPc()).toString());
                    exist.setValidPc(NumberUtil.add(exist.getValidPc(), next.getValidPc()).toString());
                    sumMoreSankeyNodesIterator.remove();
                } else {
                    targetToSankeyNodeMap.put(next.getTarget(), next);
                }
            }
            for (QueryChartResultForSankeyVO.SankeyNode sankeyNode : value) {
                sankeyNode.setSumPercentOfTotal(getPercentOfTotal(total, sum));
                sankeyNode.setSumStartPc(NumberUtil.add(startPcs).toString());
                sankeyNode.setSumValidPc(NumberUtil.add(validPcs).toString());
                sankeyNode.setSumMoreSankeyNodes(sumMoreSankeyNodes);
            }
        });
        queryChartResultForSankeyVO.setSankeyNodeList(sankeyNodeList);
    }

    private void handleSankeyNode(Integer layerIndex, List<QueryChartResultForSankeyVO.SankeyNode> sankeyNodeList, List<List<String>> rowFormatDataList,
                                  Map<Integer, String> layerPrefixMap, Map<Integer, Set<String>> layerNodeIndex,
                                  Map<Integer, Map<String, List<QueryChartResultForSankeyVO.SankeyNode>>> layerNameToNodeListMap,
                                  Map<Integer, Map<String, QueryChartResultForSankeyVO.SankeyNode>> layerSourceTargetToNodeMap,
                                  Integer dimensionNumber, String more, String total, Integer nodeNumberPerLayer) {
        Iterator<List<String>> iterator = rowFormatDataList.iterator();
        while (iterator.hasNext()) {
            List<String> strings = iterator.next();
            if(isAllRollupName(CollectionUtil.sub(strings,layerIndex, dimensionNumber))) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setPositionNode(false);
                if (layerIndex.equals(dimensionNumber)) {
                    sankeyNode.setPositionNode(true);
                }
                if(layerNameToNodeListMap.get(layerIndex-1).containsKey(strings.get(layerIndex-2))) {
                    sankeyNode.setSource(StrUtil.concat(false, layerPrefixMap.get(layerIndex-1), separator, more));
                    sankeyNode.setSourceName(more);
                } else {
                    sankeyNode.setSource(StrUtil.concat(false, layerPrefixMap.get(layerIndex-1), separator, strings.get(layerIndex-2)));
                    sankeyNode.setSourceName(strings.get(layerIndex-2));
                }
                sankeyNode.setTarget(StrUtil.concat(false, layerPrefixMap.get(layerIndex), separator, strings.get(layerIndex-1)));
                sankeyNode.setTargetName(strings.get(layerIndex-1));

                sankeyNode.setValue(strings.get(dimensionNumber));
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                if(layerNodeIndex.get(layerIndex).size() >= nodeNumberPerLayer && !layerNodeIndex.get(layerIndex).contains(sankeyNode.getTargetName())) {
                    put(sankeyNode.getTargetName(), sankeyNode, layerNameToNodeListMap.get(layerIndex));
                } else {
                    QueryChartResultForSankeyVO.SankeyNode exists = layerSourceTargetToNodeMap.get(layerIndex).get(StrUtil.join(separator, sankeyNode.getSource(), sankeyNode.getTarget()));
                    if (!ObjectUtil.isNull(exists)) {
                        exists.setValue(NumberUtil.add(sankeyNode.getValue(), exists.getValue()).toString());
                        exists.setPercentOfTotal(getPercentOfTotal(total, exists.getValue()));
                    } else {
                        sankeyNodeList.add(sankeyNode);
                    }
                    layerNodeIndex.get(layerIndex).add(sankeyNode.getTargetName());
                    layerSourceTargetToNodeMap.get(layerIndex).put(StrUtil.join(separator, sankeyNode.getSource(), sankeyNode.getTarget()), sankeyNode);
                }
                iterator.remove();
            }
        }
    }

    private void handleMore(Integer layerIndex, List<QueryChartResultForSankeyVO.SankeyNode> sankeyNodeList,
                            Map<Integer, String> layerPrefixMap,
                            Map<Integer, Map<String, List<QueryChartResultForSankeyVO.SankeyNode>>> layerNameToNodeListMap,
                            String more, String total) {
        if(!layerNameToNodeListMap.get(layerIndex).isEmpty()) {
            Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> groupByNodeName = layerNameToNodeListMap.get(layerIndex).values().stream().flatMap(List::stream).collect(Collectors.groupingBy(QueryChartResultForSankeyVO.SankeyNode::getSourceName));
            List<QueryChartResultForSankeyVO.SankeyNode> sankeyMoreNodeList = new ArrayList<>();
            groupByNodeName.forEach((key,value) -> {
                if(layerNameToNodeListMap.get(layerIndex-1).containsKey(key)) {
                    sankeyMoreNodeList.addAll(value);
                    return;
                }
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setSource(StrUtil.concat(false,layerPrefixMap.get(layerIndex-1), separator, key));
                sankeyNode.setSourceName(key);
                sankeyNode.setTarget(StrUtil.concat(false,layerPrefixMap.get(layerIndex), separator, more));
                sankeyNode.setTargetName(more);
                String[] values = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
                sankeyNode.setValue(NumberUtil.add(values).toString());
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                sankeyNode.setMore(true);
                sankeyNode.setMoreSankeyNodes(value);
                sankeyNodeList.add(sankeyNode);
            });
            if(CollectionUtil.isNotEmpty(sankeyMoreNodeList)) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setSource(StrUtil.concat(false, layerPrefixMap.get(layerIndex-1), separator, more));
                sankeyNode.setSourceName(more);
                sankeyNode.setTarget(StrUtil.concat(false,layerPrefixMap.get(layerIndex), separator, more));
                sankeyNode.setTargetName(more);
                String[] values = sankeyMoreNodeList.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
                sankeyNode.setValue(NumberUtil.add(values).toString());
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                sankeyNode.setMore(true);
                sankeyNode.setMoreSankeyNodes(sankeyMoreNodeList);
                sankeyNodeList.add(sankeyNode);
            }
        }
    }

    @Deprecated
    private void handleChannelAnalysis50(QueryChartResultForSankeyVO queryChartResultForSankeyVO, int nodeNumberPerLayer) {
        List<QueryChartResultForSankeyVO.SankeyNode> sankeyNodeList = new ArrayList<>();
        List<List<String>> rowFormatDataList = Optional.ofNullable(queryChartResultForSankeyVO.getRowFormatDataList()).orElse(Collections.emptyList());
        Iterator<List<String>> iterator = rowFormatDataList.iterator();
        String totalTemp = "";
        while (iterator.hasNext()) {
            List<String> next = iterator.next();
            if(isAllRollupName(CollectionUtil.sub(next,0, 4))) {
                totalTemp = next.get(4);
                iterator.remove();
                break;
            }
        }
        final String total = totalTemp;
        String rootPrefix = "root";
        String layerOnePrefix = "user_type";
        String layerTwoPrefix = "agent_id";
        String layerThreePrefix = "channel_id";
        String layerFourPrefix = "cid_id";

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
            if(isAllRollupName(CollectionUtil.sub(strings,1, 4))) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setPositionNode(false);
                sankeyNode.setSource(StrUtil.concat(false,rootPrefix, separator, QueryChartService.ROLLUP_NAME));
                sankeyNode.setSourceName(QueryChartService.ROLLUP_NAME);
                sankeyNode.setTarget(StrUtil.concat(false,layerOnePrefix, separator, strings.get(0)));
                sankeyNode.setTargetName(strings.get(0));

                sankeyNode.setValue(strings.get(4));
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                if(layerOneNodeIndex.size() >= nodeNumberPerLayer && !layerOneNodeIndex.contains(sankeyNode.getTargetName())) {
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
            if(isAllRollupName(CollectionUtil.sub(strings,2, 4))) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setPositionNode(false);
                if(layerOneNameToNodeListMap.containsKey(strings.get(0))) {
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
                if(layerTwoNodeIndex.size() >= nodeNumberPerLayer && !layerTwoNodeIndex.contains(sankeyNode.getTargetName())) {
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
            if(isAllRollupName(CollectionUtil.sub(strings,3, 4))) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setPositionNode(false);
                if(layerTwoNameToNodeListMap.containsKey(strings.get(1))) {
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
                if(layerThreeNodeIndex.size() >= nodeNumberPerLayer && !layerThreeNodeIndex.contains(sankeyNode.getTargetName())) {
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
            if(layerThreeNameToNodeListMap.containsKey(strings.get(2))) {
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
            sankeyNode.setStartPc(strings.get(6));
            if(layerFourNodeIndex.size() >= nodeNumberPerLayer && !layerFourNodeIndex.contains(sankeyNode.getTargetName())) {
                put(sankeyNode.getTargetName(), sankeyNode, layerFourNameToNodeListMap);
            } else {
                QueryChartResultForSankeyVO.SankeyNode exists = layerFourSourceTargetToNodeMap.get(StrUtil.join(separator, sankeyNode.getSource(), sankeyNode.getTarget()));
                if (!ObjectUtil.isNull(exists)) {
                    exists.setValue(NumberUtil.add(sankeyNode.getValue(), exists.getValue()).toString());
                    exists.setPercentOfTotal(getPercentOfTotal(total, exists.getValue()));
                    exists.setStartPc(NumberUtil.add(sankeyNode.getStartPc(), exists.getStartPc()).toString());
                    exists.setValidPc(NumberUtil.add(sankeyNode.getValidPc(), exists.getValidPc()).toString());
                } else {
                    sankeyNodeList.add(sankeyNode);
                }
                layerFourNodeIndex.add(sankeyNode.getTargetName());
                layerFourSourceTargetToNodeMap.put(StrUtil.join(separator, sankeyNode.getSource(), sankeyNode.getTarget()), sankeyNode);
            }
        }

        // 处理更多
        if(!layerOneNameToNodeListMap.isEmpty()) {
            QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
            sankeyNode.setSource(StrUtil.concat(false,rootPrefix, separator, QueryChartService.ROLLUP_NAME));
            sankeyNode.setSourceName(QueryChartService.ROLLUP_NAME);
            sankeyNode.setTarget(StrUtil.concat(false,layerOnePrefix, separator, more));
            sankeyNode.setTargetName(more);
            String[] values = layerOneNameToNodeListMap.values().stream().flatMap(List::stream).map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
            String[] percentOfTotals = layerOneNameToNodeListMap.values().stream().flatMap(List::stream).map(QueryChartResultForSankeyVO.SankeyNode::getPercentOfTotal).toArray(String[]::new);
            sankeyNode.setValue(NumberUtil.add(values).toString());
            sankeyNode.setPercentOfTotal(NumberUtil.add(percentOfTotals).toString());
            sankeyNode.setMore(true);
            sankeyNode.setMoreSankeyNodes(layerOneNameToNodeListMap.values().stream().flatMap(List::stream).collect(Collectors.toList()));
            sankeyNodeList.add(sankeyNode);
        }
        if(!layerTwoNameToNodeListMap.isEmpty()) {
            Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> groupByNodeName = layerTwoNameToNodeListMap.values().stream().flatMap(List::stream).collect(Collectors.groupingBy(QueryChartResultForSankeyVO.SankeyNode::getSourceName));
            List<QueryChartResultForSankeyVO.SankeyNode> sankeyMoreNodeList = new ArrayList<>();
            groupByNodeName.forEach((key,value) -> {
                if(layerOneNameToNodeListMap.containsKey(key)) {
                    sankeyMoreNodeList.addAll(value);
                    return;
                }
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setSource(StrUtil.concat(false,layerOnePrefix, separator, key));
                sankeyNode.setSourceName(key);
                sankeyNode.setTarget(StrUtil.concat(false,layerTwoPrefix, separator, more));
                sankeyNode.setTargetName(more);
                String[] values = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
                sankeyNode.setValue(NumberUtil.add(values).toString());
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                sankeyNode.setMore(true);
                sankeyNode.setMoreSankeyNodes(value);
                sankeyNodeList.add(sankeyNode);
            });
            if(CollectionUtil.isNotEmpty(sankeyMoreNodeList)) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setSource(StrUtil.concat(false, layerOnePrefix, separator, more));
                sankeyNode.setSourceName(more);
                sankeyNode.setTarget(StrUtil.concat(false,layerTwoPrefix, separator, more));
                sankeyNode.setTargetName(more);
                String[] values = sankeyMoreNodeList.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
                sankeyNode.setValue(NumberUtil.add(values).toString());
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                sankeyNode.setMore(true);
                sankeyNode.setMoreSankeyNodes(sankeyMoreNodeList);
                sankeyNodeList.add(sankeyNode);
            }
        }
        if(!layerThreeNameToNodeListMap.isEmpty()) {
            Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> groupByNodeName = layerThreeNameToNodeListMap.values().stream().flatMap(List::stream).collect(Collectors.groupingBy(QueryChartResultForSankeyVO.SankeyNode::getSourceName));
            List<QueryChartResultForSankeyVO.SankeyNode> sankeyMoreNodeList = new ArrayList<>();
            groupByNodeName.forEach((key,value) -> {
                if(layerTwoNameToNodeListMap.containsKey(key)) {
                    sankeyMoreNodeList.addAll(value);
                    return;
                }
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setSource(StrUtil.concat(false,layerTwoPrefix, separator, key));
                sankeyNode.setSourceName(key);
                sankeyNode.setTarget(StrUtil.concat(false,layerThreePrefix, separator, more));
                sankeyNode.setTargetName(more);
                String[] values = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
                sankeyNode.setValue(NumberUtil.add(values).toString());
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                sankeyNode.setMore(true);
                sankeyNode.setMoreSankeyNodes(value);
                sankeyNodeList.add(sankeyNode);
            });
            if(CollectionUtil.isNotEmpty(sankeyMoreNodeList)) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setSource(StrUtil.concat(false, layerTwoPrefix, separator, more));
                sankeyNode.setSourceName(more);
                sankeyNode.setTarget(StrUtil.concat(false,layerThreePrefix, separator, more));
                sankeyNode.setTargetName(more);
                String[] values = sankeyMoreNodeList.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
                sankeyNode.setValue(NumberUtil.add(values).toString());
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                sankeyNode.setMore(true);
                sankeyNode.setMoreSankeyNodes(sankeyMoreNodeList);
                sankeyNodeList.add(sankeyNode);
            }
        }
        if(!layerFourNameToNodeListMap.isEmpty()) {
            Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> groupByNodeName = layerFourNameToNodeListMap.values().stream().flatMap(List::stream).collect(Collectors.groupingBy(QueryChartResultForSankeyVO.SankeyNode::getSourceName));
            List<QueryChartResultForSankeyVO.SankeyNode> sankeyMoreNodeList = new ArrayList<>();
            groupByNodeName.forEach((key,value) -> {
                if(layerThreeNameToNodeListMap.containsKey(key)) {
                    sankeyMoreNodeList.addAll(value);
                    return;
                }
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setPositionNode(true);
                sankeyNode.setSource(StrUtil.concat(false,layerThreePrefix, separator, key));
                sankeyNode.setSourceName(key);
                sankeyNode.setTarget(StrUtil.concat(false,layerFourPrefix, separator, more));
                sankeyNode.setTargetName(more);
                String[] values = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
                String[] startPcs = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getStartPc).toArray(String[]::new);
                String[] validPcs = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValidPc).toArray(String[]::new);
                sankeyNode.setValue(NumberUtil.add(values).toString());
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                sankeyNode.setStartPc(NumberUtil.add(startPcs).toString());
                sankeyNode.setValidPc(NumberUtil.add(validPcs).toString());
                sankeyNode.setMore(true);
                sankeyNode.setMoreSankeyNodes(value);
                sankeyNodeList.add(sankeyNode);
            });
            if(CollectionUtil.isNotEmpty(sankeyMoreNodeList)) {
                QueryChartResultForSankeyVO.SankeyNode sankeyNode = new QueryChartResultForSankeyVO.SankeyNode();
                sankeyNode.setPositionNode(true);
                sankeyNode.setSource(StrUtil.concat(false, layerThreePrefix, separator, more));
                sankeyNode.setSourceName(more);
                sankeyNode.setTarget(StrUtil.concat(false,layerFourPrefix, separator, more));
                sankeyNode.setTargetName(more);
                String[] values = sankeyMoreNodeList.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValue).toArray(String[]::new);
                String[] startPcs = sankeyMoreNodeList.stream().map(QueryChartResultForSankeyVO.SankeyNode::getStartPc).toArray(String[]::new);
                String[] validPcs = sankeyMoreNodeList.stream().map(QueryChartResultForSankeyVO.SankeyNode::getValidPc).toArray(String[]::new);
                sankeyNode.setValue(NumberUtil.add(values).toString());
                sankeyNode.setPercentOfTotal(getPercentOfTotal(total, sankeyNode.getValue()));
                sankeyNode.setStartPc(NumberUtil.add(startPcs).toString());
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
            String[] startPcs = value.stream().map(QueryChartResultForSankeyVO.SankeyNode::getStartPc).toArray(String[]::new);
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
                if(exist != null) {
                    exist.setValue(NumberUtil.add(exist.getValue(), next.getValue()).toString());
                    exist.setPercentOfTotal(getPercentOfTotal(total, exist.getValue()));
                    exist.setStartPc(NumberUtil.add(exist.getStartPc(), next.getStartPc()).toString());
                    exist.setValidPc(NumberUtil.add(exist.getValidPc(), next.getValidPc()).toString());
                    sumMoreSankeyNodesIterator.remove();
                } else {
                    targetToSankeyNodeMap.put(next.getTarget(), next);
                }
            }
            for (QueryChartResultForSankeyVO.SankeyNode sankeyNode : value) {
                sankeyNode.setSumPercentOfTotal(getPercentOfTotal(total, sum));
                sankeyNode.setSumStartPc(NumberUtil.add(startPcs).toString());
                sankeyNode.setSumValidPc(NumberUtil.add(validPcs).toString());
                sankeyNode.setSumMoreSankeyNodes(sumMoreSankeyNodes);
            }
        });

        queryChartResultForSankeyVO.setSankeyNodeList(sankeyNodeList);
    }

    private void put(String key, QueryChartResultForSankeyVO.SankeyNode value, Map<String, List<QueryChartResultForSankeyVO.SankeyNode>> layerFourNameToNodeMap) {
        List<QueryChartResultForSankeyVO.SankeyNode> sankeyNodes = layerFourNameToNodeMap.get(key);
        if(CollectionUtil.isEmpty(sankeyNodes)) {
            sankeyNodes = new ArrayList<>();
            layerFourNameToNodeMap.put(key, sankeyNodes);
        }
        sankeyNodes.add(value);
    }

    private String getPercentOfTotal(String total, String value) {
        if(!NumberUtil.isNumber(total) || "0".equals(total)) {
            return "--";
        }
        return NumberUtil.round(NumberUtil.div(value, total), 4).toString();
    }

    private Boolean isAllRollupName(List<String> list) {
        for (String s : list) {
            if(!StrUtil.equals(QueryChartService.ROLLUP_NAME, s)) {
                return false;
            }
        }
        return true;
    }

}
