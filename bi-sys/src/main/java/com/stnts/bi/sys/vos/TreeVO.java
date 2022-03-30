package com.stnts.bi.sys.vos;

import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/2
 */
@Data
public class TreeVO {

    /**
     * 因为产品线ID为字符串
     */
    private String nodeId;
    private String nodeName;
    private Boolean checked;

    private String parentNodeId;

    private Integer tag;
    private String index;

    private List<TreeVO> children;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeVO treeVO = (TreeVO) o;
        return nodeId.equals(treeVO.nodeId) && parentNodeId.equals(treeVO.parentNodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId.concat(parentNodeId));
    }
}
