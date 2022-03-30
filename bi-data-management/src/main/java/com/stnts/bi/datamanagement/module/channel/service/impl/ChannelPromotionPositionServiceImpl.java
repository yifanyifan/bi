package com.stnts.bi.datamanagement.module.channel.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelChild;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotion;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotionPosition;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelChildMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelPromotionMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelPromotionPositionMapper;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionPositionPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionAllService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionPositionService;
import com.stnts.bi.entity.common.PageEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/3/2
 */
@Slf4j
@Service
public class ChannelPromotionPositionServiceImpl extends ServiceImpl<ChannelPromotionPositionMapper, ChannelPromotionPosition> implements ChannelPromotionPositionService {

    @Autowired
    private ChannelPromotionPositionMapper channelPromotionPositionMapper;

    @Autowired
    private ChannelPromotionMapper channelPromotionMapper;

    @Autowired
    private ChannelChildMapper channelChildMapper;

    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;

    @Override
    public boolean saveChannel(ChannelPromotionPosition channel) throws Exception {
        try {
            return super.save(channel);
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new BusinessException("同一渠道下推广位名称唯一");
            } else if (e instanceof DataIntegrityViolationException) {
                throw new BusinessException("推广位名称2-50个字符");
            } else {
                throw e;
            }
        }
    }

    @Override
    public boolean updateChannelPromotionPosition(ChannelPromotionPosition channel) throws Exception {
        try {
            Long channelId = channel.getChannelId();
            List<ChannelPromotionPosition> cppList = channelPromotionPositionMapper.selectList(new QueryWrapper<ChannelPromotionPosition>().lambda()
                    .eq(ChannelPromotionPosition::getChannelId, channelId)
                    .eq(ChannelPromotionPosition::getPpStatus, 1)
                    .ne(ChannelPromotionPosition::getPpId, channel.getPpId())
            );
            List<String> cppNameByDB = cppList.stream().map(ChannelPromotionPosition::getPpName).distinct().collect(Collectors.toList());
            if (cppNameByDB.contains(channel.getPpName())) {
                throw new BusinessException("推广位名称在当前渠道（或子渠道）推广位中重复：" + channel.getPpName());
            }

            super.updateById(channel);

            //存宽表
            channelPromotionAllService.updateChannelPromotionPositionThread(channel);

            return true;
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new BusinessException("同一渠道下推广位名称唯一");
            } else if (e instanceof DataIntegrityViolationException) {
                throw new BusinessException("推广位名称2-50个字符");
            } else {
                throw e;
            }
        }
    }

    @Override
    public boolean deleteChannelPromotionPosition(Long id) throws Exception {

        Integer count = channelPromotionMapper.selectCount(new QueryWrapper<ChannelPromotion>().lambda().eq(ChannelPromotion::getPpId, id));
        if (count > 0) {
            throw new BusinessException("关联PID,无法删除");
        }
        return super.removeById(id);
    }

    @Override
    public PageEntity<ChannelPromotionPosition> getChannelPromotionPositionPageList(ChannelPromotionPositionPageParam channelPageParam) throws Exception {
        Page<ChannelPromotionPosition> page = new Page<>(channelPageParam.getPageIndex(), channelPageParam.getPageSize());
        LambdaQueryWrapper<ChannelPromotionPosition> wrapper = getLambdaQueryWrapper(channelPageParam);
        IPage<ChannelPromotionPosition> iPage = channelPromotionPositionMapper.selectPage(page, wrapper);
        return new PageEntity<>(iPage);
    }

    @Override
    public List<ChannelPromotionPosition> getChannelPromotionPositionList(ChannelPromotionPositionPageParam channelPageParam) throws Exception {
        /*if(channelPageParam.getPpStatus() == null){
            channelPageParam.setPpStatus(1);
        }*/

        List<ChannelPromotionPosition> channelPromotionPositionList = new ArrayList<ChannelPromotionPosition>();

        LambdaQueryWrapper<ChannelPromotionPosition> wrapper = new LambdaQueryWrapper<>();

        List<String> subChannelIdList = new ArrayList<String>();
        if (StringUtils.isBlank(channelPageParam.getSubChannelId())) {
            //未选择子渠道时
            List<ChannelChild> channelChildList = channelChildMapper.selectList(new QueryWrapper<ChannelChild>().lambda().eq(ChannelChild::getChannelId, channelPageParam.getChannelId()));
            subChannelIdList = channelChildList.stream().map(ChannelChild::getSubChannelId).collect(Collectors.toList());

            List<String> finalSubChannelIdList = subChannelIdList;
            channelPromotionPositionList = channelPromotionPositionMapper.selectList(new QueryWrapper<ChannelPromotionPosition>().lambda()
                    .eq(ObjectUtil.isNotNull(channelPageParam.getPpStatus()), ChannelPromotionPosition::getPpStatus, channelPageParam.getPpStatus())
                    .eq(ObjectUtil.isNotNull(channelPageParam.getPpFlag()), ChannelPromotionPosition::getPpFlag, channelPageParam.getPpFlag())
                    .and(i -> i.eq(channelPageParam.getChannelId() != null, ChannelPromotionPosition::getChannelId, channelPageParam.getChannelId())
                            .or().in(finalSubChannelIdList.size() > 0, ChannelPromotionPosition::getSubChannelId, finalSubChannelIdList))
            );
        } else {
            //选择了子渠道时，获取子渠道推广位
            subChannelIdList.add(channelPageParam.getSubChannelId());
            channelPromotionPositionList = channelPromotionPositionMapper.selectList(new QueryWrapper<ChannelPromotionPosition>().lambda()
                    .eq(ObjectUtil.isNotNull(channelPageParam.getPpStatus()), ChannelPromotionPosition::getPpStatus, channelPageParam.getPpStatus())
                    .in(subChannelIdList.size() > 0, ChannelPromotionPosition::getSubChannelId, subChannelIdList)
            );

            if (channelPromotionPositionList == null || channelPromotionPositionList.size() == 0) {
                //没有子渠道推广位时，返回渠道推广位
                ChannelChild channelChild = channelChildMapper.selectOne(new QueryWrapper<ChannelChild>().lambda().eq(ChannelChild::getSubChannelId, channelPageParam.getSubChannelId()));
                Long channelId = channelChild.getChannelId();

                channelPromotionPositionList = channelPromotionPositionMapper.selectList(new QueryWrapper<ChannelPromotionPosition>().lambda()
                        .eq(ChannelPromotionPosition::getChannelId, channelId)
                        .eq(ChannelPromotionPosition::getPpFlag, 1)
                        .eq(ObjectUtil.isNotNull(channelPageParam.getPpStatus()), ChannelPromotionPosition::getPpStatus, channelPageParam.getPpStatus())
                );
            }
        }

        if (StrUtil.isNotEmpty(channelPageParam.getPid())) {
            ChannelPromotionPosition channelPromotionPosition = channelPromotionPositionMapper.selectByPid(channelPageParam.getPid());
            if (ObjectUtil.isNotEmpty(channelPromotionPosition) && channelPromotionPositionList.stream().map(ChannelPromotionPosition::getPpId).noneMatch(p -> p.longValue() == channelPromotionPosition.getPpId())) {
                channelPromotionPositionList.add(channelPromotionPosition);
            }
        }

        for (ChannelPromotionPosition channelPromotionPosition : channelPromotionPositionList) {
            Integer ppStatus = channelPromotionPosition.getPpStatus();
            channelPromotionPosition.setPpStatusStr(1 == ppStatus ? "启用" : "停用");
        }

        return channelPromotionPositionList;
    }

    @Override
    public boolean updateChannelPromotionPositionBatch(List<ChannelPromotionPosition> channel) {
        try {
            //1. 列表中名称是否有重复
            Long channelId = channel.get(0).getChannelId();
            List<Long> ppIdListByParam = channel.stream().map(ChannelPromotionPosition::getPpId).distinct().collect(Collectors.toList());
            List<ChannelPromotionPosition> cppListByDB = channelPromotionPositionMapper.selectList(new QueryWrapper<ChannelPromotionPosition>().lambda()
                    .eq(ChannelPromotionPosition::getChannelId, channelId)
                    .eq(ChannelPromotionPosition::getPpStatus, 1)
                    .notIn(ChannelPromotionPosition::getPpId, ppIdListByParam)
            );
            List<String> cppNameByDB = cppListByDB.stream().map(ChannelPromotionPosition::getPpName).distinct().collect(Collectors.toList());
            List<String> cppNameByParam = channel.stream().map(ChannelPromotionPosition::getPpName).distinct().collect(Collectors.toList());
            cppNameByDB.retainAll(cppNameByParam);
            if (cppNameByDB.size() > 0) {
                throw new BusinessException("推广位名称在当前渠道（或子渠道）推广位中重复：" + cppNameByDB);
            }
            if (channel.size() > cppNameByParam.size()) {
                throw new BusinessException("推广位名称在当前参数中重复");
            }

            return super.updateBatchById(channel);
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new BusinessException("同一渠道下推广位名称唯一");
            } else if (e instanceof DataIntegrityViolationException) {
                throw new BusinessException("推广位名称2-50个字符");
            } else {
                throw e;
            }
        }
    }

    @Override
    public boolean addChannelPromotionPositionBatch(List<ChannelPromotionPosition> channelPromotionPositionList) {
        Long channelId = channelPromotionPositionList.get(0).getChannelId();
        List<ChannelPromotionPosition> cppList = channelPromotionPositionMapper.selectList(new QueryWrapper<ChannelPromotionPosition>().lambda()
                .eq(ChannelPromotionPosition::getChannelId, channelId)
        );
        List<String> cppNameByDB = cppList.stream().map(ChannelPromotionPosition::getPpName).distinct().collect(Collectors.toList());

        List<String> cppNameByParam = channelPromotionPositionList.stream().map(ChannelPromotionPosition::getPpName).distinct().collect(Collectors.toList());
        cppNameByDB.retainAll(cppNameByParam);
        if (cppNameByDB.size() > 0) {
            throw new BusinessException("推广位名称在当前渠道（或子渠道）推广位中重复：" + StringUtil.join(cppNameByDB.toArray(), ","));
        }
        if (channelPromotionPositionList.size() > cppNameByParam.size()) {
            throw new BusinessException("推广位名称在当前参数中重复");
        }

        return this.saveBatch(channelPromotionPositionList);
    }

    @Override
    public ChannelPromotionPosition addChannelPromotionPosition(ChannelPromotionPosition cpp) {
        Long channelId = cpp.getChannelId();
        List<ChannelPromotionPosition> cppList = channelPromotionPositionMapper.selectList(new QueryWrapper<ChannelPromotionPosition>().lambda()
                .eq(ChannelPromotionPosition::getChannelId, channelId)
        );
        List<String> cppNameByDB = cppList.stream().map(ChannelPromotionPosition::getPpName).distinct().collect(Collectors.toList());

        if (cppNameByDB.contains(cpp.getPpName())) {
            throw new BusinessException("推广位名称在当前渠道（或子渠道）推广位中重复：" + cpp.getPpName());
        }

        this.save(cpp);

        return cpp;
    }

    private LambdaQueryWrapper<ChannelPromotionPosition> getLambdaQueryWrapper(ChannelPromotionPositionPageParam channelPageParam) {
        LambdaQueryWrapper<ChannelPromotionPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtil.isNotNull(channelPageParam.getChannelId()), ChannelPromotionPosition::getChannelId, channelPageParam.getChannelId());
        wrapper.eq(ObjectUtil.isNotNull(channelPageParam.getPpStatus()), ChannelPromotionPosition::getPpStatus, channelPageParam.getPpStatus());
        wrapper.eq(ObjectUtil.isNotNull(channelPageParam.getPpFlag()), ChannelPromotionPosition::getPpFlag, channelPageParam.getPpFlag());
        wrapper.like(StringUtils.isNotBlank(channelPageParam.getPpId()), ChannelPromotionPosition::getPpId, channelPageParam.getPpId());
        wrapper.like(StringUtils.isNotBlank(channelPageParam.getPpName()), ChannelPromotionPosition::getPpName, channelPageParam.getPpName());
        wrapper.like(StringUtils.isNotBlank(channelPageParam.getPlugId()), ChannelPromotionPosition::getPlugId, channelPageParam.getPlugId());
        wrapper.like(StringUtils.isNotBlank(channelPageParam.getPlugName()), ChannelPromotionPosition::getPlugName, channelPageParam.getPlugName());
        return wrapper;
    }
}
