package com.stnts.bi.datamanagement.module.channel.controller;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelClassCooperation;
import com.stnts.bi.datamanagement.module.channel.service.ChannelClassCooperationService;
import lombok.extern.slf4j.Slf4j;
import com.stnts.bi.datamanagement.module.channel.param.ChannelClassCooperationPageParam;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 渠道类型关联CCID 控制器
 *
 * @author 易樊
 * @since 2021-09-22
 */
/*@Slf4j
@RestController
@RequestMapping("/channelClassCooperation")
@Api(value = "渠道类型关联CCIDAPI", tags = {"渠道类型关联CCID"})*/
public class ChannelClassCooperationController {

    /*@Autowired
    private ChannelClassCooperationService channelClassCooperationService;

    *//**
     * 添加渠道类型关联CCID
     *//*
    @PostMapping("/add")
    @ApiOperation(value = "添加渠道类型关联CCID", response = ResultEntity.class)
    public ResultEntity<Boolean> addChannelClassCooperation(@Validated(Add.class) @RequestBody ChannelClassCooperation channelClassCooperation) throws Exception {
        boolean flag = channelClassCooperationService.saveChannelClassCooperation(channelClassCooperation);
        return ResultEntity.success(flag);
    }

    *//**
     * 修改渠道类型关联CCID
     *//*
    @PostMapping("/update")
    @ApiOperation(value = "修改渠道类型关联CCID", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelClassCooperation(@Validated(Update.class) @RequestBody ChannelClassCooperation channelClassCooperation) throws Exception {
        boolean flag = channelClassCooperationService.updateChannelClassCooperation(channelClassCooperation);
        return ResultEntity.success(flag);
    }

    *//**
     * 修改渠道类型关联CCID
     *//*
    @PostMapping("/updateAssociated")
    @ApiOperation(value = "修改渠道类型关联CCID", response = ResultEntity.class)
    public ResultEntity<Boolean> updateAssociated(@Validated(Update.class) @RequestBody ChannelClassCooperation channelClassCooperation) throws Exception {
        boolean flag = channelClassCooperationService.updateChannelClassCooperation(channelClassCooperation);
        return ResultEntity.success(flag);
    }

    *//**
     * 删除渠道类型关联CCID
     *//*
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除渠道类型关联CCID", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelClassCooperation(@PathVariable("id") Long id) throws Exception {
        boolean flag = channelClassCooperationService.deleteChannelClassCooperation(id);
        return ResultEntity.success(flag);
    }

    *//**
     * 获取渠道类型关联CCID详情
     *//*
    @GetMapping("/info/{id}")
    @ApiOperation(value = "渠道类型关联CCID详情", response = ChannelClassCooperation.class)
    public ResultEntity<ChannelClassCooperation> getChannelClassCooperation(@PathVariable("id") Long id) throws Exception {
        ChannelClassCooperation channelClassCooperation = channelClassCooperationService.getById(id);
        return ResultEntity.success(channelClassCooperation);
    }

    *//**
     * 渠道类型关联CCID分页列表
     *//*
    @PostMapping("/getPageList")
    @ApiOperation(value = "渠道类型关联CCID分页列表", response = ChannelClassCooperation.class)
    public ResultEntity<PageEntity<ChannelClassCooperation>> getChannelClassCooperationPageList(@Validated @RequestBody ChannelClassCooperationPageParam channelClassCooperationPageParam) throws Exception {
        PageEntity<ChannelClassCooperation> paging = channelClassCooperationService.getChannelClassCooperationPageList(channelClassCooperationPageParam);
        return ResultEntity.success(paging);
    }

    *//**
     * 渠道类型关联CCID列表
     *//*
    @PostMapping("/getList")
    @ApiOperation(value = "渠道类型关联CCID列表", response = ChannelClassCooperation.class)
    @ApiOperationSupport(ignoreParameters = {"channelClassCooperationPageParam.pageIndex","channelClassCooperationPageParam.pageSorts","channelClassCooperationPageParam.pageSize"})
    public ResultEntity<List<ChannelClassCooperation>> getChannelClassCooperationList(@Validated @RequestBody ChannelClassCooperationPageParam channelClassCooperationPageParam) throws Exception {
        List<ChannelClassCooperation> list = channelClassCooperationService.getChannelClassCooperationList(channelClassCooperationPageParam);
        return ResultEntity.success(list);
    }*/
}

