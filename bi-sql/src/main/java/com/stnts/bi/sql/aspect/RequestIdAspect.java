package com.stnts.bi.sql.aspect;

import cn.hutool.aop.aspects.SimpleAspect;
import cn.hutool.core.util.StrUtil;
import com.stnts.bi.monitor.LogInterceptor;
import org.slf4j.MDC;

import java.lang.reflect.Method;

/**
 * @author liutianyuan
 * @since 2020年1月15日
 */
public class RequestIdAspect extends SimpleAspect {

    private final String suffix;

    public RequestIdAspect() {
        this.suffix = "";
    }

    public RequestIdAspect(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public boolean before(Object target, Method method, Object[] args) {
        MDC.put(LogInterceptor.REQUEST_ID, StrUtil.format("{} {}", MDC.get(LogInterceptor.REQUEST_ID), suffix));
        return true;
    }

    @Override
    public boolean after(Object target, Method method, Object[] args, Object returnVal) {
        MDC.remove(LogInterceptor.REQUEST_ID);
        return true;
    }
}

