package com.stnts.bi.common;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.stnts.bi.entity.sys.LogOpEntity;
import com.stnts.bi.entity.sys.UserEntity;
import com.stnts.bi.mapper.sys.LogOpMapper;
import com.stnts.bi.utils.IpAdrressUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liang.zhang
 * @date 2020
 * @desc TODO
 */
@Aspect
@Component
@Slf4j
public class OpLog {
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private LogOpMapper logMapper;
	
	@Pointcut("@annotation(com.stnts.bi.common.BiLog)")
	public void logPointCut() {
	}
	
	@AfterReturning("logPointCut()")
	public void log(JoinPoint joinPoint) { 
		
		try {
		
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();
			BiLog biLog = method.getAnnotation(BiLog.class);
			String logType = biLog.value().getLogType();
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			UserEntity user = BiSessionUtil.build(redisTemplate, request).getSessionUser();
			if(null != user) {
				
				String reqUrl = request.getRequestURI();
				String ip = IpAdrressUtil.getRealIPAddress(request);
				
				LogOpEntity op = LogOpEntity.builder().createdBy(user.getId())
						.logIp(ip)
						.logType(logType)
						.reqUrl(reqUrl)
						.build();
				
				logMapper.insert(op);
			}
		} catch (Exception e) {
			log.warn("OpLog记录出错，错误信息: {}", e.getMessage());
		}
	}
}
