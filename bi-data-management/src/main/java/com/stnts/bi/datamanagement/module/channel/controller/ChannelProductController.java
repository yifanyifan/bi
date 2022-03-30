package com.stnts.bi.datamanagement.module.channel.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelApplication;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProduct;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductCostPageParam;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelApplicationService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductCostService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductService;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelProductCostVO;
import com.stnts.bi.datamanagement.module.channel.vo.DepartmentVO;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiService;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 产品信息 控制器
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Slf4j
@RestController
@RequestMapping("/channelProduct")
@Api(value = "产品信息API", tags = {"产品信息"})
public class ChannelProductController {

    @Autowired
    private ChannelProductService channelProductService;

    @Autowired
    private ChannelProductCostService channelProductCostService;

    @Autowired
    private ChannelApplicationService channelApplicationService;

    @Autowired
    private CooperationBiService cooperationBiService;

    @GetMapping("/search/all")
    @ApiOperation(value = "查询-搜索框全部集合 By Yf")
    public ResultEntity searchList(@ApiParam(name = "departmentCode", value = "部门Code") String departmentCode,
                                   @ApiParam(name = "cooperationMainId", value = "公司主体Id") String cooperationMainId,
                                   HttpServletRequest request) {
        Map<String, Object> mapAll = channelProductService.searchList(departmentCode, cooperationMainId, request);

        return ResultEntity.success(mapAll);
    }

    /**
     * 添加产品信息
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加产品信息", response = ResultEntity.class)
    public ResultEntity<Boolean> addChannelProduct(@Validated(Add.class) @RequestBody ChannelProduct channelProduct) throws Exception {
        if (StringUtils.isBlank(channelProduct.getDataSource())) {
            channelProduct.setDataSource("BI");
        }
        channelProductService.saveChannelProduct(channelProduct);
        return ResultEntity.success(true);
    }

    /**
     * 修改产品信息
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改产品信息", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelProduct(@Validated(Update.class) @RequestBody ChannelProduct channelProduct) throws Exception {
        boolean flag = channelProductService.updateChannelProduct(channelProduct);
        return ResultEntity.success(flag);
    }

    /**
     * 删除产品信息
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除产品信息", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelProduct(@PathVariable("id") Long id) throws Exception {
        boolean flag = channelProductService.deleteChannelProduct(id);
        return ResultEntity.success(flag);
    }

    /**
     * 获取产品信息详情
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "产品信息详情-产品编辑时获取信息用", response = ChannelProduct.class)
    public ResultEntity<ChannelProduct> getChannelProduct(@PathVariable("id") String id) throws Exception {
        ChannelProduct channelProduct = channelProductService.getById(id);

        return ResultEntity.success(channelProduct);
    }

    @PostMapping("/infoExtForTab")
    @ApiOperation(value = "产品分成详情-产品信息中Tab页使用", response = ChannelProductCostVO.class)
    @ApiOperationSupport(ignoreParameters = {"channelProductCostPageParam.channelShare", "channelProductCostPageParam.channelShareStep", "channelProductCostPageParam.channelShareType", "channelProductCostPageParam.id", "channelProductCostPageParam.keyword", "channelProductCostPageParam.orders", "channelProductCostPageParam.pageIndex", "channelProductCostPageParam.pageSize", "channelProductCostPageParam.selectByAll", "channelProductCostPageParam.selectByCCID", "channelProductCostPageParam.selectByChannelClass"})
    public ResultEntity<ChannelProductCostVO> infoExtForTab(@RequestBody ChannelProductCostPageParam channelProductCostPageParam) throws Exception {
        ChannelProductCostVO channelProductCost = channelProductCostService.getChannelProductCostExt(channelProductCostPageParam);
        return ResultEntity.success(channelProductCost);
    }

    /**
     * 产品信息分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "产品信息分页列表", response = ChannelProduct.class)
    public ResultEntity<PageEntity<ChannelProduct>> getChannelProductPageList(@Validated @RequestBody ChannelProductPageParam channelProductPageParam,
                                                                              HttpServletRequest request) throws Exception {
        PageEntity<ChannelProduct> paging = channelProductService.getChannelProductPageList(channelProductPageParam, request);
        return ResultEntity.success(paging);
    }

    /**
     * 产品信息列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "产品信息列表", response = ChannelProduct.class)
    @ApiOperationSupport(ignoreParameters = {"channelProductPageParam.pageIndex", "channelProductPageParam.pageSorts", "channelProductPageParam.pageSize"})
    public ResultEntity<List<ChannelProduct>> getChannelProductList(@Validated @RequestBody ChannelProductPageParam channelProductPageParam, HttpServletRequest request) throws Exception {
        List<ChannelProduct> list = channelProductService.getChannelProductList(channelProductPageParam, request);
        return ResultEntity.success(list);
    }

    /**
     * 产品信息列表
     */
    @PostMapping("/getProAppList")
    @ApiOperation(value = "产品/应用信息列表 By yf", response = ChannelProduct.class)
    public ResultEntity<List<ChannelApplication>> getProAppList(@Validated @RequestBody ChannelProductPageParam channelProductPageParam, HttpServletRequest request) throws Exception {
        List<ChannelApplication> list = channelProductService.getChannelProductAppList(channelProductPageParam, request);
        return ResultEntity.success(list);
    }

    @PostMapping("getListExt")
    @ApiOperation(value = "产品信息分页列表扩展")
    public ResultEntity<PageEntity<ChannelProduct>> getChannelProductPageListExt(@Validated @RequestBody ChannelProductPageParam channelProductPageParam, HttpServletRequest request) throws Exception {
        PageEntity<ChannelProduct> pageEntity = channelProductService.getChannelProductPageListExt(channelProductPageParam, request);

        return ResultEntity.success(pageEntity);
    }

    @PostMapping("getCCIDListExt")
    @ApiOperation(value = "关联CCID信息")
    @ApiOperationSupport(ignoreParameters = {"channelProductPageParam.pageIndex", "channelProductPageParam.pageSize", "channelProductPageParam.cooperationMainName", "channelProductPageParam.ccid", "channelProductPageParam.vendorCheckEndDate", "channelProductPageParam.vendorCheckStartDate", "channelProductPageParam.vendorId", "channelProductPageParam.vendorName", "channelProductPageParam.departmentCode", "channelProductPageParam.departmentCodeAllList", "channelProductPageParam.departmentName", "channelProductPageParam.keyword", "channelProductPageParam.mapAll", "channelProductPageParam.orders", "channelProductPageParam.pageSorts"})
    public ResultEntity<List<ChannelCooperation>> getCCIDListExt(@Validated @RequestBody ChannelProductPageParam channelProductPageParam, HttpServletRequest request) throws Exception {
        List<ChannelCooperation> channelCooperationList = channelProductService.getCCIDListExt(channelProductPageParam, request);

        return ResultEntity.success(channelCooperationList);
    }

    @GetMapping("listDepartment")
    @ApiOperation("搜索条件:部门列表")
    public ResultEntity<List<DepartmentVO>> listDes(String cooperationMainName, HttpServletRequest request) {
        List<DepartmentVO> departmentVOS = channelProductService.listDepartment(cooperationMainName, request);
        return ResultEntity.success(departmentVOS);
    }

    @PostMapping("/moveCPCompany")
    @ApiOperation(value = "迁移厂商")
    @ApiOperationSupport(ignoreParameters = {"channelProductPageParam.cooperationMainName", "channelProductPageParam.ccid", "channelProductPageParam.departmentCode", "channelProductPageParam.departmentCodeAllList", "channelProductPageParam.mapAll", "channelProductPageParam.orders", "channelProductPageParam.departmentName", "channelProductPageParam.keyword", "channelProductPageParam.pageIndex", "channelProductPageParam.pageSize"})
    public ResultEntity<Boolean> moveCPCompany(@Validated @RequestBody ChannelProductPageParam channelProductPageParam, HttpServletRequest request) throws Exception {
        Boolean flag = channelProductService.moveCPCompany(channelProductPageParam, request);

        return ResultEntity.success(flag);
    }

    @GetMapping("/getProductFlagList")
    @ApiOperation(value = "游戏下拉")
    public ResultEntity<Map<String, List<String>>> getProductFlagList(HttpServletRequest request) throws Exception {
        Map<String, List<String>> map = channelProductService.getProductFlagList();

        return ResultEntity.success(map);
    }
}

