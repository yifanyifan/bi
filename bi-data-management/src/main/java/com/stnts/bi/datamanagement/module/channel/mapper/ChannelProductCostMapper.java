package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductCost;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductCostPageParam;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassNode;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 产品分成 Mapper 接口
 *
 * @author 易樊
 * @since 2021-09-24
 */
@Repository
public interface ChannelProductCostMapper extends BaseMapper<ChannelProductCost> {
    /**
     * 产品对应的所有CCID
     *
     * @param productCode
     * @return
     */
    List<ChannelClassNode> getAllCCIDByProduct(@Param("productCode") String productCode);

    /**
     * 当前 产品分成 中被 勾选的 CCID集合
     *
     * @param params
     * @return
     */
    List<ChannelCooperation> getCCIDCostExt(@Param("params") ChannelProductCostPageParam params);

    Long countByProductCode(@Param("productCode") String productCode, @Param("costId") String costId);
}
