package com.stnts.bi.datamanagement.module.channel.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotion;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionGeneral;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelChildService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelCooperationService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionService;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelPromotionVO;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelPromotionZaVO;
import com.stnts.bi.datamanagement.util.SignUtil;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import com.stnts.signature.annotation.SignedMapping;
import com.stnts.signature.entity.SignedParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 渠道推广 控制器
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Slf4j
@RestController
@RequestMapping("/sign/channelPromotion")
@Api(value = "渠道推广API", tags = {"签名-渠道推广"})
@SignedMapping
public class ChannelPromotionSignController {

    @Autowired
    private ChannelPromotionService channelPromotionService;
    @Autowired
    private SignUtil signUtil;
    @Autowired
    private ChannelCooperationService channelCooperationService;
    @Autowired
    private ChannelChildService channelChildService;
    @Autowired
    private EnvironmentProperties environmentProperties;

    /**
     * 添加渠道推广
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加渠道推广", response = ResultEntity.class)
    @ApiOperationSupport(ignoreParameters = {"channelPromotion.pid", "channelPromotion.id"})
    public ResultEntity<List<String>> addChannelPromotion(@Validated(Add.class) @RequestBody ChannelPromotion channelPromotion) throws Exception {
        Map<Object, Object> map = channelPromotionService.saveChannelPromotion(channelPromotion);
        List<String> pidList = (List<String>) map.getOrDefault("pidList", Collections.emptyList());
        return ResultEntity.success(pidList);
    }

    @PostMapping("/addChannelPromotionGeneral")
    @ApiOperation(value = "添加PID_通用", response = ResultEntity.class)
    public ResultEntity<Map<String, Object>> addChannelPromotionGeneral(@RequestBody ChannelPromotion channelPromotion, String appId, Long timestamp, String sign) throws Exception {
        log.info("=====================================>添加PID_通用:{},appId:{},timestamp:{},sign:{}", JSON.toJSONString(channelPromotion), appId, timestamp, sign);

        //鉴权
        signUtil.checkSign(appId, timestamp, sign, Dict.create()
                .set("ccid", channelPromotion.getCcid())
                .set("subChannelId", channelPromotion.getSubChannelId())
                .set("subChannelName", channelPromotion.getSubChannelName())
                .set("ppFlag", channelPromotion.getPpFlag())
                .set("ppName", channelPromotion.getPpName())
                .set("extra", channelPromotion.getExtra())
                .set("productCode", channelPromotion.getProductCode())
                .set("applicationId", channelPromotion.getApplicationId())
                .set("mediumId", channelPromotion.getMediumId())
                .set("mediumName", channelPromotion.getMediumName())
                .set("pidAlias", channelPromotion.getPidAlias())
                .set("pidNum", channelPromotion.getPidNum())
                .set("userid", channelPromotion.getUserid())
                .set("checkStartDate", DateUtil.format(channelPromotion.getCheckStartDate(), "yyyy-MM-dd HH:mm:ss"))
                .set("checkEndDate", DateUtil.format(channelPromotion.getCheckEndDate(), "yyyy-MM-dd HH:mm:ss"))
                .set("ccidSettlement", channelPromotion.getCcidSettlement())
                .set("dataSource", channelPromotion.getDataSource())
        );

        Map<String, Object> map = channelPromotionService.saveChannelPromotionGeneral(channelPromotion);

        return ResultEntity.success(map);
    }

    @PostMapping("/getChannelPromotionGeneral")
    @ApiOperation(value = "渠道推广列表_通用", response = ChannelPromotion.class)
    public ResultEntity<List<ChannelPromotion>> getChannelPromotionGeneral(@Validated @RequestBody ChannelPromotionPageParam channelPromotionPageParam, String appId, Long timestamp, String sign) throws Exception {
        log.info("=====================================>渠道推广列表_通用:{},appId:{},timestamp:{},sign:{}", JSON.toJSONString(channelPromotionPageParam), appId, timestamp, sign);

        if (appId.equals(environmentProperties.getAppId2())) {
            if (StringUtils.isBlank(channelPromotionPageParam.getCcid()) && StringUtils.isBlank(channelPromotionPageParam.getProductCode())
                    && (ObjectUtil.isEmpty(channelPromotionPageParam.getChannelId()) && StringUtils.isBlank(channelPromotionPageParam.getSubChannelName()))
                    && StringUtils.isBlank(channelPromotionPageParam.getPid())
            ) {
                throw new BusinessException("查询参数必须选择一个以上");
            }
        }
        signUtil.checkSign(appId, timestamp, sign, Dict.create().set("ccid", channelPromotionPageParam.getCcid())
                .set("productCode", channelPromotionPageParam.getProductCode())
                .set("channelId", channelPromotionPageParam.getChannelId())
                .set("subChannelName", channelPromotionPageParam.getSubChannelName())
                .set("pid", channelPromotionPageParam.getPid())
        );

        List<ChannelPromotion> list = channelPromotionService.getChannelPromotionGeneral(channelPromotionPageParam);
        return ResultEntity.success(list);
    }

    @PostMapping("/addCCPGeneral")
    @ApiOperation(value = "新增渠道+CCID+PID 三合一_通用", response = ResultEntity.class)
    public ResultEntity<ChannelPromotionGeneral> addCCPGeneral(@RequestBody ChannelPromotionGeneral channelPromotionGeneral, String appId, Long timestamp, String sign) throws Exception {
        log.info("=====================================>三合一_通用:{},appId:{},timestamp:{},sign:{}", JSON.toJSONString(channelPromotionGeneral), appId, timestamp, sign);

        //鉴权
        signUtil.checkSign(appId, timestamp, sign, Dict.create()
                .set("departmentName", channelPromotionGeneral.getDepartmentName())
                .set("departmentCode", channelPromotionGeneral.getDepartmentCode())
                .set("businessDictId", channelPromotionGeneral.getBusinessDictId())
                .set("firstLevelBusiness", channelPromotionGeneral.getFirstLevelBusiness())
                .set("secondLevelBusiness", channelPromotionGeneral.getSecondLevelBusiness())
                .set("thirdLevelBusiness", channelPromotionGeneral.getThirdLevelBusiness())
                .set("channelId", channelPromotionGeneral.getChannelId())
                .set("channelName", channelPromotionGeneral.getChannelName())
                .set("companyId", channelPromotionGeneral.getCompanyId())
                .set("channelType", channelPromotionGeneral.getChannelType())
                .set("secretType", channelPromotionGeneral.getSecretType())
                .set("settlementType", channelPromotionGeneral.getSettlementType())
                .set("chargeRule", channelPromotionGeneral.getChargeRule())
                .set("channelShareType", channelPromotionGeneral.getChannelShareType())
                .set("price", channelPromotionGeneral.getPrice())
                .set("channelShare", channelPromotionGeneral.getChannelShare())
                .set("channelShareStep", channelPromotionGeneral.getChannelShareStep())
                .set("channelRate", channelPromotionGeneral.getChannelRate())
                .set("subChannelId", channelPromotionGeneral.getSubChannelId())
                .set("subChannelName", channelPromotionGeneral.getSubChannelName())
                .set("ppFlag", channelPromotionGeneral.getPpFlag())
                .set("ppName", channelPromotionGeneral.getPpName())
                .set("extra", channelPromotionGeneral.getExtra())
                .set("productCode", channelPromotionGeneral.getProductCode())
                .set("applicationId", channelPromotionGeneral.getApplicationId())
                .set("mediumId", channelPromotionGeneral.getMediumId())
                .set("mediumName", channelPromotionGeneral.getMediumName())
                .set("pidAlias", channelPromotionGeneral.getPidAlias())
                .set("pidNum", channelPromotionGeneral.getPidNum())
                .set("userid", channelPromotionGeneral.getUserid())
                .set("checkStartDate", DateUtil.format(channelPromotionGeneral.getCheckStartDate(), "yyyy-MM-dd HH:mm:ss"))
                .set("checkEndDate", DateUtil.format(channelPromotionGeneral.getCheckEndDate(), "yyyy-MM-dd HH:mm:ss"))
                .set("ccidSettlement", channelPromotionGeneral.getCcidSettlement())
                .set("dataSource", channelPromotionGeneral.getDataSource())
        );

        ChannelPromotionGeneral map = channelPromotionService.saveCCPGeneral(channelPromotionGeneral);
        return ResultEntity.success(map);
    }


    /**
     * 修改渠道推广
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改渠道推广", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelPromotion(@Validated(Update.class) @RequestBody ChannelPromotion channelPromotion) throws Exception {
        boolean flag = channelPromotionService.updateChannelPromotion(channelPromotion);
        return ResultEntity.success(flag);
    }

    /**
     * 删除渠道推广
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除渠道推广", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelPromotion(@PathVariable("id") Long id) throws Exception {
        boolean flag = channelPromotionService.deleteChannelPromotion(id);
        return ResultEntity.success(flag);
    }

    /**
     * 获取渠道推广详情
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "渠道推广详情", response = ChannelPromotion.class)
    public ResultEntity<ChannelPromotion> getChannelPromotion(@PathVariable("id") Long id) throws Exception {
        ChannelPromotion channelPromotion = channelPromotionService.getById(id);
        return ResultEntity.success(channelPromotion);
    }

    /**
     * 渠道推广分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "渠道推广分页列表", response = ChannelPromotionVO.class)
    public ResultEntity<PageEntity<ChannelPromotionVO>> getChannelPromotionPageList(@Validated @RequestBody ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception {
        //PageEntity<ChannelPromotionVO> paging = channelPromotionService.getChannelPromotionPageList(channelPromotionPageParam, request);
        PageEntity<ChannelPromotionVO> paging = channelPromotionService.getPidPageList(channelPromotionPageParam, request);
        return ResultEntity.success(paging);
    }

    /**
     * pid分页列表
     */
    @PostMapping("/getPidPageList")
    @ApiOperation(value = "pid分页列表", response = ChannelPromotionVO.class)
    public ResultEntity<PageEntity<ChannelPromotionVO>> getPidPageList(@Validated @RequestBody ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception {
        PageEntity<ChannelPromotionVO> paging = channelPromotionService.getPidPageList(channelPromotionPageParam, request);
        return ResultEntity.success(paging);
    }

    /**
     * 渠道推广列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "渠道推广列表", response = ChannelPromotion.class)
    @ApiOperationSupport(ignoreParameters = {"channelPromotionPageParam.pageIndex", "channelPromotionPageParam.pageSorts", "channelPromotionPageParam.pageSize"})
    public ResultEntity<List<ChannelPromotion>> getChannelPromotionList(@Validated @RequestBody ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception {
        List<ChannelPromotion> list = channelPromotionService.getChannelPromotionList(channelPromotionPageParam, request);
        return ResultEntity.success(list);
    }

    @PostMapping("/getList/za")
    @ApiOperation(value = "pid信息查询_ZA", response = ChannelPromotion.class)
    @ApiOperationSupport(ignoreParameters = {"channelPromotionPageParam.pageIndex", "channelPromotionPageParam.pageSorts", "channelPromotionPageParam.pageSize"})
    public ResultEntity<List<ChannelPromotionZaVO>> getChannelPromotionListByZa(@RequestBody ChannelPromotionPageParam promotionBi, SignedParam signedParam) throws Exception {
        List<ChannelPromotionZaVO> list = channelPromotionService.getChannelPromotionListByZa(promotionBi);
        return ResultEntity.success(list);
    }
}

