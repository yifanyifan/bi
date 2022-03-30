package com.stnts.bi.datamanagement.module.channel.controller;

import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductLabel;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductLabelPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductLabelService;
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
 * 产品标签 控制器
 *
 * @author 易樊
 * @since 2022-01-26
 */
@Slf4j
@RestController
@RequestMapping("/sign/channelProductLabel")
@Api(value = "产品标签API", tags = {"签名-产品标签"})
public class ChannelProductLabelSignController {
    @Autowired
    private ChannelProductLabelService channelProductLabelService;
    @Autowired
    private SignUtil signUtil;
    @Autowired
    private EnvironmentProperties environmentProperties;

    /**
     * 产品标签列表
     */
    @PostMapping("/getListGeneral")
    @ApiOperation(value = "产品标签列表", response = ChannelProductLabel.class)
    public ResultEntity<List<ChannelProductLabel>> getChannelProductLabelList(@RequestBody ChannelProductLabelPageParam channelProductLabelPageParam, String appId, Long timestamp, String sign) throws Exception {
        log.info("===========================>产品标签列表_通用，请求参数：{}, appId:{}, timestamp:{}, sign:{}", JSON.toJSON(channelProductLabelPageParam), appId, timestamp, sign);

        //鉴权
        signUtil.checkSign(appId, timestamp, sign, Dict.create()
                .set("labelArea", channelProductLabelPageParam.getLabelArea())
                .set("labelLevel", channelProductLabelPageParam.getLabelLevel())
                .set("labelValue", channelProductLabelPageParam.getLabelValue())
        );

        List<ChannelProductLabel> list = channelProductLabelService.getChannelProductLabelList(channelProductLabelPageParam);
        return ResultEntity.success(list);
    }
}

