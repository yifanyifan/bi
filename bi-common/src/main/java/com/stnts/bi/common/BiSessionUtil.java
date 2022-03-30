package com.stnts.bi.common;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.stnts.bi.entity.sys.UserEntity;
import com.stnts.bi.utils.BiUtil;

/**
 * @author liang.zhang
 * @date 2020
 * @desc TODO
 */
@Slf4j
public class BiSessionUtil {
	
	private RedisTemplate<String, Object> redisTemplate;
	
	private ThreadLocal<Map<Object, Object>> cache = new ThreadLocal<Map<Object, Object>>();
	
	public static final String SESSION_USER_KEY = "__USER__";
	
	private HttpServletRequest request;
	
	private String sessionRedisKey;
	
	private BiSessionUtil() {
	}
	
	private BiSessionUtil(RedisTemplate<String, Object> redisTemplate, HttpServletRequest request) {
		this.redisTemplate = redisTemplate;
		this.request = request;
		Assert.notNull(request, "request must not be null");
		this.sessionRedisKey = getSessionKey();
	}
	
	public static BiSessionUtil build(RedisTemplate<String, Object> redisTemplate, HttpServletRequest request) {
		return new BiSessionUtil(redisTemplate, request);
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public UserEntity getSessionUser() {
		
		Object user = cache.get();
		user = null == user ? redisTemplate.opsForHash().get(sessionRedisKey, SESSION_USER_KEY) : user;
		if(null != user) {
			String userInfo = String.valueOf(user);
			return JSON.parseObject(userInfo, UserEntity.class);
		}else {
			return null;
		}
	}
	
	private String getSessionKey() {
		String biSessionId = request.getHeader("bi-token");
		Assert.notNull(biSessionId, "biSessionId must not be null");
		return BiUtil.biSessionId(biSessionId);
	}
	
	public Object getSessionAttr(String k) {
		
		Map<Object, Object> session = cache.get();
		if(session != null) {
			return session.get(k);
		}else {
			return redisTemplate.opsForHash().get(sessionRedisKey, k);
		}
	}
	
	/**
	 * attr
	 * @param k
	 * @param v
	 */
	public void setSessionAttr(String k, String v) {
		
		try {
			redisTemplate.opsForHash().put(sessionRedisKey, SESSION_USER_KEY, v);
			Map<Object, Object> session = redisTemplate.opsForHash().entries(sessionRedisKey);
			cache.set(session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * attr
	 * @param k
	 * @param v
	 */
	public void setSessionUser(UserEntity user) {
		
		try {
			log.info("sessionRedisKey: {}", sessionRedisKey);
			String userJson = JSON.toJSONString(user, SerializerFeature.WriteMapNullValue);
			redisTemplate.opsForHash().put(sessionRedisKey, SESSION_USER_KEY, userJson);
			Map<Object, Object> session = redisTemplate.opsForHash().entries(sessionRedisKey);
			cache.set(session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 清除session
	 * @return
	 */
	public boolean clearSession() {
		return redisTemplate.delete(sessionRedisKey);
	}
}
