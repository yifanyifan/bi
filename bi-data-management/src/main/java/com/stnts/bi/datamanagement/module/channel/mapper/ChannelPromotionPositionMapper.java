package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelChild;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotionPosition;
import com.stnts.bi.datamanagement.module.exportdata.param.ExportDataParam;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/3/2
 */
@Repository
public interface ChannelPromotionPositionMapper extends BaseMapper<ChannelPromotionPosition> {

    @Select("select count(1) from dm_channel_promotion_position where channel_id = #{channelId} and pp_status = 1")
    long countByChannel(Long channelId);

    List<Channel> countByChannelIdList(@Param("channelIdList") List<Long> channelIdList);

    List<ChannelPromotionPosition> selectListByExcel(@Param("paramList") List<ExportDataParam> channelChildByExcel);

    ChannelPromotionPosition selectByPid(@Param("pid") String pid);
}
