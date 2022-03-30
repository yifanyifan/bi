package com.stnts.bi.datamanagement.module.business.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.business.entity.BusinessCheckHistory;
import com.stnts.bi.datamanagement.module.business.service.BusinessCheckHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 业务考核历史记录 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-10
 */
@RestController
@RequestMapping("/business/check/history")
public class BusinessCheckHistoryController {

    private final BusinessCheckHistoryService businessCheckHistoryService;

    public BusinessCheckHistoryController(BusinessCheckHistoryService businessCheckHistoryService) {
        this.businessCheckHistoryService = businessCheckHistoryService;
    }

    @GetMapping("/list")
    public ResultEntity list(Long businessCheckId, String operator, String startDate, String endDate) {
        List<BusinessCheckHistory> list = businessCheckHistoryService.list(new QueryWrapper<BusinessCheckHistory>()
                .lambda()
                .eq(BusinessCheckHistory::getBusinessCheckId, businessCheckId)
                .eq(StrUtil.isNotEmpty(operator), BusinessCheckHistory::getCreateUserId, operator)
                .ge(StrUtil.isNotEmpty(startDate), BusinessCheckHistory::getCreateTime, startDate + " 00:00:00")
                .le(StrUtil.isNotEmpty(endDate), BusinessCheckHistory::getCreateTime, endDate + " 23:59:59")
                .orderByDesc(BusinessCheckHistory::getId));
        return ResultEntity.success(list);
    }

}
