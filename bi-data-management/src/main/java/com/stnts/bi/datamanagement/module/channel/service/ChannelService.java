package com.stnts.bi.datamanagement.module.channel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPageParam;
import com.stnts.bi.datamanagement.module.cooperation.param.CooperationAddApiParam;
import com.stnts.bi.entity.common.PageEntity;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author liang.zhang
 */
public interface ChannelService extends IService<Channel> {

    /**
     * 保存
     *
     * @param channel
     * @return
     * @throws Exception
     */
    Channel saveChannel(Channel channel) throws Exception;

    /**
     * 修改
     *
     * @param channel
     * @return
     * @throws Exception
     */
    boolean updateChannel(Channel channel) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteChannel(Long id) throws Exception;


    /**
     * 获取分页对象
     *
     * @param channelPageParam
     * @return
     * @throws Exception
     */
    PageEntity<Channel> getChannelPageList(ChannelPageParam channelPageParam, HttpServletRequest request) throws Exception;

    public List<Channel> getChannelListCRM(ChannelPageParam channelPageParam) throws Exception;

    /**
     * 获取列表对象（无鉴权）
     *
     * @param channelPageParam
     * @return
     */
    public List<Channel> getChannelList(ChannelPageParam channelPageParam) throws Exception;

    /**
     * 获取列表对象
     *
     * @param channelPageParam
     * @return
     * @throws Exception
     */
    List<Channel> getChannelList(ChannelPageParam channelPageParam, HttpServletRequest request) throws Exception;

    Map<String, Object> searchList(String departmentCode, String companyId, Long channelId, Integer secretType, HttpServletRequest request);

    List<Channel> getChannelListGeneral(Channel channel);

    Channel saveChannelGeneral(Channel channel);

    /**
     * 当前 产品对应部门下 内结渠道 对应的 CCID
     *
     * @param productCodeList
     * @return
     */
    List<ChannelCooperation> getSettleCCIDByProd(List<String> productCodeList);

    void updateCompanyName(CooperationAddApiParam cooperationAddApiParam);
}
