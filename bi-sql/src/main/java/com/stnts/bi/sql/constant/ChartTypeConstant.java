package com.stnts.bi.sql.constant;

import lombok.Data;

/**
 * @author liutianyuan
 * @date 2019-07-12 10:27
 */

@Data
public class ChartTypeConstant {
    public static final String SIMPLE_TABLE = "simple-table";
    public static final String TABLE = "table";
    public static final String RETAIN = "retain";
    public static final String TEXT = "text";
    public static final String LINE = "line";
    /**
     * 柱形图
     */
    public static final String HISTOGRAM = "histogram";
    /**
     * 双轴图
     */
    public static final String TWO_AXIS = "two-axis";

    /**
     * 面积图
     */
    public static final String LINE_AREA = "line-area";

    /**
     * 总体留存率
     */
    public static final String RETAIN_LINE_TOTAL = "retain-line-total";

    /**
     * 留存率变化
     */
    public static final String RETAIN_LINE_CHANGE = "retain-line-change";

    /**
     * 留存表格
     */
    public static final String RETAIN_TABLE = "retain-table";

    /**
     * 留存表格,不带 总计
     */
    public static final String RETAIN_TABLE_WITH_OUT_TOTAL = "retain-table-with-out-total";

    /**
     * 总体ltv
     */
    public static final String RETAIN_LINE_TOTAL_LTV = "retain-line-total-ltv";

    /**
     * ltv变化
     */
    public static final String RETAIN_LINE_CHANGE_LTV = "retain-line-change-ltv";

    /**
     * ltv表格。下载时和RETAIN_TABLE处理不同。其它都一样。
     */
    public static final String RETAIN_TABLE_LTV = "retain-table-ltv";

    /**
     * ltv表格,不带 总计
     */
    public static final String RETAIN_TABLE_LTV_WITH_OUT_TOTAL = "retain-table-ltv-with-out-total";

    /**
     * ltv表格。下载时和RETAIN_TABLE处理不同。其它都一样。
     */
    public static final String RETAIN_TABLE_LTV_ACCUMULATE = "retain-table-ltv-accumulate";

    /**
     * ltv表格,不带 总计
     */
    public static final String RETAIN_TABLE_LTV_ACCUMULATE_WITH_OUT_TOTAL = "retain-table-ltv-accumulate-with-out-total";
}
