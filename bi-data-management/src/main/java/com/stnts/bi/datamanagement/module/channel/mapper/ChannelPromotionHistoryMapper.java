package com.stnts.bi.datamanagement.module.channel.mapper;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotionHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 渠道推广迁移历史表 Mapper 接口
 * </p>
 *
 * @author yifan
 * @since 2021-06-18
 */
@Repository
public interface ChannelPromotionHistoryMapper extends BaseMapper<ChannelPromotionHistory> {

    List<Map<String, String>> getCountByPidList(@Param("params") List<String> pidList);
}
