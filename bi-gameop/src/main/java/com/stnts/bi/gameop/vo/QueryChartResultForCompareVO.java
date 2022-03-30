package com.stnts.bi.gameop.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 刘天元
 * @Date: 2021/5/25 16:23
 */
@Data
public class QueryChartResultForCompareVO extends QueryChartResultVO {

    private BigDecimal measureMax;
    private BigDecimal measureMin;
    private BigDecimal measureAvg;

    private String momCompareStartDateStr;
    private String momCompareEndDateStr;
    private String yoyCompareStartDateStr;
    private String yoyCompareEndDateStr;

    @Data
    public static class DimensionData extends QueryChartResultVO.DimensionData {
        @JsonSerialize(using = CustomSerializeForList.class)
        private List<String> momCompareData;

        @JsonSerialize(using = CustomSerializeForList.class)
        private List<String> yoyCompareData;

        private List<String> momCompareDistinctData;

        private List<String> yoyCompareDistinctData;
    }

    @Data
    public static class MeasureData extends QueryChartResultVO.MeasureData {
        @JsonSerialize(using = CustomSerializeForList.class)
        private List<String> momCompareData;

        private List<Object> momCompareGroupData;

        @JsonSerialize(using = CustomSerializeForList.class)
        private List<String> yoyCompareData;

        private List<Object> yoyCompareGroupData;

    }

}
