package com.stnts.bi.sql.constant;

import cn.hutool.core.map.MapUtil;

import java.util.Map;

/**
 * @author liutianyuan
 */
public class FunctionConstant {
    public final static String COUNT = "count";
    public final static String COUNT_NAME = "计数";

    public final static String COUNT_DISTINCT = "count_distinct";
    public final static String COUNT_DISTINCT_NAME = "去重计数";

    public final static String SUM = "sum";
    public final static String SUM_NAME = "求和";

    public final static String AVG = "avg";
    public final static String AVG_NAME = "平均值";

    public final static String MAX = "max";
    public final static String MAX_NAME = "最大值";

    public final static String MIN = "min";
    public final static String MIN_NAME = "最小值";

    public final static String UNIQ_ARRAY = "uniqArray";
    public final static String UNIQ_ARRAY_NAME = "去重计数";


    public final static Map<String, String> NAME_MAP = MapUtil.builder(COUNT, COUNT_NAME)
            .put(COUNT_DISTINCT, COUNT_DISTINCT_NAME)
            .put(SUM, SUM_NAME)
            .put(AVG, AVG_NAME)
            .put(MAX, MAX_NAME)
            .put(MIN, MIN_NAME)
            .put(UNIQ_ARRAY, UNIQ_ARRAY_NAME)
            .map();
}
