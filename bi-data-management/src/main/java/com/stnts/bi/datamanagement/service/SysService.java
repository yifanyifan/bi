package com.stnts.bi.datamanagement.service;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.vo.DmVO;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/2
 */
public interface SysService {

    ResultEntity<List<DmVO>> listDmVOList(String keyword, List<String> departmentCodes);
}
