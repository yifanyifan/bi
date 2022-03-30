package com.stnts.bi.datamanagement.module.channel.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelMedium;
import com.stnts.bi.datamanagement.module.channel.param.ChannelMediumPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelMediumService;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import com.stnts.signature.annotation.SignedMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 媒介信息 控制器
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Slf4j
@RestController
@RequestMapping("/sign/channelMedium")
@Api(value = "媒介信息API", tags = {"签名-媒介信息"})
@SignedMapping
public class ChannelMediumSignController {

    @Autowired
    private ChannelMediumService channelMediumService;

    /**
     * 添加媒介信息
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加媒介信息", response = ResultEntity.class)
    public ResultEntity<Boolean> addChannelMedium(@Validated(Add.class) @RequestBody ChannelMedium channelMedium) throws Exception {
        channelMediumService.saveChannelMedium(channelMedium);
        return ResultEntity.success(true);
    }

    /**
     * 修改媒介信息
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改媒介信息", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelMedium(@Validated(Update.class) @RequestBody ChannelMedium channelMedium) throws Exception {
        boolean flag = channelMediumService.updateChannelMedium(channelMedium);
        return ResultEntity.success(flag);
    }

    /**
     * 删除媒介信息
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除媒介信息", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelMedium(@PathVariable("id") Long id) throws Exception {
        boolean flag = channelMediumService.deleteChannelMedium(id);
        return ResultEntity.success(flag);
    }

    /**
     * 获取媒介信息详情
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "媒介信息详情", response = ChannelMedium.class)
    public ResultEntity<ChannelMedium> getChannelMedium(@PathVariable("id") Long id) throws Exception {
        ChannelMedium channelMedium = channelMediumService.getById(id);
        return ResultEntity.success(channelMedium);
    }

    /**
     * 媒介信息分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "媒介信息分页列表", response = ChannelMedium.class)
    public ResultEntity<PageEntity<ChannelMedium>> getChannelMediumPageList(@Validated @RequestBody ChannelMediumPageParam channelMediumPageParam,
                                                                            HttpServletRequest request) throws Exception {
        PageEntity<ChannelMedium> paging = channelMediumService.getChannelMediumPageList(channelMediumPageParam, request);
        return ResultEntity.success(paging);
    }

    /**
     * 媒介信息列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "媒介信息列表", response = ChannelMedium.class)
    @ApiOperationSupport(ignoreParameters = {"channelMediumPageParam.pageIndex","channelMediumPageParam.pageSorts","channelMediumPageParam.pageSize"})
    public ResultEntity<List<ChannelMedium>> getChannelMediumList(@Validated @RequestBody ChannelMediumPageParam channelMediumPageParam, HttpServletRequest request) throws Exception {
        List<ChannelMedium> list = channelMediumService.getChannelMediumList(channelMediumPageParam, request);
        return ResultEntity.success(list);
    }

}

