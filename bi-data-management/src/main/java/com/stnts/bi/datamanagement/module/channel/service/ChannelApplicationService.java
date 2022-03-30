package com.stnts.bi.datamanagement.module.channel.service;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelApplication;
import com.stnts.bi.datamanagement.module.channel.param.ChannelApplicationPageParam;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.entity.common.PageEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 应用表 服务类
 *
 * @author 易樊
 * @since 2022-01-13
 */
public interface ChannelApplicationService extends IService<ChannelApplication> {

    /**
     * 保存
     *
     * @param channelApplication
     * @return
     * @throws Exception
     */
    boolean saveChannelApplication(ChannelApplication channelApplication, HttpServletRequest request) throws Exception;

    /**
     * 修改
     *
     * @param channelApplication
     * @return
     * @throws Exception
     */
    boolean updateChannelApplication(ChannelApplication channelApplication) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteChannelApplication(Long id) throws Exception;


    /**
     * 获取分页对象
     *
     * @param channelApplicationPageParam
     * @return
     * @throws Exception
     */
    PageEntity<ChannelApplication> getChannelApplicationPageList(ChannelApplicationPageParam channelApplicationPageParam) throws Exception;

    /**
     * 获取列表对象
     *
     * @param channelApplicationPageParam
     * @return
     * @throws Exception
     */
    List<ChannelApplication> getChannelApplicationList(ChannelApplicationPageParam channelApplicationPageParam) throws Exception;

}
