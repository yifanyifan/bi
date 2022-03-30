package com.stnts.bi.datamanagement.module.channel.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.CooperationConstant;
import com.stnts.bi.datamanagement.constant.DataSourceConstant;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelBaseIdService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelService;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationBi;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiService;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationService;
import com.stnts.bi.datamanagement.module.feign.SysClient;
import com.stnts.bi.datamanagement.util.ExcelUtils;
import com.stnts.bi.datamanagement.util.SignUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 渠道
 */
@RestController
@RequestMapping("/api/channel")
public class ChannelApiController {
    private static final Logger logger = LoggerFactory.getLogger(ChannelApiController.class);

    @Autowired
    private ChannelBaseIdService channelBaseIdService;

    @Autowired
    private CooperationBiService cooperationBiService;

    private final ChannelController channelController;
    private final ChannelService channelService;
    private final SignUtil signUtil;
    private final SysClient sysClient;
    private final EnvironmentProperties environmentProperties;
    private final CooperationService cooperationService;

    public ChannelApiController(ChannelController channelController, ChannelService channelService, SignUtil signUtil, SysClient sysClient, EnvironmentProperties environmentProperties, CooperationService cooperationService) {
        this.channelController = channelController;
        this.channelService = channelService;
        this.signUtil = signUtil;
        this.sysClient = sysClient;
        this.environmentProperties = environmentProperties;
        this.cooperationService = cooperationService;
    }

    /**
     * 渠道列表
     */
    @GetMapping("/getList")
    public ResultEntity<List<Channel>> getChannelList(Long companyId, String channelName, String appId, Long timestamp, String sign) throws Exception {
        logger.info("===========================>CRM 渠道下拉 请求参数：" + "companyId:" + companyId + ",channelName:" + channelName + ",appId:" + appId + ",timestamp:" + timestamp + ",sign:" + sign);

        if (companyId == null) {
            throw new BusinessException("公司ID为空");
        }

        signUtil.checkSign(appId, timestamp, sign, Dict.create().set("companyId", companyId).set("channelName", channelName));

        ChannelPageParam channelPageParam = new ChannelPageParam();
        channelPageParam.setCompanyId(companyId);
        channelPageParam.setChannelName(channelName);
        return channelController.getChannelListCRM(channelPageParam);
    }

    /**
     * 新境渠道
     */
    @PostMapping("/addChannelGeneral")
    public ResultEntity<Channel> addChannelGeneral(@RequestBody Channel channel, String appId, Long timestamp, String sign) throws Exception {
        logger.info("===========================>新境渠道_通用，请求参数：{}, appId:{}, timestamp:{}, sign:{}", JSON.toJSON(channel), appId, timestamp, sign);

        //鉴权
        signUtil.checkSign(appId, timestamp, sign, Dict.create()
                .set("companyId", channel.getCompanyId())
                .set("channelName", channel.getChannelName())
                .set("departmentName", channel.getDepartmentName())
                .set("departmentCode", channel.getDepartmentCode())
                .set("channelType", channel.getChannelType())
                .set("secretType", channel.getSecretType())
                .set("settlementType", channel.getSettlementType())
                .set("dataSource", channel.getDataSource())
        );

        Channel channelDB = channelService.saveChannelGeneral(channel);

        return ResultEntity.success(channelDB);
    }


    @PostMapping("/getListGeneral")
    public ResultEntity<List<Channel>> getListGeneral(@RequestBody Channel channel, String appId, Long timestamp, String sign) throws Exception {
        logger.info("=====================================>渠道信息列表_通用:{},appId:{},timestamp:{},sign:{}", JSON.toJSONString(channel), appId, timestamp, sign);

        if (appId.equals(environmentProperties.getAppId2())) {
            if (StringUtils.isBlank(channel.getCompanyName()) && StringUtils.isBlank(channel.getChannelName())
                    && StringUtils.isBlank(channel.getDepartmentCode()) && StringUtils.isBlank(channel.getDepartmentName())
                    && ObjectUtil.isEmpty(channel.getUpdateTimeStart()) && ObjectUtil.isEmpty(channel.getUpdateTimeEnd())
            ) {
                throw new BusinessException("查询参数必须选择一个以上");
            }
        }
        signUtil.checkSign(appId, timestamp, sign, Dict.create().set("companyName", channel.getCompanyName())
                .set("channelName", channel.getChannelName())
                .set("departmentCode", channel.getDepartmentCode())
                .set("departmentName", channel.getDepartmentName())
                .set("updateTimeStart", DateUtil.format(channel.getUpdateTimeStart(), "yyyy-MM-dd HH:mm:ss"))
                .set("updateTimeEnd", DateUtil.format(channel.getUpdateTimeEnd(), "yyyy-MM-dd HH:mm:ss"))
        );

        List<Channel> list = channelService.getChannelListGeneral(channel);
        return ResultEntity.success(list);
    }

    @PostMapping("/addBatchTmp")
    public ResultEntity<Boolean> addBatchTmp(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {

        String[] columNames0 = {"cooperationType", "cpName", "companyName", "companyType", "createDepartment", "createDepartmentCode", "isProtection", "isTest", "lastRemark", "dataSource", "handlerUser", "handlerUserName", "createUserId", "createUser", "createTime", "updateTime"};
        List<Map<String, Object>> list0 = ExcelUtils.leadingBySheet(file, columNames0, 2, 0);

        String[] columNames1 = {"companyName", "channelName", "departmentName", "departmentCode", "channelType", "secretType", "userid", "username", "createTime", "updateTime"};
        List<Map<String, Object>> list1 = ExcelUtils.leadingBySheet(file, columNames1, 1, 1);

        List<CooperationBi> cooperationBiList = new ArrayList<CooperationBi>();
        for (Map<String, Object> obj : list0) {
            CooperationBi cooperationBi = JSON.parseObject(JSON.toJSONString(obj), CooperationBi.class);

            if (cooperationBi.getDataSource() == null) {
                cooperationBi.setDataSource(DataSourceConstant.dataSourceBI);
            }
            if (cooperationBi.getLastStatus() == null) {
                cooperationBi.setLastStatus(CooperationConstant.LAST_STATUS_OFF);
            }
            cooperationBiService.saveCooperation(cooperationBi);

            cooperationBiList.add(cooperationBi);
        }
        logger.info("公司excel中有：" + JSON.toJSONString(cooperationBiList));

        Map<String, List<CooperationBi>> cooperationBiMap = cooperationBiList.stream().collect(Collectors.groupingBy(CooperationBi::getCompanyName));

        //如果公司在列表中出现，则以列表为准，如果不在列表中，则以数据库为准（companytype=2）
        for (Map<String, Object> obj : list1) {
            Channel channel = JSON.parseObject(JSON.toJSONString(obj), Channel.class);

            Long cooperationId = null;
            if (cooperationBiMap.containsKey(channel.getCompanyName())) {
                List<CooperationBi> cooperationBiSub = cooperationBiMap.get(channel.getCompanyName());
                if (CollectionUtil.isNotEmpty(cooperationBiSub) && cooperationBiSub.size() > 1) {
                    CooperationBi cooperationBi = cooperationBiSub.stream().filter(i -> i.getCooperationType() == 2).findFirst().get();
                    cooperationId = cooperationBi.getRelatedCooperationId();
                } else if (CollectionUtil.isNotEmpty(cooperationBiSub) && cooperationBiSub.size() == 1) {
                    CooperationBi cooperationBi = cooperationBiSub.stream().findFirst().get();
                    cooperationId = cooperationBi.getRelatedCooperationId();
                }
            } else {
                // 如果现有公司excel中没有，则从，数据库中拿
                List<Cooperation> cooperationList = cooperationService.list(new LambdaQueryWrapper<Cooperation>()
                        .eq(Cooperation::getCompanyName, channel.getCompanyName())
                );
                if (CollectionUtil.isNotEmpty(cooperationList) && cooperationList.size() > 1) {
                    List<Cooperation> cooperation2 = cooperationList.stream().filter(i -> i.getCooperationType() == 2).collect(Collectors.toList());
                    List<Cooperation> cooperation1 = cooperationList.stream().filter(i -> i.getCooperationType() == 1).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(cooperation2)) {
                        cooperationId = cooperation2.get(0).getId();
                    } else {
                        cooperationId = cooperation1.get(0).getId();
                    }
                } else if (CollectionUtil.isNotEmpty(cooperationList) && cooperationList.size() == 1) {
                    Cooperation cooperation = cooperationList.stream().findFirst().get();
                    cooperationId = cooperation.getId();
                } else {
                    logger.info("未找到公司:" + channel.getChannelName() + "," + channel.getCompanyName());
                }
            }
            channel.setCompanyId(cooperationId);
            channel.setSettlementType("2");
            Long channelId = channelBaseIdService.getNewChannelID();
            channel.setChannelId(channelId);
            Boolean b = channelService.save(channel);
            channelBaseIdService.updateNewChannelID(channelId);
        }

        logger.info("==========> over");

        return ResultEntity.success(null);
    }
}

