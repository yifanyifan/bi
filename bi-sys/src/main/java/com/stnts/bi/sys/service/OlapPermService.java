package com.stnts.bi.sys.service;

import com.alibaba.fastjson.JSONArray;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.OlapPermEntity;
import com.stnts.bi.sys.vos.olap.OlapPermItemVO;
import com.stnts.bi.sys.vos.olap.OlapPermModVO;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/12
 */
public interface OlapPermService {

    /**
     * 获取模块名称信息
     * @return
     */
    ResultEntity<List<OlapPermEntity>> modules(Integer userId);

    /**
     * 修改菜单用户权限
     * @param olapPermModVO
     * @return
     */
    ResultEntity<String> mod(OlapPermModVO olapPermModVO, String type);

    /**
     * 根据permId查找有权限的用户ID列表
     * @param permId
     * @return
     */
    ResultEntity<List<Integer>> loadUserByPermId(String permId);

    /**
     * 获取用户树
     * @return
     */
    ResultEntity<JSONArray> userTree();

    /**
     * 查出权限列表
     * @param permId
     * @param userId
     * @param master
     * @return
     */
    ResultEntity<List<OlapPermItemVO>> list(String permId, Integer userId, Integer master);

    /**
     * 删除权限  其实是改变状态 为0
     * @param permId
     * @return
     */
    ResultEntity<String> del(String permId);
}
