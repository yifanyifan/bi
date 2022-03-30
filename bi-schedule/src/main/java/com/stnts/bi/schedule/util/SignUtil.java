package com.stnts.bi.schedule.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.stnts.bi.schedule.constant.EnvironmentProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 刘天元
 */
@Service
@Slf4j
public class SignUtil {

    private final EnvironmentProperties environmentProperties;

    public SignUtil(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }

    public void checkSign(String appId, Long timestamp, String sign, Map<String, Object> parameters) throws Exception {
        String appIdYML = environmentProperties.getAppId();
        String appSecretYML = environmentProperties.getAppSecret();
        String appIdYML2 = environmentProperties.getAppId2();
        String appSecretYML2 = environmentProperties.getAppSecret2();

        Map<String, String> signMapParm = new HashMap<String, String>();
        signMapParm.put(appIdYML, appSecretYML);
        signMapParm.put(appIdYML2, appSecretYML2);

        if (!signMapParm.containsKey(appId)) {
            throw new Exception("appId 不对");
        }
        final int signExpiredTime = 5 * 60 * 1000;
        if (System.currentTimeMillis() - timestamp > signExpiredTime) {
            // 签名超时时长，默认时间为5分钟
            throw new Exception("url已失效");
        }
        TreeMap<String, Object> params = MapUtil.newTreeMap(Comparator.comparing(String::toString));
        params.put("appId", appId);
        params.put("timestamp", timestamp);

        Map<String, Object> notNullParameters = parameters.entrySet()
                .stream()
                .filter(e -> e.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        params.putAll(notNullParameters);
        params.forEach((key, value) -> {
            try {
                value = URLEncoder.encode(ObjectUtil.toString(value), CharsetUtil.UTF_8);
                params.put(key, value);
            } catch (UnsupportedEncodingException e) {
                log.info(StrUtil.format("encode {} exception", value), e);
            }
        });
        String signByParameters = getSign(params, signMapParm.get(appId));
        log.info("通过参数计算出的签名是{}", signByParameters);
        if (!ObjectUtil.equal(sign, signByParameters)) {
            throw new Exception("签名不对");
        }
    }

    public String getSign(TreeMap<String, Object> map, String secret) {
        String params = "";
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            params += StrUtil.format("{}{}={}", StrUtil.isEmpty(params) ? "" : "&", key, value);
        }
        params += secret;
        log.info("参数排序连接" + params);
        return SecureUtil.md5(params).toLowerCase();
    }
}
