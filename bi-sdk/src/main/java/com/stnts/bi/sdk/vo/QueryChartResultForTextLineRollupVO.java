package com.stnts.bi.sdk.vo;

import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.Data;

/**
 * @author liutianyuan
 */
@Data
public class QueryChartResultForTextLineRollupVO extends QueryChartResultVO {

    private TextLineRollupData textLineRollupData;

    @Data
    public static class TextLineRollupData {
        private String textValue;
        private String yoyRateValue;
        private String momRateValue;

    }
}
