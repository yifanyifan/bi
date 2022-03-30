package com.stnts.bi.datamanagement.module.channel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotionPosition;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionPositionPageParam;
import com.stnts.bi.entity.common.PageEntity;

import java.util.List;

/**
 *  @author liang.zhang
 */
public interface ChannelPromotionPositionService extends IService<ChannelPromotionPosition> {

    /**
     * 保存
     *
     * @param channel
     * @return
     * @throws Exception
     */
    boolean saveChannel(ChannelPromotionPosition channel) throws Exception;

    /**
     * 修改
     *
     * @param channel
     * @return
     * @throws Exception
     */
    boolean updateChannelPromotionPosition(ChannelPromotionPosition channel) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteChannelPromotionPosition(Long id) throws Exception;


    /**
     * 获取分页对象
     *
     * @param channelPageParam
     * @return
     * @throws Exception
     */
    PageEntity<ChannelPromotionPosition> getChannelPromotionPositionPageList(ChannelPromotionPositionPageParam channelPageParam) throws Exception;

    /**
     * 获取列表对象
     *
     * @param channelPageParam
     * @return
     * @throws Exception
     */
    List<ChannelPromotionPosition> getChannelPromotionPositionList(ChannelPromotionPositionPageParam channelPageParam) throws Exception;

    /**
     * 批量修改推广位
     * @param channel
     * @return
     */
    boolean updateChannelPromotionPositionBatch(List<ChannelPromotionPosition> channel);

    boolean addChannelPromotionPositionBatch(List<ChannelPromotionPosition> channelPromotionPositionList);

    ChannelPromotionPosition addChannelPromotionPosition(ChannelPromotionPosition channel);
}
