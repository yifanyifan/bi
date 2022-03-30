package com.stnts.bi.sys.service;

import java.util.List;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.RoleEntity;
import com.stnts.bi.sys.params.RoleParam;
import com.stnts.bi.sys.params.RolePermParam;
import com.stnts.bi.sys.vos.PermTopVO;

/**
 * @author liang.zhang
 * @date 2020年3月31日
 * @desc TODO
 */
public interface RoleService {
	
	public ResultEntity<List<RoleEntity>> listRoles();

	/**
	 * 新增角色
	 * @param role
	 * @return
	 */
	public ResultEntity<String> addOne(RoleParam role);

	/**
	 * 更新角色
	 * @param roleId
	 * @param role
	 * @return
	 */
	public ResultEntity<String> update(Integer roleId, RoleParam role);

	/**
	 * 修改角色权限
	 * @param roleId
	 * @param rolePermParam
	 * @return
	 */
	public ResultEntity<String> modRolePerm(Integer roleId, RolePermParam rolePermParam);
	
	/**
	 * 删除角色
	 * @param roleId
	 * @return
	 */
	public ResultEntity<String> delRole(Integer roleId);

	/**
	 * 查询权限树
	 * @param roleId
	 * @return
	 */
	public ResultEntity<List<PermTopVO>> detail(Integer roleId);
}
