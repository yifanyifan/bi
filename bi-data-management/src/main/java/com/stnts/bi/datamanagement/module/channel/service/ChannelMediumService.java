package com.stnts.bi.datamanagement.module.channel.service;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelMedium;
import com.stnts.bi.datamanagement.module.channel.param.ChannelMediumPageParam;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.datamanagement.module.channel.vo.DepartmentVO;
import com.stnts.bi.entity.common.PageEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 媒介信息 服务类
 *
 * @author 刘天元
 * @since 2021-02-04
 */
public interface ChannelMediumService extends IService<ChannelMedium> {

    /**
     * 保存
     *
     * @param channelMedium
     * @return
     * @throws Exception
     */
    ChannelMedium saveChannelMedium(ChannelMedium channelMedium) throws Exception;

    /**
     * 修改
     *
     * @param channelMedium
     * @return
     * @throws Exception
     */
    boolean updateChannelMedium(ChannelMedium channelMedium) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteChannelMedium(Long id) throws Exception;


    /**
     * 获取分页对象
     *
     * @param channelMediumPageParam
     * @return
     * @throws Exception
     */
    PageEntity<ChannelMedium> getChannelMediumPageList(ChannelMediumPageParam channelMediumPageParam, HttpServletRequest request) throws Exception;

    /**
     * 获取列表对象
     *
     * @param channelMediumPageParam
     * @return
     * @throws Exception
     */
    List<ChannelMedium> getChannelMediumList(ChannelMediumPageParam channelMediumPageParam, HttpServletRequest request) throws Exception;

    /**
     * 部门搜索
     * @return
     */
    List<DepartmentVO> listDepartment(HttpServletRequest request);

}
