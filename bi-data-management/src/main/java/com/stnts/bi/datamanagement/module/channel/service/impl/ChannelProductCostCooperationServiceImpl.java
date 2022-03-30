package com.stnts.bi.datamanagement.module.channel.service.impl;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductCostCooperation;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelProductCostCooperationMapper;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductCostCooperationService;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductCostCooperationPageParam;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.entity.common.PageEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * 产品分成关联CCID 服务实现类
 *
 * @author 易樊
 * @since 2021-09-24
 */
@Slf4j
@Service
public class ChannelProductCostCooperationServiceImpl extends ServiceImpl<ChannelProductCostCooperationMapper, ChannelProductCostCooperation> implements ChannelProductCostCooperationService {

    @Autowired
    private ChannelProductCostCooperationMapper channelProductCostCooperationMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveChannelProductCostCooperation(ChannelProductCostCooperation channelProductCostCooperation) throws Exception {
        return super.save(channelProductCostCooperation);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateChannelProductCostCooperation(ChannelProductCostCooperation channelProductCostCooperation) throws Exception {
        return super.updateById(channelProductCostCooperation);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteChannelProductCostCooperation(Long id) throws Exception {
        return super.removeById(id);
    }


    @Override
    public PageEntity<ChannelProductCostCooperation> getChannelProductCostCooperationPageList(ChannelProductCostCooperationPageParam channelProductCostCooperationPageParam) throws Exception {
        Page<ChannelProductCostCooperation> page = new Page<>(channelProductCostCooperationPageParam.getPageIndex(), channelProductCostCooperationPageParam.getPageSize());
        LambdaQueryWrapper<ChannelProductCostCooperation> wrapper = getLambdaQueryWrapper(channelProductCostCooperationPageParam);
        IPage<ChannelProductCostCooperation> iPage = channelProductCostCooperationMapper.selectPage(page, wrapper);
        return new PageEntity<ChannelProductCostCooperation>(iPage);
    }

    @Override
    public List<ChannelProductCostCooperation> getChannelProductCostCooperationList(ChannelProductCostCooperationPageParam channelProductCostCooperationPageParam) throws Exception {
        LambdaQueryWrapper<ChannelProductCostCooperation> wrapper = getLambdaQueryWrapper(channelProductCostCooperationPageParam);
        List<ChannelProductCostCooperation> ChannelProductCostCooperationList = channelProductCostCooperationMapper.selectList(wrapper);
        return ChannelProductCostCooperationList;
    }

    private LambdaQueryWrapper<ChannelProductCostCooperation> getLambdaQueryWrapper(ChannelProductCostCooperationPageParam channelProductCostCooperationPageParam) {
        LambdaQueryWrapper<ChannelProductCostCooperation> wrapper = new LambdaQueryWrapper<>();
        return wrapper;
    }

}
