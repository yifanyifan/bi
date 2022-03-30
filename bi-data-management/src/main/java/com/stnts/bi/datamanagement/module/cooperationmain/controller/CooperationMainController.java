package com.stnts.bi.datamanagement.module.cooperationmain.controller;

import com.alibaba.fastjson.JSON;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.cooperationmain.entity.CooperationMain;
import com.stnts.bi.datamanagement.module.cooperationmain.param.CooperationMainPageParam;
import com.stnts.bi.datamanagement.module.cooperationmain.service.CooperationMainService;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 公司主体 控制器
 *
 * @author 易樊
 * @since 2021-09-17
 */
@Slf4j
@RestController
@RequestMapping("/cooperationMain")
@Api(value = "公司主体API", tags = {"公司主体"})
public class CooperationMainController {

    @Autowired
    private CooperationMainService cooperationMainService;

    @GetMapping("/search/all")
    @ApiOperation(value = "查询-搜索框全部集合 By Yf")
    public ResultEntity searchList(@ApiParam(name = "departmentCode", value = "部门Code") String departmentCode,
                                   @ApiParam(name = "cooperationMainId", value = "公司主体ID") String cooperationMainId,
                                   HttpServletRequest request) throws Exception {
        Map<String, Object> mapAll = cooperationMainService.searchList(departmentCode, cooperationMainId, request);

        return ResultEntity.success(mapAll);
    }

    /**
     * 添加公司主体
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加公司主体", response = ResultEntity.class)
    @ApiOperationSupport(ignoreParameters = {"id", "createUserId", "createUserName", "createTime", "updateTime"})
    public ResultEntity<Boolean> addCooperationMain(@Validated(Add.class) @RequestBody CooperationMain cooperationMain, HttpServletRequest request) throws Exception {
        log.info("添加公司主体:" + JSON.toJSONString(cooperationMain));

        boolean flag = cooperationMainService.saveCooperationMain(cooperationMain, request);
        return ResultEntity.success(flag);
    }

    /**
     * 修改公司主体
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改公司主体", response = ResultEntity.class)
    @ApiOperationSupport(ignoreParameters = {"createUserId", "createUserName", "createTime", "updateTime"})
    public ResultEntity<Boolean> updateCooperationMain(@Validated(Update.class) @RequestBody CooperationMain cooperationMain) throws Exception {
        boolean flag = cooperationMainService.updateCooperationMain(cooperationMain);
        return ResultEntity.success(flag);
    }

    /**
     * 删除公司主体
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除公司主体", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteCooperationMain(@PathVariable("id") Long id) throws Exception {
        boolean flag = cooperationMainService.deleteCooperationMain(id);
        return ResultEntity.success(flag);
    }

    /**
     * 获取公司主体详情
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "公司主体详情", response = CooperationMain.class)
    public ResultEntity<CooperationMain> getCooperationMain(@PathVariable("id") Long id) throws Exception {
        CooperationMain cooperationMain = cooperationMainService.getById(id);
        return ResultEntity.success(cooperationMain);
    }

    /**
     * 公司主体分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "公司主体分页列表", response = CooperationMain.class)
    @ApiOperationSupport(ignoreParameters = {"departmentCodeAllList", "orders"})
    public ResultEntity<PageEntity<CooperationMain>> getCooperationMainPageList(@Validated @RequestBody CooperationMainPageParam cooperationMainPageParam, HttpServletRequest request) throws Exception {
        PageEntity<CooperationMain> paging = cooperationMainService.getCooperationMainPageList(cooperationMainPageParam, request);
        return ResultEntity.success(paging);
    }

    /**
     * 公司主体列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "公司主体列表", response = CooperationMain.class)
    @ApiOperationSupport(ignoreParameters = {"departmentCodeAllList", "orders", "cooperationMainPageParam.pageIndex", "cooperationMainPageParam.pageSorts", "cooperationMainPageParam.pageSize"})
    public ResultEntity<List<CooperationMain>> getCooperationMainList(@Validated @RequestBody CooperationMainPageParam cooperationMainPageParam, HttpServletRequest request) throws Exception {
        List<CooperationMain> list = cooperationMainService.getCooperationMainList(cooperationMainPageParam, request);
        return ResultEntity.success(list);
    }

}

