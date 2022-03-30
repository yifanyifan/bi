package com.stnts.bi.datamanagement.module.channel.controller;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelApplication;
import com.stnts.bi.datamanagement.module.channel.service.ChannelApplicationService;
import lombok.extern.slf4j.Slf4j;
import com.stnts.bi.datamanagement.module.channel.param.ChannelApplicationPageParam;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 应用表 控制器
 *
 * @author 易樊
 * @since 2022-01-13
 */
@Slf4j
@RestController
@RequestMapping("/channelApplication")
@Api(value = "应用表API", tags = {"应用表"})
public class ChannelApplicationController {

    @Autowired
    private ChannelApplicationService channelApplicationService;

    /**
     * 添加应用表
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加应用表", response = ResultEntity.class)
    public ResultEntity<Boolean> addChannelApplication(@Validated(Add.class) @RequestBody ChannelApplication channelApplication, HttpServletRequest request) throws Exception {
        boolean flag = channelApplicationService.saveChannelApplication(channelApplication, request);
        return ResultEntity.success(flag);
    }

    /**
     * 修改应用表
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改应用表", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelApplication(@Validated(Update.class) @RequestBody ChannelApplication channelApplication) throws Exception {
        boolean flag = channelApplicationService.updateChannelApplication(channelApplication);
        return ResultEntity.success(flag);
    }

    /**
     * 删除应用表
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除应用表", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelApplication(@PathVariable("id") Long id) throws Exception {
        boolean flag = channelApplicationService.deleteChannelApplication(id);
        return ResultEntity.success(flag);
    }

    /**
     * 获取应用表详情
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "应用表详情", response = ChannelApplication.class)
    public ResultEntity<ChannelApplication> getChannelApplication(@PathVariable("id") Long id) throws Exception {
        ChannelApplication channelApplication = channelApplicationService.getById(id);
        return ResultEntity.success(channelApplication);
    }

    /**
     * 应用表分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "应用表分页列表", response = ChannelApplication.class)
    public ResultEntity<PageEntity<ChannelApplication>> getChannelApplicationPageList(@Validated @RequestBody ChannelApplicationPageParam channelApplicationPageParam) throws Exception {
        PageEntity<ChannelApplication> paging = channelApplicationService.getChannelApplicationPageList(channelApplicationPageParam);
        return ResultEntity.success(paging);
    }

    /**
     * 应用表列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "应用表列表", response = ChannelApplication.class)
    @ApiOperationSupport(ignoreParameters = {"channelApplicationPageParam.pageIndex","channelApplicationPageParam.pageSorts","channelApplicationPageParam.pageSize"})
    public ResultEntity<List<ChannelApplication>> getChannelApplicationList(@Validated @RequestBody ChannelApplicationPageParam channelApplicationPageParam) throws Exception {
        List<ChannelApplication> list = channelApplicationService.getChannelApplicationList(channelApplicationPageParam);
        return ResultEntity.success(list);
    }

}

