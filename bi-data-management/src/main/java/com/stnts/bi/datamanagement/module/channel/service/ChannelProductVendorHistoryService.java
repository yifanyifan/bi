package com.stnts.bi.datamanagement.module.channel.service;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductVendorHistory;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductVendorHistoryPageParam;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.entity.common.PageEntity;
import java.util.List;

/**
 * 产品历史CP厂商记录 服务类
 *
 * @author 易樊
 * @since 2021-09-28
 */
public interface ChannelProductVendorHistoryService extends IService<ChannelProductVendorHistory> {

    /**
     * 保存
     *
     * @param channelProductVendorHistory
     * @return
     * @throws Exception
     */
    boolean saveChannelProductVendorHistory(ChannelProductVendorHistory channelProductVendorHistory) throws Exception;

    /**
     * 修改
     *
     * @param channelProductVendorHistory
     * @return
     * @throws Exception
     */
    boolean updateChannelProductVendorHistory(ChannelProductVendorHistory channelProductVendorHistory) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteChannelProductVendorHistory(Long id) throws Exception;


    /**
     * 获取分页对象
     *
     * @param channelProductVendorHistoryPageParam
     * @return
     * @throws Exception
     */
    PageEntity<ChannelProductVendorHistory> getChannelProductVendorHistoryPageList(ChannelProductVendorHistoryPageParam channelProductVendorHistoryPageParam) throws Exception;

    /**
     * 获取列表对象
     *
     * @param channelProductVendorHistoryPageParam
     * @return
     * @throws Exception
     */
    List<ChannelProductVendorHistory> getChannelProductVendorHistoryList(ChannelProductVendorHistoryPageParam channelProductVendorHistoryPageParam) throws Exception;

}
