package com.stnts.bi.datamanagement.module.channel.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelChild;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.param.ChannelChildPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelChildService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelCooperationService;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelChildVO;
import com.stnts.bi.datamanagement.util.SignUtil;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import com.stnts.signature.annotation.SignedMapping;
import com.stnts.signature.entity.SignedParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 子渠道 控制器
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Slf4j
@RestController
@RequestMapping("/sign/channelChild")
@Api(value = "子渠道API", tags = {"签名-子渠道"})
@SignedMapping
public class ChannelChildSignController {
    @Autowired
    private ChannelChildService channelChildService;
    @Autowired
    private ChannelCooperationService channelCooperationService;
    @Autowired
    private SignUtil signUtil;
    @Autowired
    private EnvironmentProperties environmentProperties;

    /**
     * 添加子渠道
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加子渠道", response = ResultEntity.class)
    @ApiOperationSupport(ignoreParameters = {"channelChild.id", "channelChild.subChannelId"})
    public ResultEntity<Map<Object, Object>> addChannelChild(@Validated(Add.class) @RequestBody ChannelChild channelChild, SignedParam signedParam) throws Exception {
        if (StringUtils.isBlank(channelChild.getDataSource())) {
            channelChild.setDataSource("BI");
        }
        Map<Object, Object> map = channelChildService.saveChannelChild(channelChild);
        return ResultEntity.success(map);
    }

    /**
     * 添加子渠道
     */
    @PostMapping("/addChannelChildGeneral")
    @ApiOperation(value = "添加子渠道_通用", response = ResultEntity.class)
    @ApiOperationSupport(ignoreParameters = {"channelChild.id", "channelChild.subChannelId"})
    public ResultEntity<ChannelChildVO> addChannelChildGeneral(@Validated(Add.class) @RequestBody ChannelChild channelChild, String appId, Long timestamp, String sign) throws Exception {
        log.info("===========================>添加子渠道_通用，请求参数：{}, appId:{}, timestamp:{}, sign:{}", JSON.toJSON(channelChild), appId, timestamp, sign);

        //鉴权
        signUtil.checkSign(appId, timestamp, sign, Dict.create()
                .set("channelId", channelChild.getChannelId())
                .set("subChannelName", channelChild.getSubChannelName())
                .set("dataSource", channelChild.getDataSource())
        );

        ChannelChildVO map = channelChildService.saveChannelChildGeneral(channelChild);
        return ResultEntity.success(map);
    }

    /**
     * 子渠道列表
     */
    @PostMapping("/channelChildListGeneral")
    @ApiOperation(value = "子渠道列表_通用", response = ResultEntity.class)
    public ResultEntity<List<ChannelChildVO>> channelChildListGeneral(@Validated(Add.class) @RequestBody ChannelChildVO channelChildVO, String appId, Long timestamp, String sign) throws Exception {
        log.info("===========================>添加子渠道_通用，请求参数：{}, appId:{}, timestamp:{}, sign:{}", JSON.toJSON(channelChildVO), appId, timestamp, sign);

        if (appId.equals(environmentProperties.getAppId2())) {
            if (StringUtils.isBlank(channelChildVO.getDepartmentCode()) && StringUtils.isBlank(channelChildVO.getDepartmentName())
                    && ObjectUtil.isEmpty(channelChildVO.getChannelId()) && StringUtils.isBlank(channelChildVO.getChannelName())
                    && StringUtils.isBlank(channelChildVO.getSubChannelId()) && StringUtils.isBlank(channelChildVO.getSubChannelName())
                    && ObjectUtil.isEmpty(channelChildVO.getUpdateTimeStart()) && ObjectUtil.isEmpty(channelChildVO.getUpdateTimeEnd())
            ) {
                throw new BusinessException("查询参数必须选择一个以上");
            }
        }
        //鉴权
        signUtil.checkSign(appId, timestamp, sign, Dict.create().set("departmentCode", channelChildVO.getDepartmentCode())
                .set("departmentName", channelChildVO.getDepartmentName())
                .set("channelId", channelChildVO.getChannelId())
                .set("channelName", channelChildVO.getChannelName())
                .set("subChannelId", channelChildVO.getSubChannelId())
                .set("subChannelName", channelChildVO.getSubChannelName())
                .set("updateTimeStart", DateUtil.format(channelChildVO.getUpdateTimeStart(), "yyyy-MM-dd HH:mm:ss"))
                .set("updateTimeEnd", DateUtil.format(channelChildVO.getUpdateTimeEnd(), "yyyy-MM-dd HH:mm:ss"))
        );

        List<ChannelChildVO> map = channelChildService.channelChildListGeneral(channelChildVO);
        return ResultEntity.success(map);
    }

    /**
     * 修改子渠道
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改子渠道", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelChild(@Validated(Update.class) @RequestBody ChannelChild channelChild) throws Exception {
        boolean flag = channelChildService.updateChannelChild(channelChild);
        return ResultEntity.success(flag);
    }

    /**
     * 删除子渠道
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除子渠道", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelChild(@PathVariable("id") Long id) throws Exception {
        boolean flag = channelChildService.deleteChannelChild(id);
        return ResultEntity.success(flag);
    }

    /**
     * 获取子渠道详情
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "子渠道详情", response = ChannelChild.class)
    public ResultEntity<ChannelChild> getChannelChild(@PathVariable("id") Long id) throws Exception {
        ChannelChild channelChild = channelChildService.getById(id);
        return ResultEntity.success(channelChild);
    }

    /**
     * 子渠道分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "子渠道分页列表", response = ChannelChild.class)
    public ResultEntity<PageEntity<ChannelChild>> getChannelChildPageList(@Validated @RequestBody ChannelChildPageParam channelChildPageParam) throws Exception {
        PageEntity<ChannelChild> paging = channelChildService.getChannelChildPageList(channelChildPageParam);
        return ResultEntity.success(paging);
    }

    /**
     * 子渠道列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "子渠道列表", response = ChannelChild.class)
    @ApiOperationSupport(ignoreParameters = {"channelChildPageParam.pageIndex", "channelChildPageParam.pageSorts", "channelChildPageParam.pageSize"})
    public ResultEntity<List<ChannelChild>> getChannelChildList(@Validated @RequestBody ChannelChildPageParam channelChildPageParam) throws Exception {
        List<ChannelChild> list = channelChildService.getChannelChildList(channelChildPageParam);
        return ResultEntity.success(list);
    }

    /**
     * 子渠道列表
     */
    @PostMapping("/getDetailList")
    @ApiOperation(value = "子渠道列表（携带媒体商、渠道信息）", response = ChannelChildVO.class)
    @ApiOperationSupport(ignoreParameters = {"channelChildPageParam.pageIndex", "channelChildPageParam.pageSorts", "channelChildPageParam.pageSize"})
    public ResultEntity<List<ChannelChildVO>> getChannelChildDetailList(@Validated @RequestBody ChannelChildPageParam channelChildPageParam) throws Exception {
        List<ChannelChild> list = channelChildService.getChannelChildList(channelChildPageParam);
        if (CollectionUtils.isEmpty(list)) {
            return ResultEntity.success(Collections.emptyList());
        }
        List<String> ccidList = list.stream().map(ChannelChild::getCcid).collect(Collectors.toList());
        List<ChannelCooperation> channelCooperationList = channelCooperationService.list(new LambdaQueryWrapper<ChannelCooperation>().in(ChannelCooperation::getCcid, ccidList));
        Map<String, ChannelCooperation> ccidToChannelCooperationMap = channelCooperationList.stream().collect(Collectors.toMap(ChannelCooperation::getCcid, Function.identity()));
        List<ChannelChildVO> result = list.stream().map(x -> {
            ChannelChildVO channelChildVO = new ChannelChildVO();
            channelChildVO.setSubChannelId(x.getSubChannelId());
            channelChildVO.setSubChannelName(x.getSubChannelName());
            String ccid = x.getCcid();
            if (StrUtil.isNotEmpty(ccid)) {
                channelChildVO.setCcid(ccid);
                ChannelCooperation channelCooperation = ccidToChannelCooperationMap.get(ccid);
                channelChildVO.setAgentId(channelCooperation.getAgentId());
                channelChildVO.setAgentName(channelCooperation.getAgentName());
                channelChildVO.setChannelId(channelCooperation.getChannelId());
                channelChildVO.setChannelName(channelCooperation.getChannelName());
                channelChildVO.setFirstLevelBusiness(channelCooperation.getFirstLevelBusiness());
                channelChildVO.setSecondLevelBusiness(channelCooperation.getSecondLevelBusiness());
                channelChildVO.setThirdLevelBusiness(channelCooperation.getThirdLevelBusiness());
            }
            return channelChildVO;
        }).collect(Collectors.toList());
        if (channelChildPageParam.getAgentId() != null) {
            result.removeIf(v -> !ObjectUtil.equal(v.getAgentId(), channelChildPageParam.getAgentId()));
        }
        if (channelChildPageParam.getChannelId() != null) {
            result.removeIf(v -> !ObjectUtil.equal(v.getChannelId(), channelChildPageParam.getChannelId()));
        }
        if (channelChildPageParam.getFirstLevelBusiness() != null) {
            result.removeIf(v -> !StrUtil.equals(v.getFirstLevelBusiness(), channelChildPageParam.getFirstLevelBusiness()));
        }
        if (channelChildPageParam.getSecondLevelBusiness() != null) {
            result.removeIf(v -> !StrUtil.equals(v.getSecondLevelBusiness(), channelChildPageParam.getSecondLevelBusiness()));
        }
        if (channelChildPageParam.getThirdLevelBusiness() != null) {
            result.removeIf(v -> !StrUtil.equals(v.getThirdLevelBusiness(), channelChildPageParam.getThirdLevelBusiness()));
        }
        return ResultEntity.success(result);
    }

}

