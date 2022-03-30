package com.stnts.bi.datamanagement.service;

import com.stnts.bi.datamanagement.vo.ApiSimpleVO;
import com.stnts.bi.datamanagement.vo.ApiVO;

import java.util.Map;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/12
 */
public interface ApiService {

    Map<Object, Object> initPid4Ylw(ApiVO apiVO);

    Map<Object, Object> initPid4YlwSimple(ApiSimpleVO apiSimpleVO);
}
