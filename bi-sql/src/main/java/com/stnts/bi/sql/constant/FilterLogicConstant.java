package com.stnts.bi.sql.constant;

import cn.hutool.core.map.MapUtil;

import java.util.Map;

/**
 * @author liutianyuan
 * @date 2019-05-05 16:43
 */

public class FilterLogicConstant {
    public final static String IN = "in";
    public final static String NOTIN = "notin";
    public final static String EQ = "eq";
    public final static String NEQ = "neq";
    public final static String LIKE = "like";
    public final static String NOTLIKE = "notlike";
    public final static String STARTSWITH = "startswith";
    public final static String ENDSWITH = "endswith";
    public final static String GT = "gt";
    public final static String GTE = "gte";
    public final static String LT = "lt";
    public final static String LTE = "lte";
    public final static String BETWEEN = "between";
    public final static String ISNULL = "isnull";
    public final static String ISBLANK = "isblank";
    public final static String ISEMPTY = "isempty";
    public final static String ISNOTNULL = "isnotnull";
    public final static String ISNOTBLANK = "isnotblank";
    public final static String ISNOTEMPTY = "isnotempty";

    public final static Map<String, String> NAME_MAP = MapUtil.<String, String>builder()
            .put(IN, "包含")
            .put(NOTIN, "不包含")
            .put(EQ, "等于")
            .put(NEQ, "不等于")
            .put(LIKE, "包含")
            .put(NOTLIKE, "不包含")
            .put(STARTSWITH, "开头包含")
            .put(ENDSWITH, "结尾包含")
            .put(GT, "大于")
            .put(GTE, "大于等于")
            .put(LT, "小于")
            .put(LTE, "小于等于")
            .put(BETWEEN, "区间").build();
}
