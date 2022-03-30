package com.stnts.bi.gameop.handlers;

import cn.hutool.core.collection.CollectionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: liang.zhang
 * @description: 饼图 其它处理
 * @date: 2021/12/16
 */
public class PieTopChartHandler extends BaseHandler {

    public static final String HANDLER_ID = "gameop-206";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {

        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
        List<Object> datas = queryChartResultVO.getDatas();
        //如果查不到数据  就直接回了吧
        if(CollectionUtil.isEmpty(datas)){
            return ResultEntity.success(queryChartResultVO);
        }

        QueryChartResultVO.DimensionData dimData = (QueryChartResultVO.DimensionData) datas.get(0);
        QueryChartResultVO.MeasureData meaData = (QueryChartResultVO.MeasureData) datas.get(1);

        List<String> dimList = dimData.getData().stream().limit(10).collect(Collectors.toList());
        List<String> meaList = meaData.getData().stream().limit(10).collect(Collectors.toList());
        int sum = meaData.getData().stream().skip(10).mapToInt(Integer::parseInt).sum();
        dimList.add("其它");
        meaList.add(String.valueOf(sum));

        dimData.setData(dimList);
        meaData.setData(meaList);
        return ResultEntity.success(queryChartResultVO);
    }

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO, int sankeyNodeNumberPerLayer) {
        return null;
    }
}
