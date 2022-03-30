package com.stnts.bi.datamanagement.module.exportdata.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.business.mapper.BusinessDictMapper;
import com.stnts.bi.datamanagement.module.channel.entity.*;
import com.stnts.bi.datamanagement.module.channel.mapper.*;
import com.stnts.bi.datamanagement.module.channel.param.PostPidParam;
import com.stnts.bi.datamanagement.module.channel.service.*;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.mapper.CooperationMapper;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiService;
import com.stnts.bi.datamanagement.module.cooperation.vo.UserVO;
import com.stnts.bi.datamanagement.module.exportdata.dataenum.*;
import com.stnts.bi.datamanagement.module.exportdata.param.ExportDataParam;
import com.stnts.bi.datamanagement.module.exportdata.service.ExportDataService;
import com.stnts.bi.datamanagement.module.feign.SysClient;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.datamanagement.util.JacksonUtil;
import com.stnts.bi.datamanagement.util.SignUtil;
import com.stnts.bi.entity.sys.DepartmentEntity;
import com.stnts.bi.entity.sys.UserEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExportDataServiceImpl implements ExportDataService {
    private static final Logger logger = LoggerFactory.getLogger(ExportDataServiceImpl.class);

    @Value("${data-management.setting.youtop-api-host}")
    private String youtopApiHost;

    @Autowired
    private BusinessDictMapper businessDictMapper;
    @Autowired
    private ChannelProductMapper channelProductMapper;
    @Autowired
    private ChannelMediumMapper channelMediumMapper;
    @Autowired
    private CooperationMapper cooperationMapper;
    @Autowired
    private CooperationBiService cooperationBiService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ChannelBaseIdService channelBaseIdService;
    @Autowired
    private ChannelBaseIdMapper channelBaseIdMapper;
    @Autowired
    private ChannelChildService channelChildService;
    @Autowired
    private ChannelPromotionPositionService channelPromotionPositionService;
    @Autowired
    private ChannelPromotionPositionMapper channelPromotionPositionMapper;
    @Autowired
    private ChannelCooperationService channelCooperationService;
    @Autowired
    private ChannelPromotionService channelPromotionService;
    @Autowired
    private ChannelMapper channelMapper;
    @Autowired
    private SysClient sysClient;
    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;

    private EnvironmentProperties environmentProperties;
    private SignUtil signUtil;

    public ExportDataServiceImpl(EnvironmentProperties environmentProperties, SignUtil signUtil) {
        this.environmentProperties = environmentProperties;
        this.signUtil = signUtil;
    }

    public void debugha(List<ExportDataParam> exportDataParamList) {
        List<ChannelCooperation> channelCooperationList = getCCIDByDB(exportDataParamList);
        Map<String, ChannelCooperation> channelCooperationMap = channelCooperationList.stream().collect(Collectors.toMap(o ->
                o.getDepartmentName() + o.getChannelName() + o.getFirstLevelBusiness() + o.getSecondLevelBusiness() + o.getThirdLevelBusiness()
                        + o.getChargeRule() + "_"
                        + (o.getChannelShareType() == null ? "" : o.getChannelShareType()) + "_"
                        + (o.getChannelShare() == null ? "" : o.getChannelShare()) + "_"
                        + (o.getChannelShareStep() == null ? "" : o.getChannelShareStep()) + "_"
                        + (o.getPrice() == null ? "" : o.getPrice()) + "_"
                        + (o.getChannelRate() == null ? "" : o.getChannelRate()), s -> s));
        for (ExportDataParam exportDataParam : exportDataParamList) {
            //4. CCID
            ChannelCooperation channelCooperation = new ChannelCooperation();
            String ccidKey = exportDataParam.getPromoteDepartmentName() + exportDataParam.getChannelName() + exportDataParam.getFirstLevelBusiness() + exportDataParam.getSecondLevelBusiness()
                    + exportDataParam.getThirdLevelBusiness()
                    + exportDataParam.getChargeRule() + "_"
                    + (exportDataParam.getChannelShareType() == null ? "" : exportDataParam.getChannelShareType()) + "_"
                    + (exportDataParam.getChannelShare() == null ? "" : exportDataParam.getChannelShare()) + "_"
                    + (exportDataParam.getChannelShareStep() == null ? "" : exportDataParam.getChannelShareStep()) + "_"
                    + (exportDataParam.getPrice() == null ? "" : exportDataParam.getPrice()) + "_"
                    + (exportDataParam.getChannelRate() == null ? "" : exportDataParam.getChannelRate());
            if (channelCooperationMap.containsKey(ccidKey)) {
                channelCooperation = channelCooperationMap.get(ccidKey);
            } else {
                channelCooperationMap.put(ccidKey, channelCooperation);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addBatch(List<ExportDataParam> exportDataParamList, UserEntity user, HttpServletRequest request) throws Exception {
        //仅仅DEBUG验证时，使用
        //debugha(exportDataParamList);

        //验证
        List<BusinessDict> businessDictList = validationBusinessDict(exportDataParamList);
        List<Map<String, Object>> departmentList = validationDepartment(exportDataParamList, request);
        List<Cooperation> cooperationList = validationCooperation(exportDataParamList);
        List<ChannelApplication> channelApplicationList = validationProductAndApplication(exportDataParamList);
        List<ChannelMedium> channelMediumList = validationChannelMedium(exportDataParamList);
        List<Channel> dmChannelList = validationChannel(exportDataParamList);
        Map<String, ChannelCooperation> ccidSettlementMap = validationCCIDSettlement(exportDataParamList);
        validationOther(exportDataParamList);

        //数据
        List<Channel> channelList = getChannelByDB(exportDataParamList);
        List<ChannelChild> channelChildList = getChannelChildByDB(exportDataParamList);
        List<ChannelPromotionPosition> channelPromotionPositionList = getPPidByDB(exportDataParamList);
        List<ChannelCooperation> channelCooperationList = getCCIDByDB(exportDataParamList);

        //验证返回Map
        Map<String, Map<String, Object>> departmentMap = departmentList.stream().collect(Collectors.toMap(i -> String.valueOf(i.get("name")), s -> s));
        Map<String, Cooperation> cooperationMap = cooperationList.stream().collect(Collectors.toMap(s -> String.valueOf(s.getId()), s -> s));
        Map<String, ChannelApplication> channelApplicationMap = channelApplicationList.stream().collect(Collectors.toMap(i -> i.getProductCodeParam() + "_" + (i.getApplicationName() == null ? "" : i.getApplicationName()), s -> s));
        Map<String, ChannelMedium> channelMediumMap = channelMediumList.stream().collect(Collectors.toMap(i -> i.getDepartmentName() + "_" + i.getName(), s -> s));
        //数据返回Map
        Map<String, Channel> channelMap = channelList.stream().collect(Collectors.toMap(i -> i.getDepartmentName() + "_" + i.getChannelName(), s -> s));
        Map<String, Channel> channelSecretMap = channelList.stream().collect(Collectors.toMap(i -> i.getSecretType() + "_" + i.getChannelName(), s -> s));
        Map<String, ChannelChild> channelChildMap = channelChildList.stream().collect(Collectors.toMap(i -> i.getChannelName() + "_" + i.getSubChannelName(), s -> s));
        Map<String, ChannelPromotionPosition> channelPromotionPositionMap = channelPromotionPositionList.stream().collect(Collectors.toMap(i ->
                i.getChannelName() + "_" + (String.valueOf(PPTypeEnum.CHANNELPP.getKey()).equals(String.valueOf(i.getPpFlag())) ? "" : i.getSubChannelName()) + i.getPpName() + "_" + (i.getPpFlag()), s -> s));
        Map<String, ChannelCooperation> channelCooperationMap = channelCooperationList.stream().collect(Collectors.toMap(o -> o.getDepartmentName() + o.getChannelName() + o.getFirstLevelBusiness() + o.getSecondLevelBusiness() + o.getThirdLevelBusiness() + o.getBusinessDictId() + o.getChargeRule() + "_" + (o.getChannelShareType() == null ? "" : o.getChannelShareType()) + "_" + (o.getChannelShare() == null ? "" : o.getChannelShare()) + "_" + (o.getChannelShareStep() == null ? "" : o.getChannelShareStep()) + "_" + (o.getPrice() == null ? "" : o.getPrice()) + "_" + (o.getChannelRate() == null ? "" : o.getChannelRate()), s -> s));

        // 友拓产品ID
        List<String> productCodeList = exportDataParamList.stream().map(ExportDataParam::getProductCode).collect(Collectors.toList());
        Map<String, String> productCodeToIdMap = channelProductMapper.selectList(new QueryWrapper<ChannelProduct>().lambda().in(ChannelProduct::getProductCode, productCodeList)).stream().collect(Collectors.toMap(ChannelProduct::getProductCode, s -> s.getProductId()));

        //获取用户
        List<UserVO> userVOList = getUser();
        Map<String, UserVO> userMap = userVOList.stream().collect(Collectors.toMap(i -> i.getCardNumber(), s -> s));

        //渠道ID【已用过的、最新的渠道ID】
        Long channelId = channelBaseIdService.getNewChannelID();
        //PID别名【已用过的、最新的别名ID】
        Map<String, Integer> pidAliasMap = new HashMap<String, Integer>();
        List<Channel> newChannelList = new ArrayList<Channel>();
        List<ChannelChild> newChannelChildList = new ArrayList<ChannelChild>();
        //List<ChannelPromotionPosition> newChannelPromotionPositionList = new ArrayList<ChannelPromotionPosition>();
        List<ChannelCooperation> newChannelCooperationList = new ArrayList<ChannelCooperation>();
        List<ChannelPromotion> newChannelPromotionList = new ArrayList<ChannelPromotion>();

        //新增
        for (ExportDataParam exportDataParam : exportDataParamList) {
            String companyId = exportDataParam.getCompanyId();
            Cooperation cooperation = cooperationMap.get(companyId);

            //1. 渠道
            Channel channel = new Channel();
            String channelKey = exportDataParam.getChannelDepartmentName() + "_" + exportDataParam.getChannelName();
            String channelSecretKey = ("共享".equals(exportDataParam.getSecretType()) ? "1" : "2") + "_" + exportDataParam.getChannelName();
            if (channelMap.containsKey(channelKey)) {
                channel = channelMap.get(channelKey);
            } else if (channelSecretMap.containsKey(channelSecretKey)) {
                channel = channelSecretMap.get(channelSecretKey);
                //同步到部门名称+渠道名称中
                channelMap.put(channel.getDepartmentName() + "_" + channel.getChannelName(), channel);
            } else {
                channelId++;

                channel.setCompanyId(cooperation.getId());
                channel.setCompanyName(cooperation.getCompanyName());
                channel.setChannelName(exportDataParam.getChannelName());
                Map<String, Object> channelDepartment = departmentMap.get(exportDataParam.getChannelDepartmentName());
                channel.setDepartmentCode(String.valueOf(channelDepartment.get("code")));
                channel.setDepartmentName(String.valueOf(channelDepartment.get("name")));

                ChannelTypeEnum channelTypeEnum = ChannelTypeEnum.getByValue(exportDataParam.getChannelType());
                channel.setChannelType(ObjectUtils.isNotEmpty(channelTypeEnum) ? String.valueOf(channelTypeEnum.getKey()) : null);

                SecretTypeEnum secretTypeEnum = SecretTypeEnum.getByValue(exportDataParam.getSecretType());
                channel.setSecretType(ObjectUtils.isNotEmpty(secretTypeEnum) ? secretTypeEnum.getKey() : null);

                SettlementTypeEnum settlementTypeEnum = SettlementTypeEnum.getByValue(exportDataParam.getSettlementType());
                channel.setSettlementType(ObjectUtils.isNotEmpty(settlementTypeEnum) ? String.valueOf(settlementTypeEnum.getKey()) : null);

                channel.setChannelId(channelId); //渠道ID
                if (StringUtils.isNotBlank(exportDataParam.getProductUsername())) {
                    UserVO userVO = userMap.get(exportDataParam.getProductUsername());
                    if (userVO != null) {
                        channel.setUserid(Long.valueOf(userVO.getId()));
                        channel.setUsername(userVO.getCnname());
                    }
                }
                newChannelList.add(channel);

                channelMap.put(channelKey, channel);
            }

            //2. 子渠道
            ChannelChild channelChild = new ChannelChild();
            String channelChildKey = exportDataParam.getChannelName() + "_" + exportDataParam.getSubChannelName();
            if (channelChildMap.containsKey(channelChildKey)) {
                channelChild = channelChildMap.get(channelChildKey);
            } else {
                channelChild.setChannelId(channel.getChannelId());
                String subChannelId = channelBaseIdService.getNewSubChannelID(channel.getChannelId());
                channelChild.setSubChannelId(subChannelId);
                channelChild.setSubChannelName(exportDataParam.getSubChannelName());
                newChannelChildList.add(channelChild);  //加入新子渠道

                channelChildMap.put(channelChildKey, channelChild);
            }

            //3. 推广位 + 插件
            ChannelPromotionPosition channelPromotionPosition = new ChannelPromotionPosition();
            if (StringUtils.isNotBlank(exportDataParam.getPpName()) && StringUtils.isNotBlank(exportDataParam.getPpFlag())) {
                String ppKey = exportDataParam.getChannelName() + "_" + (PPTypeEnum.CHANNELPP.getValue().equals(exportDataParam.getPpFlag()) ? "" : exportDataParam.getSubChannelName()) + exportDataParam.getPpName() + "_" + (PPTypeEnum.CHANNELPP.getValue().equals(exportDataParam.getPpFlag()) ? "1" : "2");
                if (channelPromotionPositionMap.containsKey(ppKey)) {
                    channelPromotionPosition = channelPromotionPositionMap.get(ppKey);
                } else {
                    channelPromotionPosition.setPpName(exportDataParam.getPpName());
                    channelPromotionPosition.setPpStatus(1);
                    if (StringUtils.isNotBlank(exportDataParam.getPlugId()) && StringUtils.isNotBlank(exportDataParam.getPlugName())) {
                        channelPromotionPosition.setPlugId(exportDataParam.getPlugId());
                        channelPromotionPosition.setPlugName(exportDataParam.getPlugName());
                    }
                    PPTypeEnum ppTypeEnum = PPTypeEnum.getByValue(exportDataParam.getPpFlag());
                    channelPromotionPosition.setPpFlag(ppTypeEnum.getKey());
                    channelPromotionPosition.setChannelId(channel.getChannelId());
                    channelPromotionPosition.setSubChannelId(channelChild.getSubChannelId());
                    // 改成临时表
                    channelPromotionPositionMapper.insert(channelPromotionPosition);

                    channelPromotionPositionMap.put(ppKey, channelPromotionPosition);
                }
            }

            //4. CCID
            if (StringUtils.isNotBlank(exportDataParam.getChannelShareType())) {
                exportDataParam.setChannelShareType(String.valueOf(ChannelShareTypeEnum.getByValue(exportDataParam.getChannelShareType()).getKey()));
            }

            ChannelCooperation channelCooperation = new ChannelCooperation();
            String ccidKey = exportDataParam.getPromoteDepartmentName() + exportDataParam.getChannelName() + exportDataParam.getFirstLevelBusiness() + exportDataParam.getSecondLevelBusiness() +
                    exportDataParam.getThirdLevelBusiness() + exportDataParam.getBusinessDictId()
                    + exportDataParam.getChargeRule() + "_"
                    + (exportDataParam.getChannelShareType() == null ? "" : exportDataParam.getChannelShareType()) + "_"
                    + (exportDataParam.getChannelShare() == null ? "" : exportDataParam.getChannelShare()) + "_"
                    + (exportDataParam.getChannelShareStep() == null ? "" : exportDataParam.getChannelShareStep()) + "_"
                    + (exportDataParam.getPrice() == null ? "" : exportDataParam.getPrice()) + "_"
                    + (exportDataParam.getChannelRate() == null ? "" : exportDataParam.getChannelRate());
            if (channelCooperationMap.containsKey(ccidKey)) {
                channelCooperation = channelCooperationMap.get(ccidKey);
            } else {
                String chargeRule = exportDataParam.getChargeRule();
                String chargeRuleStr = getChargeRule(chargeRule);
                String ccid = channelBaseIdService.getNewCCID(channel.getChannelId(), chargeRuleStr);
                channelCooperation.setCcid(ccid);
                channelCooperation.setAgentId(cooperation.getId());
                channelCooperation.setAgentName(cooperation.getCompanyName());
                channelCooperation.setChannelId(channel.getChannelId());
                channelCooperation.setChannelName(channel.getChannelName());
                Map<String, Object> promoteDepartment = departmentMap.get(exportDataParam.getPromoteDepartmentName());
                channelCooperation.setDepartmentCode(String.valueOf(promoteDepartment.get("code")));
                channelCooperation.setDepartmentName(String.valueOf(promoteDepartment.get("name")));
                channelCooperation.setFirstLevelBusiness(exportDataParam.getFirstLevelBusiness());
                channelCooperation.setSecondLevelBusiness(exportDataParam.getSecondLevelBusiness());
                channelCooperation.setThirdLevelBusiness(exportDataParam.getThirdLevelBusiness());
                channelCooperation.setChargeRule(chargeRule);
                channelCooperation.setChannelRate(BigDecimal.valueOf(Long.valueOf(exportDataParam.getChannelRate())));
                String channelShareFlag = exportDataParam.getChannelShareFlag();
                if (StringUtils.isNotBlank(channelShareFlag)) {
                    ChannelShareFlagType channelShareFlagType = ChannelShareFlagType.valueOf(channelShareFlag);
                    if (ChannelShareFlagType.固定分成.equals(channelShareFlagType)) {
                        channelCooperation.setChannelShare(new BigDecimal(exportDataParam.getChannelShare()));
                    } else if (ChannelShareFlagType.阶梯分成.equals(channelShareFlagType)) {
                        String channelShareStep = exportDataParam.getChannelShareStep();
                        channelCooperation.setChannelShareStep(getJXStep(channelShareStep));
                    }
                    if (StringUtils.isNotBlank(exportDataParam.getChannelShareType())) {
                        channelCooperation.setChannelShareType(exportDataParam.getChannelShareType());
                    }
                }
                if (StringUtils.isNotBlank(exportDataParam.getPrice())) {
                    channelCooperation.setPrice(BigDecimal.valueOf(Float.valueOf(exportDataParam.getPrice())));
                }
                channelCooperation.setBusinessDictId(exportDataParam.getBusinessDictId());

                if (StringUtils.isNotBlank(exportDataParam.getCcidUsername())) {
                    UserVO userVO = userMap.get(exportDataParam.getPidUsername());
                    if (userVO != null) {
                        channelCooperation.setUserid(Long.valueOf(userVO.getId()));
                        channelCooperation.setUsername(userVO.getCnname());
                    }
                }
                newChannelCooperationList.add(channelCooperation); // 加入新CCID

                channelCooperationMap.put(ccidKey, channelCooperation);
            }
            // update yf by 20210717(已撤回)
            channelChild.setCcid(channelCooperation.getCcid());

            //4. PID
            ChannelPromotion channelPromotion = new ChannelPromotion();
            channelPromotion.setPid(exportDataParam.getPid());
            channelPromotion.setDataSource(exportDataParam.getDataSource());

            String pidName = "";

            Integer lastNum = 0;
            if (!pidAliasMap.containsKey(exportDataParam.getPidAlias())) {
                pidName = exportDataParam.getPidAlias();
                lastNum = channelPromotionService.getLastNum(pidName); //by yifan 20211104

                pidName = lastNum > 0 ? pidName.concat("_").concat(String.valueOf(lastNum)) : pidName;

                pidAliasMap.put(exportDataParam.getPidAlias(), lastNum);
            } else {
                lastNum = pidAliasMap.get(exportDataParam.getPidAlias());
                lastNum = lastNum + 1;
                pidName = exportDataParam.getPidAlias() + lastNum;
            }
            channelPromotion.setPidAlias(pidName);
            pidAliasMap.put(exportDataParam.getPidAlias(), lastNum);

            channelPromotion.setCcid(channelCooperation.getCcid());
            channelPromotion.setSubChannelId(channelChild.getSubChannelId());
            channelPromotion.setPpId(channelPromotionPosition.getPpId());
            channelPromotion.setProductCode(exportDataParam.getProductCode());
            if (StringUtils.isNotBlank(exportDataParam.getApplicationName())) {
                ChannelApplication channelApplication = channelApplicationMap.get(exportDataParam.getProductCode() + "_" + exportDataParam.getApplicationName());
                channelPromotion.setApplicationId(channelApplication.getId());
            }
            if (StringUtils.isNotBlank(exportDataParam.getMediumName())) {
                String mediumNameAll = exportDataParam.getMediumName();
                String[] mediumNames = mediumNameAll.split(",");
                StringBuilder sb = new StringBuilder();
                for (String mediumName : mediumNames) {
                    ChannelMedium channelMedium = channelMediumMap.get(exportDataParam.getPromoteDepartmentName() + "_" + mediumName);
                    sb.append(channelMedium.getId()).append(",");
                }

                channelPromotion.setMediumId(sb.substring(0, sb.length() - 1));
            }
            StringBuilder sb = new StringBuilder();
            if (StringUtils.isNotBlank(exportDataParam.getIsWB()) && "是".equals(exportDataParam.getIsWB())) {
                sb.append("网吧");
            }
            if (StringUtils.isNotBlank(exportDataParam.getIsYDD()) && "是".equals(exportDataParam.getIsYDD())) {
                sb.append(sb.length() > 0 ? "," : "");
                sb.append("移动端");
            }
            if (sb.length() > 0) {
                channelPromotion.setExtra(sb.toString());
            }
            if (StringUtils.isNotBlank(exportDataParam.getPidUsername())) {
                UserVO userVO = userMap.get(exportDataParam.getPidUsername());
                if (userVO != null) {
                    channelPromotion.setUserid(Long.valueOf(userVO.getId()));
                    channelPromotion.setUsername(userVO.getCnname());
                }
            }
            channelPromotion.setCheckStartDate(DateUtils.parseDate("2021-11-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
            channelPromotion.setCheckEndDate(DateUtils.parseDate("2099-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss"));

            //内结CCID
            if (StringUtils.isNotBlank(exportDataParam.getCcidSettlement())) {
                ChannelCooperation channelCooperationTmp = ccidSettlementMap.get(exportDataParam.getCcidSettlement());
                if (ObjectUtils.isNotEmpty(channelCooperationTmp)) {
                    /*channelPromotion.setChannelIdSettlement(channelCooperationTmp.getChannelId());
                    channelPromotion.setChannelNameSettlement(channelCooperationTmp.getChannelName());*/
                    channelPromotion.setCcidSettlement(exportDataParam.getCcidSettlement());
                } else {
                    throw new BusinessException("内结CCID有异常");
                }
            }

            if (productCodeToIdMap.containsKey(channelPromotion.getProductCode())) {
                channelPromotion.setProductId(productCodeToIdMap.get(channelPromotion.getProductCode()));
            }

            newChannelPromotionList.add(channelPromotion);
        }

        // 改成临时表
        //channelBaseIdService.updateNewChannelID(channelId - 1);
        channelBaseIdService.updateNewChannelID(channelId);
        channelService.saveBatch(newChannelList);
        channelChildService.saveBatch(newChannelChildList);
        channelCooperationService.saveBatch(newChannelCooperationList);
        channelPromotionService.saveBatch(newChannelPromotionList);

        // 推送YouTop + 存宽表
        List<PostPidParam> postPidParam = new ArrayList<PostPidParam>();
        if (newChannelPromotionList != null && newChannelPromotionList.size() > 0) {
            newChannelPromotionList = newChannelPromotionList.stream().filter(i -> StringUtils.isNotBlank(i.getProductId())).collect(Collectors.toList());
            if (newChannelPromotionList.size() > 0) {
                postPidParam = DozerUtil.toBeanList(newChannelPromotionList, PostPidParam.class);
            }
        }
        channelPromotionAllService.addBatchThread(newChannelPromotionList, youtopApiHost, postPidParam);
    }

    private List<Channel> validationChannel(List<ExportDataParam> exportDataParamList) {
        if (CollectionUtil.isNotEmpty(exportDataParamList)) {
            // excel中存在同一渠道（渠道名称+渠道部门CODE+保密类型），但其它信息不一致情况
            List<ExportDataParam> channelByExcel = exportDataParamList.stream().collect(
                    Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(
                            o -> o.getChannelName() + "_" + o.getChannelDepartmentCode() + "_" + o.getCompanyId()))), ArrayList::new));
            List<ExportDataParam> one2 = exportDataParamList.stream().collect(
                    Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(
                            o -> o.getCompanyId() + "_" + o.getChannelName() + "_" + o.getChannelDepartmentCode() + "_" + o.getChannelType() + "_" + o.getSecretType() + "_" + o.getSettlementType()))), ArrayList::new));
            if (channelByExcel.size() != one2.size()) {
                throw new BusinessException("excel中存在同一渠道（渠道名称+渠道部门CODE+保密类型），但其它信息不一致情况，请检查");
            }

            //数据库中已有渠道（不用新增）
            List<Channel> channelByDB = channelMapper.selectListByExcel(exportDataParamList);
            if (CollectionUtil.isNotEmpty(channelByDB)) {
                List<String> error = new ArrayList<String>();
                for (Channel channel : channelByDB) {
                    String key = channel.getChannelName() + "_" + channel.getDepartmentCode() + "_" + channel.getCompanyId();
                    for (ExportDataParam exportDataParam : exportDataParamList) {
                        String key2 = exportDataParam.getChannelName() + "_" + exportDataParam.getChannelDepartmentCode() + "_" + exportDataParam.getCompanyId();
                        if (key.equals(key2)) {
                            //已在数据库中存在
                            if (!String.valueOf(channel.getCompanyId()).equals(exportDataParam.getCompanyId())
                                    || !channel.getChannelType().equals(exportDataParam.getChannelType().equals("自营") ? "1" : "2")
                                    || !String.valueOf(channel.getSecretType()).equals(exportDataParam.getSecretType().equals("共享") ? "1" : "2")
                                    || !channel.getSettlementType().equals(exportDataParam.getSettlementType().equals("是") ? "1" : "2")
                                    || !String.valueOf(channel.getCompanyId()).equals(exportDataParam.getCompanyId())
                            ) {
                                error.add(key);
                            }
                        }
                    }
                }
                if (CollectionUtil.isNotEmpty(error)) {
                    throw new BusinessException("渠道名和部门 已存在，但渠道其他信息对不上：" + error);
                }
            }

            return channelByDB;
        }
        return null;
    }

    /**
     * 验证内结CCID
     *
     * @param exportDataParamList
     * @return
     */
    private Map<String, ChannelCooperation> validationCCIDSettlement(List<ExportDataParam> exportDataParamList) {
        List errorCCIDSettlement = new ArrayList();
        Map<String, ChannelCooperation> inter = new HashMap<String, ChannelCooperation>();

        List<String> productCodeList = exportDataParamList.stream().map(ExportDataParam::getProductCode).filter(i -> StringUtils.isNotBlank(i)).distinct().collect(Collectors.toList());
        List<String> ccidSettlementList = exportDataParamList.stream().map(ExportDataParam::getCcidSettlement).filter(i -> StringUtils.isNotBlank(i)).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(ccidSettlementList)) {
            // 当前 产品对应部门下 内结渠道 对应的 CCID
            List<ChannelCooperation> channelCooperationAllList = channelService.getSettleCCIDByProd(productCodeList);
            Map<String, List<ChannelCooperation>> channelCooperationProductMap = channelCooperationAllList.stream().collect(Collectors.groupingBy(ChannelCooperation::getProductCode));

            for (ExportDataParam exportDataParam : exportDataParamList) {
                String ccidSettlement = exportDataParam.getCcidSettlement();
                String productCode = exportDataParam.getProductCode();
                if (StringUtils.isNotBlank(ccidSettlement)) {
                    if (channelCooperationProductMap.containsKey(productCode)) {
                        List<ChannelCooperation> channelCooperationList = channelCooperationProductMap.get(productCode);
                        Map<String, ChannelCooperation> channelCooperationMap = channelCooperationList.stream().collect(Collectors.toMap(i -> i.getCcid(), s -> s));
                        if (channelCooperationMap.containsKey(ccidSettlement)) {
                            inter.put(ccidSettlement, channelCooperationMap.get(ccidSettlement));
                        } else {
                            errorCCIDSettlement.add(ccidSettlement);
                        }
                    } else {
                        errorCCIDSettlement.add(ccidSettlement);
                    }
                }
            }
        }

        if (CollectionUtil.isNotEmpty(errorCCIDSettlement)) {
            throw new BusinessException("内结CCID不正确，应为产品对应部门下的内结渠道下的CCID：" + errorCCIDSettlement);
        }

        return inter;
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

    @Override
    public List<UserVO> getUser() {
        Long timestamp = System.currentTimeMillis();
        TreeMap<String, Object> params = MapUtil.newTreeMap(Comparator.comparing(String::toString));
        params.put("appId", environmentProperties.getUamsAppId());
        params.put("timestamp", timestamp);
        String sign = signUtil.getSign(params, environmentProperties.getUamsSecret());
        params.put("format", "json");
        params.put("sign", sign);
        String getResult = HttpUtil.get(StrUtil.format("http://{}/ump/getuserdept", environmentProperties.getUamsAddress()), params);
        Map map = JacksonUtil.fromJSON(getResult, Map.class);
        List users = ((Map<String, List>) map.get("info")).get("datas");
        List<UserVO> userVOList = DozerUtil.toBeanList(users, UserVO.class);
        List<UserVO> result = userVOList.stream()
                .filter(v -> v.getStatus() == 1)
                .peek(v -> v.setCnname(StrUtil.format("{}({})", v.getCnname(), v.getCardNumber())))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * 解析阶梯段
     *
     * @param channelShareStep
     * @return
     */
    public String getJXStep(String channelShareStep) {
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
    }

    /**
     * 获取数据库已有渠道【渠道名称】
     *
     * @param exportDataParamList
     * @return
     */
    public List<Channel> getChannelByDB(List<ExportDataParam> exportDataParamList) {
        List<ExportDataParam> channelByExcel = exportDataParamList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(
                        o -> o.getChannelName() + "_" + o.getChannelDepartmentName() + "_" + o.getSecretType()))), ArrayList::new));
        List<Channel> channelByDB = new ArrayList<Channel>();
        if (CollectionUtil.isNotEmpty(channelByExcel)) {
            channelByDB = channelMapper.selectListByExcel(channelByExcel);
        }

        return channelByDB;
    }

    /**
     * 获取数据库已有子渠道【渠道名称+子渠道名称】
     *
     * @param exportDataParamList
     * @return
     */
    public List<ChannelChild> getChannelChildByDB(List<ExportDataParam> exportDataParamList) {
        List<ExportDataParam> channelChildByExcel = exportDataParamList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(
                        o -> o.getChannelName() + "_" + o.getSubChannelName()))), ArrayList::new));
        List<ChannelChild> channelChildByDB = new ArrayList<ChannelChild>();
        if (CollectionUtil.isNotEmpty(channelChildByExcel)) {
            channelChildByDB = channelChildService.selectListByExcel(channelChildByExcel);
        }

        return channelChildByDB;
    }

    /**
     * 获取数据库已有推广位 推广位 + 插件【渠道名称+子渠道名称+推广位名称+推广位类型】
     *
     * @param exportDataParamList
     * @return
     */
    public List<ChannelPromotionPosition> getPPidByDB(List<ExportDataParam> exportDataParamList) {
        List<ExportDataParam> channelChildByExcel = exportDataParamList.stream().filter(o -> StringUtils.isNotBlank(o.getPpName()) || StringUtils.isNotBlank(o.getPpFlag())).collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(
                        o -> o.getChannelName() + "_" + o.getSubChannelName() + "_" + o.getPpName() + "_" + o.getPpFlag()))), ArrayList::new));
        List<ChannelPromotionPosition> channelChildByDB = new ArrayList<ChannelPromotionPosition>();
        if (CollectionUtil.isNotEmpty(channelChildByExcel)) {
            channelChildByDB = channelPromotionPositionMapper.selectListByExcel(channelChildByExcel);
        }

        return channelChildByDB;
    }

    /**
     * 获取数据库已有CCID【部门名称+渠道名称+计费方式+结算指标+分成比例+阶梯段+单价+渠道费率】
     *
     * @param exportDataParamList
     * @return
     */
    public List<ChannelCooperation> getCCIDByDB(List<ExportDataParam> exportDataParamList) {
        List<ExportDataParam> exportDataParamListTmp = DozerUtil.toBeanList(exportDataParamList, ExportDataParam.class);

        List<ExportDataParam> channelCooperationByExcel = exportDataParamListTmp.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(
                        o -> o.getPromoteDepartmentName() + o.getChannelName() + o.getFirstLevelBusiness() + o.getSecondLevelBusiness() + o.getThirdLevelBusiness() + o.getBusinessDictId()
                                + o.getChargeRule()
                                + (o.getChannelShareType() == null ? "" : o.getChannelShareType())
                                + (o.getChannelShare() == null ? "" : o.getChannelShare())
                                + (o.getChannelShareStep() == null ? "" : o.getChannelShareStep())
                                + (o.getPrice() == null ? "" : o.getPrice())
                                + (o.getChannelRate() == null ? "" : o.getChannelRate())
                ))), ArrayList::new));
        for (ExportDataParam exportDataParam : channelCooperationByExcel) {
            exportDataParam.setChannelShareStep(StringUtils.isNotBlank(exportDataParam.getChannelShareStep()) ? exportDataParam.getChannelShareStep() : null);
            // 将汉字转数字
            exportDataParam.setChannelShareType(StringUtils.isNotBlank(exportDataParam.getChannelShareType()) ? String.valueOf(ChannelShareTypeEnum.getByValue(exportDataParam.getChannelShareType()).getKey()) : null);
        }

        List<ChannelCooperation> channelChildByDB = new ArrayList<ChannelCooperation>();
        if (CollectionUtil.isNotEmpty(channelCooperationByExcel)) {
            channelChildByDB = channelCooperationService.selectListByExcel(channelCooperationByExcel);

            for (ChannelCooperation channelCooperation : channelChildByDB) {
                if (channelCooperation.getChannelRate() != null) {
                    channelCooperation.setChannelRate(new BigDecimal(channelCooperation.getChannelRate().stripTrailingZeros().toPlainString()));
                }
                if (channelCooperation.getChannelShare() != null) {
                    channelCooperation.setChannelShare(new BigDecimal(channelCooperation.getChannelShare().stripTrailingZeros().toPlainString()));
                }
                if (channelCooperation.getPrice() != null) {
                    channelCooperation.setPrice(new BigDecimal(channelCooperation.getPrice().stripTrailingZeros().toPlainString()));
                }
            }
        }

        return channelChildByDB;
    }

    /**
     * 业务分类【推广部门+年份+分类1/2/3级】
     *
     * @param exportDataParamList
     * @return
     */
    public List<BusinessDict> validationBusinessDict(List<ExportDataParam> exportDataParamList) {
        List<ExportDataParam> businessDictByExcel = exportDataParamList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(
                        o -> o.getPromoteDepartmentName() + "_" + o.getFirstLevelBusiness() + "_" + o.getSecondLevelBusiness() + "_" + o.getThirdLevelBusiness()))), ArrayList::new));
        List<BusinessDict> businessDictByDB = businessDictMapper.selectListByDict(businessDictByExcel);
        if (businessDictByDB.size() < businessDictByExcel.size()) {
            logger.info("================>业务分类:" + businessDictByDB.size() + ":" + businessDictByExcel.size());

            List<String> businessDictParamByExcel = businessDictByExcel.stream().map(o -> o.getPromoteDepartmentName() + "_" + o.getFirstLevelBusiness() + "_" + o.getSecondLevelBusiness() + "_" + o.getThirdLevelBusiness()).collect(Collectors.toList());
            List<String> businessDictParamByDB = businessDictByDB.stream().map(o -> o.getRootLevel() + "_" + o.getFirstLevel() + "_" + o.getSecondLevel() + "_" + o.getThirdLevel()).collect(Collectors.toList());
            businessDictParamByExcel.removeAll(businessDictParamByDB);
            throw new BusinessException("业务分类不存在" + JSON.toJSONString(businessDictParamByExcel));
        } else {
            Map<String, BusinessDict> businessDictMap = businessDictByDB.stream().collect(Collectors.toMap(i -> i.getRootLevel() + "_" + i.getFirstLevel() + "_" + i.getSecondLevel() + "_" + i.getThirdLevel(), s -> s));
            for (ExportDataParam exportDataParam : exportDataParamList) {
                String key = exportDataParam.getPromoteDepartmentName() + "_" + exportDataParam.getFirstLevelBusiness() + "_" + exportDataParam.getSecondLevelBusiness() + "_" + exportDataParam.getThirdLevelBusiness();

                if (businessDictMap.containsKey(key)) {
                    BusinessDict businessDict = businessDictMap.get(key);
                    exportDataParam.setBusinessDictId(businessDict.getId());
                }
            }
        }

        return businessDictByDB;
    }

    /**
     * 归属及推广部门【部门名称】
     *
     * @param exportDataParamList
     * @return
     */
    public List<Map<String, Object>> validationDepartment(List<ExportDataParam> exportDataParamList, HttpServletRequest request) {
        //权限验证【私有渠道 对应 部门】
        List<Map<String, Object>> dataMap = cooperationBiService.departmentList(request);

        Set<String> departmentNameByExcel = exportDataParamList.stream().filter(i -> "私有".equals(i.getSecretType())).map(ExportDataParam::getChannelDepartmentName).collect(Collectors.toSet());
        Set<String> pidDepartmentNameSYByExcel = exportDataParamList.stream().map(ExportDataParam::getPromoteDepartmentName).collect(Collectors.toSet());
        departmentNameByExcel.addAll(pidDepartmentNameSYByExcel);

        List<Map<String, Object>> departmentByDB = dataMap.stream().filter(i -> departmentNameByExcel.contains(i.get("name"))).collect(Collectors.toList());
        if (departmentByDB.size() < departmentNameByExcel.size()) {
            List<String> businessDictParamByDB = departmentByDB.stream().map(o -> String.valueOf(o.get("name"))).collect(Collectors.toList());
            departmentNameByExcel.removeAll(businessDictParamByDB);
            throw new BusinessException("部门不存在【私有渠道】" + departmentNameByExcel);
        }

        //获取【公有渠道 对应 归属部门】
        Set<String> pidDepartmentNameGXByExcel = exportDataParamList.stream().filter(i -> "共享".equals(i.getSecretType())).map(ExportDataParam::getChannelDepartmentName).collect(Collectors.toSet());
        pidDepartmentNameGXByExcel.removeAll(departmentNameByExcel);
        if (pidDepartmentNameGXByExcel != null && pidDepartmentNameGXByExcel.size() > 0) {
            logger.info("存在是公有渠道，但没有归属部门权限情况:" + pidDepartmentNameGXByExcel);

            List<Integer> orgIds = new ArrayList<Integer>();
            List<DepartmentEntity> departmentResult = sysClient.listDepartmentByOrgId(orgIds);
            logger.info("==================>获取所有部门: 有" + departmentResult + "个");
            List<DepartmentEntity> departmentEntityGXList = departmentResult.stream().filter(i -> pidDepartmentNameGXByExcel.contains(i.getName())).collect(Collectors.toList());
            for (DepartmentEntity files : departmentEntityGXList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map = JSONObject.parseObject(JSONObject.toJSONString(files), Map.class);
                departmentByDB.add(map);
            }
        }

        //处理PID推广部门CODE 及 渠道归属部门CODE
        //Map<String, String> promoteDepartmentByDBMap = dataMap.stream().filter(i -> pidDepartmentNameSYByExcel.contains(i.get("name"))).collect(Collectors.toMap(item -> String.valueOf(item.get("name")), item -> String.valueOf(item.get("code"))));
        Map<String, String> promoteDepartmentByDBMap = departmentByDB.stream().collect(Collectors.toMap(item -> String.valueOf(item.get("name")), item -> String.valueOf(item.get("code"))));
        for (ExportDataParam exportDataParam : exportDataParamList) {
            if (promoteDepartmentByDBMap.containsKey(exportDataParam.getPromoteDepartmentName())) {
                exportDataParam.setPromoteDepartmentCode(promoteDepartmentByDBMap.get(exportDataParam.getPromoteDepartmentName()));
            }
            if (promoteDepartmentByDBMap.containsKey(exportDataParam.getChannelDepartmentName())) {
                exportDataParam.setChannelDepartmentCode(promoteDepartmentByDBMap.get(exportDataParam.getChannelDepartmentName()));
            }
        }

        return departmentByDB;
    }

    /**
     * 公司【公司名称】
     *
     * @param exportDataParamList
     * @return
     */
    public List<Cooperation> validationCooperation(List<ExportDataParam> exportDataParamList) {
        List<ExportDataParam> companyIdByExcel = exportDataParamList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(
                        o -> o.getCompanyId()))), ArrayList::new));
        List<String> companyIdList = companyIdByExcel.stream().map(ExportDataParam::getCompanyId).collect(Collectors.toList());
        List<Cooperation> cooperationListByDB = cooperationMapper.selectList(new QueryWrapper<Cooperation>().lambda().in(Cooperation::getId, companyIdList));
        if (cooperationListByDB.size() < companyIdByExcel.size()) {
            List<String> companyIdParamByExcel = companyIdByExcel.stream().map(o -> o.getCompanyId()).collect(Collectors.toList());
            List<String> compnayIdByDB = cooperationListByDB.stream().map(o -> String.valueOf(o.getId())).collect(Collectors.toList());
            companyIdParamByExcel.removeAll(compnayIdByDB);
            throw new BusinessException("公司不存在" + JSON.toJSONString(companyIdParamByExcel));
        }

        return cooperationListByDB;
    }

    /**
     * 产品【推广部门 + 产品Code + 产品名称（+应用名称）】
     *
     * @param exportDataParamList
     * @return
     */
    public List<ChannelApplication> validationProductAndApplication(List<ExportDataParam> exportDataParamList) {
        List<ExportDataParam> productApplicationByExcel = exportDataParamList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(
                        o -> o.getPromoteDepartmentName() + "_" + o.getProductCode() + "_" + o.getProductName() + "_" + (StringUtils.isBlank(o.getApplicationName()) ? "" : o.getApplicationName())
                ))), ArrayList::new));
        List<ChannelApplication> productApplicationByDB = channelProductMapper.selectListByProduct(productApplicationByExcel);
        if (productApplicationByDB.size() < productApplicationByExcel.size()) {
            List<String> productApplicationParamByExcel = productApplicationByExcel.stream().map(o -> o.getPromoteDepartmentName() + "_" + o.getProductCode() + "_" + o.getProductName() + "_" + (o.getApplicationName() == null ? "" : o.getApplicationName())).collect(Collectors.toList());
            List<String> productApplicationParamByDB = productApplicationByDB.stream().map(o -> o.getDepartmentNameParam() + "_" + o.getProductCodeParam() + "_" + o.getProductNameParam() + "_" + (o.getApplicationName() == null ? "" : o.getApplicationName())).collect(Collectors.toList());
            productApplicationParamByExcel.removeAll(productApplicationParamByDB);
            throw new BusinessException("产品不存在：" + JSON.toJSONString(productApplicationParamByExcel));
        }

        return productApplicationByDB;
    }

    /**
     * 推广媒介【推广部门+媒介名称】
     *
     * @param exportDataParamList
     * @return
     */
    public List<ChannelMedium> validationChannelMedium(List<ExportDataParam> exportDataParamList) {
        /*List<ExportDataParam> mediumByExcel = exportDataParamList.stream().filter(i -> StringUtils.isNotBlank(i.getMediumName())).collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(
                        o -> o.getPromoteDepartmentName() + "_" + o.getMediumName()))), ArrayList::new));*/
        Set<ExportDataParam> mediumByExcelSet = new HashSet<ExportDataParam>();
        for (ExportDataParam exportDataParam : exportDataParamList) {
            String mediumNameAll = exportDataParam.getMediumName();
            if (StringUtils.isNotBlank(mediumNameAll)) {
                String[] mediumNames = mediumNameAll.split(",");
                for (String mediumName : mediumNames) {
                    ExportDataParam exportDataParamNew = new ExportDataParam();
                    exportDataParamNew.setMediumName(mediumName);
                    exportDataParamNew.setPromoteDepartmentName(exportDataParam.getPromoteDepartmentName());
                    mediumByExcelSet.add(exportDataParamNew);
                }
            }
        }

        List<ExportDataParam> mediumByExcel = new ArrayList<ExportDataParam>(mediumByExcelSet);
        List<ChannelMedium> channelMediumByDB = new ArrayList<ChannelMedium>();
        if (mediumByExcel.size() > 0) {
            channelMediumByDB = channelMediumMapper.selectListByMedium(mediumByExcel);
        }

        if (channelMediumByDB.size() < mediumByExcel.size()) {
            List<String> channelMediumParamByExcel = mediumByExcel.stream().map(o -> o.getPromoteDepartmentName() + "_" + o.getMediumName()).collect(Collectors.toList());
            List<String> channelMediumParamByDB = channelMediumByDB.stream().map(o -> o.getDepartmentName() + "_" + o.getName()).collect(Collectors.toList());
            channelMediumParamByExcel.removeAll(channelMediumParamByDB);
            throw new BusinessException("推广媒介不存在" + JSON.toJSONString(channelMediumParamByExcel));
        }

        return channelMediumByDB;
    }

    public void validationOther(List<ExportDataParam> exportDataParamList) {
        List<ExportDataParam> shareError = exportDataParamList.stream().filter(i ->
                //(StringUtils.isBlank(i.getChannelShareFlag()) && StringUtils.isBlank(i.getPrice())) ||
                (StringUtils.isNotBlank(i.getChannelShareFlag()) && "1".equals(ChannelShareFlagType.valueOf(i.getChannelShareFlag()).getKey()) && StringUtils.isNotBlank(i.getChannelShareStep()))
                        || (StringUtils.isNotBlank(i.getChannelShareFlag()) && "2".equals(ChannelShareFlagType.valueOf(i.getChannelShareFlag()).getKey()) && StringUtils.isNotBlank(i.getChannelShare()))
        ).collect(Collectors.toList());
        if (shareError.size() > 0) {
            throw new BusinessException("渠道分成、分成比例、阶梯段之间存在数据错误");
        }

        List<String> pidList = exportDataParamList.stream().map(ExportDataParam::getPid).distinct().collect(Collectors.toList());
        List<ChannelPromotion> channelPromotionList = channelPromotionService.list(new QueryWrapper<ChannelPromotion>().lambda().select(ChannelPromotion::getPid).in(ChannelPromotion::getPid, pidList));
        if (channelPromotionList.size() > 0) {
            List<String> pidListByDB = channelPromotionList.stream().map(ChannelPromotion::getPid).collect(Collectors.toList());
            throw new BusinessException("PID已存在" + JSON.toJSONString(pidListByDB));
        }
    }
}
