package com.stnts.bi.authorization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liang.zhang
 * @date 2020年5月20日
 * @desc TODO
 * 鉴权
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPerm {

	/**
	 * 权限code
	 * @return
	 */
	AuthCodeEnum authCode();

	/**
	 * 是否验证产品线
	 * @return
	 */
	boolean checkProduct() default false;
}
