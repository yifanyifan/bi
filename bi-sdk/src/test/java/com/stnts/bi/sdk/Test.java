package com.stnts.bi.sdk;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @Author: 刘天元
 * @Date: 2020/9/1 10:28
 */
public class Test {

    @org.junit.Test
    public void test() {
        String increaseRate = getIncreaseRate("47525", "128");
        System.out.println(increaseRate);
    }

    private String getIncreaseRate(String currentData, String preCycleData) {
        if (NumberUtil.isNumber(currentData) && NumberUtil.isNumber(preCycleData)) {
            if (StrUtil.isNotEmpty(preCycleData)) {
                double preCycleDataDouble = Double.parseDouble(preCycleData);
                if (preCycleDataDouble == 0) {
                    return "-";
                }
                Double percent = (Double.parseDouble(currentData) - preCycleDataDouble) / preCycleDataDouble;
                return String.valueOf(NumberUtil.round(percent, 4));
            }
        }
        return "-";
    }
}
