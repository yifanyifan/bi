package com.stnts.bi.gameop.handlers;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;

/**
 * @author: liang.zhang
 * @description:  处理列表需要枚举的情况  先将数据聚合 再枚举
 * @date: 2021/9/26
 */
public class GroupHandler extends BaseHandler{


    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO) {

        /**
         * 如果什么条件都不带
         */
        String tableName = queryChartParameterVO.getTableName();

//        String viewSql = getViewSql();
        return null;
    }

    @Override
    public ResultEntity<QueryChartResultVO> handler(QueryChartService queryChartService, QueryChartParameterVO queryChartParameterVO, int sankeyNodeNumberPerLayer) {
        return null;
    }
}
