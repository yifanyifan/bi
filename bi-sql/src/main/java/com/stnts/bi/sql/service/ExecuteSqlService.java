package com.stnts.bi.sql.service;

import com.stnts.bi.sql.bo.ExecuteSqlBO;
import com.stnts.bi.sql.bo.ExecuteSqlResultBO;
import com.stnts.bi.sql.entity.OlapDsDatabase;
import com.stnts.bi.sql.entity.OlapDsTable;
import com.stnts.bi.sql.util.DatabaseUtil;
import com.stnts.bi.sql.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ExecuteSqlService {


    public ExecuteSqlBO executeSql(OlapDsDatabase dsDatabase, OlapDsTable table, String sql, List<Object> bindValues, Boolean cache, String queryType) {
        long start = System.currentTimeMillis();
        ExecuteSqlBO executeSqlBO = new ExecuteSqlBO();
        executeSqlBO.setSql(sql);
        executeSqlBO.setBindValues(bindValues);
        executeSqlBO.setCache(cache);
        executeSqlBO.setCacheHit(true);
        executeSqlBO.setQueryType(queryType);
        ExecuteSqlResultBO executeSqlResultBO = executeSqlForClickhouse(dsDatabase, sql, bindValues, executeSqlBO);
        executeSqlBO.setResult(executeSqlResultBO.getResult());
        executeSqlBO.setExecuteTime(executeSqlResultBO.getExecuteTime());
        long end = System.currentTimeMillis();
        log.info("{} executeSql耗时{},命中缓存:{}", dsDatabase.getNamespace(), end - start, executeSqlBO.getCacheHit());
        return executeSqlBO;
    }

    public ExecuteSqlResultBO executeSqlForClickhouse(OlapDsDatabase dsDatabase, String sql, List<Object> bindValues, ExecuteSqlBO executeSqlBO) {
        return executeSql(dsDatabase, sql, bindValues, executeSqlBO);
    }

    private ExecuteSqlResultBO executeSql(OlapDsDatabase dsDatabase, String sql, List<Object> bindValues, ExecuteSqlBO executeSqlBO) {
        executeSqlBO.setCacheHit(false);
        List<List<String>> result = null;

        if(SqlUtil.QUERY_TYPE_COLUMN.equals(executeSqlBO.getQueryType())) {
            result = SqlUtil.queryColumnList(sql, bindValues, DatabaseUtil.getDB(dsDatabase.getNamespace(), dsDatabase.getDataSource()));
        } else if(SqlUtil.QUERY_TYPE_ROW.equals(executeSqlBO.getQueryType())) {
            result = SqlUtil.queryRowList(sql, bindValues, DatabaseUtil.getDB(dsDatabase.getNamespace(), dsDatabase.getDataSource()));
        }
        ExecuteSqlResultBO executeSqlResultBO = new ExecuteSqlResultBO();
        executeSqlResultBO.setResult(result);
        executeSqlResultBO.setExecuteTime(LocalDateTime.now());
        return executeSqlResultBO;
    }
}
