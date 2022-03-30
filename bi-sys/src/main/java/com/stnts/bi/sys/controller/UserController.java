package com.stnts.bi.sys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.BiLog;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.UserEntity;
import com.stnts.bi.entity.sys.UserProductFocusEntity;
import com.stnts.bi.entity.sys.UserRoleEntity;
import com.stnts.bi.enums.LogOpTypeEnum;
import com.stnts.bi.sys.params.UserOrgParam;
import com.stnts.bi.sys.params.UserRoleNewParam;
import com.stnts.bi.sys.params.UserRoleParam;
import com.stnts.bi.sys.service.UserService;
import com.stnts.bi.sys.vos.TreeVO;
import com.stnts.bi.sys.vos.UserRoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import javax.naming.Name;

/**
 * @author liang.zhang
 * @date 2020年3月29日
 * @desc TODO
 */
@Api(value = "用户管理", tags = {"用户管理"})
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @BiLog
    @ApiOperation("用户列表[分页]")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", defaultValue = "1", dataType = "int", value = "页数"),
            @ApiImplicitParam(name = "userId", dataType = "int", value = "用户ID"),
            @ApiImplicitParam(name = "roleIds", dataType = "int", allowMultiple = true, value = "角色ID"),
            @ApiImplicitParam(name = "productIds", dataType = "int", allowMultiple = true, value = "产品线ID")})
    @RequestMapping(value = {"list"}, method = {RequestMethod.GET})
    public ResultEntity<Page<UserEntity>> list(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "userId", required = false) Integer userId,
            @RequestParam(name = "roleIds", required = false) List<Integer> roleIds,
            @RequestParam(name = "productIds", required = false) List<Integer> productIds) {
        return this.userService.findUserListByUserId(page, userId, roleIds, productIds);
    }

    @BiLog
    @ApiOperation("用户列表v2.0[分页]")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", defaultValue = "1", dataType = "int", value = "页数"),
            @ApiImplicitParam(name = "userId", dataType = "int", value = "用户ID"),
            @ApiImplicitParam(name = "roleIds", dataType = "int", allowMultiple = true, value = "角色ID"),
            @ApiImplicitParam(name = "departmentCode", dataType = "string", value = "部门CODE"),
            @ApiImplicitParam(name = "orgId", dataType = "int", value = "组织ID"),
            @ApiImplicitParam(name = "productIds", dataType = "string", allowMultiple = true, value = "产品ID")
    })
    @RequestMapping(value = {"listNew"}, method = {RequestMethod.GET})
    public ResultEntity<Page<UserEntity>> listNew(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "userId", required = false) Integer userId,
            @RequestParam(name = "roleIds", required = false) List<Integer> roleIds,
            @RequestParam(name = "departmentCode", required = false) String departmentCode,
            @RequestParam(name = "orgId", required = false) Integer orgId,
            @RequestParam(name = "productIds", required = false) List<Integer> productIds) {
        return this.userService.findUserListBySearch(page, userId, roleIds, departmentCode, orgId, productIds);
    }

    @ApiOperation("用户列表[不分页]")
    @ApiImplicitParam(name = "cnname", dataType = "string", example = "张三", required = false)
    @GetMapping({"all"})
    public ResultEntity<List<UserEntity>> all(@RequestParam(name = "cnname", required = false) String cnname) {
        return this.userService.findUsers(cnname);
    }

    @BiLog(LogOpTypeEnum.DEL)
    @ApiOperation("删除用户角色")
    @ApiImplicitParam(name = "userId", dataType = "int", value = "用户ID", paramType = "path")
    @DeleteMapping({"{userId}/role"})
    public ResultEntity<String> delRole(@PathVariable(name = "userId") Integer userId) {
        return this.userService.delRole(userId);
    }

    @BiLog(LogOpTypeEnum.MOD)
    @ApiOperation("编辑用户角色")
    @ApiImplicitParam(name = "userId", dataType = "int", value = "用户ID", paramType = "path")
    @PutMapping({"{userId}/role"})
    public ResultEntity<String> modRole(@PathVariable(name = "userId") Integer userId,
                                        @RequestBody(required = true) List<UserRoleParam> roles) {
        return this.userService.modRole(userId, roles);
    }

    @BiLog(LogOpTypeEnum.MOD)
    @ApiOperation("编辑用户角色(V2.0)")
    @PutMapping("modRole")
    public ResultEntity<String> modRoleNew(@RequestBody UserRoleNewParam userRole) {
        return this.userService.modRoleNew(userRole);
    }

    @BiLog
    @ApiOperation("查看用户角色")
    @ApiImplicitParam(name = "userId", dataType = "int", value = "用户ID", paramType = "path")
    @GetMapping({"{userId}/role"})
    public ResultEntity<List<UserRoleEntity>> listRole(@PathVariable(name = "userId") Integer userId) {
        return this.userService.listRole(userId);
    }

    @BiLog
    @ApiOperation("查看用户角色(V2.0)")
    @ApiImplicitParam(name = "userId", dataType = "int", value = "用户ID", paramType = "path")
    @GetMapping({"{userId}/roleNew"})
    public ResultEntity<UserRoleVO> showRole(@PathVariable(name = "userId") Integer userId) {
        return this.userService.showRole(userId);
    }

    @BiLog
    @ApiOperation("删除用户权限")
    @ApiImplicitParam(name = "userId", dataType = "int", value = "用户ID", paramType = "path")
    @DeleteMapping({"{userId}/perm"})
    public ResultEntity<Boolean> delPerm(@PathVariable(name = "userId") Integer userId) {
        return this.userService.delPerm(userId);
    }


    @BiLog
    @ApiOperation("用户绑定组织")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", dataType = "int", value = "用户ID", paramType = "body"),
            @ApiImplicitParam(name = "orgIds", dataType = "int", allowMultiple = true, value = "组织ID", paramType = "body")
    })
    @PostMapping({"bindOrg"})
    public ResultEntity<Boolean> bindOrg(@RequestBody UserOrgParam userOrgParam) {
        return userService.bindOrg(userOrgParam);
    }

    @ApiOperation("查看用户SDK产品线已选")
    @ApiImplicitParam(name = "userId", dataType = "int", value = "用户ID", paramType = "path")
    @GetMapping({"{userId}/checkedSdk"})
    public ResultEntity<List<String>> checkedSdk(@PathVariable(name = "userId") Integer userId){
        return userService.checkedSdk(userId);
    }

    @ApiOperation("查看用户数据管理已选")
    @ApiImplicitParam(name = "userId", dataType = "int", value = "用户ID", paramType = "path")
    @GetMapping({"{userId}/checkedDm"})
    public ResultEntity<List<String>> checkedDm(@PathVariable(name = "userId") Integer userId){
        return userService.checkedDm(userId);
    }

    @ApiOperation("查看用户SDK权限")
    @ApiImplicitParam(name = "userId", dataType = "int", value = "用户ID", paramType = "path")
    @GetMapping({"{userId}/sdk"})
    public ResultEntity<List<TreeVO>> sdk(@PathVariable(name = "userId") Integer userId){
        return userService.sdk(userId);
    }

    @ApiOperation("用户关注产品线")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", dataType = "int", value = "用户ID", paramType = "body"),
            @ApiImplicitParam(name = "productId", dataType = "string", value = "产品线ID", paramType = "body")
    })
    @PostMapping("product/focus")
    public ResultEntity<Boolean> focusProduct(@RequestBody UserProductFocusEntity userProductFocusEntity){
        return userService.focusProduct(userProductFocusEntity);
    }

    @ApiOperation("查看用户关注产品线")
    @ApiImplicitParam(name = "userId", dataType = "int", value = "用户ID", paramType = "path")
    @GetMapping("product/focus/{userId}")
    public ResultEntity<UserProductFocusEntity> focusProduct(@PathVariable(name = "userId") Integer userId){
        return userService.getFocusProduct(userId);
    }
}
