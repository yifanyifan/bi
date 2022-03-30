package com.stnts.bi.sdk.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.stnts.bi.sdk.constant.MeasureNameConstant;
import com.stnts.bi.sql.entity.OlapChartMeasure;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author 刘天元
 */
@Service
public class SdkAfterQueryChartService {

    public void afterQuery(QueryChartParameterVO queryChartParameter, QueryChartResultVO queryChartResultVO, Integer topN) {
        String id = queryChartParameter.getId();
        if(topN != null && CollectionUtil.isNotEmpty(queryChartResultVO.getDatas())
                && StrUtil.equalsAny(id, "data-overview-operation-chart-40", "data-overview-payment-chart-40", "channel-analyse-distribution-10")) {
            QueryChartResultVO.DimensionData dimensionData = (QueryChartResultVO.DimensionData) queryChartResultVO.getDatas().get(0);

            if(dimensionData.getData().size() > topN) {
                dimensionData.setData(dimensionData.getData().subList(0, topN));
                boolean showOtherData = !StrUtil.equalsAny(
                        Optional.ofNullable(CollectionUtil.get(queryChartParameter.getMeasure(), queryChartParameter.getMeasure().size()-1))
                                .map(OlapChartMeasure::getAliasName)
                                .orElse(""),
                        MeasureNameConstant.percentMeasure);
                if(showOtherData) {
                    dimensionData.getData().add(StrUtil.format("Top {}以外", topN));
                }

                Consumer<QueryChartResultVO.MeasureData> consumer = measureData -> {
                    BigDecimal sum = NumberUtil.add(CollectionUtil.removeAny(measureData.getData().subList(topN, measureData.getData().size()), "nan", "inf").toArray(new String[0]));
                    measureData.setData(measureData.getData().subList(0, topN));
                    if(showOtherData) {
                        measureData.getData().add(sum.toString());
                    }
                };

                queryChartResultVO.getDatas().stream().filter(x -> x instanceof QueryChartResultVO.MeasureData).map(y -> (QueryChartResultVO.MeasureData) y).forEach(consumer);
            }
        }

        if(StrUtil.equalsAny(id, "channel-analyse-distribution-30", "channel-analyse-distribution-40")) {
            if(CollectionUtil.isNotEmpty(queryChartResultVO.getDatas())) {
                QueryChartResultVO.DimensionData dimensionData = (QueryChartResultVO.DimensionData) queryChartResultVO.getDatas().get(0);
                if(StrUtil.equals(dimensionData.getName(), "date")) {
                    queryChartResultVO.getDatas().remove(0);
                }
            }
        }

        Map<Integer, String> dimensionIdToNameMap = SdkQueryChartService.threadLocalDimensionIdToNameMap.get();
        Map<Integer, String> measureIdToNameMap = SdkQueryChartService.threadLocalMeasureIdToNameMap.get();
        if(CollectionUtil.isNotEmpty(queryChartResultVO.getDatas())) {
            for (Object data : queryChartResultVO.getDatas()) {
                if(data instanceof QueryChartResultVO.DimensionData) {
                    QueryChartResultVO.DimensionData dimensionData = (QueryChartResultVO.DimensionData) data;
                    dimensionData.setName(dimensionIdToNameMap.get(dimensionData.getId()));
                }
                if(data instanceof QueryChartResultVO.MeasureData) {
                    QueryChartResultVO.MeasureData measureData = (QueryChartResultVO.MeasureData) data;
                    measureData.setName(measureIdToNameMap.get(measureData.getId()));
                }
            }
        }
        SdkQueryChartService.threadLocalDimensionIdToNameMap.remove();
        SdkQueryChartService.threadLocalMeasureIdToNameMap.remove();
    }

}
