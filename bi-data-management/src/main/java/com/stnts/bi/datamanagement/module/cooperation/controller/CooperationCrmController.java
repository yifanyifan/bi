package com.stnts.bi.datamanagement.module.cooperation.controller;


import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.cooperation.param.CooperationAddApiParam;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationCrmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 合作伙伴（CRM） 前端控制器
 * </p>
 *
 * @author yifan
 * @since 2021-07-29
 */
@Slf4j
@RestController
@RequestMapping("/cooperation/cooperation-crm")
public class CooperationCrmController {
    @Autowired
    private CooperationCrmService cooperationCrmService;

    @PostMapping("/addCompany")
    public ResultEntity<Map<String, String>> addCompany(@RequestBody CooperationAddApiParam cooperationAddApiParam) {
        try {
            Map<String, String> map = cooperationCrmService.addCompany(cooperationAddApiParam);
            return ResultEntity.success(map);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            return ResultEntity.failure(e.getMessage());
        }
    }

    /**
     * 数据来源=CRM的公司，支持编辑公司名称 以及其他必填项
     *
     * @param cooperationAddApiParam
     * @return
     */
    @PostMapping("/updateCompany")
    public ResultEntity<Map<String, String>> updateCompany(@RequestBody CooperationAddApiParam cooperationAddApiParam) {
        try {
            cooperationCrmService.updateCompany(cooperationAddApiParam);
            return ResultEntity.success(null);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            return ResultEntity.failure(e.getMessage());
        }
    }

    /**
     * 渠道来源= CRM的渠道，支持修改公司
     *
     * @param cooperationAddApiParam
     * @return
     */
    @PostMapping("/updateChannel")
    public ResultEntity<Map<String, String>> updateChannel(@RequestBody CooperationAddApiParam cooperationAddApiParam) {
        try {
            cooperationCrmService.updateChannel(cooperationAddApiParam);

            return ResultEntity.success(null);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            return ResultEntity.failure(e.getMessage());
        }
    }
}
