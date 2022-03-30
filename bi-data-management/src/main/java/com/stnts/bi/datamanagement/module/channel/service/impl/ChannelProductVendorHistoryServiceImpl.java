package com.stnts.bi.datamanagement.module.channel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductVendorHistory;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelProductVendorHistoryMapper;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductVendorHistoryPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductVendorHistoryService;
import com.stnts.bi.entity.common.PageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 产品历史CP厂商记录 服务实现类
 *
 * @author 易樊
 * @since 2021-09-28
 */
@Slf4j
@Service
public class ChannelProductVendorHistoryServiceImpl extends ServiceImpl<ChannelProductVendorHistoryMapper, ChannelProductVendorHistory> implements ChannelProductVendorHistoryService {

    @Autowired
    private ChannelProductVendorHistoryMapper channelProductVendorHistoryMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveChannelProductVendorHistory(ChannelProductVendorHistory channelProductVendorHistory) throws Exception {
        return super.save(channelProductVendorHistory);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateChannelProductVendorHistory(ChannelProductVendorHistory channelProductVendorHistory) throws Exception {
        return super.updateById(channelProductVendorHistory);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteChannelProductVendorHistory(Long id) throws Exception {
        return super.removeById(id);
    }


    @Override
    public PageEntity<ChannelProductVendorHistory> getChannelProductVendorHistoryPageList(ChannelProductVendorHistoryPageParam channelProductVendorHistoryPageParam) throws Exception {
        Page<ChannelProductVendorHistory> page = new Page<>(channelProductVendorHistoryPageParam.getPageIndex(), channelProductVendorHistoryPageParam.getPageSize());
        LambdaQueryWrapper<ChannelProductVendorHistory> wrapper = getLambdaQueryWrapper(channelProductVendorHistoryPageParam);
        IPage<ChannelProductVendorHistory> iPage = channelProductVendorHistoryMapper.selectPage(page, wrapper);

        return new PageEntity<ChannelProductVendorHistory>(iPage);
    }

    @Override
    public List<ChannelProductVendorHistory> getChannelProductVendorHistoryList(ChannelProductVendorHistoryPageParam channelProductVendorHistoryPageParam) throws Exception {
        LambdaQueryWrapper<ChannelProductVendorHistory> wrapper = getLambdaQueryWrapper(channelProductVendorHistoryPageParam);
        List<ChannelProductVendorHistory> ChannelProductVendorHistoryList = channelProductVendorHistoryMapper.selectList(wrapper);

        return ChannelProductVendorHistoryList;
    }

    private LambdaQueryWrapper<ChannelProductVendorHistory> getLambdaQueryWrapper(ChannelProductVendorHistoryPageParam channelProductVendorHistoryPageParam) {
        LambdaQueryWrapper<ChannelProductVendorHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChannelProductVendorHistory::getProductCode, channelProductVendorHistoryPageParam.getProductCode());
        return wrapper;
    }

}
