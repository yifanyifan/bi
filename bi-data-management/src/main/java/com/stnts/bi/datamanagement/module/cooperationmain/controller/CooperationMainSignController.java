package com.stnts.bi.datamanagement.module.cooperationmain.controller;

import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.cooperationmain.entity.CooperationMain;
import com.stnts.bi.datamanagement.module.cooperationmain.param.CooperationMainPageParam;
import com.stnts.bi.datamanagement.module.cooperationmain.service.CooperationMainService;
import com.stnts.bi.datamanagement.util.SignUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公司主体 控制器
 *
 * @author 易樊
 * @since 2021-09-17
 */
@Slf4j
@RestController
@RequestMapping("/sign/cooperationMain")
@Api(value = "公司主体API", tags = {"签名-公司主体"})
public class CooperationMainSignController {

    @Autowired
    private CooperationMainService cooperationMainService;
    @Autowired
    private SignUtil signUtil;
    @Autowired
    private EnvironmentProperties environmentProperties;

    @PostMapping("/getListGeneral")
    @ApiOperation(value = "公司主体列表", response = CooperationMain.class)
    public ResultEntity<List<CooperationMain>> getListGeneral(@Validated @RequestBody CooperationMainPageParam cooperationMainPageParam, String appId, Long timestamp, String sign) throws Exception {
        log.info("===========================>公司主体列表_通用，请求参数：{}, appId:{}, timestamp:{}, sign:{}", JSON.toJSON(cooperationMainPageParam), appId, timestamp, sign);

        if (appId.equals(environmentProperties.getAppId2())) {
            if (StringUtils.isBlank(cooperationMainPageParam.getDepartmentCode()) && StringUtils.isBlank(cooperationMainPageParam.getCooperationMainName())) {
                throw new BusinessException("查询参数必须选择一个以上");
            }
        }
        //鉴权
        signUtil.checkSign(appId, timestamp, sign, Dict.create().set("departmentCode", cooperationMainPageParam.getDepartmentCode()).set("cooperationMainName", cooperationMainPageParam.getCooperationMainName()));

        List<CooperationMain> list = cooperationMainService.getCooperationMainListGeneral(cooperationMainPageParam);
        return ResultEntity.success(list);
    }

}

