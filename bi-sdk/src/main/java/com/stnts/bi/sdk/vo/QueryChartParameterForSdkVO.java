package com.stnts.bi.sdk.vo;

import com.stnts.bi.sql.vo.QueryChartParameterVO;
import lombok.Data;

/**
 * @Author: 刘天元
 * @Date: 2021/7/6 17:41
 */
@Data
public class QueryChartParameterForSdkVO extends QueryChartParameterVO {
    private Boolean momCompare;
    private Boolean yoyCompare;
    private Integer topN;


    /**
     * 归因口径。1：按注册；2：按行为。默认按注册。
     */
    private Integer attributionCaliber;

    /**
     * 留存图是否显示当日实时数据。0：不显示，1：显示。
     */
    private Boolean showTodayRealTimeData;
}
