package com.stnts.bi.sys.vos;

import com.stnts.bi.entity.sys.SdkDataLevelEntity;
import lombok.Data;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/28
 */
@Data
public class DataLevelVO {

    private SdkDataLevelEntity sdkDataLevelEntity;
    private List<SdkDataLevelEntity> children;
}
