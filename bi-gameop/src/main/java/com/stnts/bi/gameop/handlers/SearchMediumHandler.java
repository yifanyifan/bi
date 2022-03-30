package com.stnts.bi.gameop.handlers;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.entity.OlapChartMeasure;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/26
 */
public class SearchMediumHandler extends BaseHandler{

    public static final String HANDLER_ID = "gameop-001";

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {

        QueryChartResultVO queryChartResultVO = queryChartService.queryChart(queryChartParameterVO);
        if(CollectionUtil.isNotEmpty(queryChartResultVO.getDatas())){
            QueryChartResultVO.MeasureData meaData = (QueryChartResultVO.MeasureData)queryChartResultVO.getDatas().get(0);
            List<String> mediumIds = meaData.getData().stream().filter(Objects::nonNull).map(ids -> StringUtils.split(ids, ",")).flatMap(Arrays::stream).distinct().collect(Collectors.toList());
            mediumIds.forEach(System.out::println);

            queryChartParameterVO.setTableName("dm_channel_medium");
            QueryChartParameterVO.ConditionVO mediumCond = new QueryChartParameterVO.ConditionVO();
            mediumCond.setName("name");
            mediumCond.setFunc("in");
            mediumCond.setValue(JSON.toJSONString(mediumIds));
            queryChartParameterVO.getDashboard().add(mediumCond);
            //清除原来的统计指标
            queryChartParameterVO.getMeasure().clear();
            //查询 ID 和 NAME
            OlapChartMeasure id = new OlapChartMeasure();
            id.setName("id");
            id.setAliasName("媒介ID");
            OlapChartMeasure name = new OlapChartMeasure();
            name.setName("name");
            name.setAliasName("媒介名称");
            queryChartParameterVO.getMeasure().add(id);
            queryChartParameterVO.getMeasure().add(name);

            QueryChartResultVO resultVO = queryChartService.queryChart(queryChartParameterVO);
            return ResultEntity.success(resultVO);
        }else{
            return ResultEntity.failure("媒介信息查询异常");
        }
    }

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO, int sankeyNodeNumberPerLayer) {
        return null;
    }
}
