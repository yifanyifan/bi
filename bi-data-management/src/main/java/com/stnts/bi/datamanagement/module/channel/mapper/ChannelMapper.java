package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPageParam;
import com.stnts.bi.datamanagement.module.exportdata.param.ExportDataParam;
import com.stnts.bi.entity.sys.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/3/1
 */
@Repository
public interface ChannelMapper extends BaseMapper<Channel> {

    List<Channel> getPageList(Page<Channel> page, @Param("params") ChannelPageParam params, @Param("user") UserEntity userEntity);

    List<Channel> selectListByExcel(@Param("paramList") List<ExportDataParam> paramList);

    List<Channel> secrchList(@Param("params") ChannelPageParam channelPageParam);

    void updateCompanyName(@Param("companyId") Long companyId, @Param("companyName") String companyName);

    List<ChannelCooperation> getSettleCCIDByProd(@Param("productCodeList") List<String> productCodeList);

    List<ExportDataParam> validationChannel01(@Param("params") List<ExportDataParam> channelList01);

    List<ExportDataParam> validationChannel02(@Param("params") List<ExportDataParam> channelList02);
}
