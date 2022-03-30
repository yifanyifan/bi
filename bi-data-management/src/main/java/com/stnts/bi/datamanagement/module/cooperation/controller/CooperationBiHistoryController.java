package com.stnts.bi.datamanagement.module.cooperation.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationBiHistory;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 信息变更记录 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-13
 */
@RestController
@RequestMapping("/cooperation/bi/history")
public class CooperationBiHistoryController {

    private final CooperationBiHistoryService cooperationBiHistoryService;

    public CooperationBiHistoryController(CooperationBiHistoryService cooperationBiHistoryService) {
        this.cooperationBiHistoryService = cooperationBiHistoryService;
    }

    @GetMapping("/list")
    public ResultEntity list(Long id) {
        List<CooperationBiHistory> list = cooperationBiHistoryService.list(new QueryWrapper<CooperationBiHistory>()
                .lambda()
                .eq(CooperationBiHistory::getCooperationBiId, id)
                .orderByDesc(CooperationBiHistory::getId));
        return ResultEntity.success(list);
    }

}
