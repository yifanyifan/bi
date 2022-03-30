package com.stnts.bi.sys.common;

import java.lang.annotation.*;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/18
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OlapLog {

    /**
     * 操作内容
     * @return
     */
    String value();

}
