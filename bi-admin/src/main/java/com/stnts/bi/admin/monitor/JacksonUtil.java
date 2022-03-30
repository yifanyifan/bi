package com.stnts.bi.admin.monitor;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * @author liutianyuan
 * @date 2019-05-05 15:14
 */
@Slf4j
public class JacksonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 默认非空不输出
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 时间格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 忽略目标对象没有的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        // LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * 将 Java 对象转为 JSON 字符串
     */
    public static <T> String toJSON(T obj) {
        String jsonStr;
        try {
            jsonStr = objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("对象 转 JSON 出错！", e);
            throw new RuntimeException(e);
        }
        return jsonStr;
    }

    /**
     * 将 JSON 字符串转为 Java 对象
     */
    public static <T> T fromJSON(String json, Class<T> type) {
        if(isEmptyString(json)) {
            return null;
        }
        T obj;
        try {
            obj = objectMapper.readValue(json, type);
        } catch (Exception e) {
            log.error("JSON 转 对象 出错！", e);
            throw new RuntimeException(e);
        }
        return obj;
    }

    /**
     * json数组转Java list
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> List<T> fromJSONArray(String json, Class<T> type) {
        if(isEmptyString(json)) {
            return Collections.emptyList();
        }
        List<T> objs;
        try {
            objs = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
        } catch (Exception e) {
            log.error("JSON数组 转 对象 出错！", e);
            throw new RuntimeException(e);
        }
        return objs;
    }

    private static boolean isEmptyString(String json) {
        if(json == null || json.isEmpty()) {
            return true;
        }
        return false;
    }

}
