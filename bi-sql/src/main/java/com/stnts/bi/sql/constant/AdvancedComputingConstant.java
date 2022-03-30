package com.stnts.bi.sql.constant;

import cn.hutool.core.map.MapUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author liutianyuan
 */
public class AdvancedComputingConstant {
    public final static String YOY = "yoy";
    public final static String YOY_RATE = "yoy_rate";
    public final static String D_YOY = "d_yoy";
    public final static String W_YOY = "w_yoy";
    public final static String M_YOY = "m_yoy";
    public final static String Y_YOY = "y_yoy";
    public final static String D_YOY_RATE = "d_yoy_rate";
    public final static String W_YOY_RATE = "w_yoy_rate";
    public final static String M_YOY_RATE = "m_yoy_rate";
    public final static String Y_YOY_RATE = "y_yoy_rate";
    public final static String D_YOY_VALUE = "d_yoy_value";
    public final static String W_YOY_VALUE = "w_yoy_value";
    public final static String M_YOY_VALUE = "m_yoy_value";
    public final static String Y_YOY_VALUE = "y_yoy_value";

    public final static String YOY_NAME = "同比";
    public final static String YOY_RATE_NAME = "同比增长率";
    public final static String D_YOY_NAME = "昨日同比";
    public final static String W_YOY_NAME = "上周同比";
    public final static String M_YOY_NAME = "上月同比";
    public final static String Y_YOY_NAME = "去年同比";
    public final static String D_YOY_RATE_NAME = "日同比增长率";
    public final static String W_YOY_RATE_NAME = "周同比增长率";
    public final static String M_YOY_RATE_NAME = "月同比增长率";
    public final static String Y_YOY_RATE_NAME = "年同比增长率";
    public final static String D_YOY_VALUE_NAME = "昨日同期";
    public final static String W_YOY_VALUE_NAME = "上周同期";
    public final static String M_YOY_VALUE_NAME = "上月同期";
    public final static String Y_YOY_VALUE_NAME = "去年同期";

    public final static String MOM = "mom";
    public final static String MOM_RATE = "mom_rate";

    public final static String MOM_NAME = "环比";
    public final static String MOM_RATE_NAME = "环比增长率";

    public final static Map<String, String> NAME_MAP = MapUtil.builder(D_YOY, D_YOY_NAME)
            .put(D_YOY_RATE, D_YOY_RATE_NAME)
            .put(W_YOY, W_YOY_NAME)
            .put(W_YOY_RATE, W_YOY_RATE_NAME)
            .put(M_YOY, M_YOY_NAME)
            .put(M_YOY_RATE, M_YOY_RATE_NAME)
            .put(Y_YOY, Y_YOY_NAME)
            .put(Y_YOY_RATE, Y_YOY_RATE_NAME)
            .put(MOM, MOM_NAME)
            .put(MOM_RATE, MOM_RATE_NAME)
            .put(D_YOY_VALUE, D_YOY_VALUE_NAME)
            .put(W_YOY_VALUE, W_YOY_VALUE_NAME)
            .put(M_YOY_VALUE, M_YOY_VALUE_NAME)
            .put(Y_YOY_VALUE, Y_YOY_VALUE_NAME)
            .put(YOY, YOY_NAME)
            .put(YOY_RATE, YOY_RATE_NAME)
            .map();

    public final static Set<String> FORMAT_PERCENT_SET = new HashSet<>(Arrays.asList(MOM,MOM_RATE,D_YOY,W_YOY,M_YOY,Y_YOY,D_YOY_RATE,W_YOY_RATE,M_YOY_RATE,Y_YOY_RATE));
}
