package com.stnts.bi.sys.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.BiLog;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.SdkDataLevelEntity;
import com.stnts.bi.enums.LogOpTypeEnum;
import com.stnts.bi.groups.InsertGroup;
import com.stnts.bi.groups.UpdateGroup;
import com.stnts.bi.sys.service.SdkDataLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: liang.zhang
 * @description: 数据层级管理
 * @date: 2021/5/28
 */
@Api(value = "数据层级管理", tags = "数据层级管理")
@RestController
@RequestMapping("dataLevel")
public class SdkDataLevelController {

    @Autowired
    private SdkDataLevelService sdkDataLevelService;

    @BiLog
    @ApiOperation("数据层级列表")
    @GetMapping("list/{productId}")
    public ResultEntity<List<SdkDataLevelEntity>> list(@PathVariable String productId){
        return sdkDataLevelService.list(productId);
    }

    @BiLog(LogOpTypeEnum.NEW)
    @ApiOperation("数据层级新增")
    @ApiOperationSupport(ignoreParameters = {"sdkDataLevelEntity.levelId", "sdkDataLevelEntity.createdAt", "sdkDataLevelEntity.updatedAt", "sdkDataLevelEntity.children", "sdkDataLevelEntity.checked"})
    @PostMapping("add")
    public ResultEntity<SdkDataLevelEntity> add(@Validated(InsertGroup.class) @RequestBody SdkDataLevelEntity sdkDataLevelEntity){
        return sdkDataLevelService.add(sdkDataLevelEntity);
    }

    @BiLog(LogOpTypeEnum.MOD)
    @ApiOperation("数据层级改名")
    @ApiOperationSupport(includeParameters = {"sdkDataLevelEntity.levelId", "sdkDataLevelEntity.levelName"})
    @PostMapping("rename")
    public ResultEntity<SdkDataLevelEntity> rename(@Validated(UpdateGroup.class) @RequestBody SdkDataLevelEntity sdkDataLevelEntity){
        return sdkDataLevelService.rename(sdkDataLevelEntity);
    }

    @BiLog(LogOpTypeEnum.MOD)
    @ApiOperation("数据层级拖拽")
    @ApiOperationSupport(ignoreParameters = {"sdkDataLevelEntity.levelName", "sdkDataLevelEntity.type", "sdkDataLevelEntity.createdBy", "sdkDataLevelEntity.createdAt", "sdkDataLevelEntity.updatedAt", "sdkDataLevelEntity.children", "sdkDataLevelEntity.checked"})
    @PostMapping("drag")
    public ResultEntity<SdkDataLevelEntity> drag(@Validated(UpdateGroup.class) @RequestBody SdkDataLevelEntity sdkDataLevelEntity){
        return sdkDataLevelService.drag(sdkDataLevelEntity);
    }

    @BiLog(LogOpTypeEnum.DEL)
    @ApiOperation("数据层级删除")
    @ApiOperationSupport(includeParameters = {"sdkDataLevelEntity.levelId", "sdkDataLevelEntity.path"})
    @PostMapping("del")
    public ResultEntity<Boolean> del(@Validated(UpdateGroup.class) @RequestBody SdkDataLevelEntity sdkDataLevelEntity){
        return sdkDataLevelService.del(sdkDataLevelEntity);
    }
}
