package com.stnts.bi.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.stnts.bi.enums.LogOpTypeEnum;

/**
 * @author liang.zhang
 * @date 2020
 * @desc TODO
 * 记录日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BiLog {
	
	LogOpTypeEnum value() default LogOpTypeEnum.VIEW;
}