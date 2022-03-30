package com.stnts.bi.datamanagement.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.stnts.bi.datamanagement.module.channel.param.PostPidParam;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/3/3
 */
@Slf4j
public class PidUtil {
    public static Integer initPostPid(String host, List<PostPidParam> postPidParamAllList) {
        Map<String, List<PostPidParam>> paramMap = postPidParamAllList.stream().collect(Collectors.groupingBy(PostPidParam::getProductId));

        try {
            for (Map.Entry<String, List<PostPidParam>> me : paramMap.entrySet()) {
                String productId = me.getKey();
                List<PostPidParam> postPidParamList = me.getValue();

                List<Map<String, Object>> param = new ArrayList<Map<String, Object>>();
                for (PostPidParam postPidParam : postPidParamList) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("id", postPidParam.getId());
                    map.put("pid", postPidParam.getPid());
                    map.put("nc", postPidParam.getPidAlias());
                    param.add(map);
                }

                Map<String, Object> map = MapUtil.<String, Object>builder()
                        .put("app_key", "shuju")
                        .put("salt", RandomUtil.randomString(6))
                        .put("timestamp", Instant.now().getEpochSecond())
                        .put("pid", JSON.toJSONString(param))
                        .put("product_id", productId)
                        .build();

                log.info("youTuo initPostPid param=>" + JSON.toJSONString(map));
                Map<String, Object> objectMap = map.entrySet().stream().filter(e -> ObjectUtil.isNotNull(e.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                Map<String, Object> params = MapUtil.newTreeMap(Comparator.comparing(String::toString));

                objectMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
                        .forEachOrdered(entry -> params.put(entry.getKey(), entry.getValue()));

                String query = URLUtil.buildQuery(params, Charset.defaultCharset());
                String sign = SecureUtil.md5(SecureUtil.md5(query).concat("7zXRVnZz"));
                params.put("sign", sign);

                log.info("向友拓推送计费名称 ==========> 路径：" + host.concat("/api/post-pid") + "，参数：" + JSON.toJSONString(params));
                String result = HttpUtil.post(host.concat("/api/post-pid"), params);
                log.info("向友拓推送计费名称 ==========> 返回值：" + result);

                if (StrUtil.isNotEmpty(result)) {
                    log.info("request pid: {}", result);
                    JSONObject json = new JSONObject(result);
                    int status = json.getInt("status");
                    if (status != 0) {
                        log.info("向友拓推送计费名称 ==========> 错误：" + json.getStr("message"));
                    }
                }
            }
        } catch (Exception e) {
            log.info("向友拓推送计费名称 ==========> 异常：" + e.getMessage(), e);
        }
        return 0;
    }

    public static String initProductId(String host, String productName, String productCode, String departmentCode) {
        try {
            Map<String, Object> map = MapUtil.<String, Object>builder()
                    .put("app_key", "shuju")
                    .put("salt", RandomUtil.randomString(6))
                    .put("timestamp", Instant.now().getEpochSecond())
                    .put("name", productName)
                    .put("code", productCode)
                    .put("department_code", departmentCode)
                    .build();
            Map<String, Object> objectMap = map.entrySet().stream().filter(e -> ObjectUtil.isNotNull(e.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            Map<String, Object> params = MapUtil.newTreeMap(Comparator.comparing(String::toString));
            objectMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(entry -> params.put(entry.getKey(), entry.getValue()));

            String query = URLUtil.buildQuery(params, Charset.defaultCharset());
            String sign = SecureUtil.md5(SecureUtil.md5(query).concat("7zXRVnZz"));
            params.put("sign", sign);

            log.info("从友拓获取产品ID ==========> 路径：" + host.concat("/api/post-yx-product") + "，参数：" + JSON.toJSONString(params));
            String result = HttpUtil.post(host.concat("/api/post-yx-product"), params);
            log.info("从友拓获取产品ID ==========> 返回值" + result);

            if (StrUtil.isNotEmpty(result)) {
                log.info("request product: {}", result);
                JSONObject json = new JSONObject(result);
                int status = json.getInt("status");
                if (status == 0) {
                    return String.valueOf(json.getJSONArray("data").get(0));
                }
            }
        } catch (Exception e) {
            log.info("从友拓获取产品ID ==========> 异常：" + e.getMessage(), e);
        }
        return null;
    }
}
