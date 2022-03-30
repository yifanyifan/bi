package com.stnts.bi.datamanagement.module.channel.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelClass;
import com.stnts.bi.datamanagement.module.channel.param.ChannelClassPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelClassService;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassNode;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassVO;
import com.stnts.bi.entity.common.PageEntity;
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

/**
 * 渠道类型 控制器
 *
 * @author 易樊
 * @since 2021-09-22
 */
@Slf4j
@RestController
@RequestMapping("/channelClass")
@Api(value = "渠道类型API", tags = {"渠道类型"})
public class ChannelClassController {

    @Autowired
    private ChannelClassService channelClassService;

    @GetMapping("/search/all")
    @ApiOperation(value = "查询-搜索框全部集合 By Yf")
    @ApiOperationSupport(ignoreParameters = {"code", "nodeType", "name", "channelClassIdNum", "departmentCodeAllList", "id", "keyword", "orders", "parentId", "productCode", "selectByCCID", "selectByChannel", "selectByChargeRule", "param.pageIndex", "param.pageSorts", "param.pageSize"})
    public ResultEntity searchAll(ChannelClassPageParam param, HttpServletRequest request) {
        Map<String, Object> mapAll = channelClassService.searchAll(param, request);

        return ResultEntity.success(mapAll);
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加渠道类型", response = ResultEntity.class)
    @ApiOperationSupport(ignoreParameters = {"channelClass.ccid", "channelClass.channelClassIdPath", "channelClass.channelClassPath", "channelClass.channelId", "channelClass.channelName", "channelClass.chargeRule", "channelClass.createTime", "channelClass.departmentCode", "channelClass.departmentName", "channelClass.id", "channelClass.nodePath", "channelClass.pidNum", "channelClass.remark", "channelClass.selectByCCID", "channelClass.selectByCCIDOther", "channelClass.selectByChannel", "channelClass.selectByChannelOther", "channelClass.selectByChargeRule", "channelClass.selectByChargeRuleOther", "channelClass.selectOK", "channelClass.treeByChannel", "channelClass.treeByChargeRule", "channelClass.updateTime", "channelClass.userid", "channelClass.username"})
    public ResultEntity<Boolean> addChannelClass(@RequestBody ChannelClass channelClass, HttpServletRequest request) throws Exception {
        boolean flag = channelClassService.saveChannelClass(channelClass, request);
        return ResultEntity.success(flag);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改渠道类型-树拖拽", response = ResultEntity.class)
    @ApiOperationSupport(ignoreParameters = {"channelClass.ccid", "channelClass.channelClassIdPath", "channelClass.channelClassPath", "channelClass.channelId", "channelClass.channelName", "channelClass.chargeRule", "channelClass.createTime", "channelClass.departmentCode", "channelClass.departmentName", "channelClass.nodeType", "channelClass.nodePath", "channelClass.pidNum", "channelClass.remark", "channelClass.selectByCCID", "channelClass.selectByCCIDOther", "channelClass.selectByChannel", "channelClass.selectByChannelOther", "channelClass.selectByChargeRule", "channelClass.selectByChargeRuleOther", "channelClass.selectOK", "channelClass.treeByChannel", "channelClass.treeByChargeRule", "channelClass.updateTime", "channelClass.userid", "channelClass.username"})
    public ResultEntity<Boolean> updateChannelClass(@Validated(Update.class) @RequestBody ChannelClass channelClass, HttpServletRequest request) throws Exception {
        boolean flag = channelClassService.updateChannelClass(channelClass, request);
        return ResultEntity.success(flag);
    }

    @PostMapping("/updateAssociated")
    @ApiOperation(value = "修改渠道类型配置【关联CCID页面】", response = ResultEntity.class)
    @ApiOperationSupport(ignoreParameters = {"channelClassVO.ccid","channelClassVO.channelClassIdPath","channelClassVO.channelClassPath","channelClassVO.channelId","channelClassVO.channelName","channelClassVO.chargeRule","channelClassVO.code","channelClassVO.createTime","channelClassVO.departmentCode","channelClassVO.departmentName","channelClassVO.name","channelClassVO.nodePath","channelClassVO.nodeType","channelClassVO.parentId","channelClassVO.pidNum","channelClassVO.remark","channelClassVO.selectByCCID","channelClassVO.selectByCCIDOther","channelClassVO.selectByChannel","channelClassVO.selectByChannelOther","channelClassVO.selectByChargeRule","channelClassVO.selectByChargeRuleOther","channelClassVO.treeByChannel","channelClassVO.treeByChargeRule","channelClassVO.updateTime","channelClassVO.userid","channelClassVO.username"})
    public ResultEntity<Boolean> updateAssociated(@Validated(Update.class) @RequestBody ChannelClassVO channelClassVO) throws Exception {
        boolean flag = channelClassService.updateAssociated(channelClassVO);
        return ResultEntity.success(flag);
    }

    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除渠道类型", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelClass(@PathVariable("id") Long id) throws Exception {
        boolean flag = channelClassService.deleteChannelClass(id);
        return ResultEntity.success(flag);
    }

    @GetMapping("/info/{id}")
    @ApiOperation(value = "渠道类型详情", response = ChannelClass.class)
    public ResultEntity<ChannelClassVO> getChannelClass(@PathVariable("id") Long id) throws Exception {
        ChannelClassVO channelClass = channelClassService.getChannelClass(id);
        return ResultEntity.success(channelClass);
    }

    @PostMapping("/getPageList")
    @ApiOperation(value = "渠道类型分页列表", response = ChannelClassVO.class)
    @ApiOperationSupport(ignoreParameters = {"code", "nodeType", "name", "departmentCodeAllList", "id", "parentId", "productCode", "selectByCCID", "selectByChannel", "selectByChargeRule"})
    public ResultEntity<PageEntity<ChannelClassVO>> getChannelClassPageList(@Validated @RequestBody ChannelClassPageParam channelClassPageParam, HttpServletRequest request) throws Exception {
        PageEntity<ChannelClassVO> paging = channelClassService.getChannelClassPageList(channelClassPageParam, request);
        return ResultEntity.success(paging);
    }

    @PostMapping("/getList")
    @ApiOperation(value = "渠道类型列表", response = ChannelClassNode.class)
    @ApiOperationSupport(ignoreParameters = {"code", "nodeType", "name", "channelClassId", "channelClassIdNum", "departmentCode", "departmentCodeAllList", "id", "keyword", "orders", "parentId", "productCode", "selectByCCID", "selectByChannel", "selectByChargeRule", "channelClassPageParam.pageIndex", "channelClassPageParam.pageSorts", "channelClassPageParam.pageSize"})
    public ResultEntity<List<ChannelClassNode>> getChannelClassList(@Validated @RequestBody ChannelClassPageParam channelClassPageParam, HttpServletRequest request) throws Exception {
        List<ChannelClassNode> list = channelClassService.getChannelClassList(channelClassPageParam, request);
        return ResultEntity.success(list);
    }

    @PostMapping("/getPullList")
    @ApiOperation(value = "渠道类型下拉", response = ChannelClass.class)
    @ApiOperationSupport(ignoreParameters = {"code", "nodeType", "name", "channelClassId", "channelClassIdNum", "departmentCode", "departmentCodeAllList", "id", "keyword", "orders", "parentId", "productCode", "selectByCCID", "selectByChannel", "selectByChargeRule", "channelClassPageParam.pageIndex", "channelClassPageParam.pageSorts", "channelClassPageParam.pageSize"})
    public ResultEntity<List<ChannelClass>> getPullList(@Validated @RequestBody ChannelClassPageParam channelClassPageParam, HttpServletRequest request) throws Exception {
        List<ChannelClass> list = channelClassService.getPullList(channelClassPageParam);
        return ResultEntity.success(list);
    }
}

