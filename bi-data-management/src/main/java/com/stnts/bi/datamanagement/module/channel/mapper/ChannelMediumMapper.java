package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelMedium;

import com.stnts.bi.datamanagement.module.exportdata.param.ExportDataParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 媒介信息 Mapper 接口
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Repository
public interface ChannelMediumMapper extends BaseMapper<ChannelMedium> {
    List<ChannelMedium> selectListByMedium(@Param("paramList") List<ExportDataParam> mediumByExcel);
}
