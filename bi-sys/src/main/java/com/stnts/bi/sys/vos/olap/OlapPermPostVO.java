package com.stnts.bi.sys.vos.olap;

import lombok.Data;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/6
 */
@Data
public class OlapPermPostVO {

    private Integer userId;
    private String rootId;
    private List<OlapPermVO> perms;
}
