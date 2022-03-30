package com.stnts.bi.datamanagement.module.channel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelClass;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.param.ChannelClassPageParam;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassNode;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassVO;
import com.stnts.bi.entity.common.PageEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 渠道类型 服务类
 *
 * @author 易樊
 * @since 2021-09-22
 */
public interface ChannelClassService extends IService<ChannelClass> {

    /**
     * 保存
     *
     * @param channelClass
     * @return
     * @throws Exception
     */
    boolean saveChannelClass(ChannelClass channelClass, HttpServletRequest request) throws Exception;

    /**
     * 修改
     *
     * @param channelClass
     * @return
     * @throws Exception
     */
    boolean updateChannelClass(ChannelClass channelClass, HttpServletRequest request) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteChannelClass(Long id) throws Exception;


    /**
     * 获取分页对象
     *
     * @param channelClassPageParam
     * @return
     * @throws Exception
     */
    PageEntity<ChannelClassVO> getChannelClassPageList(ChannelClassPageParam channelClassPageParam, HttpServletRequest request) throws Exception;

    /**
     * 获取列表对象
     *
     * @param channelClassPageParam
     * @return
     * @throws Exception
     */
    List<ChannelClassNode> getChannelClassList(ChannelClassPageParam channelClassPageParam, HttpServletRequest request) throws Exception;

    ChannelClassVO getChannelClass(Long id);

    boolean updateAssociated(ChannelClassVO channelClassVO);

    Map<String, Object> searchAll(ChannelClassPageParam param, HttpServletRequest request);

    List<ChannelClass> getPullList(ChannelClassPageParam channelClassPageParam);
}
