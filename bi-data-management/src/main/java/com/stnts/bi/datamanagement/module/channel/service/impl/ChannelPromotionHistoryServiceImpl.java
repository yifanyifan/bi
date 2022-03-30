package com.stnts.bi.datamanagement.module.channel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotionHistory;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelPromotionHistoryMapper;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 渠道推广迁移历史表 服务实现类
 * </p>
 *
 * @author yifan
 * @since 2021-06-18
 */
@Service
public class ChannelPromotionHistoryServiceImpl extends ServiceImpl<ChannelPromotionHistoryMapper, ChannelPromotionHistory> implements ChannelPromotionHistoryService {
    @Autowired
    private ChannelPromotionHistoryMapper channelPromotionHistoryMapper;

    @Override
    public Map<String, String> getCountByPidList(List<String> pidList) {
        List<Map<String, String>> res = channelPromotionHistoryMapper.getCountByPidList(pidList);

        Map<String, String> map = res.stream().collect(Collectors.toMap(i -> String.valueOf(i.get("pid")), i -> String.valueOf(i.get("num"))));

        return map;
    }
}
