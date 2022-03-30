package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductVendorHistory;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductVendorHistoryPageParam;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import java.io.Serializable;

/**
 * 产品历史CP厂商记录 Mapper 接口
 *
 * @author 易樊
 * @since 2021-09-28
 */
@Repository
public interface ChannelProductVendorHistoryMapper extends BaseMapper<ChannelProductVendorHistory> {


}
