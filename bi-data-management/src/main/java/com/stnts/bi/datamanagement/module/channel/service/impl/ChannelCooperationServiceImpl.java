package com.stnts.bi.datamanagement.module.channel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.config.DataManagementConfig;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.entity.BusinessDict;
import com.stnts.bi.datamanagement.module.business.service.BusinessDictService;
import com.stnts.bi.datamanagement.module.channel.entity.*;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelClassCooperationMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelCooperationMapper;
import com.stnts.bi.datamanagement.module.channel.mapper.ChannelPromotionMapper;
import com.stnts.bi.datamanagement.module.channel.param.ChannelCooperationPageParam;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionPageParam;
import com.stnts.bi.datamanagement.module.channel.service.*;
import com.stnts.bi.datamanagement.module.channel.vo.*;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.mapper.CooperationMapper;
import com.stnts.bi.datamanagement.module.cooperation.vo.UserVO;
import com.stnts.bi.datamanagement.module.exportdata.dataenum.ChannelTypeEnum;
import com.stnts.bi.datamanagement.module.exportdata.dataenum.SecretTypeEnum;
import com.stnts.bi.datamanagement.module.exportdata.dataenum.SettlementTypeEnum;
import com.stnts.bi.datamanagement.module.exportdata.param.ExportDataParam;
import com.stnts.bi.datamanagement.module.exportdata.service.ExportDataService;
import com.stnts.bi.datamanagement.module.feign.SysClient;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.datamanagement.util.LambdaColumn;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.entity.sys.UserDmEntity;
import com.stnts.bi.entity.sys.UserEntity;
import com.stnts.bi.entity.sys.UserRoleEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 渠道合作 服务实现类
 *
 * @author 刘天元
 * @since 2021-02-03
 */
@Slf4j
@Service
public class ChannelCooperationServiceImpl extends ServiceImpl<ChannelCooperationMapper, ChannelCooperation> implements ChannelCooperationService {

    @Autowired
    private ChannelCooperationMapper channelCooperationMapper;
    @Autowired
    private ChannelChildService channelChildService;
    @Autowired
    private ChannelPromotionService channelPromotionService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ChannelBaseIdService channelBaseIdService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BusinessDictService businessDictService;
    @Autowired
    private ChannelProductService channelProductService;
    @Autowired
    private ChannelPromotionMapper channelPromotionMapper;
    @Autowired
    private SysClient sysClient;
    @Autowired
    private DataManagementConfig dataManagementConfig;
    @Autowired
    private ChannelPromotionAllService channelPromotionAllService;
    @Autowired
    private CooperationMapper cooperationMapper;
    @Autowired
    private ExportDataService exportDataService;
    @Autowired
    private ChannelClassCooperationMapper channelClassCooperationMapper;
    @Autowired
    private ChannelClassService channelClassService;
    @Autowired
    private ChannelPromotionHistoryService channelPromotionHistoryService;
    @Autowired
    private ChannelPromotionPositionService channelPromotionPositionService;
    @Autowired
    private ChannelMediumService channelMediumService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveChannelCooperation(ChannelCooperation channelCooperation, HttpServletRequest request) throws Exception {
        UserEntity user = BiSessionUtil.build(redisTemplate, request).getSessionUser();

        if (ObjectUtil.isEmpty(channelCooperation.getChannelRate())) {
            channelCooperation.setChannelRate(BigDecimal.ZERO);
        }
        channelCooperation.setAgentName(channelCooperation.getAgentName().replaceAll("（客户）|（供应商）", ""));

        BusinessDict businessDict = handlerBusinessDictId(channelCooperation.getBusinessDictId(), channelCooperation.getDepartmentCode(), channelCooperation.getFirstLevelBusiness(), channelCooperation.getSecondLevelBusiness(), channelCooperation.getThirdLevelBusiness());
        channelCooperation.setBusinessDictId(businessDict.getId());
        channelCooperation.setFirstLevelBusiness(businessDict.getFirstLevel());
        channelCooperation.setSecondLevelBusiness(businessDict.getSecondLevel());
        channelCooperation.setThirdLevelBusiness(businessDict.getThirdLevel());

        handlerChannel(channelCooperation, user);
        exists(channelCooperation, channelCooperation.getChargeRule());
        String chargeRule = getChargeRule(channelCooperation);
        String ccid = channelBaseIdService.getNewCCID(channelCooperation.getChannelId(), chargeRule);
        channelCooperation.setCcid(ccid);
        super.save(channelCooperation);

        return ccid;
    }

    /**
     * 当前部门下所有节点的渠道类型勾选
     *
     * @param departmentCode
     * @return
     */
    public List<Map<String, String>> getChannelCooperationSelect(String departmentCode) {
        List<ChannelClassCooperation> channelClassCooperationNodeList = channelClassCooperationMapper.selectList(new LambdaQueryWrapper<ChannelClassCooperation>()
                .eq(ChannelClassCooperation::getDepartmentCode, departmentCode)
        );
        // 按计费方式(CPS/CPA/CPD等)
        List<String> selectByChargeRule = channelClassCooperationNodeList.stream().filter(i -> "1".equals(i.getModeType())).map(ChannelClassCooperation::getModeId).distinct().collect(Collectors.toList());
        // 按渠道
        List<String> selectByChannel = channelClassCooperationNodeList.stream().filter(i -> "2".equals(i.getModeType())).map(ChannelClassCooperation::getModeId).distinct().collect(Collectors.toList());
        // 按CCID
        List<String> selectByCCID = channelClassCooperationNodeList.stream().filter(i -> "3".equals(i.getModeType())).map(ChannelClassCooperation::getModeId).distinct().collect(Collectors.toList());

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (String chargeRule : selectByChargeRule) {
            Map<String, String> subMap = new HashMap<String, String>();
            subMap.put("id", chargeRule);
            subMap.put("modeType", "1");
            list.add(subMap);
        }
        for (String channel : selectByChannel) {
            Map<String, String> subMap = new HashMap<String, String>();
            subMap.put("id", channel);
            subMap.put("modeType", "2");
            list.add(subMap);
        }
        for (String CCID : selectByCCID) {
            Map<String, String> subMap = new HashMap<String, String>();
            subMap.put("id", CCID);
            subMap.put("modeType", "3");
            list.add(subMap);
        }

        return list;
    }

    /**
     * 修改部门或渠道后，对PID子渠道、推广位、推广媒介进行处理
     *
     * @param channelCooperationParam
     * @param user
     * @throws Exception
     */
    private void handlerUpdateDepartmentAndChannel(ChannelCooperation channelCooperationParam, UserEntity user) throws Exception {
        ChannelCooperation channelCooperation = channelCooperationMapper.selectOne(new LambdaQueryWrapper<ChannelCooperation>().eq(ChannelCooperation::getId, channelCooperationParam.getId()));
        channelCooperationParam.setCcid(channelCooperation.getCcid());

        if (!channelCooperation.getDepartmentCode().equals(channelCooperationParam.getDepartmentCode()) || !String.valueOf(channelCooperation.getChannelId()).equals(String.valueOf(channelCooperationParam.getChannelId()))) {
            if (user.getAdmin() == 0) {
                throw new BusinessException("非超级管理员权限不允许修改CCID部门或渠道");
            }

            // 判断PID产品是否合规
            List<ChannelPromotion> channelPromotionListNo = channelPromotionMapper.checkByProDepartmentCode(channelCooperationParam);
            if (CollectionUtil.isNotEmpty(channelPromotionListNo)) {
                throw new BusinessException("请先维护关联产品对应的推广部门");
            }

            // 需考虑到渠道同时也变更了
            // 新增或者修改 对应的 子渠道+推广位+推广媒介
            // 1. 当前CCID下的PID
            List<ChannelPromotion> channelPromotionListDB = channelPromotionMapper.selectList(new LambdaQueryWrapper<ChannelPromotion>().eq(ChannelPromotion::getCcid, channelCooperation.getCcid()));
            // 2. 当前CCID下的对应子渠道 <subChannelId - 子渠道>
            List<String> subChannelIdListDB = channelPromotionListDB.stream().map(ChannelPromotion::getSubChannelId).collect(Collectors.toList());
            Map<String, ChannelChild> channelChildMapDB = new HashMap<String, ChannelChild>();
            if (CollectionUtil.isNotEmpty(subChannelIdListDB)) {
                channelChildMapDB = channelChildService.list(new LambdaQueryWrapper<ChannelChild>().in(ChannelChild::getSubChannelId, subChannelIdListDB)).stream().collect(Collectors.toMap(ChannelChild::getSubChannelId, s -> s));
            }
            // 3. 当前CCID下的对应推广位 <ppId - 推广位>
            Map<String, ChannelPromotionPosition> ppMapDB = new HashMap<String, ChannelPromotionPosition>();
            List<Long> ppIdListDB = channelPromotionListDB.stream().filter(i -> ObjectUtil.isNotEmpty(i.getPpId())).map(ChannelPromotion::getPpId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(ppIdListDB)) {
                ppMapDB = channelPromotionPositionService.list(new LambdaQueryWrapper<ChannelPromotionPosition>().in(ChannelPromotionPosition::getPpId, ppIdListDB)).stream().collect(Collectors.toMap(k -> String.valueOf(k.getPpId()), s -> s));
            }
            // 4. 当前CCID下的对应推广媒介 <mediumId - 推广位>
            Map<String, ChannelMedium> mediumListDB = new HashMap<String, ChannelMedium>();
            List<String> mediumIdListDB = channelPromotionListDB.stream().filter(i -> StringUtils.isNotBlank(i.getMediumId())).map(ChannelPromotion::getMediumId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(mediumIdListDB)) {
                mediumListDB = channelMediumService.list(new LambdaQueryWrapper<ChannelMedium>().in(ChannelMedium::getId, mediumIdListDB)).stream().collect(Collectors.toMap(k -> String.valueOf(k.getId()), s -> s));
            }

            // 5. 参数的中渠道对应的 子渠道 <subChannelName - 子渠道>
            List<ChannelChild> channelChildListParam = channelChildService.list(new LambdaQueryWrapper<ChannelChild>().eq(ChannelChild::getChannelId, channelCooperationParam.getChannelId()));
            Map<String, ChannelChild> channelChildMapParam = channelChildListParam.stream().collect(Collectors.toMap(ChannelChild::getSubChannelName, s -> s));
            // 6. 参数的中渠道对应的 推广位+子渠道推广位 <ppName - 推广位>
            List<ChannelPromotionPosition> channelPromotionPositionList = channelPromotionPositionService.list(new LambdaQueryWrapper<ChannelPromotionPosition>().eq(ChannelPromotionPosition::getChannelId, channelCooperationParam.getChannelId()));
            Map<String, ChannelPromotionPosition> ppMapParam = channelPromotionPositionList.stream().collect(Collectors.toMap(i -> i.getChannelId() + "_" + StringUtils.defaultIfBlank(i.getSubChannelId(), "") + "_" + i.getPpName() + "_" + i.getPpFlag(), s -> s));
            // 7. 参数的中CCID部门对应的 推广媒介 <mediumName - 推广媒介>
            List<ChannelMedium> channelMediumList = channelMediumService.list(new LambdaQueryWrapper<ChannelMedium>().eq(ChannelMedium::getDepartmentCode, channelCooperationParam.getDepartmentCode()));
            Map<String, ChannelMedium> mediumMapParam = channelMediumList.stream().collect(Collectors.toMap(ChannelMedium::getName, s -> s));

            for (ChannelPromotion channelPromotion : channelPromotionListDB) {
                //当前CCID下的对应子渠道、推广位、推广媒介
                ChannelChild channelChildDB = channelChildMapDB.get(channelPromotion.getSubChannelId());
                ChannelPromotionPosition channelPromotionPositionDB = ppMapDB.get(String.valueOf(channelPromotion.getPpId()));
                ChannelMedium channelMediumDB = mediumListDB.get(String.valueOf(channelPromotion.getMediumId()));

                Boolean addChannelChild = false;
                //参数的中渠道对应的子渠道名称是否存在
                if (channelChildMapParam.containsKey(channelChildDB.getSubChannelName())) {
                    channelPromotion.setSubChannelId(channelChildMapParam.get(channelChildDB.getSubChannelName()).getSubChannelId());
                } else {
                    log.info("======================>新增子渠道：" + channelChildDB.getSubChannelName());
                    ChannelChild channelChildTo = new ChannelChild();
                    channelChildTo.setChannelId(channelCooperationParam.getChannelId());
                    channelChildTo.setSubChannelName(channelChildDB.getSubChannelName());
                    channelChildTo.setDataSource("BI");
                    Map<Object, Object> addChildMap = channelChildService.saveChannelChild(channelChildTo);
                    channelPromotion.setSubChannelId(String.valueOf(addChildMap.get("subChannelId")));
                    addChannelChild = true;

                    ChannelChild channelChild = channelChildService.getOne(new LambdaQueryWrapper<ChannelChild>().eq(ChannelChild::getSubChannelId, channelPromotion.getSubChannelId()));
                    channelChildMapParam.put(channelChild.getSubChannelName(), channelChild);
                }
                //参数的中渠道对应的推广位(子渠道推广位)名称是否存在
                if (ObjectUtil.isNotEmpty(channelPromotion.getPpId())) {
                    //若参数中渠道下推广位有匹配当前PID的推广位，并且 子渠道未新增，
                    String key = channelCooperationParam.getChannelId() + "_" + (channelPromotionPositionDB.getPpFlag() == 2 ? StringUtils.defaultIfBlank(channelPromotion.getSubChannelId(), "") : "") + "_" + channelPromotionPositionDB.getPpName() + "_" + channelPromotionPositionDB.getPpFlag();
                    if (ppMapParam.containsKey(key) && ((channelPromotionPositionDB.getPpFlag() == 2 && !addChannelChild) || (channelPromotionPositionDB.getPpFlag() == 1))) {
                        channelPromotion.setPpId(ppMapParam.get(key).getPpId());
                    } else {
                        log.info("======================>新增推广位：" + channelPromotionPositionDB.getPpName());
                        ChannelPromotionPosition channelPromotionPosition = new ChannelPromotionPosition();
                        channelPromotionPosition.setPlugId(channelPromotionPositionDB.getPlugId());
                        channelPromotionPosition.setPlugName(channelPromotionPositionDB.getPlugName());
                        channelPromotionPosition.setPpName(channelPromotionPositionDB.getPpName());
                        channelPromotionPosition.setPpStatus(1);
                        channelPromotionPosition.setPpFlag(channelPromotionPositionDB.getPpFlag());
                        channelPromotionPosition.setChannelId(channelCooperationParam.getChannelId());
                        if (channelPromotionPositionDB.getPpFlag() == 2) {
                            channelPromotionPosition.setSubChannelId(channelPromotion.getSubChannelId());
                        }

                        ChannelPromotionPosition channelPromotionPositionReturn = channelPromotionPositionService.addChannelPromotionPosition(channelPromotionPosition);
                        channelPromotion.setPpId(channelPromotionPositionReturn.getPpId());

                        ppMapParam.put(channelPromotionPositionReturn.getChannelId() + "_" + StringUtils.defaultIfBlank(channelPromotionPositionReturn.getSubChannelId(), "") + "_" + channelPromotionPositionReturn.getPpName() + "_" + channelPromotionPositionReturn.getPpFlag(), channelPromotionPositionReturn);
                    }
                }
                //参数的中CCID部门对应的推广媒介名称是否存在
                if (!channelCooperation.getDepartmentCode().equals(channelCooperationParam.getDepartmentCode()) && ObjectUtil.isNotEmpty(channelPromotion.getMediumId())) {
                    if (mediumMapParam.containsKey(channelMediumDB.getName())) {
                        channelPromotion.setMediumId(String.valueOf(mediumMapParam.get(channelMediumDB.getName()).getId()));
                    } else {
                        log.info("======================>新增推广媒介：" + channelMediumDB.getName());
                        ChannelMedium channelMedium = new ChannelMedium();
                        channelMedium.setDepartmentCode(channelCooperationParam.getDepartmentCode());
                        channelMedium.setDepartmentName(channelCooperationParam.getDepartmentName());
                        channelMedium.setName(channelMediumDB.getName());
                        channelMedium.setUserid(Long.valueOf(user.getId()));
                        channelMedium.setUsername(user.getCnname());
                        ChannelMedium channelMediumReturn = channelMediumService.saveChannelMedium(channelMedium);
                        channelPromotion.setMediumId(String.valueOf(channelMediumReturn.getId()));

                        mediumMapParam.put(channelMediumReturn.getName(), channelMediumReturn);
                    }
                }
            }
            channelPromotionService.updateBatchById(channelPromotionListDB);
        }
    }

    @Override
    public BusinessDict handlerBusinessDictId(Integer businessDictId, String departmentCode, String firstLevelBusiness, String secondLevelBusiness, String thirdLevelBusiness) {
        //处理业务分类
        if (ObjectUtil.isNotEmpty(businessDictId)) {
            BusinessDict businessDict = businessDictService.getOne(new LambdaQueryWrapper<BusinessDict>()
                    .eq(BusinessDict::getId, businessDictId)
            );
            if (ObjectUtil.isNotEmpty(businessDict)) {
                if (StringUtils.isNotBlank(departmentCode) && !businessDict.getDepartmentCode().equals(departmentCode)) {
                    throw new BusinessException("业务分类与部门不匹配");
                }
                return businessDict;
            } else {
                throw new BusinessException("业务分类ID不存在");
            }
        } else {
            List<BusinessDict> businessDictList = businessDictService.list(new LambdaQueryWrapper<BusinessDict>()
                    .eq(BusinessDict::getDepartmentCode, departmentCode)
                    .le(BusinessDict::getYearStart, DateUtil.year(new Date()))
                    .ge(BusinessDict::getYearEnd, DateUtil.year(new Date()))
                    .eq(BusinessDict::getFirstLevel, firstLevelBusiness)
                    .eq(BusinessDict::getSecondLevel, secondLevelBusiness)
                    .eq(BusinessDict::getThirdLevel, thirdLevelBusiness)
                    .eq(BusinessDict::getIsValid, 1)
            );
            if (businessDictList.size() > 1) {
                throw new BusinessException("业务分类有重复");
            } else if (businessDictList.size() == 0) {
                throw new BusinessException("业务分类不存在");
            } else {
                return businessDictList.get(0);
            }
        }
    }

    @Override
    public Map<String, String> getDepartmentCodeOnly(ChannelCooperationPageParam channelCooperationPageParam) {
        Map<String, String> returnMap = new HashMap<String, String>();
        if (CollectionUtil.isEmpty(channelCooperationPageParam.getCcidList())) {
            throw new BusinessException("CCID集合不可为空");
        }
        List<ChannelCooperation> channelCooperationList = channelCooperationMapper.selectList(new LambdaQueryWrapper<ChannelCooperation>().in(ChannelCooperation::getCcid, channelCooperationPageParam.getCcidList()));
        List<String> departmentCodeList = channelCooperationList.stream().map(i -> i.getDepartmentCode()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(departmentCodeList)) {
            throw new BusinessException("CCID对应部门不存在");
        } else if (departmentCodeList.size() > 1) {
            throw new BusinessException("CCID集合必须为同一部门");
        } else if (departmentCodeList.size() == 1) {
            returnMap.put("key", channelCooperationList.get(0).getDepartmentCode());
            returnMap.put("value", channelCooperationList.get(0).getDepartmentName());
        }

        return returnMap;
    }

    @Override
    public void updateDict(BusinessDict businessDict) {
        channelCooperationMapper.updateDict(businessDict);
    }


    private void handlerChannel(ChannelCooperation channelCooperation, UserEntity user) throws Exception {
        if (ObjectUtil.isNull(channelCooperation.getChannelId())) {
            //新增渠道
            //这里两种情况，有一个是BI的，有一个是游戏运营来的
            Channel channel = new Channel().setChannelName(channelCooperation.getChannelName())
                    .setCompanyId(channelCooperation.getAgentId())
                    .setCompanyName(channelCooperation.getAgentName())
                    .setDepartmentCode(channelCooperation.getDepartmentCode())
                    .setDepartmentName(channelCooperation.getDepartmentName())
                    .setUserid(null != channelCooperation.getUserid() ? channelCooperation.getUserid() : Long.valueOf(user.getId()));
            channel.setUsername(StrUtil.isNotEmpty(channelCooperation.getUsername()) ? channelCooperation.getUsername() : user.getCnname());
            Long channelId = channelBaseIdService.getNewChannelID();
            channel.setChannelId(channelId);
            if (StringUtils.isNotBlank(channelCooperation.getDataSource())) {
                channel.setDataSource(channelCooperation.getDataSource());
            }
            channelService.saveChannel(channel);
            channelBaseIdService.updateNewChannelID(channelId);
            channelCooperation.setChannelId(channel.getChannelId());
        }
    }

    private Boolean handlerChannelGeneral(ChannelCooperation channelCooperation) throws Exception {
        Boolean isAdd = false;

        Channel channelDB = channelPromotionService.getChannelGeneral(channelCooperation.getChannelId(), channelCooperation.getChannelName(), channelCooperation.getAgentId(), channelCooperation.getDepartmentCode());

        if (ObjectUtil.isNull(channelDB)) {
            if (ObjectUtil.isEmpty(channelCooperation.getAgentId())) {
                throw new BusinessException("新增渠道：参数公司ID不可为空");
            }
            Cooperation cooperation = cooperationMapper.selectById(channelCooperation.getAgentId());
            if (ObjectUtil.isEmpty(cooperation)) {
                throw new BusinessException("新增渠道：公司不存在");
            }

            //新增渠道
            Channel channel = new Channel().setChannelName(channelCooperation.getChannelName())
                    .setCompanyId(channelCooperation.getAgentId())
                    .setCompanyName(cooperation.getCompanyName())
                    .setDepartmentCode(channelCooperation.getDepartmentCode())
                    .setDepartmentName(channelCooperation.getDepartmentName())
                    .setChannelType(StringUtils.isNotBlank(channelCooperation.getChannelType()) ? channelCooperation.getChannelType() : "2")
                    .setSecretType(ObjectUtil.isNotEmpty(channelCooperation.getSecretType()) ? channelCooperation.getSecretType() : 1)
                    .setSettlementType(ObjectUtil.isNotEmpty(channelCooperation.getSettlementType()) ? channelCooperation.getSettlementType() : "2")
                    .setUserid(channelCooperation.getUserid());
            channel.setUsername(channelCooperation.getUsername());
            if (StringUtils.isNotBlank(channelCooperation.getDataSource())) {
                channel.setDataSource(channelCooperation.getDataSource());
            }
            channelDB = channelService.saveChannel(channel);

            isAdd = true;
        }
        channelCooperation.setChannelId(channelDB.getChannelId());
        channelCooperation.setChannelName(channelDB.getChannelName());
        channelCooperation.setAgentId(channelDB.getCompanyId());
        channelCooperation.setAgentName(channelDB.getCompanyName());

        return isAdd;
    }

    @Override
    public ChannelCooperation getOneCCID(ChannelCooperation channelCooperation, String chargeRule) {
        LambdaQueryWrapper<ChannelCooperation> lambda = new QueryWrapper<ChannelCooperation>().lambda();
        lambda.eq(ChannelCooperation::getDepartmentCode, channelCooperation.getDepartmentCode())
                .eq(ChannelCooperation::getAgentId, channelCooperation.getAgentId())
                .eq(StringUtils.isNotBlank(channelCooperation.getFirstLevelBusiness()), ChannelCooperation::getFirstLevelBusiness, channelCooperation.getFirstLevelBusiness())
                .eq(StringUtils.isNotBlank(channelCooperation.getSecondLevelBusiness()), ChannelCooperation::getSecondLevelBusiness, channelCooperation.getSecondLevelBusiness())
                .eq(StringUtils.isNotBlank(channelCooperation.getThirdLevelBusiness()), ChannelCooperation::getThirdLevelBusiness, channelCooperation.getThirdLevelBusiness())
                .eq(ChannelCooperation::getChargeRule, chargeRule)
                .eq(ChannelCooperation::getChannelName, channelCooperation.getChannelName())
                .eq(ObjectUtil.isNotEmpty(channelCooperation.getBusinessDictId()), ChannelCooperation::getBusinessDictId, channelCooperation.getBusinessDictId());
        if (null != channelCooperation.getChannelRate()) {
            lambda.eq(ChannelCooperation::getChannelRate, channelCooperation.getChannelRate());
        } else {
            lambda.and(t -> t.isNull(ChannelCooperation::getChannelRate).or().eq(ChannelCooperation::getChannelRate, 0));
        }
        if (null != channelCooperation.getChannelShare()) {
            lambda.eq(ChannelCooperation::getChannelShare, channelCooperation.getChannelShare());
        } else {
            lambda.isNull(ChannelCooperation::getChannelShare);
        }
        if (StrUtil.isNotBlank(channelCooperation.getChannelShareStep())) {
            lambda.eq(ChannelCooperation::getChannelShareStep, channelCooperation.getChannelShareStep());
        } else {
            lambda.and(t -> t.isNull(ChannelCooperation::getChannelShareStep).or().eq(ChannelCooperation::getChannelShareStep, ""));
        }
        if (null != channelCooperation.getPrice()) {
            lambda.eq(ChannelCooperation::getPrice, channelCooperation.getPrice());
        } else {
            lambda.isNull(ChannelCooperation::getPrice);
        }
        if (StringUtils.isNotBlank(channelCooperation.getChannelShareType())) {
            lambda.eq(ChannelCooperation::getChannelShareType, channelCooperation.getChannelShareType());
        } else {
            lambda.and(t -> t.isNull(ChannelCooperation::getChannelShareType).or().eq(ChannelCooperation::getChannelShareType, ""));
        }
        ChannelCooperation one = super.getOne(lambda);
        return one;
    }

    @Override
    public List<ChannelCooperation> getChannelCooperationGeneral(ChannelCooperation channelCooperation) {
        List<ChannelCooperation> channelCooperationList = channelCooperationMapper.selectListSQL(channelCooperation);

        if (CollectionUtil.isNotEmpty(channelCooperationList)) {
            List<UserVO> userVOList = exportDataService.getUser();
            Map<Long, Long> userVOMap = userVOList.stream().collect(Collectors.toMap(s -> Long.valueOf(s.getId()), s -> Long.valueOf(s.getCardNumber())));

            for (ChannelCooperation channelCooperationSub : channelCooperationList) {
                if (userVOMap.containsKey(channelCooperationSub.getUserid())) {
                    channelCooperationSub.setUserid(Long.valueOf(userVOMap.get(channelCooperationSub.getUserid())));
                }
            }
        }


        return channelCooperationList;
    }

    @Override
    public void updateBusinessDictBatch(ChannelCooperationPageParam channelCooperationPageParam, HttpServletRequest request) {
        List<String> ccidList = channelCooperationPageParam.getCcidList();

        List<ChannelCooperation> channelCooperationListDB = channelCooperationMapper.selectList(new LambdaQueryWrapper<ChannelCooperation>().in(ChannelCooperation::getCcid, ccidList));
        List<String> departmentCodeListDB = channelCooperationListDB.stream().map(ChannelCooperation::getDepartmentCode).distinct().collect(Collectors.toList());
        if (departmentCodeListDB.size() > 1) {
            throw new BusinessException("CCID集合必须为同一部门");
        } else if (departmentCodeListDB.size() == 0) {
            throw new BusinessException("CCID不存在");
        }
        List<ChannelPromotion> channelPromotionList = channelPromotionMapper.selectList(new LambdaQueryWrapper<ChannelPromotion>().in(ChannelPromotion::getCcid, ccidList));
        if (CollectionUtil.isNotEmpty(channelPromotionList)) {
            List<String> ccidHaveList = channelPromotionList.stream().map(ChannelPromotion::getCcid).distinct().collect(Collectors.toList());
            throw new BusinessException("CCID已关联PID，无法修改业务分类：" + String.join(",", ccidHaveList));
        }

        ChannelCooperation channelCooperation = DozerUtil.toBean(channelCooperationPageParam, ChannelCooperation.class);
        channelCooperation.setDepartmentCode(departmentCodeListDB.get(0));

        // 处理业务分类ID
        BusinessDict businessDict = handlerBusinessDictId(channelCooperation.getBusinessDictId(), channelCooperation.getDepartmentCode(), channelCooperation.getFirstLevelBusiness(), channelCooperation.getSecondLevelBusiness(), channelCooperation.getThirdLevelBusiness());
        channelCooperation.setBusinessDictId(businessDict.getId());
        channelCooperation.setFirstLevelBusiness(businessDict.getFirstLevel());
        channelCooperation.setSecondLevelBusiness(businessDict.getSecondLevel());
        channelCooperation.setThirdLevelBusiness(businessDict.getThirdLevel());

        for (ChannelCooperation ccid : channelCooperationListDB) {
            ccid.setBusinessDictId(channelCooperation.getBusinessDictId());
            ccid.setFirstLevelBusiness(channelCooperation.getFirstLevelBusiness());
            ccid.setSecondLevelBusiness(channelCooperation.getSecondLevelBusiness());
            ccid.setThirdLevelBusiness(channelCooperation.getThirdLevelBusiness());
        }

        this.updateBatchById(channelCooperationListDB);

        channelPromotionAllService.updateBusinessDictBatchThread(ccidList, channelCooperation);
    }

    private void exists(ChannelCooperation channelCooperation, String chargeRule) {
        ChannelCooperation one = getOneCCID(channelCooperation, chargeRule);
        if (ObjectUtil.isNotEmpty(one)) {
            throw new BusinessException(String.format("【%s】渠道合作CCID已经存在。", channelCooperation.getDepartmentName()));
        }
    }

    private String getChargeRule(ChannelCooperation channelCooperation) {
        Map<String, String> map = MapUtil.<String, String>builder()
                .put("自运营CPS", "CPS").put("非A", "NOA").put("自然量", "ZRL").put("eCPM", "CPM")
                .put("历史未归属", "OTR").put("其他", "OTR").put("联运CPS", "CPS").put("盛天自运营", "OTR")
                .build();
        String chargeRule = channelCooperation.getChargeRule();
        if (StrUtil.length(chargeRule) == 3 && check(chargeRule)) {
            chargeRule = chargeRule;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateChannelCooperation(ChannelCooperation channelCooperation, HttpServletRequest request) throws Exception {
        UserEntity user = BiSessionUtil.build(redisTemplate, request).getSessionUser();

        if (ObjectUtil.isEmpty(channelCooperation.getChannelRate())) {
            channelCooperation.setChannelRate(BigDecimal.ZERO);
        }

        //处理业务分类
        BusinessDict businessDict = handlerBusinessDictId(channelCooperation.getBusinessDictId(), channelCooperation.getDepartmentCode(), channelCooperation.getFirstLevelBusiness(), channelCooperation.getSecondLevelBusiness(), channelCooperation.getThirdLevelBusiness());
        channelCooperation.setBusinessDictId(businessDict.getId());
        channelCooperation.setFirstLevelBusiness(businessDict.getFirstLevel());
        channelCooperation.setSecondLevelBusiness(businessDict.getSecondLevel());
        channelCooperation.setThirdLevelBusiness(businessDict.getThirdLevel());

        //判断是否有重复
        ChannelCooperation cooperationInDB = channelCooperationMapper.selectById(channelCooperation.getId());
        if (!equals(channelCooperation, cooperationInDB)) {
            exists(channelCooperation, channelCooperation.getChargeRule());
        }
        handlerChannel(channelCooperation, user);
        // 修改部门或渠道后，对PID子渠道、推广位、推广媒介进行处理
        handlerUpdateDepartmentAndChannel(channelCooperation, user);

        super.updateById(channelCooperation);

        //存宽表
        channelPromotionAllService.updateCCIDThread(channelCooperation);

        return true;
    }

    /**
     * 是否有更新
     *
     * @param upd
     * @param db
     * @return
     */
    private boolean equals(ChannelCooperation upd, ChannelCooperation db) {
        return upd.getDepartmentCode().equals(db.getDepartmentCode())
                && upd.getAgentName().equals(db.getAgentName())
                && upd.getFirstLevelBusiness().equals(db.getFirstLevelBusiness())
                && upd.getSecondLevelBusiness().equals(db.getSecondLevelBusiness())
                && StrUtil.equals(upd.getThirdLevelBusiness(), db.getThirdLevelBusiness())
                && upd.getChargeRule().equals(db.getChargeRule())
                && upd.getChannelName().equals(db.getChannelName())
                && equals4BigDecimal(upd.getPrice(), db.getPrice())
                && equals4BigDecimal(upd.getChannelRate(), db.getChannelRate())
                && equals4BigDecimal(upd.getChannelShare(), db.getChannelShare())
                && StrUtil.equals(upd.getChannelShareStep(), db.getChannelShareStep())
                && upd.getBusinessDictId().equals(db.getBusinessDictId());
    }

    private boolean equals4BigDecimal(BigDecimal a, BigDecimal b) {
        boolean flag = false;
        if (null == a && null == b) {
            flag = true;
        } else if (null != a && null != b && a.compareTo(b) == 0) {
            flag = true;
        }
        return flag;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteChannelCooperation(Long id) throws Exception {

        ChannelCooperation channelCooperation = channelCooperationMapper.selectById(id);
        if (null == channelCooperation) {
            throw new BusinessException("不存在");
        }
        int listCountByCCID = channelPromotionService.count(new LambdaQueryWrapper<ChannelPromotion>()
                .eq(StrUtil.isNotEmpty(channelCooperation.getCcid()), ChannelPromotion::getCcid, channelCooperation.getCcid()));
        if (listCountByCCID > 0) {
            throw new BusinessException("关联PID，无法删除");
        }
        int historyCountByCCID = channelPromotionHistoryService.count(new LambdaQueryWrapper<ChannelPromotionHistory>()
                .eq(StrUtil.isNotEmpty(channelCooperation.getCcid()), ChannelPromotionHistory::getCcid, channelCooperation.getCcid()));
        if (historyCountByCCID > 0) {
            throw new BusinessException("关联历史迁移PID，无法删除");
        }

        //删除权限系统对应的绑定树的CCID
        log.info("开始删除权限系统对应的绑定树的CCID" + channelCooperation.getCcid());
        ResultEntity resultEntity = sysClient.delDmByCcid(channelCooperation.getCcid());
        if (ResultEntity.ResultEntityEnum.FAILURE.getCode() == resultEntity.getCode()) {
            throw new BusinessException("权限系统对应的绑定树的CCID，删除失败");
        } else {
            log.info("权限系统对应的绑定树的CCID，删除成功" + channelCooperation.getCcid());
        }

        return super.removeById(id);
    }

    @Override
    public ChannelCooperation info(String ccid, boolean isCheck, HttpServletRequest request) throws Exception {
        if (isCheck) {
            checkRole(ccid, request);
        }
        ChannelCooperation channelCooperation = channelCooperationMapper.selectOne(new QueryWrapper<ChannelCooperation>().lambda().eq(StrUtil.isNotEmpty(ccid), ChannelCooperation::getCcid, ccid));

        Channel channel = channelService.getOne(new LambdaQueryWrapper<Channel>().eq(Channel::getChannelId, channelCooperation.getChannelId()));
        channelCooperation.setSecretTypeStr(SecretTypeEnum.getByKey(channel.getSecretType()).getValue());
        channelCooperation.setChannelDepartmentName(channel.getDepartmentName());

        ChannelClass channelClass = channelClassCooperationMapper.getChannelClassPath(channelCooperation);
        if (ObjectUtil.isNotEmpty(channelClass)) {
            channelCooperation.setChannelClassStr((channelClass.getNodePath() + "," + channelClass.getName()).replace(",", "/"));
        }

        return channelCooperation;
    }

    public void checkRole(String ccid, HttpServletRequest request) {
        ChannelPromotionPageParam channelPromotionPageParam = new ChannelPromotionPageParam();
        channelPromotionPageParam.setCcid(ccid);

        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelPromotionPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelPromotionPageParam.setMapAll(mapAll);

        //如果当前ccid关联了pid  普通人不能修改，管理员可以继续修改
        Long count = channelPromotionMapper.countByCCID(channelPromotionPageParam, user);
        if (count > 0) {
            Integer roleAdminId = dataManagementConfig.getRoleAdminId();
            System.out.println("--------------->1" + roleAdminId);
            List<Integer> roleIdList = user.getRoles().stream().map(UserRoleEntity::getRoleId).collect(Collectors.toList());
            System.out.println("--------------->2" + JSON.toJSONString(roleIdList));
            if (!roleIdList.contains(roleAdminId)) {
                throw new BusinessException("关联PID，如需修改，联系数据分析部。");
            }
        }
    }


    @Override
    public PageEntity<ChannelCooperation> getChannelCooperationPageList(ChannelCooperationPageParam channelCooperationPageParam, HttpServletRequest request) throws Exception {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelCooperationPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelCooperationPageParam.setMapAll(mapAll);

        Page<ChannelCooperation> page = new Page<>(channelCooperationPageParam.getPageIndex(), channelCooperationPageParam.getPageSize());
        List<ChannelCooperation> channelCooperationList = channelCooperationMapper.getChannelCooperationPageList(page, channelCooperationPageParam, user);
        return new PageEntity<>(page, channelCooperationList);
    }

    @Override
    public PageEntity<GetAgentVO> getAgentPageList(ChannelCooperationPageParam channelCooperationPageParam) throws Exception {
        Page<ChannelCooperation> page = new Page<>(channelCooperationPageParam.getPageIndex(), channelCooperationPageParam.getPageSize());
        //TODO 业务方说条件必须带部门
        QueryWrapper<ChannelCooperation> wrapper = new QueryWrapper<ChannelCooperation>().select("distinct agent_id,agent_name,department_name,department_code");
        wrapper.eq(channelCooperationPageParam.getAgentId() != null, getLambdaColumn(ChannelCooperation::getAgentId), channelCooperationPageParam.getAgentId());
        wrapper.eq(channelCooperationPageParam.getChannelId() != null, getLambdaColumn(ChannelCooperation::getChannelId), channelCooperationPageParam.getChannelId());

        wrapper.eq(StrUtil.isNotEmpty(channelCooperationPageParam.getDepartmentCode()), getLambdaColumn(ChannelCooperation::getDepartmentCode), channelCooperationPageParam.getDepartmentCode());
        wrapper.eq(StrUtil.isNotEmpty(channelCooperationPageParam.getFirstLevelBusiness()), getLambdaColumn(ChannelCooperation::getFirstLevelBusiness), channelCooperationPageParam.getFirstLevelBusiness());
        wrapper.eq(StrUtil.isNotEmpty(channelCooperationPageParam.getSecondLevelBusiness()), getLambdaColumn(ChannelCooperation::getSecondLevelBusiness), channelCooperationPageParam.getSecondLevelBusiness());
        wrapper.eq(StrUtil.isNotEmpty(channelCooperationPageParam.getThirdLevelBusiness()), getLambdaColumn(ChannelCooperation::getThirdLevelBusiness), channelCooperationPageParam.getThirdLevelBusiness());
        wrapper.eq(StrUtil.isNotEmpty(channelCooperationPageParam.getChargeRule()), getLambdaColumn(ChannelCooperation::getChargeRule), channelCooperationPageParam.getChargeRule());

        wrapper.exists(channelCooperationPageParam.getSubChannelId() != null,
                StrUtil.format("select 1 from dm_channel_child where dm_channel_child.ccid = dm_channel_cooperation.ccid and dm_channel_child.sub_channel_id = {}", channelCooperationPageParam.getSubChannelId()));

        IPage<ChannelCooperation> iPage = channelCooperationMapper.selectPage(page, wrapper);

        List<GetAgentVO> agentVOList = iPage.getRecords().stream().map(v -> {
            GetAgentVO getAgentVO = new GetAgentVO();
            getAgentVO.setAgentId(v.getAgentId());
            getAgentVO.setAgentName(v.getAgentName());
            getAgentVO.setDepartmentName(v.getDepartmentName());
            getAgentVO.setDepartmentCode(v.getDepartmentCode());
            return getAgentVO;
        }).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(agentVOList)) {
            return new PageEntity<>(iPage, agentVOList);
        }
        List<Long> agentIdList = agentVOList.stream().map(GetAgentVO::getAgentId).collect(Collectors.toList());
        List<ChannelCooperation> channelCooperationList = super.list(new LambdaQueryWrapper<ChannelCooperation>().in(ChannelCooperation::getAgentId, agentIdList));
        Map<Long, List<ChannelCooperation>> agentIdToChannelCooperation = channelCooperationList.stream().collect(Collectors.groupingBy(ChannelCooperation::getAgentId));

        List<String> ccidList = channelCooperationList.stream().map(ChannelCooperation::getCcid).collect(Collectors.toList());
        List<ChannelChild> channelChildren = channelChildService.list(new LambdaQueryWrapper<ChannelChild>()
                        // update yf by 20210717(已撤回)
                        .in(ChannelChild::getCcid, ccidList)
                /*.in(ccidList != null && ccidList.size() > 0, ChannelChild::getChannelId,
                        channelCooperationMapper.selectList(new QueryWrapper<ChannelCooperation>().lambda().in(ChannelCooperation::getCcid, ccidList))
                                .stream().map(ChannelCooperation::getChannelId).collect(Collectors.toList())
                )*/
        );

        Map<String, Set<String>> ccidToSubChannelIdSetMap = channelChildren.stream().collect(Collectors.groupingBy(ChannelChild::getCcid, Collectors.mapping(ChannelChild::getSubChannelId, Collectors.toSet())));

        for (GetAgentVO getAgentVO : agentVOList) {
            Long agentId = getAgentVO.getAgentId();
            List<ChannelCooperation> channelCooperationListTemp = agentIdToChannelCooperation.get(agentId);
            Map<Long, String> channelMap = channelCooperationListTemp.stream().filter(coo -> StrUtil.equals(getAgentVO.getDepartmentCode(), coo.getDepartmentCode())).collect(Collectors.toMap(ChannelCooperation::getChannelId, ChannelCooperation::getChannelName, (v1, v2) -> v1));
            List<GetAgentVO.Channel> channelList = channelMap.entrySet().stream().map(v -> {
                GetAgentVO.Channel channel = new GetAgentVO.Channel();
                channel.setChannelId(v.getKey());
                channel.setChannelName(v.getValue());
                return channel;
            }).collect(Collectors.toList());
            getAgentVO.setChannelList(channelList);
            if (CollectionUtil.isNotEmpty(channelList)) {
                getAgentVO.setChannelDisplay(channelList.stream().map(GetAgentVO.Channel::getChannelName).collect(Collectors.joining("、")));
            }

            Set<String> ccidSet = channelCooperationListTemp.stream().map(ChannelCooperation::getCcid).collect(Collectors.toSet());
            Set<String> subChannelIdSet = ccidSet.stream().map(v -> ccidToSubChannelIdSetMap.get(v)).filter(CollectionUtil::isNotEmpty).flatMap(Set::stream).collect(Collectors.toSet());
            getAgentVO.setSubChannelCount(Optional.ofNullable(subChannelIdSet).orElse(new HashSet<>()).size());
        }
        return new PageEntity<GetAgentVO>(iPage, agentVOList);
    }

    public <T> String getLambdaColumn(SFunction<T, ?> func) {
        return new LambdaColumn<T>().get(func);
    }

    @Override
    public List<GetChannelVO> getChannelList(Long agentId, String departmentCode) throws Exception {
        LambdaQueryWrapper<ChannelCooperation> wrapper = new LambdaQueryWrapper<ChannelCooperation>().eq(ChannelCooperation::getAgentId, agentId).eq(StrUtil.isNotEmpty(departmentCode), ChannelCooperation::getDepartmentCode, departmentCode);
        List<ChannelCooperation> channelCooperationList = channelCooperationMapper.selectList(wrapper);
        List<GetChannelVO> channelVOList = DozerUtil.toBeanList(channelCooperationList, GetChannelVO.class);
        if (CollectionUtil.isEmpty(channelVOList)) {
            log.info("没有渠道");
            return channelVOList;
        }
        List<String> ccidList = channelVOList.stream().map(GetChannelVO::getCcid).collect(Collectors.toList());
        // update yf by 20210717(已撤回)
        List<ChannelChild> channelChildList = channelChildService.list(new LambdaQueryWrapper<ChannelChild>().in(ChannelChild::getCcid, ccidList));
        /*List<ChannelChild> channelChildList = channelChildService.list(new LambdaQueryWrapper<ChannelChild>()
                .in(ccidList != null && ccidList.size() > 0, ChannelChild::getChannelId,
                        channelCooperationMapper.selectList(new QueryWrapper<ChannelCooperation>().lambda().in(ChannelCooperation::getCcid, ccidList))
                                .stream().map(ChannelCooperation::getChannelId).collect(Collectors.toList())
                )
        );*/

        if (CollectionUtil.isEmpty(channelChildList)) {
            log.info("没有子渠道");
            return channelVOList;
        }
        Map<String, List<ChannelChild>> ccidToSubChannelsMap = channelChildList.stream().collect(Collectors.groupingBy(ChannelChild::getCcid));
        List<String> subChannelIdList = channelChildList.stream().map(ChannelChild::getSubChannelId).collect(Collectors.toList());
        List<ChannelPromotion> channelPromotionList = channelPromotionService.list(new LambdaQueryWrapper<ChannelPromotion>().in(ChannelPromotion::getSubChannelId, subChannelIdList));
        Map<String, Set<String>> subChannelIdToPidSetMap = channelPromotionList.stream().collect(Collectors.groupingBy(ChannelPromotion::getSubChannelId, Collectors.mapping(ChannelPromotion::getPid, Collectors.toSet())));
        for (GetChannelVO channelVO : channelVOList) {
            String ccid = channelVO.getCcid();
            List<ChannelChild> channelChildren = ccidToSubChannelsMap.get(ccid);
            List<GetChannelVO.SubChannel> subChannels = DozerUtil.toBeanList(channelChildren, GetChannelVO.SubChannel.class);
            for (GetChannelVO.SubChannel subChannel : subChannels) {
                subChannel.setPidCount(Optional.ofNullable(subChannelIdToPidSetMap.get(subChannel.getSubChannelId())).orElse(new HashSet<>()).size());
            }
            channelVO.setSubChannelList(subChannels);
        }
        return channelVOList;
    }

    @Override
    public List<ChannelCooperation> getChannelCooperationList(ChannelCooperationPageParam channelCooperationPageParam, HttpServletRequest request) throws Exception {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelCooperationPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelCooperationPageParam.setMapAll(mapAll);

        List<ChannelCooperation> ChannelCooperationList = channelCooperationMapper.getChannelCooperationList(channelCooperationPageParam, user);
        return ChannelCooperationList;
    }

    @Override
    public PageEntity<ChannelCooperation> getChannelCooperationPageListExt(ChannelCooperationPageParam channelCooperationPageParam, HttpServletRequest request) throws Exception {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        channelCooperationPageParam.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        channelCooperationPageParam.setMapAll(mapAll);

        Page<ChannelCooperation> page = new Page<>(channelCooperationPageParam.getPageIndex(), channelCooperationPageParam.getPageSize());
        List<OrderItem> orderItemList = CollectionUtil.isNotEmpty(channelCooperationPageParam.getOrders()) ? channelCooperationPageParam.getOrders() : new ArrayList<OrderItem>();
        orderColumn(orderItemList);
        page.setOrders(orderItemList);

        List<String> columnList = orderItemList.stream().map(OrderItem::getColumn).collect(Collectors.toList());
        List<String> columnBList = new ArrayList<String>(Arrays.asList("subChannelNum", "appNum", "pidNum"));
        columnList.retainAll(columnBList);
        if (CollectionUtil.isNotEmpty(columnList)) {
            // 关联子渠道、关联产品/应用、关联PID 进行排序
            channelCooperationPageParam.setLongTimeOrder("1");
        }

        List<ChannelCooperation> channelCooperationList = channelCooperationMapper.listChannelCooperation(page, channelCooperationPageParam, user);

        Map<String, Long> channelChildMap = new HashMap<String, Long>();
        Map<String, Long> appMap = new HashMap<String, Long>();
        Map<String, Long> pidMap = new HashMap<String, Long>();
        if (!"1".equals(channelCooperationPageParam.getLongTimeOrder())) {
            List<String> ccidList = channelCooperationList.stream().map(ChannelCooperation::getCcid).collect(Collectors.toList());
            channelCooperationPageParam.setCcidList(ccidList);

            //子渠道
            List<ChannelCooperation> subChannelNumber = channelPromotionMapper.countSubChannelBatch(channelCooperationPageParam, user);
            channelChildMap = subChannelNumber.stream().collect(Collectors.toMap(ChannelCooperation::getCcid, s -> s.getNumber()));
            //关联的产品应用
            List<ChannelCooperation> appNumber = channelPromotionMapper.countAppBatch(channelCooperationPageParam, user);
            appMap = appNumber.stream().collect(Collectors.toMap(ChannelCooperation::getCcid, s -> s.getNumber()));
            //关联PID
            List<ChannelCooperation> pidNumber = channelPromotionMapper.countPidByCcidBatch(channelCooperationPageParam, user);
            pidMap = pidNumber.stream().collect(Collectors.toMap(ChannelCooperation::getCcid, s -> s.getNumber()));
        }

        for (ChannelCooperation channelCooperation : channelCooperationList) {
            if (ObjectUtil.isEmpty(channelCooperation.getSubChannelNum())) {
                channelCooperation.setSubChannelNum(channelChildMap.containsKey(channelCooperation.getCcid()) ? channelChildMap.get(channelCooperation.getCcid()) : 0l);
            }
            if (ObjectUtil.isEmpty(channelCooperation.getAppNum())) {
                channelCooperation.setAppNum(appMap.containsKey(channelCooperation.getCcid()) ? appMap.get(channelCooperation.getCcid()) : 0l);
            }
            if (ObjectUtil.isEmpty(channelCooperation.getPidNum())) {
                channelCooperation.setPidNum(pidMap.containsKey(channelCooperation.getCcid()) ? pidMap.get(channelCooperation.getCcid()) : 0l);
            }
        }
        //关联历史PID
        /*List<ChannelCooperation> pidHistoryNumber = channelPromotionMapper.countPidHistoryByCcidBatch(channelCooperationPageParam, user.getId());
        Map<String, Long> pidHistoryMap = pidHistoryNumber.stream().collect(Collectors.toMap(ChannelCooperation::getCcid, s -> s.getNumber()));*/

        return new PageEntity<>(page, channelCooperationList);
    }

    public void orderColumn(List<OrderItem> orderItemList) {
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<String> errorOrder = new ArrayList<String>();
            List<OrderItem> channelShareAdd = new ArrayList<OrderItem>();
            List<OrderItem> levelBusinessAdd = new ArrayList<OrderItem>();

            for (OrderItem orderItem : orderItemList) {
                if ("ccid".equals(orderItem.getColumn())) {
                    orderItem.setColumn("ccid");
                    continue;
                } else if ("agentName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("agent_name");
                    continue;
                } else if ("channelName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("channel_name");
                    continue;
                } else if ("chargeRule".equals(orderItem.getColumn())) {
                    orderItem.setColumn("charge_rule");
                    continue;
                } else if ("channelShare".equals(orderItem.getColumn())) {
                    orderItem.setColumn("channel_share");
                    OrderItem orderItem1 = new OrderItem();
                    orderItem1.setColumn("channel_share_step");
                    orderItem1.setAsc(orderItem.isAsc());
                    OrderItem orderItem2 = new OrderItem();
                    orderItem2.setColumn("price");
                    orderItem2.setAsc(orderItem.isAsc());

                    channelShareAdd.add(orderItem1);
                    channelShareAdd.add(orderItem2);
                    continue;
                } else if ("channelRate".equals(orderItem.getColumn())) {
                    orderItem.setColumn("channel_rate");
                    continue;
                } else if ("departmentName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("department_name");
                    continue;
                } else if ("levelBusiness".equals(orderItem.getColumn())) {
                    orderItem.setColumn("first_level_business");
                    OrderItem orderItem2 = new OrderItem();
                    orderItem2.setColumn("second_level_business");
                    orderItem2.setAsc(orderItem.isAsc());
                    OrderItem orderItem3 = new OrderItem();
                    orderItem3.setColumn("third_level_business");
                    orderItem3.setAsc(orderItem.isAsc());

                    levelBusinessAdd.add(orderItem2);
                    levelBusinessAdd.add(orderItem3);
                    continue;
                } else if ("usernameName".equals(orderItem.getColumn())) {
                    orderItem.setColumn("username");
                    continue;
                } else if ("updateTime".equals(orderItem.getColumn())) {
                    orderItem.setColumn("update_time");
                    continue;
                } else if ("subChannelNum".equals(orderItem.getColumn())) {
                    orderItem.setColumn("subChannelNum");
                    continue;
                } else if ("appNum".equals(orderItem.getColumn())) {
                    orderItem.setColumn("appNum");
                    continue;
                } else if ("pidNum".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pidNum");
                    continue;
                } else if ("pidHistoryNum".equals(orderItem.getColumn())) {
                    orderItem.setColumn("pidHistoryNum");
                    continue;
                }
                errorOrder.add(orderItem.getColumn());
            }

            if (CollectionUtil.isNotEmpty(errorOrder)) {
                throw new BusinessException("非法排序参数：" + org.apache.commons.lang.StringUtils.join(errorOrder, ","));
            }

            orderItemList.addAll(channelShareAdd);
            orderItemList.addAll(levelBusinessAdd);
        } else {
            OrderItem orderItem = new OrderItem();
            orderItem.setColumn("c.id");
            orderItem.setAsc(false);
            orderItemList.add(orderItem);
        }
    }

    @Override
    public ResultEntity<List<DepartmentVO>> listDepartment(ChannelCooperationPageParam channelCooperationPageParam) {
        List<DepartmentVO> departmentVOList = channelCooperationMapper.selectList(new QueryWrapper<ChannelCooperation>().select("department_code", "department_name").lambda()
                .eq(ObjectUtil.isNotNull(channelCooperationPageParam.getAgentId()), ChannelCooperation::getAgentId, channelCooperationPageParam.getAgentId())
                .eq(ObjectUtil.isNotNull(channelCooperationPageParam.getChannelId()), ChannelCooperation::getChannelId, channelCooperationPageParam.getChannelId()))
                .stream().map(coo -> new DepartmentVO(coo.getDepartmentCode(), coo.getDepartmentName())).collect(Collectors.toList());
        return ResultEntity.success(departmentVOList);
    }

    @Override
    public ResultEntity<List<AgentVO>> listAgent(ChannelCooperationPageParam channelCooperationPageParam) {
        List<AgentVO> agentVOList = channelCooperationMapper.selectList(new QueryWrapper<ChannelCooperation>().select("agent_id", "agent_name").lambda()
                .eq(ObjectUtil.isNotNull(channelCooperationPageParam.getDepartmentCode()), ChannelCooperation::getDepartmentCode, channelCooperationPageParam.getDepartmentCode())
                .eq(ObjectUtil.isNotNull(channelCooperationPageParam.getChannelId()), ChannelCooperation::getChannelId, channelCooperationPageParam.getChannelId()))
                .stream().map(coo -> new AgentVO(coo.getAgentId(), coo.getAgentName())).collect(Collectors.toList());
        return ResultEntity.success(agentVOList);
    }

    @Override
    public ResultEntity<List<ChannelVO>> listChannel(ChannelCooperationPageParam channelCooperationPageParam) {
        List<ChannelVO> channelVOList = channelCooperationMapper.selectList(new QueryWrapper<ChannelCooperation>().select("channel_id", "channel_name").lambda()
                .eq(ObjectUtil.isNotNull(channelCooperationPageParam.getAgentId()), ChannelCooperation::getAgentId, channelCooperationPageParam.getAgentId())
                .eq(ObjectUtil.isNotNull(channelCooperationPageParam.getDepartmentCode()), ChannelCooperation::getDepartmentCode, channelCooperationPageParam.getDepartmentCode()))
                .stream().map(coo -> new ChannelVO(coo.getChannelId(), coo.getChannelName())).collect(Collectors.toList());
        return ResultEntity.success(channelVOList);
    }

    @Override
    public ChannelCooperation getWithId(Long id, HttpServletRequest request) {
        ChannelCooperation channelCooperation = channelCooperationMapper.selectById(id);

        Channel channel = channelService.getOne(new LambdaQueryWrapper<Channel>().eq(Channel::getChannelId, channelCooperation.getChannelId()));
        channelCooperation.setChannelDepartmentCode(channel.getDepartmentCode());
        channelCooperation.setChannelDepartmentName(channel.getDepartmentName());
        channelCooperation.setSecretTypeStr(SecretTypeEnum.getByKey(channel.getSecretType()).getValue());
        return channelCooperation;
    }

    @Override
    public List<ChannelCooperation> selectListByExcel(List<ExportDataParam> channelCooperationByExcel) {
        return channelCooperationMapper.selectListByExcel(channelCooperationByExcel);
    }

    @Override
    public Map<String, Object> searchAll(ChannelCooperationPageParam param, HttpServletRequest request) {
        // 权限 By yf
        UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
        List<String> departmentCodeAllList = businessDictService.getDepartmentListByPermissions(user);
        param.setDepartmentCodeAllList(departmentCodeAllList);
        Map<Integer, List<UserDmEntity>> mapAll = businessDictService.getCCIDAndPidByPermissions(user);
        param.setMapAll(mapAll);

        if (StringUtils.isNotBlank(param.getLevelBusinessBox())) {
            String[] splitBusinessLevel = StrUtil.splitToArray(param.getLevelBusinessBox(), ',');
            String firstLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 0);
            String secondLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 1);
            String thirdLevelBusiness = ArrayUtil.<String>get(splitBusinessLevel, 2);
            param.setFirstLevelBusiness(firstLevelBusiness);
            param.setSecondLevelBusiness(secondLevelBusiness);
            param.setThirdLevelBusiness(thirdLevelBusiness);
        }

        List<ChannelCooperation> channelList = channelCooperationMapper.searchAll(param, user);

        //部门
        Set<Map<String, String>> departmentList = new HashSet<Map<String, String>>();
        //渠道
        Set<Map<String, String>> channelMapList = new HashSet<Map<String, String>>();
        //业务分类
        Set<Map<String, String>> levelList = new HashSet<Map<String, String>>();
        for (ChannelCooperation channelCooperation : channelList) {
            if (StringUtils.isNotBlank(channelCooperation.getDepartmentCode())) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("code", channelCooperation.getDepartmentCode());
                map.put("name", channelCooperation.getDepartmentName());
                departmentList.add(map);
            }
            if (channelCooperation.getChannelId() != null) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("channelId", channelCooperation.getChannelId().toString());
                map.put("channelName", channelCooperation.getChannelName());
                channelMapList.add(map);
            }
            if (channelCooperation.getChannelId() != null) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("firstLevelBusiness", channelCooperation.getFirstLevelBusiness());
                map.put("secondLevelBusiness", channelCooperation.getSecondLevelBusiness());
                map.put("thirdLevelBusiness", channelCooperation.getThirdLevelBusiness());
                levelList.add(map);
            }
        }

        departmentList = departmentList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(i -> i.get("code")))), HashSet::new));
        channelMapList = channelMapList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(i -> i.get("channelId")))), HashSet::new));
        levelList = levelList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(i -> i.get("firstLevelBusiness") + i.get("secondLevelBusiness") + i.get("thirdLevelBusiness")))), HashSet::new));

        Map<String, Object> mapAlls = new HashMap<String, Object>();
        mapAlls.put("department", departmentList);
        mapAlls.put("channel", channelMapList);
        mapAlls.put("levelList", levelList);

        return mapAlls;
    }

    @Override
    public ChannelCooperation saveChannelCooperationGeneral(ChannelCooperation channelCooperation) throws Exception {
        //参数处理
        emptyParam(channelCooperation);
        //默认值处理（部门、年份、业务分类、渠道费率、负责人）
        defaultParam(channelCooperation);

        //判断是否新增渠道
        Boolean isAdd = handlerChannelGeneral(channelCooperation);
        //判断CCID是否存在
        try {
            exists(channelCooperation, channelCooperation.getChargeRule());
            String chargeRule = getChargeRule(channelCooperation);
            String ccid = channelBaseIdService.getNewCCID(channelCooperation.getChannelId(), chargeRule);
            channelCooperation.setCcid(ccid);
            super.save(channelCooperation);
        } catch (Exception e) {
            if (isAdd && ObjectUtil.isNotEmpty(channelCooperation.getChannelId())) {
                //删除新增加的渠道
                channelService.remove(new QueryWrapper<Channel>().lambda().eq(Channel::getChannelId, channelCooperation.getChannelId()));
            }
            throw new BusinessException(e.getMessage(), e);
        }

        return channelCooperation;
    }

    public void defaultParam(ChannelCooperation channelCooperation) {
        //部门CODE
        List<String> resultList = channelProductService.departmentCodeAndNameVaild(channelCooperation.getDepartmentCode(), channelCooperation.getDepartmentName());
        channelCooperation.setDepartmentCode(resultList.get(0));
        channelCooperation.setDepartmentName(resultList.get(1));
        //业务分类
        if (ObjectUtils.isNotEmpty(channelCooperation.getBusinessDictId())) {
            BusinessDict businessDict = businessDictService.getOne(new LambdaQueryWrapper<BusinessDict>().eq(BusinessDict::getId, channelCooperation.getBusinessDictId()));
            if (ObjectUtil.isEmpty(businessDict)) {
                throw new BusinessException("业务分类不存在");
            } else if (!businessDict.getDepartmentCode().equals(channelCooperation.getDepartmentCode())
                    || !(businessDict.getYearStart() <= DateUtil.year(new Date()) && DateUtil.year(new Date()) <= businessDict.getYearEnd())
                    || ((StringUtils.isNotBlank(channelCooperation.getFirstLevelBusiness())
                    && StringUtils.isNotBlank(channelCooperation.getSecondLevelBusiness())
                    && StringUtils.isNotBlank(channelCooperation.getThirdLevelBusiness()))
                    && (!channelCooperation.getFirstLevelBusiness().equals(businessDict.getFirstLevel())
                    || !channelCooperation.getSecondLevelBusiness().equals(businessDict.getSecondLevel())
                    || !channelCooperation.getThirdLevelBusiness().equals(businessDict.getThirdLevel())))) {
                throw new BusinessException("业务分类ID与部门不匹配或已失效");
            } else {
                channelCooperation.setFirstLevelBusiness(businessDict.getFirstLevel());
                channelCooperation.setSecondLevelBusiness(businessDict.getSecondLevel());
                channelCooperation.setThirdLevelBusiness(businessDict.getThirdLevel());
            }
        } else {
            List<BusinessDict> businessDictList = businessDictService.list(new QueryWrapper<BusinessDict>().lambda()
                    .eq(BusinessDict::getDepartmentCode, channelCooperation.getDepartmentCode())
                    .le(BusinessDict::getYearStart, DateUtil.year(new Date()))
                    .ge(BusinessDict::getYearEnd, DateUtil.year(new Date()))
                    .eq(BusinessDict::getFirstLevel, channelCooperation.getFirstLevelBusiness())
                    .eq(BusinessDict::getSecondLevel, channelCooperation.getSecondLevelBusiness())
                    .eq(BusinessDict::getThirdLevel, channelCooperation.getThirdLevelBusiness())
                    .eq(BusinessDict::getIsValid, 1)
            );
            if (businessDictList.size() > 1) {
                throw new BusinessException("业务分类有重复");
            } else if (businessDictList.size() == 0) {
                throw new BusinessException("业务分类不存在");
            } else {
                channelCooperation.setBusinessDictId(businessDictList.get(0).getId());
            }
        }
        //渠道费率
        if (ObjectUtil.isEmpty(channelCooperation.getChannelRate())) {
            channelCooperation.setChannelRate(BigDecimal.ZERO);
        }
        //负责人
        if (StringUtils.isBlank(channelCooperation.getUsername())) {
            List<UserVO> userVOList = exportDataService.getUser();
            userVOList = userVOList.stream().filter(t -> String.valueOf(channelCooperation.getUserid()).equals(t.getCardNumber())).collect(Collectors.toList());
            if (userVOList.size() != 1) {
                throw new BusinessException("用户工号错误");
            } else {
                channelCooperation.setUserid(Long.valueOf(userVOList.get(0).getId()));
                channelCooperation.setUsername(userVOList.get(0).getCnname());
            }
        }
    }

    public void emptyParam(ChannelCooperation channelCooperation) {
        List<String> errorMsg = new ArrayList<String>();
        if (ObjectUtil.isNull(channelCooperation.getDepartmentName()) && ObjectUtil.isNull(channelCooperation.getDepartmentCode())) {
            errorMsg.add("部门CODE与部门名称不可同时为空");
        }
        if (ObjectUtil.isEmpty(channelCooperation.getBusinessDictId())
                && (StringUtils.isBlank(channelCooperation.getFirstLevelBusiness()) || StringUtils.isBlank(channelCooperation.getSecondLevelBusiness()) || StringUtils.isBlank(channelCooperation.getThirdLevelBusiness()))
        ) {
            errorMsg.add("业务分类ID 与 业务分类一二三级 不可同时为空");
        }
        if (ObjectUtil.isNotEmpty(channelCooperation.getBusinessDictId())
                && (StringUtils.isNotBlank(channelCooperation.getFirstLevelBusiness()) || StringUtils.isNotBlank(channelCooperation.getSecondLevelBusiness()) || StringUtils.isNotBlank(channelCooperation.getThirdLevelBusiness()))
        ) {
            errorMsg.add("业务分类ID 与 业务分类一二三级 不可同时有值");
        }
        if (ObjectUtil.isEmpty(channelCooperation.getChannelId()) && StringUtils.isBlank(channelCooperation.getChannelName())) {
            errorMsg.add("渠道ID和渠道名称不可同时为空");
        }
        if (StringUtils.isBlank(channelCooperation.getChargeRule())) {
            errorMsg.add("计费方式为空");
        }
        if (ObjectUtil.isNull(channelCooperation.getUserid())) {
            errorMsg.add("负责人工号为空");
        }

        if (StringUtils.isBlank(channelCooperation.getChannelType())) {
            channelCooperation.setChannelType("2");
        } else {
            ChannelTypeEnum channelTypeEnum = ChannelTypeEnum.getByKey(Integer.valueOf(channelCooperation.getChannelType()));
            if (ObjectUtil.isEmpty(channelTypeEnum)) {
                errorMsg.add("是否自营参数不正确");
            }
        }

        if (ObjectUtil.isEmpty(channelCooperation.getSecretType())) {
            channelCooperation.setSecretType(1);
        } else {
            SecretTypeEnum secretTypeEnum = SecretTypeEnum.getByKey(channelCooperation.getSecretType());
            if (ObjectUtil.isEmpty(secretTypeEnum)) {
                errorMsg.add("保密类型参数不正确");
            }
        }

        if (StringUtils.isBlank(channelCooperation.getSettlementType())) {
            channelCooperation.setSettlementType("2");
        } else {
            SettlementTypeEnum settlementTypeEnum = SettlementTypeEnum.getByKey(Integer.valueOf(channelCooperation.getSettlementType()));
            if (ObjectUtil.isEmpty(settlementTypeEnum)) {
                errorMsg.add("是否内结参数不正确");
            }
        }

        //chargeRule    channelShareType   price   channelShare    channelShareStep
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isBlank(channelCooperation.getChargeRule())) {
            errorMsg.add("计费方式为空");
        } else {
            List<String> one = Arrays.asList("CPS", "CPA");
            List<String> two = Arrays.asList("CPD", "CPM", "eCPM", "CPC", "CPT");
            List<String> three = Arrays.asList("CPL", "历史未归属", "盛天自运营", "不结算");
            if (one.contains(channelCooperation.getChargeRule())) {
                if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isBlank(channelCooperation.getChannelShareType())) {
                    errorMsg.add(channelCooperation.getChargeRule() + "：结算指标为空");
                }
                if (("CPS".equals(channelCooperation.getChargeRule()) && !Arrays.asList("1", "2").contains(channelCooperation.getChannelShareType())) || ("CPA".equals(channelCooperation.getChargeRule()) && !Arrays.asList("3", "4").contains(channelCooperation.getChannelShareType()))) {
                    errorMsg.add(channelCooperation.getChargeRule() + "：结算指标参数不正确");
                }
                if (ObjectUtil.isNotEmpty(channelCooperation.getChannelShare()) && com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(channelCooperation.getChannelShareStep())) {
                    errorMsg.add(channelCooperation.getChargeRule() + "：固定分成、阶梯分成参数不可同时有值");
                }
                if (ObjectUtil.isEmpty(channelCooperation.getChannelShare()) && com.baomidou.mybatisplus.core.toolkit.StringUtils.isBlank(channelCooperation.getChannelShareStep())) {
                    errorMsg.add(channelCooperation.getChargeRule() + "：固定分成、阶梯分成参数不可同时无值");
                }
                if (ObjectUtil.isNotEmpty(channelCooperation.getPrice())) {
                    errorMsg.add(channelCooperation.getChargeRule() + "：单价参数不可有值");
                }
            } else if (two.contains(channelCooperation.getChargeRule())) {
                if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(channelCooperation.getChannelShareType())) {
                    errorMsg.add(channelCooperation.getChargeRule() + "：结算指标参数不可有值");
                }
                if (ObjectUtil.isNotEmpty(channelCooperation.getChannelShare()) || com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(channelCooperation.getChannelShareStep())) {
                    errorMsg.add(channelCooperation.getChargeRule() + "：固定分成、阶梯分成参数不可有值");
                }
                if (ObjectUtil.isEmpty(channelCooperation.getPrice())) {
                    errorMsg.add(channelCooperation.getChargeRule() + "：单价为空");
                }
            } else if (three.contains(channelCooperation.getChargeRule())) {
                if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(channelCooperation.getChannelShareType())) {
                    errorMsg.add(channelCooperation.getChargeRule() + "：结算指标参数不可有值");
                }
                if (ObjectUtil.isNotEmpty(channelCooperation.getChannelShare()) || com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(channelCooperation.getChannelShareStep())) {
                    errorMsg.add(channelCooperation.getChargeRule() + "：固定分成参数或阶梯分成参数不可有值");
                }
                if (ObjectUtil.isNotEmpty(channelCooperation.getPrice())) {
                    errorMsg.add(channelCooperation.getChargeRule() + "：单价不可有值");
                }
            } else {
                errorMsg.add("计费方式参数不正确");
            }
        }

        if (errorMsg.size() > 0) {
            throw new BusinessException(String.join(",", errorMsg));
        }
    }
}
