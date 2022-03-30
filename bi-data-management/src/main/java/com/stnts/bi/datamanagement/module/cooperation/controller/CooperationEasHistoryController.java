package com.stnts.bi.datamanagement.module.cooperation.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationBiHistory;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationEasHistory;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationEasHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * eas合作伙伴信息变更记录 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-13
 */
@RestController
@RequestMapping("/cooperation/eas/history")
public class CooperationEasHistoryController {

    private final CooperationEasHistoryService cooperationEasHistoryService;

    public CooperationEasHistoryController(CooperationEasHistoryService cooperationEasHistoryService) {
        this.cooperationEasHistoryService = cooperationEasHistoryService;
    }

    @GetMapping("/list")
    public ResultEntity list(String id) {
        List<CooperationEasHistory> list = cooperationEasHistoryService.list(new QueryWrapper<CooperationEasHistory>()
                .lambda()
                .eq(CooperationEasHistory::getEasCode, id)
                .orderByDesc(CooperationEasHistory::getId));
        return ResultEntity.success(list);
    }

}
