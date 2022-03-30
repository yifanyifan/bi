package com.stnts.bi.datamanagement.module.channel.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.business.service.BusinessDictService;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.param.ChannelCooperationPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelCooperationService;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelCooperationGeneralVO;
import com.stnts.bi.datamanagement.module.channel.vo.GetAgentVO;
import com.stnts.bi.datamanagement.module.channel.vo.GetChannelVO;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiService;
import com.stnts.bi.datamanagement.module.exportdata.service.ExportDataService;
import com.stnts.bi.datamanagement.module.feign.SysClient;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.datamanagement.util.SignUtil;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import com.stnts.signature.annotation.SignedMapping;
import com.stnts.signature.entity.SignedParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 渠道合作 控制器
 *
 * @author 刘天元
 * @since 2021-02-03
 */
@Slf4j
@RestController
@RequestMapping("/sign/channelCooperation")
@Api(value = "渠道合作API", tags = {"签名-渠道合作"})
@SignedMapping
public class ChannelCooperationSignController {
    @Autowired
    private ChannelCooperationService channelCooperationService;
    @Autowired
    private CooperationBiService cooperationBiService;
    @Autowired
    private SignUtil signUtil;
    @Autowired
    private SysClient sysClient;
    @Autowired
    private ExportDataService exportDataService;
    @Autowired
    private BusinessDictService businessDictService;
    @Autowired
    private EnvironmentProperties environmentProperties;
    ;

    /**
     * 添加渠道合作
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加渠道合作", response = ResultEntity.class, notes = "签名算法如下： signature=MD5(appId=APP_ID&nonce=123&timestamp=1612325632077APP_SECRET)")
    @ApiOperationSupport(ignoreParameters = {"channelCooperation.id", "channelCooperation.ccid", "channelCooperation.updateTime"})
    public ResultEntity<Map<Object, Object>> addChannelCooperation(@Validated(Add.class) @RequestBody ChannelCooperation channelCooperation, SignedParam signedParam, HttpServletRequest request) throws Exception {
        String ccid = channelCooperationService.saveChannelCooperation(channelCooperation, request);
        Map<Object, Object> map = MapUtil.builder().put("ccid", ccid).build();
        return ResultEntity.success(map);
    }

    @PostMapping("/addChannelCooperationGeneral")
    @ApiOperation(value = "添加渠道合作_通用")
    public ResultEntity<ChannelCooperation> addChannelCooperationGeneral(@RequestBody ChannelCooperation channelCooperation, String appId, Long timestamp, String sign) throws Exception {
        log.info("===========================>新境CCID_通用，请求参数：{}, appId:{}, timestamp:{}, sign:{}", JSON.toJSON(channelCooperation), appId, timestamp, sign);

        //鉴权
        signUtil.checkSign(appId, timestamp, sign, Dict.create()
                .set("departmentCode", channelCooperation.getDepartmentCode())
                .set("departmentName", channelCooperation.getDepartmentName())
                .set("businessDictId", channelCooperation.getBusinessDictId())
                .set("firstLevelBusiness", channelCooperation.getFirstLevelBusiness())
                .set("secondLevelBusiness", channelCooperation.getSecondLevelBusiness())
                .set("thirdLevelBusiness", channelCooperation.getThirdLevelBusiness())
                .set("channelId", channelCooperation.getChannelId())
                .set("channelName", channelCooperation.getChannelName())
                .set("agentId", channelCooperation.getAgentId())
                .set("channelClass", channelCooperation.getChannelType())
                .set("secretType", channelCooperation.getSecretType())
                .set("settlementType", channelCooperation.getSettlementType())
                .set("chargeRule", channelCooperation.getChargeRule())
                .set("channelShareType", channelCooperation.getChannelShareType())
                .set("price", channelCooperation.getPrice())
                .set("channelShare", channelCooperation.getChannelShare())
                .set("channelShareStep", channelCooperation.getChannelShareStep())
                .set("channelRate", channelCooperation.getChannelRate())
                .set("userid", channelCooperation.getUserid())
                .set("dataSource", channelCooperation.getDataSource())
        );

        Long userid = channelCooperation.getUserid(); // 临时保存
        ChannelCooperation channelCooperationDB = channelCooperationService.saveChannelCooperationGeneral(channelCooperation);
        channelCooperationDB.setUserid(userid);

        return ResultEntity.success(channelCooperationDB);
    }

    @PostMapping("/getChannelCooperationGeneral")
    @ApiOperation(value = "查询渠道合作_通用")
    public ResultEntity<List<ChannelCooperationGeneralVO>> getChannelCooperationGeneral(@RequestBody ChannelCooperation channelCooperation, String appId, Long timestamp, String sign) throws Exception {
        log.info("===========================>查询CCID列表_通用，请求参数：{}, appId:{}, timestamp:{}, sign:{}", JSON.toJSON(channelCooperation), appId, timestamp, sign);

        if (appId.equals(environmentProperties.getAppId2())) {
            if (StringUtils.isBlank(channelCooperation.getDepartmentCode()) && StringUtils.isBlank(channelCooperation.getDepartmentName())
                    && StringUtils.isBlank(channelCooperation.getChargeRule())
                    && StringUtils.isBlank(channelCooperation.getFirstLevelBusiness()) && StringUtils.isBlank(channelCooperation.getSecondLevelBusiness())
                    && StringUtils.isBlank(channelCooperation.getThirdLevelBusiness())
                    && ObjectUtil.isEmpty(channelCooperation.getBusinessDictId())
                    && ObjectUtil.isEmpty(channelCooperation.getChannelId()) && StringUtils.isBlank(channelCooperation.getChannelName())
                    && ObjectUtil.isEmpty(channelCooperation.getAgentId()) && StringUtils.isBlank(channelCooperation.getAgentName())
                    && StringUtils.isBlank(channelCooperation.getSettlementType()) && StringUtils.isBlank(channelCooperation.getCcid())
            ) {
                throw new BusinessException("查询参数必须选择一个以上");
            }
        }

        //鉴权
        signUtil.checkSign(appId, timestamp, sign, Dict.create()
                .set("departmentCode", channelCooperation.getDepartmentCode())
                .set("departmentName", channelCooperation.getDepartmentName())
                .set("chargeRule", channelCooperation.getChargeRule())
                .set("businessDictId", channelCooperation.getBusinessDictId())
                .set("firstLevelBusiness", channelCooperation.getFirstLevelBusiness())
                .set("secondLevelBusiness", channelCooperation.getSecondLevelBusiness())
                .set("thirdLevelBusiness", channelCooperation.getThirdLevelBusiness())
                .set("channelId", channelCooperation.getChannelId())
                .set("channelName", channelCooperation.getChannelName())
                .set("agentId", channelCooperation.getAgentId())
                .set("agentName", channelCooperation.getAgentName())
                .set("settlementType", channelCooperation.getSettlementType())
                .set("ccid", channelCooperation.getCcid())
        );

        List<ChannelCooperation> channelCooperationList = channelCooperationService.getChannelCooperationGeneral(channelCooperation);

        List<ChannelCooperationGeneralVO> channelCooperationGeneralVOList = DozerUtil.toBeanList(channelCooperationList, ChannelCooperationGeneralVO.class);

        return ResultEntity.success(channelCooperationGeneralVOList);
    }

    /**
     * 修改渠道合作
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改渠道合作", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelCooperation(@Validated(Update.class) @RequestBody ChannelCooperation channelCooperation, SignedParam signedParam, HttpServletRequest request) throws Exception {
        boolean flag = channelCooperationService.updateChannelCooperation(channelCooperation, request);
        return ResultEntity.success(flag);
    }

    /**
     * 删除渠道合作
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除渠道合作", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelCooperation(@PathVariable("id") Long id, SignedParam signedParam) throws Exception {
        boolean flag = channelCooperationService.deleteChannelCooperation(id);
        return ResultEntity.success(flag);
    }

    /**
     * 获取渠道合作详情
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "渠道合作详情", response = ChannelCooperation.class)
    public ResultEntity<ChannelCooperation> getChannelCooperation(@PathVariable("id") Long id, SignedParam signedParam) throws Exception {
        ChannelCooperation channelCooperation = channelCooperationService.getById(id);
        return ResultEntity.success(channelCooperation);
    }

    /**
     * 渠道合作分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "渠道合作分页列表", response = ChannelCooperation.class)
    public ResultEntity<PageEntity<ChannelCooperation>> getChannelCooperationPageList(@Validated @RequestBody ChannelCooperationPageParam channelCooperationPageParam, SignedParam signedParam, HttpServletRequest request) throws Exception {
        PageEntity<ChannelCooperation> paging = channelCooperationService.getChannelCooperationPageList(channelCooperationPageParam, request);
        return ResultEntity.success(paging);
    }

    /**
     * 渠道合作列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "渠道合作列表", response = ChannelCooperation.class)
    @ApiOperationSupport(ignoreParameters = {"channelCooperationPageParam.pageIndex", "channelCooperationPageParam.pageSorts", "channelCooperationPageParam.pageSize"})
    public ResultEntity<List<ChannelCooperation>> getChannelCooperationList(@Validated @RequestBody ChannelCooperationPageParam channelCooperationPageParam, SignedParam signedParam, HttpServletRequest request) throws Exception {
        List<ChannelCooperation> list = channelCooperationService.getChannelCooperationList(channelCooperationPageParam, request);
        return ResultEntity.success(list);
    }


    /**
     * 渠道管理-媒体商主列表
     */
    @PostMapping("/getAgentPageList")
    @ApiOperation(value = "供应商列表", response = GetAgentVO.class)
    public ResultEntity<PageEntity<GetAgentVO>> getAgentPageList(@Validated @RequestBody ChannelCooperationPageParam channelCooperationPageParam) throws Exception {
        PageEntity<GetAgentVO> paging = channelCooperationService.getAgentPageList(channelCooperationPageParam);
        return ResultEntity.success(paging);
    }

    /**
     * 渠道管理-媒体商主列表-渠道列表
     */
    @GetMapping("/getChannelList/{agentId}")
    @ApiOperation(value = "渠道列表", response = GetChannelVO.class)
    public ResultEntity<List<GetChannelVO>> getChannelList(@ApiParam("供应商id") @PathVariable("agentId") Long agentId,
                                                           @ApiParam("部门code") @RequestParam(required = false) String departmentCode, SignedParam signedParam) throws Exception {
        List<GetChannelVO> result = channelCooperationService.getChannelList(agentId, departmentCode);
        return ResultEntity.success(result);
    }

    /**
     * 渠道合作分页列表
     */
    @GetMapping("/getDepartmentList")
    @ApiOperation(value = "部门列表", response = ChannelCooperation.class)
    public ResultEntity departmentList(SignedParam signedParam, HttpServletRequest request) throws Exception {
        List list = cooperationBiService.departmentList(request);
        return ResultEntity.success(list);
    }

}

