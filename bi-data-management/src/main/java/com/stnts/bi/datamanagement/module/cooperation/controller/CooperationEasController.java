package com.stnts.bi.datamanagement.module.cooperation.controller;


import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationBi;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationEas;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationEasService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 合作伙伴 源表（EAS金蝶） 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
@RestController
@RequestMapping("/cooperation/eas")
public class CooperationEasController {

    private final CooperationEasService cooperationEasService;

    public CooperationEasController(CooperationEasService cooperationEasService) {
        this.cooperationEasService = cooperationEasService;
    }

    @GetMapping("/get")
    public ResultEntity get(String id) {
        CooperationEas cooperation = cooperationEasService.getById(id);
        return ResultEntity.success(cooperation);
    }
}
