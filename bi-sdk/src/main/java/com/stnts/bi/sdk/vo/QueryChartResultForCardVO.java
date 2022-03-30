package com.stnts.bi.sdk.vo;

import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.Data;

import java.util.List;

/**
 * @author liutianyuan
 */
@Data
public class QueryChartResultForCardVO extends QueryChartResultVO {

    private List<CardData> cardDataList;

    @Data
    public static class CardData {
        private String displayName;
        private String textValue;
        private String yoyDate;
        private String momDate;
        private String yoyRateValue;
        private String momRateValue;
        private Boolean showContrast;
    }
}
