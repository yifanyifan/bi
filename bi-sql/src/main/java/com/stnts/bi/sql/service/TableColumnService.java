package com.stnts.bi.sql.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import com.stnts.bi.sql.constant.ColumnTypeConstant;
import com.stnts.bi.sql.constant.DataSourceConstant;
import com.stnts.bi.sql.constant.NameSpaceConstant;
import com.stnts.bi.sql.entity.OlapChartDimension;
import com.stnts.bi.sql.entity.OlapChartMeasure;
import com.stnts.bi.sql.util.DatabaseUtil;
import com.stnts.bi.sql.util.SqlUtil;
import com.stnts.bi.sql.vo.QueryChartParameterVO;
import com.stnts.bi.sql.bo.QueryTableColumnResultBO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liutianyuan
 */
@Service
public class TableColumnService {

    public Map<String, QueryTableColumnResultBO> getTableColumn(QueryChartParameterVO queryChartParameterVO) {
        List<QueryTableColumnResultBO> tableColumnResultVOList = queryColumnList(queryChartParameterVO);
        Map<String, QueryTableColumnResultBO> map = tableColumnResultVOList.stream().collect(Collectors.toMap(QueryTableColumnResultBO::getColumnName, value -> value));
        // 如果前端传递olapType，使用前端的
        Map<String, QueryTableColumnResultBO> tableColumnFromParameter = getTableColumnFromParameter(queryChartParameterVO);
        if(!tableColumnFromParameter.isEmpty()) {
            tableColumnFromParameter.forEach(map::put);
        }
        return map;
    }

    public List<QueryTableColumnResultBO> queryColumnList(QueryChartParameterVO queryChartParameterVO) {
        List<QueryTableColumnResultBO> tableColumnResultVOList = new ArrayList<>();
        String sql = "SELECT name, `type` FROM `system`.columns where database = ? and `table` = ?";
        List<List<String>> sqlResult = SqlUtil.queryRowList(sql, CollectionUtil.newArrayList(queryChartParameterVO.getDatabaseName(), queryChartParameterVO.getTableName()),
                DatabaseUtil.getDB(NameSpaceConstant.CLICKHOUSE, queryChartParameterVO.getDataSource()));
        sqlResult.forEach(list->{
            QueryTableColumnResultBO tableColumnResultVO = new QueryTableColumnResultBO();
            tableColumnResultVO.setColumnName(list.get(0));
            tableColumnResultVO.setColumnType(list.get(1));

            String columnType = tableColumnResultVO.getColumnType();
            String olapType = getOlapType(columnType);
            tableColumnResultVO.setOlapType(olapType);
            tableColumnResultVOList.add(tableColumnResultVO);
        });
        return tableColumnResultVOList;
    }

    private String getOlapType(String columnType) {
        String olapType = ColumnTypeConstant.TEXT;
        if(StrUtil.containsAnyIgnoreCase(columnType, "int", "float")) {
            olapType = ColumnTypeConstant.INT;
        } else if(StrUtil.containsIgnoreCase(columnType, "string")) {
            olapType = ColumnTypeConstant.TEXT;
        } else if(StrUtil.containsIgnoreCase(columnType, "date")) {
            olapType = ColumnTypeConstant.DATE;
        }
        return olapType;
    }

    public Map<String, QueryTableColumnResultBO> getTableColumnFromParameter(QueryChartParameterVO queryChartParameterVO) {
        Map<String, QueryTableColumnResultBO> result = MapUtil.newHashMap();
        for (OlapChartDimension chartDimension : queryChartParameterVO.getDimension()) {
            add(result, chartDimension.getName(), chartDimension.getOlapType());
        }
        for (OlapChartMeasure chartMeasure : queryChartParameterVO.getMeasure()) {
            add(result, chartMeasure.getName(), chartMeasure.getOlapType());
        }
        for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getDashboard()) {
            add(result, conditionVO.getName(), conditionVO.getOlapType());
        }
        for (QueryChartParameterVO.ConditionVO conditionVO : queryChartParameterVO.getScreen()) {
            add(result, conditionVO.getName(), conditionVO.getOlapType());
        }
        return result;
    }

    private void add(Map<String, QueryTableColumnResultBO> result, String name, String olapType) {
        if(StrUtil.isEmpty(olapType)) {
            return;
        }
        QueryTableColumnResultBO queryTableColumnResultBO = new QueryTableColumnResultBO();
        queryTableColumnResultBO.setColumnName(name);
        queryTableColumnResultBO.setOlapType(olapType);
        result.put(name, queryTableColumnResultBO);
    }
}
