package com.stnts.bi.datamanagement.module.channel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.channel.entity.*;
import com.stnts.bi.datamanagement.module.channel.param.PostPidParam;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperationmain.entity.CooperationMain;

import java.util.List;

/**
 * <p>
 * 渠道推广宽表 服务类
 * </p>
 *
 * @author yifan
 * @since 2021-07-21
 */
public interface ChannelPromotionAllService extends IService<ChannelPromotionAll> {
    /**
     * 新增/更新/批量录入/批量导入/小字段编辑/删除/ PID【多线程异步调用】
     */
    public void addBatchThread(List<ChannelPromotion> channelPromotionListParam, String host, List<PostPidParam> postPidParamAllList);

    /**
     * 新增/更新/批量录入/批量导入/小字段编辑/删除/ PID
     */
    public void addBatch(List<ChannelPromotion> channelPromotion);

    /**
     * 迁移 PID【多线程异步调用】
     *
     * @param channelPromotionListParam
     */
    void migrationThread(List<ChannelPromotion> channelPromotionListParam);

    /**
     * 迁移 PID
     *
     * @param channelPromotionListParam
     */
    public void migration(List<ChannelPromotion> channelPromotionListParam);

    /**
     * 修改 CCID【多线程异步调用】
     *
     * @param channelCooperation
     */
    void updateCCIDThread(ChannelCooperation channelCooperation);

    /**
     * 修改 CCID
     *
     * @param channelCooperation
     */
    public void updateCCID(ChannelCooperation channelCooperation);

    /**
     * 修改 渠道【多线程异步调用】
     */
    void updateChannelThread(Channel channel);

    /**
     * 修改 渠道
     */
    public void updateChannel(Channel channel);

    /**
     * 修改 子渠道【多线程异步调用】
     */
    void updateChannelChildThread(ChannelChild channelChild);

    /**
     * 修改 子渠道
     */
    public void updateChannelChild(ChannelChild channelChild);

    /**
     * 编辑 推广位【多线程异步调用】
     *
     * @param cpp
     */
    void updateChannelPromotionPositionThread(ChannelPromotionPosition cpp);

    /**
     * 编辑 推广位
     *
     * @param cpp
     */
    public void updateChannelPromotionPosition(ChannelPromotionPosition cpp);

    /**
     * 编辑 媒介【多线程异步调用】
     *
     * @param channelMedium
     */
    void updateChannelMediumThread(ChannelMedium channelMedium);

    /**
     * 编辑 媒介
     *
     * @param channelMedium
     */
    public void updateChannelMedium(ChannelMedium channelMedium);

    /**
     * 编辑业务分类【多线程异步调用】
     */
    void updateDictThread(BusinessDict businessDict, BusinessDict targetBusinessDict);

    /**
     * 编辑业务分类
     */
    public void updateDict(BusinessDict businessDict, BusinessDict targetBusinessDict);

    /**
     * 编辑合作方【多线程异步调用】
     *
     * @param cooperation
     */
    public void updateCompanyThread(Cooperation cooperation);

    /**
     * 编辑合作方
     *
     * @param cooperation
     */
    public void updateCompany(Cooperation cooperation);

    /**
     * 编辑 产品/应用【多线程异步调用】
     *
     * @param channelProduct
     */
    public void updateProductThread(ChannelProduct channelProduct);

    /**
     * 编辑 产品/应用
     *
     * @param channelProduct
     */
    public void updateProduct(ChannelProduct channelProduct);

    public void updateHistroySub(ChannelPromotionHistory channelPromotionHistoryOld, ChannelPromotionHistory channelPromotionHistory);

    public void moveCPCompany(ChannelProduct channelProduct);

    public void updateMainThread(CooperationMain cooperationMain);

    void updateMain(CooperationMain cooperationMain);

    public void updateNeijieThread(List<String> pidList, String ccidSettlement);

    void updateNeijie(List<String> pidList, String ccidSettlement);

    /**
     * 编辑 应用【多线程异步调用】
     *
     * @param channelApplication
     */
    public void updateChannelApplicationThread(ChannelApplication channelApplication);

    /**
     * 编辑 应用
     *
     * @param channelApplication
     */
    void updateChannelApplication(ChannelApplication channelApplication);

    /**
     * CCID批量替换业务分类【多线程异步调用】
     *
     * @param ccidList
     * @param channelCooperation
     */
    public void updateBusinessDictBatchThread(List<String> ccidList, ChannelCooperation channelCooperation);

    /**
     * CCID批量替换业务分类
     *
     * @param ccidList
     * @param channelCooperation
     */
    void updateBusinessDictBatch(List<String> ccidList, ChannelCooperation channelCooperation);
}
