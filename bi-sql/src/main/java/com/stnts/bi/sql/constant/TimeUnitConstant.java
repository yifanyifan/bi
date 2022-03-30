package com.stnts.bi.sql.constant;

import cn.hutool.core.map.MapUtil;

import java.util.Map;

/**
 * @author liutianyuan
 */
public class TimeUnitConstant {
    public final static String YEAR = "year";

    public final static String QUARTER = "quarter";

    public final static String MONTH = "month";

    public final static String WEEK = "week";

    public final static String DAY = "day";

    public final static String HOUR = "hour";

    public final static String MINUTE = "minute";

    public final static String YEAR_NAME = "年";

    public final static String MONTH_NAME = "月";

    public final static String WEEK_NAME = "周";

    public final static String DAY_NAME = "日";

    public final static String HOUR_NAME = "时";

    public final static String MINUTE_NAME = "分";

    public final static Map<String, String> NAME_MAP = MapUtil.builder(YEAR, YEAR_NAME)
            .put(MONTH, MONTH_NAME)
            .put(WEEK, WEEK_NAME)
            .put(DAY, DAY_NAME)
            .put(HOUR, HOUR_NAME)
            .put(MINUTE, MINUTE_NAME)
            .map();
}
