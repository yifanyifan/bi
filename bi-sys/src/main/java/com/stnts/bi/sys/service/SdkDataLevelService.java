package com.stnts.bi.sys.service;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.SdkDataLevelEntity;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/28
 */
public interface SdkDataLevelService {

    /**
     * 数据层级列表
     * @return
     */
    ResultEntity<List<SdkDataLevelEntity>> list(String productId);

    /**
     * 新增节点
     * @param sdkDataLevelEntity
     * @return
     */
    ResultEntity<SdkDataLevelEntity> add(SdkDataLevelEntity sdkDataLevelEntity);

    /**
     * 改名
     * @param sdkDataLevelEntity
     * @return
     */
    ResultEntity<SdkDataLevelEntity> rename(SdkDataLevelEntity sdkDataLevelEntity);

    /**
     * 拖动
     * @param sdkDataLevelEntity
     * @return
     */
    ResultEntity<SdkDataLevelEntity> drag(SdkDataLevelEntity sdkDataLevelEntity);

    /**
     * 删除数据层级
     * @param levelId
     * @return
     */
    ResultEntity<Boolean> del(SdkDataLevelEntity sdkDataLevelEntity);
}
