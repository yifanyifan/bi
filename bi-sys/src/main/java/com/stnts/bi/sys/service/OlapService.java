package com.stnts.bi.sys.service;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.OlapPermEntity;
import com.stnts.bi.sys.vos.olap.OlapPermPostVO;
import com.stnts.bi.sys.vos.olap.OlapPermVO;

import java.util.List;
import java.util.Map;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/7
 */
public interface OlapService {

    /**
     * 给OLAP提供的接口
     * 获取用户有权限的菜单树
     * @return
     */
    ResultEntity<List<OlapPermVO>> getPermList(Integer userId, String sign);

    /**
     * 提供给OLAP的接口
     * 发布用
     * 将OLAP的目录和仪表盘发布为BI的菜单和页面
     * @param olapPermPostVO
     * @return
     */
    ResultEntity<String> publish(OlapPermPostVO olapPermPostVO);
}
