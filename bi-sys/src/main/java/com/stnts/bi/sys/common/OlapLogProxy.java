package com.stnts.bi.sys.common;

import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.entity.sys.OlapLogOpEntity;
import com.stnts.bi.entity.sys.UserEntity;
import com.stnts.bi.mapper.sys.OlapLogOpMapper;
import com.stnts.bi.utils.IpAdrressUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author liang.zhang
 * @date 2020
 * @desc TODO
 */
@Aspect
@Component
@Slf4j
public class OlapLogProxy {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private OlapLogOpMapper logMapper;

	@Pointcut("@annotation(com.stnts.bi.sys.common.OlapLog)")
	public void olapLogPointCut() {
	}

	@AfterReturning("olapLogPointCut()")
	public void log(JoinPoint joinPoint) {

		try {

			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();
			OlapLog olapLog = method.getAnnotation(OlapLog.class);
			String logType = olapLog.value();
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			UserEntity user = BiSessionUtil.build(redisTemplate, request).getSessionUser();
			if(null != user) {

				String reqUrl = request.getRequestURI();
				String ip = IpAdrressUtil.getRealIPAddress(request);
				Map<Object, Object> paramMap = new HashMap<>();
				try {

					Object[] args = joinPoint.getArgs();
					List<Object> params = Arrays.asList(method.getParameters());
					IntStream.range(0, params.size()).forEach(i -> {
						Parameter param = (Parameter) params.get(i);
						paramMap.put(param.getName(), args[i]);
					});
				}catch(Exception e){
					log.warn("解析参数出错, 错误信息: {}", e.getMessage());
				}
				String body = JSON.toJSONString(paramMap, true);
				OlapLogOpEntity op = OlapLogOpEntity.builder().createdBy(user.getId())
						.logIp(ip)
						.logType(logType)
						.reqUrl(reqUrl)
						.logBody(body)
						.build();

				logMapper.insert(op);
			}
		} catch (Exception e) {
			log.warn("OlapLog记录出错，错误信息: {}", e.getMessage());
		}
	}
}
