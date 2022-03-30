package com.stnts.bi.datamanagement.module.channel.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelApplication;
import com.stnts.bi.datamanagement.module.channel.param.ChannelApplicationPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelApplicationService;
import com.stnts.bi.datamanagement.util.SignUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 应用表 控制器
 *
 * @author 易樊
 * @since 2022-01-13
 */
@Slf4j
@RestController
@RequestMapping("/sign/channelApplication")
@Api(value = "应用表API", tags = {"签名-应用表"})
public class ChannelApplicationSignController {

    @Autowired
    private ChannelApplicationService channelApplicationService;
    @Autowired
    private SignUtil signUtil;
    @Autowired
    private EnvironmentProperties environmentProperties;

    /**
     * 应用表列表
     */
    @PostMapping("/getListGeneral")
    @ApiOperation(value = "应用表列表", response = ChannelApplication.class)
    public ResultEntity<List<ChannelApplication>> getChannelApplicationList(@RequestBody ChannelApplicationPageParam channelApplicationPageParam, String appId, Long timestamp, String sign) throws Exception {
        log.info("===========================>应用表列表_通用，请求参数：{}, appId:{}, timestamp:{}, sign:{}", JSON.toJSON(channelApplicationPageParam), appId, timestamp, sign);

        //鉴权
        signUtil.checkSign(appId, timestamp, sign, Dict.create()
                .set("applicationName", channelApplicationPageParam.getApplicationName())
                .set("updateTimeStart", DateUtil.format(channelApplicationPageParam.getUpdateTimeStart(), "yyyy-MM-dd HH:mm:ss"))
                .set("updateTimeEnd", DateUtil.format(channelApplicationPageParam.getUpdateTimeEnd(), "yyyy-MM-dd HH:mm:ss"))
        );

        List<ChannelApplication> list = channelApplicationService.getChannelApplicationList(channelApplicationPageParam);
        return ResultEntity.success(list);
    }

}

