package com.stnts.bi.sys.service;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.OlapPermEntity;
import com.stnts.bi.vo.OlapPermSubVO;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/13
 */
public interface OlapMenuService {

    /**
     * 获取某菜单下某个用户对应的权限树
     * @param permId
     * @param userId
     * @return
     */
    ResultEntity<List<OlapPermEntity>> self(String permId, Integer userId);

    /**
     * 查询菜单下所有的权限树
     * @param permId
     * @return
     */
    ResultEntity<List<OlapPermEntity>> all(String permId);

    /**
     * 调整顺序  改名  和 改变状态
     * @param perms
     * @return
     */
    ResultEntity<String> mod(List<OlapPermSubVO> perms);

    /**
     * 初始化菜单
     * @return
     */
    boolean initMenu();
}
