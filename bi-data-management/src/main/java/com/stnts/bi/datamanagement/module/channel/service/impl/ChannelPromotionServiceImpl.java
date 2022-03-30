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
 * 渠道推广 服务实现类
 *
 * @author 刘天元
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
        //是否新增子渠道
        if (StrUtil.isNotEmpty(channelPromotion.getSubChannelName())) {
            subChannelId = newSubChannel(channelPromotion);
            channelPromotion.setSubChannelId(subChannelId);
        } else if (StrUtil.isNotEmpty(channelPromotion.getSubChannelId())) {
            subChannelId = channelPromotion.getSubChannelId();

            Integer childCount = channelChildService.count(new LambdaQueryWrapper<ChannelChild>().eq(ChannelChild::getSubChannelId, subChannelId));
            if (childCount == 0) {
                throw new BusinessException("子渠道ID不存在");
            }
        } else {
            throw new BusinessException("子渠道ID及子渠道名称不可同时为空");
        }

        String pidAlias = channelPromotion.getPidAlias();
        Integer pidNum = channelPromotion.getPidNum();

        //内结渠道名称
        /*if (ObjectUtil.isNotEmpty(channelPromotion.getChannelIdSettlement())) {
            Channel channel = channelService.getOne(new LambdaQueryWrapper<Channel>().eq(Channel::getChannelId, channelPromotion.getChannelIdSettlement()));
            channelPromotion.setChannelNameSettlement(channel.getChannelName());
        }*/

        // 获取PID集合
        List<String> pidList = channelBaseIdService.getNewPIDs(pidNum);
        if (CollectionUtil.isEmpty(pidList)) {
            throw new BusinessException("PID获取失败");
        }
        // 获取lastNum
        Integer lastNum = getLastNum(pidAlias); //by yifan 20211104
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
            cps.add(cp);
        }
        super.saveBatch(cps);

        // 推送YouTop + 存宽表
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
        //获取根据计费别名获取数据库中最新编码
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
            //参数处理
            emptyParam(channelPromotion);
            //默认值处理（CCID、子渠道、推广位、产品、应用、负责人、内结渠道名称）
            //defaultParam(channelPromotion);

            //CCID
            ChannelCooperation channelCooperation = ccidHandlerGeneral(channelPromotion);
            //子渠道
            Boolean isAdd = subChannelHandlerGeneral(channelCooperation, channelPromotion);
            if (isAdd) {
                blackSubChannelId.add(channelPromotion.getSubChannelId());
            }
            //推广位【返回非必填错误信息】
            List<String> returnErrorMsg1 = ppHandlerGeneral(channelCooperation, channelPromotion);
            //媒介【返回非必填错误信息】
            List<String> result = mediumCommon(channelCooperation, channelPromotion.getMediumId(), channelPromotion.getMediumName());
            channelPromotion.setMediumId(CollectionUtil.isNotEmpty(result) ? result.get(0) : null);
            channelPromotion.setMediumName(CollectionUtil.isNotEmpty(result) ? result.get(1) : null);
            //产品CODE + 内结渠道 + 内结CCID【返回非必填错误信息】
            List<String> returnErrorMsg2 = productHandlerGeneral(channelCooperation, channelPromotion);
            //应用ID【返回非必填错误信息】
            List<String> returnErrorMsg3 = appHandlerGeneral(channelPromotion);
            //负责人
            userHandlerGeneral(channelPromotion);

            //计费别名
            String pidAlias = channelPromotion.getPidAlias();
            Integer pidNum = channelPromotion.getPidNum();

            List<String> pidList = channelBaseIdService.getNewPIDs(pidNum);
            if (CollectionUtil.isEmpty(pidList)) {
                throw new BusinessException("PID获取失败");
            }
            //获取根据计费别名获取数据库中最新编码
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

            // 推送YouTop + 存宽表
            List<PostPidParam> postPidParam = new ArrayList<PostPidParam>();
            if (cps != null && cps.size() > 0) {
                cps = cps.stream().filter(i -> StringUtils.isNotBlank(i.getProductId())).collect(Collectors.toList());
                if (cps.size() > 0) {
                    postPidParam = DozerUtil.toBeanList(cps, PostPidParam.class);
                }
            }
            channelPromotionAllService.addBatchThread(cps, youtopApiHost, postPidParam);

            // 返回提示错误信息
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
        // 判断CCID是否存在
        ChannelCooperation channelCooperation = channelCooperationService.getOne(new QueryWrapper<ChannelCooperation>().lambda()
                .eq(ChannelCooperation::getCcid, channelPromotion.getCcid()));
        if (ObjectUtil.isEmpty(channelCooperation)) {
            throw new BusinessException("CCID不存在");
        }
        return channelCooperation;
    }

    public Boolean subChannelHandlerGeneral(ChannelCooperation channelCooperation, ChannelPromotion channelPromotion) throws Exception {
        Boolean isAdd = false;

        String subChannelId = null;
        ChannelChild channelChild = null;
        //是否新增子渠道
        if (StringUtils.isNotBlank(channelPromotion.getSubChannelId())) {
            channelChild = channelChildService.getOne(new LambdaQueryWrapper<ChannelChild>()
                    .eq(ChannelChild::getChannelId, channelCooperation.getChannelId())
                    .eq(ChannelChild::getSubChannelId, channelPromotion.getSubChannelId())
            );
            if (ObjectUtil.isEmpty(channelChild)) {
                throw new BusinessException("子渠道ID不存在：" + channelPromotion.getSubChannelId());
            } else if (StringUtils.isNotBlank(channelPromotion.getSubChannelName()) && !channelChild.getSubChannelName().equals(channelPromotion.getSubChannelName())) {
                throw new BusinessException("子渠道ID与子渠道名称不存在");
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
                returnErrorMsg.add("推广位类型参数不正确");
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
                returnErrorMsg.add("推广位不存在，渠道ID:" + channelCooperation.getChannelId() + (channelPromotion.getPpFlag() == 2 ? "，子渠道ID:" + channelPromotion.getSubChannelId() : "") + ", 推广位名称:" + channelPromotion.getPpName() + ", 推广位标识:" + (channelPromotion.getPpFlag() == 1 ? "渠道推广位" : "子渠道推广位"));
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
        // 查指定 部门或推广部门下 的当前产品
        ChannelProduct channelProduct = channelProductService.getOneByParam(channelCooperation.getDepartmentCode(), channelPromotion.getProductCode());

        if (ObjectUtil.isEmpty(channelProduct)) {
            throw new BusinessException("产品CODE（" + channelPromotion.getProductCode() + "） 对应的归属部门和推广部门不包含" + channelCooperation.getDepartmentName() + "（" + channelCooperation.getDepartmentCode() + "）");
        } else {
            channelPromotion.setProductId(channelProduct.getProductId());
            channelPromotion.setProductName(channelProduct.getProductName());
        }

        // 内结CCID：产品对应部门下渠道标记为内结的所有渠道 ，关联的CCID
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
                returnErrorMsg.add("更多设置关联的CCID不是该产品归属部门下的内结CCID，请及时更新。");
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
                    throw new BusinessException("应用不存在，ApplicationId：" + channelPromotion.getApplicationId());
                } else {
                    channelPromotion.setApplicationId(null);
                    returnErrorMsg.add("产品下没有应用，则不需要填应用");
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
                throw new BusinessException("应用不存在，ApplicationName:" + channelPromotion.getApplicationName());
            } else if (channelApplicationList.size() > 1) {
                throw new BusinessException("应用存在多个，ApplicationName:" + channelPromotion.getApplicationName());
            } else {
                channelPromotion.setApplicationId(channelApplicationList.get(0).getId());
            }
        } else {
            //应用为空
            ChannelProduct channelProduct = channelProductService.getOne(new LambdaQueryWrapper<ChannelProduct>().eq(ChannelProduct::getProductCode, channelPromotion.getProductCode()));
            String applicationIds = channelProduct.getApplicationIds();
            if (StringUtils.isNotBlank(applicationIds)) {
                throw new BusinessException("产品存在应用时，则应用必填");
            }
        }

        return returnErrorMsg;
    }

    public void userHandlerGeneral(ChannelPromotion channelPromotion) {
        List<UserVO> userVOList = exportDataService.getUser();
        userVOList = userVOList.stream().filter(t -> String.valueOf(channelPromotion.getUserid()).equals(t.getCardNumber())).collect(Collectors.toList());
        if (userVOList.size() != 1) {
            throw new BusinessException("用户工号错误");
        } else {
            channelPromotion.setUserid(Long.valueOf(userVOList.get(0).getId()));
            channelPromotion.setUsername(userVOList.get(0).getCnname());
        }
    }

    /*public void defaultParam(ChannelPromotion channelPromotion) throws Exception {
        List<String> blackSubChannelIdList = new ArrayList<String>();

        String subChannelId = null;
        // 判断CCID是否存在
        ChannelCooperation channelCooperation = channelCooperationService.getOne(new QueryWrapper<ChannelCooperation>().lambda()
                .eq(ChannelCooperation::getCcid, channelPromotion.getCcid()));
        if (ObjectUtil.isEmpty(channelCooperation)) {
            throw new BusinessException("CCID不存在");
        }

        //是否新增子渠道
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

        //推广位
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
                throw new BusinessException("推广位不存在，渠道ID:" + channelCooperation.getChannelId() + ", 名称:" + channelPromotion.getPpName() + ", Flag:" + channelPromotion.getPpFlag());
            } else {
                channelPromotion.setPpId(channelPromotionPosition.getPpId());
            }
        }

        //产品CODE
        ChannelProduct channelProduct = channelProductService.getOne(new QueryWrapper<ChannelProduct>().lambda()
                .eq(ChannelProduct::getDepartmentCode, channelCooperation.getDepartmentCode())
                .eq(ChannelProduct::getProductCode, channelPromotion.getProductCode()));
        if (ObjectUtil.isEmpty(channelProduct)) {
            throw new BusinessException("产品CODE不存在：" + channelPromotion.getProductCode());
        } else {
            channelPromotion.setProductId(channelProduct.getProductId());
            channelPromotion.setProductName(channelProduct.getProductName());
        }

        //应用名称
        if (StringUtils.isNotBlank(channelPromotion.getApplicationName())) {
            ChannelApplication channelApplication = channelApplicationMapper.selectOne(new QueryWrapper<ChannelApplication>().lambda()
                    .eq(ChannelApplication::getProductCode, channelPromotion.getProductCode())
                    .eq(ChannelApplication::getApplicationName, channelPromotion.getApplicationName())
            );
            if (ObjectUtil.isEmpty(channelApplication)) {
                throw new BusinessException("应用不存在，ProductCode" + channelPromotion.getProductCode() + ",ApplicationName:" + channelPromotion.getApplicationName());
            } else {
                //channelPromotion.setApplicationId(channelApplication.getApplicationId());
                channelPromotion.setApplicationId(channelApplication.getId());
            }
        } else {
            //应用为空
            List<ChannelApplication> channelApplicationList = channelApplicationService.list(new QueryWrapper<ChannelApplication>().lambda().eq(ChannelApplication::getProductCode, channelProduct.getProductCode()));
            if (channelApplicationList != null && channelApplicationList.size() > 0) {
                throw new BusinessException("产品存在应用时，则应用必填");
            }
        }

        //负责人
        List<UserVO> userVOList = exportDataService.getUser();
        userVOList = userVOList.stream().filter(t -> String.valueOf(channelPromotion.getUserid()).equals(t.getCardNumber())).collect(Collectors.toList());
        if (userVOList.size() != 1) {
            throw new BusinessException("用户工号错误");
        } else {
            channelPromotion.setUserid(Long.valueOf(userVOList.get(0).getId()));
            channelPromotion.setUsername(userVOList.get(0).getCnname());
        }
    }*/

    public void emptyParam(ChannelPromotion channelPromotion) {
        List<String> errorMsg = new ArrayList<String>();
        if (StringUtils.isBlank(channelPromotion.getCcid())) {
            errorMsg.add("CCID为空");
        }
        if (StringUtils.isBlank(channelPromotion.getSubChannelId()) && StringUtils.isBlank(channelPromotion.getSubChannelName())) {
            errorMsg.add("子渠道ID和子渠道名称不可同时为空");
        }
        if (StringUtils.isBlank(channelPromotion.getProductCode())) {
            errorMsg.add("产品CODE为空");
        } else if (!channelPromotion.getProductCode().matches("^[A-Z0-9]+$")) {
            errorMsg.add("产品CODE只支持数字及大写字母");
        }
        if (StringUtils.isBlank(channelPromotion.getPidAlias())) {
            errorMsg.add("计费别名为空");
        }
        if (ObjectUtil.isNull(channelPromotion.getUserid())) {
            errorMsg.add("负责人工号为空");
        }
        if (ObjectUtil.isEmpty(channelPromotion.getPidNum()) || channelPromotion.getPidNum() == 0) {
            channelPromotion.setPidNum(1);
        } else if (channelPromotion.getPidNum() > 1000) {
            errorMsg.add("生成个数最多1000个");
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
                throw new BusinessException("渠道ID不存在");
            } else if (StringUtils.isNotBlank(channelName) && !channelDBTemp.getChannelName().equals(channelName)) {
                throw new BusinessException("渠道ID与渠道名称不匹配");
            }
        } else {
            if (ObjectUtil.isEmpty(companyId)) {
                throw new BusinessException("新增渠道：参数公司ID不可为空");
            }

            //1.先在共享渠道中找 2.若没有则在部门中找
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
            throw new BusinessException("数据库中存在渠道，且渠道对应公司与参数公司不一致");
        }
        return channelDBTemp;
    }

    public Boolean saveCCPGeneralChannel(ChannelPromotionGeneral channelPromotionGeneral, Channel channelDB) {
        Boolean isAddChannel = false;

        Channel channelDBTemp = getChannelGeneral(channelPromotionGeneral.getChannelId(), channelPromotionGeneral.getChannelName(), channelPromotionGeneral.getCompanyId(), channelPromotionGeneral.getDepartmentCode());

        //新增渠道
        if (ObjectUtil.isEmpty(channelDBTemp)) {
            //公司
            Long companyId = channelPromotionGeneral.getCompanyId();
            if (ObjectUtil.isEmpty(companyId)) {
                throw new BusinessException("新增渠道时，公司ID必填");
            }
            Cooperation cooperation = cooperationMapper.selectById(companyId);
            if (ObjectUtil.isEmpty(cooperation)) {
                throw new BusinessException("公司不存在");
            } else {
                channelPromotionGeneral.setCompanyName(cooperation.getCompanyName());
            }

            //渠道
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
            channelDBTemp.setChannelId(channelId); //渠道ID
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
        //渠道名称共享渠道全局唯一，私有渠道部门下唯一
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
            throw new BusinessException("在【" + departmentNameStr + "】下，渠道名称+公司名称+合作类型已存在，请直接使用。");
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
        //新增PID
        String pidAlias = channelPromotionGeneral.getPidAlias();
        Integer pidNum = channelPromotionGeneral.getPidNum();
        List<String> pidList = channelBaseIdService.getNewPIDs(pidNum);
        if (CollectionUtil.isEmpty(pidList)) {
            throw new BusinessException("PID获取失败");
        }
        //获取根据计费别名获取数据库中最新编码
        Integer lastNum = this.getLastNum(pidAlias); //by yifan 20211104

        List<ChannelPromotion> cps = new ArrayList<>(pidNum);
        List<String> pidAliasList = new ArrayList<String>();
        for (int i = 0; i < pidNum; i++) {
            ChannelPromotion cp = DozerUtil.toBean(channelPromotionGeneral, ChannelPromotion.class);

            String pidName = lastNum > 0 ? pidAlias.concat("_").concat(String.valueOf(lastNum)) : pidAlias;
            lastNum++;

            cp.setPidAlias(pidName);
            cp.setPid(pidList.get(i));
            //（非必填）（默认：创建时间~20991231）
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

        // 推送YouTop + 存宽表
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
            //临时工号
            Long useridOld = channelPromotionGeneral.getUserid();

            //参数处理
            emptyParam(channelPromotionGeneral);
            //默认值处理（负责人+部门+业务分类+产品+应用+内结渠道+内结CCID+渠道费率+生成数量处理）
            defaultParam(channelPromotionGeneral);

            //新增渠道
            Channel channelDB = new Channel();
            Boolean isAddChannel = saveCCPGeneralChannel(channelPromotionGeneral, channelDB);
            if (isAddChannel) {
                blackChannelId.add(channelDB.getChannelId());
            }
            //新增CCID
            ChannelCooperation channelCooperationDB = new ChannelCooperation();
            Boolean isAddCCID = saveCCPGeneralCCID(channelPromotionGeneral, channelDB, channelCooperationDB);
            if (isAddCCID) {
                blackCCID.add(channelCooperationDB.getCcid());
            }
            //子渠道
            Boolean isAddSubChannel = subChannelHandlerCCP(channelDB, channelPromotionGeneral);
            String subChannelId = channelPromotionGeneral.getSubChannelId();
            if (isAddSubChannel) {
                blackSubChannelId.add(subChannelId);
            }
            //推广位
            ppHandlerCCP(channelDB, channelPromotionGeneral);
            //媒介
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

        if (StringUtils.isNotBlank(mediumIds)) { // 传媒介ID集合
            List<String> mediumIdsList = new ArrayList<>(Arrays.asList(mediumIds.split(",")).stream().map(i -> i.trim()).collect(Collectors.toList()));

            List<ChannelMedium> channelMediumDBList = channelMediumService.list(new LambdaQueryWrapper<ChannelMedium>().in(ChannelMedium::getId, mediumIdsList)
                    .eq(ChannelMedium::getDepartmentCode, channelCooperation.getDepartmentCode())
            );
            if (channelMediumDBList.size() < mediumIdsList.size()) {
                List<String> mediumIdDBList = channelMediumDBList.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.toList());
                mediumIdsList.removeAll(mediumIdDBList);
                resultErrorMsg.add("推广媒介ID不存在，部门Code：" + channelCooperation.getDepartmentCode() + "，媒介ID：" + String.join(",", mediumIdsList));
            } else if (StringUtils.isNotBlank(mediumNames)) { // 传媒介名称集合
                List<String> mediumNamesList = new ArrayList<>(Arrays.asList(mediumNames.split(",")).stream().map(i -> i.trim()).collect(Collectors.toList()));
                List<String> mediumNameDBList = channelMediumDBList.stream().map(ChannelMedium::getName).collect(Collectors.toList());

                if (mediumNamesList.size() != mediumNameDBList.size()) {
                    resultErrorMsg.add("媒介名称与参数媒介ID不匹配：" + String.join(",", mediumNamesList));
                } else {
                    List<String> mediumNamesListTemp = new ArrayList<String>(mediumNamesList);
                    mediumNamesListTemp.removeAll(mediumNameDBList);
                    if (mediumNamesListTemp.size() > 0) {
                        resultErrorMsg.add("媒介名称与参数媒介ID不匹配：" + String.join(",", mediumNamesListTemp));
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
        } else if (StringUtils.isNotBlank(mediumNames)) { // 传媒介名称集合
            List<String> mediumNamesList = new ArrayList<>(Arrays.asList(mediumNames.split(",")).stream().map(i -> i.trim()).distinct().collect(Collectors.toList()));

            List<ChannelMedium> channelMediumDBList = channelMediumService.list(new LambdaQueryWrapper<ChannelMedium>().in(ChannelMedium::getName, mediumNamesList)
                    .eq(ChannelMedium::getDepartmentCode, channelCooperation.getDepartmentCode())
            );
            List<String> mediumNameDBList = channelMediumDBList.stream().map(ChannelMedium::getName).collect(Collectors.toList());
            if (channelMediumDBList.size() < mediumNamesList.size()) {
                mediumNamesList.removeAll(mediumNameDBList);
                resultErrorMsg.add("参数媒介名称不存在，部门Code：" + channelCooperation.getDepartmentCode() + "，媒介名称：" + String.join(",", mediumNamesList));
            } else {
                List<String> mediumNamesListTemp = new ArrayList<String>(mediumNamesList);
                mediumNamesListTemp.removeAll(mediumNameDBList);
                if (mediumNamesListTemp.size() > 0) {
                    resultErrorMsg.add("媒介名称不存在：" + String.join(",", mediumNamesListTemp));
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
                throw new BusinessException("子渠道ID不存在");
            } else if (StringUtils.isNotBlank(channelPromotionGeneral.getSubChannelName()) && !channelChild.getSubChannelName().equals(channelPromotionGeneral.getSubChannelName())) {
                throw new BusinessException("子渠道ID与子渠道名称不匹配");
            }
            subChannelId = channelChild.getSubChannelId();
        } else {
            //1. 是否新增子渠道
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
                throw new BusinessException("推广位不存在，渠道ID:" + channelDB.getChannelId() + (channelPromotionGeneral.getPpFlag() == 2 ? "，子渠道ID:" + channelPromotionGeneral.getSubChannelId() : "") + ", 推广位名称:" + channelPromotionGeneral.getPpName() + ", 推广位标识:" + channelPromotionGeneral.getPpFlag());
            } else {
                channelPromotionGeneral.setPpId(channelPromotionPosition.getPpId());
            }
        }
    }

    public void defaultParam(ChannelPromotionGeneral channelPromotionGeneral) {
        //负责人工号
        List<UserVO> userVOList = exportDataService.getUser();
        Map<String, UserVO> userVOMap = userVOList.stream().collect(Collectors.toMap(s -> s.getCardNumber(), s -> s));
        if (!userVOMap.containsKey(String.valueOf(channelPromotionGeneral.getUserid()))) {
            throw new BusinessException("不存在该用户");
        } else {
            UserVO userVO = userVOMap.get(String.valueOf(channelPromotionGeneral.getUserid()));
            channelPromotionGeneral.setUserid(Long.valueOf(userVO.getId()));
            channelPromotionGeneral.setUsername(userVO.getCnname());
        }

        //部门CODE
        List<String> resultList = channelProductService.departmentCodeAndNameVaild(channelPromotionGeneral.getDepartmentCode(), channelPromotionGeneral.getDepartmentName());
        channelPromotionGeneral.setDepartmentCode(resultList.get(0));
        channelPromotionGeneral.setDepartmentName(resultList.get(1));

        //业务分类
        if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getBusinessDictId())) {
            BusinessDict businessDict = businessDictService.getOne(new LambdaQueryWrapper<BusinessDict>().eq(BusinessDict::getId, channelPromotionGeneral.getBusinessDictId()));
            if (ObjectUtil.isEmpty(businessDict)) {
                throw new BusinessException("业务分类不存在");
            } else if (!businessDict.getDepartmentCode().equals(channelPromotionGeneral.getDepartmentCode())
                    || !(businessDict.getYearStart() <= DateUtil.year(new Date()) && DateUtil.year(new Date()) <= businessDict.getYearEnd())
                    || ((org.apache.commons.lang.StringUtils.isNotBlank(channelPromotionGeneral.getFirstLevelBusiness())
                    && org.apache.commons.lang.StringUtils.isNotBlank(channelPromotionGeneral.getSecondLevelBusiness())
                    && org.apache.commons.lang.StringUtils.isNotBlank(channelPromotionGeneral.getThirdLevelBusiness()))
                    && (!channelPromotionGeneral.getFirstLevelBusiness().equals(businessDict.getFirstLevel())
                    || !channelPromotionGeneral.getSecondLevelBusiness().equals(businessDict.getSecondLevel())
                    || !channelPromotionGeneral.getThirdLevelBusiness().equals(businessDict.getThirdLevel())))) {
                throw new BusinessException("业务分类ID与部门不匹配或已失效");
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
                throw new BusinessException("业务分类有重复");
            } else if (businessDictList.size() == 0) {
                throw new BusinessException("业务分类不存在");
            } else {
                channelPromotionGeneral.setBusinessDictId(businessDictList.get(0).getId());
            }
        }
        //查对应的ProductId 及 ApplicationID
        ChannelProduct channelProductDB = channelProductService.getOne(new LambdaQueryWrapper<ChannelProduct>().eq(ChannelProduct::getProductCode, channelPromotionGeneral.getProductCode()));
        if (ObjectUtil.isEmpty(channelProductDB)) {
            throw new BusinessException("产品Code不存在：" + channelPromotionGeneral.getProductCode());
        }

        ChannelProduct channelProduct = channelProductService.getOneByParam(channelPromotionGeneral.getDepartmentCode(), channelPromotionGeneral.getProductCode());
        if (ObjectUtil.isEmpty(channelProduct)) {
            throw new BusinessException("产品CODE（" + channelPromotionGeneral.getProductCode() + "） 对应的归属部门和推广部门不包含" + channelPromotionGeneral.getDepartmentName() + "（" + channelPromotionGeneral.getDepartmentCode() + "）");
        } else {
            channelPromotionGeneral.setProductId(channelProduct.getProductId());
            channelPromotionGeneral.setProductName(channelProduct.getProductName());
        }
        if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getApplicationId())) {
            //应用
            ChannelApplication param = new ChannelApplication();
            param.setProductCodeList(Arrays.asList(channelProduct.getProductCode()));
            param.setId(channelPromotionGeneral.getApplicationId());
            List<ChannelApplication> channelApplicationList = channelApplicationMapper.selectByProductNameAndAppName(param);
            if (ObjectUtil.isEmpty(channelApplicationList)) {
                throw new BusinessException("应用不存在，ApplicationId:" + channelPromotionGeneral.getApplicationId());
            } else if (channelApplicationList.size() > 1) {
                throw new BusinessException("应用存在多个，ApplicationId:" + channelPromotionGeneral.getApplicationId());
            } else {
                channelPromotionGeneral.setApplicationId(channelApplicationList.get(0).getId());
                channelPromotionGeneral.setApplicationName(channelApplicationList.get(0).getApplicationName());
            }
        } else {
            //应用为空
            //List<ChannelApplication> channelApplicationList = channelApplicationService.list(new QueryWrapper<ChannelApplication>().lambda().eq(ChannelApplication::getProductCode, channelProduct.getProductCode()));
            if (StringUtils.isNotBlank(channelProduct.getApplicationIds())) {
                throw new BusinessException("产品存在应用时，则应用必填");
            }
        }

        if (StringUtils.isNotBlank(channelPromotionGeneral.getCcidSettlement())) {
            // 内结CCID：产品对应部门下渠道标记为内结的所有渠道 ，关联的CCID
            ChannelPromotionPageParam settlementCCIDParam = new ChannelPromotionPageParam();
            settlementCCIDParam.setDepartmentCode(channelProduct.getDepartmentCode());
            settlementCCIDParam.setSettlementType("1");
            settlementCCIDParam.setCcid(channelPromotionGeneral.getCcidSettlement());
            List<ChannelCooperation> cooperationList = channelPromotionMapper.settlementCCIDList(settlementCCIDParam);
            if (CollectionUtil.isEmpty(cooperationList)) {
                throw new BusinessException("内结CCID不存在");
            } else {
                channelPromotionGeneral.setChannelIdSettlement(cooperationList.get(0).getChannelId());
                channelPromotionGeneral.setChannelNameSettlement(cooperationList.get(0).getChannelName());
            }
        }

        //渠道费率
        if (ObjectUtil.isEmpty(channelPromotionGeneral.getChannelRate())) {
            channelPromotionGeneral.setChannelRate(BigDecimal.ZERO);
        }
        //生成数量
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
            errorMsg.add("部门CODE与部门名称不可同时为空");
        }
        if (ObjectUtil.isEmpty(channelPromotionGeneral.getBusinessDictId())
                && (StringUtils.isBlank(channelPromotionGeneral.getFirstLevelBusiness()) || StringUtils.isBlank(channelPromotionGeneral.getSecondLevelBusiness()) || StringUtils.isBlank(channelPromotionGeneral.getThirdLevelBusiness()))
        ) {
            errorMsg.add("业务分类ID 与 业务分类一二三级 不可同时为空");
        }
        if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getBusinessDictId())
                && (StringUtils.isNotBlank(channelPromotionGeneral.getFirstLevelBusiness()) || StringUtils.isNotBlank(channelPromotionGeneral.getSecondLevelBusiness()) || StringUtils.isNotBlank(channelPromotionGeneral.getThirdLevelBusiness()))
        ) {
            errorMsg.add("业务分类ID 与 业务分类一二三级 不可同时有值");
        }
        if (ObjectUtil.isEmpty(channelPromotionGeneral.getChannelId()) && StringUtils.isBlank(channelPromotionGeneral.getChannelName())) {
            errorMsg.add("渠道ID和渠道名称不可同时为空");
        }
        if (StringUtils.isBlank(channelPromotionGeneral.getChargeRule())) {
            errorMsg.add("计费方式为空");
        }
        if (StringUtils.isBlank(channelPromotionGeneral.getSubChannelId()) && StringUtils.isBlank(channelPromotionGeneral.getSubChannelName())) {
            errorMsg.add("子渠道ID和子渠道名称不可同时为空");
        }
        if (StringUtils.isBlank(channelPromotionGeneral.getProductCode())) {
            errorMsg.add("产品CODE为空");
        } else if (!channelPromotionGeneral.getProductCode().matches("^[A-Z0-9]+$")) {
            errorMsg.add("产品CODE只支持数字及大写字母");
        }
        if (StringUtils.isBlank(channelPromotionGeneral.getPidAlias())) {
            errorMsg.add("计费别名为空");
        }
        if (ObjectUtil.isNull(channelPromotionGeneral.getUserid())) {
            errorMsg.add("负责人工号为空");
        }
        if (StringUtils.isBlank(channelPromotionGeneral.getChannelType())) {
            channelPromotionGeneral.setChannelType("2");
        } else {
            ChannelTypeEnum channelTypeEnum = ChannelTypeEnum.getByKey(Integer.valueOf(channelPromotionGeneral.getChannelType()));
            if (ObjectUtil.isEmpty(channelTypeEnum)) {
                errorMsg.add("是否自营参数不正确");
            }
        }

        if (ObjectUtil.isEmpty(channelPromotionGeneral.getSecretType())) {
            channelPromotionGeneral.setSecretType(1);
        } else {
            SecretTypeEnum secretTypeEnum = SecretTypeEnum.getByKey(channelPromotionGeneral.getSecretType());
            if (ObjectUtil.isEmpty(secretTypeEnum)) {
                errorMsg.add("保密类型参数不正确");
            }
        }

        if (StringUtils.isBlank(channelPromotionGeneral.getSettlementType())) {
            channelPromotionGeneral.setSettlementType("2");
        } else {
            SettlementTypeEnum settlementTypeEnum = SettlementTypeEnum.getByKey(Integer.valueOf(channelPromotionGeneral.getSettlementType()));
            if (ObjectUtil.isEmpty(settlementTypeEnum)) {
                errorMsg.add("是否内结参数不正确");
            }
        }

        if (StringUtils.isNotBlank(channelPromotionGeneral.getChannelShareType())) {
            ChannelShareTypeEnum channelShareTypeEnum = ChannelShareTypeEnum.getByKey(Integer.valueOf(channelPromotionGeneral.getChannelShareType()));
            if (ObjectUtil.isEmpty(channelShareTypeEnum)) {
                errorMsg.add("结算指标参数不正确");
            }
        }

        if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getPpFlag())) {
            PPTypeEnum ppTypeEnum = PPTypeEnum.getByKey(channelPromotionGeneral.getPpFlag());
            if (ObjectUtil.isEmpty(ppTypeEnum)) {
                errorMsg.add("推广位类型参数不正确");
            }
        }

        if (channelPromotionGeneral.getPidNum() == null || channelPromotionGeneral.getPidNum() == 0) {
            channelPromotionGeneral.setPidNum(1);
        } else if (channelPromotionGeneral.getPidNum() > 1000) {
            errorMsg.add("生成个数最多1000个");
        }

        //chargeRule    channelShareType   price   channelShare    channelShareStep
        if (StringUtils.isNotBlank(channelPromotionGeneral.getChargeRule())) {
            List<String> one = Arrays.asList("CPS", "CPA");
            List<String> two = Arrays.asList("CPD", "CPM", "eCPM", "CPC", "CPT");
            List<String> three = Arrays.asList("CPL", "历史未归属", "盛天自运营", "不结算");
            if (one.contains(channelPromotionGeneral.getChargeRule())) {
                if (StringUtils.isBlank(channelPromotionGeneral.getChannelShareType())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "：结算指标为空");
                }
                if (("CPS".equals(channelPromotionGeneral.getChargeRule()) && !Arrays.asList("1", "2").contains(channelPromotionGeneral.getChannelShareType())) || ("CPA".equals(channelPromotionGeneral.getChargeRule()) && !Arrays.asList("3", "4").contains(channelPromotionGeneral.getChannelShareType()))) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "：结算指标参数不正确");
                }
                if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getChannelShare()) && StringUtils.isNotBlank(channelPromotionGeneral.getChannelShareStep())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "：固定分成、阶梯分成参数不可同时有值");
                }
                if (ObjectUtil.isEmpty(channelPromotionGeneral.getChannelShare()) && StringUtils.isBlank(channelPromotionGeneral.getChannelShareStep())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "：固定分成、阶梯分成参数不可同时无值");
                }
                if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getPrice())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "：单价参数不可有值");
                }
            } else if (two.contains(channelPromotionGeneral.getChargeRule())) {
                if (StringUtils.isNotBlank(channelPromotionGeneral.getChannelShareType())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "：结算指标参数不可有值");
                }
                if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getChannelShare()) || StringUtils.isNotBlank(channelPromotionGeneral.getChannelShareStep())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "：固定分成、阶梯分成参数不可有值");
                }
                if (ObjectUtil.isEmpty(channelPromotionGeneral.getPrice())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "：单价为空");
                }
            } else if (three.contains(channelPromotionGeneral.getChargeRule())) {
                if (StringUtils.isNotBlank(channelPromotionGeneral.getChannelShareType())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "：结算指标参数不可有值");
                }
                if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getChannelShare()) || StringUtils.isNotBlank(channelPromotionGeneral.getChannelShareStep())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "：固定分成参数或阶梯分成参数不可有值");
                }
                if (ObjectUtil.isNotEmpty(channelPromotionGeneral.getPrice())) {
                    errorMsg.add(channelPromotionGeneral.getChargeRule() + "：单价不可有值");
                }
            } else {
                errorMsg.add("计费方式参数不正确");
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
                .put("自运营CPS", "CPS").put("非A", "NOA").put("自然量", "ZRL").put("eCPM", "CPM")
                .put("历史未归属", "OTR").put("其他", "OTR").put("联运CPS", "CPS").put("盛天自运营", "OTR")
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
            String[] share1Sub = share1.split("＜|≤");
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
        logger.info("===================>此次导入的条数有" + channelPromotionList.size() + "条");

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
            throw new BusinessException("渠道合作不存在");
        }

        //子渠道
        List<String> subChannelNameByExcel = channelPromotionList.stream().map(ChannelPromotion::getSubChannelName).distinct().collect(Collectors.toList());
        List<ChannelChild> subChannelListByDB = channelChildService.list(new QueryWrapper<ChannelChild>().lambda().select(ChannelChild::getSubChannelId, ChannelChild::getSubChannelName)
                .in(ChannelChild::getSubChannelName, subChannelNameByExcel)
                .eq(ChannelChild::getChannelId, channelCooperation.getChannelId())
        );
        Map<String, String> subChannelMapByDB = subChannelListByDB.stream().collect(Collectors.toMap(item -> item.getSubChannelName(), item -> item.getSubChannelId()));
        List<String> subChannelIdListByDB = subChannelListByDB.stream().map(ChannelChild::getSubChannelId).collect(Collectors.toList());

        //子渠道对应推广位数量
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

        //推广位
        List<String> cppNameByExcel = channelPromotionList.stream().filter(i -> StringUtils.isNotBlank(i.getPpName())).map(ChannelPromotion::getPpName).distinct().collect(Collectors.toList());
        logger.info("===================>此次导入的推广位名称有" + JSON.toJSONString(cppNameByExcel));
        List<ChannelPromotionPosition> cppListByZQDDB = new ArrayList<ChannelPromotionPosition>();
        List<ChannelPromotionPosition> cppListByQDDB = new ArrayList<ChannelPromotionPosition>();
        if (cppNameByExcel.size() > 0) {
            cppListByQDDB = channelPromotionPositionService.list(new QueryWrapper<ChannelPromotionPosition>().lambda()
                    .in(ChannelPromotionPosition::getPpName, cppNameByExcel)
                    .eq(ChannelPromotionPosition::getChannelId, channelCooperation.getChannelId())
                    .eq(ChannelPromotionPosition::getPpStatus, 1)
                    .eq(ChannelPromotionPosition::getPpFlag, 1)
            );
            logger.info("===================>对应数据库渠道推广位有" + cppListByQDDB + "条");
            cppListByZQDDB = channelPromotionPositionService.list(new QueryWrapper<ChannelPromotionPosition>().lambda()
                    .in(ChannelPromotionPosition::getPpName, cppNameByExcel)
                    .eq(ChannelPromotionPosition::getChannelId, channelCooperation.getChannelId())
                    .eq(ChannelPromotionPosition::getPpStatus, 1)
                    .eq(ChannelPromotionPosition::getPpFlag, 2)
            );
            logger.info("===================>对应数据库子渠道推广位有" + cppListByZQDDB + "条");
        }

        // 验证产品
        //1. 参数 产品CODE, 产品名称
        Map<String, String> productByExcel = channelPromotionList.stream().collect(Collectors.toMap(item -> item.getProductCode(), item -> item.getProductName(), (v1, v2) -> v1));
        String departmentCode = channelCooperation.getDepartmentCode();
        //参数产品集合中，哪些产品 部门/推广部门 包含 指定部门
        List<ChannelProduct> productListByDB = channelProductService.getOneByParamList(departmentCode, new ArrayList<String>(productByExcel.keySet()));
        Map<String, String> productMap = productListByDB.stream().collect(Collectors.toMap(item -> item.getProductCode(), item -> item.getProductName()));
        //哪些产品 的部门/推广部门 没有包含 指定部门【错误】
        productByExcel.forEach((k, v) -> {
            if (!productMap.containsKey(k) || !v.equals(productMap.get(k))) {
                errorProduct.add(v + "（" + k + "）");
            }
        });

        // 内结CCID
        Map<String, List<ChannelCooperation>> settlementCooperationMap = new HashMap<String, List<ChannelCooperation>>();
        if (CollectionUtil.isEmpty(errorProduct)) {
            List<String> productCodeList = channelPromotionList.stream().map(ChannelPromotion::getProductCode).filter(i -> StringUtils.isNotBlank(i)).collect(Collectors.toList());
            List<String> ccidSettlementList = channelPromotionList.stream().map(ChannelPromotion::getCcidSettlement).filter(i -> StringUtils.isNotBlank(i)).collect(Collectors.toList());
            // 返回 内结CCID 集合
            if (CollectionUtil.isNotEmpty(ccidSettlementList)) {
                // 当前 产品对应部门下 内结渠道 对应的 CCID
                List<ChannelCooperation> channelCooperationList = channelService.getSettleCCIDByProd(productCodeList);
                settlementCooperationMap = channelCooperationList.stream().collect(Collectors.groupingBy(ChannelCooperation::getProductCode));
            }
        }

        // 应用
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

        //验证推广媒介
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
            //验证子渠道
            //String subChannelId = channelPromotion.getSubChannelId();
            String subChannelName = channelPromotion.getSubChannelName();
            if (!subChannelMapByDB.containsKey(subChannelName)) {
                errorSubChannel.add(subChannelName);
            } else {
                String subChannelId = subChannelMapByDB.get(subChannelName);
                channelPromotion.setSubChannelId(subChannelId);

                //验证推广位【前提：子渠道存在】
                if (StringUtils.isNotBlank(channelPromotion.getPpName())) {
                    logger.info("===================>该行推广位不为空" + channelPromotion.getPpName());
                    //如果子渠道有推广位，则判断推广位是否在子渠道推广位上。
                    if (ccTemp.containsKey(subChannelId) && ccTemp.get(subChannelId) > 0) {
                        Map<String, ChannelPromotionPosition> cppMap = cppListByZQDDB.stream().collect(Collectors.toMap(item -> item.getPpName() + item.getPpFlag(), item -> item));
                        String key = channelPromotion.getPpName() + 2;
                        logger.info("===================>该行推广位不为空且为子渠道推广位" + cppMap.keySet() + "，对应key值为：" + key);
                        //如果数据库不存在PPID
                        if (!cppMap.containsKey(key)) {
                            errorPp.add(channelPromotion.getPpName());
                        } else {
                            ChannelPromotionPosition channelPromotionPosition = cppMap.get(key);
                            channelPromotion.setPpId(channelPromotionPosition.getPpId());
                            channelPromotion.setPpName(channelPromotionPosition.getPpName());
                        }
                    } else { //如果子渠道没有推广位，则判断推广位是否在渠道推广位上。
                        Map<String, ChannelPromotionPosition> cppMap = cppListByQDDB.stream().collect(Collectors.toMap(item -> item.getPpName() + item.getPpFlag(), item -> item));
                        String key = channelPromotion.getPpName() + 1;
                        logger.info("===================>该行推广位不为空且为渠道推广位" + cppMap.keySet() + "，对应key值为：" + key);
                        //如果数据库不存在PPID
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

            //验证应用
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

            // 验证内结CCID
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
            sb.append("子渠道不存在：（" + StringUtil.join(errorSubChannel.toArray(), ",") + "），");
        }
        if (errorPp.size() > 0) {
            sb.append("推广位不存在：（" + StringUtil.join(errorPp.toArray(), ",") + "），");
        }
        if (errorProduct.size() > 0) {
            sb.append("产品（产品CODE）不存在：（" + StringUtil.join(errorProduct.toArray(), ",") + "），");
        }
        if (errorApplicationEmpty.size() > 0) {
            sb.append("产品存在应用，则应用必填：（" + StringUtil.join(errorApplicationEmpty.toArray(), ",") + "），");
        }
        if (errorApplication.size() > 0) {
            sb.append("应用不存在：（" + StringUtil.join(errorApplication.toArray(), ",") + "），");
        }
        if (errorMediumName.size() > 0) {
            sb.append("媒介不存在：（" + StringUtil.join(errorMediumName.toArray(), ",") + "），");
        }
        if (errorCCIDSettlement.size() > 0) {
            sb.append("无对应内结CCID：（" + StringUtil.join(errorCCIDSettlement.toArray(), ",") + "），");
        }

        if (sb.length() > 0) {
            throw new BusinessException(sb.substring(0, sb.length() - 1).toString());
        }

        super.saveBatch(channelPromotionAll);

        // 推送YouTop + 存宽表
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
                .filter(i -> !i.matches("[\\u4e00-\\u9fa5\\w-()（） ]*") || i.startsWith(" ") || i.endsWith(" ")).distinct().collect(Collectors.toList());
        if (pidErrorAlias != null && pidErrorAlias.size() > 0) {
            sb.append("计费别名只能存在中文/字母/数字/括号/横杠/下划线,且首尾不能有空格：（").append(StringUtil.join(pidErrorAlias.toArray(), ",")).append("）");
        }

        List<String> productError = channelPromotionList.stream().map(ChannelPromotion::getProductCode).filter(i -> !isUpper(i)).distinct().collect(Collectors.toList());
        if (productError != null && productError.size() > 0) {
            sb.append("产品CODE只有为大写：（").append(StringUtil.join(productError.toArray(), ",")).append("）");
        }


        return sb.toString();
    }

    /**
     * 判断是否全部为大写
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
            throw new BusinessException("CCID不存在");
        }
        Long channelId = channelCooperation.getChannelId();
        String subChannelId = channelBaseIdService.getNewSubChannelID(channelId);
        // update yf by 20210717(已撤回)
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

        //内结信息
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
                    throw new BusinessException("应用存在多个，ApplicationId:" + channelPromotionVO.getApplicationId());
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
                    case "易乐玩":
                        business = "yilewan";
                        break;
                    case "云飞扬":
                        business = "yfy";
                        break;
                    case "万兆":
                        business = "wanzhao";
                        break;
                    case "盛天游戏":
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
     * 1、通过名字判断，目标CCID对应的渠道下否存在该子渠道、推广位，
     * 不存在，则在该渠道下新增子渠道、推广位；(如果推广位为子渠道下推广位，保留子渠道和推广位的关系)
     * 存在，则把该PID下子渠道、推广位对应的ID更新成对应渠道下的ID;
     * 2、目标CCID对应的部门下是否存在迁移后的PID对应的产品/应用、推广媒介，
     * 不存在，则提示 “【$部门名称】与【$产品名称1，$产品名称2】从属关系不一致，无法迁移”，
     * “【$部门名称】下，【$推广媒介1，$推广媒介2】不存在”
     * 后端记录：
     * 1、pid和历史ccid映射关系，且记录ccid对应的有效期；
     * 2、有效期开始时间必填，结束时间不填默认20991231
     * 3、提交时，做时间是否交叉校验，不通过，提示“与历史有效期冲突，不能修改。”
     *
     * @param param
     * @throws Exception
     */
    @Override
    public void migration(ChannelPromotionPageParam param, HttpServletRequest request) throws Exception {
        UserEntity user = BiSessionUtil.build(redisTemplate, request).getSessionUser();
        if (param.getCcid().equals(param.getTargetCCId())) {
            throw new BusinessException("目标CCID与原CCID不能相同");
        }

        //如果目的渠道ID为空，则通过目标CCID查询渠道ID
        if (param.getTargetChannelId() == null) {
            ChannelCooperation channelCooperation = channelCooperationService.getOne(new QueryWrapper<ChannelCooperation>().lambda().eq(ChannelCooperation::getCcid, param.getTargetCCId()));
            param.setTargetChannelId(channelCooperation.getChannelId());
        }

        //原Pid
        List<String> pidList = param.getPidList();
        List<ChannelPromotion> channelPromotionList = channelPromotionMapper.selectList(new QueryWrapper<ChannelPromotion>().lambda().in(ChannelPromotion::getPid, pidList));
        Map<String, ChannelPromotion> channelPromotionMap = channelPromotionList.stream().collect(Collectors.toMap(ChannelPromotion::getPid, c -> c));
        //原CCid
        List<String> ccidList = channelPromotionList.stream().map(ChannelPromotion::getCcid).collect(Collectors.toList());
        List<ChannelCooperation> channelCooperationList = channelCooperationService.list(new QueryWrapper<ChannelCooperation>().lambda().in(ChannelCooperation::getCcid, ccidList));
        Map<String, ChannelCooperation> channelCooperationMap = channelCooperationList.stream().collect(Collectors.toMap(ChannelCooperation::getCcid, c -> c));
        //原子渠道
        List<String> subChannelIdList = channelPromotionList.stream().map(ChannelPromotion::getSubChannelId).collect(Collectors.toList());
        List<ChannelChild> channelChildList = channelChildService.list(new QueryWrapper<ChannelChild>().lambda().in(ChannelChild::getSubChannelId, subChannelIdList));
        Map<String, ChannelChild> channelChildMap = channelChildList.stream().collect(Collectors.toMap(ChannelChild::getSubChannelId, c -> c));
        //原推广位
        List<Long> ppIdList = channelPromotionList.stream().map(ChannelPromotion::getPpId).collect(Collectors.toList());
        List<ChannelPromotionPosition> channelPromotionPositionList = channelPromotionPositionService.list(new QueryWrapper<ChannelPromotionPosition>().lambda().in(ChannelPromotionPosition::getPpId, ppIdList));
        Map<Long, ChannelPromotionPosition> channelPromotionPositionMap = channelPromotionPositionList.stream().collect(Collectors.toMap(ChannelPromotionPosition::getPpId, c -> c));
        //目标渠道
        if (param.getTargetChannelId() != null) {
            ChannelCooperation channelCooperation = channelCooperationService.getOne(new QueryWrapper<ChannelCooperation>().lambda().eq(ChannelCooperation::getCcid, param.getTargetCCId()));
            param.setTargetChannelId(channelCooperation.getChannelId());
        }
        //目标子渠道
        List<ChannelChild> targetChannelChildList = channelChildService.list(new QueryWrapper<ChannelChild>().lambda().in(ChannelChild::getChannelId, param.getTargetChannelId()));
        Map<String, ChannelChild> targetChannelChildMap = targetChannelChildList.stream().collect(Collectors.toMap(ChannelChild::getSubChannelName, c -> c));
        List<String> targetChannelChildIdList = targetChannelChildList.stream().map(ChannelChild::getSubChannelId).distinct().collect(Collectors.toList());
        //目标渠道推广位
        List<ChannelPromotionPosition> targetChannelPromotionPositionQDList = channelPromotionPositionService.list(new QueryWrapper<ChannelPromotionPosition>().lambda()
                .eq(ChannelPromotionPosition::getChannelId, param.getTargetChannelId())
                .eq(ChannelPromotionPosition::getPpFlag, 1)
        );
        Map<String, ChannelPromotionPosition> targetChannelPromotionPositionQDMap = targetChannelPromotionPositionQDList.stream()
                .collect(Collectors.toMap(i -> i.getPpName(), c -> c));
        Map<String, ChannelPromotionPosition> targetChannelPromotionPositionQDMapMax = targetChannelPromotionPositionQDList.stream()
                .collect(Collectors.toMap(i -> i.getPpName() + i.getChannelId(), c -> c));
        //目标子渠道推广位
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
        //目标渠道推广位+子渠道推广痊
        Map<String, ChannelPromotionPosition> targetChannelPromotionPositionMap = new HashMap<String, ChannelPromotionPosition>();
        targetChannelPromotionPositionMap.putAll(targetChannelPromotionPositionQDMap);
        targetChannelPromotionPositionMap.putAll(targetChannelPromotionPositionZQDMap);

        //目标存在的应用
        List<ChannelCooperation> targetChannelCooperationList = channelCooperationService.list(new QueryWrapper<ChannelCooperation>().select("department_code, department_name").lambda().eq(ChannelCooperation::getCcid, param.getTargetCCId()));
        //目标CCID的部门对应的产品/应用/推广媒介
        String targetDepartmentCode = targetChannelCooperationList.get(0).getDepartmentCode();
        String targetDepartmentName = targetChannelCooperationList.get(0).getDepartmentName();
        ChannelProductPageParam channelProductPageParam = new ChannelProductPageParam();
        channelProductPageParam.setKeyword(targetDepartmentCode);
        List<ChannelProduct> targetChannelProductList = channelProductService.selectListProduct(channelProductPageParam); //1个ccid只对应1个部门
        List<String> targetProductCodeList = targetChannelProductList.stream().map(ChannelProduct::getProductCode).distinct().collect(Collectors.toList());

        ChannelApplication channelApplicationParam = new ChannelApplication();
        channelApplicationParam.setProductCodeList(targetProductCodeList);
        List<ChannelApplication> targetChannelApplicationList = channelApplicationMapper.selectByProductNameAndAppName(channelApplicationParam);
        List<Long> targetApplicationIdList = targetChannelApplicationList.stream().map(ChannelApplication::getId).collect(Collectors.toList());

        List<ChannelMedium> targetChannelMediumList = channelMediumService.list(new QueryWrapper<ChannelMedium>().lambda().eq(ChannelMedium::getDepartmentCode, targetDepartmentCode));
        List<String> targetMediumIdList = targetChannelMediumList.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.toList());
        logger.info("迁移，目标媒介ID集合" + JSON.toJSONString(targetMediumIdList));

        List<String> errorRowBackSubChannel = new ArrayList<String>();
        Date targetCheckStartDate = param.getCheckStartDate();
        Date targetCheckEndDate = param.getCheckEndDate();
        List errorProductCode = new ArrayList<String>();
        List errorApplicationId = new ArrayList<String>();
        List errorMediumId = new ArrayList<String>();
        List errorCPPZYName = new ArrayList<String>();  //子渠道推广位被推广位占用
        List errorCPPZQDName = new ArrayList<String>(); //渠道推广位被子渠道推广位占用
        List errorCPPQDName = new ArrayList<String>();  //被占用
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

                //----------------------判断迁移时间/产品/应用/推广媒介----------------------------
                //存在时间交叉，且当天生成的PID不能当天迁移，报错
                if (checkStartDate != null && targetCheckStartDate.getTime() <= checkStartDate.getTime()) {
                    errorByTime.add(channelPromotion.getPid());
                }
                //1个applicationId对应1组产品+应用
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
                //验证推广位-1，迁移时，如果【原子渠道推广位名称，出现在目标渠道推广位中】，则报错
                if (StringUtils.isNotBlank(ppName) && (targetChannelPromotionPositionQDMap.size() > 0 && targetChannelPromotionPositionQDMap.containsKey(ppName) && ppType == 2)) {
                    errorCPPZQDName.add(ppName);
                }
                //如果【原渠道推广位名称，出现在目标子渠道推广位中】，则报错
                if (StringUtils.isNotBlank(ppName) && (targetChannelPromotionPositionZQDMap.size() > 0 && targetChannelPromotionPositionZQDMap.containsKey(ppName) && ppType == 1)) {
                    errorCPPQDName.add(ppName);
                }
                // 验证推广位-2，新建子渠道时，判断推广位是否被占用【渠道1 子1 子推1】 -> 【渠道2 子2 子推1】（目标渠道下有空的子渠道 子1）
                // 同类推广位，占用
                if (StringUtils.isNotBlank(ppName) && !errorCPPZQDName.contains(ppName) && !errorCPPQDName.contains(ppName) && !errorCPPZYName.contains(ppName)) {
                    //特殊场景：子渠道需要新增，但目的子渠道下推广位出现相同名称，此时则不能使用相同名称的推广位，否则子渠道推广位无法与新建的子渠道有效关联，报【占用】
                    if (!targetChannelChildMap.containsKey(subChannelName) && targetChannelPromotionPositionZQDMap.containsKey(ppName)) {
                        errorCPPZYName.add(ppName);
                    }
                    //如果子渠道是存在的，子渠道推广位存在，但是子渠道推广位确在别的子渠道下，则报错
                    if (targetChannelChildMap.containsKey(subChannelName) && targetChannelPromotionPositionZQDMap.containsKey(ppName)
                            && !targetChannelPromotionPositionZQDMapMax.containsKey(ppName + targetChannelChildMap.get(subChannelName).getSubChannelId())) {
                        errorCPPZYName.add(ppName);
                    }
                    //渠道推广位，在目标子渠道推广位中出现
                    if (channelPromotionPosition.getPpFlag() != null && channelPromotionPosition.getPpFlag() == 1 && targetChannelPromotionPositionZQDMap.containsKey(ppName)) {
                        errorCPPZYName.add(ppName);
                    }
                }
                //---------------------------判断子渠道/推广位---------------------------
                //如果部门、产品、应用、媒介没有错误信息
                if (errorByTime.size() == 0 && errorProductCode.size() == 0 && errorApplicationId.size() == 0 && errorMediumId.size() == 0
                        && errorCPPZQDName.size() == 0 && errorCPPQDName.size() == 0 && errorCPPZYName.size() == 0) {
                    //根据子渠道名称查没有对应
                    if (!targetChannelChildMap.containsKey(subChannelName)) {
                        //新增子渠道
                        ChannelChild channelChildNew = new ChannelChild();
                        // update yf by 20210717(已撤回)
                        channelChildNew.setCcid(String.valueOf(param.getTargetCCId()));
                        channelChildNew.setChannelId(param.getTargetChannelId());
                        channelChildNew.setSubChannelName(subChannelName);
                        Map<Object, Object> channelChildRes = channelChildService.saveChannelChild(channelChildNew);
                        channelChildNew.setSubChannelId(String.valueOf(channelChildRes.get("subChannelId")));
                        if (StringUtils.isNotBlank(param.getDataSource())) {
                            channelChildNew.setDataSource(param.getDataSource());
                        }
                        targetChannelChildMap.put(subChannelName, channelChildNew);
                        //若程序报错，删除该List子渠道
                        errorRowBackSubChannel.add(String.valueOf(channelChildRes.get("subChannelId")));
                    }

                    ChannelChild channelChildNow = targetChannelChildMap.get(subChannelName);
                    //根据推广位名称查没有对应
                    // 渠道推广位
                    if (StringUtils.isNotBlank(ppName) && ppType == 1 && !targetChannelPromotionPositionQDMapMax.containsKey(ppName + param.getTargetChannelId())) {
                        //新增推广位
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
                        //新增推广位
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

                    //迁移设置新子渠道ID和新推广位ID
                    channelPromotion.setCcid(String.valueOf(param.getTargetCCId()));
                    channelPromotion.setSubChannelId(channelChildNow.getSubChannelId());
                    if (StringUtils.isNotBlank(ppName)) {
                        if (targetChannelPromotionPositionQDMapMax.containsKey(ppName + param.getTargetChannelId())) {
                            channelPromotion.setPpId(targetChannelPromotionPositionQDMapMax.get(ppName + param.getTargetChannelId()).getPpId());
                        } else if (targetChannelPromotionPositionZQDMapMax.containsKey(ppName + channelChildNow.getSubChannelId())) {
                            channelPromotion.setPpId(targetChannelPromotionPositionZQDMapMax.get(ppName + channelChildNow.getSubChannelId()).getPpId());
                        }
                    }
                    //时间
                    channelPromotion.setCheckStartDate(targetCheckStartDate);
                    channelPromotion.setCheckEndDate(targetCheckEndDate == null ? DateUtils.parseDate("2099-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss") : targetCheckEndDate);
                }
            }
        } catch (Exception e) {
            //删除无法回滚的子渠道
            if (errorRowBackSubChannel.size() > 0) {
                channelChildService.remove(new QueryWrapper<ChannelChild>().lambda().in(ChannelChild::getSubChannelId, errorRowBackSubChannel));
            }
            throw e;
        }

        //【$部门名称】与【$产品名称1，$产品名称2】从属关系不一致，无法迁移
        StringBuffer sb = new StringBuffer("");
        if (errorByTime.size() > 0) {
            String str = String.format("有效期冲突：（%s），", org.apache.commons.lang3.StringUtils.join(errorByTime, "、"));
            sb.append(str);
        }
        if (errorProductCode.size() > 0) {
            List<ChannelProduct> errorChannelProductList = channelProductService.list(new QueryWrapper<ChannelProduct>().lambda().select(ChannelProduct::getProductName)
                    .in(ChannelProduct::getProductCode, errorProductCode));
            List<String> errorChannelProductNameList = errorChannelProductList.stream().map(ChannelProduct::getProductName).collect(Collectors.toList());
            String str = String.format("产品从属不一致：（%s），", org.apache.commons.lang3.StringUtils.join(errorChannelProductNameList, "、"));
            sb.append(str);
        }
        if (errorApplicationId.size() > 0) {
            List<ChannelApplication> errorChannelApplicationList = channelApplicationService.list(new QueryWrapper<ChannelApplication>().lambda().select(ChannelApplication::getApplicationName).in(ChannelApplication::getId, errorApplicationId));
            List<String> errorChannelApplicationNameList = errorChannelApplicationList.stream().map(ChannelApplication::getApplicationName).collect(Collectors.toList());
            String str = String.format("应用从属不一致：（%s），", org.apache.commons.lang3.StringUtils.join(errorChannelApplicationNameList, "、"));
            sb.append(str);
        }
        if (errorMediumId.size() > 0) {
            List<ChannelMedium> errorChannelMediumList = channelMediumService.list(new QueryWrapper<ChannelMedium>().lambda().select(ChannelMedium::getName).in(ChannelMedium::getId, errorMediumId));
            List<String> errorChannelMediumNameList = errorChannelMediumList.stream().map(ChannelMedium::getName).collect(Collectors.toList());
            String str = String.format("媒介从属不一致：（%s），", org.apache.commons.lang3.StringUtils.join(errorChannelMediumNameList, "、"));
            sb.append(str);
        }
        if (errorCPPZQDName.size() > 0) {
            String str = String.format("子渠道推广位被推广位占用：（%s），", org.apache.commons.lang3.StringUtils.join(errorCPPZQDName, "、"));
            sb.append(str);
        }
        if (errorCPPQDName.size() > 0) {
            String str = String.format("渠道推广位被子渠道推广位占用：（%s），", org.apache.commons.lang3.StringUtils.join(errorCPPQDName, "、"));
            sb.append(str);
        }
        if (errorCPPZYName.size() > 0) {
            String str = String.format("推广位被占用：（%s），", org.apache.commons.lang3.StringUtils.join(errorCPPZYName, "、"));
            sb.append(str);
        }
        if (sb.length() > 0) {
            sb.insert(0, "" + targetDepartmentName + "下，").append("无法迁移");
        }

        if (sb.length() > 0) {
            throw new BusinessException(sb.toString());
        }
        //最后更新PID信息
        this.saveOrUpdateBatch(channelPromotionList);

        //存宽表
        channelPromotionAllService.migrationThread(channelPromotionList);

        //新增迁移记录
        channelPromotionHistoryService.saveBatch(channelPromotionHistoryList);
    }

    @Override
    public Map<String, Object> searchList(ChannelPromotionPageParam param, HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        param.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        param.setMapAll(mapAll);

        //参数处理
        paramCheck(param);

        List<ChannelPromotionVO> channelPromotionVOList = channelPromotionMapper.searchList(param, user);

        //部门
        Set<Map<String, String>> departmentList = new HashSet<Map<String, String>>();
        //渠道
        Set<Map<String, String>> channelMapList = new HashSet<Map<String, String>>();
        //子渠道
        Set<Map<String, String>> subChannelMapList = new HashSet<Map<String, String>>();
        //产品/应用
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
                    map.put("prodAppValue", channelPromotion.getProductName() + "（" + channelPromotion.getProductCode() + "）" + (StringUtils.isNotBlank(channelPromotion.getApplicationId()) ? "/" + channelPromotion.getApplicationName() : ""));

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
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelPromotionPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelPromotionPageParam.setMapAll(mapAll);

        //当前CCID对应部门下的所有CCID对应的渠道列表
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
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelPromotionPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelPromotionPageParam.setMapAll(mapAll);

        //当前CCID对应部门下的所有CCID
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
        //判断有效期时间与历史有效期是否有重叠
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
            throw new BusinessException("有效期时间与历史有效期存在重叠");
        }

        //是否新增子渠道
        if (StrUtil.isNotEmpty(channelPromotion.getSubChannelName())) {
            String subChannelId = newSubChannel(channelPromotion);
            channelPromotion.setSubChannelId(subChannelId);
        } else {
            Integer childCount = channelChildService.count(new LambdaQueryWrapper<ChannelChild>().eq(ChannelChild::getSubChannelId, channelPromotion.getSubChannelId()));
            if (childCount == 0) {
                throw new BusinessException("子渠道ID不存在");
            }
        }

        //设置计费别名
        ChannelPromotion channelPromotionDB = channelPromotionMapper.selectById(channelPromotion.getId());
        if (ObjectUtil.isEmpty(channelPromotionDB)) {
            throw new BusinessException("PID不存在");
        }
        //是否修改计费别名
        Boolean updateAliasFlag = false;
        String pidName = channelPromotion.getPidAlias();
        if (!pidName.equals(channelPromotionDB.getPidAlias())) {
            updateAliasFlag = true;
            Boolean flag = true;
            if (pidName.contains("_")) {
                List<String> list = Arrays.asList(pidName.split("_"));
                String last = list.get(list.size() - 1);
                if (NumberUtil.isNumber(last)) {
                    //带数字
                    ChannelPromotion cpOne = channelPromotionMapper.selectOne(new QueryWrapper<ChannelPromotion>().lambda().eq(ChannelPromotion::getPidAlias, pidName));
                    if (cpOne != null) {
                        //已存在，则去掉数字，自然新增
                        pidName = pidName.substring(0, pidName.lastIndexOf("_"));
                    } else {
                        //不存在则直接新增
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

        // 推送YouTop + 存宽表
        List<PostPidParam> postPidParamList = new ArrayList<PostPidParam>();
        if (updateAliasFlag) {
            ChannelProduct channelProduct = channelProductService.getOne(new QueryWrapper<ChannelProduct>().lambda().eq(ChannelProduct::getProductCode, channelPromotionDB.getProductCode()));
            channelPromotion.setProductId(channelProduct.getProductId());
            PostPidParam postPidParam = DozerUtil.toBean(channelPromotion, PostPidParam.class);
            postPidParamList.add(postPidParam);
        }
        List<ChannelPromotion> channelPromotionList = channelPromotionMapper.selectList(new LambdaQueryWrapper<ChannelPromotion>().eq(ChannelPromotion::getId, channelPromotion.getId()));
        logger.info("====》03补查channelPromotionList：" + JSON.toJSONString(channelPromotionList));
        channelPromotionAllService.addBatchThread(channelPromotionList, youtopApiHost, postPidParamList);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteChannelPromotion(Long id) throws Exception {
        //存宽表
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
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        cParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        cParam.setMapAll(mapAll);

        //参数处理
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

        // 媒介
        List<Long> mediumIdList = channelPromotionVOList.stream().map(ChannelPromotionVO::getMediumId).distinct().map(m -> StrUtil.split(m, ",")).flatMap(Arrays::stream).filter(StrUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList());
        List<ChannelMedium> channelMediumList = CollectionUtil.isNotEmpty(mediumIdList) ? channelMediumService.list(new LambdaQueryWrapper<ChannelMedium>().in(ChannelMedium::getId, mediumIdList)) : Collections.emptyList();
        Map<Long, String> mediumIdToName = channelMediumList.stream().collect(Collectors.toMap(ChannelMedium::getId, ChannelMedium::getName));

        for (ChannelPromotionVO channelPromotionVO : channelPromotionVOList) {
            String mediumId = channelPromotionVO.getMediumId();
            if (StrUtil.isNotEmpty(mediumId)) {
                //存在媒介
                List<String> strings = Arrays.stream(mediumId.split(",")).map(id -> mediumIdToName.get(Long.parseLong(id))).filter(i -> StringUtils.isNotBlank(i)).collect(Collectors.toList());
                channelPromotionVO.setMediumName(StrUtil.join(",", strings));
            }
        }

        return channelPromotionVOList;
    }

    /**
     * 参数处理
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

                    //应用名称暂时排序不了
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
                throw new BusinessException("非法排序参数：" + org.apache.commons.lang.StringUtils.join(errorOrder, ","));
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
            throw new BusinessException("idList参数为空");
        } else if (CollectionUtil.isEmpty(channelPromotion.getIdList()) && ObjectUtil.isNotEmpty(channelPromotion.getId())) {
            List<Long> idList = new ArrayList<Long>();
            idList.add(channelPromotion.getId());
            channelPromotion.setIdList(idList);
        }

        List<ChannelPromotion> channelPromotionList = channelPromotionMapper.selectBatchIds(channelPromotion.getIdList());

        //修改有效期
        if (ObjectUtil.isNotEmpty(channelPromotion.getCheckStartDate()) && ObjectUtil.isNotEmpty(channelPromotion.getCheckEndDate())) {
            List<String> pidList = channelPromotionList.stream().map(ChannelPromotion::getPid).collect(Collectors.toList());
            //判断有效期时间与历史有效期是否有重叠
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
                throw new BusinessException("有效期时间与历史有效期存在重叠:" + String.join(",", errorTime));
            }

            for (ChannelPromotion channelPromotionTemp : channelPromotionList) {
                channelPromotionTemp.setCheckStartDate(channelPromotion.getCheckStartDate());
                channelPromotionTemp.setCheckEndDate(channelPromotion.getCheckEndDate());
            }
        }
        //修改计费别名
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
                    //设置别名
                    String pidName = lastNum > 0 ? pidAlias.concat("_").concat(String.valueOf(lastNum)) : pidAlias;
                    lastNum++;

                    channelPromotionTemp.setPidAlias(pidName);
                }

                //设置产品ID
                if (channelPromotionMap.containsKey(channelPromotionTemp.getProductCode())) {
                    channelPromotionTemp.setProductId(channelPromotionMap.get(channelPromotionTemp.getProductCode()));
                }
            }
        }
        //修改拓展字段
        for (ChannelPromotion channelPromotionTemp : channelPromotionList) {
            channelPromotionTemp.setExtra(channelPromotion.getExtra());
        }
        //修改负责人
        for (ChannelPromotion channelPromotionTemp : channelPromotionList) {
            if (ObjectUtil.isNotEmpty(channelPromotion.getUserid())) {
                channelPromotionTemp.setUserid(channelPromotion.getUserid());
            }
            if (StringUtils.isNotBlank(channelPromotion.getUsername())) {
                channelPromotionTemp.setUsername(channelPromotion.getUsername());
            }
        }

        this.updateBatchById(channelPromotionList);

        // 推送YouTop + 存宽表
        List<PostPidParam> postPidParamList = new ArrayList<PostPidParam>();
        if (updateAliasFlag) {
            postPidParamList = DozerUtil.toBeanList(channelPromotionList, PostPidParam.class);
        }
        List<Long> idList = channelPromotionList.stream().map(ChannelPromotion::getId).collect(Collectors.toList());
        channelPromotionList = channelPromotionMapper.selectList(new LambdaQueryWrapper<ChannelPromotion>().in(ChannelPromotion::getId, idList));
        logger.info("====》02补查channelPromotionList：" + JSON.toJSONString(channelPromotionList));
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
        //符合条件的PID
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
            throw new BusinessException("替换后别名长度大于55：" + String.join(",", error55List));
        }
        this.updateBatchById(channelPromotionList);

        if (CollectionUtil.isNotEmpty(channelPromotionList)) {
            // 推送YouTop + 存宽表
            List<PostPidParam> postPidParamList = DozerUtil.toBeanList(channelPromotionList, PostPidParam.class);
            // 查询PID集合
            List<Long> idList = channelPromotionList.stream().map(ChannelPromotion::getId).collect(Collectors.toList());
            channelPromotionList = channelPromotionMapper.selectList(new LambdaQueryWrapper<ChannelPromotion>().in(ChannelPromotion::getId, idList));
            logger.info("====》01补查channelPromotionList：" + JSON.toJSONString(channelPromotionList));
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
        // 权限 By yf
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
        log.info("pid信息查询ZA，请示参数" + JSON.toJSONString(channelPromotionPageParam));

        //需要验证 传入的所有ID是不是正确，如果在BI平台 查询不到此ID，则返回error，并告知**ID不存在
        validation(channelPromotionPageParam);

        List<ChannelPromotionZaVO> channelPromotionZaVOList = channelPromotionMapper.getChannelPromotionListByZa(channelPromotionPageParam);
        return channelPromotionZaVOList;
    }

    public void validation(ChannelPromotionPageParam channelPromotionPageParam) {
        Long channelId = channelPromotionPageParam.getChannelId();
        if (channelId != null) {
            Integer channelNum = channelService.count(new QueryWrapper<Channel>().lambda().eq(Channel::getChannelId, channelId));
            if (channelNum == 0) {
                throw new BusinessException("渠道ID不存在");
            }
        } else {
            throw new BusinessException("渠道ID必填");
        }
        String subChannelId = channelPromotionPageParam.getSubChannelId();
        if (subChannelId != null) {
            Integer channelChildNum = channelChildService.count(new QueryWrapper<ChannelChild>().lambda().eq(ChannelChild::getSubChannelId, subChannelId));
            if (channelChildNum == 0) {
                throw new BusinessException("子渠道ID不存在");
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
                throw new BusinessException("推广位ID不存在：" + JSON.toJSONString(promoteIdListTemp));
            }
        }
    }

    @Override
    public PageEntity<AppVO> getProductAndAppByCcid(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception {
        // 权限 By yf
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
        // 权限 By yf
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
        //1. 获取可以成功修改的名单
        List<String> idOkList = channelPromotionMapper.selectOkSettlement(channelPromotionPageParam);
        List<String> idList = channelPromotionPageParam.getPidList();

        if (CollectionUtil.isNotEmpty(idOkList)) {
            List<ChannelPromotion> channelPromotionOkList = channelPromotionMapper.selectList(new LambdaQueryWrapper<ChannelPromotion>().in(ChannelPromotion::getId, idOkList));
            channelPromotionOkList.stream().forEach(i -> i.setCcidSettlement(channelPromotionPageParam.getCcid()));
            this.updateBatchById(channelPromotionOkList);

            //存宽表
            List<String> pidList = channelPromotionOkList.stream().map(ChannelPromotion::getPid).collect(Collectors.toList());
            channelPromotionAllService.updateNeijieThread(pidList, channelPromotionPageParam.getCcid());

            idList.removeAll(idOkList);
        }

        Map map = new HashMap();
        map.put("successNum", idOkList.size());
        map.put("failNum", idList.size());
        if (idList.size() > 0) {
            List<String> pidList = channelPromotionMapper.selectBatchIds(idList).stream().map(i -> i.getPid()).collect(Collectors.toList());

            map.put("failResult", "CCID对应的部门和PID关联的产品对应的部门不一致");
            map.put("failList", String.join(",", pidList));
        }

        return map;
    }
}
