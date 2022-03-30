package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.param.ChannelCooperationPageParam;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPageParam;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionPageParam;
import com.stnts.bi.datamanagement.module.exportdata.param.ExportDataParam;
import com.stnts.bi.entity.sys.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 渠道合作 Mapper 接口
 *
 * @author 刘天元
 * @since 2021-02-03
 */
@Repository
public interface ChannelCooperationMapper extends BaseMapper<ChannelCooperation> {

    List<ChannelCooperation> listChannelCooperation(Page<ChannelCooperation> page, @Param("params") ChannelCooperationPageParam params, @Param("user") UserEntity user);

    @Update("update dm_channel_cooperation set agent_id = #{agentId}, agent_name = #{agentName}, channel_name = #{channelName} where channel_id = #{channelId}")
    int updateChannel(ChannelCooperation channelCooperation);

    /**
     * 根据channelId计算CCID个数
     *
     * @param channelId
     * @return
     */
    long countByChannel(Long channelId);

    List<ChannelCooperation> selectListByExcel(@Param("paramList") List<ExportDataParam> channelCooperationByExcel);

    List<Channel> countByChannelIdList(@Param("params") ChannelPageParam channelPageParam, @Param("userId") Integer userId);

    List<ChannelCooperation> searchAll(@Param("params") ChannelCooperationPageParam param, @Param("user") UserEntity userEntity);

    List<ChannelCooperation> migrationCCIDList(@Param("params") ChannelPromotionPageParam channelPromotionPageParam, @Param("user") UserEntity userEntity);

    List<ChannelCooperation> migrationChannelList(@Param("params") ChannelPromotionPageParam channelPromotionPageParam, @Param("user") UserEntity userEntity);

    List<ChannelCooperation> getChannelCooperationList(@Param("params") ChannelCooperationPageParam channelCooperationPageParam, @Param("user") UserEntity userEntity);

    List<ChannelCooperation> getChannelCooperationPageList(Page<ChannelCooperation> page, @Param("params") ChannelCooperationPageParam channelCooperationPageParam, @Param("user") UserEntity userEntity);

    void updateCompanyName(@Param("companyId") Long companyId, @Param("companyName") String companyName);

    List<ChannelCooperation> getAssociated(@Param("departmentCode") String departmentCode);

    void updateCompany(@Param("channelId") String channelId, @Param("companyId") String companyId, @Param("companyName") String companyName);

    List<ChannelCooperation> selectListSQL(@Param("params") ChannelCooperation channelCooperation);

    void updateDict(@Param("params") BusinessDict businessDict);
}
