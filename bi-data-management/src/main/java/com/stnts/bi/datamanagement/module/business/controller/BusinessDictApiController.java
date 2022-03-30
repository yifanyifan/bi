package com.stnts.bi.datamanagement.module.business.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 业务分类 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-08
 */
@Slf4j
@RestController
@RequestMapping("/api/business/dict")
public class BusinessDictApiController {
    private final BusinessDictController businessDictController;
    private final SignUtil signUtil;
    private final EnvironmentProperties environmentProperties;

    public BusinessDictApiController(BusinessDictController businessDictController, SignUtil signUtil, EnvironmentProperties environmentProperties) {
        this.businessDictController = businessDictController;
        this.signUtil = signUtil;
        this.environmentProperties = environmentProperties;
    }

    @GetMapping("/tree")
    public ResultEntity tree(String appId, Long timestamp, String sign, String keyword, HttpServletRequest request) {
        signUtil.checkSign(appId, timestamp, sign, Dict.create());
        return businessDictController.tree(keyword, request);
    }

    @GetMapping("/query")
    public ResultEntity query(String appId, Long timestamp, String sign, String keyword) {
        signUtil.checkSign(appId, timestamp, sign, Dict.create());
        return businessDictController.query(keyword);
    }

    /**
     * 查询业务分类列表
     */
    @PostMapping("/queryGeneral")
    public ResultEntity queryGeneral(@RequestBody BusinessDict businessDict, String appId, Long timestamp, String sign) {
        log.info("=======================>查询业务分类列表_通用：" + JSON.toJSONString(businessDict) + ",appId:" + appId + ",timestamp:" + timestamp + ",sign:" + sign);

        if (appId.equals(environmentProperties.getAppId2())) {
            if (StringUtils.isBlank(businessDict.getRootLevel()) && StringUtils.isBlank(businessDict.getDepartmentCode()) && ObjectUtil.isEmpty(businessDict.getUpdateTimeStart()) && ObjectUtil.isEmpty(businessDict.getUpdateTimeEnd())) {
                throw new BusinessException("查询参数必须选择一个以上");
            }
        }
        signUtil.checkSign(appId, timestamp, sign, Dict.create()
                .set("rootLevel", businessDict.getRootLevel())
                .set("departmentCode", businessDict.getDepartmentCode())
                .set("updateTimeStart", DateUtil.format(businessDict.getUpdateTimeStart(), "yyyy-MM-dd HH:mm:ss"))
                .set("updateTimeEnd", DateUtil.format(businessDict.getUpdateTimeEnd(), "yyyy-MM-dd HH:mm:ss"))
        );

        return businessDictController.queryGeneral(businessDict);
    }

}
