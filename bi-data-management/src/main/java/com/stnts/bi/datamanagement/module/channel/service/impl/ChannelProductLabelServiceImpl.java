package com.stnts.bi.datamanagement.module.channel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProduct;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductLabel;
import com.stnts.bi.datamanagement.module.channel.enums.LabelLevelEnum;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelProductLabelMapper;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductLabelPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductLabelService;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.entity.sys.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 产品标签 服务实现类
 *
 * @author 易樊
 * @since 2022-01-26
 */
@Slf4j
@Service
public class ChannelProductLabelServiceImpl extends ServiceImpl<ChannelProductLabelMapper, ChannelProductLabel> implements ChannelProductLabelService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ChannelProductLabelMapper channelProductLabelMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveChannelProductLabel(ChannelProductLabel channelProductLabel, HttpServletRequest request) throws Exception {
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        channelProductLabel.setUserid(Long.valueOf(user.getId()));
        channelProductLabel.setUsername(user.getCnname());

        LabelLevelEnum labelLevelEnum = LabelLevelEnum.getByKey(channelProductLabel.getLabelLevel());
        if (ObjectUtils.isEmpty(labelLevelEnum)) {
            throw new BusinessException("标签层级不存在");
        }
        exists(channelProductLabel);

        return super.save(channelProductLabel);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateChannelProductLabel(ChannelProductLabel channelProductLabelParam) throws Exception {
        ChannelProductLabel channelProductLabelDB = channelProductLabelMapper.selectOne(new LambdaQueryWrapper<ChannelProductLabel>().eq(ChannelProductLabel::getId, channelProductLabelParam.getId()));
        BeanUtils.copyProperties(channelProductLabelParam, channelProductLabelDB, DozerUtil.getNullPropertyNamesAddId(channelProductLabelParam));

        exists(channelProductLabelDB);

        return super.updateById(channelProductLabelDB);
    }

    private void exists(ChannelProductLabel channelProductLabel) {
        if (StringUtils.isBlank(channelProductLabel.getLabelArea()) || StringUtils.isBlank(channelProductLabel.getLabelLevel()) || StringUtils.isBlank(channelProductLabel.getLabelValue())) {
            throw new BusinessException("标签层级、标签域或标签值不可为空");
        }

        List<ChannelProductLabel> channelProductLabelList = channelProductLabelMapper.selectList(new LambdaQueryWrapper<ChannelProductLabel>()
                .eq(ChannelProductLabel::getLabelArea, channelProductLabel.getLabelArea())
                .eq(ChannelProductLabel::getLabelLevel, channelProductLabel.getLabelLevel())
                .eq(ChannelProductLabel::getLabelValue, channelProductLabel.getLabelValue())
                .ne(ObjectUtils.isNotEmpty(channelProductLabel.getId()), ChannelProductLabel::getId, channelProductLabel.getId())
        );
        if (CollectionUtils.isNotEmpty(channelProductLabelList)) {
            throw new BusinessException("产品标签已存在");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteChannelProductLabel(Long id) throws Exception {
        List<ChannelProduct> channelProductList = channelProductLabelMapper.getUseLabelProduct(id);
        if (CollectionUtils.isNotEmpty(channelProductList)) {
            throw new BusinessException("标签被使用，无法删除");
        }

        return super.removeById(id);
    }

    @Override
    public PageEntity<ChannelProductLabel> getChannelProductLabelPageList(ChannelProductLabelPageParam channelProductLabelPageParam) throws Exception {
        Page<ChannelProductLabel> page = new Page<>(channelProductLabelPageParam.getPageIndex(), channelProductLabelPageParam.getPageSize());
        List<ChannelProductLabel> list = channelProductLabelMapper.selectPageParam(page, channelProductLabelPageParam);

        for (ChannelProductLabel channelProductLabel : list) {
            channelProductLabel.setLabelLevelStr(LabelLevelEnum.getByKey(channelProductLabel.getLabelLevel()).getValue());
        }

        return new PageEntity<ChannelProductLabel>(page, list);
    }

    @Override
    public List<ChannelProductLabel> getChannelProductLabelList(ChannelProductLabelPageParam channelProductLabelPageParam) throws Exception {
        if (StringUtils.isNotBlank(channelProductLabelPageParam.getLabelLevel())) {
            LabelLevelEnum labelLevelEnum = LabelLevelEnum.getByKey(channelProductLabelPageParam.getLabelLevel());
            if (ObjectUtils.isEmpty(labelLevelEnum)) {
                throw new BusinessException("标签层级不存在");
            }
        }

        LambdaQueryWrapper<ChannelProductLabel> wrapper = new LambdaQueryWrapper<ChannelProductLabel>();
        wrapper.like(StringUtils.isNotBlank(channelProductLabelPageParam.getLabelArea()), ChannelProductLabel::getLabelArea, channelProductLabelPageParam.getLabelArea());
        wrapper.like(StringUtils.isNotBlank(channelProductLabelPageParam.getLabelLevel()), ChannelProductLabel::getLabelLevel, channelProductLabelPageParam.getLabelLevel());
        wrapper.like(StringUtils.isNotBlank(channelProductLabelPageParam.getLabelValue()), ChannelProductLabel::getLabelValue, channelProductLabelPageParam.getLabelValue());
        List<ChannelProductLabel> ChannelProductLabelList = channelProductLabelMapper.selectList(wrapper);
        return ChannelProductLabelList;
    }

    @Override
    public Map getTreeAll() {
        List<ChannelProductLabel> channelProductLabelList = channelProductLabelMapper.selectList(new LambdaQueryWrapper<ChannelProductLabel>());

        Map<String, Map<String, List<ChannelProductLabel>>> channelProductLabelMap = channelProductLabelList.stream().collect(Collectors.groupingBy(ChannelProductLabel::getLabelLevel,
                Collectors.groupingBy(ChannelProductLabel::getLabelArea, Collectors.toList())
        ));

        return channelProductLabelMap;
    }

}
