package com.stnts.bi.datamanagement.module.channel.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelChild;
import com.stnts.bi.datamanagement.module.channel.param.ChannelChildPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelChildService;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelChildVO;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 子渠道 控制器
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Slf4j
@RestController
@RequestMapping("/channelChild")
@Api(value = "子渠道API", tags = {"子渠道"})
public class ChannelChildController {

    @Autowired
    private ChannelChildService channelChildService;

    /**
     * 添加子渠道
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加子渠道", response = ResultEntity.class)
    public ResultEntity<Boolean> addChannelChild(@Validated(Add.class) @RequestBody ChannelChild channelChild) throws Exception {
        if (StringUtils.isBlank(channelChild.getDataSource())) {
            channelChild.setDataSource("BI");
        }
        Map<Object, Object> map = channelChildService.saveChannelChild(channelChild);
        return ResultEntity.success(map.containsKey("subChannelId"));
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

    @GetMapping("/infoExt/{id}")
    @ApiOperation(value = "子渠道详情-拓展 by yf", response = ChannelChildVO.class)
    public ResultEntity<ChannelChild> getChannelChildExt(@PathVariable("id") Long id) throws Exception {
        ChannelChild channelChild = channelChildService.getByIdExt(id);
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

    @PostMapping("getChannelChildListByCcid")
    @ApiOperation("通过ccid获取子渠道列表")
    public ResultEntity<List<ChannelChild>> getChannelChildListByCcid(@NotNull @RequestBody ChannelChildPageParam channelChildPageParam, HttpServletRequest request) throws Exception {
        List<ChannelChild> list = channelChildService.getChannelChildListByCcid(channelChildPageParam, request);
        return ResultEntity.success(list);
    }
}

