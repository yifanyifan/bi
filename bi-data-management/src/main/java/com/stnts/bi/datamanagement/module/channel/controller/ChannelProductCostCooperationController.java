package com.stnts.bi.datamanagement.module.channel.controller;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductCostCooperation;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductCostCooperationService;
import lombok.extern.slf4j.Slf4j;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductCostCooperationPageParam;
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
 * 产品分成关联CCID 控制器
 *
 * @author 易樊
 * @since 2021-09-24
 */
@Slf4j
@RestController
@RequestMapping("/channelProductCostCooperation")
@Api(value = "产品分成关联CCIDAPI", tags = {"产品分成关联CCID"})
public class ChannelProductCostCooperationController {

    @Autowired
    private ChannelProductCostCooperationService channelProductCostCooperationService;

    /**
     * 添加产品分成关联CCID
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加产品分成关联CCID", response = ResultEntity.class)
    public ResultEntity<Boolean> addChannelProductCostCooperation(@Validated(Add.class) @RequestBody ChannelProductCostCooperation channelProductCostCooperation) throws Exception {
        boolean flag = channelProductCostCooperationService.saveChannelProductCostCooperation(channelProductCostCooperation);
        return ResultEntity.success(flag);
    }

    /**
     * 修改产品分成关联CCID
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改产品分成关联CCID", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelProductCostCooperation(@Validated(Update.class) @RequestBody ChannelProductCostCooperation channelProductCostCooperation) throws Exception {
        boolean flag = channelProductCostCooperationService.updateChannelProductCostCooperation(channelProductCostCooperation);
        return ResultEntity.success(flag);
    }

    /**
     * 删除产品分成关联CCID
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除产品分成关联CCID", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelProductCostCooperation(@PathVariable("id") Long id) throws Exception {
        boolean flag = channelProductCostCooperationService.deleteChannelProductCostCooperation(id);
        return ResultEntity.success(flag);
    }

    /**
     * 获取产品分成关联CCID详情
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "产品分成关联CCID详情", response = ChannelProductCostCooperation.class)
    public ResultEntity<ChannelProductCostCooperation> getChannelProductCostCooperation(@PathVariable("id") Long id) throws Exception {
        ChannelProductCostCooperation channelProductCostCooperation = channelProductCostCooperationService.getById(id);
        return ResultEntity.success(channelProductCostCooperation);
    }

    /**
     * 产品分成关联CCID分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "产品分成关联CCID分页列表", response = ChannelProductCostCooperation.class)
    public ResultEntity<PageEntity<ChannelProductCostCooperation>> getChannelProductCostCooperationPageList(@Validated @RequestBody ChannelProductCostCooperationPageParam channelProductCostCooperationPageParam) throws Exception {
        PageEntity<ChannelProductCostCooperation> paging = channelProductCostCooperationService.getChannelProductCostCooperationPageList(channelProductCostCooperationPageParam);
        return ResultEntity.success(paging);
    }

    /**
     * 产品分成关联CCID列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "产品分成关联CCID列表", response = ChannelProductCostCooperation.class)
    @ApiOperationSupport(ignoreParameters = {"channelProductCostCooperationPageParam.pageIndex","channelProductCostCooperationPageParam.pageSorts","channelProductCostCooperationPageParam.pageSize"})
    public ResultEntity<List<ChannelProductCostCooperation>> getChannelProductCostCooperationList(@Validated @RequestBody ChannelProductCostCooperationPageParam channelProductCostCooperationPageParam) throws Exception {
        List<ChannelProductCostCooperation> list = channelProductCostCooperationService.getChannelProductCostCooperationList(channelProductCostCooperationPageParam);
        return ResultEntity.success(list);
    }

}

