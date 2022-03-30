package com.stnts.bi.dashboard.vo;

import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class QueryChartResultForSankeyVO extends QueryChartResultVO {

    private List<SankeyNode> sankeyNodeList;

    @Data
    public static class SankeyNode {
        private String source;
        private String sourceName;
        private String target;
        private String targetName;
        private String value;
        private String percentOfTotal;
        private String sumPercentOfTotal;
        private String startPc;
        private String sumStartPc;
        private String validPc;
        private String sumValidPc;
        private Boolean positionNode;
        private Boolean more;
        private Collection<SankeyNode> moreSankeyNodes;
        private Collection<SankeyNode> sumMoreSankeyNodes;
    }
}
