package com.stnts.bi.schedule;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

public class Test {

    @org.junit.Test
    public void test() {

        String dateStr = "2020-06-01";
        DateTime date = DateUtil.offsetDay(DateUtil.parseDate(dateStr), -1);
        System.out.println(date);

    }
}
