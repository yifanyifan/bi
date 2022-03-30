package com.stnts.bi.sys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.DepartmentEntity;
import com.stnts.bi.entity.sys.OrgEntity;
import com.stnts.bi.groups.InsertGroup;
import com.stnts.bi.groups.UpdateGroup;
import com.stnts.bi.sys.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/27
 */
@Api(value = "部门组织管理", tags = "部门组织管理")
@RestController
@RequestMapping("department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @ApiOperation(value = "部门列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "查询关键字", dataType = "string"),
            @ApiImplicitParam(name = "page", value = "查询第几页", dataType = "int", defaultValue = "1")
    })
    @GetMapping("list")
    public ResultEntity<Page<DepartmentEntity>> list(@RequestParam(required = false, name = "keyword") String keyword,
                                                     @RequestParam(required = false, defaultValue = "1") Integer page){
        return departmentService.list(keyword, page);
    }

    @ApiOperation(value = "部门绑定组织", notes = "数据以json形式请求, 以下为json体参数")
    @ApiOperationSupport(includeParameters = {"departmentEntity.id", "departmentEntity.orgId"})
    @PostMapping("bindOrg")
    public ResultEntity<Boolean> bindOrg(@RequestBody DepartmentEntity departmentEntity){
        return departmentService.bindOrg(departmentEntity);
    }

    @ApiOperation(value = "组织列表", notes = "按更新时间降序排列")
    @GetMapping("listOrg")
    public ResultEntity<List<OrgEntity>> listOrg(){
        return departmentService.listOrg();
    }

    @ApiOperation("删除部门组织")
    @DeleteMapping("del/{orgId}")
    public ResultEntity<Boolean> delOrg(@PathVariable(value = "orgId") int orgId){
        return departmentService.delOrg(orgId);
    }

    @ApiOperation("新增部门组织")
    @ApiOperationSupport(includeParameters = {"orgEntity.orgName", "orgEntity.createdBy"})
    @PostMapping("addOrg")
    public ResultEntity<OrgEntity> addOrg(@Validated(InsertGroup.class) @RequestBody OrgEntity orgEntity){
        return departmentService.addOrg(orgEntity);
    }

    @ApiOperation("修改部门组织信息")
    @ApiOperationSupport(includeParameters = {"orgEntity.orgId", "orgEntity.orgName"})
    @PostMapping("updOrg")
    public ResultEntity<OrgEntity> updOrg(@Validated(UpdateGroup.class) @RequestBody OrgEntity orgEntity){
        return departmentService.updOrg(orgEntity);
    }

    @ApiOperation(value = "部门列表(不分页)")
    @GetMapping("all")
    public ResultEntity<List<DepartmentEntity>> all(){
        return departmentService.all();
    }

    @ApiOperation(value = "组织列表(不分页)")
    @GetMapping("allOrg")
    public ResultEntity<List<OrgEntity>> allOrg(){
        return departmentService.allOrg();
    }
}
