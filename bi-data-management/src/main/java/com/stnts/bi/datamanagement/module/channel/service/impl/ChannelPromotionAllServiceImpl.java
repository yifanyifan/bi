package com.stnts.bi.datamanagement.module.channel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.channel.entity.*;
import com.stnts.bi.datamanagement.module.channel.mapper.*;
import com.stnts.bi.datamanagement.module.channel.param.PostPidParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionAllService;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiService;
import com.stnts.bi.datamanagement.module.cooperation.vo.UserVO;
import com.stnts.bi.datamanagement.module.cooperationmain.entity.CooperationMain;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.datamanagement.util.PidUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 渠道推广宽表 服务实现类
 * </p>
 *
 * @author yifan
 * @since 2021-07-21
 */
@Service
public class ChannelPromotionAllServiceImpl extends ServiceImpl<ChannelPromotionAllMapper, ChannelPromotionAll> implements ChannelPromotionAllService {
    private static final Logger logger = LoggerFactory.getLogger(ChannelPromotionAllServiceImpl.class);

    @Autowired
    private ChannelPromotionAllMapper channelPromotionAllMapper;
    @Autowired
    private ChannelMapper channelMapper;
    @Autowired
    private ChannelCooperationMapper channelCooperationMapper;
    @Autowired
    private ChannelProductMapper channelProductMapper;
    @Autowired
    private ChannelApplicationMapper channelApplicationMapper;
    @Autowired
    private ChannelMediumMapper channelMediumMapper;
    @Autowired
    private ChannelChildMapper channelChildMapper;
    @Autowired
    private ChannelPromotionPositionMapper channelPromotionPositionMapper;
    @Autowired
    private ChannelPromotionMapper channelPromotionMapper;
    @Autowired
    private CooperationBiService cooperationBiService;

    //private ExecutorService pool = Executors.newFixedThreadPool(15);
    private ScheduledExecutorService pool = Executors.newScheduledThreadPool(15);

    @Override
    public void addBatchThread(List<ChannelPromotion> channelPromotionListParam, String host, List<PostPidParam> postPidParamAllList) {
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                if (StringUtils.isNotBlank(host) && CollectionUtil.isNotEmpty(postPidParamAllList)) {
                    try {
                        logger.info("=====================>【异步开始】向友拓推送计费名称，host:" + host + ", postPidParamAllList:" + JSON.toJSONString(postPidParamAllList));
                        Integer repose = PidUtil.initPostPid(host, postPidParamAllList);
                        if (repose != 0) {
                            logger.info("=====================>【异步异常】向友拓推送计费名称失败");
                        }
                        logger.info("=====================>【异步结束】向友拓推送计费名称");
                    } catch (Exception e) {
                        logger.info("=====================>【异步异常】向友拓推送计费名称异常" + e.getMessage(), e);
                    }
                }

                try {
                    logger.info("=====================>【异步开始】PID宽表" + JSON.toJSONString(channelPromotionListParam));
                    addBatch(channelPromotionListParam);
                    logger.info("=====================>【异步结束】PID宽表");
                } catch (Exception e) {
                    logger.info("=====================>【异步异常】PID宽表" + e.getMessage(), e);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void addBatch(List<ChannelPromotion> channelPromotionListParam) {
        logger.info("==============================>channelPromotionListParam，Start:" + JSON.toJSONString(channelPromotionListParam));
        List<String> pidListParam = channelPromotionListParam.stream().map(ChannelPromotion::getPid).distinct().collect(Collectors.toList());
        //查出已在宽表中的PID
        List<ChannelPromotionAll> channelPromotionAllListDB = channelPromotionAllMapper.selectList(new LambdaQueryWrapper<ChannelPromotionAll>()
                .in(ChannelPromotionAll::getPid, pidListParam)
                .eq(ChannelPromotionAll::getFlag, "1")
        );
        Map<String, ChannelPromotionAll> channelPromotionAllMap = channelPromotionAllListDB.stream().collect(Collectors.toMap(ChannelPromotionAll::getPid, s -> s));
        //CCID
        List<String> ccidList = channelPromotionListParam.stream().map(ChannelPromotion::getCcid).distinct().collect(Collectors.toList());
        List<ChannelCooperation> channelCooperationList = channelCooperationMapper.selectList(new QueryWrapper<ChannelCooperation>().lambda().in(ChannelCooperation::getCcid, ccidList));
        Map<String, ChannelCooperation> channelCooperationMap = channelCooperationList.stream().collect(Collectors.toMap(ChannelCooperation::getCcid, s -> s));
        //内结CCID
        List<String> ccidSettlementList = channelPromotionListParam.stream().map(ChannelPromotion::getCcidSettlement).distinct().collect(Collectors.toList());
        List<ChannelCooperation> channelCooperationSettlementList = channelCooperationMapper.selectList(new QueryWrapper<ChannelCooperation>().lambda().in(ChannelCooperation::getCcid, ccidSettlementList));
        Map<String, ChannelCooperation> channelCooperationSettlementMap = channelCooperationSettlementList.stream().collect(Collectors.toMap(ChannelCooperation::getCcid, s -> s));

        List<String> productCodeList = channelPromotionListParam.stream().map(ChannelPromotion::getProductCode).distinct().collect(Collectors.toList());
        List<ChannelProduct> channelProductList = channelProductMapper.selectList(new QueryWrapper<ChannelProduct>().lambda().in(ChannelProduct::getProductCode, productCodeList));
        Map<String, ChannelProduct> channelProductMap = channelProductList.stream().collect(Collectors.toMap(ChannelProduct::getProductCode, s -> s));

        List<Long> applicationIdList = channelPromotionListParam.stream().filter(i -> ObjectUtil.isNotEmpty(i.getApplicationId())).map(ChannelPromotion::getApplicationId).distinct().collect(Collectors.toList());
        Map<Long, ChannelApplication> channelApplicationMap = new HashMap<Long, ChannelApplication>();
        if (applicationIdList.size() > 0) {
            List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectList(new QueryWrapper<ChannelApplication>().lambda().in(ChannelApplication::getId, applicationIdList));
            channelApplicationMap = channelApplicationList.stream().collect(Collectors.toMap(ChannelApplication::getId, s -> s));
        }

        List<String> mediumIdList = channelPromotionListParam.stream().filter(i -> StringUtils.isNotBlank(i.getMediumId())).map(i -> i.getMediumId().split(",")).flatMap(Arrays::stream).distinct().collect(Collectors.toList());
        Map<Long, ChannelMedium> channelMediumMap = new HashMap<Long, ChannelMedium>();
        if (mediumIdList.size() > 0) {
            List<ChannelMedium> channelMediumList = channelMediumMapper.selectList(new QueryWrapper<ChannelMedium>().lambda().in(ChannelMedium::getId, mediumIdList));
            channelMediumMap = channelMediumList.stream().collect(Collectors.toMap(ChannelMedium::getId, s -> s));
        }

        List<String> subChannelIdList = channelPromotionListParam.stream().filter(i -> StringUtils.isNotBlank(i.getSubChannelId())).map(ChannelPromotion::getSubChannelId).distinct().collect(Collectors.toList());
        List<ChannelChild> channelChildList = channelChildMapper.selectList(new QueryWrapper<ChannelChild>().lambda().in(ChannelChild::getSubChannelId, subChannelIdList));
        Map<String, ChannelChild> channelChildMap = channelChildList.stream().collect(Collectors.toMap(ChannelChild::getSubChannelId, s -> s));

        List<Long> channelIdList = channelChildList.stream().filter(i -> ObjectUtil.isNotEmpty(i.getChannelId())).map(ChannelChild::getChannelId).distinct().collect(Collectors.toList());
        List<Channel> channelList = channelMapper.selectList(new QueryWrapper<Channel>().lambda().in(Channel::getChannelId, channelIdList));
        Map<Long, Channel> channelMap = channelList.stream().collect(Collectors.toMap(Channel::getChannelId, s -> s));

        List<Long> ppIdList = channelPromotionListParam.stream().filter(i -> ObjectUtil.isNotEmpty(i.getPpId())).map(ChannelPromotion::getPpId).collect(Collectors.toList());
        Map<Long, ChannelPromotionPosition> cppMap = new HashMap<Long, ChannelPromotionPosition>();
        if (CollectionUtil.isNotEmpty(ppIdList)) {
            List<ChannelPromotionPosition> channelPromotionPositionList = channelPromotionPositionMapper.selectList(new QueryWrapper<ChannelPromotionPosition>().lambda().in(ChannelPromotionPosition::getPpId, ppIdList));
            cppMap = channelPromotionPositionList.stream().collect(Collectors.toMap(ChannelPromotionPosition::getPpId, s -> s));
        }

        List<UserVO> result = cooperationBiService.queryUserTree();
        Map<String, Long> resultMap = result.stream().collect(Collectors.toMap(UserVO::getCardNumber, s -> Long.valueOf(s.getId())));

        List<ChannelPromotionAll> channelPromotionAllList = new ArrayList<ChannelPromotionAll>();
        for (ChannelPromotion channelPromotionParam : channelPromotionListParam) {
            logger.info("==============================>channelPromotionListParam，XH，Start:" + JSON.toJSONString(channelPromotionParam));

            // 兼容编辑
            ChannelPromotionAll channelPromotionAll = new ChannelPromotionAll();
            if (StringUtils.isNotBlank(channelPromotionParam.getPid()) && channelPromotionAllMap.containsKey(channelPromotionParam.getPid())) {
                channelPromotionAll = channelPromotionAllMap.get(channelPromotionParam.getPid());
            }

            ChannelCooperation channelCooperation = channelCooperationMap.get(channelPromotionParam.getCcid());
            ChannelProduct channelProduct = channelProductMap.get(channelPromotionParam.getProductCode());
            ChannelChild channelChild = channelChildMap.get(channelPromotionParam.getSubChannelId());
            Channel channel = channelMap.get(channelCooperation.getChannelId());

            channelPromotionAll.setCcidSettlement(channelPromotionParam.getCcidSettlement());
            if (StringUtils.isNotBlank(channelPromotionParam.getCcidSettlement())) {
                ChannelCooperation channelCooperationSettlement = channelCooperationSettlementMap.get(channelPromotionParam.getCcidSettlement());
                channelPromotionAll.setChannelIdSettlement(channelCooperationSettlement.getChannelId());
                channelPromotionAll.setChannelNameSettlement(channelCooperationSettlement.getChannelName());
            } else {
                channelPromotionAll.setChannelIdSettlement(null);
                channelPromotionAll.setChannelNameSettlement(null);
            }

            List<String> isNull = new ArrayList<String>(Arrays.asList(DozerUtil.getNullPropertyNamesAddId(channelCooperation)));
            isNull.removeAll(Arrays.asList("channelShare", "channelShareStep", "price"));
            BeanUtils.copyProperties(channelCooperation, channelPromotionAll, isNull.toArray(new String[isNull.size()]));
            channelPromotionAll.setDepartmentCodeAttr(channel.getDepartmentCode());
            channelPromotionAll.setDepartmentNameAttr(channel.getDepartmentName());

            channelPromotionAll.setCompanyId(String.valueOf(channelCooperation.getAgentId()));
            channelPromotionAll.setCompanyName(String.valueOf(channelCooperation.getAgentName()));
            if (ObjectUtil.isNotEmpty(channelProduct.getBusinessDictId()) && StringUtils.isNotBlank(channelProduct.getFirstLevelBusiness()) && StringUtils.isNotBlank(channelProduct.getSecondLevelBusiness()) && StringUtils.isNotBlank(channelProduct.getThirdLevelBusiness())) {
                channelPromotionAll.setBusinessDictId(channelProduct.getBusinessDictId());
                channelPromotionAll.setFirstLevelBusiness(channelProduct.getFirstLevelBusiness());
                channelPromotionAll.setSecondLevelBusiness(channelProduct.getSecondLevelBusiness());
                channelPromotionAll.setThirdLevelBusiness(channelProduct.getThirdLevelBusiness());
            } else {
                channelPromotionAll.setBusinessDictId(channelCooperation.getBusinessDictId());
            }
            channelPromotionAll.setProductCode(channelProduct.getProductCode());
            channelPromotionAll.setProductName(channelProduct.getProductName());
            channelPromotionAll.setCooperationMainId(channelProduct.getCooperationMainId());
            channelPromotionAll.setCooperationMainName(channelProduct.getCooperationMainName());
            channelPromotionAll.setVendorId(channelProduct.getVendorId());
            channelPromotionAll.setVendorName(channelProduct.getVendorName());
            channelPromotionAll.setVendorCheckStartDate(channelProduct.getVendorCheckStartDate());
            channelPromotionAll.setVendorCheckEndDate(channelProduct.getVendorCheckEndDate());
            channelPromotionAll.setSaleDepartmentCode(channelProduct.getSaleDepartmentCode());
            channelPromotionAll.setSaleDepartmentName(channelProduct.getSaleDepartmentName());
            channelPromotionAll.setProductFlag(channelProduct.getProductFlag());
            channelPromotionAll.setProductScreen(channelProduct.getProductScreen());
            channelPromotionAll.setProductClass(channelProduct.getProductClass());
            channelPromotionAll.setProductTheme(channelProduct.getProductTheme());

            if (ObjectUtil.isNotEmpty(channelPromotionParam.getApplicationId())) {
                ChannelApplication channelApplication = channelApplicationMap.get(channelPromotionParam.getApplicationId());
                channelPromotionAll.setApplicationId(channelApplication.getId());
                channelPromotionAll.setApplicationName(channelApplication.getApplicationName());
            } else {
                channelPromotionAll.setApplicationId(null);
                channelPromotionAll.setApplicationName(null);
            }
            if (StringUtils.isNotBlank(channelPromotionParam.getMediumId())) {
                String mediumIds = channelPromotionParam.getMediumId();
                String[] mediumIdSubList = mediumIds.split(",");

                StringBuilder mNameStr = new StringBuilder();
                for (String mId : mediumIdSubList) {
                    ChannelMedium channelMedium = channelMediumMap.get(Long.valueOf(mId));
                    mNameStr.append(channelMedium.getName()).append(",");
                }
                channelPromotionAll.setMediumId(mediumIds);
                channelPromotionAll.setMediumName(mNameStr.substring(0, mNameStr.length() - 1));
            } else {
                channelPromotionAll.setMediumId(null);
                channelPromotionAll.setMediumName(null);
            }
            channelPromotionAll.setSecretType(String.valueOf(channel.getSecretType()));
            channelPromotionAll.setChannelType(channel.getChannelType());
            channelPromotionAll.setSettlementType(channel.getSettlementType());

            channelPromotionAll.setSubChannelId(channelChild.getSubChannelId());
            channelPromotionAll.setSubChannelName(channelChild.getSubChannelName());
            if (channelPromotionParam.getPpId() != null) {
                ChannelPromotionPosition channelPromotionPosition = cppMap.get(channelPromotionParam.getPpId());
                channelPromotionAll.setPpId(channelPromotionPosition.getPpId());
                channelPromotionAll.setPpName(channelPromotionPosition.getPpName());
                channelPromotionAll.setPpFlag(String.valueOf(channelPromotionPosition.getPpFlag()));
                channelPromotionAll.setPlugId(channelPromotionPosition.getPlugId());
                channelPromotionAll.setPlugName(channelPromotionPosition.getPlugName());
            } else {
                channelPromotionAll.setPpId(null);
                channelPromotionAll.setPpName(null);
                channelPromotionAll.setPpFlag(null);
                channelPromotionAll.setPlugId(null);
                channelPromotionAll.setPlugName(null);
            }

            BeanUtils.copyProperties(channelPromotionParam, channelPromotionAll, DozerUtil.getNullPropertyNamesAddId(channelPromotionParam));
            logger.info("============================>channelPromotionParam" + JSON.toJSONString(channelPromotionParam));
            logger.info("============================>channelPromotionAll" + JSON.toJSONString(channelPromotionAll));

            //处理用户ID（用户工号转用户ID）
            if (resultMap.containsKey(String.valueOf(channelPromotionAll.getUserid()))) {
                channelPromotionAll.setUserid(resultMap.get(String.valueOf(channelPromotionAll.getUserid())));
            }

            //1：最新数据(只有一条)，2：旧数据(迁移产生)
            channelPromotionAll.setFlag("1");

            channelPromotionAllList.add(channelPromotionAll);
        }
        this.saveOrUpdateBatch(channelPromotionAllList);
    }

    /**
     * 历史PID编辑时间
     */
    @Override
    public void updateHistroySub(ChannelPromotionHistory channelPromotionHistoryOld, ChannelPromotionHistory channelPromotionHistory) {
        ChannelPromotionAll channelPromotionAll = channelPromotionAllMapper.selectOneByHistory(channelPromotionHistoryOld);
        if (ObjectUtil.isNotEmpty(channelPromotionAll)) {
            channelPromotionAll.setCheckStartDate(channelPromotionHistory.getCheckStartDate());
            channelPromotionAll.setCheckEndDate(channelPromotionHistory.getCheckEndDate());
            channelPromotionAllMapper.updateById(channelPromotionAll);
        }
    }

    /**
     * 迁移 产品 CP厂商
     *
     * @param channelProduct
     */
    @Override
    public void moveCPCompany(ChannelProduct channelProduct) {
        channelPromotionAllMapper.moveCPCompany(channelProduct);
    }

    @Override
    public void updateMainThread(CooperationMain cooperationMain) {
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("=====================>【异步开始】修改公司主体" + JSON.toJSONString(cooperationMain));
                    updateMain(cooperationMain);
                    logger.info("=====================>【异步结束】修改公司主体");
                } catch (Exception e) {
                    logger.info("=====================>【异步异常】修改公司主体" + e.getMessage(), e);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateMain(CooperationMain cooperationMain) {
        Long mainId = cooperationMain.getId();
        List<ChannelPromotionAll> channelPromotionAllList = channelPromotionAllMapper.selectList(new LambdaQueryWrapper<ChannelPromotionAll>().eq(ChannelPromotionAll::getCooperationMainId, mainId));
        for (ChannelPromotionAll channelPromotionAll : channelPromotionAllList) {
            channelPromotionAll.setCooperationMainName(cooperationMain.getCooperationMainName());
        }
        this.updateBatchById(channelPromotionAllList);
    }

    @Override
    public void updateNeijieThread(List<String> pidList, String ccidSettlement) {
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("=====================>【异步开始】修改内结（批量）" + JSON.toJSONString(pidList) + ",," + ccidSettlement);
                    updateNeijie(pidList, ccidSettlement);
                    logger.info("=====================>【异步结束】修改内结（批量）");
                } catch (Exception e) {
                    logger.info("=====================>【异步异常】修改内结（批量）" + e.getMessage(), e);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateNeijie(List<String> pidList, String ccidSettlement) {
        List<ChannelPromotionAll> channelPromotionAllList = channelPromotionAllMapper.selectList(new LambdaQueryWrapper<ChannelPromotionAll>()
                .in(ChannelPromotionAll::getPid, pidList)
                .eq(ChannelPromotionAll::getFlag, "1")
        );
        ChannelCooperation channelCooperation = channelCooperationMapper.selectOne(new LambdaQueryWrapper<ChannelCooperation>().eq(ChannelCooperation::getCcid, ccidSettlement));
        if (ObjectUtil.isNotEmpty(channelCooperation)) {
            for (ChannelPromotionAll channelPromotionAll : channelPromotionAllList) {
                channelPromotionAll.setChannelIdSettlement(channelCooperation.getChannelId());
                channelPromotionAll.setChannelNameSettlement(channelCooperation.getChannelName());
                channelPromotionAll.setCcidSettlement(channelCooperation.getCcid());
            }
        }
        this.updateBatchById(channelPromotionAllList);
    }

    @Override
    public void migrationThread(List<ChannelPromotion> channelPromotionListParam) {
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("=====================>【异步开始】迁移 PID" + JSON.toJSONString(channelPromotionListParam));
                    migration(channelPromotionListParam);
                    logger.info("=====================>【异步结束】迁移 PID");
                } catch (Exception e) {
                    logger.info("=====================>【异步异常】迁移 PID" + e.getMessage(), e);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void migration(List<ChannelPromotion> channelPromotionListParam) {
        List<String> pidList = channelPromotionListParam.stream().map(ChannelPromotion::getPid).collect(Collectors.toList());
        if (pidList.size() > 0) {
            //channelPromotionAllMapper.updateByFlag(pidList, "2");

            for (ChannelPromotion channelPromotion : channelPromotionListParam) {
                channelPromotionAllMapper.updateOldPid(channelPromotion);
            }
        }

        this.addBatch(channelPromotionListParam);
    }

    @Override
    public void updateCCIDThread(ChannelCooperation channelCooperation) {
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("=====================>【异步开始】编辑CCID" + JSON.toJSONString(channelCooperation));
                    updateCCID(channelCooperation);
                    logger.info("=====================>【异步结束】编辑CCID");
                } catch (Exception e) {
                    logger.info("=====================>【异步异常】编辑CCID" + e.getMessage(), e);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateCCID(ChannelCooperation channelCooperation) {
        if (StringUtils.isBlank(channelCooperation.getCcid()) && ObjectUtil.isNotNull(channelCooperation.getId())) {
            ChannelCooperation channelCooperationDB = channelCooperationMapper.selectById(channelCooperation.getId());
            channelCooperation.setCcid(channelCooperationDB.getCcid());
        }

        List<ChannelPromotionAll> channelPromotionAllList = channelPromotionAllMapper.selectList(new QueryWrapper<ChannelPromotionAll>().lambda()
                .eq(StringUtils.isNotBlank(channelCooperation.getCcid()), ChannelPromotionAll::getCcid, channelCooperation.getCcid())
                .eq(ChannelPromotionAll::getFlag, "1")
        );
        if (CollectionUtil.isEmpty(channelPromotionAllList)) {
            return;
        }
        List<String> pidList = channelPromotionAllList.stream().map(ChannelPromotionAll::getPid).collect(Collectors.toList());
        List<ChannelPromotion> channelPromotionList = channelPromotionMapper.selectList(new LambdaQueryWrapper<ChannelPromotion>().in(ChannelPromotion::getPid, pidList));
        Map<String, ChannelPromotion> channelPromotionMap = channelPromotionList.stream().collect(Collectors.toMap(i -> i.getPid(), s -> s));

        Set<String> subChannelIdSet = channelPromotionList.stream().map(ChannelPromotion::getSubChannelId).collect(Collectors.toSet());
        List<ChannelChild> channelChildList = channelChildMapper.selectList(new LambdaQueryWrapper<ChannelChild>().in(ChannelChild::getSubChannelId, subChannelIdSet));
        Map<String, String> subChannelMap = channelChildList.stream().collect(Collectors.toMap(i -> i.getSubChannelId(), s -> s.getSubChannelName()));

        Set<Long> ppIdSet = channelPromotionList.stream().filter(i -> ObjectUtil.isNotEmpty(i.getPpId())).map(ChannelPromotion::getPpId).collect(Collectors.toSet());
        Map<String, String> ppMap = new HashMap<String, String>();
        if (CollectionUtil.isNotEmpty(ppIdSet)) {
            List<ChannelPromotionPosition> ppList = channelPromotionPositionMapper.selectList(new LambdaQueryWrapper<ChannelPromotionPosition>().in(ChannelPromotionPosition::getPpId, ppIdSet));
            ppMap = ppList.stream().collect(Collectors.toMap(i -> String.valueOf(i.getPpId()), s -> s.getPpName()));
        }

        Set<String> mediumIdSet = channelPromotionList.stream().filter(i -> StringUtils.isNotBlank(i.getMediumId())).map(ChannelPromotion::getMediumId).collect(Collectors.toSet());
        Map<String, String> channelMediumMap = new HashMap<String, String>();
        if (CollectionUtil.isNotEmpty(mediumIdSet)) {
            List<ChannelMedium> channelMediumList = channelMediumMapper.selectList(new LambdaQueryWrapper<ChannelMedium>().in(ChannelMedium::getId, mediumIdSet));
            channelMediumMap = channelMediumList.stream().collect(Collectors.toMap(i -> String.valueOf(i.getId()), s -> s.getName()));
        }

        Channel channel = channelMapper.selectOne(new LambdaQueryWrapper<Channel>().eq(Channel::getChannelId, channelCooperation.getChannelId()));


        List<ChannelPromotionAll> channelPromotionAllTemp = new ArrayList<ChannelPromotionAll>();
        for (ChannelPromotionAll channelPromotionAll : channelPromotionAllList) {
            String[] ignoreProperties = {"id", "ccid", "username", "userid", "createTime", "updateTime"};
            BeanUtils.copyProperties(channelCooperation, channelPromotionAll, ignoreProperties);
            channelPromotionAll.setCompanyId(String.valueOf(channelCooperation.getAgentId()));
            channelPromotionAll.setCompanyName(channelCooperation.getAgentName());

            ChannelPromotion channelPromotion = channelPromotionMap.get(channelPromotionAll.getPid());
            if (!channelPromotion.getSubChannelId().equals(channelPromotionAll.getSubChannelId()) && subChannelMap.containsKey(channelPromotion.getSubChannelId())) {
                channelPromotionAll.setSubChannelId(channelPromotion.getSubChannelId());
                channelPromotionAll.setSubChannelName(subChannelMap.get(channelPromotion.getSubChannelId()));
            }
            if (ObjectUtil.isNotEmpty(channelPromotion.getPpId()) && !String.valueOf(channelPromotion.getPpId()).equals(String.valueOf(channelPromotionAll.getPpId())) && ppMap.containsKey(String.valueOf(channelPromotion.getPpId()))) {
                channelPromotionAll.setPpId(channelPromotion.getPpId());
                channelPromotionAll.setPpName(ppMap.get(String.valueOf(channelPromotion.getPpId())));
            }
            if (StringUtils.isNotBlank(channelPromotion.getMediumId()) && !channelPromotion.getMediumId().equals(channelPromotionAll.getMediumId()) && channelMediumMap.containsKey(channelPromotion.getMediumId())) {
                channelPromotionAll.setMediumId(channelPromotion.getMediumId());
                channelPromotionAll.setMediumName(channelMediumMap.get(channelPromotion.getMediumId()));
            }
            if (!channel.getDepartmentCode().equals(channelPromotionAll.getDepartmentCodeAttr()) || !channel.getDepartmentName().equals(channelPromotionAll.getDepartmentNameAttr())) {
                channelPromotionAll.setDepartmentCodeAttr(channel.getDepartmentCode());
                channelPromotionAll.setDepartmentNameAttr(channel.getDepartmentName());
            }

            channelPromotionAllTemp.add(channelPromotionAll);
        }

        this.saveOrUpdateBatch(channelPromotionAllTemp);

        //内结渠道
        channelPromotionAllMapper.updateChannelSettlementByCCIDSettlement(channelCooperation);
    }

    @Override
    public void updateChannelThread(Channel channel) {
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("=====================>【异步开始】编辑渠道" + JSON.toJSONString(channel));
                    updateChannel(channel);
                    logger.info("=====================>【异步结束】编辑渠道");
                } catch (Exception e) {
                    logger.info("=====================>【异步异常】编辑渠道" + e.getMessage(), e);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateChannel(Channel channel) {
        List<ChannelPromotionAll> channelPromotionAllList = channelPromotionAllMapper.selectList(new QueryWrapper<ChannelPromotionAll>().lambda()
                .eq(ChannelPromotionAll::getChannelId, channel.getChannelId())
                .eq(ChannelPromotionAll::getFlag, "1")
        );

        if (channelPromotionAllList != null && channelPromotionAllList.size() > 0) {
            List<ChannelPromotionAll> channelPromotionAllTemp = new ArrayList<ChannelPromotionAll>();
            for (ChannelPromotionAll channelPromotionAll : channelPromotionAllList) {
                channelPromotionAll.setCompanyId(String.valueOf(channel.getCompanyId()));
                channelPromotionAll.setCompanyName(channel.getCompanyName());
                channelPromotionAll.setChannelId(channel.getChannelId());
                channelPromotionAll.setChannelName(channel.getChannelName());
                channelPromotionAll.setDepartmentCodeAttr(channel.getDepartmentCode());
                channelPromotionAll.setDepartmentNameAttr(channel.getDepartmentName());
                channelPromotionAll.setSecretType(String.valueOf(channel.getSecretType()));
                channelPromotionAll.setChannelType(channel.getChannelType());
                channelPromotionAll.setSettlementType(channel.getSettlementType());

                channelPromotionAllTemp.add(channelPromotionAll);
            }

            this.saveOrUpdateBatch(channelPromotionAllTemp);
        }

        //内结渠道
        channelPromotionAllMapper.updateChannelNameSettlement(channel);
    }

    @Override
    public void updateChannelChildThread(ChannelChild channelChild) {
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("=====================>【异步开始】编辑子渠道" + JSON.toJSONString(channelChild));
                    updateChannelChild(channelChild);
                    logger.info("=====================>【异步结束】编辑子渠道");
                } catch (Exception e) {
                    logger.info("=====================>【异步异常】编辑子渠道" + e.getMessage(), e);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateChannelChild(ChannelChild channelChild) {
        List<ChannelPromotionAll> channelPromotionAllList = channelPromotionAllMapper.selectList(new QueryWrapper<ChannelPromotionAll>().lambda()
                .eq(ChannelPromotionAll::getSubChannelId, channelChild.getSubChannelId())
                .eq(ChannelPromotionAll::getFlag, "1")
        );

        //子渠道推广位
        List<ChannelPromotionPosition> cppList = channelChild.getChannelPromotionPositionList();
        Map<Long, ChannelPromotionPosition> cppMap = cppList.stream().collect(Collectors.toMap(ChannelPromotionPosition::getPpId, s -> s));

        List<ChannelPromotionAll> channelPromotionAllTemp = new ArrayList<ChannelPromotionAll>();
        for (ChannelPromotionAll channelPromotionAll : channelPromotionAllList) {
            channelPromotionAll.setSubChannelName(channelChild.getSubChannelName());

            if (cppMap.containsKey(channelPromotionAll.getPpId())) {
                ChannelPromotionPosition cppDB = cppMap.get(channelPromotionAll.getPpId());

                channelPromotionAll.setPpName(cppDB.getPpName());
                channelPromotionAll.setPlugId(cppDB.getPlugId());
                channelPromotionAll.setPlugName(cppDB.getPlugName());
            }

            channelPromotionAllTemp.add(channelPromotionAll);
        }

        this.saveOrUpdateBatch(channelPromotionAllTemp);
    }

    @Override
    public void updateChannelPromotionPositionThread(ChannelPromotionPosition cpp) {
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("=====================>【异步开始】编辑推广位" + JSON.toJSONString(cpp));
                    updateChannelPromotionPosition(cpp);
                    logger.info("=====================>【异步结束】编辑推广位");
                } catch (Exception e) {
                    logger.info("=====================>【异步异常】编辑推广位" + e.getMessage(), e);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateChannelPromotionPosition(ChannelPromotionPosition cpp) {
        List<ChannelPromotionAll> channelPromotionAllList = channelPromotionAllMapper.selectList(new QueryWrapper<ChannelPromotionAll>().lambda()
                .eq(ChannelPromotionAll::getPpId, cpp.getPpId())
                .eq(ChannelPromotionAll::getFlag, "1")
        );

        List<ChannelPromotionAll> channelPromotionAllTemp = new ArrayList<ChannelPromotionAll>();
        for (ChannelPromotionAll channelPromotionAll : channelPromotionAllList) {
            channelPromotionAll.setPpName(cpp.getPpName());
            channelPromotionAll.setPlugId(cpp.getPlugId());
            channelPromotionAll.setPlugName(cpp.getPlugName());

            channelPromotionAllTemp.add(channelPromotionAll);
        }

        this.saveOrUpdateBatch(channelPromotionAllTemp);
    }

    @Override
    public void updateChannelMediumThread(ChannelMedium channelMedium) {
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("=====================>【异步开始】编辑媒介" + JSON.toJSONString(channelMedium));
                    updateChannelMedium(channelMedium);
                    logger.info("=====================>【异步结束】编辑媒介");
                } catch (Exception e) {
                    logger.info("=====================>【异步异常】编辑媒介" + e.getMessage(), e);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateChannelMedium(ChannelMedium channelMedium) {
        List<ChannelPromotionAll> channelPromotionAllList = channelPromotionAllMapper.selectList(new QueryWrapper<ChannelPromotionAll>().lambda()
                .eq(ChannelPromotionAll::getMediumId, channelMedium.getId())
                .eq(ChannelPromotionAll::getFlag, "1")
        );

        List<ChannelPromotionAll> channelPromotionAllTemp = new ArrayList<ChannelPromotionAll>();
        for (ChannelPromotionAll channelPromotionAll : channelPromotionAllList) {
            channelPromotionAll.setMediumName(channelMedium.getName());

            channelPromotionAllTemp.add(channelPromotionAll);
        }

        this.saveOrUpdateBatch(channelPromotionAllTemp);
    }

    @Override
    public void updateDictThread(BusinessDict businessDict, BusinessDict targetBusinessDict) {
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("=====================>【异步开始】编辑业务分类，businessDict：" + JSON.toJSONString(businessDict) + "，targetBusinessDict：" + JSON.toJSONString(targetBusinessDict));
                    updateDict(businessDict, targetBusinessDict);
                    logger.info("=====================>【异步结束】编辑业务分类");
                } catch (Exception e) {
                    logger.info("=====================>【异步异常】编辑业务分类" + e.getMessage(), e);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateDict(BusinessDict businessDict, BusinessDict targetBusinessDict) {
        List<ChannelPromotionAll> channelPromotionAllList = channelPromotionAllMapper.selectList(new QueryWrapper<ChannelPromotionAll>().lambda()
                .eq(ChannelPromotionAll::getBusinessDictId, businessDict.getId())
                .eq(ChannelPromotionAll::getFlag, "1")
        );

        List<ChannelPromotionAll> channelPromotionAllTemp = new ArrayList<ChannelPromotionAll>();
        for (ChannelPromotionAll channelPromotionAll : channelPromotionAllList) {
            channelPromotionAll.setFirstLevelBusiness(targetBusinessDict.getFirstLevel());
            channelPromotionAll.setSecondLevelBusiness(targetBusinessDict.getSecondLevel());
            channelPromotionAll.setThirdLevelBusiness(targetBusinessDict.getThirdLevel());
            channelPromotionAll.setBusinessDictId(targetBusinessDict.getId());

            channelPromotionAllTemp.add(channelPromotionAll);
        }

        this.saveOrUpdateBatch(channelPromotionAllTemp);
    }

    @Override
    public void updateCompanyThread(Cooperation cooperation) {
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("=====================>【异步开始】编辑合作方" + JSON.toJSONString(cooperation));
                    updateCompany(cooperation);
                    logger.info("=====================>【异步结束】编辑合作方");
                } catch (Exception e) {
                    logger.info("=====================>【异步异常】编辑合作方" + e.getMessage(), e);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateCompany(Cooperation cooperation) {
        List<ChannelPromotionAll> channelPromotionAllList = channelPromotionAllMapper.selectList(new QueryWrapper<ChannelPromotionAll>().lambda()
                .eq(ChannelPromotionAll::getCompanyId, cooperation.getId())
                .eq(ChannelPromotionAll::getFlag, "1")
        );

        List<ChannelPromotionAll> channelPromotionAllTemp = new ArrayList<ChannelPromotionAll>();
        for (ChannelPromotionAll channelPromotionAll : channelPromotionAllList) {
            channelPromotionAll.setCompanyName(cooperation.getCompanyName());

            channelPromotionAllTemp.add(channelPromotionAll);
        }

        this.saveOrUpdateBatch(channelPromotionAllTemp);
    }

    @Override
    public void updateProductThread(ChannelProduct channelProduct) {
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("=====================>【异步开始】编辑产品" + JSON.toJSONString(channelProduct));
                    updateProduct(channelProduct);
                    logger.info("=====================>【异步结束】编辑产品");
                } catch (Exception e) {
                    logger.info("=====================>【异步异常】编辑产品" + e.getMessage(), e);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateProduct(ChannelProduct channelProduct) {
        //List<ChannelApplication> applicationList = channelProduct.getApplicationList();
        //DB
        List<ChannelPromotionAll> channelPromotionAllList = channelPromotionAllMapper.selectListByApp(channelProduct.getProductCode());

        //参数
        //Map<String, ChannelApplication> channelApplicationMap = CollectionUtil.isNotEmpty(applicationList) ? applicationList.stream().collect(Collectors.toMap(item -> item.getProductCode() + item.getId(), item -> item)) : new HashMap<String, ChannelApplication>();

        List<ChannelPromotionAll> channelPromotionAllTemp = new ArrayList<ChannelPromotionAll>();
        for (ChannelPromotionAll channelPromotionAll : channelPromotionAllList) {
            channelPromotionAll.setProductName(channelProduct.getProductName());

            /*String key = channelPromotionAll.getProductCode() + channelPromotionAll.getApplicationId();
            if (channelApplicationMap.containsKey(key)) {
                ChannelApplication channelApplication = channelApplicationMap.get(key);
                channelPromotionAll.setApplicationName(channelApplication.getApplicationName());
            } else {
                // 当Map中不存在 当前key，则说明用户在产品中做了删除应用操作
                channelPromotionAll.setApplicationId(null);
                channelPromotionAll.setApplicationName(null);
            }*/

            channelPromotionAll.setCooperationMainId(channelProduct.getCooperationMainId());
            channelPromotionAll.setCooperationMainName(channelProduct.getCooperationMainName());
            channelPromotionAll.setVendorId(channelProduct.getVendorId());
            channelPromotionAll.setVendorName(channelProduct.getVendorName());
            channelPromotionAll.setVendorCheckStartDate(channelProduct.getVendorCheckStartDate());
            channelPromotionAll.setVendorCheckEndDate(channelProduct.getVendorCheckEndDate());
            channelPromotionAll.setSaleDepartmentCode(channelProduct.getSaleDepartmentCode());
            channelPromotionAll.setSaleDepartmentName(channelProduct.getSaleDepartmentName());
            channelPromotionAll.setProductFlag(channelProduct.getProductFlag());
            channelPromotionAll.setProductScreen(channelProduct.getProductScreen());
            channelPromotionAll.setProductClass(channelProduct.getProductClass());
            channelPromotionAll.setProductTheme(channelProduct.getProductTheme());

            channelPromotionAllTemp.add(channelPromotionAll);
        }

        this.saveOrUpdateBatch(channelPromotionAllTemp);
    }


    @Override
    public void updateChannelApplicationThread(ChannelApplication channelApplication) {
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("=====================>【异步开始】编辑应用" + JSON.toJSONString(channelApplication));
                    updateChannelApplication(channelApplication);
                    logger.info("=====================>【异步结束】编辑应用");
                } catch (Exception e) {
                    logger.info("=====================>【异步异常】编辑应用" + e.getMessage(), e);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateChannelApplication(ChannelApplication channelApplication) {
        List<ChannelPromotionAll> channelPromotionAllList = channelPromotionAllMapper.selectList(new LambdaQueryWrapper<ChannelPromotionAll>().eq(ChannelPromotionAll::getApplicationId, channelApplication.getId()));

        for (ChannelPromotionAll channelPromotionAll : channelPromotionAllList) {
            channelPromotionAll.setApplicationName(channelApplication.getApplicationName());
        }

        this.saveOrUpdateBatch(channelPromotionAllList);
    }

    @Override
    public void updateBusinessDictBatchThread(List<String> ccidList, ChannelCooperation channelCooperation) {
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("=====================>【异步开始】CCID批量替换业务分类，CCID: " + JSON.toJSONString(ccidList) + "，业务分类：" + JSON.toJSONString(channelCooperation));
                    updateBusinessDictBatch(ccidList, channelCooperation);
                    logger.info("=====================>【异步结束】CCID批量替换业务分类");
                } catch (Exception e) {
                    logger.info("=====================>【异步异常】CCID批量替换业务分类" + e.getMessage(), e);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateBusinessDictBatch(List<String> ccidList, ChannelCooperation channelCooperation) {
        List<ChannelPromotionAll> channelPromotionAllList = channelPromotionAllMapper.selectList(new LambdaQueryWrapper<ChannelPromotionAll>().eq(ChannelPromotionAll::getCcid, ccidList));

        for (ChannelPromotionAll channelPromotionAll : channelPromotionAllList) {
            channelPromotionAll.setBusinessDictId(channelCooperation.getBusinessDictId());
            channelPromotionAll.setFirstLevelBusiness(channelCooperation.getFirstLevelBusiness());
            channelPromotionAll.setSecondLevelBusiness(channelCooperation.getSecondLevelBusiness());
            channelPromotionAll.setThirdLevelBusiness(channelCooperation.getThirdLevelBusiness());
        }

        this.saveOrUpdateBatch(channelPromotionAllList);
    }
}
