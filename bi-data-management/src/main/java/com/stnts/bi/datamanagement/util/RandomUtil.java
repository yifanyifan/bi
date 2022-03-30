package com.stnts.bi.datamanagement.util;

import java.util.Random;

public class RandomUtil {
    /**
     * 生成随机字符串
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        //定义一个字符串（A-Z，a-z，0-9）；
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        return getRandomString(str, length);
    }

    /**
     * 生成随机数字字符串
     *
     * @param length
     * @return
     */
    public static String getRandomStringAndNumber(int length) {
        //定义一个字符串（A-Z，a-z，0-9）；
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return getRandomString(str, length);
    }

    /**
     * 生成随机数字
     *
     * @param length
     * @return
     */
    public static String getRandomNumber(int length) {
        //定义一个字符串（A-Z，a-z，0-9）；
        String str = "0123456789";
        return getRandomString(str, length);
    }

    /**
     * 生成若干位数随机数
     *
     * @param length
     * @return
     */
    public static String getRandomString(String str, int length) {
        //由Random生成随机数
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
