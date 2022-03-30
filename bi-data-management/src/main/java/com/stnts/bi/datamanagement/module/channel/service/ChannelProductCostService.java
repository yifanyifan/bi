package com.stnts.bi.datamanagement.module.channel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductCost;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductCostPageParam;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassNode;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelProductCostVO;
import com.stnts.bi.entity.common.PageEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 产品分成 服务类
 *
 * @author 易樊
 * @since 2021-09-24
 */
public interface ChannelProductCostService extends IService<ChannelProductCost> {

    /**
     * 保存
     *
     * @param channelProductCost
     * @return
     * @throws Exception
     */
    boolean saveChannelProductCost(ChannelProductCost channelProductCost, HttpServletRequest httpServletRequest) throws Exception;

    /**
     * 修改
     *
     * @param channelProductCost
     * @return
     * @throws Exception
     */
    boolean updateChannelProductCost(ChannelProductCost channelProductCost) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteChannelProductCost(Long id) throws Exception;


    /**
     * 获取分页对象
     *
     * @param channelProductCostPageParam
     * @return
     * @throws Exception
     */
    PageEntity<ChannelProductCost> getChannelProductCostPageList(ChannelProductCostPageParam channelProductCostPageParam) throws Exception;

    /**
     * 获取列表对象
     *
     * @param channelProductCostPageParam
     * @return
     * @throws Exception
     */
    List<ChannelProductCost> getChannelProductCostList(ChannelProductCostPageParam channelProductCostPageParam) throws Exception;

    List<ChannelProductCostVO> getChannelProductCost(String productCode);

    ChannelProductCostVO getTreeByNew(String productCode);

    ChannelProductCostVO getTreeForUpdate(String productCode, String costId);

    ChannelClassNode getTree(String productCode);

    /*Integer noSet(String productCode);*/

    List<ChannelClassNode> noSetList(String productCode);

    ChannelProductCostVO getChannelProductCostExt(ChannelProductCostPageParam channelProductCostPageParam);

    ChannelClassNode getTreeStructure(String productCode);
}
