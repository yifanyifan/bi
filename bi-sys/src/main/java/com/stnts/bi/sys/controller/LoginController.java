package com.stnts.bi.sys.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.nacos.common.util.Md5Utils;
import com.stnts.bi.common.BiLog;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.UserEntity;
import com.stnts.bi.enums.LogOpTypeEnum;
import com.stnts.bi.sys.common.SysConfig;
import com.stnts.bi.sys.service.LoginService;
import com.stnts.bi.vo.PermTreeVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author liang.zhang
 * @date 2020年5月19日
 * @desc TODO
 * 包含登录所有相关：登录，用户信息，权限信息
 */
@RestController
@Api(value = "用户登录", tags = { "用户登录" })
public class LoginController {
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private SysConfig sysConfig;

	@BiLog(LogOpTypeEnum.LOGIN)
	@ApiIgnore
	@GetMapping("login")
	public ResultEntity<String> login(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(required = true, name="userid") Integer userId,
			@RequestParam(required = true, name="app_id") String appId, 
			@RequestParam(required = true, name="timestamp") Long timestamp,
			@RequestParam(required = true, name="sign") String sign) {
		
		try {
			
			//先做验签,防止模拟登陆
			if(sysConfig.isCheckSign() && !checkSign(sign, userId, appId, timestamp)) {
				return ResultEntity.failure("非法Login");
			};
			UserEntity user = loginService.findById(userId);
			if(null != user) {
				//写session.  之前需放入用户权限
				loginService.fillPerm(user);
				loginService.fillPermForOlap(user);
				BiSessionUtil.build(this.redisTemplate, request).setSessionUser(user);
				//放入用户权限码  放一个tree和一个set的
				response.sendRedirect(sysConfig.getBiIndex());
				return ResultEntity.success(null);
			}
			return ResultEntity.failure("查无此人");
		} catch (Exception e) {
			return ResultEntity.exception(e.getMessage());
		}
	}
	
	@ApiOperation("退出")
	@GetMapping("logout")
	public ResultEntity<String> logout(HttpServletRequest request, HttpServletResponse response){
		
		boolean isClear = BiSessionUtil.build(this.redisTemplate, request).clearSession();
		return isClear ? ResultEntity.success(null) : ResultEntity.failure(null);
	}
	
	@ApiOperation("刷新用户登录信息")
	@GetMapping("flush")
	public ResultEntity<String> flushSession(HttpServletRequest request, HttpServletResponse response){
		
		try {
			
			UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
			if(null != user) {
				//需要重新查询一次用户  因为用户角色可能变更
				UserEntity userNew = loginService.findById(user.getId());
				loginService.fillPerm(userNew);
				loginService.fillPermForOlap(userNew);
				BiSessionUtil.build(this.redisTemplate, request).setSessionUser(userNew);
				return ResultEntity.success(null);
			}
			return ResultEntity.failure("无登录用户信息");
		} catch (Exception e) {
			return ResultEntity.exception(e.getMessage());
		}
	}
	
	/**
	 * @param sign
	 * @param params
	 * @return
	 */
	private boolean checkSign(String sign, Object... params) {

		String signStr = String.format("app_id=%s&timestamp=%s&userid=%s%s", params[1], params[2], params[0], sysConfig.getEhomeKey());
		return StringUtils.equals(Md5Utils.getMD5(signStr.getBytes()).toLowerCase(), sign);
	}

	@ApiOperation("用户登录信息")
	@GetMapping("session")
	public ResultEntity<UserEntity> session(HttpServletRequest request) {
		UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
		return ResultEntity.success(user);
	}
	
	@ApiIgnore
	@GetMapping("bi-session")
	public ResultEntity<Map<String, Object>> biSession(HttpServletRequest request) {
		
		Map<String, Object> sessionMap = new HashMap<String, Object>();
		sessionMap.put("bi-session", request.getHeader("bi-token"));
		UserEntity user = BiSessionUtil.build(this.redisTemplate, request).getSessionUser();
		sessionMap.put("user", user);
		return ResultEntity.success(sessionMap);
	}
	
	@ApiOperation("权限树")
	@GetMapping("permtree")
	public ResultEntity<PermTreeVO> permTree(){
		return loginService.loadPermTree();
	}

	@ApiIgnore
	@GetMapping("sync/user")
	public ResultEntity<String> userSync() {
		return loginService.syncUser();
	}
	
	@ApiIgnore
	@GetMapping("sync/department")
	public ResultEntity<String> syncDepartment(){
		return loginService.syncDepartment();
	}
}
