package com.stnts.bi.datamanagement.module.channel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.channel.entity.*;
import com.stnts.bi.datamanagement.module.channel.mapper.*;
import com.stnts.bi.datamanagement.module.channel.service.ChannelBaseIdService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionService;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.datamanagement.util.RandomUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 基础ID标识 服务实现类
 * </p>
 *
 * @author yifan
 * @since 2021-06-10
 */
@Service
public class ChannelBaseIdServiceImpl extends ServiceImpl<ChannelBaseIdMapper, ChannelBaseId> implements ChannelBaseIdService {
    @Autowired
    private ChannelBaseIdMapper channelBaseIdMapper;
    @Autowired
    private ChannelMapper channelMapper;
    @Autowired
    private ChannelChildMapper channelChildMapper;
    @Autowired
    private ChannelCooperationMapper channelCooperationMapper;
    @Autowired
    private ChannelPromotionMapper channelPromotionMapper;
    @Autowired
    private ChannelPromotionService channelPromotionService;
    @Autowired
    private ChannelProductService channelProductService;

    /**
     * 渠道CID ：从1开始自增5位数字  =5位：如 10000
     * 数据库存储当铺已有最大CID
     *
     * @return
     */
    @Override
    public Long getNewChannelID() {
        Long channelId = null;

        ChannelBaseId channelBaseId = channelBaseIdMapper.selectOne(new QueryWrapper<ChannelBaseId>());
        if (channelBaseId == null) {
            channelId = 10000l;
        } else {
            channelId = channelBaseId.getChannelId() + 1;
        }

        //判断是否重复
        Integer count = channelMapper.selectCount(new LambdaQueryWrapper<Channel>().eq(Channel::getChannelId, channelId));
        if (count > 0) {
            channelId = getNewChannelID();
        }

        return channelId;
    }

    @Override
    public void updateNewChannelID(Long channelId) {
        ChannelBaseId channelBaseId = channelBaseIdMapper.selectOne(new QueryWrapper<ChannelBaseId>());
        if (channelBaseId == null) {
            channelBaseId = new ChannelBaseId();
            channelBaseId.setChannelId(channelId);
            channelBaseIdMapper.insert(channelBaseId);
        } else {
            channelBaseId.setChannelId(channelId);
            channelBaseIdMapper.updateById(channelBaseId);
        }
    }

    /**
     * 子渠道ID： 渠道CID+3位随机字母 =8位 ：如10000abF
     *
     * @return
     */
    @Override
    public String getNewSubChannelID(Long cId) {
        String subChannelId = null;

        String strNum = RandomUtil.getRandomString(3);
        subChannelId = cId + strNum;

        //判断是否重复111
        Integer count = channelChildMapper.selectCount(new LambdaQueryWrapper<ChannelChild>().eq(ChannelChild::getSubChannelId, subChannelId));
        if (count > 0) {
            subChannelId = getNewSubChannelID(cId);
        }

        return subChannelId;
    }

    /**
     * CCID规则：前缀CCID +5位渠道CID号 + 计费方式3位简写 + 3位字符串= 15位
     *
     * @return
     */
    @Override
    public String getNewCCID(Long cId, String chargeRule) {
        String ccid = null;

        String strNum = RandomUtil.getRandomString(3);
        ccid = "CCID" + cId + chargeRule + strNum;

        //判断是否重复
        Integer count = channelCooperationMapper.selectCount(new LambdaQueryWrapper<ChannelCooperation>().eq(ChannelCooperation::getCcid, ccid));
        if (count > 0) {
            ccid = getNewCCID(cId, chargeRule);
        }

        return ccid;
    }

    /**
     * PID规则：前缀PID+12位随机数字字母  =  15位
     *
     * @return
     */
    @Override
    public String getNewPID() {
        String pid = null;

        String strNum = RandomUtil.getRandomStringAndNumber(9);
        pid = "PID" + strNum;

        Integer count = channelPromotionMapper.selectCount(new LambdaQueryWrapper<ChannelPromotion>().eq(ChannelPromotion::getPid, pid));
        if (count > 0) {
            pid = getNewPID();
        }

        return pid;
    }

    @Override
    public List<String> getNewPIDs(int num) {
        List<String> pids = new ArrayList<String>();
        for (int i = 0; i < num; i++) {
            String pid = this.getNewPID();
            pids.add(pid);
        }
        return pids;
    }

    @Override
    public void getPidAliasAll(Integer pidNum, String pidAlias, ChannelPromotion channelPromotion) throws Exception {
        // 获取PID集合
        List<String> pidList = this.getNewPIDs(pidNum);
        if (CollectionUtil.isEmpty(pidList)) {
            throw new BusinessException("PID获取失败");
        }
        //获取根据计费别名获取数据库中最新编码
        Integer lastNum = channelPromotionService.getLastNum(pidAlias); //by yifan 20211104

        //查对应的ProductId
        ChannelProduct channelProduct = channelProductService.getOne(new QueryWrapper<ChannelProduct>().lambda().eq(ChannelProduct::getProductCode, channelPromotion.getProductCode()));

        List<ChannelPromotion> cps = new ArrayList<>(pidNum);
        for (int i = 0; i < pidNum; i++) {
            ChannelPromotion cp = DozerUtil.toBean(channelPromotion, ChannelPromotion.class);

            String pidName = lastNum > 0 ? pidAlias.concat("_").concat(String.valueOf(lastNum)) : pidAlias;
            lastNum++;

            cp.setPidAlias(pidName);
            cp.setPid(pidList.get(i));
            cp.setCheckStartDate(channelPromotion.getCheckStartDate());
            cp.setCheckEndDate(channelPromotion.getCheckEndDate() == null ? DateUtils.parseDate("2099-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss") : channelPromotion.getCheckEndDate());

            cp.setProductId(channelProduct.getProductId());
            cp.setProductName(channelPromotion.getProductName());
            cps.add(cp);
        }
        channelPromotionService.saveBatch(cps);
    }
}
