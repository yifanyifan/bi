package com.stnts.bi.datamanagement.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.stnts.bi.datamanagement.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: liang.zhang
 * @description:  BI和其它对接用..
 * @date: 2021/4/26
 */
@Slf4j
public class SignUtils {

    public static final String APP_GAMEOP = "game_op";
    public static final String APP_SECRET = "LOwiH0qLILmLi4sB";

    public static void check(String appId, String sign, Long timestamp, String pid){
        Map<String, Object> params = new HashMap<>();
        params.put("pid", pid);
        check(appId, sign, timestamp, params);
    }

    public static void check(String appId, String sign, Long timestamp, Map<String, Object> parameters){

        if(!StrUtil.equals(appId, APP_GAMEOP)){
            throw new BusinessException("appId不正确");
        }
//        final int signExpiredTime = 5 * 60;
//        if (System.currentTimeMillis() / 1000 - timestamp > signExpiredTime) {
//            // 签名超时时长，默认时间为5分钟
//            throw new BusinessException("url已失效");
//        }
        TreeMap<String, Object> params = MapUtil.newTreeMap(Comparator.comparing(String::toString));
        params.put("appId", appId);
        params.put("timestamp", timestamp);

        Map<String, Object> notNullParameters = parameters.entrySet()
                .stream()
                .filter(e -> e.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        params.putAll(notNullParameters);
        params.keySet().forEach(System.out::println);
        params.forEach((key, value) -> {
            try {
                value = URLEncoder.encode(ObjectUtil.toString(value), CharsetUtil.UTF_8);
                params.put(key, value);
            } catch (UnsupportedEncodingException e) {
                log.info(StrUtil.format("encode {} exception", value), e);
            }
        });
        String signByParameters = getSign(params, APP_SECRET);
        log.info("通过参数计算出的签名是{}", signByParameters);
        if(!ObjectUtil.equal(sign, signByParameters)) {
            throw new BusinessException("签名不对");
        }
    }

    public static String getSign(TreeMap<String, Object> map, String secret) {
        String params = "";
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            params +=  StrUtil.format("{}{}={}", StrUtil.isEmpty(params)?"":"&", key, value);
        }
        params += secret;
        log.info("参数排序连接" + params);
        return SecureUtil.md5(params).toLowerCase();
    }

    public static void main(String[] args) {

        long ts = System.currentTimeMillis() / 1000;
        SignUtils.check("game_op", "96d3c71f5972ba087aae1d55fefdf016", ts, "YYDT12349TB7");
    }
}
