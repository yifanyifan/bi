package com.stnts.bi.datamanagement.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author liutianyuan
 * @date 2019-05-10 18:00
 */

public class LocalDateTimeUtil {
    public static String format(LocalDateTime localDateTime) {
        if(localDateTime == null) {
            return "";
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
    }

    public static LocalDateTime parse(String dateTime) {
        if(StrUtil.isEmpty(dateTime)) {
            return null;
        }
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
    }
}
