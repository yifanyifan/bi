package com.stnts.bi.gameop.handlers;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.gameop.common.Constants;
import com.stnts.bi.sql.bo.BuildSqlResultBO;
import com.stnts.bi.sql.bo.QueryChartBO;
import com.stnts.bi.sql.entity.OlapChartDimension;
import com.stnts.bi.sql.entity.OlapChartMeasure;
import com.stnts.bi.sql.service.QueryChartService;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/10/22
 */
public abstract class BaseHandler implements Handler, Constants {


    protected String getViewSql(QueryChartService queryChartService, QueryChartParameterVO queryChartParameter, List<OlapChartDimension> dimensions, List<OlapChartMeasure> measures){

        QueryChartParameterVO newQueryChartParameterVO = new QueryChartParameterVO();
        newQueryChartParameterVO.setLimit((double) Integer.MAX_VALUE);
//        OlapChartDimension chartDimension0 = new OlapChartDimension();
//        chartDimension0.setName("uid");
//        chartDimension0.setAsName("uid");
//        chartDimension0.setOrder(-2);
//        OlapChartMeasure chartMeasure0 = new OlapChartMeasure();
//        chartMeasure0.setName("sum(login_num)");
//        chartMeasure0.setAsName("login_num");
//        chartMeasure0.setOrder(-2);
//        newQueryChartParameterVO.setDimension(CollectionUtil.newArrayList(chartDimension0));
//        newQueryChartParameterVO.setMeasure(CollectionUtil.newArrayList(chartMeasure0));
        newQueryChartParameterVO.setDimension(dimensions);
        newQueryChartParameterVO.setMeasure(measures);
        newQueryChartParameterVO.setDatabaseName(queryChartParameter.getDatabaseName());
        newQueryChartParameterVO.setTableName(queryChartParameter.getTableName());
        newQueryChartParameterVO.setDashboard(queryChartParameter.getDashboard());
        QueryChartBO queryChartBO = queryChartService.initQueryChartBO(newQueryChartParameterVO, new QueryChartResultVO());
        BuildSqlResultBO buildSqlResultBO = queryChartService.buildSql(queryChartBO);
        String sql = StrUtil.replace(buildSqlResultBO.getSql(), "?", "'{}'");
        sql = StrUtil.format(sql, buildSqlResultBO.getBindValues().toArray(new Object[0]));
        sql = StrUtil.format("({})", sql);
        System.out.println("inSql: " + sql);
        return sql;
    }

    public ResultEntity<QueryChartResultVO> getResultAfterViewSql(QueryChartService queryChartService, QueryChartParameterVO queryChartParameter, List<OlapChartDimension> dimensions, List<OlapChartMeasure> measures){

        String viewSql = getViewSql(queryChartService, queryChartParameter, dimensions, measures);
        queryChartParameter.setViewSql(viewSql);
        queryChartParameter.setDashboard(null);
        QueryChartResultVO resultVO = queryChartService.queryChart(queryChartParameter);
        return ResultEntity.success(resultVO);
    }

    public ResultEntity<QueryChartResultVO> getResultAfterViewSql(QueryChartService queryChartService, QueryChartParameterVO queryChartParameter, List<OlapChartMeasure> measures){

        String viewSql = getViewSql(queryChartService, queryChartParameter, queryChartParameter.getDimension(), measures);
        queryChartParameter.setViewSql(viewSql);
        queryChartParameter.setDashboard(null);
        QueryChartResultVO resultVO = queryChartService.queryChart(queryChartParameter);
        return ResultEntity.success(resultVO);
    }

    public OlapChartDimension partitionDimension(){
        OlapChartDimension chartDimension1 = new OlapChartDimension();
        chartDimension1.setName("partition_date");
        chartDimension1.setAsName("partition_date");
        chartDimension1.setAliasName("partition_date");
        chartDimension1.setOrder(-2);
        return chartDimension1;
    }

     public OlapChartDimension getPartitionDateDim(QueryChartParameterVO queryChartParameter){
         OlapChartDimension partitionDateDim = queryChartParameter.getDimension().stream().filter(dim -> StringUtils.equals(dim.getName(), "partition_date")).findFirst().orElseGet(this::partitionDimension);
         partitionDateDim.setAsName("partition_date");
         partitionDateDim.setAliasName("partition_date");
//         OlapChartDimension dim = partitionDimension();
//         dim.setGroup(partitionDateDim.getGroup());
         return partitionDateDim;
     }
}
