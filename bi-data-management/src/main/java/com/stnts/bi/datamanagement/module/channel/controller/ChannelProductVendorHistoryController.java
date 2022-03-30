package com.stnts.bi.datamanagement.module.channel.controller;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductVendorHistory;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductVendorHistoryService;
import lombok.extern.slf4j.Slf4j;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductVendorHistoryPageParam;
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
 * 产品历史CP厂商记录 控制器
 *
 * @author 易樊
 * @since 2021-09-28
 */
@Slf4j
@RestController
@RequestMapping("/channelProductVendorHistory")
@Api(value = "产品历史CP厂商记录API", tags = {"产品历史CP厂商记录"})
public class ChannelProductVendorHistoryController {

    @Autowired
    private ChannelProductVendorHistoryService channelProductVendorHistoryService;

    /**
     * 添加产品历史CP厂商记录
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加产品历史CP厂商记录", response = ResultEntity.class)
    public ResultEntity<Boolean> addChannelProductVendorHistory(@Validated(Add.class) @RequestBody ChannelProductVendorHistory channelProductVendorHistory) throws Exception {
        boolean flag = channelProductVendorHistoryService.saveChannelProductVendorHistory(channelProductVendorHistory);
        return ResultEntity.success(flag);
    }

    /**
     * 修改产品历史CP厂商记录
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改产品历史CP厂商记录", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelProductVendorHistory(@Validated(Update.class) @RequestBody ChannelProductVendorHistory channelProductVendorHistory) throws Exception {
        boolean flag = channelProductVendorHistoryService.updateChannelProductVendorHistory(channelProductVendorHistory);
        return ResultEntity.success(flag);
    }

    /**
     * 删除产品历史CP厂商记录
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除产品历史CP厂商记录", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelProductVendorHistory(@PathVariable("id") Long id) throws Exception {
        boolean flag = channelProductVendorHistoryService.deleteChannelProductVendorHistory(id);
        return ResultEntity.success(flag);
    }

    /**
     * 获取产品历史CP厂商记录详情
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "产品历史CP厂商记录详情", response = ChannelProductVendorHistory.class)
    public ResultEntity<ChannelProductVendorHistory> getChannelProductVendorHistory(@PathVariable("id") Long id) throws Exception {
        ChannelProductVendorHistory channelProductVendorHistory = channelProductVendorHistoryService.getById(id);
        return ResultEntity.success(channelProductVendorHistory);
    }

    /**
     * 产品历史CP厂商记录分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "产品历史CP厂商记录分页列表", response = ChannelProductVendorHistory.class)
    public ResultEntity<PageEntity<ChannelProductVendorHistory>> getChannelProductVendorHistoryPageList(@Validated @RequestBody ChannelProductVendorHistoryPageParam channelProductVendorHistoryPageParam) throws Exception {
        PageEntity<ChannelProductVendorHistory> paging = channelProductVendorHistoryService.getChannelProductVendorHistoryPageList(channelProductVendorHistoryPageParam);
        return ResultEntity.success(paging);
    }

    /**
     * 产品历史CP厂商记录列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "产品历史CP厂商记录列表", response = ChannelProductVendorHistory.class)
    @ApiOperationSupport(ignoreParameters = {"channelProductVendorHistoryPageParam.keyword","channelProductVendorHistoryPageParam.orders","channelProductVendorHistoryPageParam.pageIndex","channelProductVendorHistoryPageParam.pageSorts","channelProductVendorHistoryPageParam.pageSize"})
    public ResultEntity<List<ChannelProductVendorHistory>> getChannelProductVendorHistoryList(@Validated @RequestBody ChannelProductVendorHistoryPageParam channelProductVendorHistoryPageParam) throws Exception {
        List<ChannelProductVendorHistory> list = channelProductVendorHistoryService.getChannelProductVendorHistoryList(channelProductVendorHistoryPageParam);
        return ResultEntity.success(list);
    }

}

