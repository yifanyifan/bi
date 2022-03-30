package com.stnts.bi.authorization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.common.ResultEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.stnts.bi.entity.sys.UserEntity;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author liang.zhang
 * @date 2020年5月20日
 * @desc TODO
 * 权限过滤器
 */
@Slf4j
public class AuthInterceptor extends HandlerInterceptorAdapter{

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		boolean flag = false;
		//必须是网关过来的
		if(hasBiSession(request)) {
			// 验证权限
			flag = hasPermission(request, handler);
		}
		if(!flag){
			noPermission(response);
		}
		return flag;
	}

	/**
	 * 写了@CheckPerm注解的才验证权限，否则直接通过...
	 * @param request
	 * @param handler
	 * @return
	 */
	private boolean hasPermission(HttpServletRequest request, Object handler) {

		boolean flag = false;
		if(handler instanceof HandlerMethod) {
			
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			CheckPerm cp = handlerMethod.getMethodAnnotation(CheckPerm.class);
			if(cp != null) {
				UserEntity user = BiSessionUtil.build(redisTemplate, request).getSessionUser();
				if(null != user) {
					String cnname = user.getCnname();
					String op = cp.authCode().getDesc();
					log.info("[BI操作日志]操作: {}, 操作人: {}", op, cnname);
//					doCheckProduct(handlerMethod, user);
//					doCheckPerm(cp, user);
//					if(cp.checkProduct()){
//						//验证是否拥有当前产品线权限
//						Method method = handlerMethod.getMethod();
//						Parameter[] parameters = method.getParameters();
//						Arrays.stream(parameters).map(Parameter::getName).forEach(System.out::println);
//					}
//					flag = user.getPermSet().contains(cp.authCode().getCode());
					String permCode = cp.authCode().getCode();
					flag = cp.checkProduct() ? doCheckProduct(request, user, permCode)  : doCheckPerm(permCode, user);
				}
			}else{
				flag = true;
			}
		}else{
			//访问其它什么什么的
			flag = true;
		}
		return flag;
	}

	private boolean doCheckPerm(String permCode, UserEntity user) {
		return user.getPermSet().contains(permCode);
	}

	/**
	 *全部产品线ID
	 */
	private static final String ALL_PRODUCT = "-9";
	private boolean doCheckProduct(HttpServletRequest request, UserEntity user, String permCode) {
		//验证是否拥有当前产品线权限
		boolean flag = false;
		String data = request.getParameter("data");
		if(StringUtils.isNotEmpty(data)){

			boolean isAll = user.getProductSet().contains(ALL_PRODUCT);
			Optional<JSONObject> any = JSON.parseObject(data).getJSONArray("dashboard").toJavaList(JSONObject.class).stream().filter(o -> StringUtils.equals(o.getString("name"), "product")).findAny();
			if(any.isPresent()){
				String value = any.get().getString("value");
				List<String> permCodeList = isAll ? user.getSdkPermMap().get(ALL_PRODUCT) : user.getSdkPermMap().getOrDefault(value, Collections.emptyList());
				flag = permCodeList.contains(permCode);
			}
		}
		return flag;
	}

	private boolean hasBiSession(HttpServletRequest request) {
		return StringUtils.isNotEmpty(request.getHeader("bi-token"));
	}
	
	private void noPermission(HttpServletResponse response) {

		try{
			ResultEntity resultEntity = ResultEntity.forbidden("未登录或者无访问权限");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=utf-8");
			response.getWriter().write(JSON.toJSONString(resultEntity));
		}catch(Exception e){
			//pass
		}
	}
}
