package com.stnts.bi.datamanagement.module.channel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelApplication;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProduct;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelApplicationMapper;
import com.stnts.bi.datamanagement.module.channel.param.ChannelApplicationPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelApplicationService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionAllService;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.entity.sys.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用表 服务实现类
 *
 * @author 易樊
 * @since 2022-01-13
 */
@Slf4j
@Service
public class ChannelApplicationServiceImpl extends ServiceImpl<ChannelApplicationMapper, ChannelApplication> implements ChannelApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(ChannelApplicationServiceImpl.class);

    @Autowired
    private ChannelApplicationMapper channelApplicationMapper;
    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;
    @Autowired
    private ChannelProductService channelProductService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveChannelApplication(ChannelApplication channelApplication, HttpServletRequest request) throws Exception {
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        channelApplication.setUserid(Long.valueOf(user.getId()));
        channelApplication.setUsername(user.getCnname());

        check(channelApplication);
        super.save(channelApplication);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateChannelApplication(ChannelApplication channelApplication) throws Exception {
        check(channelApplication);
        super.updateById(channelApplication);

        //改产品表
        List<ChannelProduct> channelProductList = channelProductService.selectListByAppId(String.valueOf(channelApplication.getId()));
        if (CollectionUtil.isNotEmpty(channelProductList)) {
            String appIdsAll = channelProductList.stream().map(ChannelProduct::getApplicationIds).collect(Collectors.joining(","));
            List<String> appIdsList = new ArrayList<String>(Arrays.asList(appIdsAll.split(",")));
            List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectList(new LambdaQueryWrapper<ChannelApplication>().in(ChannelApplication::getId, appIdsList));

            for (ChannelProduct channelProduct : channelProductList) {
                List<String> applicationIdList = new ArrayList<String>(Arrays.asList(channelProduct.getApplicationIds().split(",")));
                List<ChannelApplication> channelApplicationListFiler = channelApplicationList.stream().filter(i -> applicationIdList.contains(String.valueOf(i.getId()))).collect(Collectors.toList());
                channelProduct.setApplicationIds(channelApplicationListFiler.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.joining(",")));
                channelProduct.setApplicationNames(channelApplicationListFiler.stream().map(i -> String.valueOf(i.getApplicationName())).collect(Collectors.joining(",")));
            }
            channelProductService.updateBatchById(channelProductList);
        }

        //存宽表
        channelPromotionAllService.updateChannelApplicationThread(channelApplication);

        return true;
    }

    public void check(ChannelApplication channelApplication) {
        if (StringUtils.isBlank(channelApplication.getApplicationName())) {
            throw new BusinessException("应用名称不可为空");
        }

        List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectList(new LambdaQueryWrapper<ChannelApplication>()
                .eq(ChannelApplication::getApplicationName, channelApplication.getApplicationName())
                .ne(ObjectUtil.isNotEmpty(channelApplication.getId()), ChannelApplication::getId, channelApplication.getId())
        );
        if (CollectionUtil.isNotEmpty(channelApplicationList)) {
            throw new BusinessException("应用名称已存在：" + channelApplication.getApplicationName());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteChannelApplication(Long id) throws Exception {
        List<ChannelProduct> channelProductList = channelApplicationMapper.getUseAppProduct(id);
        if (CollectionUtil.isNotEmpty(channelProductList)) {
            throw new BusinessException("应用有产品关联，无法删除");
        }

        return super.removeById(id);
    }


    @Override
    public PageEntity<ChannelApplication> getChannelApplicationPageList(ChannelApplicationPageParam channelApplicationPageParam) throws Exception {
        Page<ChannelApplication> page = new Page<>(channelApplicationPageParam.getPageIndex(), channelApplicationPageParam.getPageSize());
        List<ChannelApplication> iPage = channelApplicationMapper.selectPageParam(page, channelApplicationPageParam);
        return new PageEntity<ChannelApplication>(page, iPage);
    }

    @Override
    public List<ChannelApplication> getChannelApplicationList(ChannelApplicationPageParam channelApplicationPageParam) throws Exception {
        LambdaQueryWrapper<ChannelApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(channelApplicationPageParam.getApplicationName()), ChannelApplication::getApplicationName, channelApplicationPageParam.getApplicationName())
                .ge(ObjectUtil.isNotEmpty(channelApplicationPageParam.getUpdateTimeStart()), ChannelApplication::getUpdateTime, channelApplicationPageParam.getUpdateTimeStart())
                .le(ObjectUtil.isNotEmpty(channelApplicationPageParam.getUpdateTimeEnd()), ChannelApplication::getUpdateTime, channelApplicationPageParam.getUpdateTimeEnd());
        List<ChannelApplication> ChannelApplicationList = channelApplicationMapper.selectList(wrapper);
        return ChannelApplicationList;
    }
}
