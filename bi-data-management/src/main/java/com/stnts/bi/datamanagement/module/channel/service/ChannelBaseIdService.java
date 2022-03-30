package com.stnts.bi.datamanagement.module.channel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelBaseId;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotion;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 基础ID标识 服务类
 * </p>
 *
 * @author yifan
 * @since 2021-06-10
 */
public interface ChannelBaseIdService extends IService<ChannelBaseId> {

    public Long getNewChannelID();

    public void updateNewChannelID(Long channelId);

    public String getNewSubChannelID(Long cId);

    public String getNewCCID(Long cId, String chargeRule);

    public String getNewPID();

    public List<String> getNewPIDs(int num);

    void getPidAliasAll(Integer pidNum, String pidAlias, ChannelPromotion channelPromotion) throws Exception;
}
