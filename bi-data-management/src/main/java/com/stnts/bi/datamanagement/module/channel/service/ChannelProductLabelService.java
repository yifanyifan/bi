package com.stnts.bi.datamanagement.module.channel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductLabel;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductLabelPageParam;
import com.stnts.bi.entity.common.PageEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 产品标签 服务类
 *
 * @author 易樊
 * @since 2022-01-26
 */
public interface ChannelProductLabelService extends IService<ChannelProductLabel> {

    /**
     * 保存
     *
     * @param channelProductLabel
     * @return
     * @throws Exception
     */
    boolean saveChannelProductLabel(ChannelProductLabel channelProductLabel, HttpServletRequest request) throws Exception;

    /**
     * 修改
     *
     * @param channelProductLabel
     * @return
     * @throws Exception
     */
    boolean updateChannelProductLabel(ChannelProductLabel channelProductLabel) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteChannelProductLabel(Long id) throws Exception;


    /**
     * 获取分页对象
     *
     * @param channelProductLabelPageParam
     * @return
     * @throws Exception
     */
    PageEntity<ChannelProductLabel> getChannelProductLabelPageList(ChannelProductLabelPageParam channelProductLabelPageParam) throws Exception;

    /**
     * 获取列表对象
     *
     * @param channelProductLabelPageParam
     * @return
     * @throws Exception
     */
    List<ChannelProductLabel> getChannelProductLabelList(ChannelProductLabelPageParam channelProductLabelPageParam) throws Exception;

    Map getTreeAll();
}
