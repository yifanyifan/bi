package com.stnts.bi.gameop.handlers;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/12/16
 */
public class LineTopChartHandler extends BaseHandler{

    public static final String HANDLER_ID = "gameop-207";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {

        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
        List<Object> datas = queryChartResultVO.getDatas();

        //如果查不到数据  就直接回了吧
        if(CollectionUtil.isEmpty(datas)){
            return ResultEntity.success(queryChartResultVO);
        }

        //如果是特殊的 比如 汇总，这个时候是没有维度信息的
        // 这里要么是 日期+渠道+数据  要么是  渠道+数据
        QueryChartResultVO.DimensionData dimData = datas.size() == 3 ? (QueryChartResultVO.DimensionData) datas.get(1) : (QueryChartResultVO.DimensionData) datas.get(0);
//        QueryChartResultVO.DimensionData dimData = (QueryChartResultVO.DimensionData) datas.get(1);
        List<String> dimList = dimData.getData().stream().limit(10).collect(Collectors.toList());
        String condStr = JSONUtil.toJsonStr(dimList);
        //增加渠道条件限制
        QueryChartParameterVO.ConditionVO cond = new QueryChartParameterVO.ConditionVO();
        cond.setName("channel_name");
        cond.setValue(condStr);
        cond.setLogic("in");

        queryChartParameterVO.getDashboard().add(cond);

        return ResultEntity.success(queryChartService.queryChart(queryChartParameterVO));
    }

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO, int sankeyNodeNumberPerLayer) {
        return null;
    }
}
