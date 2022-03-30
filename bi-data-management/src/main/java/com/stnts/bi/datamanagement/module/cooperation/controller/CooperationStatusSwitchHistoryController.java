package com.stnts.bi.datamanagement.module.cooperation.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationStatusSwitchHistory;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationStatusSwitchHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 合作伙伴跟进状态 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-09
 */
@RestController
@RequestMapping("/cooperation/status/history")
public class CooperationStatusSwitchHistoryController {

    private final CooperationStatusSwitchHistoryService cooperationStatusSwitchHistoryService;

    public CooperationStatusSwitchHistoryController(CooperationStatusSwitchHistoryService cooperationStatusSwitchHistoryService) {
        this.cooperationStatusSwitchHistoryService = cooperationStatusSwitchHistoryService;
    }


    @GetMapping("/list")
    public ResultEntity list(Long cooperationId) {
        List<CooperationStatusSwitchHistory> list = cooperationStatusSwitchHistoryService.list(new QueryWrapper<CooperationStatusSwitchHistory>()
                .lambda().eq(CooperationStatusSwitchHistory::getCooperationId, cooperationId));
        return ResultEntity.success(list);
    }


}
