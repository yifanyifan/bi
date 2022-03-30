package com.stnts.bi.datamanagement.module.channel.service;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelClassCooperation;
import com.stnts.bi.datamanagement.module.channel.param.ChannelClassCooperationPageParam;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.entity.common.PageEntity;
import java.util.List;

/**
 * 渠道类型关联CCID 服务类
 *
 * @author 易樊
 * @since 2021-09-22
 */
public interface ChannelClassCooperationService extends IService<ChannelClassCooperation> {

    /**
     * 保存
     *
     * @param channelClassCooperation
     * @return
     * @throws Exception
     */
    boolean saveChannelClassCooperation(ChannelClassCooperation channelClassCooperation) throws Exception;

    /**
     * 修改
     *
     * @param channelClassCooperation
     * @return
     * @throws Exception
     */
    boolean updateChannelClassCooperation(ChannelClassCooperation channelClassCooperation) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteChannelClassCooperation(Long id) throws Exception;


    /**
     * 获取分页对象
     *
     * @param channelClassCooperationPageParam
     * @return
     * @throws Exception
     */
    PageEntity<ChannelClassCooperation> getChannelClassCooperationPageList(ChannelClassCooperationPageParam channelClassCooperationPageParam) throws Exception;

    /**
     * 获取列表对象
     *
     * @param channelClassCooperationPageParam
     * @return
     * @throws Exception
     */
    List<ChannelClassCooperation> getChannelClassCooperationList(ChannelClassCooperationPageParam channelClassCooperationPageParam) throws Exception;
}
