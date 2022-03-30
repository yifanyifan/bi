package com.stnts.bi.datamanagement.module.channel.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProduct;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotion;
import com.stnts.bi.datamanagement.module.channel.param.PostPidParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionAllService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionService;
import com.stnts.bi.datamanagement.util.DozerUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 渠道推广宽表 前端控制器
 * </p>
 *
 * @author yifan
 * @since 2021-07-21
 */
@RestController
@RequestMapping("/channelPromotionAll")
public class ChannelPromotionAllController {
    @Value("${data-management.setting.youtop-api-host}")
    private String youtopApiHost;

    @Autowired
    private ChannelPromotionService channelPromotionService;
    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;
    @Autowired
    private ChannelProductService channelProductService;

    @PostMapping("/fill")
    @ApiOperation(value = "PID表存在，但宽表不存在，则用该接口对宽表填补", response = ResultEntity.class)
    public ResultEntity<String> addFill(@RequestBody List<String> fillPid) throws Exception {
        List<ChannelPromotion> channelPromotionList = channelPromotionService.list(new LambdaQueryWrapper<ChannelPromotion>().in(ChannelPromotion::getPid, fillPid));

        List<String> ccid = channelPromotionList.stream().map(ChannelPromotion::getPid).collect(Collectors.toList());
        fillPid.removeAll(ccid);
        if (CollectionUtil.isNotEmpty(fillPid)) {
            throw new BusinessException("PID不存在：" + StringUtils.join(fillPid, ","));
        }

        List<PostPidParam> postPidParam = new ArrayList<PostPidParam>();
        if (channelPromotionList != null && channelPromotionList.size() > 0) {
            List<String> productCodeList = channelPromotionList.stream().map(ChannelPromotion::getProductCode).collect(Collectors.toList());
            List<ChannelProduct> channelProductList = channelProductService.list(new LambdaQueryWrapper<ChannelProduct>().in(ChannelProduct::getProductCode, productCodeList));
            Map<String, String> channelProductMap = channelProductList.stream().collect(Collectors.toMap(item -> item.getProductCode(), item -> item.getProductId()));

            for (ChannelPromotion channelPromotion : channelPromotionList) {
                if (channelProductMap.containsKey(channelPromotion.getProductCode())) {
                    channelPromotion.setProductId(channelProductMap.get(channelPromotion.getProductCode()));
                }
            }

            List<ChannelPromotion> cps = channelPromotionList.stream().filter(i -> StringUtils.isNotBlank(i.getProductId())).collect(Collectors.toList());
            if (cps.size() > 0) {
                postPidParam = DozerUtil.toBeanList(cps, PostPidParam.class);
            }
        }
        channelPromotionAllService.addBatchThread(channelPromotionList, youtopApiHost, postPidParam);
        //channelPromotionAllService.addBatch(channelPromotionList);
        return ResultEntity.success(null);
    }
}
