package com.stnts.bi.datamanagement.module.channel.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProductCost;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductCostPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductCostService;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassNode;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelProductCostVO;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 产品分成 控制器
 *
 * @author 易樊
 * @since 2021-09-24
 */
@Slf4j
@RestController
@RequestMapping("/channelProductCost")
@Api(value = "产品分成API", tags = {"产品分成"})
public class ChannelProductCostController {

    @Autowired
    private ChannelProductCostService channelProductCostService;

    /**
     * 添加产品分成
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加产品分成", response = ResultEntity.class)
    @ApiOperationSupport(ignoreParameters = {"channelProductCost.ccid", "channelProductCost.channelClassIdPath", "channelProductCost.channelClassNodeRoot", "channelProductCost.channelClassPath", "channelProductCost.channelCooperationList", "channelProductCost.channelId", "channelProductCost.channelName", "channelProductCost.chargeRule", "channelProductCost.createTime", "channelProductCost.departmentCode", "channelProductCost.id", "channelProductCost.selectByAll", "channelProductCost.selectByAllOther", "channelProductCost.selectByCCID", "channelProductCost.selectByCCIDOther", "channelProductCost.selectByChannelClass", "channelProductCost.selectByChannelClassOther", "channelProductCost.selectByNo", "channelProductCost.selectByNoOther", "channelProductCost.updateTime", "channelProductCost.userid", "channelProductCost.username"})
    public ResultEntity<Boolean> addChannelProductCost(@Validated(Add.class) @RequestBody ChannelProductCost channelProductCost, HttpServletRequest httpServletRequest) throws Exception {
        boolean flag = channelProductCostService.saveChannelProductCost(channelProductCost, httpServletRequest);
        return ResultEntity.success(flag);
    }

    /**
     * 修改产品分成
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改产品分成", response = ResultEntity.class)
    @ApiOperationSupport(ignoreParameters = {"channelProductCost.ccid", "channelProductCost.channelClassIdPath", "channelProductCost.channelClassNodeRoot",
            "channelProductCost.channelClassPath", "channelProductCost.channelCooperationList", "channelProductCost.channelId", "channelProductCost.channelName",
            "channelProductCost.chargeRule", "channelProductCost.createTime", "channelProductCost.departmentCode", "channelProductCost.selectByAll", "channelProductCost.selectByAllOther",
            "channelProductCost.selectByCCID", "channelProductCost.selectByCCIDOther", "channelProductCost.selectByChannelClass", "channelProductCost.selectByChannelClassOther",
            "channelProductCost.selectByNo", "channelProductCost.selectByNoOther", "channelProductCost.updateTime", "channelProductCost.userid", "channelProductCost.username"})
    public ResultEntity<Boolean> updateChannelProductCost(@RequestBody ChannelProductCost channelProductCost) throws Exception {
        boolean flag = channelProductCostService.updateChannelProductCost(channelProductCost);
        return ResultEntity.success(flag);
    }

    /**
     * 删除产品分成
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除产品分成", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelProductCost(@PathVariable("id") Long id) throws Exception {
        boolean flag = channelProductCostService.deleteChannelProductCost(id);
        return ResultEntity.success(flag);
    }

    @GetMapping("/getTreeAddData/{productCode}")
    @ApiOperation(value = "产品分成详情-新增标签数据", response = ChannelProductCostVO.class)
    public ResultEntity<ChannelProductCostVO> getTreeAddData(@PathVariable("productCode") String productCode) throws Exception {
        ChannelProductCostVO channelProductCostVO = channelProductCostService.getTreeByNew(productCode);
        return ResultEntity.success(channelProductCostVO);
    }

    @GetMapping("/getTreeForUpdate/{productCode}/{costId}")
    @ApiOperation(value = "获取关联CCID树-更新时获取树用", response = ChannelProductCostVO.class)
    public ResultEntity<ChannelProductCostVO> getTreeForUpdate(@PathVariable("productCode") String productCode, @PathVariable("costId") String costId) throws Exception {
        ChannelProductCostVO channelProductCostVO = channelProductCostService.getTreeForUpdate(productCode, costId);
        return ResultEntity.success(channelProductCostVO);
    }

    /*@GetMapping("/noSet/{productCode}")
    @ApiOperation(value = "未配置CCID数量", response = ChannelProductCostVO.class)
    public ResultEntity<Integer> noSet(@PathVariable("productCode") String productCode) throws Exception {
        Integer count = channelProductCostService.noSet(productCode);
        return ResultEntity.success(count);
    }*/

    @GetMapping("/noSetList/{productCode}")
    @ApiOperation(value = "未配置CCID列表", response = ChannelClassNode.class)
    public ResultEntity<List<ChannelClassNode>> noSetList(@PathVariable("productCode") String productCode) throws Exception {
        List<ChannelClassNode> channelClassNodeList = channelProductCostService.noSetList(productCode);
        return ResultEntity.success(channelClassNodeList);
    }

    @GetMapping("/getTreeStructure/{productCode}")
    @ApiOperation(value = "产品分成详情-获取树结构", response = ChannelProductCostVO.class)
    public ResultEntity<ChannelClassNode> getTreeStructure(@PathVariable("productCode") String productCode) throws Exception {
        ChannelClassNode channelProductCost = channelProductCostService.getTreeStructure(productCode);
        return ResultEntity.success(channelProductCost);
    }

    @GetMapping("/info/{productCode}")
    @ApiOperation(value = "产品分成详情-各标签数据", response = ChannelProductCostVO.class)
    public ResultEntity<List<ChannelProductCostVO>> getChannelProductCost(@PathVariable("productCode") String productCode) throws Exception {
        List<ChannelProductCostVO> channelProductCost = channelProductCostService.getChannelProductCost(productCode);
        return ResultEntity.success(channelProductCost);
    }

    /**
     * 产品分成分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "产品分成分页列表", response = ChannelProductCost.class)
    public ResultEntity<PageEntity<ChannelProductCost>> getChannelProductCostPageList(@Validated @RequestBody ChannelProductCostPageParam channelProductCostPageParam) throws Exception {
        PageEntity<ChannelProductCost> paging = channelProductCostService.getChannelProductCostPageList(channelProductCostPageParam);
        return ResultEntity.success(paging);
    }

    /**
     * 产品分成列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "产品分成列表", response = ChannelProductCost.class)
    @ApiOperationSupport(ignoreParameters = {"channelProductCostPageParam.pageIndex", "channelProductCostPageParam.pageSorts", "channelProductCostPageParam.pageSize"})
    public ResultEntity<List<ChannelProductCost>> getChannelProductCostList(@Validated @RequestBody ChannelProductCostPageParam channelProductCostPageParam) throws Exception {
        List<ChannelProductCost> list = channelProductCostService.getChannelProductCostList(channelProductCostPageParam);
        return ResultEntity.success(list);
    }
}

