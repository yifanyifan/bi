package com.stnts.bi.datamanagement.module.channel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.business.service.BusinessDictService;
import com.stnts.bi.datamanagement.module.channel.entity.*;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelApplicationMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelCooperationMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelPromotionMapper;
import com.stnts.bi.datamanagement.module.channel.param.*;
import com.stnts.bi.datamanagement.module.channel.service.*;
import com.stnts.bi.datamanagement.module.channel.vo.*;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.mapper.CooperationMapper;
import com.stnts.bi.datamanagement.module.cooperation.vo.UserVO;
import com.stnts.bi.datamanagement.module.exportdata.dataenum.*;
import com.stnts.bi.datamanagement.module.exportdata.service.ExportDataService;
import com.stnts.bi.datamanagement.module.feign.SysClient;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.entity.sys.UserDmEntity;
import com.stnts.bi.entity.sys.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ???????????? ???????????????
 *
 * @author ?????????
 * @since 2021-02-04
 */
@Slf4j
@Service
public class ChannelPromotionServiceImpl extends ServiceImpl<ChannelPromotionMapper, ChannelPromotion> implements ChannelPromotionService {
    private static final Logger logger = LoggerFactory.getLogger(ChannelPromotionServiceImpl.class);

    @Value("${data-management.setting.youtop-api-host}")
    private String youtopApiHost;

    @Autowired
    private ChannelPromotionMapper channelPromotionMapper;
    @Autowired
    private ChannelCooperationService channelCooperationService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ChannelChildService channelChildService;
    @Autowired
    private ChannelMediumService channelMediumService;
    @Autowired
    private ChannelProductService channelProductService;
    @Autowired
    private ChannelPromotionPositionService channelPromotionPositionService;
    @Autowired
    private EnvironmentProperties properties;
    @Autowired
    private ChannelBaseIdService channelBaseIdService;
    @Autowired
    private ChannelApplicationService channelApplicationService;
    @Autowired
    private ChannelApplicationMapper channelApplicationMapper;
    @Autowired
    private ChannelPromotionHistoryService channelPromotionHistoryService;
    @Autowired
    private BusinessDictService businessDictService;
    @Autowired
    private ChannelCooperationMapper channelCooperationMapper;
    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SysClient sysClient;
    @Autowired
    private CooperationMapper cooperationMapper;
    @Autowired
    private ExportDataService exportDataService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<Object, Object> saveChannelPromotion(ChannelPromotion channelPromotion) throws Exception {
        String subChannelId = null;
        //?????????????????????
        if (StrUtil.isNotEmpty(channelPromotion.getSubChannelName())) {
            subChannelId = newSubChannel(channelPromotion);
            channelPromotion.setSubChannelId(subChannelId);
        } else if (StrUtil.isNotEmpty(channelPromotion.getSubChannelId())) {
            subChannelId = channelPromotion.getSubChannelId();

            Integer childCount = channelChildService.count(new LambdaQueryWrapper<ChannelChild>().eq(ChannelChild::getSubChannelId, subChannelId));
            if (childCount == 0) {
                throw new BusinessException("?????????ID?????????");
            }
        } else {
            throw new BusinessException("?????????ID????????????????????????????????????");
        }

        String pidAlias = channelPromotion.getPidAlias();
        Integer pidNum = channelPromotion.getPidNum();

        //??????????????????
        /*if (ObjectUtil.isNotEmpty(channelPromotion.getChannelIdSettlement())) {
            Channel channel = channelService.getOne(new LambdaQueryWrapper<Channel>().eq(Channel::getChannelId, channelPromotion.getChannelIdSettlement()));
            channelPromotion.setChannelNameSettlement(channel.getChannelName());
        }*/

        // ??????PID??????
        List<String> pidList = channelBaseIdService.getNewPIDs(pidNum);
        if (CollectionUtil.isEmpty(pidList)) {
            throw new BusinessException("PID????????????");
        }
        // ??????lastNum
        Integer lastNum = getLastNum(pidAlias); //by yifan 20211104
        //????????????ProductId
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
            cps.add(cp);
        }
        super.saveBatch(cps);

        // ??????YouTop + ?????????
        List<PostPidParam> postPidParam = new ArrayList<PostPidParam>();
        if (cps != null && cps.size() > 0) {
            cps = cps.stream().filter(i -> StringUtils.isNotBlank(i.getProductId())).collect(Collectors.toList());
            if (cps.size() > 0) {
                postPidParam = DozerUtil.toBeanList(cps, PostPidParam.class);
            }
        }
        channelPromotionAllService.addBatchThread(cps, youtopApiHost, postPidParam);

        Map<Object, Object> map = MapUtil.builder().put("subChannelId", subChannelId).put("pidList", pidList).build();
        return map;
    }

    @Override
    public Integer getLastNum(String pidAlias) {
        //??????????????????????????????????????????????????????
        Integer lastNum = 0;
        String str = channelPromotionMapper.getNumByPidAlias(pidAlias); // yf
        if (StringUtils.isNotBlank(str)) {
            if (str.equals(pidAlias)) {
                lastNum = 1;
            } else {
                List<String> list = Arrays.asList(str.split("_"));
                String last = list.get(list.size() - 1);
                if (str.contains("_") && NumberUtil.isNumber(last)) {
                    lastNum = Integer.valueOf(last) + 1;
                } else {
                    lastNum = 1;
                }
            }
        }
        return lastNum;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> saveChannelPromotionGeneral(ChannelPromotion channelPromotion) throws Exception {
        Long userId = channelPromotion.getUserid();

        Map<String, Object> all = new HashMap<String, Object>();
        List<String> blackSubChannelId = new ArrayList<String>();

        List<ChannelPromotion> cps = new ArrayList<>();
        try {
            //????????????
            emptyParam(channelPromotion);
            //??????????????????CCID??????????????????????????????????????????????????????????????????????????????
            //defaultParam(channelPromotion);

            //CCID
            ChannelCooperation channelCooperation = ccidHandlerGeneral(channelPromotion);
            //?????????
            Boolean isAdd = subChannelHandlerGeneral(channelCooperation, channelPromotion);
            if (isAdd) {
                blackSubChannelId.add(channelPromotion.getSubChannelId());
            }
            //??????????????????????????????????????????
            List<String> returnErrorMsg1 = ppHandlerGeneral(channelCooperation, channelPromotion);
            //???????????????????????????????????????
            List<String> result = mediumCommon(channelCooperation, channelPromotion.getMediumId(), channelPromotion.getMediumName());
            channelPromotion.setMediumId(CollectionUtil.isNotEmpty(result) ? result.get(0) : null);
            channelPromotion.setMediumName(CollectionUtil.isNotEmpty(result) ? result.get(1) : null);
            //??????CODE + ???????????? + ??????CCID?????????????????????????????????
            List<String> returnErrorMsg2 = productHandlerGeneral(channelCooperation, channelPromotion);
            //??????ID?????????????????????????????????
            List<String> returnErrorMsg3 = appHandlerGeneral(channelPromotion);
            //?????????
            userHandlerGeneral(channelPromotion);

            //????????????
            String pidAlias = channelPromotion.getPidAlias();
            Integer pidNum = channelPromotion.getPidNum();

            List<String> pidList = channelBaseIdService.getNewPIDs(pidNum);
            if (CollectionUtil.isEmpty(pidList)) {
                throw new BusinessException("PID????????????");
            }
            //??????????????????????????????????????????????????????
            Integer lastNum = this.getLastNum(pidAlias);//by yifan 20211104

            for (int i = 0; i < pidNum; i++) {
                ChannelPromotion cp = DozerUtil.toBean(channelPromotion, ChannelPromotion.class);

                String pidName = lastNum > 0 ? pidAlias.concat("_").concat(String.valueOf(lastNum)) : pidAlias;
                lastNum++;

                cp.setPidAlias(pidName);
                cp.setPid(pidList.get(i));
                cp.setCheckStartDate(channelPromotion.getCheckStartDate() == null ? new Date() : channelPromotion.getCheckStartDate());
                cp.setCheckEndDate(channelPromotion.getCheckEndDate() == null ? DateUtils.parseDate("2099-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss") : channelPromotion.getCheckEndDate());

                cp.setProductId(channelPromotion.getProductId());
                cp.setProductName(channelPromotion.getProductName());
                cps.add(cp);
            }
            super.saveBatch(cps);

            // ??????YouTop + ?????????
            List<PostPidParam> postPidParam = new ArrayList<PostPidParam>();
            if (cps != null && cps.size() > 0) {
                cps = cps.stream().filter(i -> StringUtils.isNotBlank(i.getProductId())).collect(Collectors.toList());
                if (cps.size() > 0) {
                    postPidParam = DozerUtil.toBeanList(cps, PostPidParam.class);
                }
            }
            channelPromotionAllService.addBatchThread(cps, youtopApiHost, postPidParam);

            // ????????????????????????
            List<String> returnErrorAll = new ArrayList<String>();
            returnErrorAll.addAll(returnErrorMsg1);
            if (CollectionUtil.isNotEmpty(channelCooperation.getReturnErrorMsgList())) {
                returnErrorAll.addAll(channelCooperation.getReturnErrorMsgList());
            }
            returnErrorAll.addAll(returnErrorMsg2);
            returnErrorAll.addAll(returnErrorMsg3);
            cps.stream().forEach(t -> t.setUserid(userId));
            all.put("list", cps);
            all.put("msg", returnErrorAll);
        } catch (Exception e) {
            if (blackSubChannelId.size() > 0) {
                channelChildService.remove(new QueryWrapper<ChannelChild>().lambda().in(ChannelChild::getSubChannelId, blackSubChannelId));
            }

            throw new BusinessException(e.getMessage(), e);
        }

        return all;
    }

    public ChannelCooperation ccidHandlerGeneral(ChannelPromotion channelPromotion) {
        // ??????CCID????????????
        ChannelCooperation channelCooperation = channelCooperationService.getOne(new QueryWrapper<ChannelCooperation>().lambda()
                .eq(ChannelCooperation::getCcid, channelPromotion.getCcid()));
        if (ObjectUtil.isEmpty(channelCooperation)) {
            throw new BusinessException("CCID?????????");
        }
        return channelCooperation;
    }

    public Boolean subChannelHandlerGeneral(ChannelCooperation channelCooperation, ChannelPromotion channelPromotion) throws Exception {
        Boolean isAdd = false;

        String subChannelId = null;
        ChannelChild channelChild = null;
        //?????????????????????
        if (StringUtils.isNotBlank(channelPromotion.getSubChannelId())) {
            channelChild = channelChildService.getOne(new LambdaQueryWrapper<ChannelChild>()
                    .eq(ChannelChild::getChannelId, channelCooperation.getChannelId())
                    .eq(ChannelChild::getSubChannelId, channelPromotion.getSubChannelId())
            );
            if (ObjectUtil.isEmpty(channelChild)) {
                throw new BusinessException("?????????ID????????????" + channelPromotion.getSubChannelId());
            } else if (StringUtils.isNotBlank(channelPromotion.getSubChannelName()) && !channelChild.getSubChannelName().equals(channelPromotion.getSubChannelName())) {
                throw new BusinessException("?????????ID???????????????????????????");
            }
            subChannelId = channelChild.getSubChannelId();
        } else {
            channelChild = channelChildService.getOne(new QueryWrapper<ChannelChild>().lambda()
                    .eq(ChannelChild::getChannelId, channelCooperation.getChannelId())
                    .eq(ChannelChild::getSubChannelName, channelPromotion.getSubChannelName()));
            if (ObjectUtil.isEmpty(channelChild)) {
                subChannelId = newSubChannel(channelPromotion);
                isAdd = true;
            } else {
                subChannelId = channelChild.getSubChannelId();
            }
        }
        channelPromotion.setSubChannelId(subChannelId);

        return isAdd;
    }

    public List<String> ppHandlerGeneral(ChannelCooperation channelCooperation, ChannelPromotion channelPromotion) {
        List<String> returnErrorMsg = new ArrayList<String>();

        if (ObjectUtil.isNotEmpty(channelPromotion.getPpFlag())) {
            PPTypeEnum ppTypeEnum = PPTypeEnum.getByKey(channelPromotion.getPpFlag());
            if (ObjectUtil.isEmpty(ppTypeEnum)) {
                returnErrorMsg.add("??????????????????????????????");
            }
        }

        if (StringUtils.isNotBlank(channelPromotion.getPpName())) {
            if (ObjectUtil.isEmpty(channelPromotion.getPpFlag())) {
                channelPromotion.setPpFlag(1);
            }
            ChannelPromotionPosition channelPromotionPosition = channelPromotionPositionService.getOne(new QueryWrapper<ChannelPromotionPosition>().lambda()
                    .eq(ChannelPromotionPosition::getChannelId, channelCooperation.getChannelId())
                    .eq(channelPromotion.getPpFlag() == 2, ChannelPromotionPosition::getSubChannelId, channelPromotion.getSubChannelId())
                    .eq(ChannelPromotionPosition::getPpName, channelPromotion.getPpName())
                    .eq(ChannelPromotionPosition::getPpFlag, channelPromotion.getPpFlag())
            );
            if (ObjectUtil.isEmpty(channelPromotionPosition)) {
                returnErrorMsg.add("???????????????????????????ID:" + channelCooperation.getChannelId() + (channelPromotion.getPpFlag() == 2 ? "????????????ID:" + channelPromotion.getSubChannelId() : "") + ", ???????????????:" + channelPromotion.getPpName() + ", ???????????????:" + (channelPromotion.getPpFlag() == 1 ? "???????????????" : "??????????????????"));
            } else {
                channelPromotion.setPpId(channelPromotionPosition.getPpId());
            }
        }

        if (CollectionUtil.isNotEmpty(returnErrorMsg)) {
            channelPromotion.setPpFlag(null);
            channelPromotion.setPpName(null);
        }

        return returnErrorMsg;
    }

    public List<String> productHandlerGeneral(ChannelCooperation channelCooperation, ChannelPromotion channelPromotion) {
        List<String> returnErrorMsg = new ArrayList<String>();

        ChannelProduct channelProductDB = channelProductService.getOne(new LambdaQueryWrapper<ChannelProduct>().eq(ChannelProduct::getProductCode, channelPromotion.getProductCode()));
        // ????????? ???????????????????????? ???????????????
        ChannelProduct channelProduct = channelProductService.getOneByParam(channelCooperation.getDepartmentCode(), channelPromotion.getProductCode());

        if (ObjectUtil.isEmpty(channelProduct)) {
            throw new BusinessException("??????CODE???" + channelPromotion.getProductCode() + "??? ?????????????????????????????????????????????" + channelCooperation.getDepartmentName() + "???" + channelCooperation.getDepartmentCode() + "???");
        } else {
            channelPromotion.setProductId(channelProduct.getProductId());
            channelPromotion.setProductName(channelProduct.getProductName());
        }

        // ??????CCID???????????????????????????????????????????????????????????? ????????????CCID
        if (StringUtils.isNotBlank(channelPromotion.getCcidSettlement())) {
            ChannelPromotionPageParam settlementCCIDParam = new ChannelPromotionPageParam();
            settlementCCIDParam.setDepartmentCode(channelProductDB.getDepartmentCode());
            settlementCCIDParam.setSettlementType("1");
            /*if (ObjectUtil.isNotEmpty(channelPromotion.getChannelIdSettlement())) {
                settlementCCIDParam.setChannelId(channelPromotion.getChannelIdSettlement());
            }*/
            settlementCCIDParam.setCcid(channelPromotion.getCcidSettlement());
            List<ChannelCooperation> cooperationList = channelPromotionMapper.settlementCCIDList(settlementCCIDParam);
            if (CollectionUtil.isEmpty(cooperationList)) {
                channelPromotion.setCcidSettlement(null);
                returnErrorMsg.add("?????????????????????CCID???????????????????????????????????????CCID?????????????????????");
            } else {
                channelPromotion.setChannelIdSettlement(cooperationList.get(0).getChannelId());
                channelPromotion.setChannelNameSettlement(cooperationList.get(0).getChannelName());
            }
        }

        return returnErrorMsg;
    }

    public List<String> appHandlerGeneral(ChannelPromotion channelPromotion) {
        List<String> returnErrorMsg = new ArrayList<String>();

        if (ObjectUtil.isNotEmpty(channelPromotion.getApplicationId())) {
            ChannelApplication channelApplication = channelApplicationMapper.selectOne(new LambdaQueryWrapper<ChannelApplication>().eq(ChannelApplication::getId, channelPromotion.getApplicationId()));
            if (ObjectUtil.isEmpty(channelApplication)) {
                ChannelProduct channelProduct = channelProductService.getOne(new LambdaQueryWrapper<ChannelProduct>().eq(ChannelProduct::getProductCode, channelPromotion.getProductCode()));
                if (StringUtils.isNotBlank(channelProduct.getApplicationIds())) {
                    throw new BusinessException("??????????????????ApplicationId???" + channelPromotion.getApplicationId());
                } else {
                    channelPromotion.setApplicationId(null);
                    returnErrorMsg.add("?????????????????????????????????????????????");
                }
            } else {
                channelPromotion.setApplicationId(channelApplication.getId());
            }
        } else if (StringUtils.isNotBlank(channelPromotion.getApplicationName())) {
            ChannelApplication param = new ChannelApplication();
            param.setProductCodeList(Arrays.asList(channelPromotion.getProductCode()));
            param.setApplicationName(channelPromotion.getApplicationName());
            List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectByProductNameAndAppName(param);

            if (CollectionUtil.isEmpty(channelApplicationList)) {
                throw new BusinessException("??????????????????ApplicationName:" + channelPromotion.getApplicationName());
            } else if (channelApplicationList.size() > 1) {
                throw new BusinessException("?????????????????????ApplicationName:" + channelPromotion.getApplicationName());
            } else {
                channelPromotion.setApplicationId(channelApplicationList.get(0).getId());
            }
        } else {
            //????????????
            ChannelProduct channelProduct = channelProductService.getOne(new LambdaQueryWrapper<ChannelProduct>().eq(ChannelProduct::getProductCode, channelPromotion.getProductCode()));
            String applicationIds = channelProduct.getApplicationIds();
            if (StringUtils.isNotBlank(applicationIds)) {
                throw new BusinessException("???????????????????????????????????????");
            }
        }

        return returnErrorMsg;
    }

    public void userHandlerGeneral(ChannelPromotion channelPromotion) {
        List<UserVO> userVOList = exportDataService.getUser();
        userVOList = userVOList.stream().filter(t -> String.valueOf(channelPromotion.getUserid()).equals(t.getCardNumber())).collect(Collectors.toList());
        if (userVOList.size() != 1) {
            throw new BusinessException("??????????????????");
        } else {
            channelPromotion.setUserid(Long.valueOf(userVOList.get(0).getId()));
            channelPromotion.setUsername(userVOList.get(0).getCnname());
        }
    }

    /*public void defaultParam(ChannelPromotion channelPromotion) throws Exception {
        List<String> blackSubChannelIdList = new ArrayList<String>();

        String subChannelId = null;
        // ??????CCID????????????
        ChannelCooperation channelCooperation = channelCooperationService.getOne(new QueryWrapper<ChannelCooperation>().lambda()
                .eq(ChannelCooperation::getCcid, channelPromotion.getCcid()));
        if (ObjectUtil.isEmpty(channelCooperation)) {
            throw new BusinessException("CCID?????????");
        }

        //?????????????????????
        ChannelChild channelChild = channelChildService.getOne(new QueryWrapper<ChannelChild>().lambda()
                .eq(ChannelChild::getChannelId, channelCooperation.getChannelId())
                .eq(ChannelChild::getSubChannelName, channelPromotion.getSubChannelName()));
        if (ObjectUtil.isEmpty(channelChild)) {
            subChannelId = newSubChannel(channelPromotion);
            blackSubChannelIdList.add(subChannelId);
        } else {
            subChannelId = channelChild.getSubChannelId();
        }
        channelPromotion.setSubChannelId(subChannelId);

        //?????????
        if (StringUtils.isNotBlank(channelPromotion.getPpName())) {
            if (ObjectUtil.isEmpty(channelPromotion.getPpFlag())) {
                channelPromotion.setPpFlag(1);
            }
            ChannelPromotionPosition channelPromotionPosition = channelPromotionPositionService.getOne(new QueryWrapper<ChannelPromotionPosition>().lambda()
                    .eq(ChannelPromotionPosition::getChannelId, channelCooperation.getChannelId())
                    .eq(channelPromotion.getPpFlag() == 2, ChannelPromotionPosition::getSubChannelId, subChannelId)
                    .eq(ChannelPromotionPosition::getPpName, channelPromotion.getPpName())
                    .eq(ChannelPromotionPosition::getPpFlag, channelPromotion.getPpFlag())
            );
            if (ObjectUtil.isEmpty(channelPromotionPosition)) {
                throw new BusinessException("???????????????????????????ID:" + channelCooperation.getChannelId() + ", ??????:" + channelPromotion.getPpName() + ", Flag:" + channelPromotion.getPpFlag());
            } else {
                channelPromotion.setPpId(channelPromotionPosition.getPpId());
            }
        }

        //??????CODE
        ChannelProduct channelProduct = channelProductService.getOne(new QueryWrapper<ChannelProduct>().lambda()
                .eq(ChannelProduct::getDepartmentCode, channelCooperation.getDepartmentCode())
                .eq(ChannelProduct::getProductCode, channelPromotion.getProductCode()));
        if (ObjectUtil.isEmpty(channelProduct)) {
            throw new BusinessException("??????CODE????????????" + channelPromotion.getProductCode());
        } else {
            channelPromotion.setProductId(channelProduct.getProductId());
            channelPromotion.setProductName(channelProduct.getProductName());
        }

        //????????????
        if (StringUtils.isNotBlank(channelPromotion.getApplicationName())) {
            ChannelApplication channelApplication = channelApplicationMapper.selectOne(new QueryWrapper<ChannelApplication>().lambda()
                    .eq(ChannelApplication::getProductCode, channelPromotion.getProductCode())
                    .eq(ChannelApplication::getApplicationName, channelPromotion.getApplicationName())
            );
            if (ObjectUtil.isEmpty(channelApplication)) {
                throw new BusinessException("??????????????????ProductCode" + channelPromotion.getProductCode() + ",ApplicationName:" + channelPromotion.getApplicationName());
            } else {
                //channelPromotion.setApplicationId(channelApplication.getApplicationId());
                channelPromotion.setApplicationId(channelApplication.getId());
            }
        } else {
            //????????????
            List<ChannelApplication> channelApplicationList = channelApplicationService.list(new QueryWrapper<ChannelApplication>().lambda().eq(ChannelApplication::getProductCode, channelProduct.getProductCode()));
            if (channelApplicationList != null && channelApplicationList.size() > 0) {
                throw new BusinessException("???????????????????????????????????????");
            }
        }

        //?????????
        List<UserVO> userVOList = exportDataService.getUser();
        userVOList = userVOList.stream().filter(t -> String.valueOf(channelPromotion.getUserid()).equals(t.getCardNumber())).collect(Collectors.toList());
        if (userVOList.size() != 1) {
            throw new BusinessException("??????????????????");
        } else {
            channelPromotion.setUserid(Long.valueOf(userVOList.get(0).getId()));
            channelPromotion.setUsername(userVOList.get(0).getCnname());
        }
    }*/

    public void emptyParam(ChannelPromotion channelPromotion) {
        List<String> errorMsg = new ArrayList<String>();
        if (StringUtils.isBlank(channelPromotion.getCcid())) {
            errorMsg.add("CCID??????");
        }
        if (StringUtils.isBlank(channelPromotion.getSubChannelId()) && StringUtils.isBlank(channelPromotion.getSubChannelName())) {
            errorMsg.add("?????????ID????????????????????????????????????");
        }
        if (StringUtils.isBlank(channelPromotion.getProductCode())) {
            errorMsg.add("??????CODE??????");
        } else if (!channelPromotion.getProductCode().matches("^[A-Z0-9]+$")) {
            errorMsg.add("??????CODE??????????????????????????????");
        }
        if (StringUtils.isBlank(channelPromotion.getPidAlias())) {
            errorMsg.add("??????????????????");
        }
        if (ObjectUtil.isNull(channelPromotion.getUserid())) {
            errorMsg.add("?????????????????????");
        }
        if (ObjectUtil.isEmpty(channelPromotion.getPidNum()) || channelPromotion.getPidNum() == 0) {
            channelPromotion.setPidNum(1);
        } else if (channelPromotion.getPidNum() > 1000) {
            errorMsg.add("??????????????????1000???");
        }
        if (errorMsg.size() > 0) {
            throw new BusinessException(String.join(",", errorMsg));
        }
        if ("".equals(channelPromotion.getApplicationId())) {
            channelPromotion.setApplicationId(null);
        }
    }

    @Override
    public Channel getChannelGeneral(Long channelId, String channelName, Long companyId, String departmentCode) {
        Channel channelDBTemp = new Channel();
        if (ObjectUtil.isNotEmpty(channelId)) {
            channelDBTemp = channelService.getOne(new LambdaQueryWrapper<Channel>().eq(Channel::getChannelId, channelId));
            if (ObjectUtil.isEmpty(channelDBTemp)) {
                throw new BusinessException("??????ID?????????");
            } else if (StringUtils.isNotBlank(channelName) && !channelDBTemp.getChannelName().equals(channelName)) {
                throw new BusinessException("??????ID????????????????????????");
            }
        } else {
            if (ObjectUtil.isEmpty(companyId)) {
                throw new BusinessException("???????????????????????????ID????????????");
            }

            //1.???????????????????????? 2.???????????????????????????
            channelDBTemp = channelService.getOne(new QueryWrapper<Channel>().lambda()
                    .eq(Channel::getChannelName, channelName)
                    .eq(Channel::getCompanyId, companyId)
                    .eq(Channel::getSecretType, 1)
            );
            if (ObjectUtil.isEmpty(channelDBTemp)) {
                channelDBTemp = channelService.getOne(new QueryWrapper<Channel>().lambda()
                        .eq(Channel::getChannelName, channelName)
                        .eq(Channel::getCompanyId, companyId)
                        .eq(Channel::getDepartmentCode, departmentCode)
                );
            }
        }
        if (ObjectUtil.isNotEmpty(channelDBTemp) && ObjectUtil.isNotEmpty(companyId) && !channelDBTemp.getCompanyId().equals(companyId)) {
            throw new BusinessException("????????????????????????????????????????????????????????????????????????");
        }
        return channelDBTemp;
    }

    public Boolean saveCCPGeneralChannel(ChannelPromotionGeneral channelPromotionGeneral, Channel channelDB) {
        Boolean isAddChannel = false;

        Channel channelDBTemp = getChannelGeneral(channelPromotionGeneral.getChannelId(), channelPromotionGeneral.getChannelName(), channelPromotionGeneral.getCompanyId(), channelPromotionGeneral.getDepartmentCode());

        //????????????
        if (ObjectUtil.isEmpty(channelDBTemp)) {
            //??????
            Long companyId = channelPromotionGeneral.getCompanyId();
            if (ObjectUtil.isEmpty(companyId)) {
                throw new BusinessException("????????????????????????ID??????");
            }
            Cooperation cooperation = cooperationMapper.selectById(companyId);
            if (ObjectUtil.isEmpty(cooperation)) {
                throw new BusinessException("???????????????");
            } else {
                channelPromotionGeneral.setCompanyName(cooperation.getCompanyName());
            }

            //??????
            channelDBTemp = new Channel();
            channelDBTemp.setCompanyId(cooperation.getId());
            channelDBTemp.setCompanyName(cooperation.getCompanyName());
            channelDBTemp.setChannelName(channelPromotionGeneral.getChannelName());
            channelDBTemp.setDepartmentCode(channelPromotionGeneral.getDepartmentCode());
            channelDBTemp.setDepartmentName(channelPromotionGeneral.getDepartmentName());
            if (StringUtils.isBlank(channelPromotionGeneral.getChannelType())) {
                channelPromotionGeneral.setChannelType("2");
            }
            channelDBTemp.setChannelType(channelPromotionGeneral.getChannelType());
            if (ObjectUtil.isEmpty(channelPromotionGeneral.getSecretType())) {
                channelPromotionGeneral.setSecretType(1);
            }
            channelDBTemp.setSecretType(channelPromotionGeneral.getSecretType());
            if (ObjectUtil.isEmpty(channelPromotionGeneral.getSettlementType())) {
                channelPromotionGeneral.setSettlementType("2");
            }
            channelDBTemp.setSettlementType(channelPromotionGeneral.getSettlementType());

            Long channelId = channelBaseIdService.getNewChannelID();
            channelDBTemp.setChannelId(channelId); //??????ID
            channelDBTemp.setUserid(channelPromotionGeneral.getUserid());
            channelDBTemp.setUsername(channelPromotionGeneral.getUsername());

            check(channelDBTemp);
            if (StringUtils.isNotBlank(channelPromotionGeneral.getDataSource())) {
                channelDBTemp.setDataSource(channelPromotionGeneral.getDataSource());
            }
            channelService.save(channelDBTemp);
            channelBaseIdService.updateNewChannelID(channelId);

            isAddChannel = true;
        }
        BeanUtils.copyProperties(channelDBTemp, channelDB);
        channelPromotionGeneral.setChannelId(channelDBTemp.getChannelId());
        channelPromotionGeneral.setChannelName(channelDBTemp.getChannelName());

        return isAddChannel;
    }

    public void check(Channel channel) {
        List<Channel> channelList = new ArrayList<Channel>();
        //??????????????????????????????????????????????????????????????????
        if (channel.getSecretType() == 1) {
            channelList = channelService.list(new LambdaQueryWrapper<Channel>()
                    .eq(Channel::getChannelName, channel.getChannelName())
                    .eq(Channel::getCompanyId, channel.getCompanyId())
                    .and(i -> i.eq(Channel::getSecretType, 1).or().eq(Channel::getDepartmentCode, channel.getDepartmentCode()))
            );
        } else {
            channelList = channelService.list(new LambdaQueryWrapper<Channel>()
                    .eq(Channel::getChannelName, channel.getChannelName())
                    .eq(Channel::getCompanyId, channel.getCompanyId())
                    .eq(Channel::getDepartmentCode, channel.getDepartmentCode())
            );
        }
        if (CollectionUtil.isNotEmpty(channelList)) {
            String departmentNameStr = channelList.stream().map(i -> i.getDepartmentName()).collect(Collectors.joining(","));
            throw new BusinessException("??????" + departmentNameStr + "?????????????????????+????????????+??????????????????????????????????????????");
        }
    }

    public Boolean saveCCPGeneralCCID(ChannelPromotionGeneral channelPromotionGeneral, Channel channelDB, ChannelCooperation channelCooperationDB) {
        Boolean isAddCCID = false;

        ChannelCooperation channelCooperationParam = DozerUtil.toBean(channelPromotionGeneral, ChannelCooperation.class);
        channelCooperationParam.setAgentId(channelDB.getCompanyId());
        ChannelCooperation channelCooperationDBTemp = channelCooperationService.getOneCCID(channelCooperationParam, channelCooperationParam.getChargeRule());
        if (ObjectUtil.isEmpty(channelCooperationDBTemp)) {
            channelCooperationDBTemp = new ChannelCooperation();
            String chargeRule = channelPromotionGeneral.getChargeRule();
            String chargeRuleStr = getChargeRule(chargeRule);
            String ccid = channelBaseIdService.getNewCCID(channelDB.getChannelId(), chargeRuleStr);
            channelCooperationDBTemp.setCcid(ccid);
            channelCooperationDBTemp.setAgentId(channelDB.getCompanyId());
            channelCooperationDBTemp.setAgentName(channelDB.getCompanyName());
            channelCooperationDBTemp.setChannelId(channelDB.getChannelId());
            channelCooperationDBTemp.setChannelName(channelDB.getChannelName());
            channelCooperationDBTemp.setDepartmentCode(channelPromotionGeneral.getDepartmentCode());
            channelCooperationDBTemp.setDepartmentName(channelPromotionGeneral.getDepartmentName());
            channelCooperationDBTemp.setBusinessDictId(channelPromotionGeneral.getBusinessDictId());
            channelCooperationDBTemp.setFirstLevelBusiness(channelPromotionGeneral.getFirstLevelBusiness());
            channelCooperationDBTemp.setSecondLevelBusiness(channelPromotionGeneral.getSecondLevelBusiness());
            channelCooperationDBTemp.setThirdLevelBusiness(channelPromotionGeneral.getThirdLevelBusiness());
            channelCooperationDBTemp.setChargeRule(chargeRule);
            channelCooperationDBTemp.setChannelRate(ObjectUtil.isEmpty(channelPromotionGeneral.getChannelRate()) ? BigDecimal.ZERO : channelPromotionGeneral.getChannelRate());
            channelCooperationDBTemp.setChannelShare(channelPromotionGeneral.getChannelShare());
            if (StringUtils.isNotBlank(channelPromotionGeneral.getChannelShareStep())) {
                channelCooperationDBTemp.setChannelShareStep(channelPromotionGeneral.getChannelShareStep());
            }
            channelCooperationDBTemp.setChannelShareType(channelPromotionGeneral.getChannelShareType());
            channelCooperationDBTemp.setPrice(channelPromotionGeneral.getPrice());
            channelCooperationDBTemp.setUserid(channelPromotionGeneral.getUserid());
            channelCooperationDBTemp.setUsername(channelPromotionGeneral.getUsername());
            if (StringUtils.isNotBlank(channelPromotionGeneral.getDataSource())) {
                channelCooperationDBTemp.setDataSource(channelPromotionGeneral.getDataSource());
            }
            channelCooperationService.save(channelCooperationDBTemp);

            isAddCCID = true;
        }
        BeanUtils.copyProperties(channelCooperationDBTemp, channelCooperationDB);
        channelPromotionGeneral.setCcid(channelCooperationDBTemp.getCcid());

        return isAddCCID;
    }

    public List<ChannelPromotion> saveCCPGeneralPID(ChannelPromotionGeneral channelPromotionGeneral) throws Exception {
        //??????PID
        String pidAlias = channelPromotionGeneral.getPidAlias();
        Integer pidNum = channelPromotionGeneral.getPidNum();
        List<String> pidList = channelBaseIdService.getNewPIDs(pidNum);
        if (CollectionUtil.isEmpty(pidList)) {
            throw new BusinessException("PID????????????");
        }
        //??????????????????????????????????????????????????????
        Integer lastNum = this.getLastNum(pidAlias); //by yifan 20211104

        List<ChannelPromotion> cps = new ArrayList<>(pidNum);
        List<String> pidAliasList = new ArrayList<String>();
        for (int i = 0; i < pidNum; i++) {
            ChannelPromotion cp = DozerUtil.toBean(channelPromotionGeneral, ChannelPromotion.class);

            String pidName = lastNum > 0 ? pidAlias.concat("_").concat(String.valueOf(lastNum)) : pidAlias;
            lastNum++;

            cp.setPidAlias(pidName);
            cp.setPid(pidList.get(i));
            //???????????????????????????????????????~20991231???
            cp.setCheckStartDate(channelPromotionGeneral.getCheckStartDate() != null ? channelPromotionGeneral.getCheckStartDate() : new Date());
            cp.setCheckEndDate(channelPromotionGeneral.getCheckEndDate() != null ? channelPromotionGeneral.getCheckEndDate() : DateUtils.parseDate("2099-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss"));

            cp.setProductId(channelPromotionGeneral.getProductId());
            cp.setProductName(channelPromotionGeneral.getProductName());
            cp.setUserid(channelPromotionGeneral.getUserid());
            cp.setUsername(channelPromotionGeneral.getUsername());
            cps.add(cp);

            pidAliasList.add(pidName);
        }
        super.saveBatch(cps);

        // ??????YouTop + ?????????
        List<PostPidParam> postPidParam = new ArrayList<PostPidParam>();
        if (cps != null && cps.size() > 0) {
            cps = cps.stream().filter(i -> StringUtils.isNotBlank(i.getProductId())).collect(Collectors.toList());
            if (cps.size() > 0) {
                postPidParam = DozerUtil.toBeanList(cps, PostPidParam.class);
            }
        }
        channelPromotionAllService.addBatchThread(cps, youtopApiHost, postPidParam);

        return cps;
    }

    @Override
    public ChannelPromotionGeneral saveCCPGeneral(ChannelPromotionGeneral channelPromotionGeneral) throws Exception {
        List<Long> blackChannelId = new ArrayList<Long>();
        List<String> blackCCID = new ArrayList<String>();
        List<String> blackSubChannelId = new ArrayList<String>();

        ChannelPromotionGeneral channelPromotionGeneralReturn = new ChannelPromotionGeneral();
        try {
            //????????????
            Long useridOld = channelPromotionGeneral.getUserid();

            //????????????
            emptyParam(channelPromotionGeneral);
            //???????????????????????????+??????+????????????+??????+??????+????????????+??????CCID+????????????+?????????????????????
            defaultParam(channelPromotionGeneral);

            //????????????
            Channel channelDB = new Channel();
            Boolean isAddChannel = saveCCPGeneralChannel(channelPromotionGeneral, channelDB);
            if (isAddChannel) {
                blackChannelId.add(channelDB.getChannelId());
            }
            //??????CCID
            ChannelCooperation channelCooperationDB = new ChannelCooperation();
            Boolean isAddCCID = saveCCPGeneralCCID(channelPromotionGeneral, channelDB, channelCooperationDB);
            if (isAddCCID) {
                blackCCID.add(channelCooperationDB.getCcid());
            }
            //?????????
            Boolean isAddSubChannel = subChannelHandlerCCP(channelDB, channelPromotionGeneral);
            String subChannelId = channelPromotionGeneral.getSubChannelId();
            if (isAddSubChannel) {
                blackSubChannelId.add(subChannelId);
            }
            //?????????
            ppHandlerCCP(channelDB, channelPromotionGeneral);
            //??????
            List<String> result = mediumCommon(channelCooperationDB, channelPromotionGeneral.getMediumId(), channelPromotionGeneral.getMediumName());
            if (CollectionUtil.isNotEmpty(channelCooperationDB.getReturnErrorMsgList())) {
                throw new BusinessException(channelCooperationDB.getReturnErrorMsgList().stream().distinct().collect(Collectors.joining(",")));
            }
            channelPromotionGeneral.setMediumId(CollectionUtil.isNotEmpty(result) ? result.get(0) : null);
            channelPromotionGeneral.setMediumName(CollectionUtil.isNotEmpty(result) ? result.get(1) : null);
            //PID
            List<ChannelPromotion> cps = saveCCPGeneralPID(channelPromotionGeneral);

            BeanUtils.copyProperties(channelDB, channelPromotionGeneralReturn, DozerUtil.getNullPropertyNames(channelDB));
            BeanUtils.copyProperties(channelCooperationDB, channelPromotionGeneralReturn, DozerUtil.getNullPropertyNames(channelCooperationDB));
            channelPromotionGeneralReturn.setCompanyId(channelCooperationDB.getAgentId());
            channelPromotionGeneralReturn.setCompanyName(channelCooperationDB.getAgentName());
            BeanUtils.copyProperties(channelPromotionGeneral, channelPromotionGeneralReturn, DozerUtil.getNullPropertyNames(channelPromotionGeneral));
            //cps.stream().forEach(t -> t.setUserid(useridOld));

            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            for (ChannelPromotion channelPromotion : cps) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("pidAlias", channelPromotion.getPidAlias());
                map.put("pid", channelPromotion.getPid());
                list.add(map);
            }
            channelPromotionGeneralReturn.setCps(list);
            channelPromotionGeneralReturn.setUserid(useridOld);
        } catch (Exception e) {
            if (blackChannelId.size() > 0) {
                channelService.remove(new QueryWrapper<Channel>().lambda().in(Channel::getChannelId, blackChannelId));
            }
            if (blackCCID.size() > 0) {
                channelCooperationService.remove(new QueryWrapper<ChannelCooperation>().lambda().in(ChannelCooperation::getCcid, blackCCID));
            }
            if (blackSubChannelId.size() > 0) {
                channelChildService.remove(new QueryWrapper<ChannelChild>().lambda().in(ChannelChild::getSubChannelId, blackSubChannelId));
            }
            throw new BusinessException(e.getMessage(), e);
        }

        return channelPromotionGeneralReturn;
    }

    public List<String> mediumCommon(ChannelCooperation channelCooperation, String mediumIds, String mediumNames) {
        List<String> resultErrorMsg = new ArrayList<String>();
        List<String> result = new ArrayList<String>();
        String mediumIdsEnd = null;
        String mediumNamesEnd = null;

        if (StringUtils.isNotBlank(mediumIds)) { // ?????????ID??????
            List<String> mediumIdsList = new ArrayList<>(Arrays.asList(mediumIds.split(",")).stream().map(i -> i.trim()).collect(Collectors.toList()));

            List<ChannelMedium> channelMediumDBList = channelMediumService.list(new LambdaQueryWrapper<ChannelMedium>().in(ChannelMedium::getId, mediumIdsList)
                    .eq(ChannelMedium::getDepartmentCode, channelCooperation.getDepartmentCode())
            );
            if (channelMediumDBList.size() < mediumIdsList.size()) {
                List<String> mediumIdDBList = channelMediumDBList.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.toList());
                mediumIdsList.removeAll(mediumIdDBList);
                resultErrorMsg.add("????????????ID??????????????????Code???" + channelCooperation.getDepartmentCode() + "?????????ID???" + String.join(",", mediumIdsList));
            } else if (StringUtils.isNotBlank(mediumNames)) { // ?????????????????????
                List<String> mediumNamesList = new ArrayList<>(Arrays.asList(mediumNames.split(",")).stream().map(i -> i.trim()).collect(Collectors.toList()));
                List<String> mediumNameDBList = channelMediumDBList.stream().map(ChannelMedium::getName).collect(Collectors.toList());

                if (mediumNamesList.size() != mediumNameDBList.size()) {
                    resultErrorMsg.add("???????????????????????????ID????????????" + String.join(",", mediumNamesList));
                } else {
                    List<String> mediumNamesListTemp = new ArrayList<String>(mediumNamesList);
                    mediumNamesListTemp.removeAll(mediumNameDBList);
                    if (mediumNamesListTemp.size() > 0) {
                        resultErrorMsg.add("???????????????????????????ID????????????" + String.join(",", mediumNamesListTemp));
                    } else {
                        List<String> mediumNamesDBList = channelMediumDBList.stream().map(ChannelMedium::getName).collect(Collectors.toList());
                        mediumNamesEnd = String.join(",", mediumNamesDBList);
                        mediumIdsEnd = String.join(",", mediumIdsList);
                    }
                }
            } else {
                List<String> mediumNamesDBList = channelMediumDBList.stream().map(ChannelMedium::getName).collect(Collectors.toList());
                mediumNamesEnd = String.join(",", mediumNamesDBList);
                mediumIdsEnd = String.join(",", mediumIdsList);
            }
        } else if (StringUtils.isNotBlank(mediumNames)) { // ?????????????????????
            List<String> mediumNamesList = new ArrayList<>(Arrays.asList(mediumNames.split(",")).stream().map(i -> i.trim()).distinct().collect(Collectors.toList()));

            List<ChannelMedium> channelMediumDBList = channelMediumService.list(new LambdaQueryWrapper<ChannelMedium>().in(ChannelMedium::getName, mediumNamesList)
                    .eq(ChannelMedium::getDepartmentCode, channelCooperation.getDepartmentCode())
            );
            List<String> mediumNameDBList = channelMediumDBList.stream().map(ChannelMedium::getName).collect(Collectors.toList());
            if (channelMediumDBList.size() < mediumNamesList.size()) {
                mediumNamesList.removeAll(mediumNameDBList);
                resultErrorMsg.add("????????????????????????????????????Code???" + channelCooperation.getDepartmentCode() + "??????????????????" + String.join(",", mediumNamesList));
            } else {
                List<String> mediumNamesListTemp = new ArrayList<String>(mediumNamesList);
                mediumNamesListTemp.removeAll(mediumNameDBList);
                if (mediumNamesListTemp.size() > 0) {
                    resultErrorMsg.add("????????????????????????" + String.join(",", mediumNamesListTemp));
                } else {
                    List<String> mediumIdDBList = channelMediumDBList.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.toList());
                    mediumIdsEnd = String.join(",", mediumIdDBList);
                    mediumNamesEnd = String.join(",", mediumNameDBList);
                }
            }
        }

        if (CollectionUtil.isNotEmpty(resultErrorMsg)) {
            channelCooperation.setReturnErrorMsgList(resultErrorMsg);
            return result;
        }
        if (StringUtils.isNotBlank(mediumIdsEnd) && StringUtils.isNotBlank(mediumNamesEnd)) {
            result.add(mediumIdsEnd);
            result.add(mediumNamesEnd);
        }
        return result;
    }

    public Boolean subChannelHandlerCCP(Channel channelDB, ChannelPromotionGeneral channelPromotionGeneral) throws Exception {
        Boolean isAdd = false;

        String subChannelId = null;

        ChannelChild channelChild = null;
        if (StringUtils.isNotBlank(channelPromotionGeneral.getSubChannelId())) {
            channelChild = channelChildService.getOne(new QueryWrapper<ChannelChild>().lambda()
                    .eq(ChannelChild::getChannelId, channelDB.getChannelId())
                    .eq(ChannelChild::getSubChannelId, channelPromotionGeneral.getSubChannelId())
            );
            if (ObjectUtil.isEmpty(channelChild)) {
                throw new BusinessException("?????????ID?????????");
            } else if (StringUtils.isNotBlank(channelPromotionGeneral.getSubChannelName()) && !channelChild.getSubChannelName().equals(channelPromotionGeneral.getSubChannelName())) {
                throw new BusinessException("?????????ID???????????????????????????");
            }
            subChannelId = channelChild.getSubChannelId();
        } else {
            //1. ?????????????????????
            channelChild = channelChildService.getOne(new QueryWrapper<ChannelChild>().lambda()
                    .eq(ChannelChild::getChannelId, channelDB.getChannelId())
                    .eq(ChannelChild::getSubChannelName, channelPromotionGeneral.getSubChannelName())
            );
            if (ObjectUtil.isEmpty(channelChild)) {
                ChannelPromotion channelPromotion = DozerUtil.toBean(channelPromotionGeneral, ChannelPromotion.class);
                subChannelId = newSubChannel(channelPromotion);
                isAdd = true;
            } else {
                subChannelId = channelChild.getSubChannelId();
            }
        }
        channelPromotionGeneral.setSubChannelId(subChannelId);

        return isAdd;
    }

    public void ppHandlerCCP(Channel channelDB, ChannelPromotionGeneral channelPromotionGeneral) {
        if (StringUtils.isNotBlank(channelPromotionGeneral.getPpName())) {
            channelPromotionGeneral.setPpFlag(ObjectUtil.isEmpty(channelPromotionGeneral.getPpFlag()) ? 1 : channelPromotionGeneral.getPpFlag());
            ChannelPromotionPosition channelPromotionPosition = channelPromotionPositionService.getOne(new QueryWrapper<ChannelPromotionPosition>().lambda()
                    .eq(ChannelPromotionPosition::getChannelId, channelDB.getChannelId())
                    .eq(channelPromotionGeneral.getPpFlag() == 2, ChannelPromotionPosition::getSubChannelId, channelPromotionGeneral.getSubChannelId())
                    .eq(ChannelPromotionPosition::getPpName, channelPromotionGeneral.getPpName())
                    .eq(ChannelPromotionPosition::getPpFlag, channelPromotionGeneral.getPpFlag())
            );
            if (ObjectUtil.isEmpty(channelPromotionPosition)) {
                throw new BusinessException("???????????????????????????ID:" + channelDB.getChannelId() + (channelPromotionGeneral.getPpFlag() == 2 ? "????????????ID:" + channelPromotionGeneral.getSubChannelId() : "") + ", ???????????????:" + channelPromotionGeneral.getPpName() + ", ???????????????:" + channelPromotionGeneral.getPpFlag());
            } else {
                channelPromotionGeneral.setPpId(channelPromotionPosition.getPpId());
            }
        }
    }

    public void defaultParam(ChannelPromotionGeneral channelPromotionGeneral) {
        //???????????????
        List<UserVO> userVOList = exportDataService.getUser();
        Map<String, UserVO> userVOMap = userVOList.stream().collect(Collectors.toMap(s -> s.getCardNumber(), s -> s));
        if (!userVOMap.containsKey(String.valueOf(channelPromotionGeneral.getUserid()))) {
            throw new BusinessException("??????????????????");
        } else {
            UserVO userVO = userVOMap.get(String.valueOf(channelPromotionGeneral.getUserid()));
            channelPromotionGeneral.setUserid(Long.valueOf(userVO.getId()));
            channelPromotionGeneral.setUsername(userVO.getCnname());
        }

        //??????CODE
        List<String> resultList = channelProductService.departmentCodeAndNameVaild(channelPromotionGeneral.getDepartmentCode(), channelPromotionGeneral.getDepartmentName());
        channelPromotionGeneral.setDepartmentCode(resultList.get(0));
        channelPromotionGeneral.setDepartmentName(resultList.get(1));

        //????????????
        if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getBusinessDictId())) {
            BusinessDict businessDict = businessDictService.getOne(new LambdaQueryWrapper<BusinessDict>().eq(BusinessDict::getId, channelPromotionGeneral.getBusinessDictId()));
            if (ObjectUtil.isEmpty(businessDict)) {
                throw new BusinessException("?????????????????????");
            } else if (!businessDict.getDepartmentCode().equals(channelPromotionGeneral.getDepartmentCode())
                    || !(businessDict.getYearStart() <= DateUtil.year(new Date()) && DateUtil.year(new Date()) <= businessDict.getYearEnd())
                    || ((org.apache.commons.lang.StringUtils.isNotBlank(channelPromotionGeneral.getFirstLevelBusiness())
                    && org.apache.commons.lang.StringUtils.isNotBlank(channelPromotionGeneral.getSecondLevelBusiness())
                    && org.apache.commons.lang.StringUtils.isNotBlank(channelPromotionGeneral.getThirdLevelBusiness()))
                    && (!channelPromotionGeneral.getFirstLevelBusiness().equals(businessDict.getFirstLevel())
                    || !channelPromotionGeneral.getSecondLevelBusiness().equals(businessDict.getSecondLevel())
                    || !channelPromotionGeneral.getThirdLevelBusiness().equals(businessDict.getThirdLevel())))) {
                throw new BusinessException("????????????ID??????????????????????????????");
            } else {
                channelPromotionGeneral.setFirstLevelBusiness(businessDict.getFirstLevel());
                channelPromotionGeneral.setSecondLevelBusiness(businessDict.getSecondLevel());
                channelPromotionGeneral.setThirdLevelBusiness(businessDict.getThirdLevel());
            }

        } else {
            List<BusinessDict> businessDictList = businessDictService.list(new QueryWrapper<BusinessDict>().lambda()
                    .eq(BusinessDict::getDepartmentCode, channelPromotionGeneral.getDepartmentCode())
                    .le(BusinessDict::getYearStart, DateUtil.year(new Date()))
                    .ge(BusinessDict::getYearEnd, DateUtil.year(new Date()))
                    .eq(BusinessDict::getFirstLevel, channelPromotionGeneral.getFirstLevelBusiness())
                    .eq(BusinessDict::getSecondLevel, channelPromotionGeneral.getSecondLevelBusiness())
                    .eq(BusinessDict::getThirdLevel, channelPromotionGeneral.getThirdLevelBusiness())
                    .eq(BusinessDict::getIsValid, 1)
            );
            if (businessDictList.size() > 1) {
                throw new BusinessException("?????????????????????");
            } else if (businessDictList.size() == 0) {
                throw new BusinessException("?????????????????????");
            } else {
                channelPromotionGeneral.setBusinessDictId(businessDictList.get(0).getId());
            }
        }
        //????????????ProductId ??? ApplicationID
        ChannelProduct channelProductDB = channelProductService.getOne(new LambdaQueryWrapper<ChannelProduct>().eq(ChannelProduct::getProductCode, channelPromotionGeneral.getProductCode()));
        if (ObjectUtil.isEmpty(channelProductDB)) {
            throw new BusinessException("??????Code????????????" + channelPromotionGeneral.getProductCode());
        }

        ChannelProduct channelProduct = channelProductService.getOneByParam(channelPromotionGeneral.getDepartmentCode(), channelPromotionGeneral.getProductCode());
        if (ObjectUtil.isEmpty(channelProduct)) {
            throw new BusinessException("??????CODE???" + channelPromotionGeneral.getProductCode() + "??? ?????????????????????????????????????????????" + channelPromotionGeneral.getDepartmentName() + "???" + channelPromotionGeneral.getDepartmentCode() + "???");
        } else {
            channelPromotionGeneral.setProductId(channelProduct.getProductId());
            channelPromotionGeneral.setProductName(channelProduct.getProductName());
        }
        if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getApplicationId())) {
            //??????
            ChannelApplication param = new ChannelApplication();
            param.setProductCodeList(Arrays.asList(channelProduct.getProductCode()));
            param.setId(channelPromotionGeneral.getApplicationId());
            List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectByProductNameAndAppName(param);
            if (ObjectUtil.isEmpty(channelApplicationList)) {
                throw new BusinessException("??????????????????ApplicationId:" + channelPromotionGeneral.getApplicationId());
            } else if (channelApplicationList.size() > 1) {
                throw new BusinessException("?????????????????????ApplicationId:" + channelPromotionGeneral.getApplicationId());
            } else {
                channelPromotionGeneral.setApplicationId(channelApplicationList.get(0).getId());
                channelPromotionGeneral.setApplicationName(channelApplicationList.get(0).getApplicationName());
            }
        } else {
            //????????????
            //List<ChannelApplication> channelApplicationList = channelApplicationService.list(new QueryWrapper<ChannelApplication>().lambda().eq(ChannelApplication::getProductCode, channelProduct.getProductCode()));
            if (StringUtils.isNotBlank(channelProduct.getApplicationIds())) {
                throw new BusinessException("???????????????????????????????????????");
            }
        }

        if (StringUtils.isNotBlank(channelPromotionGeneral.getCcidSettlement())) {
            // ??????CCID???????????????????????????????????????????????????????????? ????????????CCID
            ChannelPromotionPageParam settlementCCIDParam = new ChannelPromotionPageParam();
            settlementCCIDParam.setDepartmentCode(channelProduct.getDepartmentCode());
            settlementCCIDParam.setSettlementType("1");
            settlementCCIDParam.setCcid(channelPromotionGeneral.getCcidSettlement());
            List<ChannelCooperation> cooperationList = channelPromotionMapper.settlementCCIDList(settlementCCIDParam);
            if (CollectionUtil.isEmpty(cooperationList)) {
                throw new BusinessException("??????CCID?????????");
            } else {
                channelPromotionGeneral.setChannelIdSettlement(cooperationList.get(0).getChannelId());
                channelPromotionGeneral.setChannelNameSettlement(cooperationList.get(0).getChannelName());
            }
        }

        //????????????
        if (ObjectUtil.isEmpty(channelPromotionGeneral.getChannelRate())) {
            channelPromotionGeneral.setChannelRate(BigDecimal.ZERO);
        }
        //????????????
        if (ObjectUtil.isEmpty(channelPromotionGeneral.getPidNum()) || channelPromotionGeneral.getPidNum() == 0) {
            channelPromotionGeneral.setPidNum(1);
        }
        if ("".equals(channelPromotionGeneral.getApplicationId())) {
            channelPromotionGeneral.setApplicationId(null);
        }
    }

    public void emptyParam(ChannelPromotionGeneral channelPromotionGeneral) {
        List<String> errorMsg = new ArrayList<String>();

        if (StringUtils.isBlank(channelPromotionGeneral.getDepartmentName()) && StringUtils.isBlank(channelPromotionGeneral.getDepartmentCode())) {
            errorMsg.add("??????CODE?????????????????????????????????");
        }
        if (ObjectUtil.isEmpty(channelPromotionGeneral.getBusinessDictId())
                && (StringUtils.isBlank(channelPromotionGeneral.getFirstLevelBusiness()) || StringUtils.isBlank(channelPromotionGeneral.getSecondLevelBusiness()) || StringUtils.isBlank(channelPromotionGeneral.getThirdLevelBusiness()))
        ) {
            errorMsg.add("????????????ID ??? ???????????????????????? ??????????????????");
        }
        if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getBusinessDictId())
                && (StringUtils.isNotBlank(channelPromotionGeneral.getFirstLevelBusiness()) || StringUtils.isNotBlank(channelPromotionGeneral.getSecondLevelBusiness()) || StringUtils.isNotBlank(channelPromotionGeneral.getThirdLevelBusiness()))
        ) {
            errorMsg.add("????????????ID ??? ???????????????????????? ??????????????????");
        }
        if (ObjectUtil.isEmpty(channelPromotionGeneral.getChannelId()) && StringUtils.isBlank(channelPromotionGeneral.getChannelName())) {
            errorMsg.add("??????ID?????????????????????????????????");
        }
        if (StringUtils.isBlank(channelPromotionGeneral.getChargeRule())) {
            errorMsg.add("??????????????????");
        }
        if (StringUtils.isBlank(channelPromotionGeneral.getSubChannelId()) && StringUtils.isBlank(channelPromotionGeneral.getSubChannelName())) {
            errorMsg.add("?????????ID????????????????????????????????????");
        }
        if (StringUtils.isBlank(channelPromotionGeneral.getProductCode())) {
            errorMsg.add("??????CODE??????");
        } else if (!channelPromotionGeneral.getProductCode().matches("^[A-Z0-9]+$")) {
            errorMsg.add("??????CODE??????????????????????????????");
        }
        if (StringUtils.isBlank(channelPromotionGeneral.getPidAlias())) {
            errorMsg.add("??????????????????");
        }
        if (ObjectUtil.isNull(channelPromotionGeneral.getUserid())) {
            errorMsg.add("?????????????????????");
        }
        if (StringUtils.isBlank(channelPromotionGeneral.getChannelType())) {
            channelPromotionGeneral.setChannelType("2");
        } else {
            ChannelTypeEnum channelTypeEnum = ChannelTypeEnum.getByKey(Integer.valueOf(channelPromotionGeneral.getChannelType()));
            if (ObjectUtil.isEmpty(channelTypeEnum)) {
                errorMsg.add("???????????????????????????");
            }
        }

        if (ObjectUtil.isEmpty(channelPromotionGeneral.getSecretType())) {
            channelPromotionGeneral.setSecretType(1);
        } else {
            SecretTypeEnum secretTypeEnum = SecretTypeEnum.getByKey(channelPromotionGeneral.getSecretType());
            if (ObjectUtil.isEmpty(secretTypeEnum)) {
                errorMsg.add("???????????????????????????");
            }
        }

        if (StringUtils.isBlank(channelPromotionGeneral.getSettlementType())) {
            channelPromotionGeneral.setSettlementType("2");
        } else {
            SettlementTypeEnum settlementTypeEnum = SettlementTypeEnum.getByKey(Integer.valueOf(channelPromotionGeneral.getSettlementType()));
            if (ObjectUtil.isEmpty(settlementTypeEnum)) {
                errorMsg.add("???????????????????????????");
            }
        }

        if (StringUtils.isNotBlank(channelPromotionGeneral.getChannelShareType())) {
            ChannelShareTypeEnum channelShareTypeEnum = ChannelShareTypeEnum.getByKey(Integer.valueOf(channelPromotionGeneral.getChannelShareType()));
            if (ObjectUtil.isEmpty(channelShareTypeEnum)) {
                errorMsg.add("???????????????????????????");
            }
        }

        if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getPpFlag())) {
            PPTypeEnum ppTypeEnum = PPTypeEnum.getByKey(channelPromotionGeneral.getPpFlag());
            if (ObjectUtil.isEmpty(ppTypeEnum)) {
                errorMsg.add("??????????????????????????????");
            }
        }

        if (channelPromotionGeneral.getPidNum() == null || channelPromotionGeneral.getPidNum() == 0) {
            channelPromotionGeneral.setPidNum(1);
        } else if (channelPromotionGeneral.getPidNum() > 1000) {
            errorMsg.add("??????????????????1000???");
        }

        //chargeRule    channelShareType   price   channelShare    channelShareStep
        if (StringUtils.isNotBlank(channelPromotionGeneral.getChargeRule())) {
            List<String> one = Arrays.asList("CPS", "CPA");
            List<String> two = Arrays.asList("CPD", "CPM", "eCPM", "CPC", "CPT");
            List<String> three = Arrays.asList("CPL", "???????????????", "???????????????", "?????????");
            if (one.contains(channelPromotionGeneral.getChargeRule())) {
                if (StringUtils.isBlank(channelPromotionGeneral.getChannelShareType())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "?????????????????????");
                }
                if (("CPS".equals(channelPromotionGeneral.getChargeRule()) && !Arrays.asList("1", "2").contains(channelPromotionGeneral.getChannelShareType())) || ("CPA".equals(channelPromotionGeneral.getChargeRule()) && !Arrays.asList("3", "4").contains(channelPromotionGeneral.getChannelShareType()))) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "??????????????????????????????");
                }
                if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getChannelShare()) && StringUtils.isNotBlank(channelPromotionGeneral.getChannelShareStep())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "??????????????????????????????????????????????????????");
                }
                if (ObjectUtil.isEmpty(channelPromotionGeneral.getChannelShare()) && StringUtils.isBlank(channelPromotionGeneral.getChannelShareStep())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "??????????????????????????????????????????????????????");
                }
                if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getPrice())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "???????????????????????????");
                }
            } else if (two.contains(channelPromotionGeneral.getChargeRule())) {
                if (StringUtils.isNotBlank(channelPromotionGeneral.getChannelShareType())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "?????????????????????????????????");
                }
                if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getChannelShare()) || StringUtils.isNotBlank(channelPromotionGeneral.getChannelShareStep())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "????????????????????????????????????????????????");
                }
                if (ObjectUtil.isEmpty(channelPromotionGeneral.getPrice())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "???????????????");
                }
            } else if (three.contains(channelPromotionGeneral.getChargeRule())) {
                if (StringUtils.isNotBlank(channelPromotionGeneral.getChannelShareType())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "?????????????????????????????????");
                }
                if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getChannelShare()) || StringUtils.isNotBlank(channelPromotionGeneral.getChannelShareStep())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "??????????????????????????????????????????????????????");
                }
                if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getPrice())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "?????????????????????");
                }
            } else {
                errorMsg.add("???????????????????????????");
            }
        }
        if (errorMsg.size() > 0) {
            throw new BusinessException(String.join(",", errorMsg));
        }
    }

    @Override
    public List<ChannelPromotion> getChannelPromotionGeneral(ChannelPromotionPageParam channelPromotionPageParam) {
        List<ChannelPromotion> channelPromotionList = channelPromotionMapper.getChannelPromotionGeneral(channelPromotionPageParam);

        List<UserVO> userVOList = exportDataService.getUser();
        Map<Long, Long> userVOMap = userVOList.stream().collect(Collectors.toMap(t -> Long.valueOf(t.getId()), s -> Long.valueOf(s.getCardNumber())));

        if (channelPromotionList.size() > 1) {
            List<String> subChannelIdList = channelPromotionList.stream().map(ChannelPromotion::getSubChannelId).collect(Collectors.toList());
            List<String> productCodeList = channelPromotionList.stream().map(ChannelPromotion::getProductCode).collect(Collectors.toList());
            List<Long> applicationIdList = channelPromotionList.stream().filter(t -> ObjectUtil.isNotEmpty(t.getApplicationId())).map(ChannelPromotion::getApplicationId).collect(Collectors.toList());
            Set<String> mediumIdStrList = channelPromotionList.stream().filter(t -> StringUtils.isNotBlank(t.getMediumId())).map(ChannelPromotion::getMediumId).collect(Collectors.toSet());

            List<ChannelChild> channelChildList = new ArrayList<ChannelChild>();
            if (CollectionUtil.isNotEmpty(subChannelIdList)) {
                channelChildList = channelChildService.list(new QueryWrapper<ChannelChild>().lambda().in(ChannelChild::getSubChannelId, subChannelIdList));
            }
            List<ChannelProduct> channelProductList = new ArrayList<ChannelProduct>();
            if (CollectionUtil.isNotEmpty(productCodeList)) {
                channelProductList = channelProductService.list(new QueryWrapper<ChannelProduct>().lambda().in(ChannelProduct::getProductCode, productCodeList));
            }
            List<ChannelApplication> channelApplicationList = new ArrayList<ChannelApplication>();
            if (CollectionUtil.isNotEmpty(applicationIdList)) {
                channelApplicationList = channelApplicationService.list(new QueryWrapper<ChannelApplication>().lambda().in(ChannelApplication::getId, applicationIdList));
            }

            List<Long> mediumIdList = mediumIdStrList.stream().distinct().map(m -> StrUtil.split(m, ",")).flatMap(Arrays::stream).filter(StrUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList());
            List<ChannelMedium> channelMediumList = CollectionUtil.isNotEmpty(mediumIdList) ? channelMediumService.list(new LambdaQueryWrapper<ChannelMedium>().in(ChannelMedium::getId, mediumIdList)) : Collections.emptyList();
            Map<Long, String> mediumIdToName = channelMediumList.stream().collect(Collectors.toMap(ChannelMedium::getId, ChannelMedium::getName));

            Map<String, ChannelChild> channelChildMap = channelChildList.stream().collect(Collectors.toMap(ChannelChild::getSubChannelId, s -> s));
            Map<String, ChannelProduct> channelProductMap = channelProductList.stream().collect(Collectors.toMap(ChannelProduct::getProductCode, s -> s));
            Map<Long, ChannelApplication> channelApplicationMap = channelApplicationList.stream().collect(Collectors.toMap(ChannelApplication::getId, s -> s));

            for (ChannelPromotion channelPromotion : channelPromotionList) {
                if (channelChildMap.containsKey(channelPromotion.getSubChannelId())) {
                    channelPromotion.setSubChannelName(channelChildMap.get(channelPromotion.getSubChannelId()).getSubChannelName());
                }
                if (channelProductMap.containsKey(channelPromotion.getProductCode())) {
                    channelPromotion.setProductName(channelProductMap.get(channelPromotion.getProductCode()).getProductName());
                }
                if (ObjectUtil.isNotEmpty(channelPromotion.getApplicationId()) && channelApplicationMap.containsKey(channelPromotion.getApplicationId())) {
                    channelPromotion.setApplicationName(channelApplicationMap.get(channelPromotion.getApplicationId()).getApplicationName());
                }
                if (userVOMap.containsKey(channelPromotion.getUserid())) {
                    channelPromotion.setUserid(userVOMap.get(channelPromotion.getUserid()));
                }
                if (StringUtils.isNotBlank(channelPromotion.getMediumId())) {
                    List<String> strings = Arrays.stream(channelPromotion.getMediumId().split(",")).map(id -> mediumIdToName.get(Long.parseLong(id))).filter(i -> StringUtils.isNotBlank(i)).collect(Collectors.toList());
                    channelPromotion.setMediumName(StrUtil.join(",", strings));
                }
            }
        }

        return channelPromotionList;
    }

    @Override
    public List<Map<String, String>> countPidByMedium(ChannelMediumPageParam channelMediumPageParam, UserEntity user) {
        return channelPromotionMapper.countPidByMedium(channelMediumPageParam, user);
    }

    @Override
    public List<Channel> settlementChannelList(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest httpServletRequest) {
        channelPromotionPageParam.setDepartmentCode(null);

        UserEntity user = BiSessionUtil.build(this.redisTemplate, httpServletRequest).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelPromotionPageParam.setDepartmentCodeAllList(departmentCodeAllList);

        channelPromotionPageParam.setSettlementType("1");
        List<Channel> channelList = channelPromotionMapper.settlementChannelList(channelPromotionPageParam);

        if (StringUtils.isNotBlank(channelPromotionPageParam.getPid())) {
            Map<String, Channel> channelMap = channelList.stream().collect(Collectors.toMap(s -> String.valueOf(s.getChannelId()), s -> s));

            ChannelPromotion channelPromotion = channelPromotionMapper.selectOne(new LambdaQueryWrapper<ChannelPromotion>().eq(ChannelPromotion::getPid, channelPromotionPageParam.getPid()));
            String ccidSettlement = channelPromotion.getCcidSettlement();
            if (StringUtils.isNotBlank(ccidSettlement)) {
                ChannelCooperation channelCooperation = channelCooperationMapper.selectOne(new LambdaQueryWrapper<ChannelCooperation>().eq(ChannelCooperation::getCcid, ccidSettlement));
                String channelId = String.valueOf(channelCooperation.getChannelId());
                if (!channelMap.containsKey(channelId)) {
                    Channel channel = channelService.getOne(new LambdaQueryWrapper<Channel>().eq(Channel::getChannelId, channelId));
                    if (ObjectUtil.isNotEmpty(channel)) {
                        channelList.add(channel);
                    }

                }
            }
        }

        return channelList;
    }

    @Override
    public List<ChannelCooperation> settlementCCIDList(ChannelPromotionPageParam channelPromotionPageParam) {
        channelPromotionPageParam.setSettlementType("1");
        return channelPromotionMapper.settlementCCIDList(channelPromotionPageParam);
    }

    private String getChargeRule(String chargeRule) {
        Map<String, String> map = MapUtil.<String, String>builder()
                .put("?????????CPS", "CPS").put("???A", "NOA").put("?????????", "ZRL").put("eCPM", "CPM")
                .put("???????????????", "OTR").put("??????", "OTR").put("??????CPS", "CPS").put("???????????????", "OTR")
                .build();
        if (map.containsKey(chargeRule)) {
            chargeRule = map.get(chargeRule);
        } else if (StrUtil.length(chargeRule) == 3 && check(chargeRule)) {
            chargeRule = chargeRule;
        } else {
            chargeRule = "OTR";
        }
        return chargeRule;
    }

    private boolean check(String strData) {
        char c = strData.charAt(0);
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            return true;
        } else {
            return false;
        }
    }

    /*public String getJXStep(String channelShareStep) {
        List<String> shareStep = Arrays.asList(channelShareStep.split("\\n"));

        List<Map<String, Object>> shareStepJsonAll = new ArrayList<Map<String, Object>>();
        for (String sharestr : shareStep) {
            String[] share = sharestr.split("\\|");
            Map<String, Object> map = new HashMap<String, Object>();

            String share1 = share[0];
            String[] share1Sub = share1.split("???|???");
            List<String> numList = new ArrayList<String>();
            numList.add(share1Sub[0]);
            if (share1Sub.length == 2) {
                numList.add("");
            } else {
                numList.add(share1Sub[2]);
            }
            map.put("num", numList);

            String share2 = share[1];
            map.put("share", share2);

            shareStepJsonAll.add(map);
        }

        return JSON.toJSONString(shareStepJsonAll);
    }*/

    @Override
    public Boolean saveChannelPromotionBatch(List<ChannelPromotion> channelPromotionList, HttpServletRequest request) throws Exception {
        logger.info("===================>????????????????????????" + channelPromotionList.size() + "???");

        List errorSubChannel = new ArrayList();
        List errorPp = new ArrayList();
        List errorProduct = new ArrayList();
        List errorApplication = new ArrayList();
        List errorApplicationEmpty = new ArrayList();
        List errorMediumName = new ArrayList();
        List errorCCIDSettlement = new ArrayList();

        String errorMsg = validationFormat(channelPromotionList);
        if (StringUtils.isNotBlank(errorMsg)) {
            throw new BusinessException(errorMsg.toString());
        }

        String ccid = channelPromotionList.get(0).getCcid();
        ChannelCooperation channelCooperation = channelCooperationService.getOne(new QueryWrapper<ChannelCooperation>().lambda().eq(ChannelCooperation::getCcid, ccid));
        if (channelCooperation == null) {
            throw new BusinessException("?????????????????????");
        }

        //?????????
        List<String> subChannelNameByExcel = channelPromotionList.stream().map(ChannelPromotion::getSubChannelName).distinct().collect(Collectors.toList());
        List<ChannelChild> subChannelListByDB = channelChildService.list(new QueryWrapper<ChannelChild>().lambda().select(ChannelChild::getSubChannelId, ChannelChild::getSubChannelName)
                .in(ChannelChild::getSubChannelName, subChannelNameByExcel)
                .eq(ChannelChild::getChannelId, channelCooperation.getChannelId())
        );
        Map<String, String> subChannelMapByDB = subChannelListByDB.stream().collect(Collectors.toMap(item -> item.getSubChannelName(), item -> item.getSubChannelId()));
        List<String> subChannelIdListByDB = subChannelListByDB.stream().map(ChannelChild::getSubChannelId).collect(Collectors.toList());

        //??????????????????????????????
        List<ChannelPromotionPosition> channelChildPPCount = new ArrayList<ChannelPromotionPosition>();
        if (subChannelIdListByDB.size() > 0) {
            channelChildPPCount = channelPromotionPositionService.list(new QueryWrapper<ChannelPromotionPosition>().select("sub_channel_id", "count(*) subChannelPPIDNumber").lambda()
                    .in(ChannelPromotionPosition::getSubChannelId, subChannelIdListByDB)
                    .eq(ChannelPromotionPosition::getPpFlag, 2)
                    .eq(ChannelPromotionPosition::getPpStatus, 1)
                    .groupBy(ChannelPromotionPosition::getSubChannelId)
            );
        }
        Map<String, Long> ccTemp = channelChildPPCount.stream().collect(Collectors.toMap(item -> item.getSubChannelId(), item -> item.getSubChannelPPIDNumber()));

        //?????????
        List<String> cppNameByExcel = channelPromotionList.stream().filter(i -> StringUtils.isNotBlank(i.getPpName())).map(ChannelPromotion::getPpName).distinct().collect(Collectors.toList());
        logger.info("===================>?????????????????????????????????" + JSON.toJSONString(cppNameByExcel));
        List<ChannelPromotionPosition> cppListByZQDDB = new ArrayList<ChannelPromotionPosition>();
        List<ChannelPromotionPosition> cppListByQDDB = new ArrayList<ChannelPromotionPosition>();
        if (cppNameByExcel.size() > 0) {
            cppListByQDDB = channelPromotionPositionService.list(new QueryWrapper<ChannelPromotionPosition>().lambda()
                    .in(ChannelPromotionPosition::getPpName, cppNameByExcel)
                    .eq(ChannelPromotionPosition::getChannelId, channelCooperation.getChannelId())
                    .eq(ChannelPromotionPosition::getPpStatus, 1)
                    .eq(ChannelPromotionPosition::getPpFlag, 1)
            );
            logger.info("===================>?????????????????????????????????" + cppListByQDDB + "???");
            cppListByZQDDB = channelPromotionPositionService.list(new QueryWrapper<ChannelPromotionPosition>().lambda()
                    .in(ChannelPromotionPosition::getPpName, cppNameByExcel)
                    .eq(ChannelPromotionPosition::getChannelId, channelCooperation.getChannelId())
                    .eq(ChannelPromotionPosition::getPpStatus, 1)
                    .eq(ChannelPromotionPosition::getPpFlag, 2)
            );
            logger.info("===================>????????????????????????????????????" + cppListByZQDDB + "???");
        }

        // ????????????
        //1. ?????? ??????CODE, ????????????
        Map<String, String> productByExcel = channelPromotionList.stream().collect(Collectors.toMap(item -> item.getProductCode(), item -> item.getProductName(), (v1, v2) -> v1));
        String departmentCode = channelCooperation.getDepartmentCode();
        //???????????????????????????????????? ??????/???????????? ?????? ????????????
        List<ChannelProduct> productListByDB = channelProductService.getOneByParamList(departmentCode, new ArrayList<String>(productByExcel.keySet()));
        Map<String, String> productMap = productListByDB.stream().collect(Collectors.toMap(item -> item.getProductCode(), item -> item.getProductName()));
        //???????????? ?????????/???????????? ???????????? ????????????????????????
        productByExcel.forEach((k, v) -> {
            if (!productMap.containsKey(k) || !v.equals(productMap.get(k))) {
                errorProduct.add(v + "???" + k + "???");
            }
        });

        // ??????CCID
        Map<String, List<ChannelCooperation>> settlementCooperationMap = new HashMap<String, List<ChannelCooperation>>();
        if (CollectionUtil.isEmpty(errorProduct)) {
            List<String> productCodeList = channelPromotionList.stream().map(ChannelPromotion::getProductCode).filter(i -> StringUtils.isNotBlank(i)).collect(Collectors.toList());
            List<String> ccidSettlementList = channelPromotionList.stream().map(ChannelPromotion::getCcidSettlement).filter(i -> StringUtils.isNotBlank(i)).collect(Collectors.toList());
            // ?????? ??????CCID ??????
            if (CollectionUtil.isNotEmpty(ccidSettlementList)) {
                // ?????? ????????????????????? ???????????? ????????? CCID
                List<ChannelCooperation> channelCooperationList = channelService.getSettleCCIDByProd(productCodeList);
                settlementCooperationMap = channelCooperationList.stream().collect(Collectors.groupingBy(ChannelCooperation::getProductCode));
            }
        }

        // ??????
        List<ChannelProduct> channelProductList = channelProductService.list(new LambdaQueryWrapper<ChannelProduct>().in(ChannelProduct::getProductCode, productByExcel.keySet()));

        String applicationIds = channelProductList.stream().filter(i -> StringUtils.isNotBlank(i.getApplicationIds())).map(ChannelProduct::getApplicationIds).collect(Collectors.joining(","));
        List<ChannelApplication> channelApplicationList = new ArrayList<ChannelApplication>();
        if (StringUtils.isNotBlank(applicationIds)) {
            List<String> applicationIdList = new ArrayList<String>(Arrays.asList(applicationIds.split(",")));
            channelApplicationList = channelApplicationMapper.selectList(new LambdaQueryWrapper<ChannelApplication>().in(ChannelApplication::getId, applicationIdList));
        }

        Map<String, List<ChannelApplication>> channelApplicationMapByDB = new HashMap<String, List<ChannelApplication>>();
        for (ChannelProduct channelProduct : channelProductList) {
            if (StringUtils.isNotBlank(channelProduct.getApplicationIds())) {
                List<String> appIdList = new ArrayList<String>(Arrays.asList(channelProduct.getApplicationIds().split(",")));
                List<ChannelApplication> appTmp = channelApplicationList.stream().filter(i -> appIdList.contains(String.valueOf(i.getId()))).collect(Collectors.toList());

                channelApplicationMapByDB.put(channelProduct.getProductCode(), appTmp);
            }
        }
        /*List<ChannelApplication> channelApplicationListByDB = channelApplicationService.list(new QueryWrapper<ChannelApplication>().lambda()
                .in(ChannelApplication::getProductCode, productByExcel.keySet())
        );
        Map<String, List<ChannelApplication>> channelApplicationMapByDB = channelApplicationListByDB.stream().collect(Collectors.groupingBy(ChannelApplication::getProductCode));*/

        //??????????????????
        List<String> mediumNameListByExcel = channelPromotionList.stream().filter(i -> StringUtils.isNotBlank(i.getMediumName())).map(ChannelPromotion::getMediumName).distinct().collect(Collectors.toList());
        List<ChannelMedium> channelMediumListByDB = channelMediumService.list(new QueryWrapper<ChannelMedium>().lambda()
                .eq(ChannelMedium::getDepartmentCode, channelCooperation.getDepartmentCode())
        );
        Map<String, ChannelMedium> channelMediumMap = channelMediumListByDB.stream().collect(Collectors.toMap(item -> item.getName(), item -> item));
        for (String mediumName : mediumNameListByExcel) {
            String[] mediums = mediumName.split(",");
            for (String medium : mediums) {
                if (!channelMediumMap.containsKey(medium)) {
                    errorMediumName.add(medium);
                }
            }
        }

        List<String> productCodeList = channelPromotionList.stream().map(ChannelPromotion::getProductCode).collect(Collectors.toList());
        Map<String, String> productCodeToIdMap = channelProductService.list(new QueryWrapper<ChannelProduct>().lambda().in(ChannelProduct::getProductCode, productCodeList)).stream().collect(Collectors.toMap(ChannelProduct::getProductCode, s -> s.getProductId()));

        List<ChannelPromotion> channelPromotionAll = new ArrayList<ChannelPromotion>();
        Map<String, Integer> pidAliasMap = new HashMap<String, Integer>();
        for (ChannelPromotion channelPromotion : channelPromotionList) {
            //???????????????
            //String subChannelId = channelPromotion.getSubChannelId();
            String subChannelName = channelPromotion.getSubChannelName();
            if (!subChannelMapByDB.containsKey(subChannelName)) {
                errorSubChannel.add(subChannelName);
            } else {
                String subChannelId = subChannelMapByDB.get(subChannelName);
                channelPromotion.setSubChannelId(subChannelId);

                //?????????????????????????????????????????????
                if (StringUtils.isNotBlank(channelPromotion.getPpName())) {
                    logger.info("===================>????????????????????????" + channelPromotion.getPpName());
                    //?????????????????????????????????????????????????????????????????????????????????
                    if (ccTemp.containsKey(subChannelId) && ccTemp.get(subChannelId) > 0) {
                        Map<String, ChannelPromotionPosition> cppMap = cppListByZQDDB.stream().collect(Collectors.toMap(item -> item.getPpName() + item.getPpFlag(), item -> item));
                        String key = channelPromotion.getPpName() + 2;
                        logger.info("===================>????????????????????????????????????????????????" + cppMap.keySet() + "?????????key?????????" + key);
                        //????????????????????????PPID
                        if (!cppMap.containsKey(key)) {
                            errorPp.add(channelPromotion.getPpName());
                        } else {
                            ChannelPromotionPosition channelPromotionPosition = cppMap.get(key);
                            channelPromotion.setPpId(channelPromotionPosition.getPpId());
                            channelPromotion.setPpName(channelPromotionPosition.getPpName());
                        }
                    } else { //?????????????????????????????????????????????????????????????????????????????????
                        Map<String, ChannelPromotionPosition> cppMap = cppListByQDDB.stream().collect(Collectors.toMap(item -> item.getPpName() + item.getPpFlag(), item -> item));
                        String key = channelPromotion.getPpName() + 1;
                        logger.info("===================>?????????????????????????????????????????????" + cppMap.keySet() + "?????????key?????????" + key);
                        //????????????????????????PPID
                        if (!cppMap.containsKey(key)) {
                            errorPp.add(channelPromotion.getPpName());
                        } else {
                            ChannelPromotionPosition channelPromotionPosition = cppMap.get(key);
                            channelPromotion.setPpId(channelPromotionPosition.getPpId());
                            channelPromotion.setPpName(channelPromotionPosition.getPpName());
                        }
                    }
                }
            }

            //????????????
            String productCode = channelPromotion.getProductCode();
            List<ChannelApplication> channelApplicationsByDB = new ArrayList<ChannelApplication>();
            if (channelApplicationMapByDB.size() > 0) {
                if (channelApplicationMapByDB.containsKey(productCode)) {
                    channelApplicationsByDB = channelApplicationMapByDB.get(productCode);
                    List<String> applicationNameListByDB = channelApplicationsByDB.stream().map(ChannelApplication::getApplicationName).collect(Collectors.toList());
                    if (StringUtils.isBlank(channelPromotion.getApplicationName())) {
                        errorApplicationEmpty.add(channelPromotion.getProductName());
                    } else if (!applicationNameListByDB.contains(channelPromotion.getApplicationName())) {
                        errorApplication.add(channelPromotion.getApplicationName());
                    }
                }
            }

            // ????????????CCID
            String ccidSettlement = channelPromotion.getCcidSettlement();
            if (StringUtils.isNotBlank(ccidSettlement)) {
                if (settlementCooperationMap.containsKey(productCode)) {
                    List<ChannelCooperation> channelCooperationList = settlementCooperationMap.get(productCode);
                    Map<String, ChannelCooperation> channelCooperationMap = channelCooperationList.stream().collect(Collectors.toMap(i -> i.getCcid(), s -> s));
                    if (channelCooperationMap.containsKey(ccidSettlement)) {
                        /*ChannelCooperation channelCooperationTemp = channelCooperationMap.get(ccidSettlement);
                        channelPromotion.setChannelIdSettlement(channelCooperationTemp.getChannelId());
                        channelPromotion.setChannelNameSettlement(channelCooperationTemp.getChannelName());*/
                    } else {
                        errorCCIDSettlement.add(channelPromotion.getCcidSettlement());
                    }
                } else {
                    errorCCIDSettlement.add(channelPromotion.getCcidSettlement());
                }
            }

            if (errorSubChannel.size() == 0 && errorPp.size() == 0 && errorProduct.size() == 0 && errorApplication.size() == 0 && errorMediumName.size() == 0 && errorCCIDSettlement.size() == 0) {
                Map<String, ChannelApplication> channelApplicationMap = channelApplicationsByDB.stream().collect(Collectors.toMap(item -> item.getApplicationName(), item -> item));
                String applicationName = channelPromotion.getApplicationName();
                if (channelApplicationMap.containsKey(applicationName)) {
                    channelPromotion.setApplicationId(channelApplicationMap.get(applicationName).getId());
                }

                String pid = channelBaseIdService.getNewPID();
                channelPromotion.setPid(pid);

                Integer lastNum = 0;
                String pidName = channelPromotion.getPidAlias().trim();
                if (pidAliasMap.containsKey(pidName)) {
                    lastNum = pidAliasMap.get(pidName) + 1;
                } else {
                    lastNum = this.getLastNum(pidName); //by yifan 20211104
                }
                pidAliasMap.put(pidName, lastNum);
                channelPromotion.setPidAlias(lastNum == 0 ? pidName : pidName + "_" + lastNum);

                String mediumName = channelPromotion.getMediumName();
                if (StringUtils.isNotBlank(mediumName)) {
                    String[] mediums = mediumName.split(",");
                    StringBuilder sb = new StringBuilder();
                    for (String medium : mediums) {
                        ChannelMedium channelMedium = channelMediumMap.get(medium);

                        if (channelMedium != null) {
                            sb.append(channelMedium.getId()).append(",");
                        }
                    }
                    channelPromotion.setMediumId(sb.substring(0, sb.length() - 1));
                }

                channelPromotion.setCheckStartDate(channelPromotion.getCheckStartDate());
                channelPromotion.setCheckEndDate(channelPromotion.getCheckEndDate() == null ? DateUtils.parseDate("2099-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss") : channelPromotion.getCheckEndDate());

                if (productCodeToIdMap.containsKey(channelPromotion.getProductCode())) {
                    channelPromotion.setProductId(productCodeToIdMap.get(channelPromotion.getProductCode()));
                }

                channelPromotionAll.add(channelPromotion);
            }
        }

        StringBuilder sb = new StringBuilder();
        if (errorSubChannel.size() > 0) {
            sb.append("????????????????????????" + StringUtil.join(errorSubChannel.toArray(), ",") + "??????");
        }
        if (errorPp.size() > 0) {
            sb.append("????????????????????????" + StringUtil.join(errorPp.toArray(), ",") + "??????");
        }
        if (errorProduct.size() > 0) {
            sb.append("???????????????CODE??????????????????" + StringUtil.join(errorProduct.toArray(), ",") + "??????");
        }
        if (errorApplicationEmpty.size() > 0) {
            sb.append("??????????????????????????????????????????" + StringUtil.join(errorApplicationEmpty.toArray(), ",") + "??????");
        }
        if (errorApplication.size() > 0) {
            sb.append("?????????????????????" + StringUtil.join(errorApplication.toArray(), ",") + "??????");
        }
        if (errorMediumName.size() > 0) {
            sb.append("?????????????????????" + StringUtil.join(errorMediumName.toArray(), ",") + "??????");
        }
        if (errorCCIDSettlement.size() > 0) {
            sb.append("???????????????CCID??????" + StringUtil.join(errorCCIDSettlement.toArray(), ",") + "??????");
        }

        if (sb.length() > 0) {
            throw new BusinessException(sb.substring(0, sb.length() - 1).toString());
        }

        super.saveBatch(channelPromotionAll);

        // ??????YouTop + ?????????
        List<PostPidParam> postPidParam = new ArrayList<PostPidParam>();
        if (channelPromotionAll != null && channelPromotionAll.size() > 0) {
            channelPromotionAll = channelPromotionAll.stream().filter(i -> StringUtils.isNotBlank(i.getProductId())).collect(Collectors.toList());
            if (channelPromotionAll.size() > 0) {
                postPidParam = DozerUtil.toBeanList(channelPromotionAll, PostPidParam.class);
            }
        }
        channelPromotionAllService.addBatchThread(channelPromotionAll, youtopApiHost, postPidParam);

        return true;
    }

    private String validationFormat(List<ChannelPromotion> channelPromotionList) {
        StringBuilder sb = new StringBuilder();

        List<String> pidErrorAlias = channelPromotionList.stream().map(ChannelPromotion::getPidAlias)
                .filter(i -> !i.matches("[\\u4e00-\\u9fa5\\w-()?????? ]*") || i.startsWith(" ") || i.endsWith(" ")).distinct().collect(Collectors.toList());
        if (pidErrorAlias != null && pidErrorAlias.size() > 0) {
            sb.append("??????????????????????????????/??????/??????/??????/??????/?????????,??????????????????????????????").append(StringUtil.join(pidErrorAlias.toArray(), ",")).append("???");
        }

        List<String> productError = channelPromotionList.stream().map(ChannelPromotion::getProductCode).filter(i -> !isUpper(i)).distinct().collect(Collectors.toList());
        if (productError != null && productError.size() > 0) {
            sb.append("??????CODE?????????????????????").append(StringUtil.join(productError.toArray(), ",")).append("???");
        }


        return sb.toString();
    }

    /**
     * ???????????????????????????
     *
     * @param word
     * @return
     */
    public static Boolean isUpper(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!Character.isDigit(c) && Character.isLowerCase(c)) {
                return false;
            }
        }
        return true;
    }

    private String newSubChannel(ChannelPromotion channelPromotion) throws Exception {
        String ccid = channelPromotion.getCcid();
        ChannelCooperation channelCooperation = channelCooperationService.getOne(new QueryWrapper<ChannelCooperation>().select("channel_id").lambda().eq(ChannelCooperation::getCcid, ccid));
        if (ObjectUtil.isEmpty(channelCooperation)) {
            throw new BusinessException("CCID?????????");
        }
        Long channelId = channelCooperation.getChannelId();
        String subChannelId = channelBaseIdService.getNewSubChannelID(channelId);
        // update yf by 20210717(?????????)
        ChannelChild channelChild = new ChannelChild()
                .setCcid(ccid)
                .setSubChannelId(subChannelId)
                .setSubChannelName(channelPromotion.getSubChannelName())
                .setChannelId(channelId);
        if (StringUtils.isNotBlank(channelPromotion.getDataSource())) {
            channelChild.setDataSource(channelPromotion.getDataSource());
        }
        channelChildService.saveChannelChild(channelChild);
        return channelChild.getSubChannelId();
    }

    @Override
    public ChannelPromotionVO info(Long id) {
        ChannelPromotion channelPromotion = super.getById(id);

        String pidAliasByDB = channelPromotion.getPidAlias();
        if (StringUtils.isNotBlank(pidAliasByDB) && pidAliasByDB.contains("_")) {
            String[] pidAliass = pidAliasByDB.split("_");
            String pidAliasNum = pidAliass[pidAliass.length - 1];
            if (pidAliasNum.matches("[0-9]*")) {
                String pidAliasStr = pidAliasByDB.substring(0, pidAliasByDB.lastIndexOf(pidAliasNum) - 1);
                channelPromotion.setPidAliasStr(pidAliasStr);
            } else {
                channelPromotion.setPidAliasStr(pidAliasByDB);
            }
        } else {
            channelPromotion.setPidAliasStr(pidAliasByDB);
        }

        ChannelPromotionVO channelPromotionVO = DozerUtil.toBean(channelPromotion, ChannelPromotionVO.class);
        setBaseMsg(channelPromotionVO);

        //????????????
        String ccidSettlement = channelPromotionVO.getCcidSettlement();
        if (StringUtils.isNotBlank(ccidSettlement)) {
            ChannelCooperation channelCooperation = channelCooperationMapper.selectOne(new LambdaQueryWrapper<ChannelCooperation>().eq(ChannelCooperation::getCcid, ccidSettlement));
            channelPromotionVO.setChannelIdSettlement(channelCooperation.getChannelId());
            channelPromotionVO.setChannelNameSettlement(channelCooperation.getChannelName());
            channelPromotionVO.setAgentIdSettlement(channelCooperation.getAgentId());
            channelPromotionVO.setAgentNameSettlement(channelCooperation.getAgentName());
            channelPromotionVO.setChargeRuleSettlement(channelCooperation.getChargeRule());
            channelPromotionVO.setChannelRateSettlement(channelCooperation.getChannelRate());
            channelPromotionVO.setChannelShareSettlement(channelCooperation.getChannelShare());
            channelPromotionVO.setChannelShareStepSettlement(channelCooperation.getChannelShareStep());
            channelPromotionVO.setPriceSettlement(channelCooperation.getPrice());
            channelPromotionVO.setChannelShareTypeSettlement(channelCooperation.getChannelShareType());
            channelPromotionVO.setChannelShareTypeStrSettlement(StringUtils.isNotBlank(channelCooperation.getChannelShareType()) ? ChannelShareTypeEnum.getByKey(Integer.valueOf(channelCooperation.getChannelShareType())).getValue() : null);
        }

        return channelPromotionVO;
    }

    public void setBaseMsg(ChannelPromotionVO channelPromotionVO) {
        ChannelCooperation channelCooperation = channelCooperationService.getOne(new QueryWrapper<ChannelCooperation>().lambda().eq(StrUtil.isNotEmpty(channelPromotionVO.getCcid()), ChannelCooperation::getCcid, channelPromotionVO.getCcid()));
        if (null != channelCooperation) {
            channelPromotionVO.setAgentId(channelCooperation.getAgentId());
            channelPromotionVO.setAgentName(channelCooperation.getAgentName());
            channelPromotionVO.setChannelId(channelCooperation.getChannelId());
            channelPromotionVO.setChannelName(channelCooperation.getChannelName());
            channelPromotionVO.setDepartmentCode(channelCooperation.getDepartmentCode());
            channelPromotionVO.setDepartmentName(channelCooperation.getDepartmentName());
            channelPromotionVO.setFirstLevelBusiness(channelCooperation.getFirstLevelBusiness());
            channelPromotionVO.setSecondLevelBusiness(channelCooperation.getSecondLevelBusiness());
            channelPromotionVO.setThirdLevelBusiness(channelCooperation.getThirdLevelBusiness());
        }

        Channel channel = channelService.getOne(new LambdaQueryWrapper<Channel>().eq(Channel::getChannelId, channelCooperation.getChannelId()));
        if (ObjectUtil.isNotEmpty(channel)) {
            channelPromotionVO.setChannelDepartmentName(channel.getDepartmentName());
            channelPromotionVO.setSecretTypeStr(SecretTypeEnum.getByKey(channel.getSecretType()).getValue());
        }

        String subChannelId = channelPromotionVO.getSubChannelId();
        if (null != subChannelId) {
            ChannelChild channelChild = channelChildService.getOne(new QueryWrapper<ChannelChild>().lambda()
                    .eq(channelCooperation != null && channelCooperation.getChannelId() != null, ChannelChild::getChannelId, channelCooperation.getChannelId())
                    .eq(StringUtils.isNotBlank(channelPromotionVO.getSubChannelId()), ChannelChild::getSubChannelId, channelPromotionVO.getSubChannelId()));
            if (null != channelChild) {
                channelPromotionVO.setSubChannelName(channelChild.getSubChannelName());
            }
        }

        Long ppId = channelPromotionVO.getPpId();
        if (null != ppId) {
            ChannelPromotionPosition channelPromotionPosition = channelPromotionPositionService.getOne(new QueryWrapper<ChannelPromotionPosition>()
                    .lambda().eq(ObjectUtil.isNotNull(channelPromotionVO.getPpId()), ChannelPromotionPosition::getPpId, channelPromotionVO.getPpId()));
            if (null != channelPromotionPosition) {
                channelPromotionVO.setPromotionPositionName(channelPromotionPosition.getPpName());
            }
        }

        String mediumId = channelPromotionVO.getMediumId();
        if (StrUtil.isNotEmpty(mediumId)) {
            List<Long> ids = Arrays.stream(mediumId.split(",")).map(Long::parseLong).collect(Collectors.toList());
            List<String> strings = channelMediumService.listByIds(ids).stream().map(ChannelMedium::getName).collect(Collectors.toList());
            channelPromotionVO.setMediumName(StrUtil.join(",", strings));
        }

        if (StringUtils.isNotBlank(channelPromotionVO.getProductCode())) {
            ChannelProduct channelProduct = channelProductService.getOne(new QueryWrapper<ChannelProduct>().lambda()
                    .eq(StringUtils.isNotBlank(channelPromotionVO.getProductCode()), ChannelProduct::getProductCode, channelPromotionVO.getProductCode())
            );
            channelPromotionVO.setProductName(channelProduct.getProductName());
            channelPromotionVO.setProductDepartmentCode(channelProduct.getDepartmentCode());
            channelPromotionVO.setProductDepartmentName(channelProduct.getDepartmentName());
            if (channelPromotionVO.getApplicationId() != null) {
                ChannelApplication param = new ChannelApplication();
                param.setProductCodeList(Arrays.asList(channelProduct.getProductCode()));
                param.setId(Long.valueOf(channelPromotionVO.getApplicationId()));
                List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectByProductNameAndAppName(param);

                if (channelApplicationList.size() > 1) {
                    throw new BusinessException("?????????????????????ApplicationId:" + channelPromotionVO.getApplicationId());
                }

                /*channelApplicationService.getOne(new QueryWrapper<ChannelApplication>().lambda()
                        .eq(channelProduct != null, ChannelApplication::getProductCode, channelProduct.getProductCode())
                        .eq(channelPromotionVO.getApplicationId() != null, ChannelApplication::getId, channelPromotionVO.getApplicationId())
                );*/
                channelPromotionVO.setApplicationName(CollectionUtil.isNotEmpty(channelApplicationList) ? channelApplicationList.get(0).getApplicationName() : null);
            }
        }
    }

    @Override
    public ResultEntity<List<DepartmentVO>> listDepartment(ChannelPromotionPageParam channelPromotionPageParam) {
        List<DepartmentVO> departmentVOS = channelPromotionMapper.listDepartment(channelPromotionPageParam);
        return ResultEntity.success(departmentVOS);
    }

    @Override
    public ResultEntity<List<AgentVO>> listCompany(ChannelPromotionPageParam channelPromotionPageParam) {
        List<AgentVO> agentVOS = channelPromotionMapper.listAgent(channelPromotionPageParam);
        return ResultEntity.success(agentVOS);
    }

    @Override
    public ResultEntity<List<ChannelVO>> listChannel(ChannelPromotionPageParam channelPromotionPageParam) {
        List<ChannelVO> channelVOS = channelPromotionMapper.listChannel(channelPromotionPageParam);
        return ResultEntity.success(channelVOS);
    }

    @Override
    public ResultEntity<List<SubChannelVO>> listSubChannel(ChannelPromotionPageParam channelPromotionPageParam) {
        List<SubChannelVO> subChannelVOS = channelPromotionMapper.listSubChannel(channelPromotionPageParam);
        return ResultEntity.success(subChannelVOS);
    }

    @Override
    public ResultEntity<PidVO> getPidBusinessInfo(String pid) {

        ChannelPromotion channelPromotion = channelPromotionMapper.selectOne(new QueryWrapper<ChannelPromotion>().select("pid", "medium_id")
                .lambda().eq(ChannelPromotion::getPid, pid));
        if (null != channelPromotion && StrUtil.isNotEmpty(channelPromotion.getMediumId())) {
            List<Integer> mediumIds = Stream.of(StrUtil.split(channelPromotion.getMediumId(), ",")).map(Integer::parseInt).collect(Collectors.toList());
            String names = channelMediumService.listByIds(mediumIds).stream().map(ChannelMedium::getName).map(name -> {
                String business;
                switch (name) {
                    case "?????????":
                        business = "yilewan";
                        break;
                    case "?????????":
                        business = "yfy";
                        break;
                    case "??????":
                        business = "wanzhao";
                        break;
                    case "????????????":
                        business = "st";
                        break;
                    default:
                        business = null;
                }
                return business;
            }).collect(Collectors.joining(","));
            return ResultEntity.success(new PidVO(pid, names));
        }
        return ResultEntity.failure(null);
    }

    /**
     * 1??????????????????????????????CCID??????????????????????????????????????????????????????
     * ????????????????????????????????????????????????????????????(??????????????????????????????????????????????????????????????????????????????)
     * ??????????????????PID?????????????????????????????????ID???????????????????????????ID;
     * 2?????????CCID??????????????????????????????????????????PID???????????????/????????????????????????
     * ????????????????????? ??????$?????????????????????$????????????1???$????????????2?????????????????????????????????????????????
     * ??????$????????????????????????$????????????1???$????????????2???????????????
     * ???????????????
     * 1???pid?????????ccid????????????????????????ccid?????????????????????
     * 2?????????????????????????????????????????????????????????20991231
     * 3???????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param param
     * @throws Exception
     */
    @Override
    public void migration(ChannelPromotionPageParam param, HttpServletRequest request) throws Exception {
        UserEntity user = BiSessionUtil.build(redisTemplate, request).getSessionUser();
        if (param.getCcid().equals(param.getTargetCCId())) {
            throw new BusinessException("??????CCID??????CCID????????????");
        }

        //??????????????????ID????????????????????????CCID????????????ID
        if (param.getTargetChannelId() == null) {
            ChannelCooperation channelCooperation = channelCooperationService.getOne(new QueryWrapper<ChannelCooperation>().lambda().eq(ChannelCooperation::getCcid, param.getTargetCCId()));
            param.setTargetChannelId(channelCooperation.getChannelId());
        }

        //???Pid
        List<String> pidList = param.getPidList();
        List<ChannelPromotion> channelPromotionList = channelPromotionMapper.selectList(new QueryWrapper<ChannelPromotion>().lambda().in(ChannelPromotion::getPid, pidList));
        Map<String, ChannelPromotion> channelPromotionMap = channelPromotionList.stream().collect(Collectors.toMap(ChannelPromotion::getPid, c -> c));
        //???CCid
        List<String> ccidList = channelPromotionList.stream().map(ChannelPromotion::getCcid).collect(Collectors.toList());
        List<ChannelCooperation> channelCooperationList = channelCooperationService.list(new QueryWrapper<ChannelCooperation>().lambda().in(ChannelCooperation::getCcid, ccidList));
        Map<String, ChannelCooperation> channelCooperationMap = channelCooperationList.stream().collect(Collectors.toMap(ChannelCooperation::getCcid, c -> c));
        //????????????
        List<String> subChannelIdList = channelPromotionList.stream().map(ChannelPromotion::getSubChannelId).collect(Collectors.toList());
        List<ChannelChild> channelChildList = channelChildService.list(new QueryWrapper<ChannelChild>().lambda().in(ChannelChild::getSubChannelId, subChannelIdList));
        Map<String, ChannelChild> channelChildMap = channelChildList.stream().collect(Collectors.toMap(ChannelChild::getSubChannelId, c -> c));
        //????????????
        List<Long> ppIdList = channelPromotionList.stream().map(ChannelPromotion::getPpId).collect(Collectors.toList());
        List<ChannelPromotionPosition> channelPromotionPositionList = channelPromotionPositionService.list(new QueryWrapper<ChannelPromotionPosition>().lambda().in(ChannelPromotionPosition::getPpId, ppIdList));
        Map<Long, ChannelPromotionPosition> channelPromotionPositionMap = channelPromotionPositionList.stream().collect(Collectors.toMap(ChannelPromotionPosition::getPpId, c -> c));
        //????????????
        if (param.getTargetChannelId() != null) {
            ChannelCooperation channelCooperation = channelCooperationService.getOne(new QueryWrapper<ChannelCooperation>().lambda().eq(ChannelCooperation::getCcid, param.getTargetCCId()));
            param.setTargetChannelId(channelCooperation.getChannelId());
        }
        //???????????????
        List<ChannelChild> targetChannelChildList = channelChildService.list(new QueryWrapper<ChannelChild>().lambda().in(ChannelChild::getChannelId, param.getTargetChannelId()));
        Map<String, ChannelChild> targetChannelChildMap = targetChannelChildList.stream().collect(Collectors.toMap(ChannelChild::getSubChannelName, c -> c));
        List<String> targetChannelChildIdList = targetChannelChildList.stream().map(ChannelChild::getSubChannelId).distinct().collect(Collectors.toList());
        //?????????????????????
        List<ChannelPromotionPosition> targetChannelPromotionPositionQDList = channelPromotionPositionService.list(new QueryWrapper<ChannelPromotionPosition>().lambda()
                .eq(ChannelPromotionPosition::getChannelId, param.getTargetChannelId())
                .eq(ChannelPromotionPosition::getPpFlag, 1)
        );
        Map<String, ChannelPromotionPosition> targetChannelPromotionPositionQDMap = targetChannelPromotionPositionQDList.stream()
                .collect(Collectors.toMap(i -> i.getPpName(), c -> c));
        Map<String, ChannelPromotionPosition> targetChannelPromotionPositionQDMapMax = targetChannelPromotionPositionQDList.stream()
                .collect(Collectors.toMap(i -> i.getPpName() + i.getChannelId(), c -> c));
        //????????????????????????
        List<ChannelPromotionPosition> targetChannelPromotionPositionZQDList = new ArrayList<ChannelPromotionPosition>();
        if (targetChannelChildIdList.size() > 0) {
            targetChannelPromotionPositionZQDList = channelPromotionPositionService.list(new QueryWrapper<ChannelPromotionPosition>().lambda()
                    .in(ChannelPromotionPosition::getSubChannelId, targetChannelChildIdList)
                    .eq(ChannelPromotionPosition::getPpFlag, 2)
            );
        }
        Map<String, ChannelPromotionPosition> targetChannelPromotionPositionZQDMap = targetChannelPromotionPositionZQDList.stream()
                .collect(Collectors.toMap(i -> i.getPpName(), c -> c));
        Map<String, ChannelPromotionPosition> targetChannelPromotionPositionZQDMapMax = targetChannelPromotionPositionZQDList.stream()
                .collect(Collectors.toMap(i -> i.getPpName() + i.getSubChannelId(), c -> c));
        //?????????????????????+??????????????????
        Map<String, ChannelPromotionPosition> targetChannelPromotionPositionMap = new HashMap<String, ChannelPromotionPosition>();
        targetChannelPromotionPositionMap.putAll(targetChannelPromotionPositionQDMap);
        targetChannelPromotionPositionMap.putAll(targetChannelPromotionPositionZQDMap);

        //?????????????????????
        List<ChannelCooperation> targetChannelCooperationList = channelCooperationService.list(new QueryWrapper<ChannelCooperation>().select("department_code, department_name").lambda().eq(ChannelCooperation::getCcid, param.getTargetCCId()));
        //??????CCID????????????????????????/??????/????????????
        String targetDepartmentCode = targetChannelCooperationList.get(0).getDepartmentCode();
        String targetDepartmentName = targetChannelCooperationList.get(0).getDepartmentName();
        ChannelProductPageParam channelProductPageParam = new ChannelProductPageParam();
        channelProductPageParam.setKeyword(targetDepartmentCode);
        List<ChannelProduct> targetChannelProductList = channelProductService.selectListProduct(channelProductPageParam); //1???ccid?????????1?????????
        List<String> targetProductCodeList = targetChannelProductList.stream().map(ChannelProduct::getProductCode).distinct().collect(Collectors.toList());

        ChannelApplication channelApplicationParam = new ChannelApplication();
        channelApplicationParam.setProductCodeList(targetProductCodeList);
        List<ChannelApplication> targetChannelApplicationList = channelApplicationMapper.selectByProductNameAndAppName(channelApplicationParam);
        List<Long> targetApplicationIdList = targetChannelApplicationList.stream().map(ChannelApplication::getId).collect(Collectors.toList());

        List<ChannelMedium> targetChannelMediumList = channelMediumService.list(new QueryWrapper<ChannelMedium>().lambda().eq(ChannelMedium::getDepartmentCode, targetDepartmentCode));
        List<String> targetMediumIdList = targetChannelMediumList.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.toList());
        logger.info("?????????????????????ID??????" + JSON.toJSONString(targetMediumIdList));

        List<String> errorRowBackSubChannel = new ArrayList<String>();
        Date targetCheckStartDate = param.getCheckStartDate();
        Date targetCheckEndDate = param.getCheckEndDate();
        List errorProductCode = new ArrayList<String>();
        List errorApplicationId = new ArrayList<String>();
        List errorMediumId = new ArrayList<String>();
        List errorCPPZYName = new ArrayList<String>();  //????????????????????????????????????
        List errorCPPZQDName = new ArrayList<String>(); //??????????????????????????????????????????
        List errorCPPQDName = new ArrayList<String>();  //?????????
        List errorByTime = new ArrayList<String>();
        List<ChannelPromotionHistory> channelPromotionHistoryList = new ArrayList<ChannelPromotionHistory>();
        try {
            for (String pid : pidList) {
                ChannelPromotion channelPromotion = channelPromotionMap.get(pid);

                ChannelChild channelChild = channelChildMap.get(channelPromotion.getSubChannelId());
                String subChannelName = channelChild.getSubChannelName();

                ChannelPromotionPosition channelPromotionPosition = channelPromotionPositionMap.get(channelPromotion.getPpId());
                String ppName = channelPromotionPosition != null ? channelPromotionPosition.getPpName() : "";
                Integer ppType = channelPromotionPosition != null ? channelPromotionPosition.getPpFlag() : 0;

                Date checkStartDate = channelPromotion.getCheckStartDate();
                Date checkEndDate = channelPromotion.getCheckEndDate();

                //----------------------??????????????????/??????/??????/????????????----------------------------
                //???????????????????????????????????????PID???????????????????????????
                if (checkStartDate != null && targetCheckStartDate.getTime() <= checkStartDate.getTime()) {
                    errorByTime.add(channelPromotion.getPid());
                }
                //1???applicationId??????1?????????+??????
                if (targetProductCodeList.size() > 0 && channelPromotion.getProductCode() != null && !targetProductCodeList.contains(channelPromotion.getProductCode())) {
                    errorProductCode.add(channelPromotion.getProductCode());
                }
                if (targetApplicationIdList.size() > 0 && channelPromotion.getApplicationId() != null && !targetApplicationIdList.contains(channelPromotion.getApplicationId())) {
                    errorApplicationId.add(channelPromotion.getApplicationId());
                }

                if (targetMediumIdList.size() > 0 && StringUtils.isNotBlank(channelPromotion.getMediumId())) {
                    String mediumIdAll = channelPromotion.getMediumId();
                    String[] mediumIds = mediumIdAll.split(",");
                    for (int i = 0; i < mediumIds.length; i++) {
                        String mediumId = mediumIds[i];
                        if (!targetMediumIdList.contains(mediumId)) {
                            errorMediumId.add(mediumId);
                        }
                    }
                }
                //???????????????-1??????????????????????????????????????????????????????????????????????????????????????????????????????
                if (StringUtils.isNotBlank(ppName) && (targetChannelPromotionPositionQDMap.size() > 0 && targetChannelPromotionPositionQDMap.containsKey(ppName) && ppType == 2)) {
                    errorCPPZQDName.add(ppName);
                }
                //???????????????????????????????????????????????????????????????????????????????????????
                if (StringUtils.isNotBlank(ppName) && (targetChannelPromotionPositionZQDMap.size() > 0 && targetChannelPromotionPositionZQDMap.containsKey(ppName) && ppType == 1)) {
                    errorCPPQDName.add(ppName);
                }
                // ???????????????-2???????????????????????????????????????????????????????????????1 ???1 ??????1??? -> ?????????2 ???2 ??????1??????????????????????????????????????? ???1???
                // ????????????????????????
                if (StringUtils.isNotBlank(ppName) && !errorCPPZQDName.contains(ppName) && !errorCPPQDName.contains(ppName) && !errorCPPZYName.contains(ppName)) {
                    //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    if (!targetChannelChildMap.containsKey(subChannelName) && targetChannelPromotionPositionZQDMap.containsKey(ppName)) {
                        errorCPPZYName.add(ppName);
                    }
                    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    if (targetChannelChildMap.containsKey(subChannelName) && targetChannelPromotionPositionZQDMap.containsKey(ppName)
                            && !targetChannelPromotionPositionZQDMapMax.containsKey(ppName + targetChannelChildMap.get(subChannelName).getSubChannelId())) {
                        errorCPPZYName.add(ppName);
                    }
                    //??????????????????????????????????????????????????????
                    if (channelPromotionPosition.getPpFlag() != null && channelPromotionPosition.getPpFlag() == 1 && targetChannelPromotionPositionZQDMap.containsKey(ppName)) {
                        errorCPPZYName.add(ppName);
                    }
                }
                //---------------------------???????????????/?????????---------------------------
                //?????????????????????????????????????????????????????????
                if (errorByTime.size() == 0 && errorProductCode.size() == 0 && errorApplicationId.size() == 0 && errorMediumId.size() == 0
                        && errorCPPZQDName.size() == 0 && errorCPPQDName.size() == 0 && errorCPPZYName.size() == 0) {
                    //????????????????????????????????????
                    if (!targetChannelChildMap.containsKey(subChannelName)) {
                        //???????????????
                        ChannelChild channelChildNew = new ChannelChild();
                        // update yf by 20210717(?????????)
                        channelChildNew.setCcid(String.valueOf(param.getTargetCCId()));
                        channelChildNew.setChannelId(param.getTargetChannelId());
                        channelChildNew.setSubChannelName(subChannelName);
                        Map<Object, Object> channelChildRes = channelChildService.saveChannelChild(channelChildNew);
                        channelChildNew.setSubChannelId(String.valueOf(channelChildRes.get("subChannelId")));
                        if (StringUtils.isNotBlank(param.getDataSource())) {
                            channelChildNew.setDataSource(param.getDataSource());
                        }
                        targetChannelChildMap.put(subChannelName, channelChildNew);
                        //???????????????????????????List?????????
                        errorRowBackSubChannel.add(String.valueOf(channelChildRes.get("subChannelId")));
                    }

                    ChannelChild channelChildNow = targetChannelChildMap.get(subChannelName);
                    //????????????????????????????????????
                    // ???????????????
                    if (StringUtils.isNotBlank(ppName) && ppType == 1 && !targetChannelPromotionPositionQDMapMax.containsKey(ppName + param.getTargetChannelId())) {
                        //???????????????
                        ChannelPromotionPosition channelPromotionPositionNew = new ChannelPromotionPosition();
                        channelPromotionPositionNew.setPpName(ppName);
                        channelPromotionPositionNew.setPpStatus(1);
                        channelPromotionPositionNew.setChannelId(param.getTargetChannelId());
                        channelPromotionPositionNew.setPpFlag(1);
                        channelPromotionPositionNew.setPlugId(channelPromotionPosition.getPlugId());
                        channelPromotionPositionNew.setPlugName(channelPromotionPosition.getPlugName());
                        channelPromotionPositionService.save(channelPromotionPositionNew);
                        targetChannelPromotionPositionQDMap.put(ppName, channelPromotionPositionNew);
                        targetChannelPromotionPositionQDMapMax.put(ppName + param.getTargetChannelId(), channelPromotionPositionNew);
                    } else if (StringUtils.isNotBlank(ppName) && ppType == 2 && !targetChannelPromotionPositionZQDMapMax.containsKey(ppName + channelChildNow.getSubChannelId())) {
                        //???????????????
                        ChannelPromotionPosition channelPromotionPositionNew = new ChannelPromotionPosition();
                        channelPromotionPositionNew.setPpName(ppName);
                        channelPromotionPositionNew.setPpStatus(1);
                        channelPromotionPositionNew.setChannelId(param.getTargetChannelId());
                        channelPromotionPositionNew.setSubChannelId(channelChildNow.getSubChannelId());
                        channelPromotionPositionNew.setPpFlag(2);
                        channelPromotionPositionNew.setPlugId(channelPromotionPosition.getPlugId());
                        channelPromotionPositionNew.setPlugName(channelPromotionPosition.getPlugName());
                        channelPromotionPositionService.save(channelPromotionPositionNew);
                        targetChannelPromotionPositionZQDMap.put(ppName, channelPromotionPositionNew);
                        targetChannelPromotionPositionZQDMapMax.put(ppName + channelChildNow.getSubChannelId(), channelPromotionPositionNew);
                    }

                    ChannelPromotionHistory channelPromotionHistory = new ChannelPromotionHistory();
                    BeanUtils.copyProperties(channelPromotion, channelPromotionHistory, new String[]{"id"});
                    if (checkStartDate.getTime() < targetCheckStartDate.getTime() && targetCheckStartDate.getTime() < checkEndDate.getTime()) {
                        Date date = DateUtils.addDays(targetCheckStartDate, -1);
                        Date endDate = DateUtil.parse(DateUtil.format(date, "yyyy-MM-dd") + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
                        channelPromotionHistory.setCheckEndDate(endDate);
                    } else if (checkEndDate.getTime() < targetCheckStartDate.getTime()) {
                        channelPromotionHistory.setCheckEndDate(checkEndDate);
                    }
                    channelPromotionHistory.setChannelId(param.getChannelId());
                    channelPromotionHistory.setChannelName(param.getChannelName());
                    channelPromotionHistory.setSubChannelName(subChannelName);
                    ChannelCooperation cooperation = channelCooperationMap.get(channelPromotion.getCcid());
                    if (cooperation != null) {
                        BeanUtils.copyProperties(cooperation, channelPromotionHistory, new String[]{"id", "userid", "username", "createTime", "updateTime"});
                    }
                    channelPromotionHistory.setOperid(Long.valueOf(user.getId()));
                    channelPromotionHistory.setOpername(user.getCnname());
                    channelPromotionHistoryList.add(channelPromotionHistory);

                    //????????????????????????ID???????????????ID
                    channelPromotion.setCcid(String.valueOf(param.getTargetCCId()));
                    channelPromotion.setSubChannelId(channelChildNow.getSubChannelId());
                    if (StringUtils.isNotBlank(ppName)) {
                        if (targetChannelPromotionPositionQDMapMax.containsKey(ppName + param.getTargetChannelId())) {
                            channelPromotion.setPpId(targetChannelPromotionPositionQDMapMax.get(ppName + param.getTargetChannelId()).getPpId());
                        } else if (targetChannelPromotionPositionZQDMapMax.containsKey(ppName + channelChildNow.getSubChannelId())) {
                            channelPromotion.setPpId(targetChannelPromotionPositionZQDMapMax.get(ppName + channelChildNow.getSubChannelId()).getPpId());
                        }
                    }
                    //??????
                    channelPromotion.setCheckStartDate(targetCheckStartDate);
                    channelPromotion.setCheckEndDate(targetCheckEndDate == null ? DateUtils.parseDate("2099-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss") : targetCheckEndDate);
                }
            }
        } catch (Exception e) {
            //??????????????????????????????
            if (errorRowBackSubChannel.size() > 0) {
                channelChildService.remove(new QueryWrapper<ChannelChild>().lambda().in(ChannelChild::getSubChannelId, errorRowBackSubChannel));
            }
            throw e;
        }

        //???$?????????????????????$????????????1???$????????????2???????????????????????????????????????
        StringBuffer sb = new StringBuffer("");
        if (errorByTime.size() > 0) {
            String str = String.format("?????????????????????%s??????", org.apache.commons.lang3.StringUtils.join(errorByTime, "???"));
            sb.append(str);
        }
        if (errorProductCode.size() > 0) {
            List<ChannelProduct> errorChannelProductList = channelProductService.list(new QueryWrapper<ChannelProduct>().lambda().select(ChannelProduct::getProductName)
                    .in(ChannelProduct::getProductCode, errorProductCode));
            List<String> errorChannelProductNameList = errorChannelProductList.stream().map(ChannelProduct::getProductName).collect(Collectors.toList());
            String str = String.format("???????????????????????????%s??????", org.apache.commons.lang3.StringUtils.join(errorChannelProductNameList, "???"));
            sb.append(str);
        }
        if (errorApplicationId.size() > 0) {
            List<ChannelApplication> errorChannelApplicationList = channelApplicationService.list(new QueryWrapper<ChannelApplication>().lambda().select(ChannelApplication::getApplicationName).in(ChannelApplication::getId, errorApplicationId));
            List<String> errorChannelApplicationNameList = errorChannelApplicationList.stream().map(ChannelApplication::getApplicationName).collect(Collectors.toList());
            String str = String.format("???????????????????????????%s??????", org.apache.commons.lang3.StringUtils.join(errorChannelApplicationNameList, "???"));
            sb.append(str);
        }
        if (errorMediumId.size() > 0) {
            List<ChannelMedium> errorChannelMediumList = channelMediumService.list(new QueryWrapper<ChannelMedium>().lambda().select(ChannelMedium::getName).in(ChannelMedium::getId, errorMediumId));
            List<String> errorChannelMediumNameList = errorChannelMediumList.stream().map(ChannelMedium::getName).collect(Collectors.toList());
            String str = String.format("???????????????????????????%s??????", org.apache.commons.lang3.StringUtils.join(errorChannelMediumNameList, "???"));
            sb.append(str);
        }
        if (errorCPPZQDName.size() > 0) {
            String str = String.format("??????????????????????????????????????????%s??????", org.apache.commons.lang3.StringUtils.join(errorCPPZQDName, "???"));
            sb.append(str);
        }
        if (errorCPPQDName.size() > 0) {
            String str = String.format("????????????????????????????????????????????????%s??????", org.apache.commons.lang3.StringUtils.join(errorCPPQDName, "???"));
            sb.append(str);
        }
        if (errorCPPZYName.size() > 0) {
            String str = String.format("????????????????????????%s??????", org.apache.commons.lang3.StringUtils.join(errorCPPZYName, "???"));
            sb.append(str);
        }
        if (sb.length() > 0) {
            sb.insert(0, "" + targetDepartmentName + "??????").append("????????????");
        }

        if (sb.length() > 0) {
            throw new BusinessException(sb.toString());
        }
        //????????????PID??????
        this.saveOrUpdateBatch(channelPromotionList);

        //?????????
        channelPromotionAllService.migrationThread(channelPromotionList);

        //??????????????????
        channelPromotionHistoryService.saveBatch(channelPromotionHistoryList);
    }

    @Override
    public Map<String, Object> searchList(ChannelPromotionPageParam param, HttpServletRequest request) {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        param.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        param.setMapAll(mapAll);

        //????????????
        paramCheck(param);

        List<ChannelPromotionVO> channelPromotionVOList = channelPromotionMapper.searchList(param, user);

        //??????
        Set<Map<String, String>> departmentList = new HashSet<Map<String, String>>();
        //??????
        Set<Map<String, String>> channelMapList = new HashSet<Map<String, String>>();
        //?????????
        Set<Map<String, String>> subChannelMapList = new HashSet<Map<String, String>>();
        //??????/??????
        Set<Map<String, String>> prodAppMapList = new HashSet<Map<String, String>>();

        for (ChannelPromotionVO channelPromotion : channelPromotionVOList) {
            if (channelPromotion != null) {
                if (org.apache.commons.lang.StringUtils.isNotBlank(channelPromotion.getDepartmentCode())) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("code", channelPromotion.getDepartmentCode());
                    map.put("name", channelPromotion.getDepartmentName());
                    departmentList.add(map);
                }
                if (channelPromotion.getChannelId() != null) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("channelId", channelPromotion.getChannelId().toString());
                    map.put("channelName", channelPromotion.getChannelName());
                    channelMapList.add(map);
                }
                if (channelPromotion.getSubChannelId() != null) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("subChannelId", channelPromotion.getSubChannelId());
                    map.put("subChannelName", channelPromotion.getSubChannelName());
                    subChannelMapList.add(map);
                }
                if (StringUtils.isNotBlank(channelPromotion.getProductCode())) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("prodAppKey", channelPromotion.getProductCode() + (StringUtils.isNotBlank(channelPromotion.getApplicationId()) ? ":" + channelPromotion.getApplicationId() : ""));
                    map.put("prodAppValue", channelPromotion.getProductName() + "???" + channelPromotion.getProductCode() + "???" + (StringUtils.isNotBlank(channelPromotion.getApplicationId()) ? "/" + channelPromotion.getApplicationName() : ""));

                    prodAppMapList.add(map);
                }
            }
        }

        Map<String, Object> mapAlls = new HashMap<String, Object>();
        mapAlls.put("department", departmentList);
        mapAlls.put("channel", channelMapList);
        mapAlls.put("subChannel", subChannelMapList);
        mapAlls.put("prodApp", prodAppMapList);

        return mapAlls;
    }

    public static <T> List<T> list2OtherList(List originList, Class<T> tClass) {
        List<T> list = new ArrayList<>();
        for (Object info : originList) {
            T t = JSON.parseObject(JSON.toJSONString(info), tClass);
            list.add(t);
        }
        return list;
    }

    @Override
    public List<Channel> migrationChannelList(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelPromotionPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelPromotionPageParam.setMapAll(mapAll);

        //??????CCID????????????????????????CCID?????????????????????
        String ccid = channelPromotionPageParam.getCcid();
        String keyword = channelPromotionPageParam.getKeyword();
        ChannelCooperation channelCooperation = channelCooperationService.getOne(new QueryWrapper<ChannelCooperation>().lambda().eq(ChannelCooperation::getCcid, ccid));
        String departmentCode = channelCooperation.getDepartmentCode();
        channelPromotionPageParam.setDepartmentCode(departmentCode);

        List<ChannelCooperation> channelCooperationList = channelCooperationMapper.migrationChannelList(channelPromotionPageParam, user);
        List<Long> channelIdList = channelCooperationList.stream().map(ChannelCooperation::getChannelId).collect(Collectors.toList());

        List<Channel> channelList = channelService.list(new QueryWrapper<Channel>().lambda()
                .in(Channel::getChannelId, channelIdList)
                .and(StringUtils.isNotBlank(keyword), w -> w.like(Channel::getChannelName, keyword)
                        .or().like(Channel::getCompanyName, keyword)
                        .or().like(Channel::getDepartmentName, keyword)
                        .or(SecretTypeEnum.getByValue(keyword) != null, k -> k.eq(Channel::getSecretType, SecretTypeEnum.getByValue(keyword).getKey()))
                )
        );

        channelList = channelList.stream().map(i -> i.setSecretTypeStr(SecretTypeEnum.getByKey(i.getSecretType()).getValue())).collect(Collectors.toList());

        return channelList;
    }

    @Override
    public List<ChannelCooperation> migrationCCIDList(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelPromotionPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelPromotionPageParam.setMapAll(mapAll);

        //??????CCID????????????????????????CCID
        String ccid = channelPromotionPageParam.getCcid();
        ChannelCooperation channelCooperation = channelCooperationService.getOne(new QueryWrapper<ChannelCooperation>().lambda().eq(ChannelCooperation::getCcid, ccid));
        String departmentCode = channelCooperation.getDepartmentCode();
        channelPromotionPageParam.setDepartmentCode(departmentCode);

        List<ChannelCooperation> channelCooperationList = channelCooperationMapper.migrationCCIDList(channelPromotionPageParam, user);

        return channelCooperationList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateChannelPromotion(ChannelPromotion channelPromotion) throws Exception {
        //??????????????????????????????????????????????????????
        List<ChannelPromotionHistory> listByTime = channelPromotionHistoryService.list(new QueryWrapper<ChannelPromotionHistory>().lambda()
                .eq(ChannelPromotionHistory::getPid, channelPromotion.getPid())
                .and(i -> i.and(o -> o.between(ChannelPromotionHistory::getCheckStartDate, channelPromotion.getCheckStartDate(), channelPromotion.getCheckEndDate()))
                        .or(x -> x.between(ChannelPromotionHistory::getCheckEndDate, channelPromotion.getCheckStartDate(), channelPromotion.getCheckEndDate()))
                        .or(x -> x.ge(ChannelPromotionHistory::getCheckStartDate, channelPromotion.getCheckStartDate())
                                .le(ChannelPromotionHistory::getCheckEndDate, channelPromotion.getCheckEndDate()))
                        .or(x -> x.ge(ChannelPromotionHistory::getCheckStartDate, channelPromotion.getCheckEndDate()))
                )
        );
        if (listByTime.size() > 0) {
            throw new BusinessException("?????????????????????????????????????????????");
        }

        //?????????????????????
        if (StrUtil.isNotEmpty(channelPromotion.getSubChannelName())) {
            String subChannelId = newSubChannel(channelPromotion);
            channelPromotion.setSubChannelId(subChannelId);
        } else {
            Integer childCount = channelChildService.count(new LambdaQueryWrapper<ChannelChild>().eq(ChannelChild::getSubChannelId, channelPromotion.getSubChannelId()));
            if (childCount == 0) {
                throw new BusinessException("?????????ID?????????");
            }
        }

        //??????????????????
        ChannelPromotion channelPromotionDB = channelPromotionMapper.selectById(channelPromotion.getId());
        if (ObjectUtil.isEmpty(channelPromotionDB)) {
            throw new BusinessException("PID?????????");
        }
        //????????????????????????
        Boolean updateAliasFlag = false;
        String pidName = channelPromotion.getPidAlias();
        if (!pidName.equals(channelPromotionDB.getPidAlias())) {
            updateAliasFlag = true;
            Boolean flag = true;
            if (pidName.contains("_")) {
                List<String> list = Arrays.asList(pidName.split("_"));
                String last = list.get(list.size() - 1);
                if (NumberUtil.isNumber(last)) {
                    //?????????
                    ChannelPromotion cpOne = channelPromotionMapper.selectOne(new QueryWrapper<ChannelPromotion>().lambda().eq(ChannelPromotion::getPidAlias, pidName));
                    if (cpOne != null) {
                        //??????????????????????????????????????????
                        pidName = pidName.substring(0, pidName.lastIndexOf("_"));
                    } else {
                        //????????????????????????
                        flag = false;
                    }
                }
            }

            if (flag) {
                Integer lastNum = this.getLastNum(pidName); //by yifan 20211104
                pidName = lastNum > 0 ? pidName.concat("_").concat(String.valueOf(lastNum)) : pidName;
                channelPromotion.setPidAlias(pidName);
            }
        }

        channelPromotionMapper.updateByIdSql(channelPromotion);

        // ??????YouTop + ?????????
        List<PostPidParam> postPidParamList = new ArrayList<PostPidParam>();
        if (updateAliasFlag) {
            ChannelProduct channelProduct = channelProductService.getOne(new QueryWrapper<ChannelProduct>().lambda().eq(ChannelProduct::getProductCode, channelPromotionDB.getProductCode()));
            channelPromotion.setProductId(channelProduct.getProductId());
            PostPidParam postPidParam = DozerUtil.toBean(channelPromotion, PostPidParam.class);
            postPidParamList.add(postPidParam);
        }
        List<ChannelPromotion> channelPromotionList = channelPromotionMapper.selectList(new LambdaQueryWrapper<ChannelPromotion>().eq(ChannelPromotion::getId, channelPromotion.getId()));
        logger.info("====???03??????channelPromotionList???" + JSON.toJSONString(channelPromotionList));
        channelPromotionAllService.addBatchThread(channelPromotionList, youtopApiHost, postPidParamList);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteChannelPromotion(Long id) throws Exception {
        //?????????
        ChannelPromotion channelPromotion = channelPromotionMapper.selectById(id);
        channelPromotionAllService.remove(new QueryWrapper<ChannelPromotionAll>().lambda().eq(ChannelPromotionAll::getPid, channelPromotion.getPid()));
        channelPromotionHistoryService.remove(new QueryWrapper<ChannelPromotionHistory>().lambda().eq(ChannelPromotionHistory::getPid, channelPromotion.getPid()));

        return super.removeById(id);
    }

    @Override
    public PageEntity<ChannelPromotionVO> getPidPageList(ChannelPromotionPageParam cParam, HttpServletRequest request) throws Exception {
        Page<ChannelPromotion> page = new Page<ChannelPromotion>(cParam.getPageIndex(), cParam.getPageSize());
        List<ChannelPromotionVO> channelPromotionVOList = getPidList(page, cParam, request);

        return new PageEntity<>(page, channelPromotionVOList);
    }

    public List<ChannelPromotionVO> getPidList(Page<ChannelPromotion> page, ChannelPromotionPageParam cParam, HttpServletRequest request) {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        cParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        cParam.setMapAll(mapAll);

        //????????????
        paramCheck(cParam);

        if (ObjectUtil.isEmpty(page)) {
            page = new Page<ChannelPromotion>();
            page.setSize(-1);
        }
        List<OrderItem> orderItemList = CollectionUtil.isNotEmpty(cParam.getOrders()) ? cParam.getOrders() : new ArrayList<OrderItem>();
        orderColumn(orderItemList);
        page.setOrders(orderItemList);

        List<ChannelPromotionVO> channelPromotionVOList = channelPromotionMapper.getListByCond(page, cParam, user);
        if (CollectionUtil.isEmpty(channelPromotionVOList)) {
            return channelPromotionVOList;
        }

        // ??????
        List<Long> mediumIdList = channelPromotionVOList.stream().map(ChannelPromotionVO::getMediumId).distinct().map(m -> StrUtil.split(m, ",")).flatMap(Arrays::stream).filter(StrUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList());
        List<ChannelMedium> channelMediumList = CollectionUtil.isNotEmpty(mediumIdList) ? channelMediumService.list(new LambdaQueryWrapper<ChannelMedium>().in(ChannelMedium::getId, mediumIdList)) : Collections.emptyList();
        Map<Long, String> mediumIdToName = channelMediumList.stream().collect(Collectors.toMap(ChannelMedium::getId, ChannelMedium::getName));

        for (ChannelPromotionVO channelPromotionVO : channelPromotionVOList) {
            String mediumId = channelPromotionVO.getMediumId();
            if (StrUtil.isNotEmpty(mediumId)) {
                //????????????
                List<String> strings = Arrays.stream(mediumId.split(",")).map(id -> mediumIdToName.get(Long.parseLong(id))).filter(i -> StringUtils.isNotBlank(i)).collect(Collectors.toList());
                channelPromotionVO.setMediumName(StrUtil.join(",", strings));
            }
        }

        return channelPromotionVOList;
    }

    /**
     * ????????????
     *
     * @param cParam
     */
    public void paramCheck(ChannelPromotionPageParam cParam) {
        String prodApp = cParam.getProdApp();
        if (StringUtils.isNotBlank(prodApp)) {
            String[] prodAppSubs = prodApp.split(",");

            List<ChannelApplication> channelApplicationList = new ArrayList<ChannelApplication>();
            for (String prodAppSub : prodAppSubs) {
                ChannelApplication channelApplication = new ChannelApplication();
                if (prodAppSub.contains(":")) {
                    String[] str = prodAppSub.split(":");
                    channelApplication.setProductCodeParam(str[0]);
                    channelApplication.setId(Long.valueOf(str[1]));
                } else {
                    channelApplication.setProductCodeParam(prodAppSub);
                }
                channelApplicationList.add(channelApplication);
            }
            cParam.setChannelApplicationList(channelApplicationList);
        }
    }

    public void orderColumn(List<OrderItem> orderItemList) {
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<String> errorOrder = new ArrayList<String>();
            List<OrderItem> productNameAndApplicationNameAdd = new ArrayList<OrderItem>();

            Iterator<OrderItem> iterator = orderItemList.iterator();
            while (iterator.hasNext()) {
                OrderItem orderItem = iterator.next();
                if ("pid".equals(orderItem.getColumn())) {
                    orderItem.setColumn("p.pid");
                    continue;
                } else if ("pidAlias".equals(orderItem.getColumn())) {
                    orderItem.setColumn("p.pid_alias");
                    continue;
                } else if ("ccid".equals(orderItem.getColumn())) {
                    orderItem.setColumn("p.ccid");
                    continue;
                } else if ("channelName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("dc.channel_name");
                    continue;
                } else if ("subChannelName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("d.sub_channel_name");
                    continue;
                } else if ("departmentName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("c.department_name");
                    continue;
                } else if ("promotionPositionName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pp.pp_name");
                    continue;
                } else if ("productNameAndApplicationName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pt.product_name");
                    OrderItem orderItem1 = new OrderItem();
                    orderItem1.setColumn("pt.product_code");
                    orderItem1.setAsc(orderItem.isAsc());

                    //??????????????????????????????
                    /*OrderItem orderItem2 = new OrderItem();
                    orderItem2.setColumn("ap.application_name");
                    orderItem2.setAsc(orderItem.isAsc());*/

                    productNameAndApplicationNameAdd.add(orderItem1);
                    //productNameAndApplicationNameAdd.add(orderItem2);
                    continue;
                } else if ("mediumName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("m.name");
                    continue;
                } else if ("extra".equals(orderItem.getColumn())) {
                    orderItem.setColumn("p.extra");
                    continue;
                } else if ("ccidHistoryNum".equals(orderItem.getColumn())) {
                    orderItem.setColumn("ccidHistoryNum");
                    continue;
                } else if ("usernameName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("p.username");
                    continue;
                } else if ("createTime".equals(orderItem.getColumn())) {
                    orderItem.setColumn("p.create_time");

                    OrderItem orderItem1 = new OrderItem();
                    orderItem1.setColumn("p.id");
                    productNameAndApplicationNameAdd.add(orderItem1);
                    continue;
                } else if ("updateTime".equals(orderItem.getColumn())) {
                    orderItem.setColumn("p.update_time");

                    OrderItem orderItem1 = new OrderItem();
                    orderItem1.setColumn("p.id");
                    productNameAndApplicationNameAdd.add(orderItem1);
                    continue;
                } else if ("checkStartDate".equals(orderItem.getColumn())) {
                    orderItem.setColumn("p.check_start_date");

                    OrderItem orderItem1 = new OrderItem();
                    orderItem1.setColumn("p.id");
                    productNameAndApplicationNameAdd.add(orderItem1);
                    continue;
                }
                errorOrder.add(orderItem.getColumn());
            }

            if (CollectionUtil.isNotEmpty(errorOrder)) {
                throw new BusinessException("?????????????????????" + org.apache.commons.lang.StringUtils.join(errorOrder, ","));
            }

            orderItemList.addAll(productNameAndApplicationNameAdd);
        } else {
            OrderItem orderItem = new OrderItem();
            orderItem.setColumn("p.id");
            orderItem.setAsc(false);
            orderItemList.add(orderItem);
        }
    }

    @Override
    public List<ChannelPromotionVO> getPidPageListToExcel(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) {
        List<ChannelPromotionVO> channelPromotionVOList = getPidList(null, channelPromotionPageParam, request);

        return channelPromotionVOList;
    }

    @Override
    public boolean updateChannelPromotionSub(ChannelPromotion channelPromotion) {
        if (CollectionUtil.isEmpty(channelPromotion.getIdList()) && ObjectUtil.isEmpty(channelPromotion.getId())) {
            throw new BusinessException("idList????????????");
        } else if (CollectionUtil.isEmpty(channelPromotion.getIdList()) && ObjectUtil.isNotEmpty(channelPromotion.getId())) {
            List<Long> idList = new ArrayList<Long>();
            idList.add(channelPromotion.getId());
            channelPromotion.setIdList(idList);
        }

        List<ChannelPromotion> channelPromotionList = channelPromotionMapper.selectBatchIds(channelPromotion.getIdList());

        //???????????????
        if (ObjectUtil.isNotEmpty(channelPromotion.getCheckStartDate()) && ObjectUtil.isNotEmpty(channelPromotion.getCheckEndDate())) {
            List<String> pidList = channelPromotionList.stream().map(ChannelPromotion::getPid).collect(Collectors.toList());
            //??????????????????????????????????????????????????????
            List<String> errorTime = new ArrayList<String>();
            for (String pid : pidList) {
                List<ChannelPromotionHistory> listByTime = channelPromotionHistoryService.list(new QueryWrapper<ChannelPromotionHistory>().lambda()
                        .eq(ChannelPromotionHistory::getPid, pid)
                        .and(i -> i.and(o -> o.between(ChannelPromotionHistory::getCheckStartDate, channelPromotion.getCheckStartDate(), channelPromotion.getCheckEndDate()))
                                .or(x -> x.between(ChannelPromotionHistory::getCheckEndDate, channelPromotion.getCheckStartDate(), channelPromotion.getCheckEndDate()))
                                .or(x -> x.ge(ChannelPromotionHistory::getCheckStartDate, channelPromotion.getCheckStartDate())
                                        .le(ChannelPromotionHistory::getCheckEndDate, channelPromotion.getCheckEndDate()))
                                .or(x -> x.ge(ChannelPromotionHistory::getCheckStartDate, channelPromotion.getCheckEndDate()))
                        )
                );
                if (listByTime.size() > 0) {
                    errorTime.add(pid);
                }
            }
            if (CollectionUtil.isNotEmpty(errorTime)) {
                throw new BusinessException("?????????????????????????????????????????????:" + String.join(",", errorTime));
            }

            for (ChannelPromotion channelPromotionTemp : channelPromotionList) {
                channelPromotionTemp.setCheckStartDate(channelPromotion.getCheckStartDate());
                channelPromotionTemp.setCheckEndDate(channelPromotion.getCheckEndDate());
            }
        }
        //??????????????????
        Boolean updateAliasFlag = false;
        if (StringUtils.isNotBlank(channelPromotion.getPidAlias())) {
            updateAliasFlag = true;
            List<String> productCodeList = channelPromotionList.stream().map(ChannelPromotion::getProductCode).collect(Collectors.toList());
            List<ChannelProduct> channelProductList = channelProductService.list(new LambdaQueryWrapper<ChannelProduct>().in(ChannelProduct::getProductCode, productCodeList));
            Map<String, String> channelPromotionMap = channelProductList.stream().collect(Collectors.toMap(i -> i.getProductCode(), i -> i.getProductId()));

            String pidAlias = channelPromotion.getPidAlias();
            Integer lastNum = this.getLastNum(pidAlias);  //by yifan 20211104

            for (ChannelPromotion channelPromotionTemp : channelPromotionList) {
                if (!pidAlias.equals(channelPromotionTemp.getPidAlias())) {
                    //????????????
                    String pidName = lastNum > 0 ? pidAlias.concat("_").concat(String.valueOf(lastNum)) : pidAlias;
                    lastNum++;

                    channelPromotionTemp.setPidAlias(pidName);
                }

                //????????????ID
                if (channelPromotionMap.containsKey(channelPromotionTemp.getProductCode())) {
                    channelPromotionTemp.setProductId(channelPromotionMap.get(channelPromotionTemp.getProductCode()));
                }
            }
        }
        //??????????????????
        for (ChannelPromotion channelPromotionTemp : channelPromotionList) {
            channelPromotionTemp.setExtra(channelPromotion.getExtra());
        }
        //???????????????
        for (ChannelPromotion channelPromotionTemp : channelPromotionList) {
            if (ObjectUtil.isNotEmpty(channelPromotion.getUserid())) {
                channelPromotionTemp.setUserid(channelPromotion.getUserid());
            }
            if (StringUtils.isNotBlank(channelPromotion.getUsername())) {
                channelPromotionTemp.setUsername(channelPromotion.getUsername());
            }
        }

        this.updateBatchById(channelPromotionList);

        // ??????YouTop + ?????????
        List<PostPidParam> postPidParamList = new ArrayList<PostPidParam>();
        if (updateAliasFlag) {
            postPidParamList = DozerUtil.toBeanList(channelPromotionList, PostPidParam.class);
        }
        List<Long> idList = channelPromotionList.stream().map(ChannelPromotion::getId).collect(Collectors.toList());
        channelPromotionList = channelPromotionMapper.selectList(new LambdaQueryWrapper<ChannelPromotion>().in(ChannelPromotion::getId, idList));
        logger.info("====???02??????channelPromotionList???" + JSON.toJSONString(channelPromotionList));
        channelPromotionAllService.addBatchThread(channelPromotionList, youtopApiHost, postPidParamList);

        return true;
    }

    public static void main(String[] args) {
        Integer i = new Integer(2);
        System.out.println(i == 2);
        System.out.println("2".equals(i));
    }

    @Override
    public boolean updateSubReplace(ChannelPromotion channelPromotion) {
        //???????????????PID
        List<ChannelPromotion> channelPromotionList = channelPromotionMapper.selectSubReplace(channelPromotion);
        List<String> pidAliasDBList = channelPromotionList.stream().map(ChannelPromotion::getPidAlias).collect(Collectors.toList());

        Map<String, Integer> aliasCount = new HashMap<String, Integer>();
        for (String pidAliasDB : pidAliasDBList) {
            String pidAliasNew = pidAliasDB.replace(channelPromotion.getReplaceSource(), channelPromotion.getReplaceTarget());
            Integer lastNum = getLastNum(pidAliasNew);
            aliasCount.put(pidAliasNew, lastNum);
        }
        List<String> error55List = new ArrayList<String>();
        for (ChannelPromotion channelPromotionDB : channelPromotionList) {
            String pidAliasNew = channelPromotionDB.getPidAlias().replace(channelPromotion.getReplaceSource(), channelPromotion.getReplaceTarget());
            if (aliasCount.containsKey(pidAliasNew)) {

                Integer lastNum = aliasCount.get(pidAliasNew);
                String pidName = lastNum > 0 ? pidAliasNew.concat("_").concat(String.valueOf(lastNum)) : pidAliasNew;
                channelPromotionDB.setPidAlias(pidName);

                lastNum++;
                aliasCount.put(pidAliasNew, lastNum);

                if (pidName.length() > 55) {
                    error55List.add(channelPromotionDB.getPid());
                }
            }
        }
        if (CollectionUtil.isNotEmpty(error55List)) {
            throw new BusinessException("???????????????????????????55???" + String.join(",", error55List));
        }
        this.updateBatchById(channelPromotionList);

        if (CollectionUtil.isNotEmpty(channelPromotionList)) {
            // ??????YouTop + ?????????
            List<PostPidParam> postPidParamList = DozerUtil.toBeanList(channelPromotionList, PostPidParam.class);
            // ??????PID??????
            List<Long> idList = channelPromotionList.stream().map(ChannelPromotion::getId).collect(Collectors.toList());
            channelPromotionList = channelPromotionMapper.selectList(new LambdaQueryWrapper<ChannelPromotion>().in(ChannelPromotion::getId, idList));
            logger.info("====???01??????channelPromotionList???" + JSON.toJSONString(channelPromotionList));
            channelPromotionAllService.addBatchThread(channelPromotionList, youtopApiHost, postPidParamList);
        }
        return true;
    }

    @Override
    public String getNumByPidAlias(String pidAlias) {
        return channelPromotionMapper.getNumByPidAlias(pidAlias);
    }

    @Override
    public List<ChannelPromotion> getChannelPromotionList(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelPromotionPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelPromotionPageParam.setMapAll(mapAll);

        List<ChannelPromotion> channelPromotionList = channelPromotionMapper.getChannelPromotionList(channelPromotionPageParam, user);
        return channelPromotionList;
    }

    @Override
    public List<ChannelPromotionZaVO> getChannelPromotionListByZa(ChannelPromotionPageParam channelPromotionPageParam) throws Exception {
        log.info("pid????????????ZA???????????????" + JSON.toJSONString(channelPromotionPageParam));

        //???????????? ???????????????ID???????????????????????????BI?????? ???????????????ID????????????error????????????**ID?????????
        validation(channelPromotionPageParam);

        List<ChannelPromotionZaVO> channelPromotionZaVOList = channelPromotionMapper.getChannelPromotionListByZa(channelPromotionPageParam);
        return channelPromotionZaVOList;
    }

    public void validation(ChannelPromotionPageParam channelPromotionPageParam) {
        Long channelId = channelPromotionPageParam.getChannelId();
        if (channelId != null) {
            Integer channelNum = channelService.count(new QueryWrapper<Channel>().lambda().eq(Channel::getChannelId, channelId));
            if (channelNum == 0) {
                throw new BusinessException("??????ID?????????");
            }
        } else {
            throw new BusinessException("??????ID??????");
        }
        String subChannelId = channelPromotionPageParam.getSubChannelId();
        if (subChannelId != null) {
            Integer channelChildNum = channelChildService.count(new QueryWrapper<ChannelChild>().lambda().eq(ChannelChild::getSubChannelId, subChannelId));
            if (channelChildNum == 0) {
                throw new BusinessException("?????????ID?????????");
            }
        }
        List<Long> promoteIdList = channelPromotionPageParam.getPromoteIdList();
        if (promoteIdList != null && promoteIdList.size() > 0) {
            List<ChannelPromotionPosition> channelPromotionPositionList = channelPromotionPositionService.list(new QueryWrapper<ChannelPromotionPosition>().lambda().in(ChannelPromotionPosition::getPpId, promoteIdList));
            List<Long> ppidDB = channelPromotionPositionList.stream().map(ChannelPromotionPosition::getPpId).collect(Collectors.toList());

            List<Long> promoteIdListTemp = new ArrayList<Long>();

            for (Long promoteId : promoteIdList) {
                promoteIdListTemp.add(promoteId);
            }
            promoteIdListTemp.removeAll(ppidDB);
            if (promoteIdListTemp.size() > 0) {
                throw new BusinessException("?????????ID????????????" + JSON.toJSONString(promoteIdListTemp));
            }
        }
    }

    @Override
    public PageEntity<AppVO> getProductAndAppByCcid(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelPromotionPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelPromotionPageParam.setMapAll(mapAll);

        Page<AppVO> page = new Page<>(channelPromotionPageParam.getPageIndex(), channelPromotionPageParam.getPageSize());
        List<AppVO> result = channelPromotionMapper.getProductAndAppByCcid(page, channelPromotionPageParam, user);

        List<String> applicationIdList = result.stream().filter(i -> StringUtils.isNotBlank(i.getApplicationId())).map(AppVO::getApplicationId).distinct().collect(Collectors.toList());
        List<ChannelApplication> channelChildren = channelApplicationMapper.getApplicationNameByChild(applicationIdList);
        Map<String, String> channelChildrenMap = channelChildren.stream().collect(Collectors.toMap(s -> String.valueOf(s.getId()), s -> s.getApplicationName()));

        for (AppVO appVO : result) {
            if (channelChildrenMap.containsKey(appVO.getApplicationId())) {
                appVO.setApplicationName(channelChildrenMap.get(appVO.getApplicationId()));
            }
        }

        return new PageEntity<>(page, result);
    }

    @Override
    public List<AppVO> getAppPageListNoPage(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) {
        // ?????? By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelPromotionPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelPromotionPageParam.setMapAll(mapAll);

        List<AppVO> result = channelPromotionMapper.getProductAndAppByCcid(null, channelPromotionPageParam, user);

        List<String> applicationIdList = result.stream().filter(i -> StringUtils.isNotBlank(i.getApplicationId())).map(AppVO::getApplicationId).distinct().collect(Collectors.toList());
        Map<String, String> channelChildrenMap = new HashMap<String, String>();
        if (CollectionUtil.isNotEmpty(applicationIdList)) {
            List<ChannelApplication> channelChildren = channelApplicationMapper.getApplicationNameByChild(applicationIdList);
            channelChildrenMap = channelChildren.stream().collect(Collectors.toMap(s -> String.valueOf(s.getId()), s -> s.getApplicationName()));
        }

        for (AppVO appVO : result) {
            if (channelChildrenMap.containsKey(appVO.getApplicationId())) {
                appVO.setApplicationName(channelChildrenMap.get(appVO.getApplicationId()));
            }
        }

        return result;
    }

    @Override
    public List<Channel> settlementChannelListByBatch(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) {
        channelPromotionPageParam.setDepartmentCode(null);

        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelPromotionPageParam.setDepartmentCodeAllList(departmentCodeAllList);

        channelPromotionPageParam.setSettlementType("1");
        return channelPromotionMapper.settlementChannelList(channelPromotionPageParam);
    }

    @Override
    public List<ChannelCooperation> settlementCCIDListByBatch(ChannelPromotionPageParam channelPromotionPageParam) {
        List<String> pidList = channelPromotionPageParam.getPidList();
        List<ChannelPromotion> channelPromotionList = channelPromotionMapper.selectList(new LambdaQueryWrapper<ChannelPromotion>().in(ChannelPromotion::getId, pidList));
        List<String> productCodeList = channelPromotionList.stream().map(ChannelPromotion::getProductCode).collect(Collectors.toList());
        List<ChannelProduct> channelProductList = channelProductService.list(new LambdaQueryWrapper<ChannelProduct>().in(ChannelProduct::getProductCode, productCodeList));
        List<String> productDepartmentCodeList = channelProductList.stream().map(ChannelProduct::getDepartmentCode).collect(Collectors.toList());
        channelPromotionPageParam.setProductDepartmentCodeList(productDepartmentCodeList);

        channelPromotionPageParam.setSettlementType("1");
        return channelPromotionMapper.settlementCCIDList(channelPromotionPageParam);
    }

    @Override
    public Map settlementUpdateBatch(ChannelPromotionPageParam channelPromotionPageParam) {
        //1. ?????????????????????????????????
        List<String> idOkList = channelPromotionMapper.selectOkSettlement(channelPromotionPageParam);
        List<String> idList = channelPromotionPageParam.getPidList();

        if (CollectionUtil.isNotEmpty(idOkList)) {
            List<ChannelPromotion> channelPromotionOkList = channelPromotionMapper.selectList(new LambdaQueryWrapper<ChannelPromotion>().in(ChannelPromotion::getId, idOkList));
            channelPromotionOkList.stream().forEach(i -> i.setCcidSettlement(channelPromotionPageParam.getCcid()));
            this.updateBatchById(channelPromotionOkList);

            //?????????
            List<String> pidList = channelPromotionOkList.stream().map(ChannelPromotion::getPid).collect(Collectors.toList());
            channelPromotionAllService.updateNeijieThread(pidList, channelPromotionPageParam.getCcid());

            idList.removeAll(idOkList);
        }

        Map map = new HashMap();
        map.put("successNum", idOkList.size());
        map.put("failNum", idList.size());
        if (idList.size() > 0) {
            List<String> pidList = channelPromotionMapper.selectBatchIds(idList).stream().map(i -> i.getPid()).collect(Collectors.toList());

            map.put("failResult", "CCID??????????????????PID???????????????????????????????????????");
            map.put("failList", String.join(",", pidList));
        }

        return map;
    }
}
