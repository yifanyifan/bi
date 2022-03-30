package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductCostCooperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 产品分成关联CCID Mapper 接口
 *
 * @author 易樊
 * @since 2021-09-24
 */
@Repository
public interface ChannelProductCostCooperationMapper extends BaseMapper<ChannelProductCostCooperation> {

    /**
     * 查本节点勾选
     */
    List<ChannelProductCostCooperation> selectListAll(@Param("productCode") String productCode, @Param("costId") String costId);

    /**
     * 查其它节点勾选
     */
    List<ChannelProductCostCooperation> selectListAll2(@Param("productCode") String productCode, @Param("costId") String costId);
}
