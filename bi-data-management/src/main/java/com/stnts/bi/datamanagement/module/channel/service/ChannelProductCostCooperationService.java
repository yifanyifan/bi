package com.stnts.bi.datamanagement.module.channel.service;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductCostCooperation;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductCostCooperationPageParam;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.entity.common.PageEntity;
import java.util.List;

/**
 * 产品分成关联CCID 服务类
 *
 * @author 易樊
 * @since 2021-09-24
 */
public interface ChannelProductCostCooperationService extends IService<ChannelProductCostCooperation> {

    /**
     * 保存
     *
     * @param channelProductCostCooperation
     * @return
     * @throws Exception
     */
    boolean saveChannelProductCostCooperation(ChannelProductCostCooperation channelProductCostCooperation) throws Exception;

    /**
     * 修改
     *
     * @param channelProductCostCooperation
     * @return
     * @throws Exception
     */
    boolean updateChannelProductCostCooperation(ChannelProductCostCooperation channelProductCostCooperation) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteChannelProductCostCooperation(Long id) throws Exception;


    /**
     * 获取分页对象
     *
     * @param channelProductCostCooperationPageParam
     * @return
     * @throws Exception
     */
    PageEntity<ChannelProductCostCooperation> getChannelProductCostCooperationPageList(ChannelProductCostCooperationPageParam channelProductCostCooperationPageParam) throws Exception;

    /**
     * 获取列表对象
     *
     * @param channelProductCostCooperationPageParam
     * @return
     * @throws Exception
     */
    List<ChannelProductCostCooperation> getChannelProductCostCooperationList(ChannelProductCostCooperationPageParam channelProductCostCooperationPageParam) throws Exception;

}
