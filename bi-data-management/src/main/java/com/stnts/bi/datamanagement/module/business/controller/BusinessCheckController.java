package com.stnts.bi.datamanagement.module.business.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.business.entity.BusinessCheck;
import com.stnts.bi.datamanagement.module.business.entity.BusinessCheckHistory;
import com.stnts.bi.datamanagement.module.business.service.BusinessCheckHistoryService;
import com.stnts.bi.datamanagement.module.business.service.BusinessCheckService;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationBiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 考核明细 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-08
 */
@RestController
@RequestMapping("/business/check")
@Api(value = "考核明细API", tags = {"考核明细"})
public class BusinessCheckController {
    @Autowired
    private CooperationBiService cooperationBiService;
    @Autowired
    private BusinessCheckService businessCheckService;
    @Autowired
    private BusinessCheckHistoryService businessCheckHistoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/search/all")
    @ApiOperation(value = "查询-搜索框全部集合 By Yf")
    public ResultEntity searchList(@ApiParam(name = "departmentCode", value = "部门Code") String departmentCode,
                                   @ApiParam(name = "businessLevel", value = "层级") String businessLevel,
                                   HttpServletRequest request) {
        Map<String, Object> mapAll = businessCheckService.searchList(departmentCode, businessLevel, request);
        return ResultEntity.success(mapAll);
    }

    @GetMapping("/search/department")
    @ApiOperation(value = "查询-部门 By Yf")
    public ResultEntity departmentList(@ApiParam(name = "departmentCode", value = "部门Code") String departmentCode,
                                       @ApiParam(name = "businessLevel", value = "层级") String businessLevel,
                                       HttpServletRequest request) {
        List<Map<String, String>> resultList = businessCheckService.departmentList(departmentCode, businessLevel, request);
        return ResultEntity.success(resultList);
    }

    @GetMapping("/list")
    @ApiOperation(value = "考核明细分页页面")
    public ResultEntity<Page<BusinessCheck>> list(@ApiParam(name = "department", value = "部门名称") String department,
                                                  @ApiParam(name = "departmentCode", value = "部门Code") String departmentCode,
                                                  @ApiParam(name = "businessLevel", value = "层级") String businessLevel,
                                                  Integer currentPage,
                                                  Integer pageSize,
                                                  HttpServletRequest request) {
        Page<BusinessCheck> businessCheckList = businessCheckService.listPage(department, departmentCode, businessLevel, currentPage, pageSize, request);
        return ResultEntity.success(businessCheckList);
    }

    @GetMapping("/get")
    @ApiOperation(value = "考核明细查询")
    public ResultEntity<BusinessCheck> get(@ApiParam(name = "id", value = "考核明细ID") Long id) {
        BusinessCheck businessCheck = businessCheckService.getById(id);
        return ResultEntity.success(businessCheck);
    }

    @PostMapping("/save")
    @ApiOperation(value = "考核明细新增")
    public ResultEntity<BusinessCheck> save(@RequestBody BusinessCheck businessCheck, HttpServletRequest request) {
        businessCheckService.saveBusinessCheck(businessCheck, request);
        return ResultEntity.success(businessCheck);
    }

    @PostMapping("/update")
    @ApiOperation(value = "考核明细更新")
    public ResultEntity<BusinessCheck> update(@RequestBody BusinessCheck businessCheck, HttpServletRequest request) {
        businessCheckService.updateBusinessCheck(businessCheck, request);
        return ResultEntity.success(businessCheck);
    }

    @PostMapping("/remove")
    @ApiOperation(value = "考核明细删除", response = ResultEntity.class)
    public ResultEntity remove(Long id) {
        businessCheckService.removeById(id);
        businessCheckHistoryService.remove(new LambdaQueryWrapper<BusinessCheckHistory>().eq(BusinessCheckHistory::getBusinessCheckId, id));
        return ResultEntity.success(null);
    }
}
