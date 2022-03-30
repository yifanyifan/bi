package com.stnts.bi.datamanagement.module.channel.service;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotionHistory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 渠道推广迁移历史表 服务类
 * </p>
 *
 * @author yifan
 * @since 2021-06-18
 */
public interface ChannelPromotionHistoryService extends IService<ChannelPromotionHistory> {

    Map<String, String> getCountByPidList(List<String> pidList);
}
