package com.stnts.bi.sys.service;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.UserEntity;
import com.stnts.bi.vo.OlapPermSubVO;
import com.stnts.bi.vo.PermTreeVO;

import java.util.List;

/**
 * @author liang.zhang
 * @date 2020年5月20日
 * @desc TODO
 */
public interface LoginService {

	/**
	 * 根据用户ID查找用户
	 * @return
	 */
	public UserEntity findById(Integer userId);
	
	/**
	 * 同步用户
	 * @return
	 */
	public ResultEntity<String> syncUser();
	
	/**
	 * 填充权限树和权限CODE SET
	 * @param user
	 */
	public void fillPerm(UserEntity user);

	/**
	 * 加载权限树
	 * @return
	 */
	public ResultEntity<PermTreeVO> loadPermTree();

	/**
	 *     同步部门信息
	 * @return
	 */
	public ResultEntity<String> syncDepartment();

	/**
	 * 填充OLAP权限
	 * @param userNew
	 */
	void fillPermForOlap(UserEntity userNew);

	/**
	 * 查找用户的有权限的根目录
	 * @param userId
	 * @return
	 */
//	List<OlapPermSubVO> selectRootPermList(Integer userId);
}
