package com.stnts.bi.datamanagement.module.channel.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductLabel;
import com.stnts.bi.datamanagement.module.channel.enums.LabelLevelEnum;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductLabelPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductLabelService;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 产品标签 控制器
 *
 * @author 易樊
 * @since 2022-01-26
 */
@Slf4j
@RestController
@RequestMapping("/channelProductLabel")
@Api(value = "产品标签API", tags = {"产品标签"})
public class ChannelProductLabelController {

    @Autowired
    private ChannelProductLabelService channelProductLabelService;

    /**
     * 获取标签域
     */
    @GetMapping("/getLabelAreaList")
    @ApiOperation(value = "获取标签域")
    public ResultEntity<List<String>> getLabelAreaList() throws Exception {
        List<String> areaList = channelProductLabelService.list().stream().map(ChannelProductLabel::getLabelArea).distinct().collect(Collectors.toList());
        return ResultEntity.success(areaList);
    }

    /**
     * 获取标签域
     */
    @GetMapping("/getLabelLevelList")
    @ApiOperation(value = "获取标签层级")
    public ResultEntity<List> getLabelLevelList() throws Exception {
        List result = LabelLevelEnum.getMap();
        return ResultEntity.success(result);
    }

    /**
     * 添加产品标签
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加产品标签", response = ResultEntity.class)
    public ResultEntity<Boolean> addChannelProductLabel(@Validated(Add.class) @RequestBody ChannelProductLabel channelProductLabel, HttpServletRequest request) throws Exception {
        boolean flag = channelProductLabelService.saveChannelProductLabel(channelProductLabel, request);
        return ResultEntity.success(flag);
    }

    /**
     * 修改产品标签
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改产品标签", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelProductLabel(@Validated(Update.class) @RequestBody ChannelProductLabel channelProductLabel) throws Exception {
        boolean flag = channelProductLabelService.updateChannelProductLabel(channelProductLabel);
        return ResultEntity.success(flag);
    }

    /**
     * 删除产品标签
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除产品标签", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelProductLabel(@PathVariable("id") Long id) throws Exception {
        boolean flag = channelProductLabelService.deleteChannelProductLabel(id);
        return ResultEntity.success(flag);
    }

    /**
     * 获取产品标签详情
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "产品标签详情", response = ChannelProductLabel.class)
    public ResultEntity<ChannelProductLabel> getChannelProductLabel(@PathVariable("id") Long id) throws Exception {
        ChannelProductLabel channelProductLabel = channelProductLabelService.getById(id);
        channelProductLabel.setLabelLevelStr(LabelLevelEnum.getByKey(channelProductLabel.getLabelLevel()).getValue());
        return ResultEntity.success(channelProductLabel);
    }

    /**
     * 产品标签分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "产品标签分页列表", response = ChannelProductLabel.class)
    public ResultEntity<PageEntity<ChannelProductLabel>> getChannelProductLabelPageList(@Validated @RequestBody ChannelProductLabelPageParam channelProductLabelPageParam) throws Exception {
        PageEntity<ChannelProductLabel> paging = channelProductLabelService.getChannelProductLabelPageList(channelProductLabelPageParam);
        return ResultEntity.success(paging);
    }

    /**
     * 产品标签列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "产品标签列表", response = ChannelProductLabel.class)
    @ApiOperationSupport(ignoreParameters = {"channelProductLabelPageParam.pageIndex", "channelProductLabelPageParam.pageSorts", "channelProductLabelPageParam.pageSize"})
    public ResultEntity<List<ChannelProductLabel>> getChannelProductLabelList(@Validated @RequestBody ChannelProductLabelPageParam channelProductLabelPageParam) throws Exception {
        List<ChannelProductLabel> list = channelProductLabelService.getChannelProductLabelList(channelProductLabelPageParam);
        return ResultEntity.success(list);
    }

    /**
     * 产品标签列表
     */
    @GetMapping("/getTreeAll")
    @ApiOperation(value = "产品标签树")
    public ResultEntity<Map> getTreeAll() throws Exception {
        Map map = channelProductLabelService.getTreeAll();
        return ResultEntity.success(map);
    }

}

