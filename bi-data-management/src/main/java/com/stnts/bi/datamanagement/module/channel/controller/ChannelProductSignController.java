package com.stnts.bi.datamanagement.module.channel.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelApplication;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProduct;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductService;
import com.stnts.bi.datamanagement.module.exportdata.service.ExportDataService;
import com.stnts.bi.datamanagement.module.feign.SysClient;
import com.stnts.bi.datamanagement.util.SignUtil;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import com.stnts.signature.annotation.SignedMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 产品信息 控制器
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Slf4j
@RestController
@RequestMapping("/sign/channelProduct")
@Api(value = "产品信息API", tags = {"签名-产品信息"})
@SignedMapping
public class ChannelProductSignController {
    @Autowired
    private ExportDataService exportDataService;
    @Autowired
    private ChannelProductService channelProductService;
    @Autowired
    private SysClient sysClient;
    @Autowired
    private SignUtil signUtil;
    @Autowired
    private EnvironmentProperties environmentProperties;

    /**
     * 添加产品信息
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加产品信息", response = ResultEntity.class)
    public ResultEntity<Boolean> addChannelProduct(@Validated(Add.class) @RequestBody ChannelProduct channelProduct) throws Exception {
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
    @ApiOperation(value = "产品信息详情", response = ChannelProduct.class)
    public ResultEntity<ChannelProduct> getChannelProduct(@PathVariable("id") Long id) throws Exception {
        ChannelProduct channelProduct = channelProductService.getById(id);
        return ResultEntity.success(channelProduct);
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

    @PostMapping("/addGeneral")
    @ApiOperation(value = "添加产品信息_通用", response = ResultEntity.class)
    public ResultEntity<ChannelProduct> addChannelProductGeneral(@RequestBody ChannelProduct channelProduct, String appId, Long timestamp, String sign) throws Exception {
        log.info("=====================================>添加产品信息_通用:{},appId:{},timestamp:{},sign:{}", JSON.toJSONString(channelProduct), appId, timestamp, sign);

        //鉴权
        Dict dict = Dict.create().set("departmentCode", channelProduct.getDepartmentCode())
                .set("departmentName", channelProduct.getDepartmentName())
                .set("cooperationMainId", channelProduct.getCooperationMainId())
                .set("productCode", channelProduct.getProductCode())
                .set("productName", channelProduct.getProductName())
                .set("vendorId", channelProduct.getVendorId())
                .set("applicationIds", channelProduct.getApplicationIds())
                .set("businessDictId", channelProduct.getBusinessDictId())
                .set("firstLevelBusiness", channelProduct.getFirstLevelBusiness())
                .set("secondLevelBusiness", channelProduct.getSecondLevelBusiness())
                .set("thirdLevelBusiness", channelProduct.getThirdLevelBusiness())
                .set("saleDepartmentCode", channelProduct.getSaleDepartmentCode())
                .set("productFlag", channelProduct.getProductFlag())
                .set("productScreen", channelProduct.getProductScreen())
                .set("productClass", channelProduct.getProductClass())
                .set("productTheme", channelProduct.getProductTheme())
                .set("userid", channelProduct.getUserid())
                .set("dataSource", channelProduct.getDataSource());
        List<ChannelApplication> applicationList = channelProduct.getApplicationList();
        if (CollectionUtil.isNotEmpty(applicationList)) {
            dict.set("applicationName", applicationList.stream().map(ChannelApplication::getApplicationName).collect(Collectors.joining()));
        }
        signUtil.checkSign(appId, timestamp, sign, dict);

        Long carNumber = channelProduct.getUserid(); // 临时保存
        ChannelProduct channelProductDB = channelProductService.saveChannelProductGeneral(channelProduct);
        channelProductDB.setUserid(carNumber);

        return ResultEntity.success(channelProductDB);
    }

    /**
     * 产品信息列表
     */
    @PostMapping("/getListGeneral")
    public ResultEntity<List<ChannelProduct>> getChannelProductListGeneral(@RequestBody ChannelProductPageParam channelProductPageParam, String appId, Long timestamp, String sign) throws Exception {
        log.info("=====================================>产品信息列表_通用:{},appId:{},timestamp:{},sign:{}", JSON.toJSONString(channelProductPageParam), appId, timestamp, sign);

        if (appId.equals(environmentProperties.getAppId2())) {
            if (StringUtils.isBlank(channelProductPageParam.getDepartmentName())
                    && StringUtils.isBlank(channelProductPageParam.getDepartmentCode())
                    && StringUtils.isBlank(channelProductPageParam.getProductCode())
            ) {
                throw new BusinessException("查询参数必须选择一个以上");
            }
        }
        signUtil.checkSign(appId, timestamp, sign, Dict.create()
                .set("departmentCode", channelProductPageParam.getDepartmentCode())
                .set("departmentName", channelProductPageParam.getDepartmentName())
                .set("productCode", channelProductPageParam.getProductCode())
        );

        List<ChannelProduct> page = channelProductService.getChannelProductPageListExtGeneral(channelProductPageParam);
        return ResultEntity.success(page);
    }

}

