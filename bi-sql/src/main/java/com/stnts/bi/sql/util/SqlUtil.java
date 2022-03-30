package com.stnts.bi.sql.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author liutianyuan
 */
@Slf4j
public class SqlUtil {
    public final static String QUERY_TYPE_ROW = "ROW";
    public final static String QUERY_TYPE_COLUMN = "COLUMN";

    public static List<List<String>> queryRowList(String sql, List<Object> bindValues, Db db) {
        try {

            List<List<String>> result = db.query(sql, rs -> {
                List<List<String>> list = new ArrayList<>();
                int count = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    List<String> subList = new ArrayList<>();
                    for (int i = 1; i <= count; i++) {
                        String value = getValue(rs, i);
                        subList.add(value);
                    }
                    list.add(subList);
                }
                return list;
            }, toArray(bindValues));
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("queryRowList exception", e);
        }
    }

    private static Object[] toArray(List<Object> bindValues) {
        return Optional.ofNullable(bindValues).filter(CollectionUtil::isNotEmpty).map(temp -> temp.toArray(new Object[temp.size()])).orElse(new Object[]{});
    }

    private static String getValue(ResultSet rs, int i) throws SQLException {
        String value;
        String columnTypeName = rs.getMetaData().getColumnTypeName(i);
        if(StrUtil.equalsIgnoreCase("BOOLEAN", columnTypeName)) {
            value = String.valueOf(rs.getBoolean(i));
        } else if(StrUtil.equalsIgnoreCase(columnTypeName, "YEAR")) {
            value = String.valueOf(rs.getInt(i));
        } else {
            value = rs.getString(i);
        }
        return value;
    }

    public static List<List<String>> queryColumnList(String sql, List<Object> bindValues, Db db) {
        try {
            List<List<String>> result = db.query(sql, rs -> {
                List<List<String>> list = new ArrayList<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int count = metaData.getColumnCount();
                for (int i = 0; i < count; i++) {
                    List<String> subList = new ArrayList<>();
                    list.add(subList);
                }
                while (rs.next()) {
                    for (int i = 1; i <= count; i++) {
                        list.get(i-1).add(getValue(rs, i));
                    }
                }
                return list;
            }, toArray(bindValues));
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("queryColumnList exception", e);
        }
    }

    public static String queryString(String sql, Db db) {
        try {
            return db.queryString(sql);
        } catch (SQLException e) {
            throw new RuntimeException("queryString exception", e);
        }
    }

    public static String queryString(String sql, List<Object> bindValues, Db db) {
        try {
            return db.queryString(sql, toArray(bindValues));
        } catch (SQLException e) {
            throw new RuntimeException("queryString exception", e);
        }
    }

}
