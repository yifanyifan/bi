package com.stnts.bi.dashboard.handlers;

import cn.hutool.core.collection.CollectionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/10/22
 *
 * 带带业务大盘-用户分析-充值排名
 */
public class Handler107 extends BaseHandler {

    public static final String HANDLER_ID = "dashboard-dd-107";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO){

//        setAggDimCond(queryChartParameterVO, HANDLER_ID);
        //要查一个总充值   每个用户充值除以总充值得到占比
        rmConds(queryChartParameterVO);
        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);

        if(CollectionUtil.isNotEmpty(queryChartResultVO.getDatas())){

            try {

                queryChartParameterVO.setLimit(null);
                queryChartParameterVO.setDimension(Collections.emptyList());
                QueryChartResultVO totalQueryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
                QueryChartResultVO.MeasureData totalMea = (QueryChartResultVO.MeasureData) totalQueryChartResultVO.getDatas().get(0);
                String total = totalMea.getData().get(0);
                QueryChartResultVO.MeasureData itemMea = (QueryChartResultVO.MeasureData) queryChartResultVO.getDatas().get(1);
                List<String> items = itemMea.getData();
                String max = items.get(0);

                List<String> relativeRatioList = new ArrayList<>();
                List<String> ratioList = new ArrayList<>(items.size());
                IntStream.range(0, items.size()).forEach(i -> {
                    relativeRatioList.add(ratio(items.get(i), max));
                    ratioList.add(ratio(items.get(i), total));
                });
                // 占比
                QueryChartResultVO.MeasureData relativeRatioMea = new QueryChartResultVO.MeasureData();
                relativeRatioMea.setData(relativeRatioList);
                relativeRatioMea.setDisplayName("占比");
                relativeRatioMea.setDigitDisplay("percent");
                queryChartResultVO.getDatas().add(relativeRatioMea);
                // 全平台占比
                QueryChartResultVO.MeasureData ratioMea = new QueryChartResultVO.MeasureData();
                ratioMea.setData(ratioList);
                ratioMea.setDisplayName("全平台占比");
                ratioMea.setDigitDisplay("percent");
                queryChartResultVO.getDatas().add(ratioMea);
                // 行式数据
                List<List<String>> rowFormatDataList = queryChartResultVO.getRowFormatDataList();
                if(CollectionUtil.isNotEmpty(rowFormatDataList)){
                    IntStream.range(0, rowFormatDataList.size()).forEach(i -> {
                        rowFormatDataList.get(i).add(relativeRatioList.get(i));
                        rowFormatDataList.get(i).add(ratioList.get(i));
                    });
                }
            } catch (Exception e) {
                //基本是没数据，不管   正常输出
            }
        }
        return ResultEntity.success(queryChartResultVO);
    }
}
