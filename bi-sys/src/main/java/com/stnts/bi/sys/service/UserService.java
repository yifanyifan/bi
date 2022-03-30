package com.stnts.bi.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.UserDmEntity;
import com.stnts.bi.entity.sys.UserEntity;
import com.stnts.bi.entity.sys.UserProductFocusEntity;
import com.stnts.bi.entity.sys.UserRoleEntity;
import com.stnts.bi.sys.params.UserOrgParam;
import com.stnts.bi.sys.params.UserRoleNewParam;
import com.stnts.bi.sys.params.UserRoleParam;
import com.stnts.bi.sys.vos.TreeVO;
import com.stnts.bi.sys.vos.UserRoleVO;

/**
 * @author liang.zhang
 * @date 2020年3月29日
 * @desc TODO
 */
public interface UserService {
	
	/**
	 * 用户列表
	 * @param page
	 * @param userId
	 * @return
	 */
	ResultEntity<Page<UserEntity>> findUserListByUserId(Integer page, Integer userId, List<Integer> roleIds, List<Integer> productIds);
	ResultEntity<Page<UserEntity>> findUserListBySearch(Integer page, Integer userId, List<Integer> roleIds, String departmentCode, Integer orgId, List<Integer> productIds);

	/**
	 * 删除用户角色
	 * @param userId
	 * @return
	 */
	public ResultEntity<String> delRole(Integer userId);

	/**
	 * 编辑用户角色
	 * @param userId
	 * @param roles
	 * @return
	 */
	public ResultEntity<String> modRole(Integer userId, List<UserRoleParam> roles);

	/**
	 * 查询所有用户
	 * @param cnname 
	 * @return
	 */
	public ResultEntity<List<UserEntity>> findUsers(String cnname);


	/**
	 * 查看用户角色
	 * @param userId
	 * @return
	 */
	public ResultEntity<List<UserRoleEntity>> listRole(Integer userId);

	/**
	 * 删除用户权限
	 * @param userId
	 * @return
	 */
	ResultEntity<Boolean> delPerm(Integer userId);

	/**
	 * 用户绑定组织
	 * @param userOrgParam
	 * @return
	 */
    ResultEntity<Boolean> bindOrg(UserOrgParam userOrgParam);

	/**
	 * 权限管理2.0的角色权限编辑
	 * @param userRole
	 * @return
	 */
	ResultEntity<String> modRoleNew(UserRoleNewParam userRole);

	/**
	 * 展示用户权限
	 * @param userId
	 * @return
	 */
    ResultEntity<UserRoleVO> showRole(Integer userId);

	/**
	 * sdk已选
	 * @param userId
	 * @return
	 */
	ResultEntity<List<String>> checkedSdk(Integer userId);

	/**
	 * 数据管理已选
	 * @param userId
	 * @return
	 */
	ResultEntity<List<String>> checkedDm(Integer userId);

    /**
     * 用户拥有的SDK权限
     * @param userId
     * @return
     */
    ResultEntity<List<TreeVO>> sdk(Integer userId);

	/**
	 * 获取用户在数据管理中拥有的权限
	 * @param userId
	 * @return
	 */
	ResultEntity<List<UserDmEntity>> listDmByUserId(Integer userId);

	/**
	 * 维护CCID  清理过期CCID
	 * @param ccid
	 * @return
	 */
    ResultEntity<Boolean> delDmByCcid(String ccid);

	/**
	 * 关注产品线
	 * @param userProductFocusEntity
	 * @return
	 */
	ResultEntity<Boolean> focusProduct(UserProductFocusEntity userProductFocusEntity);

	/**
	 * 查看关注的产品线
	 * @param userId
	 * @return
	 */
	ResultEntity<UserProductFocusEntity> getFocusProduct(Integer userId);
}
