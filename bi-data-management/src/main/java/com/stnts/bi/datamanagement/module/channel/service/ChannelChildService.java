package com.stnts.bi.datamanagement.module.channel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelChild;
import com.stnts.bi.datamanagement.module.channel.param.ChannelChildPageParam;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelChildVO;
import com.stnts.bi.datamanagement.module.exportdata.param.ExportDataParam;
import com.stnts.bi.entity.common.PageEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 子渠道 服务类
 *
 * @author 刘天元
 * @since 2021-02-04
 */
public interface ChannelChildService extends IService<ChannelChild> {

    /**
     * 保存
     *
     * @param channelChild
     * @return
     * @throws Exception
     */
    Map<Object, Object> saveChannelChild(ChannelChild channelChild) throws Exception;

    /**
     * 修改
     *
     * @param channelChild
     * @return
     * @throws Exception
     */
    boolean updateChannelChild(ChannelChild channelChild) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteChannelChild(Long id) throws Exception;


    /**
     * 获取分页对象
     *
     * @param channelChildPageParam
     * @return
     * @throws Exception
     */
    PageEntity<ChannelChild> getChannelChildPageList(ChannelChildPageParam channelChildPageParam) throws Exception;

    /**
     * 获取列表对象
     *
     * @param channelChildPageParam
     * @return
     * @throws Exception
     */
    List<ChannelChild> getChannelChildList(ChannelChildPageParam channelChildPageParam) throws Exception;

    List<ChannelChild> getChannelChildListByPP(ChannelChildPageParam channelChildPageParam);

    /**
     * 通过ccid获取子渠道列表
     *
     * @param ccid
     * @return
     * @throws Exception
     */
    List<ChannelChild> getChannelChildListByCcid(ChannelChildPageParam channelChildPageParam, HttpServletRequest request) throws Exception;

    /**
     * 获取子渠道信息拓展
     *
     * @param id
     * @return
     */
    ChannelChild getByIdExt(Long id);

    List<ChannelChild> selectListByExcel(List<ExportDataParam> channelChildByExcel);

    ChannelChildVO saveChannelChildGeneral(ChannelChild channelChild) throws Exception;

    List<ChannelChildVO> channelChildListGeneral(ChannelChildVO channelChildVO);
}
