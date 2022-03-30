package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelApplication;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProduct;
import com.stnts.bi.datamanagement.module.channel.param.ChannelApplicationPageParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 应用表 Mapper 接口
 *
 * @author 易樊
 * @since 2022-01-13
 */
@Repository
public interface ChannelApplicationMapper extends BaseMapper<ChannelApplication> {
    List<ChannelApplication> getApplicationNameByChild(@Param("params") List paramList);

    List<ChannelApplication> selectByProductNameAndAppName(@Param("params") ChannelApplication channelApplication);

    List<ChannelApplication> selectPageParam(Page<ChannelApplication> page, @Param("params") ChannelApplicationPageParam channelApplicationPageParam);

    List<ChannelProduct> getUseAppProduct(@Param("id") Long id);
}
