package com.stnts.bi.schedule.service;

import com.stnts.bi.schedule.deduct.vo.OrderDeductVO;
import com.stnts.bi.schedule.deduct.vo.PidDeductVO;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/2
 */
public interface CjtDataService {

    /**
     * 产品扣量列表
     *
     * @param orderDeductVO
     * @return
     */
    List<OrderDeductVO> orderDeductList(OrderDeductVO orderDeductVO);

    /**
     * PID扣量列表
     *
     * @param pidDeductVO
     * @return
     */
    List<PidDeductVO> pidDeductList(PidDeductVO pidDeductVO);
}
