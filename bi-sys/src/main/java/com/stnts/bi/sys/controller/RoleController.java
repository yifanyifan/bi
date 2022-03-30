package com.stnts.bi.sys.controller;

import com.stnts.bi.common.BiLog;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.RoleEntity;
import com.stnts.bi.enums.LogOpTypeEnum;
import com.stnts.bi.sys.params.RoleParam;
import com.stnts.bi.sys.params.RolePermParam;
import com.stnts.bi.sys.service.RoleService;
import com.stnts.bi.sys.vos.PermTopVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liang.zhang
 * @date 2020年3月31日
 * @desc TODO
 */
@Api(value = "角色管理", tags = { "角色管理" })
@RestController
@RequestMapping(value = "role")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@BiLog
	@ApiOperation("角色列表")
	@GetMapping({ "list" })
	public ResultEntity<List<RoleEntity>> list() {
		return roleService.listRoles();
	}

	@BiLog(LogOpTypeEnum.NEW)
	@ApiOperation("新建角色")
	@PostMapping({ "new" })
	public ResultEntity<String> add(@RequestBody RoleParam role) {
		return roleService.addOne(role);
	}

	@BiLog(LogOpTypeEnum.MOD)
	@ApiOperation("修改角色")
	@ApiImplicitParam(name = "roleId", dataType = "int", value = "角色ID", paramType = "path")
	@PutMapping({ "{roleId}" })
	public ResultEntity<String> modRole(@PathVariable(name = "roleId", required = true) Integer roleId,
			@RequestBody RoleParam role) {
		return roleService.update(roleId, role);
	}

	@BiLog(LogOpTypeEnum.MOD)
	@ApiOperation("修改角色权限")
	@ApiImplicitParam(name = "roleId", dataType = "int", value = "角色ID", paramType = "path")
	@PutMapping({ "{roleId}/perm" })
	public ResultEntity<String> modRolePerm(@PathVariable(required = true, name = "roleId") Integer roleId,
			@RequestBody RolePermParam rolePermParam) {
		return roleService.modRolePerm(roleId, rolePermParam);
	}

	@BiLog
	@ApiOperation("查看角色权限")
	@ApiImplicitParam(name = "roleId", dataType = "int", value = "角色ID{为0时查询菜单}", paramType = "path")
	@GetMapping({ "{roleId}/perm" })
	public ResultEntity<List<PermTopVO>> detail(@PathVariable(required = true, name = "roleId") Integer roleId) {
		return roleService.detail(roleId);
	}

	@BiLog(LogOpTypeEnum.DEL)
	@ApiOperation("删除角色")
	@ApiImplicitParam(name = "roleId", dataType = "int", value = "角色ID", paramType = "path")
	@DeleteMapping({ "{roleId}" })
	public ResultEntity<String> delRole(@PathVariable(required = true, name = "roleId") Integer roleId) {
		return roleService.delRole(roleId);
	}
}
