package com.stnts.bi.dashboard.vo;

import com.stnts.bi.sql.vo.QueryChartResultVO;
import lombok.Data;

import java.util.List;

@Data
public class QueryChartResultForTreeVO extends QueryChartResultVO {

    private TreeNode treeNode;

    @Data
    public static class TreeNode {
        private String name;
        private String income;
        private String cost;
        private String roi;
        private List<TreeNode> children;
        private Integer childrenNumber;
    }
}
