package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelChild;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotion;
import com.stnts.bi.datamanagement.module.channel.param.ChannelChildPageParam;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelChildVO;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassVO;
import com.stnts.bi.datamanagement.module.exportdata.param.ExportDataParam;
import com.stnts.bi.entity.sys.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 子渠道 Mapper 接口
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Repository
public interface ChannelChildMapper extends BaseMapper<ChannelChild> {

    @Select("select count(distinct sub_channel_id) from dm_channel_child where ccid = #{ccid}")
    long countSubChannel(String ccid);

    @Select("select count(1) from dm_channel_child where channel_id = #{channelId}")
    long countByChannel(Long channelId);

    List<Channel> countByChannelIdList(@Param("channelIdList") List<Long> channelIdList);

    List<ChannelChild> listChannelChildByCcid(@Param("params") ChannelChildPageParam channelChildPageParam, @Param("user") UserEntity user);

    List<ChannelChild> selectListByExcel(@Param("paramList") List<ExportDataParam> channelChildByExcel);

    List<ChannelChildVO> channelChildListGeneral(@Param("param") ChannelChildVO channelChildVO);

    List<ChannelChild> getChannelChildListByPP(@Param("channelId") Long channelId);

    List<ChannelChild> selectListBySQL(@Param("page") Page<ChannelChild> page, @Param("params") ChannelChildPageParam channelChildPageParam);
}
