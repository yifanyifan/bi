package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelClass;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelClassCooperation;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.param.ChannelClassPageParam;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassCooperationVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 渠道类型关联CCID Mapper 接口
 *
 * @author 易樊
 * @since 2021-09-22
 */
@Repository
public interface ChannelClassCooperationMapper extends BaseMapper<ChannelClassCooperation> {
    List<ChannelClassCooperation> selectByDepartment(@Param("departmentCodeList") List<String> departmentCodeList);

    List<ChannelClassCooperation> searchAll(@Param("params") ChannelClassPageParam param);

    ChannelClass getChannelClassPath(@Param("params") ChannelCooperation channelCooperation);

    List<ChannelClassCooperationVO> getRepeatSelect(@Param("params") ChannelClassCooperationVO channelClassCooperationVO);

    List<ChannelClassCooperation> selectByChannelClassId(@Param("params") List<Long> channelClassIdList);
}
