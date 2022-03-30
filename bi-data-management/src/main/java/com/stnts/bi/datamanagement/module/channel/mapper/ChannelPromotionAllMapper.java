package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.datamanagement.module.channel.entity.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 渠道推广宽表 Mapper 接口
 * </p>
 *
 * @author yifan
 * @since 2021-07-21
 */
@Repository
public interface ChannelPromotionAllMapper extends BaseMapper<ChannelPromotionAll> {

    void updateByFlag(@Param("params") List<String> pidList, @Param("flag") String flag);

    void updateOldPid(@Param("params") ChannelPromotion channelPromotion);

    List<ChannelPromotionAll> selectListByApp(@Param("productCode") String productCode);

    ChannelPromotionAll selectOneByHistory(@Param("params") ChannelPromotionHistory channelPromotionHistoryOld);

    void moveCPCompany(@Param("params") ChannelProduct channelProduct);

    void updateCompanyName(@Param("companyId") Long companyId, @Param("companyName") String companyName);

    void updateCompany(@Param("channelId") String channelId, @Param("companyId") String companyId, @Param("companyName") String companyName);

    void updateChannelNameSettlement(@Param("params") Channel channel);

    void updateChannelSettlementByCCIDSettlement(@Param("params") ChannelCooperation channelCooperation);
}
