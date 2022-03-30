package com.stnts.bi.sys.controller;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.BiLog;
import com.stnts.bi.enums.LogOpTypeEnum;
import com.stnts.bi.sys.vos.ProductBindVO;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.ProductEntity;
import com.stnts.bi.sys.service.ProductService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author liang.zhang
 * @date 2020年4月8日
 * @desc TODO
 */
@Api(value = "产品线管理", tags = "产品线管理")
@RestController
@RequestMapping(value = "product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("产品线列表[不分页]")
    @ApiImplicitParam(name = "name", dataType = "string", required = false, example = "带带", paramType = "query")
    @GetMapping(value = "all")
    public ResultEntity<List<ProductEntity>> all(@RequestParam(name = "name", required = false) String name) {
        return productService.all(name);
    }

    @ApiOperation("产品线列表[分页]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", required = false, example = "1", paramType = "query"),
            @ApiImplicitParam(name = "name", dataType = "string", required = false, example = "带带", paramType = "query")
    })
    @GetMapping(value = "list")
    public ResultEntity<Page<ProductEntity>> list(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                  @RequestParam(name = "name", required = false) String name) {
        return productService.list(page, name);
    }

    @ApiIgnore
    @GetMapping("sync")
    public ResultEntity<String> productSync() {
        return productService.syncProduct();
    }

    @BiLog(LogOpTypeEnum.DEL)
    @ApiOperation("删除产品线下用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", dataType = "int", required = true, example = "110", paramType = "path"),
            @ApiImplicitParam(name = "userIds", dataType = "int", required = false, example = "110", paramType = "query")
    })
    @DeleteMapping(value = "user/{productId}")
    public ResultEntity<String> delUsers(@PathVariable(value = "productId") Integer productId,
                                         @RequestParam(value = "userIds", required = false)  List<Integer> userIds){
        return productService.delUsers(productId, userIds);
    }

    @BiLog(LogOpTypeEnum.MOD)
    @ApiOperation("产品线绑定数据层级")
    @ApiOperationSupport(includeParameters = {"productEntity.productId", "productEntity.levelId"})
    @PostMapping("bind")
    public ResultEntity<String> bindDataLevel(@RequestBody ProductEntity productEntity){
        return productService.bindDataLevel(productEntity);
    }
}
