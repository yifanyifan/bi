package com.stnts.bi.datamanagement.module.channel.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotionPosition;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionPositionPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionPositionService;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推广位推广位
 */
@Slf4j
@RestController
@RequestMapping("/channelPromotionPosition")
@Api(value = "推广位API", tags = {"推广位"})
public class ChannelPromotionPositionController {

    @Autowired
    private ChannelPromotionPositionService positionService;



    /**
     * 添加推广位
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加推广位", response = ResultEntity.class)
    public ResultEntity<Boolean> addChannelPromotionPosition(@Validated(Add.class) @RequestBody ChannelPromotionPosition channel) throws Exception {
        ChannelPromotionPosition flag = positionService.addChannelPromotionPosition(channel);
        return ResultEntity.success(true);
    }

    @PostMapping("/saveBatch")
    @ApiOperation(value = "批量添加推广位", response = ResultEntity.class)
    public ResultEntity<Boolean> addChannelPromotionPositionBatch(@Validated(Add.class) @RequestBody List<ChannelPromotionPosition> channelPromotionPositionList) throws Exception {
        boolean flag = positionService.addChannelPromotionPositionBatch(channelPromotionPositionList);
        return ResultEntity.success(flag);
    }

    /**
     * 修改推广位
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改推广位", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelPromotionPosition(@Validated(Update.class) @RequestBody ChannelPromotionPosition channel) throws Exception {
        boolean flag = positionService.updateChannelPromotionPosition(channel);
        return ResultEntity.success(flag);
    }

    @PostMapping("/updateBatch")
    @ApiOperation(value = "批量修改推广位", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelPromotionPositionBatch(@Validated(Update.class) @RequestBody List<ChannelPromotionPosition> channel) throws Exception {
        boolean flag = positionService.updateChannelPromotionPositionBatch(channel);
        return ResultEntity.success(flag);
    }

    /**
     * 删除推广位
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除推广位", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelPromotionPosition(@PathVariable("id") Long id) throws Exception {
        boolean flag = positionService.deleteChannelPromotionPosition(id);
        return ResultEntity.success(flag);
    }

    /**
     * 获取推广位详情
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "推广位详情", response = ChannelPromotionPosition.class)
    public ResultEntity<ChannelPromotionPosition> getChannelPromotionPosition(@PathVariable("id") Long id) throws Exception {
        ChannelPromotionPosition channel = positionService.getById(id);
        return ResultEntity.success(channel);
    }

    /**
     * 推广位分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "推广位分页列表")
    public ResultEntity<PageEntity<ChannelPromotionPosition>> getChannelPromotionPositionPageList(@Validated @RequestBody ChannelPromotionPositionPageParam channelPageParam) throws Exception {
        PageEntity<ChannelPromotionPosition> paging = positionService.getChannelPromotionPositionPageList(channelPageParam);
        return ResultEntity.success(paging);
    }

    /**
     * 推广位列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "推广位列表")
    @ApiOperationSupport(ignoreParameters = {"channelPageParam.pageIndex","channelPageParam.pageSorts","channelPageParam.pageSize"})
    public ResultEntity<List<ChannelPromotionPosition>> getChannelPromotionPositionList(@Validated @RequestBody ChannelPromotionPositionPageParam channelPageParam) throws Exception {
        List<ChannelPromotionPosition> list = positionService.getChannelPromotionPositionList(channelPageParam);
        return ResultEntity.success(list);
    }

    @PostMapping("switch/status")
    @ApiOperation("切换状态")
    @ApiOperationSupport(ignoreParameters = {"ppName", "channelId", "createTime", "updateTime"})
    public ResultEntity<Boolean> switchStatus(ChannelPromotionPosition channelPromotionPosition){

        ChannelPromotionPosition pp = positionService.getById(channelPromotionPosition.getPpId());
        pp.setPpStatus(pp.getPpStatus());
        positionService.save(pp);
        return ResultEntity.success(true);
    }
}

