package com.stnts.bi.datamanagement.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.datamanagement.config.DataManagementConfig;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.business.service.BusinessDictService;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelChild;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotion;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelChildMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelCooperationMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelMapper;
import com.stnts.bi.datamanagement.module.channel.param.PostPidParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelBaseIdService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionAllService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionService;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.mapper.CooperationMapper;
import com.stnts.bi.datamanagement.service.ApiService;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.datamanagement.vo.ApiSimpleVO;
import com.stnts.bi.datamanagement.vo.ApiVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/12
 */
@EnableTransactionManagement
@Service
@Slf4j
public class ApiServiceImpl implements ApiService {
    @Value("${data-management.setting.youtop-api-host}")
    private String youtopApiHost;
    @Autowired
    private DataManagementConfig dataManagementConfig;
    @Autowired
    private EnvironmentProperties properties;
    @Autowired
    private CooperationMapper cooperationMapper;
    @Autowired
    private ChannelMapper channelMapper;
    @Autowired
    private ChannelChildMapper channelChildMapper;
    @Autowired
    private ChannelPromotionService channelPromotionService;
    @Autowired
    private ChannelCooperationMapper channelCooperationMapper;
    @Autowired
    private ChannelBaseIdService channelBaseIdService;
    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;
    @Autowired
    private BusinessDictService businessDictService;

    private Map<Object, Object> initPid(ApiVO apiVO) {

        /**
         * 串联维表
         * 1.如果没有渠道,则需要建立渠道信息
         * 2.建CCID
         * 3.建PID
         */
        try {
            checkDict(apiVO);
            companyHandler(apiVO);
            channelHandler(apiVO);
            ccidHandler(apiVO);
            subChannelHandler(apiVO);
            pidHandler(apiVO);
            return MapUtil.builder()
                    .put("channelId", apiVO.getChannelId())
                    .put("subChannelId", apiVO.getSubChannelId())
                    .put("ccid", apiVO.getCcid())
                    .put("pidList", apiVO.getPidList())
                    .build();
        } catch (Exception e) {
            log.warn("生成PID报错, 异常信息: {}", e.getMessage());
            throw new BusinessException("生成PID报错, 异常信息: " + e.getMessage());
        }
    }

    public void checkDict(ApiVO apiVO) {
        if (ObjectUtil.isNotEmpty(apiVO.getBusinessDictId())) {
            BusinessDict businessDict = businessDictService.getOne(new LambdaQueryWrapper<BusinessDict>().eq(BusinessDict::getId, apiVO.getBusinessDictId()));
            if (ObjectUtil.isEmpty(businessDict)) {
                throw new BusinessException("业务分类不存在");
            } else if (!businessDict.getDepartmentCode().equals(apiVO.getDepartmentCode())
                    || !(businessDict.getYearStart() <= DateUtil.year(new Date()) && DateUtil.year(new Date()) <= businessDict.getYearEnd())
                    || ((StringUtils.isNotBlank(apiVO.getFirstLevelBusiness())
                    && StringUtils.isNotBlank(apiVO.getSecondLevelBusiness())
                    && StringUtils.isNotBlank(apiVO.getThirdLevelBusiness()))
                    && (!apiVO.getFirstLevelBusiness().equals(businessDict.getFirstLevel())
                    || !apiVO.getSecondLevelBusiness().equals(businessDict.getSecondLevel())
                    || !apiVO.getThirdLevelBusiness().equals(businessDict.getThirdLevel())))) {
                throw new BusinessException("业务分类ID与部门不匹配或已失效");
            } else {
                apiVO.setFirstLevelBusiness(businessDict.getFirstLevel());
                apiVO.setSecondLevelBusiness(businessDict.getSecondLevel());
                apiVO.setThirdLevelBusiness(businessDict.getThirdLevel());
            }
        } else {
            List<BusinessDict> businessDictList = businessDictService.list(new QueryWrapper<BusinessDict>().lambda()
                    .eq(BusinessDict::getDepartmentCode, apiVO.getDepartmentCode())
                    .le(BusinessDict::getYearStart, DateUtil.year(new Date()))
                    .ge(BusinessDict::getYearEnd, DateUtil.year(new Date()))
                    .eq(BusinessDict::getFirstLevel, apiVO.getFirstLevelBusiness())
                    .eq(BusinessDict::getSecondLevel, apiVO.getSecondLevelBusiness())
                    .eq(BusinessDict::getThirdLevel, apiVO.getThirdLevelBusiness())
                    .eq(BusinessDict::getIsValid, 1)
            );
            if (businessDictList.size() > 1) {
                throw new BusinessException("业务分类有重复");
            } else if (businessDictList.size() == 0) {
                throw new BusinessException("业务分类不存在");
            } else {
                apiVO.setBusinessDictId(businessDictList.get(0).getId());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<Object, Object> initPid4Ylw(ApiVO apiVO) {

        apiVO.setMediumIds(dataManagementConfig.getYlwMediumId());
        apiVO.setProductCode(String.valueOf(dataManagementConfig.getYlwProductCode()));
        apiVO.setProductId(dataManagementConfig.getYlwProductId());
        return initPid(apiVO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<Object, Object> initPid4YlwSimple(ApiSimpleVO apiSimpleVO) {

        String ccid = apiSimpleVO.getCcid();
        ChannelCooperation channelCooperation = channelCooperationMapper.selectOne(new QueryWrapper<ChannelCooperation>().lambda()
                .eq(ChannelCooperation::getCcid, ccid));
        if (null == ccid) {
            throw new BusinessException("ccid不存在, 请确认");
        }
        ApiVO apiVO = new ApiVO()
                .setChannelId(channelCooperation.getChannelId())
                .setSubChannelId(apiSimpleVO.getSubChannelId())
                .setSubChannelName(apiSimpleVO.getSubChannelName())
                .setPidAlias(apiSimpleVO.getPidAlias());
        apiVO.setCcid(ccid);
        apiVO.setProductCode(String.valueOf(dataManagementConfig.getYlwProductCode()));
        apiVO.setProductId(dataManagementConfig.getYlwProductId());
        apiVO.setMediumIds(dataManagementConfig.getYlwMediumId());
        apiVO.setUserid(channelCooperation.getUserid());
        apiVO.setUsername(channelCooperation.getUsername());
        if (StringUtils.isNotBlank(apiSimpleVO.getDataSource())) {
            apiVO.setDataSource(apiSimpleVO.getDataSource());
        }
        subChannelHandler(apiVO);
        pidHandler(apiVO);
        return MapUtil.builder()
                .put("channelId", apiVO.getChannelId())
                .put("subChannelId", apiVO.getSubChannelId())
                .put("ccid", apiVO.getCcid())
                .put("pidList", apiVO.getPidList())
                .build();
    }

    /**
     * 子渠道处理
     * 如果子渠道ID存在，则验证一下数据是否合法
     * 如果子渠道ID不存在，子渠道名也不存在，则创建同渠道名的子渠道
     * 如果子渠道ID不存在，子渠道名存在，则创建子渠道
     *
     * @param apiVO
     * @return
     */
    private ChannelChild subChannelHandler(ApiVO apiVO) {

        String subChannelId = apiVO.getSubChannelId();
        String subChannelName = apiVO.getSubChannelName();
        //ID不为空，名称不为空  验证
        if (StringUtils.isNoneBlank(subChannelId) && StrUtil.isNotEmpty(subChannelName)) {
            Integer selectCount = channelChildMapper.selectCount(new QueryWrapper<ChannelChild>().lambda()
                    .eq(ChannelChild::getChannelId, apiVO.getChannelId())
                    .eq(ChannelChild::getSubChannelId, subChannelId)
                    .eq(ChannelChild::getSubChannelName, apiVO.getSubChannelName()));
            if (selectCount == 0) {
                throw new BusinessException("子渠道信息不合法");
            }
        } else if (StringUtils.isBlank(subChannelId) && StrUtil.isNotEmpty(subChannelName)) {
            ChannelChild channelChild = channelChildMapper.selectOne(new QueryWrapper<ChannelChild>().lambda()
                    .eq(ChannelChild::getChannelId, apiVO.getChannelId())
                    .eq(ChannelChild::getSubChannelName, subChannelName));
            if (null == channelChild) {
                subChannelId = channelBaseIdService.getNewSubChannelID(apiVO.getChannelId());
                apiVO.setSubChannelId(subChannelId);
                channelChild = new ChannelChild()
                        .setChannelId(apiVO.getChannelId())
                        .setSubChannelId(subChannelId)
                        .setSubChannelName(subChannelName);
                if (StringUtils.isNotBlank(apiVO.getDataSource())) {
                    channelChild.setDataSource(apiVO.getDataSource());
                }
                channelChildMapper.insert(channelChild);
            } else {
                apiVO.setSubChannelId(channelChild.getSubChannelId());
            }
        } else if (StringUtils.isNoneBlank(subChannelId) && StrUtil.isEmpty(subChannelName)) {
            ChannelChild channelChild = channelChildMapper.selectOne(new QueryWrapper<ChannelChild>().lambda()
                    .eq(ChannelChild::getChannelId, apiVO.getChannelId())
                    .eq(ChannelChild::getSubChannelId, subChannelId));
            if (null != channelChild) {
                apiVO.setSubChannelName(channelChild.getSubChannelName());
            } else {
                throw new BusinessException("子渠道信息不合法");
            }
        } else {
            throw new BusinessException("子渠道信息不合法");
        }
        return null;
    }

    /**
     * PID处理
     *
     * @param apiVO
     * @return
     */
    private void pidHandler(ApiVO apiVO) {
        try {
            String pidAlias = apiVO.getPidAlias();
            Integer pidNum = apiVO.getPidNum();
            List<String> pidList = channelBaseIdService.getNewPIDs(pidNum);
            if (CollectionUtil.isEmpty(pidList)) {
                throw new BusinessException("PID获取失败");
            }
            apiVO.setPidList(pidList);

            //默认值
            if ("".equals(apiVO.getApplicationId())) {
                apiVO.setApplicationId(null);
            }
            if (ObjectUtil.isNotEmpty(apiVO.getPpId()) && 0l == apiVO.getPpId()) {
                apiVO.setPpId(null);
            }

            ChannelPromotion channelPromotion = new ChannelPromotion()
                    .setPidAlias(pidAlias)
                    .setCcid(apiVO.getCcid())
                    .setUserid(apiVO.getUserid())
                    .setUsername(apiVO.getUsername())
                    .setProductCode(apiVO.getProductCode())
                    .setProductId(ObjectUtil.isNotEmpty(apiVO.getProductId()) ? String.valueOf(apiVO.getProductId()) : null)
                    .setSubChannelId(apiVO.getSubChannelId())
                    .setSubChannelName(apiVO.getSubChannelName())
                    .setMediumId(apiVO.getMediumIds())
                    .setApplicationId(apiVO.getApplicationId())
                    .setExtra(apiVO.getExtra())
                    .setPpId(apiVO.getPpId())
                    .setCheckStartDate(new Date())
                    .setCheckEndDate(DateUtils.parseDate("2099-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss"));
            if (StringUtils.isNotBlank(apiVO.getDataSource())) {
                channelPromotion.setDataSource(apiVO.getDataSource());
            }
            List<ChannelPromotion> cps = new ArrayList<>(pidNum);
            for (int i = 0; i < pidNum; i++) {
                ChannelPromotion cp = DozerUtil.toBean(channelPromotion, ChannelPromotion.class);
                String pidName = i == 0 ? pidAlias : pidAlias.concat("_").concat(String.valueOf(i));
                cp.setPidAlias(pidName);
                cp.setPid(pidList.get(i));
                cps.add(cp);
            }
            channelPromotionService.saveBatch(cps);

            // 推送YouTop + 存宽表
            List<PostPidParam> postPidParam = new ArrayList<PostPidParam>();
            if (cps != null && cps.size() > 0) {
                cps = cps.stream().filter(i -> StringUtils.isNotBlank(i.getProductId())).collect(Collectors.toList());
                if (cps.size() > 0) {
                    postPidParam = DozerUtil.toBeanList(cps, PostPidParam.class);
                }
            }
            channelPromotionAllService.addBatchThread(cps, youtopApiHost, postPidParam);
        } catch (ParseException e) {
            log.info(e.getMessage(), e);
        }
    }

    private ChannelCooperation ccidHandler(ApiVO apiVO) {
        ChannelCooperation channelCooperation = new ChannelCooperation()
                .setChannelId(apiVO.getChannelId())
                .setChannelName(apiVO.getChannelName())
                .setAgentId(apiVO.getCompanyId())
                .setAgentName(apiVO.getCompanyName())
                .setDepartmentCode(apiVO.getDepartmentCode())
                .setDepartmentName(apiVO.getDepartmentName())
                .setChargeRule(apiVO.getChargeRule())
                .setChannelRate(apiVO.getChannelRate())
                .setChannelShare(apiVO.getChannelShare())
                .setChannelShareStep(apiVO.getChannelShareStep())
                .setBusinessDictId(apiVO.getBusinessDictId())
                .setFirstLevelBusiness(apiVO.getFirstLevelBusiness())
                .setSecondLevelBusiness(apiVO.getSecondLevelBusiness())
                .setThirdLevelBusiness(apiVO.getThirdLevelBusiness())
                .setUserid(apiVO.getUserid());
        channelCooperation.setUsername(apiVO.getUsername());
        ChannelCooperation channelCooperationExists = exists(channelCooperation, channelCooperation.getChargeRule());
        if (null != channelCooperationExists) {
            log.info("ccid已存在,直接使用已存在ccid");
            apiVO.setCcid(channelCooperationExists.getCcid());
            return channelCooperationExists;
        } else {
            log.info("ccid不存在,创建ccid");
            String chargeRule = getChargeRule(channelCooperation.getChargeRule());
            String ccid = channelBaseIdService.getNewCCID(apiVO.getChannelId(), chargeRule);
            //StrUtil.format("CCID{}{}{}", channelCooperation.getChannelId() % 100000, chargeRule.toUpperCase(), RandomUtil.randomString(3));
            apiVO.setCcid(ccid);
            channelCooperation.setCcid(ccid);
            if (StringUtils.isNotBlank(apiVO.getDataSource())) {
                channelCooperation.setDataSource(apiVO.getDataSource());
            }
            channelCooperationMapper.insert(channelCooperation);
            return channelCooperation;
        }
    }

    /**
     * 处理公司信息
     *
     * @param apiVO
     */
    private void companyHandler(ApiVO apiVO) {

        String companyName = apiVO.getCompanyName();
        List<Cooperation> cooperationList = cooperationMapper.selectList(new QueryWrapper<Cooperation>().select("id", "company_name").lambda()
                .eq(Cooperation::getCompanyName, companyName).orderByDesc(Cooperation::getCooperationType));
        if (CollectionUtil.isEmpty(cooperationList)) {
            throw new BusinessException("公司名称:" + companyName + ", 找不到公司信息");
        }
        apiVO.setCompanyId(cooperationList.get(0).getId());
    }

    /**
     * 处理渠道信息
     *
     * @param apiVO
     * @return
     */
    private Channel channelHandler(ApiVO apiVO) {

        String channelName = apiVO.getChannelName();
        Channel channel = channelMapper.selectOne(new QueryWrapper<Channel>().lambda()
                .eq(Channel::getChannelName, channelName)
                .eq(Channel::getCompanyId, apiVO.getCompanyId())
        );
        if (null == channel) {
            long channelId = channelBaseIdService.getNewChannelID();
            apiVO.setChannelId(channelId);
            channel = new Channel()
                    .setChannelId(channelId)
                    .setChannelName(channelName)
                    .setCompanyId(apiVO.getCompanyId())
                    .setCompanyName(apiVO.getCompanyName())
                    .setDepartmentCode(apiVO.getDepartmentCode())
                    .setDepartmentName(apiVO.getDepartmentName())
                    .setSettlementType(StringUtils.isNotBlank(apiVO.getSettlementType()) ? apiVO.getSettlementType() : "2")
                    .setSecretType(apiVO.getSecretType())
                    .setUserid(apiVO.getUserid());
            channel.setUsername(apiVO.getUsername());
            if (StringUtils.isNotBlank(apiVO.getDataSource())) {
                channel.setDataSource(apiVO.getDataSource());
            }
            channelMapper.insert(channel);
            channelBaseIdService.updateNewChannelID(channelId);
        } else {
            apiVO.setChannelId(channel.getChannelId());
        }
        return channel;
    }

    /**
     * apiVo包装类
     */
    @Data
    public class ApiVoWrap {
        private ApiVO apiVO;

        /**
         * 是否合法
         */
        public boolean ok() {
            return true;
        }
    }

    private String getChargeRule(String _chargeRule) {
        Map<String, String> map = MapUtil.<String, String>builder()
                .put("自运营CPS", "CPS").put("非A", "NOA").put("自然量", "ZRL").put("eCPM", "CPM")
                .put("历史未归属", "OTR").put("其他", "OTR").put("联运CPS", "CPS")
                .build();
        String chargeRule = _chargeRule;
        if (StrUtil.length(chargeRule) == 3 && check(chargeRule)) {
            chargeRule = _chargeRule;
        } else if (map.containsKey(chargeRule)) {
            chargeRule = map.get(chargeRule);
        } else {
            chargeRule = "OTR";
        }
        return chargeRule;
    }

    /**
     * 判断一个字符串是否为字母
     *
     * @param strData
     * @return
     */
    private boolean check(String strData) {
        char c = strData.charAt(0);
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            return true;
        } else {
            return false;
        }
    }

    private ChannelCooperation exists(ChannelCooperation channelCooperation, String chargeRule) {
        LambdaQueryWrapper<ChannelCooperation> lambda = new QueryWrapper<ChannelCooperation>().lambda();
        lambda.eq(ChannelCooperation::getDepartmentCode, channelCooperation.getDepartmentCode())
                .eq(ChannelCooperation::getAgentId, channelCooperation.getAgentId())
                .eq(ChannelCooperation::getBusinessDictId, channelCooperation.getBusinessDictId())
                .eq(ChannelCooperation::getFirstLevelBusiness, channelCooperation.getFirstLevelBusiness())
                .eq(ChannelCooperation::getSecondLevelBusiness, channelCooperation.getSecondLevelBusiness())
                .eq(ChannelCooperation::getThirdLevelBusiness, channelCooperation.getThirdLevelBusiness())
                .eq(ChannelCooperation::getChargeRule, chargeRule)
                .eq(ChannelCooperation::getChannelName, channelCooperation.getChannelName());
        if (null != channelCooperation.getChannelRate()) {
            lambda.eq(ChannelCooperation::getChannelRate, channelCooperation.getChannelRate());
        } else {
            lambda.isNull(ChannelCooperation::getChannelRate);
        }
        if (null != channelCooperation.getChannelShare()) {
            lambda.eq(ChannelCooperation::getChannelShare, channelCooperation.getChannelShare());
        } else {
            lambda.isNull(ChannelCooperation::getChannelShare);
        }
        if (StrUtil.isNotEmpty(channelCooperation.getChannelShareStep())) {
            lambda.eq(ChannelCooperation::getChannelShareStep, channelCooperation.getChannelShareStep());
        } else {
            lambda.isNull(ChannelCooperation::getChannelShareStep);
        }
        if (null != channelCooperation.getPrice()) {
            lambda.eq(ChannelCooperation::getPrice, channelCooperation.getPrice());
        } else {
            lambda.isNull(ChannelCooperation::getPrice);
        }
        return channelCooperationMapper.selectOne(lambda);
    }
}
