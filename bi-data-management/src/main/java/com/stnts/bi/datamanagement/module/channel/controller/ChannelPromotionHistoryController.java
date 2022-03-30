package com.stnts.bi.datamanagement.module.channel.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotion;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotionHistory;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionHistoryPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionAllService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionHistoryService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionService;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 渠道推广迁移历史表 前端控制器
 * </p>
 *
 * @author yifan
 * @since 2021-06-18
 */
@RestController
@RequestMapping("/channelPromotionHistory")
@Api(value = "渠道推广迁移历史表", tags = {"渠道推广迁移历史表"})
public class ChannelPromotionHistoryController {
    @Autowired
    private ChannelPromotionHistoryService channelPromotionHistoryService;
    @Autowired
    private ChannelPromotionService channelPromotionService;
    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;

    @PostMapping("/getList")
    @ApiOperation(value = "渠道推广迁移历史列表", response = ChannelPromotionHistory.class)
    public ResultEntity<List<ChannelPromotionHistory>> getChannelList(@Validated @RequestBody ChannelPromotionHistoryPageParam pageParam, HttpServletRequest request) throws Exception {
        List<ChannelPromotionHistory> list = channelPromotionHistoryService.list(new QueryWrapper<ChannelPromotionHistory>()
                .lambda().eq(StrUtil.isNotEmpty(pageParam.getPid()), ChannelPromotionHistory::getPid, pageParam.getPid())
                .orderByDesc(ChannelPromotionHistory::getCheckStartDate).orderByDesc(ChannelPromotionHistory::getId)
        );

        return ResultEntity.success(list);
    }

    @PostMapping("/updateSub")
    @ApiOperation(value = "修改渠道推广迁移历史表【小字段】", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelPromotionSub(@Validated(Update.class) @RequestBody ChannelPromotionHistoryPageParam channelPromotionHistoryPageParam) throws Exception {
        ChannelPromotionHistory channelPromotionHistory = channelPromotionHistoryService.getById(channelPromotionHistoryPageParam.getId());
        ChannelPromotionHistory channelPromotionHistoryOld = DozerUtil.toBean(channelPromotionHistory, ChannelPromotionHistory.class);

        //起始或终止在范围内，则重复
        List<ChannelPromotionHistory> channelPromotionHistoryList = channelPromotionHistoryService.list(new QueryWrapper<ChannelPromotionHistory>().lambda()
                .eq(ChannelPromotionHistory::getPid, channelPromotionHistory.getPid())
                .and(x -> x.and(o -> o.le(ChannelPromotionHistory::getCheckStartDate, channelPromotionHistoryPageParam.getCheckStartDate())
                        .ge(ChannelPromotionHistory::getCheckEndDate, channelPromotionHistoryPageParam.getCheckStartDate()))
                        .or(i -> i.le(ChannelPromotionHistory::getCheckStartDate, channelPromotionHistoryPageParam.getCheckEndDate())
                                .ge(ChannelPromotionHistory::getCheckEndDate, channelPromotionHistoryPageParam.getCheckEndDate())
                        ))
                .ne(ChannelPromotionHistory::getId, channelPromotionHistory.getId())
        );
        ChannelPromotion channelPromotion = channelPromotionService.getOne(new QueryWrapper<ChannelPromotion>().lambda()
                .eq(ChannelPromotion::getPid, channelPromotionHistory.getPid())
                .and(x -> x.and(o -> o.le(ChannelPromotion::getCheckStartDate, channelPromotionHistoryPageParam.getCheckStartDate())
                        .ge(ChannelPromotion::getCheckEndDate, channelPromotionHistoryPageParam.getCheckStartDate()))
                        .or(i -> i.le(ChannelPromotion::getCheckStartDate, channelPromotionHistoryPageParam.getCheckEndDate())
                                .ge(ChannelPromotion::getCheckEndDate, channelPromotionHistoryPageParam.getCheckEndDate())
                        ).or(i -> i.ge(ChannelPromotion::getCheckStartDate, channelPromotionHistoryPageParam.getCheckStartDate())
                                .le(ChannelPromotion::getCheckEndDate, channelPromotionHistoryPageParam.getCheckEndDate())
                        ).or(i -> i.le(ChannelPromotion::getCheckEndDate, channelPromotionHistoryPageParam.getCheckStartDate())))
        );

        if (channelPromotionHistoryList.size() > 0 || channelPromotion != null) {
            Set<String> pidErrorList = new HashSet<String>();
            if (channelPromotionHistoryList.size() > 0) {
                pidErrorList.addAll(channelPromotionHistoryList.stream().map(ChannelPromotionHistory::getPid).collect(Collectors.toList()));
            }
            if (channelPromotion != null) {
                pidErrorList.add(channelPromotion.getPid());
            }
            throw new BusinessException("有效期冲突：（" + StringUtils.join(pidErrorList, "、") + "）");
        }

        channelPromotionHistory.setCheckStartDate(channelPromotionHistoryPageParam.getCheckStartDate());
        channelPromotionHistory.setCheckEndDate(channelPromotionHistoryPageParam.getCheckEndDate());
        Boolean b = channelPromotionHistoryService.updateById(channelPromotionHistory);

        //存宽表
        channelPromotionAllService.updateHistroySub(channelPromotionHistoryOld, channelPromotionHistory);

        return ResultEntity.success(b);
    }
}
