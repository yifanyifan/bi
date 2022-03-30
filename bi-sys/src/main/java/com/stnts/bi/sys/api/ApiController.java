package com.stnts.bi.sys.api;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.DepartmentEntity;
import com.stnts.bi.entity.sys.UserDmEntity;
import com.stnts.bi.sys.service.DepartmentService;
import com.stnts.bi.sys.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: liang.zhang
 * @description: 提供接口服务的
 * @date: 2021/7/6
 */
@Api(value = "系统管理接口", tags = {"系统管理接口"})
@RestController
@Slf4j
@RequestMapping("api")
public class ApiController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userService;

    @ApiOperation("通过组织ID获取部门列表")
    @ApiImplicitParam(name = "orgId", required = true, type = "int", example = "组织ID：1", allowMultiple = true)
    @GetMapping("listDepartmentByOrgId")
    public List<DepartmentEntity> listDepartmentByOrgId(@RequestParam List<Integer> orgIds){
        return departmentService.listDepartmentByOrgId(orgIds);
    }

    @ApiOperation("通过用户ID获取数据管理权限")
    @ApiImplicitParam(name = "userId", required = true, type = "int", example = "用户ID：2239")
    @GetMapping("listDmByUserId")
    public ResultEntity<List<UserDmEntity>> listDmByUserId(@RequestParam Integer userId){
        return userService.listDmByUserId(userId);
    }

    @ApiOperation("删除数据管理中过期CCID")
    @ApiImplicitParam(name = "ccid", required = true, type = "string", example = "CCID：ABCDEFG123")
    @GetMapping("delDmByCcid")
    public ResultEntity<Boolean> delDmByCcid(@RequestParam String ccid){
        return userService.delDmByCcid(ccid);
    }
}
