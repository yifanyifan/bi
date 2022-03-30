package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProduct;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductLabel;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductLabelPageParam;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import java.io.Serializable;
import java.util.List;

/**
 * 产品标签 Mapper 接口
 *
 * @author 易樊
 * @since 2022-01-26
 */
@Repository
public interface ChannelProductLabelMapper extends BaseMapper<ChannelProductLabel> {
    List<ChannelProductLabel> selectPageParam(Page<ChannelProductLabel> page, @Param("params") ChannelProductLabelPageParam channelProductLabelPageParam);

    List<ChannelProduct> getUseLabelProduct(@Param("id") Long id);
}
