package com.stnts.bi.datamanagement.module.channel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelClassCooperation;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelClassCooperationMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelClassMapper;
import com.stnts.bi.datamanagement.module.channel.param.ChannelClassCooperationPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelClassCooperationService;
import com.stnts.bi.entity.common.PageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 渠道类型关联CCID 服务实现类
 *
 * @author 易樊
 * @since 2021-09-22
 */
@Slf4j
@Service
public class ChannelClassCooperationServiceImpl extends ServiceImpl<ChannelClassCooperationMapper, ChannelClassCooperation> implements ChannelClassCooperationService {

    @Autowired
    private ChannelClassCooperationMapper channelClassCooperationMapper;
    @Autowired
    private ChannelClassMapper channelClassMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveChannelClassCooperation(ChannelClassCooperation channelClassCooperation) throws Exception {
        return super.save(channelClassCooperation);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateChannelClassCooperation(ChannelClassCooperation channelClassCooperation) throws Exception {
        return super.updateById(channelClassCooperation);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteChannelClassCooperation(Long id) throws Exception {
        return super.removeById(id);
    }


    @Override
    public PageEntity<ChannelClassCooperation> getChannelClassCooperationPageList(ChannelClassCooperationPageParam channelClassCooperationPageParam) throws Exception {
        Page<ChannelClassCooperation> page = new Page<>(channelClassCooperationPageParam.getPageIndex(), channelClassCooperationPageParam.getPageSize());
        LambdaQueryWrapper<ChannelClassCooperation> wrapper = getLambdaQueryWrapper(channelClassCooperationPageParam);
        IPage<ChannelClassCooperation> iPage = channelClassCooperationMapper.selectPage(page, wrapper);
        return new PageEntity<ChannelClassCooperation>(iPage);
    }

    @Override
    public List<ChannelClassCooperation> getChannelClassCooperationList(ChannelClassCooperationPageParam channelClassCooperationPageParam) throws Exception {
        LambdaQueryWrapper<ChannelClassCooperation> wrapper = getLambdaQueryWrapper(channelClassCooperationPageParam);
        List<ChannelClassCooperation> ChannelClassCooperationList = channelClassCooperationMapper.selectList(wrapper);
        return ChannelClassCooperationList;
    }

    private LambdaQueryWrapper<ChannelClassCooperation> getLambdaQueryWrapper(ChannelClassCooperationPageParam channelClassCooperationPageParam) {
        LambdaQueryWrapper<ChannelClassCooperation> wrapper = new LambdaQueryWrapper<>();
        return wrapper;
    }

}
